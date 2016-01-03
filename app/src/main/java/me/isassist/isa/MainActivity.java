package me.isassist.isa;

import android.app.FragmentManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * TODO: situation when location cannot be accessed or is outside warsaw
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ListFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    private final String TAG = this.getClass().getSimpleName();
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.list_row_name);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        for(Bihapi b: Bihapi.values()){
            if(b == Bihapi.CASH_MACHINES) continue;
            new FetchAPI(this, b).execute();
        }

    }
    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = new ListFragment();
        Bundle args = new Bundle();

        args.putDouble("LATITUDE", mLastLocation.getLatitude());
        args.putDouble("LONGITUDE", mLastLocation.getLongitude());

        switch (id)
        {
            case R.id.nav_atms:
            {
                args.putSerializable("API_TYPE", Bihapi.CASH_MACHINES);
                break;
            }
            case R.id.nav_bikes:
            {
                args.putSerializable("API_TYPE", Bihapi.VETURILO);
                break;
            }
            case R.id.nav_city_offices:
            {
                args.putSerializable("API_TYPE", Bihapi.CITY_OFFICES);
                break;
            }
            case R.id.nav_dormitories:
            {
                args.putSerializable("API_TYPE", Bihapi.DORMITORIES);
                break;
            }
            case R.id.nav_hotels:
            {
                args.putSerializable("API_TYPE", Bihapi.HOTELS);
                break;
            }
            case R.id.nav_pharmacies:
            {
                args.putSerializable("API_TYPE", Bihapi.PHARMACIES);
                break;
            }
            case R.id.nav_sport_fields:
            {
                args.putSerializable("API_TYPE", Bihapi.SPORT_FIELDS);
                break;
            }
            case R.id.nav_swimming:
            {
                args.putSerializable("API_TYPE", Bihapi.SWIMMING_POOLS);
                break;
            }
            case R.id.nav_police_offices:
            {
                args.putSerializable("API_TYPE", Bihapi.POLICE_OFFICES);
                break;
            }
        }
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method needed by interface for interaction with fragment
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Handles getting last locations
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle)
    {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        catch (SecurityException ex)
        {
            //wystepuje w marshmallow, gdy skurwesyn odmowil podania lokalizacji
        }
        if (mLastLocation != null) {
            String loc = new String();
            loc += "lat:  " + mLastLocation.getLatitude();
            loc += ", lon: " + mLastLocation.getLongitude();
            Toast.makeText(this, loc, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No location detected!!!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handles getting last locations
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Handles getting last locations
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }
}
