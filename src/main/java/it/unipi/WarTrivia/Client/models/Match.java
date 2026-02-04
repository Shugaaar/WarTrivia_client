/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.WarTrivia.Client.models;

/**
 *
 * @author massimilianocoltelli
 */

import java.io.Serializable;
import java.util.List;


public class Match implements Serializable{
    
    public static int nQuestions=10;

    public enum MatchStatus { 
        FIRST_R, //sfidante deve rispondere
        SECOND_R, //sfidato deve rispondere
        COMPLETED         //entrambi hanno terminato
    }

    
    private Long id;

    
    private User challenger;

   
    
    private User challenged;

 
    
    private int challengerScore;

    
    private int challengedScore;

    
    private MatchStatus status;

    
    private List<Question> questions;
    
    
    //private Friendship friendship;
    
    
    private Integer counter;


    public Match() {
    }

    public Match(User challenger, User challenged,List<Question> questions) {
        this.challenger = challenger;
        this.challenged = challenged;
        //this.friendship=friendship;
        this.questions = questions;
        this.status = MatchStatus.FIRST_R;
        this.challengerScore = 0;
        this.challengedScore = 0;
        this.counter=0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getChallenger() {
        return challenger;
    }

    public void setChallenger(User challenger) {
        this.challenger = challenger;
    }

    public User getChallenged() {
        return challenged;
    }

    public void setChallenged(User challenged) {
        this.challenged = challenged;
    }

    public int getChallengerScore() {
        return challengerScore;
    }

    public void incrementChallengerScore() {
        this.challengerScore++;
    }

    public int getChallengedScore() {
        return challengedScore;
    }

    public void incrementChallengedScore() {
        this.challengedScore++;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> q) {
        
        this.questions=q;
    }
    
    public Integer getCounter(){
        return this.counter;
    }
    
    public void incrementCounter(){
        this.counter++;
    }
    
    public void clearCounter(){
        this.counter=0;
    }
}
