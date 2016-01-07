package me.isassist.isa;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Adapter for ListView in ListFragment.
 * Handles showing a single row of list
 */
public class ItemsListAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<Hashtable<String, String>> mData;
    private static LayoutInflater mInflater =null;
    private Location mLocation;
    private Bihapi mAPIType;

    public ItemsListAdapter(Activity activity, Location loc, ArrayList<Hashtable<String, String>> data, Bihapi api) {
        mActivity = activity;
        mData = data;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLocation = loc;
        mAPIType = api;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if(convertView == null)
            vi = mInflater.inflate(R.layout.list_row, null);

        TextView name = (TextView)vi.findViewById(R.id.list_row_name); // title
        TextView address = (TextView)vi.findViewById(R.id.list_row_address); // artist name
        TextView distance = (TextView)vi.findViewById(R.id.list_row_distance); // duration

        Hashtable<String, String> item = mData.get(position);

        switch (mAPIType)
        {
            case CASH_MACHINES:
                if (item.get("WWW_BANKU").equals("http://www.euronetpolska.pl"))
                    name.setText("Euronet");

                address.setText(item.get("ULICA") + " " + item.get("NUMER"));
                break;
            case VETURILO:
                name.setText(item.get("LOKALIZACJA"));
                address.setText("Station ID: " + item.get("NR_STACJI"));
                //address.setVisibility(TextView.INVISIBLE);
                break;
            case HOTELS:
                name.setText(Html.fromHtml(item.get("OPIS")));
                address.setText(item.get("ULICA") + " " + item.get("NUMER"));
                break;
            default:
                name.setText(Html.fromHtml(item.get("OPIS")));
                address.setText(item.get("ULICA") + " " + item.get("NUMER"));
        }

        Location loc = new Location("");
        loc.setLatitude(Double.parseDouble(item.get("lat")));
        loc.setLongitude(Double.parseDouble(item.get("lon")));

        float calculatedDistance = loc.distanceTo(mLocation);
        if (calculatedDistance < 1000.0)
        {
            calculatedDistance = Math.round(calculatedDistance);
            distance.setText(String.format("Distance: %d m", (int)calculatedDistance));
        }
        else
        {
            calculatedDistance = calculatedDistance/1000f;
            //calculatedDistance = Math.round(calculatedDistance);
            distance.setText(String.format("Distance: %.1f km", calculatedDistance));

        }
        return vi;
    }
}
