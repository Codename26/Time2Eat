package com.example.user.time2eat;


import android.os.AsyncTask;

public class DataBaseHelper{
    private FoursquareItemDatabase mDb;
    private FoursquareItem mItem;

    public DataBaseHelper(){
    }

    public static void addItemAsync(FoursquareItemDatabase db, FoursquareItem item){
        AddItemAsyncTask task = new AddItemAsyncTask(db, item);
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
            db.foursquareItemDao().insert(item);
            return null;
        }
    }
}
