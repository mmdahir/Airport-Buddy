package group11.tcss450.uw.edu.airportbuddy.dashboardFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.activities.StartActivity;
import group11.tcss450.uw.edu.airportbuddy.tasks.TSAWebServiceTask;


/**
 * A fragment that holds any information pertaining to
 * TSA line wait times.
 */
public class TSAFragment extends Fragment implements
        TSAWebServiceTask.OnTsaTaskCompleteListener {

    private String mAirportCode = "null";

    private static final String TAG = "TSA Fragment";

    /**
     * Default constructor.
     */
    public TSAFragment() {
        // Required empty public constructor
    }


    /**
     * Creates the view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container  This is the parent view that the fragment's UI should be attached to.
     * The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState This fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tsa, container, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, getActivity().toString());
                connectToApi();
            }
        });
        return v;
    }

    /**
     * Connects to the TSA API and gets the wait time for TSA lines.
     */
     public void connectToApi() {
        Log.d(TAG, mAirportCode);
         if (!mAirportCode.equals("null")) {
             AsyncTask<String, Void, String> task;
             task = new TSAWebServiceTask(TSAFragment.this);
             task.execute(mAirportCode);
         }
    }

    public void setAirportCode(String theAirportCode) {
        if (!theAirportCode.isEmpty())
            mAirportCode = theAirportCode;
    }

    /**
     * Outputs the returned wait time for the TSA line at the entered airport
     *
     * @param time current TSA line wait time
     */
    @Override
    public void OnTsaTaskCompletion(int time) {
        ((TextView) getView().findViewById(R.id.wait_time_text))
                .setText("0 H " + String.valueOf(time) + " M");
    }
}
