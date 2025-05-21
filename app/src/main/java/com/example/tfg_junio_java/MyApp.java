package com.example.tfg_junio_java;

import android.app.Application;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CloudinaryManager.init(this);
    }
}
