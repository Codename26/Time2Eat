package com.example.user.time2eat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class VenueDetailsActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvPhone;
    private TextView tvAddress;
    private TextView tvPriceTier;
    private TextView tvRating;
    private TextView tvUserRating;
    private EditText etName;
    private FoursquareItem mItem;
    private RatingBar mRatingBar;
    private Button btnRate;
    private int mUserRating = 0;


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

    private void initInterface(final FoursquareItem mItem) {

        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvPriceTier = findViewById(R.id.tvPriceTier);
        tvRating = findViewById(R.id.tvRating);
        tvUserRating = findViewById(R.id.tvUserRating);
        mRatingBar = findViewById(R.id.ratingBar);
        btnRate = findViewById(R.id.btnRate);
        etName = findViewById(R.id.editText);

        tvName.setText(mItem.getName());
        tvPhone.setText(mItem.getPhone());
        tvAddress.setText(mItem.getAddress());
        tvPriceTier.setText(mItem.getPrice());
        tvRating.setText(String.valueOf(mItem.getRating()));

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mUserRating = Math.round(v*2);
                tvUserRating.setText(String.valueOf(mUserRating) + "/10");
            }
        });

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUserRating > 0) {
                    mItem.setUserRating(mUserRating);
                }
                if (!etName.getText().equals("")) {
                    mItem.setUserName(String.valueOf(etName.getText()));
                }
                Intent intent = new Intent();
                intent.putExtra(MapsActivity.VENUE_DETAILS, mItem);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }




}
