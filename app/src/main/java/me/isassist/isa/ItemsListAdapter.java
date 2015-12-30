package me.isassist.isa;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by juzer on 30-Dec-15.
 */
public class ItemsListAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<Hashtable<String, String>> mData;
    private static LayoutInflater mInflater =null;
    private Location mLocation;

    public ItemsListAdapter(Activity activity, Location loc, ArrayList<Hashtable<String, String>> data) {
        mActivity = activity;
        mData = data;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLocation = loc;
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

        // Setting all values in listview
        name.setText(item.get("OPIS"));
        address.setText(item.get("ULICA") + " " + item.get("NUMER"));

        Location loc = new Location("");
        loc.setLatitude(Double.parseDouble(item.get("lat")));
        loc.setLongitude(Double.parseDouble(item.get("lon")));

        float calculatedDistance = loc.distanceTo(mLocation);
        if (calculatedDistance < 1000.0)
        {
            calculatedDistance = Math.round(calculatedDistance);
            distance.setText("Distance: " + calculatedDistance + " m");
        }
        else
        {
            calculatedDistance = calculatedDistance/1000f;
            calculatedDistance = Math.round(calculatedDistance);
            distance.setText("Distance: " + calculatedDistance + " km");

        }
        return vi;
    }
}
