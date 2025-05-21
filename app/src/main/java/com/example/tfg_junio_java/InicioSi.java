package com.example.tfg_junio_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class InicioSi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_si);

        Button btnSubir = findViewById(R.id.btnSubirPeliculaAdmin);
        btnSubir.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView avatar = findViewById(R.id.avatarToolbar);
        avatar.setOnClickListener(v -> {
            // Mostrar opciones para editar o borrar usuario
        });





        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore.getInstance().collection("DatosUsuario").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Boolean esAdmin = documentSnapshot.getBoolean("admin");
                        Log.d("ADMIN_CHECK", "Valor de admin: " + esAdmin);

                        if (esAdmin != null && esAdmin) {
                            btnSubir.setVisibility(View.VISIBLE);
                            btnSubir.setOnClickListener(v -> {
                                Intent intent = new Intent(InicioSi.this, SubirPeliculaActivity.class);
                                startActivity(intent);
                            });
                        }else {
                            btnSubir.setVisibility(View.GONE);
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



    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}

