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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Question implements Serializable{

    
    private Long id;

    
    private String questionText;

    
    private String correctAnswer;

    
    private String wrongAnswers;

    public Question() {
    }

    public Question( String questionText, String correctAnswer, List<String> incorrectAnswersList) {
        
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
       
        this.wrongAnswers = String.join("|", incorrectAnswersList);
    }

    public List<String> getWrongAnswersList() {
        if (this.wrongAnswers == null || this.wrongAnswers.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(this.wrongAnswers.split("\\|"));
    }

    public void setWrongAnswersList(List<String> answers) {
        if (answers != null) {
            this.wrongAnswers = String.join("|", answers);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(String wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }
    
    public static class ClientQuestion {
        
        private Long id;
        private String questionText;
        private List<String> possibleAnswers;
        private Long matchId;

        
        public ClientQuestion() {}

        
        public ClientQuestion(Question q,Long matchId) {
            this.id = q.getId();
            this.questionText = q.getQuestionText();
            this.matchId=matchId;
            
            
            this.possibleAnswers = new ArrayList<>(q.getWrongAnswersList());
            
            
            this.possibleAnswers.add(q.getCorrectAnswer());
            
            Collections.shuffle(this.possibleAnswers);
        }

        public Long getId() {
            return id;
        }

        public String getQuestionText() {
            return questionText;
        }

        public List<String> getPossibleAnswers() {
            return possibleAnswers;
        }
        
        public Long getMatchId(){
            return matchId;
        }
    }
}