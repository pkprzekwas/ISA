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
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
    private final int MOCK_LOCATION_DIAMETER = 2000;

    private Location mLocation;

    private ArrayList<Hashtable<String, String>> mData;

    private ProgressBar mProgressBar;
    private ListView mListView;

    private Bihapi mAPI;

    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
     * Method called from FetchAPI class to refresh the results
     * @param data
     */
    public void refresh(ArrayList<Hashtable<String, String>> data)
    {
        Log.i(TAG, "refresh()");
        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        mListView = (ListView) getView().findViewById(R.id.listView);
        mData = data;

        /*
        try {
            FileInputStream fileInputStream = getActivity().openFileInput("hotels");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            mData = (ArrayList<Hashtable<String, String>>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        */

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mListView.setAdapter(new ItemsListAdapter(getActivity(), mLocation, data));
        mListView.setVisibility(ListView.VISIBLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Hashtable<String, String> item = mData.get(position);
                intent.putExtra("DATA", item);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ArrayList<Hashtable<String, String>> list = null;
        //TODO: wyjebac ta liste w argumentach
        //new FetchAPI(getActivity(), Bihapi.PHARMACIES, this).execute();

        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        mListView = (ListView) v.findViewById(R.id.listView);

        try {
            FileInputStream fileInputStream = getActivity().openFileInput(mAPI.name());
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            mData = (ArrayList<Hashtable<String, String>>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        //TODO: sortowanie po odleglosci obiektow od mLocation
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mListView.setAdapter(new ItemsListAdapter(getActivity(), mLocation, mData));
        mListView.setVisibility(ListView.VISIBLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Hashtable<String, String> item = mData.get(position);
                intent.putExtra("DATA", item);
                startActivity(intent);
            }
        });

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
