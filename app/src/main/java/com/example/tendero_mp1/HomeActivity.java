package com.example.tendero_mp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.media.MediaPlayer;

public class HomeActivity extends AppCompatActivity {
    private MediaPlayer lobbyPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton btnPlayGame = findViewById(R.id.btnPlayGame);

        lobbyPlayer = MediaPlayer.create(this, R.raw.music_lobby);
        lobbyPlayer.setLooping(true); // Make the music loop
        lobbyPlayer.setVolume(0.5f, 0.5f);

        btnPlayGame.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }@Override
    protected void onStart() {
        super.onStart();
        if (lobbyPlayer != null) {
            lobbyPlayer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lobbyPlayer != null && lobbyPlayer.isPlaying()) {
            lobbyPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lobbyPlayer != null) {
            lobbyPlayer.stop();
            lobbyPlayer.release();
            lobbyPlayer = null;
        }
    }
}
