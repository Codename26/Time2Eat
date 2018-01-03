package com.example.user.time2eat;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.List;

public class VenuesListActivity extends AppCompatActivity implements FoursquareLoader.FetchItemsCallback{
    private RecyclerView mRecyclerView;
    private FoursquareLoader foursquareLoader;
    private LinearLayoutManager linearLayoutManager;
    private Location mLastKnownLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues_list);
        Intent intent = getIntent();
        if (intent.hasExtra(MapsActivity.LAST_KNOWN_LOCATION)){
            mLastKnownLocation = intent.getParcelableExtra(MapsActivity.LAST_KNOWN_LOCATION);
        }
        initRecyclerView();
        foursquareLoader = new FoursquareLoader(mLastKnownLocation);
        foursquareLoader.registerCallback(this);
        foursquareLoader.retrofitCreator();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.venues_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
    }


    @Override
    public void itemsFetchedCallBack() {
        List<FoursquareItem> items = foursquareLoader.getVenues();
        VenuesAdapter adapter = new VenuesAdapter(items);
        mRecyclerView.setAdapter(adapter);
    }
}
