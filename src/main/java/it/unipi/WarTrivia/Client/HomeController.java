/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package it.unipi.WarTrivia.Client;

import it.unipi.WarTrivia.Client.models.Friendship;
import it.unipi.WarTrivia.Client.models.UserSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import com.google.gson.*;
import it.unipi.WarTrivia.Client.models.Match;
import it.unipi.WarTrivia.Client.models.Question.ClientQuestion;
import it.unipi.WarTrivia.Client.models.User;
import java.io.IOException;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;



/**
 * FXML Controller class
 *
 * @author massimilianocoltelli
 */
public class HomeController implements Initializable {
    
    
    private final UserSession session=UserSession.getSession();
    
    @FXML private Label usernameField;
    
    @FXML private ListView friendsView;
    
    @FXML private ListView usersView;
    
    @FXML private VBox matchContainer;
    
    
    public void switchToLogin(){
        try{
            App.setRoot("login");
        }catch(IOException e){
            System.err.println("Errore: "+e);
        } 
    }
    
    public void switchToGame(){
        try{
            App.setRoot("game");
        }catch(IOException e){
            System.err.println("Errore: "+e);
        } 
    }
    
    @FXML
    private void init(){
        
        Task initialize=new Task<Void>(){
            @Override public Void call()throws Exception{
                
                URL url=new URL("http://localhost:8080/inizializza");
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                
                BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
        
                String inputLine;
                StringBuffer content=new StringBuffer(); //contenuto scaricato
                while((inputLine=in.readLine())!=null){
                    content.append(inputLine);
                }
                in.close();
                
                return null;
                
            }
        };
        
        initialize.setOnFailed(event->{
            System.out.println(initialize.getValue());
        });
        
        new Thread(initialize).start();
    }
    
    
    //GESTIONE DEL REFRESH AUTOMATICO
    
    private Timeline timeline;
    
    public void startAutoRefresh(){
        timeline=new Timeline(
            new KeyFrame(Duration.seconds(5),event->{
                //System.out.println("Refresh automatico in corso...");
                refresh();
            }));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        
        timeline.play();
    }
    
    @FXML
    private void refresh(){
        showFriends();
        showUsers();
        showMatches();
    }
    
    @FXML 
    private void quit(){
        UserSession.cleanUserSession();
        switchToLogin();
    }
    
    
    //GESTIONE UTENTI E AMICIZIE
    
    private void showFriends(){
        
        Task <List<Friendship>> showfriends=new Task<List<Friendship>>(){
            @Override
            public List<Friendship> call()throws Exception{
                
                Long userId=session.getUserId();
                
                String stringURL=String.format("http://localhost:8080/getfriends?user=%s",userId);
                
                URL url=new URL(stringURL);
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
        
                BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
        
                String inputLine;
                StringBuffer content=new StringBuffer(); //contenuto scaricato
                while((inputLine=in.readLine())!=null){
                    content.append(inputLine);
                }
                in.close();
        
                
        
                Gson gson=new Gson();
                JsonElement json=gson.fromJson(content.toString(),JsonElement.class);
                JsonArray jsonArray = json.getAsJsonArray();
                
                List<Friendship> friendships=new ArrayList<Friendship>();
                
                //System.out.println(jsonArray.get(0).toString());
         
                for(int i=0;i<jsonArray.size();i++){
                    friendships.add(gson.fromJson(jsonArray.get(i).getAsJsonObject(),Friendship.class)); 
                }
                
                return friendships;
                
            }
        };
        
        showfriends.setOnSucceeded(event->{
            List<Friendship> friendships=showfriends.getValue();
            //System.out.println(friendships.get(0).getState());
            
            friendsView.getItems().clear();
            friendsView.getItems().addAll(friendships);
            
        });
        
        showfriends.setOnFailed(event->{
            System.out.println("Server irraggiungibile");
            System.out.println(showfriends.getException());
        });
        
        new Thread(showfriends).start();
              
    }
    
    private void delFriend(Friendship f){
        
        Task delfriend =new Task<Void>(){
            @Override
            public Void call()throws Exception{
                
                String stringURL=String.format("http://localhost:8080/delfriend?f=%s",f.getId());
                
                URL url=new URL(stringURL);
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                
                BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
        
                in.readLine();
                in.close();
                
                return null;
                  
            }
        };
        
        delfriend.setOnSucceeded(event->{
            
            friendsView.getItems().remove(f);
            
        });
        
        delfriend.setOnFailed(event->{
            System.out.println(delfriend.getException());
        });
        
        new Thread(delfriend).start();
    }
    
