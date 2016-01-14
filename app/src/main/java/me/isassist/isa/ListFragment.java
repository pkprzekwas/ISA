package me.isassist.isa;

import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import java.util.ArrayList;
import java.util.Hashtable;

public class ListFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private Location mLocation;
    private ArrayList<Hashtable<String, String>> mData;
    private ProgressBar mProgressBar;
    private ListView mListView;
    private Bihapi mAPI;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getParcelable("LOCATION");
            mAPI = (Bihapi) savedInstanceState.getSerializable("API");
        }

        if (getArguments() != null) {
            MainActivity activity = (MainActivity) getActivity();
            mLocation = activity.mLastLocation;
            mAPI = (Bihapi) getArguments().getSerializable("API_TYPE");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("LOCATION", mLocation);
        savedInstanceState.putSerializable("API", mAPI);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Method called from DownloadFromFile class to refresh the results
     * @param data API data sorted by distance from "current" location
     */
    public void refresh(ArrayList<Hashtable<String, String>> data)
    {
        Log.i(TAG, "refresh()");
        try {
            mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
            mListView = (ListView) getView().findViewById(R.id.listView);
        }
        catch (NullPointerException ex)
        {
            return;
        }
        mData = data;

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            mListView.setAdapter(new ItemsListAdapter(getActivity(), mLocation, data, mAPI));
            mListView.setVisibility(ListView.VISIBLE);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    Hashtable<String, String> item = mData.get(position);
                    intent.putExtra("DATA", item);
                    intent.putExtra("LAT", mLocation.getLatitude());
                    intent.putExtra("LON", mLocation.getLongitude());
                    intent.putExtra("API_TYPE", mAPI);
                    startActivity(intent);
                }
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_list, container, false);

        if (mLocation != null)
            new DownloadFromFile(getActivity(), this, mLocation, mAPI.name()).execute();

        getActivity().setTitle(mAPI.toString());

        // Inflate the layout for this fragment
        return v;
    }
}
