package com.example.cppoll;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;


import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

public class QuestionListActivity extends AppCompatActivity {
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_list);

        final ListView list = findViewById(R.id.list);
        ArrayList<QuestionListQuestion> arrayList = new ArrayList<QuestionListQuestion>();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add:
                        startActivity(new Intent(QuestionListActivity.this,NewPollActivity.class));
                        break;
                    case R.id.action_polls:

                        break;
                    case R.id.action_person:
                        startActivity(new Intent(QuestionListActivity.this,ViewProfileActivity.class));
                        break;
                }
                return true;
            }
        });
        Realm.init(this);
        String appID = "cp-poll-rrqey";
        App app = new App(new AppConfiguration.Builder(appID)
                .build());

        Credentials credentials = Credentials.anonymous();

        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully authenticated anonymously.");
                User user = app.currentUser();
                mongoClient=user.getMongoClient("mongodb-atlas");
                mongoDatabase=mongoClient.getDatabase("test");
                MongoCollection<Document> mongoCollection=mongoDatabase.getCollection("polls");
                Log.v("mongo",mongoCollection.toString());
                RealmResultTask<MongoCursor<Document> >findTask = mongoCollection.find().iterator();

                findTask.getAsync(task ->{
                    if(task.isSuccess()){
                        Log.v("task","success");
                        MongoCursor<Document> results = task.get();
                        while(results.hasNext()) {
                            Log.v("FindFunction", "Found Something");
                            Document res = results.next();
                            System.out.println(res.toJson().toString());
                            String json=res.toJson();
                            Log.v("item",json);
                            try {
                                JSONObject poll=new JSONObject((json));
                                JSONArray vu=poll.getJSONArray("votedUsers");
                                ArrayList<String>votedU=new ArrayList<String>();
                                for (int i =0;i<vu.length();i++){
                                    votedU.add(vu.getJSONObject(i).getString("$oid"));
                                }
                                arrayList.add(new QuestionListQuestion(poll.getJSONObject("_id").getString("$oid"),poll.getString("question"),votedU));


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }else{
                        Log.v("task","failure");
                    }

                    QuestionListAdapter customAdapter = new QuestionListAdapter(this, arrayList);
                    list.setAdapter(customAdapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String qid=arrayList.get(position).Id;
                            Intent Vpoll;
                            if(arrayList.get(position).VotedUsers.indexOf(MainActivity.user._id)==-1)
                            Vpoll=new Intent(QuestionListActivity.this,VotePollActivity.class);
                            else
                                Vpoll=new Intent(QuestionListActivity.this,ViewPollActivity.class);
                            Vpoll.putExtra("id",qid);
                            Bundle extra = new Bundle();
                            extra.putSerializable("pollF",arrayList.get(position).VotedUsers );
                            Vpoll.putExtra("pollFextra",extra);
                            Vpoll.putExtra("position",position);

                            Log.v("qclick","clicked");
                            startActivity(Vpoll);
                        }
                    });
                });
            } else {
                Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
            }
        });




    }
}
