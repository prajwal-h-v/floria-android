package prajwal.practice.crophelpproject.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

public class Crop implements Serializable {
    private static final String TAG = "customLogging";
    private final String id;
    private final String name;
    private final String description;
    private final String usage;
    private final String propagation;
    private final String imageURL;

    public Crop(String id, String name, String description, String usage, String propagation, String imageURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.propagation = propagation;
        this.imageURL = imageURL;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public String getPropagation() {
        return propagation;
    }

    public void setImage(ImageView cropImage, ProgressBar progressBar) {
        try{
            new FetchImage(cropImage, progressBar).execute(imageURL);


        }catch (Exception e){
            Log.d(TAG, "setImage: exception -> "+e.getMessage());
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Crop{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", usage='" + usage + '\'' +
                ", propagation='" + propagation + '\'' +
                '}';
    }

    public String toList(String text){

        String[] texts = text.split("\\. ");
        String list ="";

        for(String item : texts){

            list = list +"  "+ item.trim() +".\n\n";

        }
        return list;

    }
}

