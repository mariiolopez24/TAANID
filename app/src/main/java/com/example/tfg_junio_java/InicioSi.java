package com.example.tfg_junio_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class InicioSi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_si);

        Button btnSubir = findViewById(R.id.btnSubirPeliculaAdmin);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Boolean esAdmin = documentSnapshot.getBoolean("admin");
                        Log.d("ADMIN_CHECK", "Valor de admin: " + esAdmin); // 👈 Aquí

                        if (esAdmin != null && esAdmin) {
                            Log.d("ADMIN_CHECK", "Es admin, mostrando botón"); // 👈 Aquí
                            btnSubir.setVisibility(View.VISIBLE);
                            btnSubir.setOnClickListener(v -> {
                                Intent intent = new Intent(InicioSi.this, SubirPeliculaActivity.class);
                                startActivity(intent);
                            });
                        } else {
                            Log.d("ADMIN_CHECK", "No es admin o valor nulo"); // 👈 Aquí
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ADMIN_CHECK", "Error al obtener documento", e);
                    });
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Lista lista = new Lista();
        ft.replace(R.id.fragmentContainerView, lista).commit();
    }

}
