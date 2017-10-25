package group11.tcss450.uw.edu.airportbuddy.dashboardFragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnDashboardInteractionListener;
import group11.tcss450.uw.edu.airportbuddy.tasks.GetUserPreferencesWebServiceTask;
import group11.tcss450.uw.edu.airportbuddy.tasks.TSAWebServiceTask;


/**
 * This is the fragment that contains the boarding information. When clicked,
 * BoardingInputFragment will appear to prompt the user for inputs regarding gate # and
 * boarding time. Once the user hits submit, that data will be displayed on this fragment.
 */
public class BoardingFragment extends Fragment implements GetUserPreferencesWebServiceTask.OnPreferencesTaskCompleteListener {

    /**
     * Fragment interaction listener.
     */
    private OnDashboardInteractionListener mListener;

    /**
     * String representation of the earliness user preference
     */
    private String earliness;

    /**
     * View.
     */
    private View v;

    /**
     * Used to output status messages.
     */
    public String TAG = "From BoardingFragment";

    /**
     * Default constructor.
     */
    public BoardingFragment() {
    }


    /**
     * Sets value of gate text view
     * @param gate String value to be inserted in the view.
     */
    public void setGateTextView (String gate) {
        TextView a;
        a = (TextView) getActivity().findViewById(R.id.boarding_gate);
        a.setText("Gate " + gate);
    }



    /**
     * Sets value of boarding text view
     * @param time String value to be inserted in the view.
     */
    public void setBoardingTextView(String time) {
        TextView boardingTimeDisplay;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
        Date boardingTimeDate = new Date();
        Date currentTimeDate = new Date();

        // Get today's date in yyyy/MM/dd
        Date today1 = new Date();
        String todaysDate = new SimpleDateFormat("yyyy/MM/dd").format(today1);

        Date today2 = new Date();
        String fullDate = new SimpleDateFormat("yyyy/MM/dd hh:mm a").format(today2);

        // Append today's date with the time passed in
        String boardingTime = todaysDate + " " + time;

        // Handle pm -> am case by incrementing date by 1
        if (boardingTime.toLowerCase().contains("am") && fullDate.toLowerCase().contains("pm")) {
            Calendar c = Calendar.getInstance();
            try {
                boardingTimeDate = sdf.parse(boardingTime);
                currentTimeDate = sdf.parse(fullDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.setTime(boardingTimeDate);
            c.add(Calendar.DATE, 1);
            boardingTimeDate = c.getTime();
        } else {
            try {
                boardingTimeDate = sdf.parse(boardingTime);
                currentTimeDate = sdf.parse(fullDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



        if (earliness.equals("0")) {
            // Math for converting milliseconds to hours/mins
            long millis = boardingTimeDate.getTime() - currentTimeDate.getTime();
            int Hours = (int) (millis/(1000 * 60 * 60));
            int Mins = (int) (millis/(1000*60)) % 60;

            String diff = Hours + " H " + (Mins) + " M";
            boardingTimeDisplay = (TextView) getActivity().findViewById(R.id.boarding_time);
            boardingTimeDisplay.setText("Boarding in " + diff);
        } else {
            // Incorporate earliness preference by upping current time by [earliness preference] hours
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentTimeDate);
            cal.add(Calendar.HOUR,Integer.parseInt(earliness));

            //Convert from cal -> date
            Date adjustedTime = cal.getTime();


            long millis = boardingTimeDate.getTime() - adjustedTime.getTime();

            // If the user cannot be early to their flight
            if (millis < 0) {
                millis = boardingTimeDate.getTime() - currentTimeDate.getTime();
                int Hours = (int) (millis/(1000 * 60 * 60));
                int Mins = (int) (millis/(1000*60)) % 60;

                String diff = Hours + " H " + (Mins) + " M";
                boardingTimeDisplay = (TextView) getActivity().findViewById(R.id.boarding_time);
                boardingTimeDisplay.setText("Boarding in " + diff + " \n (Can't arrive early)" );

            } else {
                int Hours = (int) (millis/(1000 * 60 * 60));
                int Mins = (int) (millis/(1000*60)) % 60;

                String diff = Hours + " H " + (Mins) + " M";
                boardingTimeDisplay = (TextView) getActivity().findViewById(R.id.boarding_time);
                boardingTimeDisplay.setText("Boarding in " + diff + " \n (" + earliness + " H early)" );
            }

        }


    }



    /**
     * Connects to the database to get user's earliness preference
     */
    private void getEarlinessPreference() {
        AsyncTask<String, Void, String> task;
        task = new GetUserPreferencesWebServiceTask(BoardingFragment.this, this.getContext());
        task.execute();
    }


    /**
     * Creates the view for this fragment and sets onClickListeners.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container  This is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState This fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_boarding, container, false);
        getEarlinessPreference();

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: " + v );
                mListener.OnInteractionListener("Boarding Fragment Visibility", null, null, null, null, null);

            }
        });

        return v;
    }


    /**
     * Called once the fragment is associated with its activity
     * @param context The context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDashboardInteractionListener) {
            mListener = (OnDashboardInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBoardingFragmentInteractionListener");
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
     * Callback that recieves and sets the earliness preference
     * @param earliness the earliness preference
     */
    @Override
    public void OnPreferencesTaskCompletion(String earliness) {
        if (earliness != null) {
            setEarliness(earliness);
        }
    }

    /**
     * Setter for earliness preference
     * @param earliness is the earliness preference
     */
    public void setEarliness(String earliness) {
        this.earliness = earliness;
    }

    /**
     * Getter for the earliness preference
     * @return a string representation of earliness
     */
    public String getEarliness() {
        return earliness;
    }
}
