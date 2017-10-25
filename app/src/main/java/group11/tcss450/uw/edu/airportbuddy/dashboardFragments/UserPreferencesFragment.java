package group11.tcss450.uw.edu.airportbuddy.dashboardFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnDashboardInteractionListener;
import group11.tcss450.uw.edu.airportbuddy.tasks.GetUserPreferencesWebServiceTask;

/**
 * This is a Fragment that allows a user to set their User Preferences, accessed from the navigation bar.
 */
public class UserPreferencesFragment extends Fragment implements GetUserPreferencesWebServiceTask.OnPreferencesTaskCompleteListener {

    /**
     * Reference to the spinner object
     */
    Spinner spinner;

    /**
     * Fragment interaction listener.
     */
    private OnDashboardInteractionListener mListener;

    /**
     * The starting url of the connection.
     */
    private static final String PARTIAL_URL
            = "http://cssgate.insttech.washington.edu/"
            + "~brianjor/";

    /**
     * Sets and organizes the view. Also sets up the Spinner and a button's onClick listener.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_user_preferences, container, false);
        View v = inflater.inflate(R.layout.fragment_user_preferences, container, false);
        spinner = (Spinner) v.findViewById(R.id.hour_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.hours, R.layout.spinner);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        AsyncTask<String, Void, String> getPrefTask = new GetUserPreferencesWebServiceTask(this, getContext());
        getPrefTask.execute();

        final String username = getArguments().getString("Username");

        v.findViewById(R.id.preferences_submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = spinner.getSelectedItem().toString();
                if (!username.equals("null")) {
                    AsyncTask<String, Void, String> task;
                    task = new UserPreferenceWebServiceTask();
                    task.execute(PARTIAL_URL, username, result, "preferences.php?");
                }
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

    @Override
    public void OnPreferencesTaskCompletion(String earliness) {
        spinner.setSelection(Integer.parseInt(earliness));
    }

    /**
     * Private Class that connects to a php script that manages the Database.
     */
    private class UserPreferenceWebServiceTask extends AsyncTask<String, Void, String> {



        /**
         * Does the connection to the web service in the background.
         */
        @Override
        protected String doInBackground(String... strings) {
            String service = strings[3];
            if (strings.length != 4) {
                throw new IllegalArgumentException("Two String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];
            try {

                String data = URLEncoder.encode("username", "UTF-8")
                        + "=" + URLEncoder.encode(strings[1], "UTF-8")
                        + "&"
                        + URLEncoder.encode("earliness", "UTF-8")
                        + "=" + URLEncoder.encode(strings[2], "UTF-8");

                URL urlObject = new URL(url + service + data);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                response = "Unable to connect, Reason: "
                        + e.getMessage();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return response;
        }

        /**
         * Sets up the returned value depending on if a correction connection was made.
         * @param result A string with the value of if it was a successful save or not.
         */
        @Override
        protected void onPostExecute(String result) {
            switch (result) {
                case "204":
                    Toast.makeText(getActivity(), "Saved your preference", Toast.LENGTH_LONG).show();
                    String updatedSpinnerValue = spinner.getSelectedItem().toString();
                    mListener.OnInteractionListener("Updated preference", null, null, null, null, updatedSpinnerValue);
                    break;
                default:
                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}
