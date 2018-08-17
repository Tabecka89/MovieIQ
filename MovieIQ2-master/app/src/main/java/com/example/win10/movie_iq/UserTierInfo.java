package com.example.win10.movie_iq;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserTierInfo implements Serializable {
    private String tier = "tier0";
    private ArrayList<Question> answeredQuestions;
    private Map<String, Integer> hintTakenIndex;
    private Map<String, Integer> currentPointsForQuestion;
    private boolean isOpen;

    public UserTierInfo(){}

    public UserTierInfo(String tier) {
        this.tier = tier;
        answeredQuestions = new ArrayList<>();
        hintTakenIndex = new HashMap<>();
        isOpen = true;
        currentPointsForQuestion = new HashMap<>();
        answeredQuestions.add(new Question());
        currentPointsForQuestion.put("default", 0);
        hintTakenIndex.put("default", 0);
    }

    public String getTier() {
        return tier;
    }

    public ArrayList<Question> getAnsweredQuestions() {
        return answeredQuestions;
    }

    public Map<String, Integer> getHintTakedIndex() {
        return hintTakenIndex;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public void setAnsweredQuestions(ArrayList<Question> answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    public void setHintTakedIndex(Map<String, Integer> hintTakedIndex) {
        this.hintTakenIndex = hintTakedIndex;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void addAnsweredQuestion(Question question) {
        answeredQuestions.add(question);
    }

    public void addHintTakedIndexed(String answerOfQuestion) {
        int count = getNumOfHintsTaken(answerOfQuestion);
        hintTakenIndex.put(answerOfQuestion, count + 1);
    }

    public int getNumOfHintsTaken(String answerOfQuestion){
        if(hintTakenIndex == null)
            return 0;
        else if(hintTakenIndex.get(answerOfQuestion) != null)
            return hintTakenIndex.get(answerOfQuestion);
        return 0;
    }

    public Map<String, Integer> getCurrentPointsForQuestion() {
        return currentPointsForQuestion;
    }

    public void setCurrentPointsForQuestion(Map<String, Integer> currentPointsForQuestion) {
        this.currentPointsForQuestion = currentPointsForQuestion;
    }

    public boolean checkIfTheQuestionIsAnsweredByAnswer(String answer){
        for (int i =0 ; i<answeredQuestions.size(); i++){
            if(answer.equalsIgnoreCase(answeredQuestions.get(i).getAnswer()))
                return true;
        }
        return false;
    }


    @Override
    public String toString() {
        return "UserTierInfo{" +
                "tier='" + tier + '\'' +
                ", answeredQuestions=" + answeredQuestions +
                ", hintTakedIndex=" + hintTakenIndex +
                ", currentPointsForQuestion=" + currentPointsForQuestion +
                ", isOpen=" + isOpen +
                '}';
    }
}
