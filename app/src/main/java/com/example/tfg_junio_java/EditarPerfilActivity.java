
package com.example.tfg_junio_java;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    private String nombreOriginal;
    private String fechaNacimientoOriginal;
    private Button btnGuardarCambios;
    private boolean datosCargados = false;
    private boolean ignorarCambios = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        CloudinaryManager.init(getApplicationContext());

        editNombre = findViewById(R.id.editNombre);
        editFechaNacimiento = findViewById(R.id.editFechaNacimiento);
        imageAvatar = findViewById(R.id.imageAvatar);
        Button btnCambiarAvatar = findViewById(R.id.btnCambiarAvatar);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        btnGuardarCambios.setEnabled(false);

        cargarDatosUsuario();

        btnCambiarAvatar.setOnClickListener(v -> abrirGaleria());

        editFechaNacimiento.setOnClickListener(v -> mostrarSelectorFecha());

        btnGuardarCambios.setOnClickListener(v -> guardarCambios());

        editNombre.addTextChangedListener(textWatcher);
        editFechaNacimiento.addTextChangedListener(textWatcher);
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        ignorarCambios = true;

        FirebaseFirestore.getInstance().collection("DatosUsuario")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String nombre = document.getString("Nombre");
                        editNombre.setText(nombre);
                        nombreOriginal = nombre;

                        if (document.contains("FechaNacimiento")) {
                            com.google.firebase.Timestamp timestamp = document.getTimestamp("FechaNacimiento");
                            if (timestamp != null) {
                                Date fecha = timestamp.toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String fechaFormateada = sdf.format(fecha);
                                editFechaNacimiento.setText(fechaFormateada);
                                fechaNacimientoOriginal = fechaFormateada;
                            }
                        }

                        avatarActualUrl = document.getString("Avatar");
                        if (avatarActualUrl != null && !avatarActualUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(avatarActualUrl)
                                    .circleCrop()
                                    .into(imageAvatar);
                        }

                        datosCargados = true;
                        ignorarCambios = false;
                        verificarCambios();
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
        if (!datosCargados) return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String nombre = editNombre.getText().toString().trim();
        String fechaStr = editFechaNacimiento.getText().toString().trim();

        boolean nombreCambiado = !nombre.equals(nombreOriginal);
        boolean fechaCambiada = !fechaStr.equals(fechaNacimientoOriginal);
        boolean avatarCambiado = nuevaImagenUri != null;

        if (!nombreCambiado && !fechaCambiada && !avatarCambiado) {
            Toast.makeText(this, getString(R.string.noCambios), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = sdf.parse(fechaStr);

            Map<String, Object> datos = new HashMap<>();
            if (nombreCambiado) datos.put("Nombre", nombre);
            if (fechaCambiada) datos.put("FechaNacimiento", new com.google.firebase.Timestamp(fecha));

            if (avatarCambiado) {
                MediaManager.get().upload(nuevaImagenUri)
                        .callback(new UploadCallback() {
                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                String nuevaUrl = resultData.get("secure_url").toString();
                                datos.put("Avatar", nuevaUrl);
                                guardarEnFirestore(user.getUid(), datos);
                            }

                            @Override public void onError(String requestId, ErrorInfo error) {
                                Toast.makeText(EditarPerfilActivity.this, getString(R.string.errorAvatar), Toast.LENGTH_SHORT).show();
                            }

                            @Override public void onStart(String requestId) {}
                            @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                            @Override public void onReschedule(String requestId, ErrorInfo error) {}
                        })
                        .dispatch();
            } else {
                if (datos.isEmpty()) {
                    Toast.makeText(this, getString(R.string.noCambios), Toast.LENGTH_SHORT).show();
                } else {
                    guardarEnFirestore(user.getUid(), datos);
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.fechaInvalida), Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarEnFirestore(String uid, Map<String, Object> datos) {
        FirebaseFirestore.getInstance().collection("DatosUsuario")
                .document(uid)
                .update(datos)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, getString(R.string.perfilActualizado), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.errorCambios), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            nuevaImagenUri = data.getData();
            imageAvatar.setImageURI(nuevaImagenUri);
            verificarCambios();
        }
    }

    private void verificarCambios() {
        String nombreActual = editNombre.getText().toString().trim();
        String fechaActual = editFechaNacimiento.getText().toString().trim();

        boolean nombreCambiado = !nombreActual.equals(nombreOriginal);
        boolean fechaCambiada = !fechaActual.equals(fechaNacimientoOriginal);
        boolean avatarCambiado = nuevaImagenUri != null;

        btnGuardarCambios.setEnabled(nombreCambiado || fechaCambiada || avatarCambiado);
        btnGuardarCambios.setBackgroundTintList(ContextCompat.getColorStateList(this,
                btnGuardarCambios.isEnabled() ? R.color.orange : R.color.grisClaro));
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!ignorarCambios) {
                verificarCambios();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
}
