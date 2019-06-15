package com.wyrnlab.jotdownthatmovie.View.Activities;

import com.wyrnlab.jotdownthatmovie.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

public class VideoPlayer extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);
 
        WebView mWebView = (WebView) findViewById(R.id.webView);;
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(PluginState.ON);
        mWebView.loadUrl("http://www.youtube.com/embed/" + "NgNtsmvn2PU" + "?autoplay=1&vq=small");
        mWebView.setWebChromeClient(new WebChromeClient());
    }
}
