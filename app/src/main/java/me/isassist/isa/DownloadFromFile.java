package me.isassist.isa;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

/**
 * Class downloads data from file in separated thread to improve application performance.
 * Created by Patryk on 2016-01-04.
 */
public class DownloadFromFile extends AsyncTask<String,Void,ArrayList<Hashtable<String, String>>> {

    private String APIname;
    private Context mContext;
    private final String TAG = this.getClass().getSimpleName();
    private ListFragment mFragment;
    private Location mLocation;

    public DownloadFromFile(Context context, ListFragment fragment, Location location, String APIname){
        this.APIname = APIname;
        this.mContext = context;
        this.mFragment = fragment;
        this.mLocation = location;
    }

    /**
     * Sorts data by distance to our location.
     * @param data API data for current tested API
     * @return Sorted by distance API data
     */
    private ArrayList<Hashtable<String, String>> sortByDistance(ArrayList<Hashtable<String, String>> data){

        Collections.sort(data, new Comparator<Hashtable<String,String>>() {
            @Override
            public int compare(Hashtable<String,String> data1, Hashtable<String,String> data2) {
                Location firstLoc = new Location("");
                Location secondLoc = new Location("");

                firstLoc.setLatitude(Double.parseDouble(data1.get("lat")));
                firstLoc.setLongitude(Double.parseDouble(data1.get("lon")));

                secondLoc.setLatitude(Double.parseDouble(data2.get("lat")));
                secondLoc.setLongitude(Double.parseDouble(data2.get("lon")));

                float firstDistance = firstLoc.distanceTo(mLocation);
                float secondDistance = secondLoc.distanceTo(mLocation);
                return Double.compare(firstDistance, secondDistance);
            }
        });

        return data;
    }

    @Override
    protected ArrayList<Hashtable<String, String>> doInBackground(String... params) {

        ArrayList<Hashtable<String, String>> data = null;

        // reading data from file
        try {
            FileInputStream fileInputStream = mContext.openFileInput(APIname);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            data = (ArrayList<Hashtable<String, String>>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        // sorting data by location
        data = sortByDistance(data);

        return data;
    }

    protected void onPostExecute(ArrayList<Hashtable<String, String>> result) {
        if (result != null) {
            // call a method in fragment, which will display results of the fetching on screen
            mFragment.refresh(result);
        }
    }
}
