package com.instagramclone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PopupActivity extends Activity {

    ImageView imageView;
    TextView username;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        getWindow().setBackgroundDrawable(new ColorDrawable(0));

        imageView = (ImageView) findViewById(R.id.fullimageView);
        username = (TextView) findViewById(R.id.nameTextview);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        String name = getIntent().getStringExtra("name");

        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

       /* int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        if (h<500){
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 700));
        }
        if (h>2000){
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1500));
        }

        *//*if (h>2000){
            bitmap.setHeight(h/2);
            bitmap.setWidth(w/2);
        }*//*
        Log.d("height and wieght",h+","+w);*/
        imageView.setImageBitmap(bitmap);
        username.setText(name);


    }

    public void cloaseimage(View view) {
        onBackPressed();
    }

    public void download(View view) {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        long t = System.currentTimeMillis();
        String fname = "Image-" + t + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            Log.d("working ", "i guess");
            // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
            //     Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            out.flush();
            out.close();
            Toast.makeText(PopupActivity.this, "Successfully Downloaded!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.d("not working IO", "" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("not working else", "" + e.getMessage());
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("ExternalStorage", "Scanned " + path + ":");
                        Log.d("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    public void share(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);

        } else {

            Bitmap icon = bitmap;
            if (icon != null) {
                long t = System.currentTimeMillis();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + t + "temporary_file.jpg");
                try {
                    if (f.createNewFile()) {
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(bytes.toByteArray());
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + t + "temporary_file.jpg"));
                startActivity(Intent.createChooser(share, "Share"));
            }

        }

    }
}
