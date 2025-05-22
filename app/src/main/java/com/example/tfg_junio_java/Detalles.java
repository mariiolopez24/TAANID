
package com.example.tfg_junio_java;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Detalles extends Fragment {

    private static final String ARG_PELICULA = "pelicula";
    private static final String ARG_ES_ADMIN = "esAdmin";

    private Pelicula pelicula;
    private boolean esAdmin;

    private TextView textTitulo;
    private TextView textSinopsis;
    private ImageView image;
    private Button btnEditarPelicula;
    private Button btnVerTrailer;
    private Button btnVerPelicula;
    private WebView webView;

    public Detalles() {}

    public static Detalles newInstance(Pelicula pelicula, boolean esAdmin) {
        Detalles fragment = new Detalles();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PELICULA, pelicula);
        args.putBoolean(ARG_ES_ADMIN, esAdmin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        btnVerTrailer = view.findViewById(R.id.btnVerTrailer);
        btnVerPelicula = view.findViewById(R.id.btnVerPelicula);
        webView = view.findViewById(R.id.webViewPelicula);
        ImageView estrellaFavorito = view.findViewById(R.id.estrellaFavoritoDetalles);

        if (pelicula != null) {
            textTitulo.setText(pelicula.getNombrePeli());
            textSinopsis.setText(pelicula.getSinopsis());

            Glide.with(this)
                    .load(pelicula.getImagenUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(image);
        }

        btnEditarPelicula.setVisibility(esAdmin ? View.VISIBLE : View.GONE);

        btnEditarPelicula.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditarPeliculaActivity.class);
            intent.putExtra("pelicula", pelicula);
            startActivity(intent);
        });

        btnVerTrailer.setOnClickListener(v -> {
            if (pelicula.getUrlTrailer() != null && !pelicula.getUrlTrailer().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pelicula.getUrlTrailer()));
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "No hay tráiler disponible", Toast.LENGTH_SHORT).show();
            }
        });

        btnVerPelicula.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && !user.isAnonymous()) {
                if (pelicula.getUrlPelicula() != null && !pelicula.getUrlPelicula().isEmpty()) {
                    webView.setVisibility(View.VISIBLE);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setWebViewClient(new WebViewClient());

                    // Extraer ID del video de YouTube
                    String videoId = Uri.parse(pelicula.getUrlPelicula()).getQueryParameter("v");
                    if (videoId != null) {
                        String embedUrl = "https://www.youtube.com/embed/" + videoId;
                        webView.loadUrl(embedUrl);
                    } else {
                        Toast.makeText(getContext(), "URL de YouTube no válida", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No hay película disponible", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Debes iniciar sesión para ver la película completa", Toast.LENGTH_SHORT).show();
            }
        });

        estrellaFavorito.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null || user.isAnonymous()) {
                Toast.makeText(getContext(), "Debes iniciar sesión para añadir a favoritos", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = user.getUid();
            String peliId = pelicula.getId();

            db.collection("DatosUsuario").document(uid)
                    .collection("Favoritos").document(peliId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            db.collection("DatosUsuario").document(uid)
                                    .collection("Favoritos").document(peliId)
                                    .delete();
                            estrellaFavorito.setImageResource(R.drawable.ic_star_border);
                        } else {
                            db.collection("DatosUsuario").document(uid)
                                    .collection("Favoritos").document(peliId)
                                    .set(pelicula);
                            estrellaFavorito.setImageResource(R.drawable.ic_star_filled);
                        }
                    });
        });

        return view;
    }
}
