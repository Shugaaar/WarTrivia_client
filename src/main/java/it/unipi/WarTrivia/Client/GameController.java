/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.WarTrivia.Client;

import it.unipi.WarTrivia.Client.models.Match;
import it.unipi.WarTrivia.Client.models.Question.ClientQuestion;
import it.unipi.WarTrivia.Client.models.UserSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 *
 * @author massimilianocoltelli
 */
public class GameController implements Initializable {
    
    UserSession session=UserSession.getSession();
    
    private int questionCounter=0;
    
    
    //GESTIONE TIMER
    
    private Timeline timeline;
    private static final int QUESTIONTIME = 15; 
    private Integer seconds = QUESTIONTIME;
    
    @FXML Label timer;
    
    
    
    private void startTimer() {
            
        seconds = QUESTIONTIME;
        timer.setText(seconds.toString());
        timer.getStyleClass().remove("timer-danger");

    
        if (timeline != null) {
            timeline.stop();
        }

    
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
    
        timeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(1), event -> {
                
                seconds--; 
                      
                timer.setText(seconds.toString());

            if (seconds == 5) {               
                timer.getStyleClass().add("timer-danger");
            }

            if (seconds <= 0) {
                timeline.stop();
                answer(null,true);
            }
            
            })
        );

        timeline.playFromStart();
    }
    
    
    
    @FXML Button button1;
    @FXML Button button2;
    @FXML Button button3;
    @FXML Button button4;
    
    @FXML Label question;
    @FXML Label questionNumber;
    
    private Integer score=0;
    @FXML Label scoreLabel;
    
    
    
    
    @FXML
    public void buttonHandler(ActionEvent event){
        answer(event,false);
    }
    
    private void disableButtons() {
        button1.setDisable(true);
        button2.setDisable(true);
        button3.setDisable(true);
        button4.setDisable(true);
    }

    private void enableButtons() {
        button1.setDisable(false);
        button2.setDisable(false);
        button3.setDisable(false);
        button4.setDisable(false);
}
    
    
    
    public void answer(ActionEvent event,boolean timeout){
        
        String msg;
        Button b=null;
        
        if(timeout){
            msg="Timeout";
        }else{
            b=(Button)event.getSource();
            msg=b.getText();
        }
        
        Task<String> answer=new Task<String>(){
            @Override public String call()throws Exception{
                
                String encodedAnswer = URLEncoder.encode(msg, StandardCharsets.UTF_8);
                
                String stringURL=String.format("http://localhost:8080/answer?matchId=%s&answer=%s",
                        session.getQuestions().get(0).getMatchId(),encodedAnswer);
                
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
                
                return content.toString();
                   
            }
        };
        
        final Button but=b;
        
        answer.setOnSucceeded(e->{
            
            disableButtons();
            
            String correctAnswer=answer.getValue();
            
            if(but!=null){
                but.getStyleClass().add("answer-wrong");
            }
            
            if(button1.getText().equals(correctAnswer)){
                button1.getStyleClass().add("answer-correct");
                button1.getStyleClass().remove("answer-wrong");
                if(but!=null&&but.equals(button1)){
                    score++;
                    scoreLabel.setText(score.toString());
                }             
            }
            if(button2.getText().equals(correctAnswer)){
                button2.getStyleClass().add("answer-correct");
                button2.getStyleClass().remove("answer-wrong");
                if(but!=null&&but.equals(button2)){
                    score++;
                    scoreLabel.setText(score.toString());
                } 
            }
            if(button3.getText().equals(correctAnswer)){
                button3.getStyleClass().add("answer-correct");
                button3.getStyleClass().remove("answer-wrong");
                if(but!=null&&but.equals(button3)){
                    score++;
                    scoreLabel.setText(score.toString());
                } 
            }
            if(button4.getText().equals(correctAnswer)){
                button4.getStyleClass().add("answer-correct");
                button4.getStyleClass().remove("answer-wrong");
                if(but!=null&&but.equals(button4)){
                    score++;
                    scoreLabel.setText(score.toString());
                } 
            }
            
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(p -> {
     
                loadQuestion();        
             });
    
            timeline.stop();
            pause.play();
            
        });
        
        answer.setOnFailed(e->{
            System.out.println(e);
            switchToHome();
        });
        
        new Thread(answer).start();
    }
    
    private String unescapeHtml(String text) {
        if (text == null) return null;
        
        return text
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&#039;", "'")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&amp;", "&")
        .replace("&ntilde;", "ñ")
        .replace("&eacute;", "é")
        .replace("&Agrave;", "À");
        
    }
    
    public void loadQuestion(){
        
        if(questionCounter==Match.nQuestions){
            switchToHome();
            return;
        }
        
        List<ClientQuestion> questions=session.getQuestions();
        ClientQuestion q=questions.get(questionCounter);
        
        List<String> possibleAnswers=q.getPossibleAnswers();
        
        button1.setText(unescapeHtml(possibleAnswers.get(0)));
        button1.getStyleClass().remove("answer-correct");
        button1.getStyleClass().remove("answer-wrong");
        
        button2.setText(unescapeHtml(possibleAnswers.get(1)));
        button2.getStyleClass().remove("answer-correct");
        button2.getStyleClass().remove("answer-wrong");
        
        button3.setText(unescapeHtml(possibleAnswers.get(2)));
        button3.getStyleClass().remove("answer-correct");
        button3.getStyleClass().remove("answer-wrong");
        
        button4.setText(unescapeHtml(possibleAnswers.get(3)));
        button4.getStyleClass().remove("answer-correct");
        button4.getStyleClass().remove("answer-wrong");
        
        enableButtons();
        
        question.setText(unescapeHtml(q.getQuestionText()));
        
        questionCounter++;
        
        questionNumber.setText(questionCounter+"/"+Match.nQuestions);
        
        startTimer();
          
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        loadQuestion();
        scoreLabel.setText("0");
    }  
    
    @FXML public void switchToHome(){
        try{
            App.setRoot("home");
        }catch(IOException e){
            System.err.println("Errore: "+e);
        }
    }
    
}
