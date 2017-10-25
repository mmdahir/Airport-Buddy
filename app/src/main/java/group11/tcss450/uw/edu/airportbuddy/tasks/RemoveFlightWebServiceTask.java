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
import java.util.List;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.adapters.FlightAdapter;
import group11.tcss450.uw.edu.airportbuddy.menu.Flight;

/**
 * Removes the selected flight from the users saved flight list in the DB.
 */
public class RemoveFlightWebServiceTask extends AsyncTask<Flight, Void, String> {
    private final String PARTIAL_URL = "http://cssgate.insttech.washington.edu/~brianjor/";
    private final String SERVICE = "deleteSavedFlight.php?";
    private Flight mFlight;
    private Context context;
    private List<Flight> mListFlight;
    private RecyclerView.Adapter mAdapter;

    /**
     * Constructor that takes in context, saved flights, and recycler adapter.
     * @param context Context of the activity
     * @param flights list of flights
     * @param adapter recycler view adapter
     */
    public RemoveFlightWebServiceTask (Context context, List<Flight> flights,
                                       RecyclerView.Adapter adapter){
        super();
        this.context = context;
        mListFlight = flights;
        mAdapter = adapter;
    }

    @Override
    protected String doInBackground(Flight... flight) {
        mFlight = flight[0];
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
                    + URLEncoder.encode(username, "UTF-8")
                    + "&"
                    + URLEncoder.encode("flightNumber", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(mFlight.getFlightNumber()), "UTF-8");
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
            mListFlight.remove(mFlight);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }
    }
}
