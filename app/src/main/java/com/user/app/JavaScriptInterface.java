package com.user.app;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class JavaScriptInterface {
    private final Context context;
    private RewardedAd rewardedAd;
    // private RewardedAd rewardAd;
    private boolean isAdReady = false;
    private final String TAG = "MainActivity";
    private Activity activity;

    public void setRewardedAd(RewardedAd ad) {
        rewardedAd = ad;
        isAdReady = true;
    }

    public boolean isAdReady() {
        return isAdReady;
    }

    public JavaScriptInterface(Context context) {
        this.context = context;
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }

    @JavascriptInterface
    public void set_wallpaper(String imageUrl) {
        // Di sini Anda bisa mengatur gambar wallpaper sebagai latar belakang
        // menggunakan WallpaperManager atau cara lain yang sesuai.
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            InputStream inputStream = context.getAssets().open(imageUrl); // Baca gambar dari assets
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(context, "Wallpaper successfully set.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to set wallpaper.", Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void reward(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Code untuk menampilkan Rewarded Ads di sini
                // rewarded ads
                 AdRequest adRequest = new AdRequest.Builder().build();
                 RewardedAd.load(context, "ca-app-pub-3940256099942544/5224354917",
                     adRequest, new RewardedAdLoadCallback() {
                         @Override
                         public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                             // Handle the error.
                             Log.d(TAG, loadAdError.toString());
                             // Toast.makeText(context, loadAdError.toString(), Toast.LENGTH_SHORT).show();
                             rewardedAd = null;
                         }

                         @Override
                         public void onAdLoaded(@NonNull RewardedAd ad) {
                             rewardedAd = ad;
                             Log.d(TAG, "Ad was loaded.");
                             // Toast.makeText(context, "Ad was loaded.", Toast.LENGTH_SHORT).show();
                         }
                     }
                 );

                //   menjalankan ads reward
                if (isAdReady) {
                    rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            // Toast.makeText(context, "The user earned the reward.", Toast.LENGTH_SHORT).show();
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                        }
                    });
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                    // Toast.makeText(context, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
       
        

        
    }
}
