package pruebas.prueba1.video;

import pruebas.prueba1.MainActivity.SearchBaseUrl;
import pruebas.pureba1.R;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);
 
        WebView mWebView = (WebView) findViewById(R.id.ViewVideo);;
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(PluginState.ON);
        mWebView.loadUrl("http://www.youtube.com/embed/" + "NgNtsmvn2PU" + "?autoplay=1&vq=small");
        mWebView.setWebChromeClient(new WebChromeClient());
        
        
        /*VideoView videoView = (VideoView) findViewById(R.id.ViewVideo);
    	//Use a media controller so that you can scroll the video contents
    	//and also to pause, start the video.
    	MediaController mediaController = new MediaController(this); 
    	mediaController.setAnchorView(videoView);
    	videoView.setMediaController(mediaController);
    	videoView.setVideoURI(Uri.parse("rtsp://r4---sn-4g57kuer.c.youtube.com/CiILENy73wIaGQn12Odrsm0DNhMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp"));
    	videoView.start();*/
    }
}
