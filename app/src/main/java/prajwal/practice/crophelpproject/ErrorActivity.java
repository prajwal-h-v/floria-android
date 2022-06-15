package prajwal.practice.crophelpproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity {

    TextView errorMessageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        errorMessageView = findViewById(R.id.tv_error_message);
        errorMessageView.setText(getIntent().getStringExtra("Error"));
    }
}