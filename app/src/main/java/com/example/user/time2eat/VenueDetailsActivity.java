package com.example.user.time2eat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class VenueDetailsActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvPhone;
    private TextView tvAddress;
    private TextView tvPriceTier;
    private TextView tvRating;
    private FoursquareItem mItem;

    Button btnRating1;
    Button btnRating2;
    Button btnRating3;
    Button btnRating4;
    Button btnRating5;
    Button btnRating6;
    Button btnRating7;
    Button btnRating8;
    Button btnRating9;
    Button btnRating10;


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
        initRatingButtons();
    }

    private void initRatingButtons() {
        btnRating1 = findViewById(R.id.ivButtonStar1);
        btnRating2 = findViewById(R.id.ivButtonStar2);
        btnRating3 = findViewById(R.id.ivButtonStar3);
        btnRating4 = findViewById(R.id.ivButtonStar4);
        btnRating5 = findViewById(R.id.ivButtonStar5);
        btnRating6 = findViewById(R.id.ivButtonStar6);
        btnRating7 = findViewById(R.id.ivButtonStar7);
        btnRating8 = findViewById(R.id.ivButtonStar8);
        btnRating9 = findViewById(R.id.ivButtonStar9);
        btnRating10 = findViewById(R.id.ivButtonStar10);
    }

    public void buttonRatingListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               switch (view.getId()){
                   case R.id.ivButtonStar1:
                       btnRating1.setBackgroundResource(R.drawable.ic_star_on);
                       break;
               }
            }
        });
    }
}
