package com.example.cppoll;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.bson.BsonArray;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class NewPollActivity extends AppCompatActivity {
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    String id;
    EditText question;
    Button b;
    NewPollAdapter customAdapter;
    ArrayList<String> options;
    Button submit;
    ListView list;


    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_poll);

        b=(Button) findViewById(R.id.addbut);


        question=(EditText) findViewById(R.id.ques);
        list=findViewById(R.id.list);

        submit=(Button)findViewById(R.id.submit);
        options=new ArrayList<String>();
        Realm.init(this);
        String appID = "cp-poll-rrqey";
        App app = new App(new AppConfiguration.Builder(appID)
                .build());

        Credentials credentials = Credentials.anonymous();

        customAdapter = new NewPollAdapter(this, options);
        list.setAdapter(customAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                options.remove(position);
                customAdapter = new NewPollAdapter(NewPollActivity.this, options);
                list.setAdapter(customAdapter);
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.add("");
                customAdapter = new NewPollAdapter(NewPollActivity.this, options);
                list.setAdapter(customAdapter);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Document poll=new Document();
                Map<String,String> aut=new HashMap<String,String>();
                aut.put("$oid",MainActivity.user._id);

                        Document a=new Document("id",aut);
                        a.append("user",MainActivity.user.username);
                poll.append("question",question.getText().toString());
                poll.append("votedUsers",new ArrayList<String>());

                poll.append("author",a);
                poll.append("options",new ArrayList<Document>());
                poll.append("comments",new ArrayList<String>());


                        Log.v("QUICKSTART", "Successfully authenticated anonymously.");
                        User user = app.currentUser();
                        mongoClient=user.getMongoClient("mongodb-atlas");
                        mongoDatabase=mongoClient.getDatabase("test");

                        MongoCollection<Document> mongoCollection=mongoDatabase.getCollection("polls");
                        MongoCollection<Document> mongoOptionsCollection=mongoDatabase.getCollection("options");


                        Log.v("mongo",mongoCollection.toString());



                mongoCollection.insertOne(poll).getAsync(t -> {
                    if (t.isSuccess()) {

                                String ii=t.get().getInsertedId().toString();
                              String iid=ii.substring(19,ii.length()-1);
                        ArrayList<Document> opArr=new ArrayList<Document>();
                        for(int i=0;i<customAdapter.arrayList.size();i++ ){
                            Document d=new Document();
                            d.append("text",customAdapter.arrayList.get(i)).append("votes",0).append("poll",iid);
                            opArr.add(d);
                        }
                            mongoOptionsCollection.insertMany(opArr).getAsync(t1 -> {
                                if (t1.isSuccess()) {
                                    Map<String,String> objectId = new HashMap<String,String>();
                                    objectId.put("$oid",iid);
                                    RealmResultTask<MongoCursor<Document>> findOptions=mongoOptionsCollection.find(new Document("poll",iid)).iterator();
                                    ArrayList<Map<String,String>> ahm=new     ArrayList<Map<String,String>>();
                                    findOptions.getAsync(t2->{
                                        if(t2.isSuccess()){
                                            MongoCursor<Document> results = t2.get();
                                            while(results.hasNext()){
                                                Document res = results.next();
                                                String json=res.toJson();
                                                try {
                                                    JSONObject o=new JSONObject((json));
                                                    Map<String,String> mm=new HashMap<String,String>();

                                                    String optn=o.getJSONObject("_id").getString("$oid");
                                                    mm.put("$oid",optn);

                                                    ahm.add(mm);



                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                            mongoCollection.updateOne(new Document("_id",objectId),new Document("$set",new Document("options",ahm))).getAsync(t3->{
                                      if(t3.isSuccess()){
                                           startActivity(new Intent(NewPollActivity.this,QuestionListActivity.class));
                                       }else{
                                           Log.e("EXAMPLE", "failed to insert documents with: " + t3.getError().getErrorMessage());
                                       }
                                  });
                                        }
                                    });



//

                                } else {
                                    Log.e("EXAMPLE", "failed to insert documents with: " + t1.getError().getErrorMessage());
                                }
                            });
//                        mongoCollection.insertMany()

                    } else {
                        Log.e("EXAMPLE", "failed to insert documents with: " + t.getError().getErrorMessage());
                    }
                });



            }
        });



    }

}