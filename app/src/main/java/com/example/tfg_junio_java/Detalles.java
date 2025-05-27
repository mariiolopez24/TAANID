
package com.example.tfg_junio_java;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private EditText editComentario;
    private Button btnEnviarComentario;
    private LinearLayout layoutComentarios;
    private RatingBar ratingBar;

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
        editComentario = view.findViewById(R.id.editComentario);
        btnEnviarComentario = view.findViewById(R.id.btnEnviarComentario);
        layoutComentarios = view.findViewById(R.id.layoutComentarios);
        ratingBar = view.findViewById(R.id.ratingBar);

        if (pelicula != null) {
            textTitulo.setText(pelicula.getNombrePeli());
            String idioma = java.util.Locale.getDefault().getLanguage();
            String sinopsis = pelicula.getSinopsis().get(idioma);
            if (sinopsis == null) {
                sinopsis = pelicula.getSinopsis().get("es");
            }
            textSinopsis.setText(sinopsis);


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
                Toast.makeText(getContext(), getString(R.string.noTrailer), Toast.LENGTH_SHORT).show();
            }
        });

        btnVerPelicula.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && !user.isAnonymous()) {
                if (pelicula.getUrlPelicula() != null && !pelicula.getUrlPelicula().isEmpty()) {
                    webView.setVisibility(View.VISIBLE);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setWebViewClient(new WebViewClient());


                    String videoId = Uri.parse(pelicula.getUrlPelicula()).getQueryParameter("v");
                    if (videoId != null) {
                        String embedUrl = "https://www.youtube.com/embed/" + videoId;
                        webView.loadUrl(embedUrl);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.noUrl), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.noPeli), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.iniciaPeli), Toast.LENGTH_SHORT).show();
            }
        });

        estrellaFavorito.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null || user.isAnonymous()) {
                Toast.makeText(getContext(), getString(R.string.iniciarFavoritos), Toast.LENGTH_SHORT).show();
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous()) {
            editComentario.setOnTouchListener((v, event) -> {
                Toast.makeText(getContext(), getString(R.string.iniciarComentar), Toast.LENGTH_SHORT).show();
                return true;
            });

            ratingBar.setOnTouchListener((v, event) -> {
                Toast.makeText(getContext(), getString(R.string.iniciarPuntuar), Toast.LENGTH_SHORT).show();
                return true;
            });

            btnEnviarComentario.setEnabled(false);
        }

        btnEnviarComentario.setOnClickListener(v -> {
            String texto = editComentario.getText().toString().trim();
            int puntuacion = (int) ratingBar.getRating();

            if (texto.isEmpty() && puntuacion == 0) {
                Toast.makeText(getContext(), getString(R.string.escribirComentoPuntar), Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String peliId = pelicula.getId();

            db.collection("DatosUsuario").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String nombreUsuario = documentSnapshot.getString("Nombre");
                        Comentario comentario = new Comentario(
                                user.getUid(),
                                nombreUsuario != null ? nombreUsuario : "Anónimo",
                                texto,
                                puntuacion,
                                System.currentTimeMillis()
                        );

                        db.collection("Peliculas").document(peliId)
                                .collection("Comentarios")
                                .add(comentario)
                                .addOnSuccessListener(doc -> {
                                    Toast.makeText(getContext(), getString(R.string.comentEnviado), Toast.LENGTH_SHORT).show();
                                    editComentario.setText("");
                                    ratingBar.setRating(0);
                                    cargarComentarios();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), getString(R.string.errorComent), Toast.LENGTH_SHORT).show();
                                });
                    });
        });

        cargarComentarios();

        return view;
    }

    private void cargarComentarios() {
        layoutComentarios.removeAllViews();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String peliId = pelicula.getId();

        db.collection("Peliculas").document(peliId)
                .collection("Comentarios")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {
                        String nombre = doc.getString("nombreUsuario");
                        String texto = doc.getString("comentario");
                        int puntuacion = doc.getLong("puntuacion").intValue();
                        String comentarioId = doc.getId();
                        String usuarioIdComentario = doc.getString("usuarioId");
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        boolean esPropio = currentUser != null && currentUser.getUid().equals(usuarioIdComentario);

                        LinearLayout comentarioLayout = new LinearLayout(getContext());
                        comentarioLayout.setOrientation(LinearLayout.HORIZONTAL);
                        comentarioLayout.setGravity(Gravity.CENTER_VERTICAL);
                        comentarioLayout.setPadding(0, 8, 0, 8);

                        TextView tv = new TextView(getContext());
                        tv.setText(nombre + ": " + texto);
                        tv.setTextColor(Color.WHITE);

                        RatingBar rating = new RatingBar(getContext(), null, android.R.attr.ratingBarStyleSmall);
                        LinearLayout.LayoutParams ratingParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        rating.setLayoutParams(ratingParams);
                        rating.setNumStars(5);
                        rating.setStepSize(1.0f);
                        rating.setRating(puntuacion);
                        rating.setIsIndicator(true);
                        rating.setScaleX(0.85f);
                        rating.setScaleY(0.85f);
                        rating.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF6600")));

                        comentarioLayout.addView(tv);
                        comentarioLayout.addView(rating);

                        if (esPropio) {
                            ImageButton btnEditar = new ImageButton(getContext());
                            btnEditar.setImageResource(R.drawable.ic_edit);
                            btnEditar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6600")));
                            btnEditar.setColorFilter(Color.WHITE);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(16, 8, 0, 8);
                            btnEditar.setLayoutParams(params);

                            btnEditar.setOnClickListener(v -> {
                                mostrarDialogoEditarComentario(doc.getId(), texto, puntuacion);
                            });

                            comentarioLayout.addView(btnEditar);
                        }

                        if (esAdmin || esPropio) {
                            ImageButton btnEliminar = new ImageButton(getContext());
                            btnEliminar.setImageResource(R.drawable.ic_delete);
                            btnEliminar.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            btnEliminar.setColorFilter(Color.WHITE);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 8, 0, 8);
                            btnEliminar.setLayoutParams(params);
                            btnEliminar.setContentDescription("Eliminar comentario");

                            btnEliminar.setOnClickListener(v -> {
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View dialogView = inflater.inflate(R.layout.dialog_eliminar_comentario, null);

                                AlertDialog dialog = new AlertDialog.Builder(requireContext())
                                        .setView(dialogView)
                                        .create();

                                TextView title = dialogView.findViewById(R.id.dialogTitle);
                                TextView message = dialogView.findViewById(R.id.dialogMessage);
                                Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);
                                Button btnAceptar = dialogView.findViewById(R.id.btnAceptar);

                                btnCancelar.setOnClickListener(v1 -> dialog.dismiss());
                                btnAceptar.setOnClickListener(v1 -> {
                                    FirebaseFirestore.getInstance()
                                            .collection("Peliculas").document(pelicula.getId())
                                            .collection("Comentarios").document(comentarioId)
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(getContext(), getString(R.string.comentEliminado), Toast.LENGTH_SHORT).show();
                                                cargarComentarios();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), getString(R.string.errorComentEliminado), Toast.LENGTH_SHORT).show();
                                            });
                                    dialog.dismiss();
                                });

                                dialog.show();
                            });

                            comentarioLayout.addView(btnEliminar);
                        }

                        layoutComentarios.addView(comentarioLayout);
                    }
                });
    }

    private void mostrarDialogoEditarComentario(String comentarioId, String textoActual, int puntuacionActual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_editar_comentario, null);
        builder.setView(dialogView);

        EditText editComentario = dialogView.findViewById(R.id.editComentarioDialog);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBarDialog);
        Button btnGuardar = dialogView.findViewById(R.id.btnGuardarComentario);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarComentario);

        editComentario.setText(textoActual);
        ratingBar.setRating(puntuacionActual);

        AlertDialog dialog = builder.create();

        btnGuardar.setOnClickListener(v -> {
            String nuevoTexto = editComentario.getText().toString().trim();
            int nuevaPuntuacion = (int) ratingBar.getRating();

            if (nuevoTexto.isEmpty()) {
                Toast.makeText(getContext(), "El comentario no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance()
                    .collection("Peliculas").document(pelicula.getId())
                    .collection("Comentarios").document(comentarioId)
                    .update("comentario", nuevoTexto, "puntuacion", nuevaPuntuacion)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Comentario actualizado", Toast.LENGTH_SHORT).show();
                        cargarComentarios();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    });

            dialog.dismiss();
        });
        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
