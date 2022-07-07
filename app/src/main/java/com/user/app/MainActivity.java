package com.user.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import android.widget.RelativeLayout;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import android.view.View;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.LoadAdError;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartAppSDK.init(this, "204848974", false);
        StartAppAd.disableSplash();

        RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
        mainLayout.setVisibility(View.GONE);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                mAdView.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
                // Get the Main relative layout of the entire activity
                // RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
                // Define StartApp Banner
                Banner startAppBanner = new Banner(MainActivity.this);
                RelativeLayout.LayoutParams bannerParameters =
                            new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);    
                // Add to main Layout
                mainLayout.addView(startAppBanner, bannerParameters);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
            // .setDomain("api.example.com")
            .addPathHandler("/assets/", new AssetsPathHandler(this))
            .build();

        WebView webview = (WebView) findViewById(R.id.webview);

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,  WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (
                    url.contains("http://") 
                    || url.contains("https://")
                    && !url.contains("https://appassets.androidplatform.net")
                ){
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                    return true;
                } else {
                    return false;
                }

            }
          
        });

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        String databasePath = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(databasePath);
        webSettings.setDomStorageEnabled(true);


        if (savedInstanceState == null) {
            webview.loadUrl("https://appassets.androidplatform.net/assets/dist/index.html#/");
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView webview = (WebView) findViewById(R.id.webview);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webview.canGoBack()) {
                        webview.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        WebView webview = (WebView) findViewById(R.id.webview);

        super.onSaveInstanceState(outState);
        webview.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        WebView webview = (WebView) findViewById(R.id.webview);

        super.onRestoreInstanceState(savedInstanceState);
        webview.restoreState(savedInstanceState);
    }


}
