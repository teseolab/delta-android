package no.ntnu.mikaelr.delta.util;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageHandler {

    public static Bitmap getImageFromCameraIntentResult(Uri imageUri) throws IOException {
        Bitmap image;
        try {
            image = ImageHandler.decodeAndRotateImage(imageUri, 200);
        } catch (IOException e) {
            throw new IOException(ErrorMessage.COULD_NOT_ADD_IMAGE);
        }
        return image;
    }

    public static Bitmap getImageFromGalleryIntentResult(Intent data, ContentResolver contentResolver) throws IOException {
        Bitmap image;
        try {
            // Two streams are necessary, since the image will be decoded twice.
            // One time to find the correct size, and another for the sampling.
            InputStream inputStream = contentResolver.openInputStream(data.getData());
            InputStream inputStream2 = contentResolver.openInputStream(data.getData());
            image = decodeImageFromFilePath(inputStream, inputStream2, 200);
        } catch (FileNotFoundException e) {
            throw new IOException(ErrorMessage.COULD_NOT_ADD_IMAGE);
        }
        return image;
    }

    public static Bitmap rotateImage(Uri imageUri, ContentResolver contentResolver) throws IOException {

        ExifInterface ei = new ExifInterface(imageUri.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap image = bitmapFromUri(imageUri, contentResolver);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                image = rotateImage(image, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                image = rotateImage(image, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                image = rotateImage(image, 270);
                break;
        }

        return image;

    }

    public static Bitmap bitmapFromUri(Uri imageUri, ContentResolver contentResolver) throws IOException {
        return MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
    }

    public static Bitmap decodeAndRotateImage(Uri imageUri, int size) throws IOException {

        ExifInterface ei = new ExifInterface(imageUri.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap image = decodeImageFromFilePath(imageUri, size);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                image = rotateImage(image, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                image = rotateImage(image, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                image = rotateImage(image, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
        }

        return image;
    }

    public static Bitmap rotateImage(Bitmap bitmap, String imageUri) throws IOException {
        ExifInterface ei = new ExifInterface(imageUri);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap = rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
        }
        return bitmap;
    }

    public static Bitmap decodeImageFromFilePath(InputStream inputStream, InputStream inputStream2, int size) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, o);

        // Find the correct scale value. It should be the power of 2.
        int scale = 1;
        while(o.outWidth / scale / 2 >= size &&
                o.outHeight / scale / 2 >= size) {
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;

        return BitmapFactory.decodeStream(inputStream2, null, o2);
    }

    /** Decodes image and scales it to reduce memory consumption */
    public static Bitmap decodeImageFromFilePath(Uri uri, int size) {

        File imageFile = new File(uri.getPath());

        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(imageFile), null, o);

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= size &&
                    o.outHeight / scale / 2 >= size) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(imageFile), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Decodes image and scales it to reduce memory consumption */
    public static Bitmap decodeImageFromFileUri(ContentResolver contentResolver, Uri uri, int size) {

        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, o);

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= size &&
                    o.outHeight / scale / 2 >= size) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap rotatedImage;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        rotatedImage = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return rotatedImage;
    }

    public static byte[] byteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static ByteArrayInputStream inputStreamFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] byteArray = bos.toByteArray();
        return new ByteArrayInputStream(byteArray);
    }

    public static void writeBitmapToFile(Bitmap bitmap, File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Create a file Uri for saving an image */
    public static Uri getOutputImageFileUri(){
        return Uri.fromFile(getOutputImageFile());
    }

    /** Create a File for saving an image */
    public static File getOutputImageFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Delta");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Delta", "failed to create directory");
                return null;
            }
        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imageFile;
        imageFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");

        return imageFile;
    }

}
