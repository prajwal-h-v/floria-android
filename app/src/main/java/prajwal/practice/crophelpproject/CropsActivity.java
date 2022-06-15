package prajwal.practice.crophelpproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import prajwal.practice.crophelpproject.utils.Crop;
import prajwal.practice.crophelpproject.utils.CropAdapter;

public class CropsActivity extends AppCompatActivity {

    private static final String TAG = "customLogging";
    GridView cropsGV;
    ArrayList<Crop> cropArrayList;
    ProgressDialog progressDialog;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crops);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        cropsGV = findViewById(R.id.cropsGV);
        status= findViewById(R.id.crops_status);
        cropArrayList = new ArrayList<>();
        makeRequest();

        cropsGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Crop crop = cropArrayList.get(i);
                Intent copInfo = new Intent(CropsActivity.this, CropInfoActivity.class);
                copInfo.putExtra("crop", crop);
                startActivity(copInfo);
            }
        });

    }

    private void makeRequest() {
        String url = "https://floria-app.herokuapp.com/api/all-crops";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            int len = response.length();
            for(int i =0; i < len; i++){
                try {
                    JSONObject jsonObject = response.getJSONObject(i);

                    Crop crop = new Crop(
                            jsonObject.getJSONObject("_id").get("$oid").toString(),
                            jsonObject.getString("name"),
                            jsonObject.getString("description"),
                            jsonObject.getString("usage"),
                            jsonObject.getString("propagation"),
                            jsonObject.getString("url")
                    );
                    cropArrayList.add(crop);
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.cancel();
                }

            }
            CropAdapter cropAdapter = new CropAdapter(this, cropArrayList);
            cropsGV.setAdapter(cropAdapter);
            if(cropArrayList.size()==0){
                status.setText("No Crop Data available at this time.");
            }
            else{
                status.setText("Found "+cropArrayList.size()+ " crops");
            }
            progressDialog.cancel();


        }, error -> {
            Log.d(TAG, "makeRequest: error: "+error);
            progressDialog.cancel();
        });
        jsonArrayRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }


}