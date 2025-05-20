package com.example.tfg_junio_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

public class InicioNo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_no);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();
            }
        },1000);
    }
}