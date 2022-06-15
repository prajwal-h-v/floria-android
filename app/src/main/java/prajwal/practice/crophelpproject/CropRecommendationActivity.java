package prajwal.practice.crophelpproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import prajwal.practice.crophelpproject.utils.Crop;

public class CropRecommendationActivity extends AppCompatActivity {

    private static final String TAG = "customLogging";
    EditText n, p, k, ph, rainfall;
    Button getLoc, getRec,exploreCrop;
    String location;
    String recCrop= null;

    TextView currLoc, recommendedCrop;
    int PERMISSION_ID = 44;
    boolean isLocation;
    double lat=0, lon=0;
    ProgressBar loading;
    String url ="https://floria-app.herokuapp.com/api/crop-recommend";
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLocation = false;
        setContentView(R.layout.activity_crop_recommendation);
        n = findViewById(R.id.nitrogen);
        p = findViewById(R.id.phosphorous);
        k = findViewById(R.id.potassium);
        ph = findViewById(R.id.ph);
        rainfall = findViewById(R.id.rainfall);
        currLoc = findViewById(R.id.currentLocation);
        loading = findViewById(R.id.loadingPb);
        loading.setVisibility(View.INVISIBLE);
        getLoc = findViewById(R.id.btn_loc);
        getRec = findViewById(R.id.btn_getRec);
        exploreCrop = findViewById(R.id.exploreCrop);
        exploreCrop.setVisibility(View.INVISIBLE);
        recommendedCrop = findViewById(R.id.recommendedCrop);
        recommendedCrop.setVisibility(View.INVISIBLE);
        exploreCrop.setOnClickListener(view -> {
            if(recCrop != null){
                goToCrop(recCrop);
            }
        });
        getRec.setOnClickListener(view -> {
            loading.setVisibility(View.VISIBLE);
            if(checkAllFields()){
                getRecommendation();
            }

        });
        checkLoc();
        getLoc.setOnClickListener(view -> {
            getLoc();
            if(lon != 0 && lat != 0){
                Geocoder geocoder = new Geocoder(CropRecommendationActivity.this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(lat,lon,1);
                    if(addressList != null && addressList.size() > 0){
                        location = addressList.get(0).getSubAdminArea().split(" ")[0];
                        currLoc.setText(location);
                        isLocation = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            checkLoc();
        });


    }

    private void goToCrop(String recCrop) {
        String baseUrl = "http://192.168.1.4:5000/cropInformation/"+recCrop;
        JsonObjectRequest cropJsonRequest = new JsonObjectRequest(baseUrl, response->{

            try {
                Crop crop = new Crop(
                        response.getJSONObject("_id").get("$oid").toString(),
                        response.getString("name"),
                        response.getString("description"),
                        response.getString("usage"),
                        response.getString("propagation"),
                        response.getString("url")
                );
                Intent cropData = new Intent(CropRecommendationActivity.this, CropInfoActivity.class);
                cropData.putExtra("crop", crop);
                startActivity(cropData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

            Toast.makeText(this, "Something went wrong\nTry Again Later!!", Toast.LENGTH_SHORT).show();
        });
        Volley.newRequestQueue(this).add(cropJsonRequest);
    }

    private void getRecommendation() {
        int nitrogen = Integer.parseInt(String.valueOf(n.getText()));
        int phosphorous = Integer.parseInt(String.valueOf(p.getText()));
        int potassium = Integer.parseInt(String.valueOf(k.getText()));
        int phv = Integer.parseInt(String.valueOf(ph.getText()));
        int rain =Integer.parseInt(String.valueOf(rainfall.getText()));
        String city = location;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(
                        Request.Method.POST,
                url,
                new JSONObject(getParams(nitrogen,phosphorous,potassium,phv,rain,city)),
                response -> {
                    loading.setVisibility(View.INVISIBLE);
                    try{
                        recCrop = response.getString("crop");
                        Log.d(TAG, "onResponse: success "+ recCrop);
                        recommendedCrop.setVisibility(View.VISIBLE);
                        recommendedCrop.setText("It is recommended to grow \n"+recCrop.toUpperCase());
                        exploreCrop.setVisibility(View.VISIBLE);

                    }
                    catch (Exception e){
                        Log.d(TAG, "onResponse: exception: "+e.getMessage());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onErrorResponse: "+error);
                        Intent errorIntent = new Intent(CropRecommendationActivity.this,ErrorActivity.class);
                        errorIntent.putExtra("Error", "Connection Failed");
                        startActivity(errorIntent);

                    }
                }
                );
        requestQueue.add(stringRequest);



    }

    private Map getParams(int nitrogen, int phosphorous, int potassium, int phv, int rain, String city) {
        Map<String, String> params = new HashMap<>();
        params.put("nitrogen", String.valueOf(nitrogen));
        params.put("phosphorous", String.valueOf(phosphorous));
        params.put("pottasium", String.valueOf(potassium));
        params.put("ph", String.valueOf(phv));
        params.put("rainfall", String.valueOf(rain));
        params.put("city", city);
        return params;
    }



    private boolean checkAllFields() {
        boolean flag = true;
        if(n.getText().toString().trim().isEmpty()){
            n.setError("Please Fill this Field");
            flag = false;
        }
        if(p.getText().toString().trim().isEmpty()){
            p.setError("Please Fill this Field");
            flag = false;
        }
        if(k.getText().toString().trim().isEmpty()){
            k.setError("Please Fill this Field");
            flag = false;
        }
        if(ph.getText().toString().trim().isEmpty()){
            ph.setError("Please Fill this Field");
            flag = false;
        }
        if(rainfall.getText().toString().trim().isEmpty()){
            rainfall.setError("Please Fill this Field");
            flag = false;
        }
        return flag;
    }

    private void getLoc() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();

                    } else {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                });
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_ID);

    }

    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5);
        locationRequest.setFastestInterval(10);
        locationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            lon = lastLocation.getLongitude();
            lat = lastLocation.getLatitude();
        }
    };

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_ID){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLoc();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkPermission()) {
            getLoc();
        }
    }

    private void checkLoc(){
        getRec.setEnabled(isLocation);
    }
}