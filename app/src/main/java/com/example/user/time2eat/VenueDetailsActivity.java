package com.example.user.time2eat;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class VenueDetailsActivity extends AppCompatActivity implements DataBaseHelper.UpdateItemCallback,
DataBaseHelper.GetItemCallback {

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
    private DataBaseHelper mHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);
        Intent intent = getIntent();
        if (intent.hasExtra(MapsActivity.VENUE_DETAILS)) {
            mItem = intent.getParcelableExtra(MapsActivity.VENUE_DETAILS);
        }
        if (mItem != null) {
            loadItem(mItem);
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
        if (mItem.getPhone() != null) {
            tvPhone.setText(mItem.getPhone());
        } else tvPhone.setText(R.string.phone_null);
        if (mItem.getAddress() != null) {
            tvAddress.setText(mItem.getAddress());
        } else tvAddress.setText(R.string.address_null);
        if (mItem.getPrice() != null) {
            String price = "";
            if (mItem.getPrice().equals("1")) {
                price = getString(R.string.price_tier_1);
            } else if (mItem.getPrice().equals("2")) {
                price = getString(R.string.price_tier_2);
            } else if (mItem.getPrice().equals("3")) {
                price = getString(R.string.price_tier_3);
            }
            tvPriceTier.setText(price);
        } else tvPriceTier.setText(R.string.price_null);
        tvRating.setText(String.valueOf(mItem.getRating()));
        setRatingColor(tvRating, mItem.getRating());
        tvUserRating.setText(String.valueOf(mItem.getUserRating()) + "/10");
        etName.setText(mItem.getUserName());
        mRatingBar.setRating(mItem.getUserRating() /2);

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

                saveItem(mItem);
            }
        });


    }

    private void saveItem(FoursquareItem mItem) {
        mHelper = new DataBaseHelper(this);
        mHelper.registerUpdateItemCallback(this);
        mHelper.updateItem(mItem);
    }

    private void loadItem(FoursquareItem item){
        mHelper = new DataBaseHelper(this);
        mHelper.registerGetItemCallback(this);
        mHelper.getItemAsync(item);
    }


    @Override
    public void itemUpdatedCallback() {
        finish();
    }

    @Override
    public void itemGotCallback() {
        FoursquareItem item = mHelper.getItem();
        if (item != null) {
            initInterface(item);
        } else initInterface(mItem);
    }

    public void setRatingColor(View view, double ratingValue){
        int color;
        if (ratingValue >= 8.0){
            color = ContextCompat.getColor(getApplicationContext(), R.color.colorGreenRating);
        } else if (ratingValue >= 7.0 && ratingValue < 8.0){
            color = ContextCompat.getColor(getApplicationContext(), R.color.colorYellowRating);
        } else if (ratingValue >= 6.0 && ratingValue < 7.0){
            color = ContextCompat.getColor(getApplicationContext(), R.color.colorAmberRating);
        }else if (ratingValue >= 5.0 && ratingValue < 6.0){
            color = ContextCompat.getColor(getApplicationContext(), R.color.colorOrangeRating);
        }else if (ratingValue == 0.0){
            color = ContextCompat.getColor(getApplicationContext(), R.color.colorZeroRating);
        } else {
            color = ContextCompat.getColor(getApplicationContext(), R.color.colorRedRating);
        }
        view.setBackgroundColor(color);
    }
}
