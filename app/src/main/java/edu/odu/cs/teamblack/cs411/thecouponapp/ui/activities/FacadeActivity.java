package edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;

public class FacadeActivity extends AppCompatActivity {

    private int[] expectedSequence = {R.id.imageButton1, R.id.imageButton3, R.id.imageButton2, R.id.imageButton4};
    // Current position in the sequence of button presses.
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facade_activity);

        // Set up the store change button
        Button changeStoreButton = findViewById(R.id.buttonChangeStore);
        changeStoreButton.setOnClickListener(v -> showStoreSelectionDialog());

        // Consider what these ImageButtons are supposed to do.
        // If they're supposed to navigate to different parts of the app,
        // they should create an Intent for MainActivity with extras indicating which fragment to display.

        ImageButton btn1 = findViewById(R.id.imageButton1);
        ImageButton btn2 = findViewById(R.id.imageButton2);
        ImageButton btn3 = findViewById(R.id.imageButton3);
        ImageButton btn4 = findViewById(R.id.imageButton4);

        // Set up click listeners for image buttons.
        btn1.setOnClickListener(v -> handleButtonClick(R.id.imageButton1));
        btn2.setOnClickListener(v -> handleButtonClick(R.id.imageButton2));
        btn3.setOnClickListener(v -> handleButtonClick(R.id.imageButton3));
        btn4.setOnClickListener(v -> handleButtonClick(R.id.imageButton4));
    }

    private void handleButtonClick(int clickedButtonId) {
        // Check if the clicked button is the expected button in the sequence
        if (clickedButtonId == expectedSequence[currentPosition]) {
            currentPosition++; // Move to the next position in the sequence
            // Check if the user has completed the sequence
            if (currentPosition == expectedSequence.length) {
                // Navigate to MainActivity
                navigateToMain();
            }
        } else {
            // Reset the sequence if the user clicks the wrong button
            currentPosition = 0;
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(FacadeActivity.this, MainActivity.class);
        intent.putExtra("fragmentToLoad", "HomeFragment"); // Pass extra to indicate which fragment to load
        startActivity(intent);
        finish();
    }

    private void showStoreSelectionDialog() {
        final CharSequence[] stores = {
                "Walmart", "Sam's Club", "Family Dollar",
                "Kroger", "Safeway", "Publix",
                "Aldi", "Costco", "Target", "Trader Joe's", "Piggly Wiggly"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a store");
        builder.setItems(stores, (dialog, which) -> {
            TextView textViewStoreName = findViewById(R.id.textViewStoreName);
            textViewStoreName.setText(stores[which]);
            updateCouponImages(stores[which].toString());
        });
        builder.show();
    }

    private void updateCouponImages(String storeName) {
        ImageButton btn1 = findViewById(R.id.imageButton1);
        ImageButton btn2 = findViewById(R.id.imageButton2);
        ImageButton btn3 = findViewById(R.id.imageButton3);
        ImageButton btn4 = findViewById(R.id.imageButton4);

        switch (storeName) {
            case "Walmart":
                btn1.setImageResource(R.drawable.walmart_coupon);
                btn2.setImageResource(R.drawable.walmart_coupon2);
                btn3.setImageResource(R.drawable.walmart_coupon3);
                btn4.setImageResource(R.drawable.walmart_coupon4);
                break;
            case "Sam's Club":
                btn1.setImageResource(R.drawable.sams_coupon);
                btn2.setImageResource(R.drawable.sams_coupon);
                btn3.setImageResource(R.drawable.sams_coupon);
                btn4.setImageResource(R.drawable.sams_coupon);
                break;
            case "Family Dollar":
                btn1.setImageResource(R.drawable.familydollar_coupon);
                btn2.setImageResource(R.drawable.familydollar_coupon2);
                btn3.setImageResource(R.drawable.familydollar_coupon);
                btn4.setImageResource(R.drawable.familydollar_coupon2);
                break;
            case "Kroger":
                btn1.setImageResource(R.drawable.kroger_coupon);
                btn2.setImageResource(R.drawable.kroger_coupon);
                btn3.setImageResource(R.drawable.kroger_coupon);
                btn4.setImageResource(R.drawable.kroger_coupon);
                break;
            case "Safeway":
                btn1.setImageResource(R.drawable.grocery_coupon_6);
                btn2.setImageResource(R.drawable.grocery_coupon_7);
                btn3.setImageResource(R.drawable.grocery_coupon_8);
                btn4.setImageResource(R.drawable.grocery_coupon_6);
                break;
            case "Publix":
                btn1.setImageResource(R.drawable.publix_coupon);
                btn2.setImageResource(R.drawable.publix_coupon2);
                btn3.setImageResource(R.drawable.publix_coupon);
                btn4.setImageResource(R.drawable.publix_coupon2);
                break;
            case "Aldi":
                btn1.setImageResource(R.drawable.aldi_coupon);
                btn2.setImageResource(R.drawable.aldi_coupon2);
                btn3.setImageResource(R.drawable.aldi_coupon);
                btn4.setImageResource(R.drawable.aldi_coupon2);
                break;
            case "Costco":
                btn1.setImageResource(R.drawable.costco_coupon);
                btn2.setImageResource(R.drawable.costco_coupon2);
                btn3.setImageResource(R.drawable.costco_coupon);
                btn4.setImageResource(R.drawable.costco_coupon2);
                break;
            case "Target":
                btn1.setImageResource(R.drawable.target_coupon);
                btn2.setImageResource(R.drawable.target_coupon2);
                btn3.setImageResource(R.drawable.target_coupon3);
                btn4.setImageResource(R.drawable.target_coupon2);
                break;
            case "Trader Joe's":
                btn1.setImageResource(R.drawable.traderjoecoupon1);
                btn2.setImageResource(R.drawable.traderjoecoupon2);
                btn3.setImageResource(R.drawable.traderjoecoupon3);
                btn4.setImageResource(R.drawable.traderjoecoupon4);
                break;
            case "Piggly Wiggly":
                btn1.setImageResource(R.drawable.pigglycoupon1);
                btn2.setImageResource(R.drawable.pigglycoupon2);
                btn3.setImageResource(R.drawable.pigglycoupon3);
                btn4.setImageResource(R.drawable.pigglycoupon4);
                break;
        }
    }

}
