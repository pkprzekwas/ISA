package me.isassist.isa;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MainFragment extends Fragment
{
    private final String TAG = this.getClass().getSimpleName();

    //swipe gesture detection
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.app_name));
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        //Log.i(TAG, "ACTION_DOWN");
                        x1 = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //Log.i(TAG, "ACTION_UP");
                        x2 = event.getX();
                        float deltaX = x2 - x1;
                        if (deltaX > MIN_DISTANCE)
                        {
                            DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                            drawer.openDrawer(GravityCompat.START);
                            return false;
                        }
                        break;
                }
                return false;
            }
        });
        return view;
    }


}
