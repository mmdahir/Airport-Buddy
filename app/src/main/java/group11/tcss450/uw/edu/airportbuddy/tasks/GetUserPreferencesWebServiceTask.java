package group11.tcss450.uw.edu.airportbuddy.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import group11.tcss450.uw.edu.airportbuddy.R;

/**
 * Gets the users saved preference from the DB.
 */
public class GetUserPreferencesWebServiceTask extends AsyncTask<String, Void, String> {

    /**
     * Partial URL to cssgate
     */
    private final String PARTIAL_URL = "http://cssgate.insttech.washington.edu/~brianjor/";

    /**
     * Rest of URL to access the service script
     */
    private final String SERVICE = "getUserPreference.php?";

    /**
     * Context
     */
    private Context context;


    /**
     * A listener for when the task to get earliness preference is completed.
     */
    private OnPreferencesTaskCompleteListener mListener;


    /**
     * Constructor to get context to get shared preferences
     * @param listener the listener
     */
    public GetUserPreferencesWebServiceTask(final OnPreferencesTaskCompleteListener listener, Context context) {
        super();
        mListener = listener;
        this.context = context;
    }

    /**
     * Connects to service
     * @param params the parameters
     * @return the response from the asyncTask done in background
     */
    @Override
    protected String doInBackground(String... params) {
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

    /**
     * Makes toast reporting error if any, or sends result to dashboard activity post execute
     * @param result of doInBackground
     */
    @Override
    protected void onPostExecute(String result) {
        if (result.startsWith("Unable to connect")) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        } else {
            mListener.OnPreferencesTaskCompletion(result);
        }
    }

    /**
     * Interface for those listening for the finished task.
     */
    public interface OnPreferencesTaskCompleteListener {
        /**
         * Task has finished and is passing the earliness value to its listeners.
         *
         * @param earliness the users earliness preference
         */
        void OnPreferencesTaskCompletion(String earliness);
    }
}
