package com.example.win10.movie_iq;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class ClipActivity extends AppCompatActivity {

    // Variables.
    private VideoView clipView;
    private Uri clipUri;
    private Question q;
    private Soundtrack soundtrack;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pb = findViewById(R.id.progressBarClip);
        pb.setVisibility(View.VISIBLE);

        soundtrack = (Soundtrack) getIntent().getSerializableExtra(getString(R.string.soundtrack));
        soundtrack.getMediaPlayer().pause(); // Pausing soundtrack.

        // Clip setup.
        clipView = (VideoView) findViewById(R.id.clipView);
        q = (Question) getIntent().getSerializableExtra(getString(R.string.question));
        clipUri = Uri.parse(q.getUri());
        clipView.setVideoURI(clipUri);
        clipView.requestFocus();
        clipView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pb.setVisibility(View.GONE);
                mp.setLooping(true);
            }
        });
        clipView.start(); // Start clip on a loop.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clipView.stopPlayback();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        soundtrack.getMediaPlayer().start(); // Resuming soundtrack.
        finish();
    }

}
