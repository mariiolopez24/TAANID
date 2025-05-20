package com.example.tfg_junio_java;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public static Detalles newInstance(Pelicula pelicula) {
        Detalles fragment = new Detalles();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PELICULA, pelicula);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pelicula = (Pelicula) getArguments().getSerializable(ARG_PELICULA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalles, container, false);

        textTitulo = view.findViewById(R.id.titulodetalles);
        textSinopsis = view.findViewById(R.id.sinopsisdetalles);
        image = view.findViewById(R.id.imagendetalles);

        if (pelicula != null) {
            textTitulo.setText(pelicula.getNombrePeli());
            textSinopsis.setText(pelicula.getSinopsis());

            Glide.with(this)
                    .load(pelicula.getImagenUrl())
                    .placeholder(R.drawable.placeholder) // opcional
                    .into(image);
        }

        return view;
    }
}
