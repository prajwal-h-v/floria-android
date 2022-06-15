package prajwal.practice.crophelpproject.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;

public class FetchImage extends AsyncTask<String,Void, Bitmap> {
    @SuppressLint("StaticFieldLeak")
    ImageView imageView;
    ProgressBar progressBar;

    public FetchImage(ImageView imageView, ProgressBar progressBar) {
        this.imageView = imageView;
        this.progressBar = progressBar;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String urlDisplay =strings[0];
        Bitmap myImage =null;
        try{
            InputStream in = new java.net.URL(urlDisplay).openStream();
            myImage = BitmapFactory.decodeStream(in);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return myImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        imageView.setImageBitmap(bitmap);
        if(progressBar != null) progressBar.setVisibility(View.GONE);
    }
}
