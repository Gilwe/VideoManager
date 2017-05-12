package com.example.gilad.videomanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class FoundVideo {
    public String originalUrl = "";
    public String title = "";
    public float size = 0.00f;
    public String originalHTML = "";

    // Setters
    public void setOriginalHTML(String originalHTML) {
        this.originalHTML = originalHTML;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSize(float size) {
        this.size = size;
    }

    FoundVideo (FoundVideo other)
    {
        this.originalUrl = other.originalUrl;
        this.originalHTML = other.originalHTML;
        this.title = other.title;
        this.size = other.size;
    }

    // Default Ctor
    public FoundVideo() {
    }

    public void getTitleFromHTML()
    {
        Document doc = Jsoup.parse(this.originalHTML);

        if (doc != null) {

        this.title = doc.title();
        if (this.title.contains(" - YouTube")) {
            this.title = this.title.substring(0, this.title.indexOf(" - YouTube"));
        }
    }
    }
}

class LoadListener {

    @JavascriptInterface
    public void processHTML(String html) {
        if (html.contains("<video")) {
            synchronized (this) {
                ((WebActivity) GlobalVars.getCurActivity()).HTML = html;
            }
        }
    }
}

public class WebActivity extends AppCompatActivity {

    WebView webView;
    String HTML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_web_act);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (intent != null) {
            webView = (WebView) this.findViewById(R.id.web_view);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.addJavascriptInterface(new LoadListener(), "HTMLOUT");

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    HTML = "";
                }
            });

            webView.loadUrl("https://clips.twitch.tv/AmericanColdSardinePeanutButterJellyTime");
        }

        // Setting the current activity for external code
        GlobalVars.setCurActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Boolean bSuper = super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.web, menu);

        return bSuper;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.download): {
                new YouTubePageStreamUriGetter().execute(webView.getUrl());
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        if (webView != null) {
            if (webView.canGoBack())
                webView.goBack();
            return;
        }

        // In any other case
        super.onBackPressed();
    }
}
