
package com.example.tfg_junio_java;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.webkit.WebView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.bumptech.glide.Glide;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class DetallesInstrumentedTest {

    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testFragmentCreation() {
        Pelicula pelicula = new Pelicula("1", "https://example.com/image.jpg", "Example Movie", null, "https://example.com/movie", "https://example.com/trailer");
        boolean esAdmin = true;

        Bundle args = new Bundle();
        args.putSerializable("pelicula", pelicula);
        args.putBoolean("esAdmin", esAdmin);

        FragmentScenario<Detalles> scenario = FragmentScenario.launchInContainer(Detalles.class, args);

        scenario.onFragment(fragment -> {
            assertNotNull(fragment);
            assertEquals(pelicula, fragment.getArguments().getSerializable("pelicula"));
            assertEquals(esAdmin, fragment.getArguments().getBoolean("esAdmin"));
        });
    }

    @Test
    public void testViewInitialization() {
        Pelicula pelicula = new Pelicula("1", "https://example.com/image.jpg", "Example Movie", null, "https://example.com/movie", "https://example.com/trailer");
        boolean esAdmin = true;

        Bundle args = new Bundle();
        args.putSerializable("pelicula", pelicula);
        args.putBoolean("esAdmin", esAdmin);

        FragmentScenario<Detalles> scenario = FragmentScenario.launchInContainer(Detalles.class, args);

        scenario.onFragment(fragment -> {
            View view = fragment.getView();
            assertNotNull(view);

            TextView textTitulo = view.findViewById(R.id.titulodetalles);
            TextView textSinopsis = view.findViewById(R.id.sinopsisdetalles);
            ImageView image = view.findViewById(R.id.imagendetalles);
            Button btnEditarPelicula = view.findViewById(R.id.btnEditarPelicula);
            Button btnVerTrailer = view.findViewById(R.id.btnVerTrailer);
            Button btnVerPelicula = view.findViewById(R.id.btnVerPelicula);
            WebView webView = view.findViewById(R.id.webViewPelicula);

            assertNotNull(textTitulo);
            assertNotNull(textSinopsis);
            assertNotNull(image);
            assertNotNull(btnEditarPelicula);
            assertNotNull(btnVerTrailer);
            assertNotNull(btnVerPelicula);
            assertNotNull(webView);

            assertEquals(pelicula.getNombrePeli(), textTitulo.getText().toString());
            Glide.with(fragment).load(pelicula.getImagenUrl()).placeholder(R.drawable.placeholder).into(image);
        });
    }
}
