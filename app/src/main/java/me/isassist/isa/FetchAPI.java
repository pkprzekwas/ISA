package me.isassist.isa;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.net.ssl.HttpsURLConnection;


/**
 * Class that fetches JSON data from the API and parses it.
 */
class FetchAPI extends AsyncTask<String, Void, ArrayList<Hashtable<String, String>>>
{
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private double mX, mY;
    private int mDiameter;
    private int mMaxFeatures;
    private int mAPITypeResourceID;
    private ArrayList<Hashtable<String, String>> mResult;
    private ListFragment mFragment;

    /**
     *
     * @param c context
     * @param APITypeResourceID contains String resource with part of the URL containing chosen API
     * @param x first geographic coordinate
     * @param y second geographic coordinate
     * @param diameter the diameter of the circle of search
     * @param result ArrayList of Hashtable which will contain findings of the search in key - value pairs
     */
    public FetchAPI(Context c, int APITypeResourceID, double x, double y, int diameter, ArrayList<Hashtable<String, String>> result, ListFragment fragment)
    {
        mContext = c;
        mX = x;
        mY = y;
        mDiameter = diameter;
        mMaxFeatures = 0;
        mAPITypeResourceID = APITypeResourceID;
        mResult = result;
        mFragment = fragment;
    }

    /**
     *
     * @param c context
     * @param APITypeResourceID contains String resource with part of the URL containing chosen API
     * @param x first geographic coordinate
     * @param y second geographic coordinate
     * @param diameter the diameter of the circle of search
     * @param maxFeatures max number of returned findings
     * @param result ArrayList of Hashtable which will contain findings of the search in key - value pairs
     */
    public FetchAPI(Context c, int APITypeResourceID, double x, double y, int diameter, int maxFeatures, ArrayList<Hashtable<String, String>> result)
    {
        mContext = c;
        mX = x;
        mY = y;
        mDiameter = diameter;
        mMaxFeatures = maxFeatures;
        mAPITypeResourceID = APITypeResourceID;
        mResult = result;
    }

    /**
     * Method parses JSON String from API into ArrayList of Hashtables containing key-value
     * characteristics of items
     *
     * @param jsonStr String with JSON raw data
     * @return ArrayList of Hashtables with parsed data
     * @throws JSONException
     */
    private ArrayList<Hashtable<String, String>> getDataFromJSON(String jsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String API_DATA = "data";
        final String API_GEOMETRY = "geometry";
        final String API_COORDINATES = "coordinates";
        final String API_LAT = "lat";
        final String API_LON = "lon";
        final String API_PROPERTIES = "properties";
        final String API_KEY = "key";
        final String API_VALUE = "value";

        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray itemsArray = jsonObject.getJSONArray(API_DATA);
        ArrayList<Hashtable<String, String>> itemsList = new ArrayList<Hashtable<String, String>>();

        for(int i = 0; i < itemsArray.length(); i++)
        {
            // Get the JSON object representing the single item
            JSONObject itemObject = itemsArray.getJSONObject(i);
            Hashtable<String, String> itemHashtable = new Hashtable<>();

            // Get the JSON data containing geometry and coordinates
            JSONObject geometryObject = itemObject.getJSONObject(API_GEOMETRY);
            JSONObject coordinatesObject = geometryObject.getJSONObject(API_COORDINATES);

            itemHashtable.put(API_LAT, coordinatesObject.getString(API_LAT));
            itemHashtable.put(API_LON, coordinatesObject.getString(API_LON));

            JSONArray itemPropertiesArray = itemObject.getJSONArray(API_PROPERTIES);

            // Get the key-value pairs of characteristics of a given item
            for (int j = 0; j < itemPropertiesArray.length(); j++)
            {
                JSONObject propertyObject = itemPropertiesArray.getJSONObject(j);
                itemHashtable.put(propertyObject.getString(API_KEY), propertyObject.getString(API_VALUE));
            }
            itemsList.add(itemHashtable);
        }
        return itemsList;
    }

    /**
     * Method connects to the Internet, downloads data from API and calls a method to parse the data
     * @param params none
     * @return parsed data in ArrayList of Hashtables
     */
    @Override
    protected ArrayList<Hashtable<String, String>> doInBackground(String... params)
    {
        ArrayList<Hashtable<String, String>> returnList = null;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String JSONString = null;

        Authenticator.setDefault(new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Constants.API_LOGIN, Constants.API_PASSWORD.toCharArray());
            }
        });

        Uri.Builder uriBuilder = Uri.parse(mContext.getString(R.string.api_base_url)+mContext.getString(mAPITypeResourceID))
                .buildUpon()
                .appendQueryParameter("circle", mX + "," + mY + "," + mDiameter);

        if (mMaxFeatures > 0)
            uriBuilder.appendQueryParameter("maxFeatures", Integer.toString(mMaxFeatures));

        Uri uri = uriBuilder.build();


        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast

            URL url = new URL(uri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(new CustomCACert(mContext, R.raw.ca_tp_der).getSSLSocketFactory());
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {buffer.append(line);}

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            JSONString = buffer.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            // If the code didn't successfully get the JSON data
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        try {
            returnList = getDataFromJSON(JSONString);
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }
        mResult = returnList;
        return returnList;
    }

    @Override
    protected void onPostExecute(ArrayList<Hashtable<String, String>> result) {
        if (result != null)
        {
            mFragment.refresh(result);
        }
    }
}