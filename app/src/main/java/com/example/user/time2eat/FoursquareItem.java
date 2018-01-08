package com.example.user.time2eat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Codename26 on 31.12.2017.
 */

public class FoursquareItem implements Parcelable {
    private String id;
    private String name;
    private String phone;
    private String address;
    private double rating;
    private int distance;
    private String price;
    private double latitude;
    private double longitude;
    private boolean best = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isBest() {
        return best;
    }

    public void setBest(boolean best) {
        this.best = best;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.address);
        dest.writeDouble(this.rating);
        dest.writeInt(this.distance);
        dest.writeString(this.price);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeByte(this.best ? (byte) 1 : (byte) 0);
    }

    public FoursquareItem() {
    }

    protected FoursquareItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.address = in.readString();
        this.rating = in.readDouble();
        this.distance = in.readInt();
        this.price = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.best = in.readByte() != 0;
    }

    public static final Creator<FoursquareItem> CREATOR = new Creator<FoursquareItem>() {
        @Override
        public FoursquareItem createFromParcel(Parcel source) {
            return new FoursquareItem(source);
        }

        @Override
        public FoursquareItem[] newArray(int size) {
            return new FoursquareItem[size];
        }
    };
}
