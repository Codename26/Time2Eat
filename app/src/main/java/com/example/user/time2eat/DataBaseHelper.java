package com.example.user.time2eat;


import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper{
    private FoursquareItemDatabase mDb;
    private FoursquareItem mItem;
    private List<FoursquareItem> mItems;

    public DataBaseHelper(Context context){
        mDb = FoursquareItemDatabase.getFoursquareItemDatabase(context);
    }

    public interface FetchItemsFromDBCallback{
        void itemsLoadedCallBack();
    }
    private FetchItemsFromDBCallback mFetchItemsFromDBCallback;

    public void registerCallback(FetchItemsFromDBCallback fetchItemsFromDBCallback){
        mFetchItemsFromDBCallback = fetchItemsFromDBCallback;
    }

    public static void addItemAsync(FoursquareItemDatabase db, FoursquareItem item){
        AddItemAsyncTask task = new AddItemAsyncTask(db, item);
        task.execute();
    }

    public void addItems(List<FoursquareItem> items) {
        AddItemsAsyncTask task = new AddItemsAsyncTask(mDb, items);
        task.execute();
    }

    public void orderItems(){
        mItems = new ArrayList<>();
        GetItemsAsyncTask task = new GetItemsAsyncTask(mDb);
        task.execute();

    }

    public List<FoursquareItem> getItems(){
        return mItems;
    }

    private class GetItemsAsyncTask extends AsyncTask<Void, Void, List<FoursquareItem>> {
        private FoursquareItemDatabase db;
        public GetItemsAsyncTask(FoursquareItemDatabase db) {
            this.db = db;
        }
        @Override
        protected List<FoursquareItem> doInBackground(Void... voids) {
            return db.foursquareItemDao().getAll();
        }

        @Override
        protected void onPostExecute(List<FoursquareItem> items) {
            mItems = items;
            if (mFetchItemsFromDBCallback != null){
                mFetchItemsFromDBCallback.itemsLoadedCallBack();
            }
        }
    }

    private static class AddItemAsyncTask extends AsyncTask<Void, Void, Void> {
        private FoursquareItemDatabase db;
        private FoursquareItem item;
        public AddItemAsyncTask(FoursquareItemDatabase db, FoursquareItem item) {
            this.db = db;
            this.item = item;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (db.foursquareItemDao().getById(item.getId()) == null) {
                    db.foursquareItemDao().insert(item);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class AddItemsAsyncTask extends AsyncTask<Void, Void, Void> {
        private FoursquareItemDatabase db;
        private List<FoursquareItem> items;
        public AddItemsAsyncTask(FoursquareItemDatabase db, List<FoursquareItem> items) {
            this.db = db;
            this.items = items;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            for (FoursquareItem item : items) {
                try {
                    if (db.foursquareItemDao().getById(item.getId()) == null) {
                        db.foursquareItemDao().insert(item);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
