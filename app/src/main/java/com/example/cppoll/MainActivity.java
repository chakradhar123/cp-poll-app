package com.example.cppoll;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    static GoogleSignInClient mGoogleSignInClient;
    static GoogleSignInAccount account;
    static CPUser user;
    private static final int RC_SIGN_IN =7;
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(account == null) {
            startActivity(new Intent(this, LoginActivity.class));
            Log.w("Here","here");
        } else {
            startActivity(new Intent(this, QuestionListActivity.class));
            Log.w("Here","here");
            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_add:
                            startActivity(new Intent(MainActivity.this,NewPollActivity.class));
                            break;
                        case R.id.action_polls:
                            startActivity(new Intent(MainActivity.this,QuestionListActivity.class));
                            break;
                        case R.id.action_person:
                            startActivity(new Intent(MainActivity.this,ViewProfileActivity.class));
                            break;
                    }
                    return true;
                }
            });
        }


    }

    @Override
    public void onClick(View v) {

    }

//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.sign_out :
//                signOut();
//                break;
//        }
//    }

//    private void signOut() {
//        if(mGoogleSignInClient != null) {
//            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
//
//                }
//            });
//        }
//    }

//    private class LoadAndShowPhoto extends AsyncTask<String, Void, Bitmap> {
//        ImageView imageView;
//
//        public LoadAndShowPhoto(ImageView imageView) {
//            this.imageView = imageView;
//            Toast.makeText(getApplicationContext(), "Loading Photo..", Toast.LENGTH_SHORT).show();
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String imageURL = urls[0];
//            Bitmap bimage = null;
//            try {
//                InputStream in = new java.net.URL(imageURL).openStream();
//                bimage = BitmapFactory.decodeStream(in);
//
//            } catch (Exception e) {
//                Log.e("LoadAndShowPhoto", e.getMessage());
//            }
//            return bimage;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            imageView.setImageBitmap(result);
//        }
//    }
}
