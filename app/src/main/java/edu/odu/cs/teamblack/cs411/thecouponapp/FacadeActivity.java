package edu.odu.cs.teamblack.cs411.thecouponapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class FacadeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facade);

        ImageButton btn = findViewById(R.id.imageButton);

        btn.setOnClickListener(v -> startActivity(new Intent(FacadeActivity.this, HomeActivity.class)));

        btn = findViewById(R.id.imageButton2);
        btn.setOnClickListener(v -> startActivity(new Intent(FacadeActivity.this, HomeActivity.class)));

        btn = findViewById(R.id.imageButton3);
        btn.setOnClickListener(v -> startActivity(new Intent(FacadeActivity.this, HomeActivity.class)));
    }
}