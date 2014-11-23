package org.worshipsongs.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import org.worshipsongs.worship.R;

/**
 * @Author : Madasamy
 * @Version : 1.0.0
 */
public class AboutWebViewActivity extends Activity
{
    private static final String ABOUT_FILE_PATH = "file:///android_asset/about.html";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_webview_activity);
        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(ABOUT_FILE_PATH);
    }
}
