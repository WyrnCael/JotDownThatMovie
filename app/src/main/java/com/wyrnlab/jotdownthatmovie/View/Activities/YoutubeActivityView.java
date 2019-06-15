package com.wyrnlab.jotdownthatmovie.View.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.wyrnlab.jotdownthatmovie.R;

import com.wyrnlab.jotdownthatmovie.Model.General;

/**
 * Created by Jota on 03/03/2017.
 */

public class YoutubeActivityView extends YouTubeBaseActivity {

    private String trailerId;
    protected ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);

        Intent i = getIntent();
        trailerId = (String)i.getSerializableExtra("TrailerId");

        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        YouTubePlayer.OnInitializedListener initListener = new YouTubePlayer.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.setFullscreen(true);
                youTubePlayer.loadVideo(trailerId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                /*Intent intent =  new Intent(YoutubeActivityView.this, VideoEnabledWebView.class);
                intent.putExtra("Pelicula", pelicula);
                startActivityForResult(intent, 1);*/
            }
        };
        youTubeView.initialize(General.APIKEY, initListener);
    }

}