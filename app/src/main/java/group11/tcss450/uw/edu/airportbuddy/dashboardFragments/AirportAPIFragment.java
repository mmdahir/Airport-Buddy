package group11.tcss450.uw.edu.airportbuddy.dashboardFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnDashboardInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Takes in the current location and airport and connects to google maps matrix api.
 */
public class AirportAPIFragment extends Fragment {



    /**
     * The key for getting argument data.
     */
    public static final String KEY = "Distance and Time";

    /**
     * Textview that will display distance and time from airport.
     */
    private TextView mTextView;

    private String TAG = "airportAPI";
    /**
     * Default constructor.
     */
    public AirportAPIFragment() {
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_airport_api, container, false);
        mTextView = (TextView) v.findViewById(R.id.result);
        if(getArguments() != null) {
            String info = getArguments().getString(KEY);
            parseJSON(info);
        }
        return v;
    }

    /**
     * Parses the json to display distance and time from the airport.
     *
     * @param json json string from google distance matrix api
     */
    private void parseJSON(final String json) {
        try {
            JSONObject object = new JSONObject(json);

            object = object.getJSONObject("rows").getJSONObject("elements");
            if (object.getString("status").equals("OK")) {

                JSONObject distanceObject = object.getJSONObject("distance");

                String distance = "Distance: " + distanceObject.getString("text");

                JSONObject durationObject = object.getJSONObject("duration");

                String duration = "Duration: " + durationObject.getString("text");
//                JSONObject show = response.getJSONArray("data").getJSONObject(0);

                mTextView.setText(distance + duration);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
