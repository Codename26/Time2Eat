package com.example.user.time2eat;

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
    private String ll = "40.7484,-73.9857";
    private List<FoursquareItem> foursquareItems;
    private int limit = 20;
    private int radius = 750;
    private LatLng mLastKnownLocation;

    public FoursquareLoader(LatLng latLng){
        mLastKnownLocation = latLng;
        ll = stringFromLatLng(mLastKnownLocation);
        Log.i("ll", ll);
    }

    public interface MessagesApi {

        @GET("search?v=20171229")
        Call<String> searchVenues(@Query("client_id") String clientID,
                                  @Query("client_secret") String clientSecret,
                                  @Query("radius") int radius,
                                  @Query("categoryId") String categoryId,
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
        Call<String> messages = messagesApi.searchVenues(CLIENT_ID, CLIENT_SECRET, radius, CATEGORY_ID, ll);
        messages.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "response " + response);
                    parseFoursquare(response.body().toString());

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
    public void parseFoursquare(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("response")) {
                if (jsonObject.getJSONObject("response").has("venues")) {
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("venues");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject contact = (JSONObject) jsonArray.getJSONObject(i).getJSONObject("contact");
                        JSONObject location = (JSONObject) jsonArray.getJSONObject(i).getJSONObject("location");
                        System.out.println(jsonArray.getJSONObject(i));
                        FoursquareItem item = new FoursquareItem();
                        item.setId(jsonArray.getJSONObject(i).getString("id"));
                        item.setName(jsonArray.getJSONObject(i).getString("name"));
                        if (location.has("address")) {
                            item.setAddress(location.getString("address"));
                        }
                        if (location.has("lat")){
                            item.setLatitude(location.getDouble("lat"));
                        }
                        if (location.has("lng")){
                            item.setLongitude(location.getDouble("lng"));
                        }
                        if (contact.has("phone")) {
                            item.setPhone(contact.getString("phone"));
                        }
                        if (location.has("distance")) {
                            item.setDistance(location.getInt("distance"));
                        }
                        foursquareItems.add(item);
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
}