    private void addFriend(User u){
        
        Task<Friendship> addfriend =new Task<Friendship>(){
            @Override
            public Friendship call()throws Exception{
                
                String stringURL=String.format("http://localhost:8080/addfriend?user1=%s&user2=%s",session.getUserId(),u.getId());
                
                URL url=new URL(stringURL);
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                
                BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
        
                String inputLine;
                StringBuffer content=new StringBuffer(); //contenuto scaricato
                while((inputLine=in.readLine())!=null){
                    content.append(inputLine);
                }
                in.close();
                
                Gson gson=new Gson();
                
                Friendship f=gson.fromJson(content.toString(),Friendship.class);
                //System.out.println(f.getId());
                
                return f;
                  
            }
        };
        
        addfriend.setOnSucceeded(event->{
           
            Friendship f=addfriend.getValue();
            if(f==null)
                return;
            
            friendsView.getItems().add(f);
            friendsView.getItems().remove(session.getUserId());
            
            
        });
        
        addfriend.setOnFailed(event->{
            System.out.println(addfriend.getException());
        });
        
        new Thread(addfriend).start();
    }
    
    private void acceptFriend(Friendship f){
        
        Task acceptfriend =new Task<Void>(){
            @Override
            public Void call()throws Exception{
                
                String stringURL=String.format("http://localhost:8080/acceptfriend?f=%s",f.getId());
                
                URL url=new URL(stringURL);
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                
                BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
        
                String inputLine;
                StringBuffer content=new StringBuffer(); //contenuto scaricato
                while((inputLine=in.readLine())!=null){
                    content.append(inputLine);
                }
                in.close();
                
                return null;
                  
            }
        };
        
        acceptfriend.setOnSucceeded(event->{
            
            refresh();
            
        });
        
        acceptfriend.setOnFailed(event->{
            System.out.println(acceptfriend.getException());
        });
        
        new Thread(acceptfriend).start();
        
    }
     
    private void showUsers(){
        
        Task <List<User>> showusers=new Task<List<User>>(){
            @Override
            public List<User> call()throws Exception{
                
                URL url=new URL("http://localhost:8080/getusers");
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
        
                BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
        
                String inputLine;
                StringBuffer content=new StringBuffer(); 
                while((inputLine=in.readLine())!=null){
                    content.append(inputLine);
                }
                in.close();
        
                //DESERIALIZZO I DATI
        
                Gson gson=new Gson();
                JsonElement json=gson.fromJson(content.toString(),JsonElement.class);
                JsonArray jsonArray = json.getAsJsonArray();
                
                List<User> users=new ArrayList<User>();
         
                for(int i=0;i<jsonArray.size();i++){
                    users.add(gson.fromJson(jsonArray.get(i).getAsJsonObject(),User.class));  
                }
                
                //System.out.println(users.get(0).getUsername());
                
                return users;
                
            }
        };
        
        showusers.setOnSucceeded(event->{
            List<User> users=showusers.getValue();
            for(int i=0;i<users.size();i++){
                if(users.get(i).getId().equals(session.getUserId())){
                    users.remove(i);
                    break;
                }
            }
            
            usersView.getItems().clear();
            usersView.getItems().addAll(users);
                 
        });
        
        showusers.setOnFailed(event->{
            
            System.out.println(showusers.getException());
        });
        
        new Thread(showusers).start();
        
    }
    
    
    //GESTIONE DEI MATCH
    
