package edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;

public class FacadeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facade);

        // Consider what these ImageButtons are supposed to do.
        // If they're supposed to navigate to different parts of the app,
        // they should create an Intent for MainActivity with extras indicating which fragment to display.

        ImageButton btn = findViewById(R.id.imageButton1);
        btn.setOnClickListener(v -> navigateToMain());

        ImageButton btn2 = findViewById(R.id.imageButton2);
        btn2.setOnClickListener(v -> navigateToMain());

        ImageButton btn3 = findViewById(R.id.imageButton3);
        btn3.setOnClickListener(v -> navigateToMain());

        ImageButton btn4 = findViewById(R.id.imageButton4);
        btn4.setOnClickListener(v -> navigateToMain());
    }

    private void navigateToMain() {
        Intent intent = new Intent(FacadeActivity.this, MainActivity.class);
        intent.putExtra("fragmentToLoad", "HomeFragment"); // Pass extra to indicate which fragment to load
        startActivity(intent);
        finish();
    }
}
