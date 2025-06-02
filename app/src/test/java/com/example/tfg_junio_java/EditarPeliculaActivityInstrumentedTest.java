
package com.example.tfg_junio_java;

import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bumptech.glide.Glide;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class EditarPeliculaActivityInstrumentedTest {

    @Test
    public void testActivityLaunchesWithPelicula() {

        Map<String, String> sinopsis = new HashMap<>();
        sinopsis.put("es", "Sinopsis en español");
        sinopsis.put("en", "Synopsis in English");
        Pelicula pelicula = new Pelicula("1", "https://example.com/image.jpg", "Sample Movie", sinopsis, "https://example.com/trailer", "https://example.com/movie");


        Intent intent = new Intent(getApplicationContext(), EditarPeliculaActivity.class);
        intent.putExtra("pelicula", pelicula);


        try (ActivityScenario<EditarPeliculaActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {

                assertNotNull(activity);


                EditText editNombre = activity.findViewById(R.id.editNombre);
                EditText editSinopsis = activity.findViewById(R.id.editSinopsis);
                EditText editSinopsisEn = activity.findViewById(R.id.editSinopsisEn);
                EditText editTrailer = activity.findViewById(R.id.editTrailer);
                EditText editUrlPelicula = activity.findViewById(R.id.editUrlPelicula);
                ImageView imagePreview = activity.findViewById(R.id.imagePreview);

                assertEquals("Sample Movie", editNombre.getText().toString());
                assertEquals("Sinopsis en español", editSinopsis.getText().toString());
                assertEquals("Synopsis in English", editSinopsisEn.getText().toString());
                assertEquals("https://example.com/trailer", editTrailer.getText().toString());
                assertEquals("https://example.com/movie", editUrlPelicula.getText().toString());

                Glide.with(activity).load(pelicula.getImagenUrl()).into(imagePreview);
            });
        }
    }
}
