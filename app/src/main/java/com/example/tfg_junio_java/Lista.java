
package com.example.tfg_junio_java;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Lista extends Fragment {

    private ArrayList<Pelicula> peliculas;
    private RecyclerView recyclerLista;
    private RecyclerAdapter adapter;
    private EditText searchInput;
    private Button btnFiltrarFavoritos;
    private boolean mostrandoFavoritos = false;

    public Lista() {
        peliculas = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista, container, false);

        recyclerLista = view.findViewById(R.id.recyclerPelis);
        searchInput = view.findViewById(R.id.search_input);
        btnFiltrarFavoritos = view.findViewById(R.id.btnFiltrarFavoritos);

        configurarLayoutManager();

        // Listener para filtrar mientras se escribe
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnFiltrarFavoritos.setOnClickListener(v -> {
            mostrandoFavoritos = !mostrandoFavoritos;
            if (mostrandoFavoritos) {
                cargarFavoritosDesdeFirestore();
                btnFiltrarFavoritos.setText("Mostrar Todas");
            } else {
                verificarSiEsAdminYMostrarPeliculas();
                btnFiltrarFavoritos.setText("Mostrar Favoritos");
            }
        });

        verificarSiEsAdminYMostrarPeliculas();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        configurarLayoutManager(); // también aquí para cuando se rota
    }

    private void configurarLayoutManager() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerLista.setLayoutManager(new GridLayoutManager(getContext(), 3));
        } else {
            recyclerLista.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private void verificarSiEsAdminYMostrarPeliculas() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore.getInstance().collection("DatosUsuario").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Boolean adminValue = documentSnapshot.getBoolean("admin");
                        boolean esAdmin = adminValue != null && adminValue;
                        cargarPeliculasDesdeFirestore(esAdmin);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), getString(R.string.errorPermisos), Toast.LENGTH_SHORT).show();
                        cargarPeliculasDesdeFirestore(false);
                    });
        } else {
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
                        if (pelicula != null) {
                            pelicula.setId(doc.getId());
                            peliculas.add(pelicula);
                        }
                    }

                    if (isAdded()) {
                        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                        adapter = new RecyclerAdapter(getContext(), peliculas, ft, esAdmin);
                        recyclerLista.setAdapter(adapter);
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), getString(R.string.errorCargarPeli), Toast.LENGTH_SHORT).show();
                });
    }

    private void cargarFavoritosDesdeFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore.getInstance().collection("DatosUsuario").document(uid)
                    .collection("Favoritos")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        peliculas.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Pelicula pelicula = doc.toObject(Pelicula.class);
                            if (pelicula != null) {
                                pelicula.setId(doc.getId());
                                peliculas.add(pelicula);
                            }
                        }

                        if (isAdded()) {
                            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                            adapter = new RecyclerAdapter(getContext(), peliculas, ft, false);
                            recyclerLista.setAdapter(adapter);
                        }

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), getString(R.string.errorCargarFavoritos), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
