package com.wyrnlab.jotdownthatmovie.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Jota on 06/03/2017.
 */

public class ImageHandler {

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        // Decode bitmap with inSampleSize set
        ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
        return BitmapFactory.decodeStream(imageStream);
    }
}
