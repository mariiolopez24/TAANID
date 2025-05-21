package com.example.tfg_junio_java;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        verificarSiEsAdminYMostrarPeliculas();

        return view;
    }

    private void verificarSiEsAdminYMostrarPeliculas() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Boolean adminValue = documentSnapshot.getBoolean("esAdmin");
                        boolean esAdmin = adminValue != null && adminValue;

                        Context context = getContext();
                        if (context != null) {
                            SharedPreferences prefs = context.getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE);
                            prefs.edit().putBoolean("esAdmin", esAdmin).apply();
                        }

                        cargarPeliculasDesdeFirestore(esAdmin);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al verificar permisos", Toast.LENGTH_SHORT).show();

                        Context context = getContext();
                        if (context != null) {
                            SharedPreferences prefs = context.getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE);
                            prefs.edit().putBoolean("esAdmin", false).apply();
                        }

                        cargarPeliculasDesdeFirestore(false);
                    });
        } else {
            Context context = getContext();
            if (context != null) {
                SharedPreferences prefs = context.getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE);
                prefs.edit().putBoolean("esAdmin", false).apply();
            }

            cargarPeliculasDesdeFirestore(false);
        }
    }





    private void cargarPeliculasDesdeFirestore(boolean esAdmin) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Peliculas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    peliculas.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Pelicula pelicula = doc.toObject(Pelicula.class);
                        pelicula.setId(doc.getId());
                        peliculas.add(pelicula);
                    }

                    if (isAdded()) {
                        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                        adapter = new RecyclerAdapter(getContext(), peliculas, ft, esAdmin);
                        recyclerLista.setAdapter(adapter);
                    }

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
