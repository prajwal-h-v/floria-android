package prajwal.practice.crophelpproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class LogoActivity extends AppCompatActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        new Handler().postDelayed(() -> {


            Intent intent = new Intent(LogoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();


        }, 3000);
    }
}