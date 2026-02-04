/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package it.unipi.WarTrivia.Client;

import it.unipi.WarTrivia.Client.models.UserSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author massimilianocoltelli
 */
public class LoginController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }  
    
    @FXML public void switchToRegister(){
        try{
            App.setRoot("register");
        }catch(IOException e){
            System.err.println("Errore: "+e);
        }
    }
    
    private void switchToHome(){
        try{
            App.setRoot("home");
        }catch(IOException e){
            System.err.println("Errore: "+e);
        }
    }
    
    @FXML TextField usernameField;
    
    @FXML PasswordField passwordField;
    
    @FXML Text textField;
    
    String usingUsername;
    
    @FXML
    public void login(){
        
        Task<Long> login= new Task<Long>(){
            @Override public Long call()throws Exception{
                    
                
                String username=usernameField.getText();
                usingUsername=username;
                String password=passwordField.getText();
                
                String urlString=String.format("http://localhost:8080/login?username=%s&passwd=%s",username,password);
                
                URL url=new URL(urlString);
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
        
                BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
        
                String inputLine;
                StringBuffer content=new StringBuffer(); //contenuto scaricato
                
                while((inputLine=in.readLine())!=null){
                    content.append(inputLine);
                }
                in.close();
                        
                Long userId=Long.parseLong(content.toString());
                        
                return userId;
                
            }
        };
        
        login.setOnSucceeded(event->{
            
            Long userId=login.getValue();
            
            if(userId==-1){
                textField.setText("Wrong credentials");
     
            }else{
                UserSession.setSession(userId, usingUsername);
                switchToHome();
            }
            
        });
        
        login.setOnFailed(event->{
            textField.setText("Server unreachable...");
        });
        
        new Thread(login).start();
        
        
    }
    
}
