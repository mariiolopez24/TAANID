
package com.example.tfg_junio_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private TextView username;
    private TextView password;
    private Button iniciarSesion;
    private Button registro;
    private Button SesionInvitado;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        iniciarSesion = findViewById(R.id.iniciarSesion);
        registro = findViewById(R.id.botonRegistro);
        SesionInvitado = findViewById(R.id.SesionInvitado);

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.d("LOGIN_SUCCESS", "Usuario autenticado: " + user.getUid());
                                Intent intent = new Intent(MainActivity.this, InicioSi.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.w("LOGIN_FAILED", "Error de inicio de sesión", task.getException());
                                Toast.makeText(MainActivity.this, "Error de inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
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
                FirebaseAuth.getInstance().signInAnonymously()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Sesión anónima iniciada correctamente
                                Intent intent = new Intent(MainActivity.this, InicioSi.class);
                                startActivity(intent);
                                finish(); // Opcional: cierra la pantalla de login
                            } else {
                                // Error al iniciar sesión anónima
                                Log.e("INVITADO", "Error al iniciar como invitado", task.getException());
                                Toast.makeText(MainActivity.this, "No se pudo iniciar como invitado", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}
