package prajwal.practice.crophelpproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    private static final int INTERNET_PERMISSION = 100;
    private static final int CAMERA_PERMISSION = 101;
    RequestQueue requestQueue;
    Intent errorIntent;
    private CardView cropRecommendationCard, diseaseDetectionCard, CropInformationCard ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initApp();

//      OnClick for Crop recommendation
            cropRecommendationCard.setOnClickListener(view -> {
                Intent cropRecommend = new Intent(MainActivity.this, CropRecommendationActivity.class);
                startActivity(cropRecommend);
            });

//      OnClick for Disease Detection
            diseaseDetectionCard.setOnClickListener(view -> {
                Intent diseaseDetection = new Intent(MainActivity.this, DiseaseDetectionActivity.class);
                startActivity(diseaseDetection);
            });

//        Onclick for crop Information
            CropInformationCard.setOnClickListener(view -> {
                Intent cropActivity = new Intent(MainActivity.this, CropsActivity.class);
                startActivity(cropActivity);
            });

        }
        catch (Exception e){
            Toast.makeText(this, "err"+ e, Toast.LENGTH_SHORT).show();
        }
    }
    private void initApp(){
        errorIntent = new Intent(MainActivity.this, ErrorActivity.class);
        requestQueue = Volley.newRequestQueue(this);
        checkPermissions();
            setContentView(R.layout.activity_main);

//            errorIntent.putExtra("Error", "No sufficient Permissions");
//            startActivity(errorIntent);
//            finish();

        cropRecommendationCard = findViewById(R.id.btn_crop_recommend);
        diseaseDetectionCard = findViewById(R.id.btn_crop_detect);
        CropInformationCard = findViewById(R.id.btn_crop_information);
    }
    private void checkPermissions() {
        boolean allPermission;
        boolean internetPermission = false;
        boolean cameraPermission = false;
        if(checkPermission(Manifest.permission.INTERNET, INTERNET_PERMISSION)) internetPermission = true;
        if(checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION) )cameraPermission= true;
        allPermission = internetPermission && cameraPermission;

    }
    private boolean checkPermission(String permission, int reqCode){
        boolean isGranted = false;
        if(ContextCompat.checkSelfPermission(MainActivity.this, permission) ==
                PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {permission}, reqCode);
        }
        else{
            isGranted = true;
        }
        return isGranted;
    }
    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(reqCode, permissions, grantResults);
        if(reqCode == INTERNET_PERMISSION){
            if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){

            }
            else{
                Toast.makeText(this, "Internet permission denied", Toast.LENGTH_SHORT).show();

            }
        }
        else if (reqCode == CAMERA_PERMISSION){
            if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){

            }
            else{
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

}