
package com.example.tfg_junio_java;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class InicioSi extends AppCompatActivity {

    private static final int EDITAR_PERFIL_REQUEST = 1001;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_si);

        Button btnSubir = findViewById(R.id.btnSubirPeliculaAdmin);
        btnSubir.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        avatar = findViewById(R.id.avatarToolbar);

        avatar.setOnClickListener(v -> {
            View popupView = LayoutInflater.from(this).inflate(R.layout.dialog_avatar_menu, null);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true
            );

            Button btnEditarPerfil = popupView.findViewById(R.id.btnEditarPerfil);
            Button btnCerrarSesion = popupView.findViewById(R.id.btnCerrarSesion);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.isAnonymous()) {
                btnCerrarSesion.setEnabled(false);
                btnCerrarSesion.setAlpha(0.5f);
            }

            btnEditarPerfil.setOnClickListener(view -> {
                popupWindow.dismiss();
                if (user != null && !user.isAnonymous()) {
                    Intent intent = new Intent(InicioSi.this, EditarPerfilActivity.class);
                    startActivityForResult(intent, EDITAR_PERFIL_REQUEST);
                } else {
                    Toast.makeText(InicioSi.this, getString(R.string.iniciarSesionEditar), Toast.LENGTH_SHORT).show();
                }
            });

            btnCerrarSesion.setOnClickListener(view -> {
                popupWindow.dismiss();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(InicioSi.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });


            int[] location = new int[2];
            v.getLocationOnScreen(location);
            popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupHeight = popupView.getMeasuredHeight();
            int xOffset = -popupView.getMeasuredWidth() + v.getWidth();
            int yOffset = -popupHeight;

            popupWindow.showAsDropDown(v, xOffset, yOffset);
        });

        cargarDatosUsuario(btnSubir);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Lista lista = new Lista();
        ft.replace(R.id.fragmentContainerView, lista).commit();
    }

    private void cargarDatosUsuario(Button btnSubir) {
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
                        } else {
                            btnSubir.setVisibility(View.GONE);
                        }

                        // Cargar avatar
                        String avatarUrl = documentSnapshot.getString("Avatar");
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.avatar_profile_vector)
                                    .circleCrop()
                                    .into(avatar);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ADMIN_CHECK", "Error al obtener documento", e);
                    });
        }
    }

    private void recargarAvatar() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("DatosUsuario")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String avatarUrl = documentSnapshot.getString("Avatar");
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.avatar_profile_vector)
                                    .circleCrop()
                                    .into(avatar);
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDITAR_PERFIL_REQUEST) {
            recargarAvatar();
            Toast.makeText(this, getString(R.string.perfilActualizado), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean esAnonimo = user != null && user.isAnonymous();

        if (currentFragment instanceof Lista) {
            if (!esAnonimo) {
                // Mostrar diálogo personalizado solo si el usuario está registrado
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmacion, null);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create();

                Button btnAceptar = dialogView.findViewById(R.id.btnAceptar);
                Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

                btnAceptar.setOnClickListener(v -> {
                    dialog.dismiss();
                    irAlLogin();
                });

                btnCancelar.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            } else {
                irAlLogin();
            }
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void irAlLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
