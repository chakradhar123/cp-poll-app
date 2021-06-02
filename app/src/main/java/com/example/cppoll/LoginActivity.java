package com.example.cppoll;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    static int RC_SIGN_IN = 1;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        MainActivity.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*Directly start MainActivity if Already LoggedIn*/
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            createUser(account);
            MainActivity.account = account;
            startActivity(new Intent(this, MainActivity.class));

        }
    }
    public void createUser(GoogleSignInAccount account){
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
                MongoCollection<Document> mongoCollection=mongoDatabase.getCollection("users");

                Document queryFilter  = new Document("googleId", account.getId());
                RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();

                findTask.getAsync(task ->{
                    if(task.isSuccess()){
                        Log.v("task","success");
                        MongoCursor<Document> results = task.get();

                        if(results.hasNext()) {
                            Log.v("FindFunction", "Found Something");
                            Document res = results.next();
                            System.out.println(res.toJson().toString());
                            String json=res.toJson();
                            Log.v("item",json);
                            try {
                                JSONObject us=new JSONObject((json));
                                MainActivity.user=new CPUser(us.getJSONObject("_id").getString("$oid"),us.getBoolean("isAdmin"),us.getString("username"),us.getString("email"));




                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }else{

                            Document insertU=new Document().append("email",account.getEmail()).append("username",account.getDisplayName()).append("isAdmin",false).append("googleId",account.getId());

                            mongoCollection.insertOne(insertU).getAsync(t -> {
                                if (t.isSuccess()) {
                                    Log.v("EXAMPLE", "successfully inserted a document with id: " + t.get().getInsertedId());
                                    MainActivity.user=new CPUser(t.get().getInsertedId().toString(),false,account.getDisplayName(),account.getDisplayName());
                                } else {
                                    Log.e("EXAMPLE", "failed to insert documents with: " + t.getError().getErrorMessage());
                                }
                            });
                        }
                    }else{
                        Log.v("task","failure");
                    }


                });
            } else {
                Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    public void signIn() {
        Intent signInIntent = MainActivity.mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);


            // Signed in successfully, show authenticated UI.
            if(account != null) {
                createUser(account);
                MainActivity.account = account;

                startActivity(new Intent(this, MainActivity.class));

            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("LoginActivity", "signInResult:failed code=" + e.getStatusCode());
        }
    }
}