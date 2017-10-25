package group11.tcss450.uw.edu.airportbuddy.dashboardFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.dialogs.TimePickerDialogFragment;
import group11.tcss450.uw.edu.airportbuddy.menu.Flight;
import group11.tcss450.uw.edu.airportbuddy.menu.Flights;

/**
 * Fragment that allows a user to enter flight data to save.
 */
public class AddFlightFragment extends Fragment {

    /**
     * Constructor.
     */
    public AddFlightFragment() {
        // Required empty public constructor
    }

    private Flight mFlight;

    /**
     * Sets up the view.
     *
     * @param inflater view inflater
     * @param container container
     * @param savedInstanceState saved instance state
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_flight, container, false);

        Button b = (Button) v.findViewById(R.id.confirm_add_flight);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flightNumber = ((EditText) getView()
                        .findViewById(R.id.flight_number_et)).getText().toString();
                int gateNumber = Integer.valueOf(((EditText) getView().findViewById(R.id.gate_number_et))
                        .getText().toString());

                String boardingTimeString = ((TextView) getView()
                        .findViewById(R.id.add_flight_boarding_time)).getText().toString();
                Time boarding = new Time(timeStringToLong(boardingTimeString));

                String departureTimeString = ((TextView) getView()
                        .findViewById(R.id.add_flight_departure_time)).getText().toString();
                Time departure = new Time(timeStringToLong(departureTimeString));

                String arrivalTimeString = ((TextView) getView()
                        .findViewById(R.id.add_flight_arrival_time)).getText().toString();
                Time arrival = new Time(timeStringToLong(arrivalTimeString));

                String locationTo = ((EditText) getView().findViewById(R.id.location_to_et))
                        .getText().toString();
                String locationFrom = ((EditText) getView().findViewById(R.id.location_from_et))
                        .getText().toString();
                mFlight = new Flight(flightNumber, gateNumber, departure, arrival, boarding, locationTo,
                        locationFrom);
                Log.d("adding flight", mFlight.toString());
                AsyncTask<Flight, Void, String> task = new AddSavedFlightWebServiceTask();
                task.execute(mFlight);
            }
        });
        TextView tv = (TextView) v.findViewById(R.id.add_flight_boarding_time);
        setTextViewOnClickListener(tv, "boarding_time");
        tv = (TextView) v.findViewById(R.id.add_flight_departure_time);
        setTextViewOnClickListener(tv, "departure_time");
        tv = (TextView) v.findViewById(R.id.add_flight_arrival_time);
        setTextViewOnClickListener(tv, "arrival_time");

        return v;
    }

    /**
     * Method to set onClickListener to text view for time pickers.
     *
     * @param tv the text view
     * @param identifier its identifier
     */
    private void setTextViewOnClickListener(TextView tv, final String identifier) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = new TimePickerDialogFragment();
                Bundle args = new Bundle();
                args.putString("TAG", identifier);
                fragment.setArguments(args);
                fragment.show(getActivity().getSupportFragmentManager(), "time");
            }
        });
    }

    /**
     * Converts a string "hh:mm a" to a long.
     *
     * @param time time string in format "hh:mm a"
     * @return time as a long
     */
    private long timeStringToLong(String time) {
        DateFormat df = new SimpleDateFormat("hh:mm a");
        Date dt = new Date();
        try {
            dt = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt.getTime();
    }

    /**
     * Web service that adds a flight to the users saved flights in DB.
     */
    private class AddSavedFlightWebServiceTask extends AsyncTask<Flight, Void, String> {
        private final String PARTIAL_URL = "http://cssgate.insttech.washington.edu/"
                + "~brianjor/";
        private final String SERVICE = "insertSavedFlights.php?";
        @Override
        protected String doInBackground(Flight... flights) {
            Flight flight = flights[0];
            String response = "";
            HttpURLConnection urlConnection = null;
            SharedPreferences sharedPref = getActivity()
                    .getSharedPreferences("PREF", Context.MODE_PRIVATE);
            String username = sharedPref.getString(getString(R.string.saved_username), "null");
            try {
                URL urlObject = new URL(PARTIAL_URL + SERVICE);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                String data = URLEncoder.encode("userName", "UTF-8") + "="
                        + URLEncoder.encode(username, "UTF-8")
                        + "&"
                        + URLEncoder.encode("flightNumber", "UTF-8") + "="
                        + URLEncoder.encode(String.valueOf(flight.getFlightNumber()), "UTF-8")
                        + "&"
                        + URLEncoder.encode("gateNumber", "UTF-8") + "="
                        + URLEncoder.encode(String.valueOf(flight.getGateNumber()), "UTF-8")
                        + "&"
                        + URLEncoder.encode("departure", "UTF-8") + "="
                        + URLEncoder.encode(String.valueOf(flight.getDeparture()), "UTF-8")
                        + "&"
                        + URLEncoder.encode("arrival", "UTF-8") + "="
                        + URLEncoder.encode(String.valueOf(flight.getArrival()), "UTF-8")
                        + "&"
                        + URLEncoder.encode("boarding", "UTF-8") + "="
                        + URLEncoder.encode(String.valueOf(flight.getBoarding()), "UTF-8")
                        + "&"
                        + URLEncoder.encode("locationTo", "UTF-8") + "="
                        + URLEncoder.encode(String.valueOf(flight.getLocationTo()), "UTF-8")
                        + "&"
                        + URLEncoder.encode("locationFrom", "UTF-8") + "="
                        + URLEncoder.encode(String.valueOf(flight.getLocationFrom()), "UTF-8");
                Log.d("flight add to db", data);
                wr.write(data);
                wr.flush();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while((s = buffer.readLine()) != null) {
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
        @Override
        protected void onPostExecute(String result) {
            switch (result) {
                case "205":
                    Flights.getInstance().addFlight(mFlight);
                    Toast.makeText(getActivity(), getString(R.string.flight_added),
                            Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                    break;
                default:
                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}
