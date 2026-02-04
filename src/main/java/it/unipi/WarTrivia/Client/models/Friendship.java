/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.WarTrivia.Client.models;

import java.io.Serializable;

/**
 *
 * @author massimilianocoltelli
 */



public class Friendship implements Serializable{

    
    private Long id;

    
    private User user1;

    
    private User user2;

    
    private int user1Wins;

    
    private int user2Wins;
    
    
    private boolean accepted;

    
    public Friendship() {
    }

    public Friendship(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.user1Wins = 0;
        this.user2Wins = 0;
        this.accepted=false;
    }


    public void incrementWins(User user){
        if(user.equals(this.user1)){
            this.user1Wins++;
        }else if(user.equals(this.user2)){
           this.user2Wins++;
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user) {
        this.user1 = user;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user) {
        this.user2 = user;
    }

    public int getUser1Wins() {
        return user1Wins;
    }

    public int getUser2Wins() {
        return user2Wins;
    }
    
    public void accept(){
        this.accepted=true;
    }
    
    public boolean getAccepted(){
        return accepted;
    }

}
