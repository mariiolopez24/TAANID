
package com.example.tfg_junio_java;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarPeliculaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editNombre, editSinopsis, editTrailer;
    private ImageView imagePreview;
    private Uri imagenUri;
    private Pelicula pelicula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CloudinaryManager.init(getApplicationContext());

        setContentView(R.layout.activity_editar_pelicula);

        Button btnEliminarPelicula = findViewById(R.id.btnEliminarPelicula);
        btnEliminarPelicula.setOnClickListener(v -> eliminarPelicula());

        editNombre = findViewById(R.id.editNombre);
        editSinopsis = findViewById(R.id.editSinopsis);
        editTrailer = findViewById(R.id.editTrailer);
        imagePreview = findViewById(R.id.imagePreview);
        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        Button btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        pelicula = (Pelicula) getIntent().getSerializableExtra("pelicula");

        if (pelicula != null) {
            editNombre.setText(pelicula.getNombrePeli());
            editSinopsis.setText(pelicula.getSinopsis());
            editTrailer.setText(pelicula.getUrlTrailer());

            Glide.with(this)
                    .load(pelicula.getImagenUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(imagePreview);
        }

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagenUri = data.getData();
            imagePreview.setImageURI(imagenUri);
        }
    }

    private void guardarCambios() {
        String nombrePeli = editNombre.getText().toString().trim();
        String sinopsis = editSinopsis.getText().toString().trim();
        String urlTrailer = editTrailer.getText().toString().trim();

        if (nombrePeli.isEmpty() || sinopsis.isEmpty() || urlTrailer.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> peliculaActualizada = new HashMap<>();
        peliculaActualizada.put("nombrePeli", nombrePeli);
        peliculaActualizada.put("sinopsis", sinopsis);
        peliculaActualizada.put("urlTrailer", urlTrailer);

        if (imagenUri != null) {
            MediaManager.get().upload(imagenUri)
                    .callback(new UploadCallback() {
                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String imagenUrl = resultData.get("secure_url").toString();
                            peliculaActualizada.put("imagenUrl", imagenUrl);
                            actualizarPeliculaEnFirestore(peliculaActualizada);
                        }

                        @Override public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(EditarPeliculaActivity.this, "Error al subir imagen: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        }

                        @Override public void onStart(String requestId) {}
                        @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                        @Override public void onReschedule(String requestId, ErrorInfo error) {}
                    })
                    .dispatch();
        } else {
            actualizarPeliculaEnFirestore(peliculaActualizada);
        }
    }

    private void actualizarPeliculaEnFirestore(Map<String, Object> peliculaActualizada) {
        FirebaseFirestore.getInstance().collection("Peliculas")
                .document(pelicula.getId())
                .update(peliculaActualizada)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditarPeliculaActivity.this, "Película actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarPeliculaActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                });
    }

    private void eliminarPelicula() {
        FirebaseFirestore.getInstance().collection("Peliculas")
                .document(pelicula.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Película eliminada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar película", Toast.LENGTH_SHORT).show();
                });
    }

}
