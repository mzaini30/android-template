package com.user.app;

import android.app.Activity;
import android.content.Intent;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.ValueCallback;

import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import android.widget.RelativeLayout;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    // variables para manejar la subida de archivos
    private final static int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri[]> mUploadMessage;
    private RewardedAd rewardedAd;
    private final String TAG = "MainActivity";
    private AdView mAdView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            new AppOpenManager(this.getApplication(), "ca-app-pub-3940256099942544/3419835294", "portrait");
        } else  {
            new AppOpenManager(this.getApplication(), "ca-app-pub-3940256099942544/3419835294", "landscape");
        }
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

        // establecemos el cliente chrome para seleccionar archivos
        webview.setWebChromeClient(new MyWebChromeClient());

        // Setelah iklan berhasil dimuat
        JavaScriptInterface jsInterface = new JavaScriptInterface(this);

       RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
       new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
           @Override
           public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
               // Handle the error.
               Log.d(TAG, loadAdError.toString());
               rewardedAd = null;
               jsInterface.setRewardedAd(rewardedAd);
           }

           @Override
           public void onAdLoaded(@NonNull RewardedAd ad) {
               rewardedAd = ad;
               jsInterface.setRewardedAd(rewardedAd);
               Log.d(TAG, "Ad was loaded.");
           }
       });

        webview.addJavascriptInterface(jsInterface, "Andro");

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        String databasePath = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(databasePath);
        webSettings.setDomStorageEnabled(true);


        if (savedInstanceState == null) {
            webview.loadUrl("https://appassets.androidplatform.net/assets/index.html");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // manejo de seleccion de archivo
        if (requestCode == FILECHOOSER_RESULTCODE) {

            if (null == mUploadMessage || intent == null || resultCode != RESULT_OK) {
                return;
            }

            Uri[] result = null;
            String dataString = intent.getDataString();

            if (dataString != null) {
                result = new Uri[]{ Uri.parse(dataString) };
            }

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
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

    /**
     * Clase para configurar el chrome client para que nos permita seleccionar archivos
     */
    private class MyWebChromeClient extends WebChromeClient {

        // maneja la accion de seleccionar archivos
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

            // asegurar que no existan callbacks
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }

            mUploadMessage = filePathCallback;

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*"); // set MIME type to filter

            MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FILECHOOSER_RESULTCODE );

            return true;
        }
    }


}
