package com.example.cppoll;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.bson.Document;
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

public class VotePollActivity extends AppCompatActivity {
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    String id;
    int pos;
    TextView question;
    VoteOptionsAdapter customAdapter;
    static ArrayList<ViewOption> options;
    ArrayList<ViewOption> Alloptions;
    ListView list;


    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote_poll);
        Serializable s=getIntent().getSerializableExtra("id");
        Serializable p=getIntent().getSerializableExtra("position");
        pos=Integer.parseInt(p.toString());
        id=s.toString();
        Log.v("something",id);
        question=findViewById(R.id.question);
        list=findViewById(R.id.list);

        Alloptions=new ArrayList<ViewOption>();
        options=new ArrayList<ViewOption>();
        Realm.init(this);
        String appID = "cp-poll-rrqey";
        App app = new App(new AppConfiguration.Builder(appID)
                .build());

        Credentials credentials = Credentials.anonymous();
        customAdapter = new VoteOptionsAdapter(this, options);
        list.setAdapter(customAdapter);
list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long pid) {
              Log.v("clicked",Integer.toString(position));
        Log.v("clicked",Long.toHexString(view.getId()));
        User user = app.currentUser();
        mongoClient=user.getMongoClient("mongodb-atlas");
        mongoDatabase=mongoClient.getDatabase("test");

        MongoCollection<Document> mCollection=mongoDatabase.getCollection("polls");
        MongoCollection<Document> mongoO=mongoDatabase.getCollection("options");


        Map<String,String> oId = new HashMap<String,String>();
        oId.put("$oid",id);
        Log.v("mongo",mCollection.toString());
        Document qFilter  = new Document("_id", oId);



        Bundle extra = getIntent().getBundleExtra("pollFextra");


    ArrayList <String>vu =(ArrayList<String>) extra.getSerializable("pollF");;
    vu.add(MainActivity.user._id);
    Log.v("added",vu.toString());
    ArrayList<Map<String,String>> ahm=new     ArrayList<Map<String,String>>();
    for(int i=0;i<vu.size();i++){
        Map<String,String> objectId = new HashMap<String,String>();
        objectId.put("$oid",vu.get(i));
        ahm.add(objectId);
    }
        Map<String,String> oId2 = new HashMap<String,String>();
        oId2.put("$oid",options.get(position).Id);
        Log.v("mongo",mCollection.toString());
        Document qFilter2  = new Document("_id", oId2);
        int v=options.get(position).Votes+1;
        Document oup=new Document("votes",v);
    mongoO.updateOne(qFilter2,new Document("$set",oup)).getAsync(t -> {
        if (t.isSuccess()) {

            Intent ii=new Intent(VotePollActivity.this,ViewPollActivity.class);
            ii.putExtra("id",id);
            startActivity(ii);

        } else {
            Log.e("EXAMPLE", "failed to insert documents with: " + t.getError().getErrorMessage());
        }
    });
        mCollection.updateOne(qFilter,new Document("$set",new Document("votedUsers",ahm))).getAsync(t -> {
            if (t.isSuccess()) {


            } else {
                Log.e("EXAMPLE", "failed to insert documents with: " + t.getError().getErrorMessage());
            }
        });

    }
});
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully authenticated anonymously.");
                User user = app.currentUser();
                mongoClient=user.getMongoClient("mongodb-atlas");
                mongoDatabase=mongoClient.getDatabase("test");

                MongoCollection<Document> mongoCollection=mongoDatabase.getCollection("polls");
                MongoCollection<Document> mongoOptionsCollection=mongoDatabase.getCollection("options");

                Map<String,String> objectId = new HashMap<String,String>();
                objectId.put("$oid",id);
                Log.v("mongo",mongoCollection.toString());
                Document queryFilter  = new Document("_id", objectId);
                RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();
                RealmResultTask<MongoCursor<Document>> findOptions = mongoOptionsCollection.find().iterator();
                findOptions.getAsync(task->{
                    if(task.isSuccess()){
                        MongoCursor<Document> results = task.get();
                        while(results.hasNext()){
                            Document res = results.next();
                            String json=res.toJson();
                            try {
                                JSONObject op=new JSONObject((json));
                                Log.v("js",json);
                                Alloptions.add(new ViewOption(op.getJSONObject("_id").getString("$oid"),op.getString("text"),op.getInt("votes")));


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        findTask.getAsync(t ->{
                            if(t.isSuccess()){
                                Log.v("task","success");
                                MongoCursor<Document> resul = t.get();
                                while(resul.hasNext()) {
                                    Log.v("FindFunction", "Found Something");
                                    Document res = resul.next();
                                    System.out.println(res.toJson().toString());
                                    String json=res.toJson();
                                    Log.v("item",json);
                                    try {
                                        JSONObject poll=new JSONObject((json));
                                        question.setText(poll.getString("question"));
                                        JSONArray optns=poll.getJSONArray("options");
                                        for(int i=0;i<optns.length();i++){
                                            String optionId=optns.getJSONObject(i).getString("$oid");

                                            for (int j=0;j<Alloptions.size();j++){
                                                Log.v(Alloptions.get(j).Id,optionId);
                                                if(Alloptions.get(j).Id.equals(optionId)){
                                                    options.add(Alloptions.get(j));
                                                    break;
                                                }
                                            }

                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                Log.v("item",options.toString());
                                customAdapter = new VoteOptionsAdapter(this, options);
                                list.setAdapter(customAdapter);

                            }else{
                                Log.v("task","failure");
                            }



                        });
                    }
                });

            } else {
                Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
            }
        });
    }

}
