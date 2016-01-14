package me.isassist.isa;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

public class JSONParser {

    /**
     * Method parses JSON String from API into ArrayList of Hashtables containing key-value
     * characteristics of items
     *
     * @param jsonStr String with JSON raw data
     * @return ArrayList of Hashtables with parsed data
     * @throws JSONException
     */
    public static ArrayList<Hashtable<String, String>> getDataFromJSON(String jsonStr)
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
}