    private HBox createMatchCard(Match match) {

        Match.MatchStatus status=match.getStatus();
    
        boolean iAmChallenger=match.getChallenger().getId().equals(session.getUserId());
    
        String opponentName=(iAmChallenger)?match.getChallenged().getUsername():match.getChallenger().getUsername();
    
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("match-card");
      
        Label vsLabel = new Label("vs " + opponentName);
        vsLabel.getStyleClass().add("vs-label");
    
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
    
        Label scoreLabel=null;
        Label infoLabel=null;
        Button accept=null;
        
        if(status==Match.MatchStatus.FIRST_R&&iAmChallenger){
            return null;
        }
    
        if(status==Match.MatchStatus.FIRST_R){
        
            scoreLabel= new Label("? - ?" );
            scoreLabel.setStyle("-fx-text-fill: blue;");
            infoLabel= new Label("Has challenged you! Wait your turn..." );
        
        }else if(status==Match.MatchStatus.SECOND_R){
        
            if(iAmChallenger){
            
                scoreLabel= new Label(match.getChallengerScore()+" - ?" );
                infoLabel= new Label("It's your opponent turn..." );
            }else{
                scoreLabel= new Label("? - "+match.getChallengerScore());
                infoLabel= new Label("It's your turn..." );
                accept=new Button("Play");
                accept.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f4f8, #d9e2ec); -fx-text-fill: black;");
                
            
                accept.setOnAction(event->{
                    acceptChallenge(match);
                });
            
            }
            scoreLabel.setStyle("-fx-text-fill: blue;");
        
        }else if(status==Match.MatchStatus.COMPLETED){
        
            boolean iWon=(iAmChallenger&&match.getChallengerScore()>match.getChallengedScore())||(!iAmChallenger&&match.getChallengerScore()<match.getChallengedScore());
        
            String info=(iWon)?"You won!":"You lost :(";
        
            if(iAmChallenger){
            
                scoreLabel= new Label(match.getChallengerScore()+" - "+ match.getChallengedScore());
                infoLabel= new Label(info);
            }else{
                scoreLabel= new Label(match.getChallengedScore()+" - "+match.getChallengerScore());
                infoLabel= new Label(info);
            
            
            }
            scoreLabel.setStyle(iWon ? "-fx-text-fill: #00cc66;" : "-fx-text-fill: #ff4d4d;");
        }
        
        scoreLabel.getStyleClass().add("match-score");
        
        Region spacer2 = new Region();
        //HBox.setHgrow(spacer2, Priority.ALWAYS);
            
        if(accept!=null){
            Region spacer3 = new Region();
            //HBox.setHgrow(spacer1, Priority.ALWAYS);
            
            card.getChildren().addAll(vsLabel,spacer3,accept, spacer1,infoLabel,scoreLabel, spacer2);
            
        }else{
            card.getChildren().addAll(vsLabel, spacer1,infoLabel,scoreLabel, spacer2);
        }

        return card;
    }
    
    
    public void showMatches(){
        
        Task <List<Match>> showmatches=new Task<List<Match>>(){
            @Override
            public List<Match> call()throws Exception{
                
                String stringURL=String.format("http://localhost:8080/getmatches?user=%s", session.getUserId());
                URL url=new URL(stringURL);
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
        
                BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
        
                String inputLine;
                StringBuffer content=new StringBuffer(); 
                while((inputLine=in.readLine())!=null){
                    content.append(inputLine);
                }
                in.close();
        
                //DESERIALIZZO I DATI
        
                Gson gson=new Gson();
                JsonElement json=gson.fromJson(content.toString(),JsonElement.class);
                JsonArray jsonArray = json.getAsJsonArray();
                
                List<Match> matches=new ArrayList<Match>();
         
                for(int i=0;i<jsonArray.size();i++){
                    matches.add(gson.fromJson(jsonArray.get(i).getAsJsonObject(),Match.class));  
                }
                
                //System.out.println(users.get(0).getUsername());
                
                return matches;
                
            }
        };
        
        showmatches.setOnSucceeded(event->{
            List<Match> matches=showmatches.getValue();
            //System.out.println(matches.get(0).getChallenger().getUsername());
            
            matchContainer.getChildren().clear();
            
            for(int i=0;i<matches.size();i++){
                HBox hbox=createMatchCard(matches.get(i));
                if(hbox==null)
                    continue;
                matchContainer.getChildren().add(hbox);
            }
            
            
        });
        
        showmatches.setOnFailed(event->{
            
            System.out.println(showmatches.getException());
        });
        
        new Thread(showmatches).start();
        
    }
    
    public void challenge(User opponent){
        
        Task<List<ClientQuestion>> challenge=new Task<List<ClientQuestion>>(){
            @Override
            public List<ClientQuestion> call()throws Exception{
                
                String stringURL=String.format("http://localhost:8080/challenge?challenger=%s&challenged=%s", session.getUserId(),opponent.getId());
                URL url=new URL(stringURL);
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                
                BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
        
                String inputLine;
                StringBuffer content=new StringBuffer(); 
                while((inputLine=in.readLine())!=null){
                    content.append(inputLine);
                }
                in.close();
                
                Gson gson=new Gson();
                JsonElement json=gson.fromJson(content.toString(), JsonElement.class);
                JsonArray jsonArray=json.getAsJsonArray();
                
                List<ClientQuestion> questions=new ArrayList<ClientQuestion>();
                for(int i=0;i<jsonArray.size();i++){
                    questions.add(gson.fromJson(jsonArray.get(i), ClientQuestion.class));
                }
        
                return questions;
                
            }
        };
        
        challenge.setOnSucceeded(event->{
            
            session.setQuestions(challenge.getValue());
            //System.out.println(challenge.getValue().toString());
            switchToGame();
                      
        });
        
        challenge.setOnFailed(event->{
            
            System.out.println(challenge.getException());
        });
        
        new Thread(challenge).start();
        
    }
    
