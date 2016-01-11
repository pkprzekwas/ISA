package me.isassist.isa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private final String TAG = this.getClass().getSimpleName();

    GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    protected LocationRequest mLocationRequest;

    /**
     * When this variable is not null it means a fragment with a certain list is open
     * This variable is used to restore this fragment after restart of activity
     * (for example screen rotation)
     */
    private Bihapi mListType = null;

    public void showMainFragment()
    {
        //show the main fragment with instruction picture
        Fragment mainFragment = new MainFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, mainFragment).commit();
    }

    public void showLoadingFragment(String text)
    {
        //show the main fragment with instruction picture
        Bundle args = new Bundle();
        args.putString("LOADING_TEXT", text);
        Fragment loadingFragment = new LoadingFragment();
        loadingFragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, loadingFragment).commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.list_row_name);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        // nie ruszać - nie wiem czemu ale sprawia problemy
        final LocationManager manager = (LocationManager) getSystemService( this.LOCATION_SERVICE );

        // checking internet connection
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        mLocationRequest = createLocationRequest();

        //restoring activity (restart)
        if (savedInstanceState != null)
        {
            mLastLocation = savedInstanceState.getParcelable("LOCATION");
            mListType = (Bihapi) savedInstanceState.getSerializable("LIST_TYPE");

            //we need to launch a previous fragment
            if (mListType != null) {
                Fragment fragment = new ListFragment();
                Bundle args = new Bundle();

                if (mLastLocation == null) {
                    Toast.makeText(this, "Waiting for the location...", Toast.LENGTH_LONG).show();
                } else {
                    args.putSerializable("API_TYPE", mListType);
                    fragment.setArguments(args);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            }
        }
        else {

            boolean allFilesExist = true;
            for (Bihapi b : Bihapi.values()) {
                if (fileExists(b)) {
                } else {
                    allFilesExist = false;
                }
            }

            if (allFilesExist) {
                showMainFragment();
            } else {
                showLoadingFragment("Loading data\n Please wait!");
                for (Bihapi b : Bihapi.values()) {
                    if (!fileExists(b)) {
                        new FetchAPI(this, this, b, FetchAPI.FetchType.FILE_RESOURCES).execute();
                    }
                }
            }
        }
        Log.i(TAG, "SCIEZKA: " + getFilesDir().getAbsolutePath());
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putParcelable("LOCATION", mLastLocation);
        if (mListType != null)
            savedInstanceState.putSerializable("LIST_TYPE", mListType);
        super.onSaveInstanceState(savedInstanceState);
    }

        /**
         * In case of lack of GPS conecction method sends a prompt to turn it on.
         */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean fileExists(Bihapi APIType){
        File file = getBaseContext().getFileStreamPath(APIType.name());
        return file.exists();
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
            if (getFragmentManager().getBackStackEntryCount() > 0 ){
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
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

        if (id == R.id.action_refresh) {
            if (!isNetworkAvailable())
            {
                Toast.makeText(this, "No internet connection available!", Toast.LENGTH_LONG).show();
                return true;
            }
            showLoadingFragment("Updating\nThis can take a while\n Please wait!");
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }

            for(Bihapi b: Bihapi.values())
                new FetchAPI(this, this, b, FetchAPI.FetchType.INTERNET).execute();
            return true;
        }
        else if (id == R.id.action_about){
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.about_dialog);
            dialog.setTitle("About");

            TextView text = (TextView) dialog.findViewById(R.id.licensesText);
            text.setText(Html.fromHtml("Building, Tall, Trading, Human graphics by <a href=\"http://www.freepik.com/\">Freepik</a> and Building graphic by <a href=\"http://www.unocha.org\">Ocha</a> and Haw Gestures Stroke graphic by <a href=\"http://yanlu.de\">Yannick</a> from <a href=\"http://www.flaticon.com/\">Flaticon</a> are licensed under <a href=\"http://creativecommons.org/licenses/by/3.0/\" title=\"Creative Commons BY 3.0\">CC BY 3.0</a>.Park graphic by <a href=\"http://www.freepik.com\">Freepik</a> from <a href=\"http://www.flaticon.com/\">Flaticon</a> is licensed under <a href=\"http://creativecommons.org/licenses/by/3.0/\" title=\"Creative Commons BY 3.0\">CC BY 3.0</a>. Made with <a href=\"http://logomakr.com\" title=\"Logo Maker\">Logo Maker</a>"));

            dialog.show();
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

        if (mLastLocation == null)
        {
            Toast.makeText(this, "Waiting for the location...", Toast.LENGTH_LONG).show();
            return false;
        }


        switch (id)
        {
            case R.id.nav_atms:
            {
                args.putSerializable("API_TYPE", Bihapi.CASH_MACHINES);
                mListType = Bihapi.CASH_MACHINES;
                break;
            }
            case R.id.nav_bikes:
            {
                args.putSerializable("API_TYPE", Bihapi.VETURILO);
                mListType = Bihapi.VETURILO;
                break;
            }
            case R.id.nav_city_offices:
            {
                args.putSerializable("API_TYPE", Bihapi.CITY_OFFICES);
                mListType = Bihapi.CITY_OFFICES;
                break;
            }
            case R.id.nav_dormitories:
            {
                args.putSerializable("API_TYPE", Bihapi.DORMITORIES);
                mListType = Bihapi.DORMITORIES;
                break;
            }
            case R.id.nav_hotels:
            {
                args.putSerializable("API_TYPE", Bihapi.HOTELS);
                mListType = Bihapi.HOTELS;
                break;
            }
            case R.id.nav_pharmacies:
            {
                args.putSerializable("API_TYPE", Bihapi.PHARMACIES);
                mListType = Bihapi.PHARMACIES;
                break;
            }
            case R.id.nav_sport_fields:
            {
                args.putSerializable("API_TYPE", Bihapi.SPORT_FIELDS);
                mListType = Bihapi.SPORT_FIELDS;
                break;
            }
            case R.id.nav_swimming:
            {
                args.putSerializable("API_TYPE", Bihapi.SWIMMING_POOLS);
                mListType = Bihapi.SWIMMING_POOLS;
                break;
            }
            case R.id.nav_police_offices:
            {
                args.putSerializable("API_TYPE", Bihapi.POLICE_OFFICES);
                mListType = Bihapi.POLICE_OFFICES;
                break;
            }
            case R.id.nav_theaters:
            {
                args.putSerializable("API_TYPE", Bihapi.THEATRES);
                mListType = Bihapi.THEATRES;
                break;
            }
        }
        fragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
        catch (SecurityException ex)
        {
            //wystepuje w marshmallow, gdy skurwesyn odmowil podania lokalizacji
        }
        if (mLastLocation != null) {
            String loc = new String();
            loc += "lat:  " + mLastLocation.getLatitude();
            loc += ", lon: " + mLastLocation.getLongitude();
            //Toast.makeText(this, loc, Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "No location detected!!!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handless getting last locations
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

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        //Toast.makeText(this, "Location updated!", Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(200);
        locationRequest.setFastestInterval(200);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
