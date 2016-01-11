package me.isassist.isa;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.net.ssl.HttpsURLConnection;


/**
 * Class that fetches JSON data from the API and parses it.
 * TODO: better handling of exceptions
 * TODO: situations when nothing was found in the given area
 * TODO: situations with API error that needs retry
 */
class FetchAPI extends AsyncTask<String, Void, ArrayList<Hashtable<String, String>>>
{
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private ArrayList<Hashtable<String, String>> mResult;
    private ListFragment mFragment;
    private Bihapi mAPI;
    private FetchType mFetchType;
    MainActivity mActivity;

    public enum FetchType
    {
        INTERNET, FILE_RESOURCES
    }

    /**
     *
     * @param c context
     */
    public FetchAPI(Context c, MainActivity m, Bihapi API, FetchType type)
    {
        //mFragment = fragment;
        mActivity = m;
        mContext = c;
        mAPI = API;
        mFetchType = type;
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
        Log.d(TAG, "Rozpoczynam pobieranie API " + mAPI.name());

        ArrayList<Hashtable<String, String>> returnList = null;

        // Will contain the raw JSON response as a string.
        String JSONString = null;

        if (mFetchType == FetchType.INTERNET) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;

            Authenticator.setDefault(new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Constants.API_LOGIN, Constants.API_PASSWORD.toCharArray());
                }
            });

            Uri.Builder uriBuilder = Uri.parse(mAPI.getURL()).buildUpon();

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
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

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
            } finally {
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
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
                return null;
            }
        }
        else if (mFetchType == FetchType.FILE_RESOURCES)
        {
            InputStream inputStream = null;
            switch (mAPI) {
                case CITY_OFFICES:
                    inputStream = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.city_offices));
                    break;
                case CASH_MACHINES:
                    inputStream = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.cash_machines));
                    break;
                case DORMITORIES:
                    inputStream = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.dormitories));
                    break;
                case PHARMACIES:
                    inputStream = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.pharmacies));
                    break;
                case HOTELS:
                    inputStream = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.hotels));
                    break;
                case POLICE_OFFICES:
                    inputStream = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.police_offices));
                    break;
                case SPORT_FIELDS:
                    inputStream = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.sport_fields));
                    break;
                case SWIMMING_POOLS:
                    inputStream = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.swimming_pools));
                    break;
                case VETURILO:
                    inputStream = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.veturilo));
                    break;
                case THEATRES:
                    inputStream = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.theatres));
                    break;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
            JSONString = buffer.toString();
            try {
                returnList = getDataFromJSON(JSONString);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
                return null;
            }
        }

        Log.d(TAG, "Rozpoczynam zapisywanie do pliku " + mAPI.name());

        try {
            FileOutputStream fileOutputStream = mContext.openFileOutput(mAPI.name(), Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(returnList);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, "Zapisano " + mAPI.name());
        mResult = returnList;
        return returnList;
    }

    /**
     * Simple method which compares to strings
     * and checks if saved file was the last one.
     * @param name string with api name
     * @return true if last activity - false otherwise
     */
    private boolean checkIfLast(String name){
        if(name.equals("THEATRES"))
            return true;
        else
            return false;
    }

    /**
     * Calls a method in fragment, passes data fetched from API as argument
     * @param result
     */
    @Override
    protected void onPostExecute(ArrayList<Hashtable<String, String>> result) {
        boolean isLast = checkIfLast(mAPI.name());
        if (isLast)
        {
            mActivity.showMainFragment();
        }
        if (result == null)
        {
            Toast.makeText(mContext, "Update of " + mAPI.toString() + " failed!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(mContext, mAPI.toString() + " updated!", Toast.LENGTH_SHORT).show();
        }
    }
}
