package me.isassist.isa;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private final double MOCK_LOCATION_X = 21.035;
    private final double MOCK_LOCATION_Y = 52.249;
    //private final int MOCK_LOCATION_DIAMETER = 2000;

    private Location mLocation;

    private ArrayList<Hashtable<String, String>> mData;

    private ProgressBar mProgressBar;
    private ListView mListView;

    private Bihapi mAPI;
    private ImageView mInstruction;


    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstruction = (ImageView) getActivity().findViewById(R.id.instruction);
        mInstruction.setVisibility(View.INVISIBLE);

        Log.i(TAG, "onCreate()");
        if (getArguments() != null) {
            mLocation = new Location("");
            /**
            mLocation.setLongitude(getArguments().getDouble("LONGITUDE"));
            mLocation.setLatitude(getArguments().getDouble("LATITUDE"));
             **/
            mLocation.setLatitude(MOCK_LOCATION_Y);
            mLocation.setLongitude(MOCK_LOCATION_X);
            mAPI = (Bihapi) getArguments().getSerializable("API_TYPE");
        }
    }

    /**
     * Method called from DownloadFromFile class to refresh the results
     * @param data API data sorted by distance from "current" location
     */
    public void refresh(ArrayList<Hashtable<String, String>> data)
    {
        Log.i(TAG, "refresh()");
        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        mListView = (ListView) getView().findViewById(R.id.listView);
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

        new DownloadFromFile(getActivity(), this, mLocation, mAPI.name()).execute();
        //TODO: sortowanie po odleglosci obiektow od mLocation

        // Inflate the layout for this fragment
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
