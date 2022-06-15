package prajwal.practice.crophelpproject.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import prajwal.practice.crophelpproject.R;

public class CropAdapter extends ArrayAdapter<Crop> {
    public CropAdapter(Context context, ArrayList<Crop> cropList ){
        super(context, 0, cropList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item,parent,false);
        }
        Crop crop = getItem(position);
        TextView cardTV = listItemView.findViewById(R.id.cardTV);
        ImageView cardIV = listItemView.findViewById(R.id.cardIV);
        cardTV.setText(crop.getName());
        crop.setImage(cardIV,null);
        return listItemView;

    }
}
