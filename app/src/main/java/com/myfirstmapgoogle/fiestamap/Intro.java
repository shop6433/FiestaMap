package com.myfirstmapgoogle.fiestamap;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Intro extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        new Thread(){
            public void run(){
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(Intro.this,MapsActivity.class);
                startActivity(i);
                finish();
            }
        }.start();
    }
}