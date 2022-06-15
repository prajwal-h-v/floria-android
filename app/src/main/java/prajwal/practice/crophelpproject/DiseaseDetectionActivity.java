package prajwal.practice.crophelpproject;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import prajwal.practice.crophelpproject.utils.Disease;
import prajwal.practice.crophelpproject.utils.VolleyMultiPartRequest;


public class DiseaseDetectionActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "customLogging";
    private static final String URL = "https://floria-app.herokuapp.com/api/disease-prediction";
    private static final int REQUEST_PERMISSION = 100;
    public static final int PICK_IMAGE_REQUEST = 2;
    TextView label_crop, label_disease, label_cause, label_cure;
    TextView crop_name, crop_disease, crop_cause, crop_cure, title;
    ProgressDialog progressDialog;
    Bitmap imageBitmap;
    ImageView imageView;
    Button takePicture, selectPicture,submit;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_detection);
//        Initializing app components
        initApp();
        checkImage();

//        Onclick listener for image selection
        selectPicture.setOnClickListener(view->{
            resetComponents();
            dispatchImageChooser();
        });

//        Onclick listener for launching camera
        takePicture.setOnClickListener(view -> {
            resetComponents();
            dispatchTakePictureIntent();
        });

//        Onclick listener for uploading image to server
        submit.setOnClickListener(view -> {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            uploadImage(imageBitmap);
        });



    }

//    function to initialize all UI components
    private void initApp() {
        imageView = findViewById(R.id.dd_image);
        takePicture = findViewById(R.id.dd_captureImage);
        submit = findViewById(R.id.dd_submit);
        selectPicture = findViewById(R.id.dd_selectImage);
        progressDialog = new ProgressDialog(DiseaseDetectionActivity.this);
        label_crop = findViewById(R.id.label_crop);
        label_disease = findViewById(R.id.label_disease);
        label_cause = findViewById(R.id.label_cause);
        label_cure = findViewById(R.id.label_cure);
        crop_name = findViewById(R.id.crop_name);
        crop_disease = findViewById(R.id.crop_disease);
        crop_cause = findViewById(R.id.crop_cause);
        crop_cure = findViewById(R.id.crop_cure);
        title = findViewById(R.id.dd_title);
        resetComponents();
    }

//    checking if image is available to upload
    private void checkImage(){
        submit.setEnabled(imageBitmap != null);
    }

//    launching image chooser
    private void dispatchImageChooser() {
        Intent imageChoose = new Intent();
        imageChoose.setType("image/*");
        imageChoose.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imageChoose, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void resetComponents(){
        imageBitmap = null;
        imageView.setImageBitmap(null);
        checkImage();
        crop_name.setText(null);
        crop_disease.setText(null);
        crop_cause.setText(null);
        crop_cure.setText(null);
        label_crop.setVisibility(View.GONE);
        label_disease.setVisibility(View.GONE);
        label_cause.setVisibility(View.GONE);
        label_cure.setVisibility(View.GONE);
        title.setText("Please Upload Your Image");


    }

//    launching camera
    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        catch (ActivityNotFoundException e){
            Log.d(TAG, "dispatchTakePictureIntent: "+e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            checkImage();
            imageView.setImageBitmap(imageBitmap);

        }
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            Uri selectedImageURI = data.getData();
            if(selectedImageURI != null){
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageURI);
                    checkImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(imageBitmap);
            }
        }
    }

//    uploading image to server
    private void uploadImage(Bitmap bitmap){
        if(bitmap != null){
            VolleyMultiPartRequest volleyMultiPartRequest = new VolleyMultiPartRequest(
                    Request.Method.POST,
                    URL,
                    response -> {
                        progressDialog.cancel();
                        try{
                            JSONObject jsonObject = new JSONObject(new String(response.data));

                            Log.d(TAG, "uploadImage: success => "+jsonObject.get("message"));
                            JSONObject d_json = jsonObject.getJSONObject("message");

                            Disease disease = new Disease(
                                    d_json.getString("disease_name").trim(),
                                    d_json.getString("crop").trim(),
                                    d_json.getString("name").trim(),
                                    d_json.getString("cause").trim(),
                                    d_json.getString("cure").trim()
                            );
                            displayDisease(disease);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "uploadImage: error => "+e);
                        }

                    }, error -> {
                        progressDialog.cancel();
                        Log.d(TAG, "uploadImage: error "+error);
                        TimeoutError t = new TimeoutError();
                        if(error.getClass().equals(t.getClass())){
                            title.setText("Timed out, please try again!!");
                        }
                    }){
                

                @Override
                protected Map<String, DataPart> getByteData() throws AuthFailureError {
                    Map<String, DataPart> params = new HashMap<>();
                    long imageName = System.currentTimeMillis();
                    params.put("image", new DataPart(imageName+".png", getFileDataFromDrawable(bitmap)));
                    return params;
                }
            };
            volleyMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(this).add(volleyMultiPartRequest);
        }
        else{
            Intent errorIntent = new Intent(DiseaseDetectionActivity.this, ErrorActivity.class);
            errorIntent.putExtra("Error","No Image Selected!!!");
            startActivity(errorIntent);
        }
    }

    private void displayDisease(Disease disease) {
        title.setText("Here is the result");
        label_crop.setVisibility(View.VISIBLE);
        crop_name.setText(disease.getCropName());
        if(!disease.getDiseaseName().equals("Could not detect crop")) {
            crop_name.setTextColor(getColor(R.color.basic_green_light));
            label_disease.setVisibility(View.VISIBLE);
            crop_disease.setText(disease.getName());
        }
        else{
            crop_name.setTextColor(getColor(R.color.color_danger));
        }
        if(!disease.getCauses().isEmpty()){
            label_cause.setVisibility(View.VISIBLE);
            String causes = disease.getCauses();
            causes = causes.replace(". ", ".\n\n");
            crop_cause.setText(causes);
        }
        else{
            label_cause.setVisibility(View.GONE);
        }
        if(!disease.getCures().isEmpty()){
            label_cure.setVisibility(View.VISIBLE);
            String cures = disease.getCures();
            cures = cures.replace(". ", ".\n\n");
            crop_cure.setText(cures);
        }
        else{
            label_cure.setVisibility(View.GONE);
        }

    }

    //    helper function to get data from image
    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}