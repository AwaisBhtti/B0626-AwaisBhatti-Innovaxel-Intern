package com.example.splitwise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Animation titleAnim;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleAnim = AnimationUtils.loadAnimation(this, R.anim.title_anim);
        title = findViewById(R.id.txtTitle);
        title.startAnimation(titleAnim);
        
        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                // If logged in, go to HomeActivity
                startActivity(new Intent(this, HomeActivity.class));
            } else {
                // If not logged in, go to LoginActivity
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }
}