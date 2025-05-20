package com.example.tfg_junio_java;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Lista extends Fragment implements SearchView.OnQueryTextListener {

    private ArrayList<Pelicula> peliculas;
    private RecyclerView recyclerLista;
    private SearchView search;
    private RecyclerAdapter adapter;

    public Lista() {
        peliculas = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista, container, false);

        recyclerLista = view.findViewById(R.id.recyclerPelis);
        search = view.findViewById(R.id.search);
        search.setOnQueryTextListener(this);

        recyclerLista.setLayoutManager(new LinearLayoutManager(view.getContext()));
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        adapter = new RecyclerAdapter(view.getContext(), peliculas, ft);
        recyclerLista.setAdapter(adapter);

        cargarPeliculasDesdeFirestore();

        return view;
    }

    private void cargarPeliculasDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Peliculas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    peliculas.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Pelicula pelicula = doc.toObject(Pelicula.class);
                        peliculas.add(pelicula);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar películas", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }
}
