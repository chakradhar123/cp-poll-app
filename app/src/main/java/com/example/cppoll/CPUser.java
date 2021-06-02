package com.example.cppoll;

import org.bson.types.ObjectId;

public class CPUser {
    boolean isAdmin;
String username;
String _id;
String email;
public CPUser(String id,boolean ad, String u, String e){
    this.username=u;
    this.isAdmin=ad;
    this._id=id;
    this.email=e;
}
}
