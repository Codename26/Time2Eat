package com.example.user.time2eat;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Update;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Codename26 on 31.12.2017.
 */

public class FoursquareLoader {

    private static final String TAG = "MainActivity";
    private static final String CLIENT_ID = "2RSD0BHKRAOU043MHKNMALCCN4PTJP42GWRCW1PKMXOUKOK2";
    private static final String CLIENT_SECRET = "IWJY41WDLP3H43HE5AW0CTFLRFNPRUZEVF3C5HQYQMIMJSNA";
    private static final String CATEGORY_ID = "4d4b7105d754a06374d81259";
    private static final String SECTION = "food";
    private String ll;
    private List<FoursquareItem> foursquareItems;
    private int mRadius = 500;
    private Context mContext;

    public FoursquareLoader(LatLng latLng, int radius, Context context){

        ll = stringFromLatLng(latLng);
        mRadius = radius;
        Log.i("ll", ll);
        mContext = context;
    }

    public interface MessagesApi {

        @GET("explore?v=20171229")
        Call<String> searchVenues(@Query("client_id") String clientID,
                                  @Query("client_secret") String clientSecret,
                                  @Query("radius") int radius,
                                  @Query("section") String section,
                                  @Query("ll") String ll);

    }
   public interface FetchItemsCallback{
        void itemsFetchedCallBack();
    }

    public void retrofitCreator(){
        foursquareItems = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.foursquare.com/v2/venues/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MessagesApi messagesApi = retrofit.create(MessagesApi.class);
        Call<String> messages = messagesApi.searchVenues(CLIENT_ID, CLIENT_SECRET, mRadius, SECTION, ll);
        messages.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "response " + response);
                    parseFoursquareExplore(response.body().toString());

                    Log.i(TAG, "response " + response.body());
                } else {
                    Log.i(TAG, "response code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void parseFoursquareExplore(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("response")) {
                if (jsonObject.getJSONObject("response").has("groups")) {
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("groups");
                    jsonObject = jsonArray.getJSONObject(0);
                    jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        FoursquareItem item = new FoursquareItem();
                        if (jsonArray.getJSONObject(i).has("venue")) {
                            jsonObject = jsonArray.getJSONObject(i).getJSONObject("venue");
                            if (jsonObject.has("contact")) {
                                JSONObject contact = (JSONObject) jsonObject.getJSONObject("contact");
                                if (contact.has("phone")) {
                                    item.setPhone(contact.getString("phone"));
                                }
                            }
                            if (jsonObject.has("location")) {
                                JSONObject location = (JSONObject) jsonObject.getJSONObject("location");
                                if (location.has("address")) {
                                    item.setAddress(location.getString("address"));
                                }
                                if (location.has("lat")) {
                                    item.setLatitude(location.getDouble("lat"));
                                }
                                if (location.has("lng")) {
                                    item.setLongitude(location.getDouble("lng"));
                                }

                                if (location.has("distance")) {
                                    item.setDistance(location.getInt("distance"));
                                }
                            }
                            item.setId(jsonObject.getString("id"));
                            item.setName(jsonObject.getString("name"));

                            if (jsonObject.has("rating")){
                                item.setRating(jsonObject.getDouble("rating"));
                            }
                            foursquareItems.add(item);
                        }
                    }
                }
            }
            if (mFetchItemsCallback != null) {
                mFetchItemsCallback.itemsFetchedCallBack();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    FetchItemsCallback mFetchItemsCallback;
    void registerCallback(FetchItemsCallback fetchItemsCallback){
        mFetchItemsCallback = fetchItemsCallback;
    }

    public List<FoursquareItem> getVenues(){
        sortVenues();
        findBestVenue();
        return foursquareItems;
    }

    public String stringFromLatLng(LatLng latLng) {
        return (String.valueOf(latLng.latitude).replaceAll(",",".")
                + "," + String.valueOf(latLng.longitude).replaceAll(",","."));
    }

    private void sortVenues(){
        FoursquareItem temp;
        for (int i = foursquareItems.size()-1; i > 0 ; i--) {
            for (int j = 0; j < i; j++) {
                if (foursquareItems.get(j).getDistance() > foursquareItems.get(j+1).getDistance()){
                    temp = foursquareItems.get(j);
                    foursquareItems.set(j, foursquareItems.get(j+1));
                    foursquareItems.set(j+1, temp);
                }

            }

        }
    }
    private void findBestVenue(){
        int maxIndex = 0;
        double max = 0;
        boolean hasRating = false;

        for (int i = 0; i < foursquareItems.size(); i++) {
            if (foursquareItems.get(i).getRating() > 0){
                maxIndex = i;
                max = foursquareItems.get(i).getRating();
                hasRating = true;
            }
        }
        if (!hasRating){
            return;
        }
        List maxIndexes = new ArrayList();

        for (int i = 0; i < foursquareItems.size(); i++) {
            if (foursquareItems.get(i).getRating() > max) {
                maxIndex = i;
                max = foursquareItems.get(i).getRating();
            }
        }
            for (int i = 0; i < foursquareItems.size(); i++) {
            if (i == maxIndex){
                continue;
            }
              if (foursquareItems.get(i).getRating() == max) {
                  maxIndexes.add(i);
              }
            }

        foursquareItems.get(maxIndex).setBest(true);
        if (maxIndexes.size() != 0) {
            for (int i = 0; i < maxIndexes.size(); i++) {
                foursquareItems.get(i).setBest(true);
            }
        }
    }

   new DataBaseHelper.addItemAsync()


}
