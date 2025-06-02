package com.example.tfg_junio_java;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class RecyclerAdapterTest {

    private Context context;
    private ArrayList<Pelicula> peliculas;
    private RecyclerAdapter adapter;

    @Mock
    private androidx.fragment.app.FragmentTransaction ft;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();
        peliculas = new ArrayList<>();

        Map<String, String> sinopsis1 = new HashMap<>();
        sinopsis1.put("es", "Sinopsis en español de la película 1");
        sinopsis1.put("en", "Synopsis in English of movie 1");
        peliculas.add(new Pelicula("1", "https://example.com/image1.jpg", "Película 1", sinopsis1, "https://example.com/trailer1", "https://example.com/movie1"));

        Map<String, String> sinopsis2 = new HashMap<>();
        sinopsis2.put("es", "Sinopsis en español de la película 2");
        sinopsis2.put("en", "Synopsis in English of movie 2");
        peliculas.add(new Pelicula("2", "https://example.com/image2.jpg", "Película 2", sinopsis2, "https://example.com/trailer2", "https://example.com/movie2"));

        adapter = new RecyclerAdapter(context, peliculas, ft, true);
    }

    @Test
    public void testFilterByTitle() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        adapter.getFilter().filter("Película 1", count -> latch.countDown());
        latch.await();

        assertEquals(1, adapter.getItemCount());
        assertEquals("Película 1", adapter.listaPelis.get(0).getNombrePeli());
    }

    @Test
    public void testFilterBySynopsis() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        adapter.getFilter().filter("Sinopsis en español de la película 2", count -> latch.countDown());
        latch.await();

        assertEquals(1, adapter.getItemCount());
        assertEquals("Película 2", adapter.listaPelis.get(0).getNombrePeli());
    }

    @Test
    public void testFilterNoMatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        adapter.getFilter().filter("No existe", count -> latch.countDown());
        latch.await();

        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testFilterEmpty() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        adapter.getFilter().filter("", count -> latch.countDown());
        latch.await();

        assertEquals(2, adapter.getItemCount());
    }
}
