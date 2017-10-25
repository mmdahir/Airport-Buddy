package group11.tcss450.uw.edu.airportbuddy.dashboardFragments;


import android.Manifest;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnDashboardInteractionListener;
import group11.tcss450.uw.edu.airportbuddy.tasks.MapsMatrixWebServiceTask;

/**
 * Fragment that allows user to input information about their airport and current location.
 */
public class AirportInputFragment extends Fragment implements
        MapsMatrixWebServiceTask.OnMapsMatrixTaskCompleteListener {

    /**
     * Interaction listener.
     */
    private OnDashboardInteractionListener mListener;

    private static final String TAG = "Airport Input Fragment";

    private String mLocation;

    private String mStartLocation;

    private String mEndLocation;

    /**
     * Partial url to connect to google's distance matrix api.
     */
    private final String PARTIAL_URL
            = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial";

    /**
     * Empty constructor
     */
    public AirportInputFragment() {
        // Required empty public constructor
    }

    /**
     * @param inflater  The LayoutInflater object that can be used to
     *                  inflate any views in the fragment,
     * @param container This is the parent view that the fragment's UI should be attached to.
     * The fragment should not add the view itself, but this can
     *                  be used to generate the LayoutParams of the view.
     * @param savedInstanceState This fragment is being re-constructed from
     *                           a previous saved state as given here
     * @return Return the View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_airport_input, container, false);

        final Button locationButton = (Button)  v.findViewById(R.id.current_location_button);

        final int permissionCheck1 = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        final int permissionCheck2 = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        mLocation = "null";
        mStartLocation = "null";
        mEndLocation = "null";

        final EditText e = (EditText) v.findViewById(R.id.current_location);

        v.findViewById(R.id.airport_submit_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mStartLocation = ((EditText) v.findViewById(R.id.current_location))
                                .getText().toString();;
                        mEndLocation = ((EditText) v.findViewById(R.id.airport_autocomplete))
                                .getText().toString();
                        connectToApi();
                        mListener.OnInteractionListener("Airport Fragment Visibility", null,
                                null, null, null, null);
                    }
                });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocation.equals("null"))
                    e.setText(mLocation);
            }
        });

        return v;
    }

    public void setCurrentLocation(String theLocation) {
        mLocation = theLocation;
    }

    /**
     * Checks if an edit text has text
     * @param editText the edit text we are checking
     * @return boolean representing whether or not there is text in the edit text
     */
    private boolean isEmpty(EditText editText) {
        if (editText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    /**
     * Connects to API using the input provided by user.
     */
    public void connectToApi() {
        View v = this.getView();
        AsyncTask<String, Void, String> task;
        task = new MapsMatrixWebServiceTask(AirportInputFragment.this);
        task.execute(PARTIAL_URL, mStartLocation, mEndLocation);
    }

    /**
     * @param context Called when a fragment is first attached to its context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDashboardInteractionListener) {
            mListener = (OnDashboardInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAirportFragmentInteractionListener");
        }
    }

    /**
     * Called immediately prior to the fragment no longer being associated with its activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Gets json from maps matrix api and shows the distance and duration.
     *
     * @param message json string to output
     */
    @Override
    public void onMapsMatrixTaskCompletion(String message) {
        Log.d(TAG, "inside");
        try {
            JSONObject json = new JSONObject(message);
            String airportCode = "";

            String airport = json.getJSONArray("destination_addresses").getString(0);
            if (airport.contains("(") && airport.contains(")")) {
                int start = airport.indexOf("(") + 1;
                int end = airport.indexOf(")");


                for (int i = 0; i < (end - start); i++) {
                    airportCode += airport.charAt(i + start);
                }

                airport = airport.substring(0, start - 1);
            }
            Log.d(TAG, airport);
            Log.d(TAG, airportCode);

            View view = this.getView();
            if (!airport.isEmpty()) {
                mListener.OnInteractionListener("Airport Code", null, null, airport, null, null);
            }

            JSONObject jsonRows = json.getJSONArray("rows").getJSONObject(0);
            JSONArray jsonArr = jsonRows.getJSONArray("elements");
            String distance = jsonArr.getJSONObject(0).getJSONObject("distance").getString("text");
            String duration = jsonArr.getJSONObject(0).getJSONObject("duration").getString("text");
            String s = "Distance: " + distance + ", Duration: " + duration;
            Log.d("JSON elements:", s);
            mListener.OnInteractionListener("Airport ETA", null, null, airportCode, duration, null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
