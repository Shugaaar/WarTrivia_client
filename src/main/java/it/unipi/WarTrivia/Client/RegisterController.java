/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package it.unipi.WarTrivia.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author massimilianocoltelli
 */
public class RegisterController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    //@FXML VBox vbox;
    
    @FXML public void switchToLogin(){
        try{
            App.setRoot("login");
        }catch(IOException e){
            System.err.println("Errore: "+e);
        }      
    }
    
    @FXML PasswordField passwordField;
    
    public boolean isPasswordValid(){
        
        String pwd=passwordField.getText();
        boolean valid=pwd.matches(".{8,20}");
        
        if (valid) {
            passwordField.setStyle("-fx-border-color: limegreen; -fx-border-width: 2;");
        } else {
            passwordField.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            
            
            textField.setText("Password must be within 8 - 20 characters");
            
        }
        return valid;
    }
    
    @FXML TextField usernameField;
    
    public boolean isUsernameValid(){
        
        String usr=usernameField.getText();
        boolean valid=usr.matches(".{3,20}");
        
        if (valid) {
            usernameField.setStyle("-fx-border-color: limegreen; -fx-border-width: 2;");
        } else {
            usernameField.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            
            
            textField.setText("Username must be within 3 - 20 characters");
            
        }
        return valid;
    }
    
    @FXML Text textField;
    
    //REGISTRAZIONE
    
    
     
    @FXML 
    public void register(){
        
        Task<Boolean> register =new Task<Boolean>(){
            @Override public Boolean call()throws IOException{
                    
                        String username=usernameField.getText();
                        String password=passwordField.getText();
                
                        String urlString=String.format("http://localhost:8080/register?username=%s&passwd=%s",username,password);
                
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
                        
                        Boolean ret=Boolean.parseBoolean(content.toString());
                        
                        return ret;
                            
            }
        };
        
        register.setOnSucceeded(event->{
            Boolean ret=register.getValue();
            if(ret){
                            System.out.println("Registrato!");
                            
                            textField.setText("Registration succeeded, now you can login...");
                            
            
                        }else{
                            System.out.println("Utente già esistente");
                            
                            textField.setText("Username already taken, please select other...");
                            
                                
                        }
        });
        
        register.setOnFailed(event->{
            textField.setText("Server unreachable...");
        });
        
        
    
        if(isUsernameValid()&&isPasswordValid()){
            new Thread(register).start();
    
        }else{
            System.out.println("Formato delle credenziali non valido");
      
        }
        
        
    }
    
    
    
    
    
}
