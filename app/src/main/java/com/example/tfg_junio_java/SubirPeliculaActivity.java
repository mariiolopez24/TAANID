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

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SubirPeliculaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editNombre, editSinopsis, editTrailer, editUrlPelicula;
    private ImageView imagePreview;
    private Uri imagenUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CloudinaryManager.init(getApplicationContext());
        setContentView(R.layout.activity_subir_pelicula);

        editNombre = findViewById(R.id.editNombre);
        editSinopsis = findViewById(R.id.editSinopsis);
        editTrailer = findViewById(R.id.editTrailer);
        editUrlPelicula = findViewById(R.id.editUrlPelicula);
        imagePreview = findViewById(R.id.imagePreview);
        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        Button btnSubirPelicula = findViewById(R.id.btnSubirPelicula);

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        btnSubirPelicula.setOnClickListener(v -> verificarCamposYSubir());
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

    private void verificarCamposYSubir() {
        String nombrePeli = editNombre.getText().toString().trim();
        String sinopsis = editSinopsis.getText().toString().trim();
        String urlTrailer = editTrailer.getText().toString().trim();
        String urlPelicula = editUrlPelicula.getText().toString().trim();

        if (nombrePeli.isEmpty() || sinopsis.isEmpty() || urlTrailer.isEmpty() || urlPelicula.isEmpty() || imagenUri == null) {
            Toast.makeText(this, "Por favor, completa todos los campos y selecciona una imagen.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("Peliculas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean existeDuplicado = queryDocumentSnapshots.getDocuments().stream().anyMatch(doc ->
                            nombrePeli.equalsIgnoreCase(doc.getString("nombrePeli")) ||
                                    sinopsis.equalsIgnoreCase(doc.getString("sinopsis")) ||
                                    urlTrailer.equalsIgnoreCase(doc.getString("urlTrailer")) ||
                                    urlPelicula.equalsIgnoreCase(doc.getString("urlPelicula"))
                    );

                    if (existeDuplicado) {
                        Toast.makeText(this, "Ya existe una película con alguno de esos datos.", Toast.LENGTH_SHORT).show();
                    } else {
                        subirImagenYGuardarPelicula(nombrePeli, sinopsis, urlTrailer, urlPelicula);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al verificar duplicados.", Toast.LENGTH_SHORT).show();
                });
    }


    private void subirImagenYGuardarPelicula(String nombrePeli, String sinopsis, String urlTrailer, String urlPelicula) {
        MediaManager.get().upload(imagenUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imagenUrl = resultData.get("secure_url").toString();

                        Map<String, Object> pelicula = new HashMap<>();
                        pelicula.put("nombrePeli", nombrePeli);
                        pelicula.put("sinopsis", sinopsis);
                        pelicula.put("urlTrailer", urlTrailer);
                        pelicula.put("urlPelicula", urlPelicula);
                        pelicula.put("imagenUrl", imagenUrl);

                        FirebaseFirestore.getInstance().collection("Peliculas")
                                .add(pelicula)
                                .addOnSuccessListener(documentReference -> {
                                    String idGenerado = documentReference.getId();
                                    documentReference.update("id", idGenerado)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(SubirPeliculaActivity.this, "Película guardada", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(SubirPeliculaActivity.this, "Guardada, pero no se pudo actualizar el ID", Toast.LENGTH_SHORT).show();
                                                finish();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SubirPeliculaActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(SubirPeliculaActivity.this, "Error al subir imagen: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }
}
