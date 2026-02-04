/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.WarTrivia.Client.models;

import it.unipi.WarTrivia.Client.models.Question.ClientQuestion;
import java.util.List;

/**
 *
 * @author massimilianocoltelli
 */
public class UserSession {
    
    // FACCIO SI CHE SI POSSA AVERE UNA SOLA ISTANZA
    private static UserSession session;
    
    private Long userId; 
    private String username; 
    private List<ClientQuestion> questions;
    private Long matchId;

    private UserSession(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public static void setSession(Long userId, String username) {
        session = new UserSession(userId, username);
    }

    public static UserSession getSession() {
        return session;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username){
        session.username=username;
    }
    
    public void setQuestions(List<ClientQuestion> questions){
        session.questions=questions;
    }
    
    public List<ClientQuestion> getQuestions(){
        return session.questions;
    }
    
    public Long getMatchId(){
        return matchId;
    }
    
    public void setMatchId(Long matchId){
        this.matchId=matchId;
    }

    public static void cleanUserSession() {
        session = null;
    }
}
