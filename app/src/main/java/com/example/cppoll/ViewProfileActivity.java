package com.example.cppoll;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.io.InputStream;

public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener {

    static GoogleSignInClient mGoogleSignInClient;
    static GoogleSignInAccount account;
    static int RC_SIGN_IN = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        TextView name = findViewById(R.id.name);
        account=MainActivity.account;
        name.setText(account.getDisplayName());
        TextView mail=findViewById(R.id.email);
        mail.setText(account.getEmail());
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add:
                        startActivity(new Intent(ViewProfileActivity.this,NewPollActivity.class));
                        break;
                    case R.id.action_polls:
                        startActivity(new Intent(ViewProfileActivity.this,QuestionListActivity.class));
                        break;
                    case R.id.action_person:

                        break;
                }
                return true;
            }
        });
        /*Loading and Showing Profile Photo*/
        new LoadAndShowPhoto((ImageView) findViewById(R.id.photo)).execute(account.getPhotoUrl().toString());
        findViewById(R.id.sign_out).setOnClickListener(v -> {
            switch (v.getId()) {
                case R.id.sign_out :
                    signOut();
                    break;
            }
        });
    }



    private void signOut() {
        if(MainActivity.mGoogleSignInClient != null) {
            MainActivity.mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.v("logout","here");
                    startActivity(new Intent(ViewProfileActivity.this, LoginActivity.class));

                }
            });
        }
    }

    @Override
    public void onClick(View v) {

    }

    private class LoadAndShowPhoto extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public LoadAndShowPhoto(ImageView imageView) {
            this.imageView = imageView;
            Toast.makeText(getApplicationContext(), "Loading Photo..", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("LoadAndShowPhoto", e.getMessage());
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }


}
