package com.example.win10.movie_iq;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private String question;
    private String answer;
    private static final int POINTS = 10;
    private int currentPoints = POINTS;

    private int tier ;
    private ArrayList<String> hints = new ArrayList<>();
    private ArrayList<String> facts = new ArrayList<>();
    private ArrayList<String> solutions = new ArrayList<>();

    private boolean isSolved = false;

    private String uri;


    public Question(){}


    public String getQuestion() {
        return question;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getAnswer() {
        return answer;
    }

    public static int getPOINTS() {
        return POINTS;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }



    public int getTier() {
        return tier;
    }



    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }



    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
    }


    public ArrayList<String> getHints() {
        return hints;
    }

    public void setHints(ArrayList<String> hints) {
        this.hints = hints;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public void setSolutions(ArrayList<String> solutions){
        this.solutions = solutions;
    }

    public ArrayList<String> getSolutions() {
        return solutions;
    }

    public ArrayList<String> getFacts() {
        return facts;
    }

    public void setFacts(ArrayList<String> facts){
        this.facts = facts;
    }


    public void reducePoints(){
        currentPoints -= 2;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }


    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", currentPoints=" + currentPoints +
                ", tier=" + tier +
                ", hints=" + hints +
                '}';
    }
}
