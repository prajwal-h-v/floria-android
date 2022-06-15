package prajwal.practice.crophelpproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import prajwal.practice.crophelpproject.utils.Crop;

public class CropInfoActivity extends AppCompatActivity {

    private static final String TAG = "customLogging";
    TextView cropName, cropDescription, cropDescriptionLabel, cropUsage, cropUsageLabel, cropProp, cropPropLabel;
    ImageView cropImage;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_info);
        Crop crop = (Crop) getIntent().getSerializableExtra("crop");
        initLayout();
        setLayout(crop);




    }

    private void initLayout() {
        cropName = findViewById(R.id.cropInfoName);
        cropDescription = findViewById(R.id.info_desc);
        cropDescriptionLabel = findViewById(R.id.info_desc_label);
        cropUsage = findViewById(R.id.info_usage);
        cropUsageLabel = findViewById(R.id.info_usage_label);
        cropProp = findViewById(R.id.info_prop);
        cropPropLabel = findViewById(R.id.info_prop_label);
        cropImage = findViewById(R.id.cropIV);
        progressBar = findViewById(R.id.imagePB);
    }

    private void setLayout(Crop crop) {
        cropName.setText(crop.getName());
        cropDescriptionLabel.setText("About "+crop.getName());
        cropDescription.setText(crop.toList(crop.getDescription()));
        cropUsageLabel.setText("Uses of "+ crop.getName());
        cropUsage.setText(crop.toList(crop.getUsage()));
        cropPropLabel.setText("Propagation for "+crop.getName());
        cropProp.setText(crop.toList(crop.getPropagation()));
        crop.setImage(cropImage, progressBar);

    }
}