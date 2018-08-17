package com.example.win10.movie_iq;

import android.media.MediaPlayer;

import java.io.Serializable;

public class Soundtrack implements Serializable {
    private static transient MediaPlayer mediaPlayer;

    public Soundtrack() {
        mediaPlayer = new MediaPlayer();
    }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }


}
