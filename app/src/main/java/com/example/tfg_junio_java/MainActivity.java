package com.example.tfg_junio_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class MainActivity extends AppCompatActivity {
    TextView username;
    TextView password;
    Button iniciarSesion;
    Button registro;

    Button SesionInvitado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("usuarios");

        username = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        iniciarSesion = findViewById(R.id.iniciarSesion);
        registro = findViewById(R.id.botonRegistro);
        SesionInvitado = findViewById(R.id.SesionInvitado);
        BaseDatosHelper dbHelper = new BaseDatosHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EstructuraBDD.column_usuario, username.getText().toString());
        values.put(EstructuraBDD.column_contraseña, password.getText().toString());

        long newRowId = db.insert(EstructuraBDD.tabla_usuarios, null, values);

        // Escribir datos en Firebase
        myRef.child(String.valueOf(newRowId)).setValue(values);

        // Leer datos desde Firebase
        myRef.child(String.valueOf(newRowId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String usuario = dataSnapshot.child(EstructuraBDD.column_usuario).getValue(String.class);
                    String contrasenia = dataSnapshot.child(EstructuraBDD.column_contraseña).getValue(String.class);
                    Log.d("TAG", "Usuario: " + usuario + ", Contraseña: " + contrasenia);
                } else {
                    Log.d("TAG", "No se encontraron datos.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Error al leer datos.", error.toException());
            }
        });

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userdb = username.getText().toString();
                String passdb = password.getText().toString();

                Cursor cursor = dbHelper.getReadableDatabase().query(
                        EstructuraBDD.tabla_usuarios,
                        EstructuraBDD.projection,
                        EstructuraBDD.selection,
                        new String[]{userdb, passdb},
                        null,
                        null,
                        EstructuraBDD.sortOrderAsc
                );

                if (cursor.moveToFirst()) {
                    String usuario = cursor.getString(1);
                    String contrasenia = cursor.getString(2);
                    if (userdb.equals(usuario) && passdb.equals(contrasenia) && !userdb.isEmpty() && !passdb.isEmpty() ) {
                        Intent j = new Intent(MainActivity.this, FragmentActivity.class); // Navega a FragmentActivity
                        startActivity(j);
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                FragmentoRegistro fragmentoRegistro = new FragmentoRegistro();
                ft.replace(android.R.id.content, fragmentoRegistro).commit();
            }
        });

        SesionInvitado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(MainActivity.this, FragmentActivity.class); // Navega a FragmentActivity
                startActivity(j);

            }
        });
    }
}