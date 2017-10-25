package group11.tcss450.uw.edu.airportbuddy.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Creates a connection to the TSA API Web Service, where using the user entered
 * airport code returns the current TSA line wait time.
 */
public class TSAWebServiceTask extends AsyncTask<String, Void, String>{

    /**
     * A listener for when the task to get TSA wait times is completed.
     */
    private OnTsaTaskCompleteListener mListener;

    /**
     * Initializes the web service task and connects to the listener.
     *
     * @param listener the listener
     */
     public TSAWebServiceTask(final OnTsaTaskCompleteListener listener) {
         super();
         mListener = listener;
     }

    /**
     * Connects to the TSA API and requests the wait time for the supplied airport code.
     * Requests for JSON return data.
     *
     * @param params hold the airport code to get wait times for
     * @return the JSON return data holding the wait times
     */
    @Override
    protected String doInBackground(String... params) {
        final String PARTIAL_URL = "http://apps.tsa.dhs.gov/MyTSAWebService/GetTSOWaitTimes.ashx?ap=";
        final String airport = "SEA"; //Seattle-Tacoma International
        final String jsonRequest = "&output=json";
        if (params.length != 1) {
            throw new IllegalArgumentException("One string argument required.");
        }
//        String airport = params[0];
        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(PARTIAL_URL + airport + jsonRequest);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream content = urlConnection.getInputStream();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }

        } catch (Exception e) {
            response = e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;

    }

    /**
     * Tears apart the returned JSON and tells the listeners that the task is completed
     *
     * @param response the JSON response
     */
    @Override
    protected void onPostExecute(String response) {
        try {
            Log.d("TSA onPostExecute", response);
            JSONObject json = new JSONObject(response);
            JSONArray waitTimes = json.getJSONArray("WaitTimes");
            int waitTime = waitTimes.getJSONObject(0).getInt("WaitTime");
            Log.d("waitTime", String.valueOf(waitTime));
            mListener.OnTsaTaskCompletion(waitTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Interface to connect with TSA web service task
     */
    public interface OnTsaTaskCompleteListener {
        void OnTsaTaskCompletion(int time);
    }
}
