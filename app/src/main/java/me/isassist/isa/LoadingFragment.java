package me.isassist.isa;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class LoadingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_loading, container, false);
        TextView loadingText = (TextView) view.findViewById(R.id.loadingText);
        String text = getArguments().getString("LOADING_TEXT");
        if (text != null)
            loadingText.setText(text);
        return view;
    }

}
