package com.memory.wq.provider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.memory.wq.constants.AppProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileOP {


    private final Context context;

    public FileOP(Context context) {
        this.context = context;
    }

    public File handleImage(Uri sourceUri) {
        File tempFile = createTempImageFile();
        try (InputStream in = context.getContentResolver().openInputStream(sourceUri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {

                int width = bitmap.getWidth() / 2;
                int height = bitmap.getHeight() / 2;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                scaledBitmap.recycle();
            }
            bitmap.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }
//        System.out.println("======压缩后大小:"+tempFile.length()/1024+"Kb");
        return tempFile;
    }




    public File createTempImageFile() {

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timeStamp = dateTime.format(date);


        String imageFileName = "avatar_" + timeStamp + "_";

        //文件夹
        File fileDir = new File(context.getCacheDir(), "avatars");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File tempFile = null;
        try {
            tempFile = File.createTempFile(imageFileName, ".jpg", fileDir);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }


    public void deleteTempCameraFile() {
        File fileDir = new File(context.getCacheDir(), "avatars");
        if (!fileDir.exists()) {
            return;
        }
        for (File tempFile : fileDir.listFiles()) {
            if (tempFile.isFile() && tempFile.isFile()) {
                tempFile.delete();
            }
        }

    }

    public Uri file2Uri(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24)
            uri = FileProvider.getUriForFile(context, AppProperties.AUTHORITY, file);
        else
            uri = Uri.fromFile(file);
        return uri;
    }
}
