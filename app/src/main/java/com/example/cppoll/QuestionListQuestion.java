package com.example.cppoll;

import java.util.ArrayList;

import io.realm.RealmModel;

public class QuestionListQuestion  {
    String Id;
    String Question;

    ArrayList<String> VotedUsers;
    public QuestionListQuestion(String id, String question ,ArrayList<String> votedUsers){
        this.Id=id;
        this.Question=question;
        this.VotedUsers=votedUsers;

    }
}
