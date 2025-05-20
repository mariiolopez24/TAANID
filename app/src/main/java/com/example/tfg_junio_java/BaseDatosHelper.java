package com.example.tfg_junio_java;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BaseDatosHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TAANID";
    private static final int DATABASE_VERSION = 1;
    public BaseDatosHelper(@Nullable Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EstructuraBDD.SQL_Entar_Datos);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(EstructuraBDD.SQL_Borrar_Datos);
        onCreate(db);

    }
}

