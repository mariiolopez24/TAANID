
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubirPeliculaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editNombre, editSinopsis, editTrailer;
    private ImageView imagePreview;
    private Uri imagenUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_pelicula);

        editNombre = findViewById(R.id.editNombre);
        editSinopsis = findViewById(R.id.editSinopsis);
        editTrailer = findViewById(R.id.editTrailer);
        imagePreview = findViewById(R.id.imagePreview);
        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        Button btnSubirPelicula = findViewById(R.id.btnSubirPelicula);

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        btnSubirPelicula.setOnClickListener(v -> subirPelicula());
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

    private void subirPelicula() {
        String nombrePeli = editNombre.getText().toString().trim();
        String sinopsis = editSinopsis.getText().toString().trim();
        String urlTrailer = editTrailer.getText().toString().trim();

        if (nombrePeli.isEmpty() || sinopsis.isEmpty() || urlTrailer.isEmpty() || imagenUri == null) {
            Toast.makeText(this, "Por favor, completa todos los campos y selecciona una imagen.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("peliculas/" + UUID.randomUUID().toString() + ".jpg");

        storageRef.putFile(imagenUri)
            .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imagenUrl = uri.toString();

                // Crear objeto película
                Map<String, Object> pelicula = new HashMap<>();
                pelicula.put("nombrePeli", nombrePeli);
                pelicula.put("sinopsis", sinopsis);
                pelicula.put("urlTrailer", urlTrailer);
                pelicula.put("imagenUrl", imagenUrl);

                // Guardar en Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Peliculas")
                  .add(pelicula)
                  .addOnSuccessListener(documentReference -> {
                      Toast.makeText(SubirPeliculaActivity.this, "Película guardada", Toast.LENGTH_SHORT).show();
                      finish(); // Cerrar la actividad después de guardar
                  })
                  .addOnFailureListener(e -> {
                      Toast.makeText(SubirPeliculaActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                  });
            }))
            .addOnFailureListener(e -> {
                Toast.makeText(SubirPeliculaActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
            });
    }
}
