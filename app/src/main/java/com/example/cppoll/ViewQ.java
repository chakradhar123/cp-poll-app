package com.example.cppoll;

import java.util.ArrayList;

public class ViewQ {
    String Id;
    String Question;
    ArrayList<ViewOption>Options;

    public ViewQ(String id,String question,ArrayList<ViewOption> options){
        this.Id=id;
        this.Question=question;
        this.Options=options;
    }
}
