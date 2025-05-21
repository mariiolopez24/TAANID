package com.example.tfg_junio_java;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditarPerfilActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editNombre, editFechaNacimiento;
    private ImageView imageAvatar;
    private Uri nuevaImagenUri;
    private String avatarActualUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        CloudinaryManager.init(getApplicationContext());

        editNombre = findViewById(R.id.editNombre);
        editFechaNacimiento = findViewById(R.id.editFechaNacimiento);
        imageAvatar = findViewById(R.id.imageAvatar);
        Button btnCambiarAvatar = findViewById(R.id.btnCambiarAvatar);
        Button btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        cargarDatosUsuario();

        btnCambiarAvatar.setOnClickListener(v -> abrirGaleria());

        editFechaNacimiento.setOnClickListener(v -> mostrarSelectorFecha());

        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore.getInstance().collection("DatosUsuario")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        editNombre.setText(document.getString("Nombre"));
                        if (document.contains("FechaNacimiento")) {
                            com.google.firebase.Timestamp timestamp = document.getTimestamp("FechaNacimiento");
                            if (timestamp != null) {
                                Date fecha = timestamp.toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                editFechaNacimiento.setText(sdf.format(fecha));
                            }
                        }
                        avatarActualUrl = document.getString("Avatar");
                        if (avatarActualUrl != null && !avatarActualUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(avatarActualUrl)
                                    .circleCrop()
                                    .into(imageAvatar);
                        }
                    }
                });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    private void mostrarSelectorFecha() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    editFechaNacimiento.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void guardarCambios() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String nombre = editNombre.getText().toString().trim();
        String fechaStr = editFechaNacimiento.getText().toString().trim();

        if (nombre.isEmpty() || fechaStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = sdf.parse(fechaStr);

            Map<String, Object> datos = new HashMap<>();
            datos.put("Nombre", nombre);
            datos.put("FechaNacimiento", new com.google.firebase.Timestamp(fecha));

            if (nuevaImagenUri != null) {
                MediaManager.get().upload(nuevaImagenUri)
                        .callback(new UploadCallback() {
                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                String nuevaUrl = resultData.get("secure_url").toString();
                                datos.put("Avatar", nuevaUrl);
                                guardarEnFirestore(user.getUid(), datos);
                            }

                            @Override public void onError(String requestId, ErrorInfo error) {
                                Toast.makeText(EditarPerfilActivity.this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
                            }

                            @Override public void onStart(String requestId) {}
                            @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                            @Override public void onReschedule(String requestId, ErrorInfo error) {}
                        })
                        .dispatch();
            } else {
                guardarEnFirestore(user.getUid(), datos);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Fecha inválida", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarEnFirestore(String uid, Map<String, Object> datos) {
        FirebaseFirestore.getInstance().collection("DatosUsuario")
                .document(uid)
                .update(datos)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar cambios", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            nuevaImagenUri = data.getData();
            imageAvatar.setImageURI(nuevaImagenUri);
        }
    }
}
