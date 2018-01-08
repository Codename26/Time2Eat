package com.example.user.time2eat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class VenueDetailsActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvPhone;
    private TextView tvAddress;
    private TextView tvPriceTier;
    private TextView tvRating;
    private FoursquareItem mItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(MapsActivity.VENUE_DETAILS)) {
            mItem = intent.getParcelableExtra(MapsActivity.VENUE_DETAILS);
            setContentView(R.layout.activity_venue_details);
            initInterface(mItem);
        }

    }

    private void initInterface(FoursquareItem mItem) {
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvPriceTier = findViewById(R.id.tvPriceTier);
        tvRating = findViewById(R.id.tvRating);

        tvName.setText(mItem.getName());
        tvPhone.setText(mItem.getPhone());
        tvAddress.setText(mItem.getAddress());
        tvPriceTier.setText(mItem.getPrice());
        tvRating.setText(String.valueOf(mItem.getRating()));
    }
}
