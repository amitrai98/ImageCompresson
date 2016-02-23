package tablayoutdemo.android.com.imagecompresson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Currency;

/**
 * Created by amitrai on 19/02/2016.
 */
public class ImageUtil {

    private static final String FILE_NAME = "abc.jpg";
    private static ProgressDialog mprogressDialog;

    private String outputfilepath = "";
    private String FOLDER_NAME = "Image_Compression";

    private static String TAG = ImageUtil.class.getSimpleName();

    /**
     * Alert dialog to show information
     *
     * @param context
     * @param title
     * @param message
     */
    public static void showAlertDialog(Context context, String title, String message) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                }).create().show();
    }

    /**
     * Show the Toast message
     */

    public static void showToast(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    public static void showprogress(Context context, String message, boolean isCancellable) {

        mprogressDialog = new ProgressDialog(context);
        mprogressDialog.setTitle("");
        mprogressDialog.setIndeterminate(false);
        mprogressDialog.setMessage(message);
        mprogressDialog.setCancelable(isCancellable);
        mprogressDialog.show();
    }

    public static void dismissProgress() {
        if (mprogressDialog != null && mprogressDialog.isShowing()) {
            mprogressDialog.dismiss();
            mprogressDialog = null;
        }
    }

    /**
     * @param context
     * @return true, if Internet available otherwise false
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
            if (networkInfos != null)
                for (NetworkInfo info : networkInfos) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;
    }






    // compress the image
    public static String compressImage(Activity act, String filePath,
                                       final ImageView img_view, final TextView txt_filsizeaftercompression) {

//        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
//        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;


//        Bitmap imgBmp = BitmapFactory.decodeFile(filePath);
//        ByteArrayOutputStream bao_c = new ByteArrayOutputStream();
//        imgBmp.compress(Bitmap.CompressFormat.JPEG, 10, bao_c);
//        byte[] ba = bao_c.toByteArray();
//        String ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);
//        Log.e("ba1", "" + ba1);
//      max Height and width values of the compressed image is taken as 816x612
//        float maxHeight = 1024.0f;
//        float maxWidth = 816.0f;
//        float imgRatio = actualWidth / actualHeight;
//        float maxRatio = maxWidth / maxHeight;
//
////      width and height values are set maintaining the aspect ratio of the image
//        if (actualHeight > maxHeight || actualWidth > maxWidth) {
//            if (imgRatio < maxRatio) {
//                imgRatio = maxHeight / actualHeight;
//                actualWidth = (int) (imgRatio * actualWidth);
//                actualHeight = (int) maxHeight;
//            } else if (imgRatio > maxRatio) {
//                imgRatio = maxWidth / actualWidth;
//                actualHeight = (int) (imgRatio * actualHeight);
//                actualWidth = (int) maxWidth;
//            } else {
//                actualHeight = (int) maxHeight;
//                actualWidth = (int) maxWidth;
//            }
//        }

//        ContentBody foto = new InputStreamBody(in, "image/jpeg", "filename");


//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inSampleSize = 1;//calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;

        options.inDither=false;                     //Disable Dithering mode
            //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        options.inTempStorage=new byte[32 * 1024];
//        options.inTempStorage = new byte[64 * 1024];

        Bitmap bmp=null;
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
        //    scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);

        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas();
//        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
//            scaledBitmap = Bitmap.createBitmap(bmp, 0, 0,
//                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
//                    true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename();

        String ba2 = null;

        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            bmp.compress(Bitmap.CompressFormat.JPEG, 30, out);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
         boolean bbb =    bmp.compress(Bitmap.CompressFormat.JPEG, 30, bao);
            byte [] bas = bao.toByteArray();
            ba2 = Base64.encodeToString(bas, Base64.DEFAULT);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap finalScaledBitmap = bmp;

        final String finalBa = ba2;
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(finalScaledBitmap != null)
                    img_view.setImageBitmap(finalScaledBitmap);
                    txt_filsizeaftercompression.setText("file size after compression "+ humanReadableByteCount(finalBa.length(), true) );
            }
        });
        Log.e(TAG, ""+ba2.length());
        return ba2;
    }

    // calculates a proper value for inSampleSize based on the actual and required dimensions:
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    // get the file name of compressed image
    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "FileCompression/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

//    public static String getRealPathFromURI(Uri contentUri, Context c) {
//        return compressImage(getPath(c, contentUri));
//    }

//    public static String getPath(final Context context, final Uri uri) {
//
//        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
//
//        // DocumentProvider
//
//        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
//            // ExternalStorageProvider
//            if (isExternalStorageDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
//                }
//
//            }
//            // DownloadsProvider
//            else if (isDownloadsDocument(uri)) {
//
//                String id = DocumentsContract.getDocumentId(uri);//getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//
//                return getDataColumn(context, contentUri, null, null);
//            }
//            // MediaProvider
//            else if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                Uri contentUri = null;
//                if ("image".equals(type)) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//
//                final String selection = "_id=?";
//                final String[] selectionArgs = new String[]{
//                        split[1]
//                };
//
//                return getDataColumn(context, contentUri, selection, selectionArgs);
//            }
//        }
//        // MediaStore (and general)
//        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            return getDataColumn(context, uri, null, null);
//        }
//        // File
//        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//
//        return null;
//    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public void createAlert(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
    }

    public String getDeviceID(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * convert currency code to currency symbol
     *
     * @param currencyCode currency code
     * @return currency code/currency symbol
     */
    public String getCurrencySymbol(String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode);
            return currency.getSymbol();
        } catch (Exception e) {
            return currencyCode;
        }
    }


    /**
     * retunrs the file path
     * @return
     */
    private String getOutputfilepath(){
        File yourFile = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME+ File.separator+FILE_NAME);
        String json_string = "";
        if(!yourFile.exists())
            return yourFile.getAbsolutePath();
        else {
            try {
                 yourFile.createNewFile();
                return yourFile.getAbsolutePath();
            }
            catch(Exception e){
                System.err.println("Error: Target File Cannot Be Read");
            }

            return null;
        }

    }

    /**
     * getting file size in kb
     * @param bytes
     * @param si
     * @return
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    Bitmap stringToBitmapConverter(String image) {
        String encodedImage = image;
        byte[] decodedString = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


}
