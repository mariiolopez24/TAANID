package com.example.tfg_junio_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toolbar;

public class InicioSi extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_si);




        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Lista lista = new Lista();
        ft.replace(R.id.fragmentContainerView,lista).commit();


    }






}