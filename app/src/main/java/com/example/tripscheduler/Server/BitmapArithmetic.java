package com.example.tripscheduler.Server;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import afu.org.checkerframework.checker.nullness.qual.NonNull;
import afu.org.checkerframework.checker.nullness.qual.Nullable;

public class BitmapArithmetic {
    private static final int PIXEL_THRESHOLD = 100;

    public static Bitmap resizeBitmap(Bitmap img) {
            int dstWidth = img.getWidth();
            int dstHeight = img.getHeight();

            dstHeight = dstHeight * PIXEL_THRESHOLD / dstWidth;
            dstWidth = PIXEL_THRESHOLD;

            img = Bitmap.createScaledBitmap(img, dstWidth, dstHeight, true);

            return img;
    }
}
