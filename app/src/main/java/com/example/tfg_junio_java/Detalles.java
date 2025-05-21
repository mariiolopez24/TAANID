package com.example.tfg_junio_java;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class Detalles extends Fragment {

    private static final String ARG_PELICULA = "pelicula";
    private Pelicula pelicula;

    private TextView textTitulo;
    private TextView textSinopsis;
    private ImageView image;

    public Detalles() {
        // Constructor vacío requerido
    }

    private static final String ARG_ES_ADMIN = "esAdmin";
    private boolean esAdmin;
    private Button btnEditarPelicula;


    public static Detalles newInstance(Pelicula pelicula, boolean esAdmin) {
        Detalles fragment = new Detalles();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PELICULA, pelicula);
        args.putBoolean(ARG_ES_ADMIN, esAdmin); // ← AÑADIDO
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pelicula = (Pelicula) getArguments().getSerializable(ARG_PELICULA);
        }

        if (getArguments() != null) {
            pelicula = (Pelicula) getArguments().getSerializable(ARG_PELICULA);
            esAdmin = getArguments().getBoolean(ARG_ES_ADMIN, false);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalles, container, false);

        textTitulo = view.findViewById(R.id.titulodetalles);
        textSinopsis = view.findViewById(R.id.sinopsisdetalles);
        image = view.findViewById(R.id.imagendetalles);
        btnEditarPelicula = view.findViewById(R.id.btnEditarPelicula);

        if (getArguments() != null) {
            pelicula = (Pelicula) getArguments().getSerializable("pelicula");
        }

        if (pelicula != null) {
            textTitulo.setText(pelicula.getNombrePeli());
            textSinopsis.setText(pelicula.getSinopsis());

            Glide.with(this)
                    .load(pelicula.getImagenUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(image);
        }

        // Leer esAdmin desde SharedPreferences y asignarlo al campo de clase
        SharedPreferences prefs = requireContext().getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE);
        esAdmin = prefs.getBoolean("esAdmin", false);

        btnEditarPelicula.setVisibility(esAdmin ? View.VISIBLE : View.GONE);
        btnEditarPelicula.setVisibility(View.VISIBLE);

        btnEditarPelicula.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditarPeliculaActivity.class);
            intent.putExtra("pelicula", pelicula);
            startActivity(intent);
        });

        return view;
    }


}
