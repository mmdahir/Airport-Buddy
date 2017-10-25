package group11.tcss450.uw.edu.airportbuddy.dashboardFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnDashboardInteractionListener;

/**
 * Fragment containing information about airport
 * Activities that contain this fragment must implement the
 * {@link OnDashboardInteractionListener} interface
 * to handle interaction events.
 */
public class AirportFragment extends Fragment{

    /**
     * Reference to airport textview
     */
    private TextView airportDisplay;

    /**
     * Reference to airport eta
     */
    private TextView airportETA;

    /**
     * Fragment interaction listener
     */
    private OnDashboardInteractionListener mListener;

    private String TAG = "AirportFragment";

    /**
     * Empty constructor
     */
    public AirportFragment() {
        // Required empty public constructor
    }


    /**
     * Creates the view for this fragment.
     *
     * @param inflater the inflater
     * @param container the container
     * @param savedInstanceState any saved states
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_airport, container, false);
        airportDisplay = (TextView)v.findViewById(R.id.airport_display);
        airportETA = (TextView)v.findViewById(R.id.airportETA);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnInteractionListener("Airport Fragment Visibility", null, null, null, null, null);
            }
        });
        return v;
    }


    /**
     * Called once the fragment is associated with its activity
     * @param context Context
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
     * Called when the fragment is no longer attached to its activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Sets the airport code display after user enters airport.
     * @param airportCode the airport the user inputs
     */
    public void setDisplay(String airportCode){
        airportDisplay.setText(airportCode);
    }

    public void setETA(String ETA) {
        String formatETA = ETA.replace("hours", "H");
        formatETA = formatETA.replace("mins", "M");
        airportETA.setText(formatETA);
    }


}
