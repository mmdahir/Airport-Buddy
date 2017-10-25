package group11.tcss450.uw.edu.airportbuddy.activities;

import android.Manifest;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.sql.Time;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.dashboardFragments.AirportFragment;
import group11.tcss450.uw.edu.airportbuddy.dashboardFragments.AirportInputFragment;
import group11.tcss450.uw.edu.airportbuddy.dashboardFragments.BoardingFragment;
import group11.tcss450.uw.edu.airportbuddy.dashboardFragments.BoardingInputFragment;
import group11.tcss450.uw.edu.airportbuddy.dashboardFragments.TSAFragment;
import group11.tcss450.uw.edu.airportbuddy.dialogs.LogoutDialogFragment;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnDashboardInteractionListener;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnSavedFlightInteractionListener;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnTimePickerListener;
import group11.tcss450.uw.edu.airportbuddy.menu.Flight;
import group11.tcss450.uw.edu.airportbuddy.dashboardFragments.SavedFlightsFragment;
import group11.tcss450.uw.edu.airportbuddy.dashboardFragments.UserPreferencesFragment;
import group11.tcss450.uw.edu.airportbuddy.services.UpdateDashService;

/**
 * Activity to display the dash view.
 */
public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnDashboardInteractionListener,
        OnSavedFlightInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        UpdateDashService.onServiceInteractionListener,
        OnTimePickerListener {

    /**
     * Reference to the boarding fragment
     */
    private BoardingFragment boardingFragment;

    /**
     * Reference to airport fragment
     */
    private AirportFragment airportFragment;

    /**
     * Reference to the airport input fragment
     */
    private AirportInputFragment mAirportInputFragment;

    /**
     * Reference to the tsa fragment
     */
    private TSAFragment mTSAFragment;

    /**
     * Reference to the boarding input fragment
     */
    private BoardingInputFragment mBoardingInputFragment;

    /**
     * Used to status messages
     */
    public String TAG = "from Dash Activity: ";

    /**
     * Service used to update dashboard fragments
     */
    private UpdateDashService mService;

    /**
     * If the service has been binded yet
     */
    private boolean mBound = false;

    /**
     * If the service has been attached
     */
    private boolean mServiceAttached = false;

    /**
     * The Username of the current logged-in user.
     */
    private String mUsername;

    /**
     * Client to let us use Google Services
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will
     never be more frequent * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Permissions location
     */
    private static final int MY_PERMISSIONS_LOCATIONS = 814;

    /**
     * Helps get the location
     */
    private LocationRequest mLocationRequest;

    /**
     * Current location of User
     */
    private Location mCurrentLocation;

    /**
     * Setting up and initializing everything when activity is starting up.
     * @param savedInstanceState Will get used if activity is being re-initialized
     *                           after previously being shut down.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setUpLocationServices();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        mUsername = intent.getStringExtra(StartActivity.EXTRA_MESSAGE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Log.i(TAG, "onCreate: " + navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.username_text_view);
        nav_user.setText(mUsername);

        airportFragment = (AirportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.airport_fragment);
        boardingFragment = (BoardingFragment) getSupportFragmentManager()
                .findFragmentById(R.id.boarding_fragment);
        mTSAFragment = (TSAFragment) getSupportFragmentManager()
                .findFragmentById(R.id.tsa_fragment);
        mAirportInputFragment =
                (AirportInputFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.airport_input_fragment);
        mBoardingInputFragment = (BoardingInputFragment) getSupportFragmentManager()
                .findFragmentById(R.id.boarding_input_fragment);
    }

    /**
     * Sets up location services so that we may get the user's current lcoation,
     * also requests for permissions if it has not been given
     */
    private void setUpLocationServices() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_LOCATIONS);
        }
    }

    /**
     * Called when user pressed the back key.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Creattes an Options Menu to access the settings button.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    /**
     * Deals with what happens if an options item is selected.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.nav_preferences) {
//            Log.i(TAG, "onOptionsItemSelected: CLICKED!!");
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Deals with what happens when the Preferences tab is selected in the Navigation bar.
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_preferences) {
            Log.i(TAG, "onNavigationItemSelected: CLICKED!!");
            UserPreferencesFragment userPreferencesFragment;
            Bundle args = new Bundle();
            args.putString("Username", mUsername);
            userPreferencesFragment = new UserPreferencesFragment();
            userPreferencesFragment.setArguments(args);
            FragmentTransaction transaction;
            transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dash_container, userPreferencesFragment)
                    .addToBackStack(null);
            transaction.commit();

        } else if (id == R.id.saved_flights) {
            SavedFlightsFragment savedFlights;
            savedFlights = new SavedFlightsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dash_container, savedFlights)
                    .addToBackStack(null).commit();
        } else if (id == R.id.nav_logout) {
            DialogFragment fragment = new LogoutDialogFragment();
            if (fragment != null)
                fragment.show(getFragmentManager(), "launch");
        } else if (id == R.id.dashboard) {
            getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Code for what happens when an interaction happens between the fragments and the activity
     * @param switchCase the type of interaction
     * @param gate the gate number of the user
     * @param boarding the boarding time for when the user is able to board the airplane
     * @param airportCode the code for the airport that the user is at
     * @param airportETA the estimated time for when the user will arrive at the airport from their
     *                   current location
     * @param earliness how early the user wants to be at the airport
     */
    @Override
    public void OnInteractionListener(String switchCase, String gate, String boarding,
                                      String airportCode, String airportETA, String earliness) {
        switch(switchCase) {
            case "Airport Fragment Visibility":
                FrameLayout fl = (FrameLayout) findViewById(R.id.airport_input_layout);
                if (fl.getVisibility() == View.GONE) {
                    Log.d("airportinput", "show");
                    fl.setVisibility(View.VISIBLE);
                    fl.startAnimation(AnimationUtils.loadAnimation(this, R.anim.enter_left));
                } else {
                    fl.setVisibility(View.GONE);
                    fl.startAnimation(AnimationUtils.loadAnimation(this, R.anim.exit_right));
                    Log.d("airportinput", "hide");
                }
                break;
            case "Airport ETA":
                Log.i(TAG, "OnInteractionListener: " + airportETA);
                airportFragment.setETA(airportETA);
                mTSAFragment.setAirportCode(airportCode);
                mService.setOnServiceInteractionListener(this);
                mServiceAttached = true;
                UpdateDashService.setServiceAlarm(this, true);
                break;
            case "Airport Code":
                airportFragment.setDisplay(airportCode);
                break;
            case "Boarding Fragment Visibility":
                fl = (FrameLayout) findViewById(R.id.boarding_input_layout);
                if (fl.getVisibility() == View.GONE) {
                    Log.d("boardinginput", "show");
                    fl.setVisibility(View.VISIBLE);
                    fl.startAnimation(AnimationUtils.loadAnimation(this, R.anim.enter_left));
                } else {
                    fl.setVisibility(View.GONE);
                    fl.startAnimation(AnimationUtils.loadAnimation(this, R.anim.exit_right));
                    Log.d("boardinginput", "hide");
                }
                break;
            case "Boarding Input":
                boardingFragment.setBoardingTextView(boarding);
                boardingFragment.setGateTextView(gate);
                break;
            case "Updated preference":
                boardingFragment.setEarliness(earliness);
                break;
        }
    }

    /**
     * Flight is being loaded to the dash.
     *
     * @param flight flight to load to dash
     */
    @Override
    public void onSavedFlightInteraction(Flight flight) {
        ((TextView) findViewById(R.id.airport_autocomplete)).setText(flight.getLocationFrom());
        ((Button) findViewById(R.id.airport_submit_button)).performClick();

        ((TextView) findViewById(R.id.gate_text)).setText(String.valueOf(flight.getGateNumber()));
        Time time = flight.getBoarding();
        int hour = time.getHours();
        int minute = time.getMinutes();
        String ampm = "am";
        if (hour > 12) {
            hour -= 12;
            ampm = "pm";
        }
        ((TextView) findViewById(R.id.boarding_text)).setText(hour + ":" + minute + " " + ampm);

        findViewById(R.id.boarding_submit_button).performClick();
    }

    /**
     * Result of the permissions requested. Lets us know if we can use the location service to get
     * their current location
     * @param requestCode request code
     * @param permissions permission that was requested
     * @param grantResults if the user granted us permission or not
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // locations-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Locations need to be working for this portion,"
                            + "please provide permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is aLocationListener
        //(http://developer.android.com/reference/com/google/
        //          android/gms/location/Loca tionListener.html).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/
        //          android/gms/location/LocationListener.html).
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    /**
     * Disconnects the google api client and unbind's and stops the service
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            if (mServiceAttached)
                UpdateDashService.setServiceAlarm(this, false);
        }
    }

    /**
     * Connects the google api client, and bind's the service that updates the dashboard
     */
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, UpdateDashService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Stops the service when the app is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mBound && mServiceAttached)
            UpdateDashService.setServiceAlarm(this, false);
    }

    /**
     * Restarts the service when the app is returned to focus
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mBound && mServiceAttached)
            UpdateDashService.setServiceAlarm(this, true);
    }

    /**
     * Disconnects the google api client and stops the service
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        // Unbind from the service
        if (mBound && mServiceAttached) {
            UpdateDashService.setServiceAlarm(this, false);
        }
    }

    /**
     * Restarts the service when the app is back
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (mBound && mServiceAttached)
            UpdateDashService.setServiceAlarm(this, true);
    }

    /**
     * What happens when the google api client connects
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        //do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mCurrentLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mCurrentLocation != null) {
                    String currentLocation = mCurrentLocation.getLatitude() + ","
                            + mCurrentLocation.getLongitude();
                    Log.d(TAG, currentLocation);
                    mAirportInputFragment.setCurrentLocation(currentLocation);
                }

                startLocationUpdates();
            }
        }
    }
    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        String currentLocation = mCurrentLocation.getLatitude() + ","
                + mCurrentLocation.getLongitude();
        Log.d(TAG, currentLocation);
        mAirportInputFragment.setCurrentLocation(currentLocation);
    }

    /**
     * What happens when the google api client disconnects
     * @param cause the cause of the disconnection
     */
    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * what happens when the connection for the google api client fails
     * @param result
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        /**
         * What happens when the service is connected and binded to the class
         * @param className current activity
         * @param service updatedashboard service
         */
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to UpdateDashService, cast the IBinder and get UpdateDashService instance
            UpdateDashService.LocalBinder binder = (UpdateDashService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        /**
         * what happens when the service is disconnected
         * @param arg0
         */
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /**
     * When the service runs, this method is called to update the airport window and the tsa
     */
    @Override
    public void onServiceInteraction() {
        if (mCurrentLocation != null) {
            String currentLocation = mCurrentLocation.getLatitude() + ","
                    + mCurrentLocation.getLongitude();
            Log.d(TAG, currentLocation);
            mAirportInputFragment.setCurrentLocation(currentLocation);
        }
        mAirportInputFragment.connectToApi();
        mTSAFragment.connectToApi();
        if (!mBoardingInputFragment.getGate().isEmpty() ||
                !mBoardingInputFragment.getBoardingTime().isEmpty()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBoardingInputFragment.sendLogistics();
                }
            });
        }
        Log.d(TAG, "Updated dashboard "); 
    }
    
    /* A time was picked and something has to be done with the data.
     * @param time time in hh:mm
     * @param tag originator tag
     */
    @Override
    public void onTimePicked(String time, String tag) {
        Log.d(tag, time);
        switch (tag) {
            case "boarding_time":
                ((TextView)findViewById(R.id.add_flight_boarding_time)).setText(time);
                break;
            case "departure_time":
                ((TextView)findViewById(R.id.add_flight_departure_time)).setText(time);
                break;
            case "arrival_time":
                ((TextView)findViewById(R.id.add_flight_arrival_time)).setText(time);
                break;
        }
    }
}
