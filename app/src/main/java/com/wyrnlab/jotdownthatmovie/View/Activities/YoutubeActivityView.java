package com.wyrnlab.jotdownthatmovie.View.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.wyrnlab.jotdownthatmovie.R;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * Created by Jota on 03/03/2017.
 */

public class YoutubeActivityView extends AppCompatActivity {

    private String trailerId;
    protected ProgressDialog pDialog;
    private FrameLayout fullscreenViewContainer;

    private YouTubePlayer youTubePlayer;
    private boolean isFullscreen = false;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Trailer");
        setContentView(R.layout.video_player_complete);

        Intent i = getIntent();
        trailerId = (String)i.getSerializableExtra("TrailerId");
        Log.d("Trailer", trailerId);

        //onBackPressedDispatcher.addCallback(onBackPressedCallback);
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        FrameLayout fullscreenViewContainer = findViewById(R.id.full_screen_view_container);
        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1).fullscreen(0)
                .build();
        // we need to initialize manually in order to pass IFramePlayerOptions to the player
        youTubePlayerView.setEnableAutomaticInitialization(false);

        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View view, @NonNull Function0<Unit> function0) {
                isFullscreen = true;
                // the video will continue playing in fullscreenView
                youTubePlayerView.setVisibility(View.GONE);
                fullscreenViewContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(view);
                // optionally request landscape orientation
                // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            @Override
            public void onExitFullscreen() {
                finish();
            }
        });
        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                YoutubeActivityView.this.youTubePlayer = youTubePlayer;
                youTubePlayer.toggleFullscreen();
                youTubePlayer.loadVideo(trailerId, 0f);
            }
        }, iFramePlayerOptions);
        getLifecycle().addObserver(youTubePlayerView);
        //youTubePlayer.toggleFullscreen();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

}