package me.isassist.isa;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Hashtable;

public class DetailActivity extends AppCompatActivity {

    private HashMap<String, String> mData;
    private Location mLocation;
    private Bihapi mAPIType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
        Button navigateButton = (Button) findViewById(R.id.navigateButton);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = mData.get("lat");
                String lon = mData.get("lon");
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + "&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });**/

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("DATA")) {
            mData = (HashMap<String, String>) intent.getSerializableExtra("DATA");
            mLocation = new Location("");
            mLocation.setLongitude(intent.getDoubleExtra("LON", 0));
            mLocation.setLatitude(intent.getDoubleExtra("LAT", 0));
            mAPIType = (Bihapi) intent.getSerializableExtra("API_TYPE");
        }
        setContentView(R.layout.activity_detail);
        switch (mAPIType) {
            case CITY_OFFICES:
                break;
            case CASH_MACHINES:
                break;
            case DORMITORIES:
                break;
            case PHARMACIES:
                break;
            case HOTELS:
                break;
            case POLICE_OFFICES:
                break;
            case SPORT_FIELDS:
                break;
            case SWIMMING_POOLS:
                break;
            case VETURILO:
                break;
            case THEATRES:
                break;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
