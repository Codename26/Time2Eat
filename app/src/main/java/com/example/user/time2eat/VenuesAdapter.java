package com.example.user.time2eat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Codename26 on 01.01.2018.
 */

public class VenuesAdapter extends RecyclerView.Adapter<VenuesViewholder>{
    private List<FoursquareItem> items;


    public VenuesAdapter(List<FoursquareItem> fItems){
        items = fItems;
    }

    @Override
    public VenuesViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.venue_item, parent, false);
        return new VenuesViewholder(v);
    }

    @Override
    public void onBindViewHolder(VenuesViewholder holder, int position) {
      final FoursquareItem item = items.get(position);
        holder.setName(item.getName());
        holder.setAddress(item.getAddress());
        holder.setDistance(item.getDistance());
        holder.setRating(item.getRating());
        holder.setRatingColor(holder, item.getRating());

        if (mItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(item);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (items == null){
            return 0;
        }
        return items.size();
    }

    private ItemClickListener mItemClickListener;

    public void setItemClickListener(ItemClickListener listener){
        mItemClickListener = listener;
    }

    public interface ItemClickListener{
        void onItemClick(FoursquareItem item);
    }
}

class VenuesViewholder extends RecyclerView.ViewHolder{
    private Context mContext;
    private TextView name;
    private TextView address;
    private TextView distance;
    private TextView rating;

    public String getName() {
        return name.getText().toString();
    }

    public void setName(String sName) {
        name.setText(sName);
    }

    public String getAddress() {
        return address.getText().toString();
    }

    public void setAddress(String address) {
        this.address.setText(address);
    }

    public String getDistance() {
        return distance.getText().toString();
    }

    public void setDistance(int distance) {
        this.distance.setText(String.valueOf(distance) + "м.");
    }

    public String getRating() {
        return rating.getText().toString();
    }

    public void setRating(double rating) {
        this.rating.setText(String.valueOf(rating));
    }

    public void setRatingColor(VenuesViewholder holder, double ratingValue){
        int color;
            if (ratingValue >= 8.0){
            color = ContextCompat.getColor(mContext, R.color.colorGreenRating);
        } else if (ratingValue >= 7.0 && ratingValue < 8.0){
                color = ContextCompat.getColor(mContext, R.color.colorYellowRating);
            } else if (ratingValue >= 6.0 && ratingValue < 7.0){
                color = ContextCompat.getColor(mContext, R.color.colorAmberRating);
            }else if (ratingValue >= 5.0 && ratingValue < 6.0){
                color = ContextCompat.getColor(mContext, R.color.colorOrangeRating);
            }else if (ratingValue == 0.0){
                color = ContextCompat.getColor(mContext, R.color.colorZeroRating);
            } else {
                color = ContextCompat.getColor(mContext, R.color.colorRedRating);
            }
        holder.rating.setBackgroundColor(color);
    }

    public VenuesViewholder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.tvName);
        address = itemView.findViewById(R.id.tvAddress);
        distance = itemView.findViewById(R.id.tvDistance);
        rating = itemView.findViewById(R.id.tvRating);
        mContext = itemView.getContext();

    }


}