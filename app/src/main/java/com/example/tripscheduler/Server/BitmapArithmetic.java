package com.example.tripscheduler.Server;

import android.graphics.Bitmap;

public class BitmapArithmetic {
    private static final int PIXEL_THRESHOLD = 200;

    public static Bitmap resizeBitmap(Bitmap img) {
            int dstWidth = img.getWidth();
            int dstHeight = img.getHeight();

            dstHeight = dstHeight * PIXEL_THRESHOLD / dstWidth;
            dstWidth = PIXEL_THRESHOLD;

            img = Bitmap.createScaledBitmap(img, dstWidth, dstHeight, true);

            return img;
    }
}
