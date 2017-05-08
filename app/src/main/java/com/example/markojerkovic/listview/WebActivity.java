package com.example.markojerkovic.listview;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.content.Intent;


public class WebActivity extends AppCompatActivity {

    static final public String WEBPAGE_NOTHING = "about:blank";
    static final public String LOG_TAG = "webviewActivity";
    public String news_host = null;

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        myWebView = (WebView) findViewById(R.id.web_view);
        myWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Get url from the intent
        String url = null;
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
        } else {
            url = extras.getString("URL");
        }

        // If the url begins with "www." then the host name is the
        // substring of everything besides the "www."
        if (Uri.parse(url).getHost().startsWith("www.")) {
            news_host = Uri.parse(url).getHost().substring(4);
        } else {
            news_host = Uri.parse(url).getHost();
        }
        Log.d(LOG_TAG, "news host: " + news_host);

        myWebView.loadUrl(url);
    }



    private class MyWebViewClient extends WebViewClient {
        // If the url is not from the same the host as the original news_host,
        // then the url should be opened in chrome.
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().contains(news_host)) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    // Source: http://stackoverflow.com/questions/6077141/
    //                         how-to-go-back-to-previous-page-if-back-button-is-pressed-in-webview
    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "onPause called");
        Method pause = null;
        try {
            pause = WebView.class.getMethod("onPause");
        } catch (SecurityException e) {
            // Nothing
        } catch (NoSuchMethodException e) {
            // Nothing
        }
        if (pause != null) {
            try {
                pause.invoke(myWebView);
            } catch (InvocationTargetException e) {
            } catch (IllegalAccessException e) {
            }
        } else {
            myWebView.clearView();
            myWebView.loadUrl(WEBPAGE_NOTHING);
        }
        super.onPause();
    }

}

