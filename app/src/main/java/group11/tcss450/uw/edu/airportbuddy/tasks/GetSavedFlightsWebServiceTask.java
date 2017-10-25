package group11.tcss450.uw.edu.airportbuddy.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Time;
import java.util.StringTokenizer;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.menu.Flight;
import group11.tcss450.uw.edu.airportbuddy.menu.Flights;

/**
 * Web service that gets saved flights for the user.
 */
public class GetSavedFlightsWebServiceTask extends AsyncTask<Void, Void, String> {
    private final String PARTIAL_URL = "http://cssgate.insttech.washington.edu/~brianjor/";
    private final String SERVICE = "getSavedFlights.php?";
    private Context context;
    private RecyclerView.Adapter mAdapter;

    /**
     * Constructor that takes in the context and recycler adapter.
     *
     * @param context activity context
     * @param adapter recyclerview adapter
     */
    public GetSavedFlightsWebServiceTask(Context context, RecyclerView.Adapter adapter) {
        super();
        this.context = context;
        this.mAdapter = adapter;
    }

    @Override
    protected String doInBackground(Void... params) {
        String response = "";
        HttpURLConnection urlConnection = null;
        SharedPreferences sharedPref = context
                .getSharedPreferences("PREF", Context.MODE_PRIVATE);
        String username = sharedPref.getString(context.getString(R.string.saved_username), "null");
        try {
            URL urlObject = new URL(PARTIAL_URL + SERVICE);
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            String data = URLEncoder.encode("userName", "UTF-8") + "="
                    + URLEncoder.encode(username, "UTF-8");
            //Log.d("getting saved flights", PARTIAL_URL+SERVICE+data);
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
        if (result.startsWith("Unable to connect")) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        } else {
            Flights.clearFlights();
            parseResultString(result);
            mAdapter.notifyDataSetChanged();
            //Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getActivity(), getString(R.string.refreshed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Parses the result string and pulls out flight information.
     * @param result string containing flight data
     */
    private void parseResultString(String result) {
        StringTokenizer st = new StringTokenizer(result, "\t");
        while (st.hasMoreTokens()) {
            String flightNumber = st.nextToken();
            int gateNumber = Integer.valueOf(st.nextToken());
            Time departure = Time.valueOf(st.nextToken());
            Time arrival = Time.valueOf(st.nextToken());
            Time boarding = Time.valueOf(st.nextToken());
            String locationTo = st.nextToken();
            String locationFrom = st.nextToken();
            Flight flight = new Flight(flightNumber, gateNumber, departure, arrival, boarding, locationTo,
                    locationFrom);
            Flights.addFlight(flight);
        }
    }
}
