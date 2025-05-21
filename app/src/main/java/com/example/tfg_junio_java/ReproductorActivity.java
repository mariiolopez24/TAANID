package com.example.tfg_junio_java;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class ReproductorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        VideoView videoView = findViewById(R.id.videoView);
        String urlPelicula = getIntent().getStringExtra("urlPelicula");

        if (urlPelicula != null) {
            Uri videoUri = Uri.parse(urlPelicula);
            videoView.setVideoURI(videoUri);

            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            videoView.start();
        }
    }
}
