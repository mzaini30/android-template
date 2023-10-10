package com.user.app;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class JavaScriptInterface {
    private final Context context;

    public JavaScriptInterface(Context context) {
        this.context = context;
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
}