    public void acceptChallenge(Match m){
        
        Task<List<ClientQuestion>> acceptchallenge=new Task<List<ClientQuestion>>(){
            @Override
            public List<ClientQuestion> call()throws Exception{
                
                String stringURL=String.format("http://localhost:8080/acceptchallenge?m=%s",m.getId());
                URL url=new URL(stringURL);
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                
                BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
        
                String inputLine;
                StringBuffer content=new StringBuffer(); 
                while((inputLine=in.readLine())!=null){
                    content.append(inputLine);
                }
                in.close();
                
                Gson gson=new Gson();
                JsonElement json=gson.fromJson(content.toString(), JsonElement.class);
                JsonArray jsonArray=json.getAsJsonArray();
                
                List<ClientQuestion> questions=new ArrayList<ClientQuestion>();
                for(int i=0;i<jsonArray.size();i++){
                    questions.add(gson.fromJson(jsonArray.get(i), ClientQuestion.class));
                }
        
                return questions;
                
            }
        };
        
        acceptchallenge.setOnSucceeded(event->{
            
            session.setQuestions(acceptchallenge.getValue());
            //System.out.println(challenge.getValue().toString());
            switchToGame();
                      
        });
        
        acceptchallenge.setOnFailed(event->{
            
            System.out.println(acceptchallenge.getException());
        });
        
        new Thread(acceptchallenge).start();
        
    }
    
    
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        usernameField.setText(session.getUsername());
        
        //AMICIZIE
        
        friendsView.setCellFactory(param -> new ListCell<Friendship>() {
            
           
            protected void updateItem(Friendship f,boolean empty) {
                super.updateItem(f, empty);

                if (empty ||f == null) {
                
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    
                    boolean first=(f.getUser1().getId().equals(session.getUserId()))?true:false;
                    String opponentUsername=(!first)?f.getUser1().getUsername():f.getUser2().getUsername();
                    User opponent=(!first)?f.getUser1():f.getUser2();
                    int myWins=(first)?f.getUser1Wins():f.getUser2Wins();
                    int myLosses=(first)?f.getUser2Wins():f.getUser1Wins();
        
                    HBox hBox = new HBox(10);
                    hBox.setAlignment(Pos.CENTER_LEFT);
        
                    Label nameLabel=new Label(opponentUsername);
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 16px;");
        
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    hBox.getChildren().addAll(nameLabel, spacer);
                    
                    if(f.getAccepted()){
                        
                        Label scoreLabel = new Label("Wins:  " + myWins + "  -  Losses:  " + myLosses);
                        String color = (myWins >= myLosses) ? "#00cc66" : "#ff4d4d";
                        scoreLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px;");
                        
                        hBox.getChildren().add(scoreLabel);
                        
                    }else{
                        
                        Label requestLabel;
                            
                        if(f.getUser1().getId().equals(session.getUserId())){
                             requestLabel=new Label("Request Sent");
                        }else{
                             requestLabel=new Label("Wanna be friends?");
                        }
                        
                        hBox.getChildren().add(requestLabel);
                    }
        
                    setText(null); 
                    setGraphic(hBox);
        
                //MENU A SCOMPARSA
        
                    ContextMenu contextMenu = new ContextMenu();
                
                
                    if(f.getAccepted()){
                        MenuItem challengeItem = new MenuItem("⚔️ Challenge");
                        challengeItem.setOnAction(event -> {
                            
                            challenge(opponent);
        
                        });
                        
                        contextMenu.getItems().add(challengeItem);
                        
                    }else{
                        
                        if(!f.getUser1().getId().equals(session.getUserId())){
                            MenuItem acceptItem = new MenuItem("Accept");
                            acceptItem.setOnAction(event -> {
                            
                                acceptFriend(f);
        
                            });
                        
                            contextMenu.getItems().add(acceptItem);
                        }   
                    }
                    
                    MenuItem removeItem = new MenuItem("🗑️ Remove");
                    removeItem.setStyle("-fx-text-fill: red;"); 
                    removeItem.setOnAction(event -> {
                        
                        delFriend(f);
            
                    });
                
                    contextMenu.getItems().add(removeItem);
                         
                    setContextMenu(contextMenu);
                    
                }
            }
         });
        
        showFriends();
        
        
        usersView.setCellFactory(param -> new ListCell<User>() {
            
           
            protected void updateItem(User u,boolean empty) {
                super.updateItem(u, empty);

                if (empty||u == null) {
                
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                } else {
        
                    HBox hBox = new HBox(10);
                    hBox.setAlignment(Pos.CENTER_LEFT);
        
                    Label nameLabel=new Label(u.getUsername());
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 16px;");
        
                    hBox.getChildren().addAll(nameLabel);
                    
                    setText(null); 
                    setGraphic(hBox);
        
                //MENU A SCOMPARSA
        
                    ContextMenu contextMenu = new ContextMenu();
                
                
                    MenuItem addItem = new MenuItem("Add to friends");
                    addItem.setOnAction(event -> {
                        
                        addFriend(u);
                        
                    });
                
                
                    contextMenu.getItems().add(addItem);
                         
                    setContextMenu(contextMenu);
                    
                }
            }
         });
        
        showUsers();
        
        showMatches();
        
        startAutoRefresh();
    }
    
}
