package com.example.resource;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int var = 1500;
    TextView resources, welcome;
    Animation topanim, bottomanim;
    SharedPreferences sharedPreferences;
    Boolean log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        topanim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        bottomanim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        resources = (TextView) findViewById(R.id.resources);
        welcome = (TextView) findViewById(R.id.welcome);
        resources.setAnimation(topanim);
        welcome.setAnimation(bottomanim);

       sharedPreferences=getSharedPreferences("resources",Context.MODE_PRIVATE);
       log=sharedPreferences.getBoolean("isloggedin",false);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    if(log==false){
                        Intent intent = new Intent(MainActivity.this, signupActivity.class);
                        startActivity(intent);
                        finish();
                   }
                    else {
                        Intent intent1 = new Intent(MainActivity.this, resourceActivity.class);
                        startActivity(intent1);
                        finish();
                  }
            }
        }, var);
    }
}
