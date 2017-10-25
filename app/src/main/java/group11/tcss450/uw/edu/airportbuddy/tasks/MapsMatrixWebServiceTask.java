package group11.tcss450.uw.edu.airportbuddy.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import group11.tcss450.uw.edu.airportbuddy.R;

/**
 * Created by mustafdahir on 5/3/17.
 */

public class MapsMatrixWebServiceTask extends AsyncTask<String, Void, String> {

    private final String KEY = "AIzaSyDslgqVhzxgEa6PKuS86JD55dKVI06dIdo";

    private OnMapsMatrixTaskCompleteListener mListener;

    public MapsMatrixWebServiceTask(final OnMapsMatrixTaskCompleteListener listener) {
        super();
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... strings) {
        if (strings.length != 3) {
            throw new IllegalArgumentException("Three string arguments required.");
        }

        String response = "";
        HttpURLConnection urlConnection = null;
        String url = strings[0];
        Log.d("Web Service", strings[0]);
        try {


//                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            String data = "&" + URLEncoder.encode("origins", "UTF-8")
                    + "=" + URLEncoder.encode(strings[1], "UTF-8");

            data += '&';

            data += URLEncoder.encode("destinations", "UTF-8")
                    + "=" + URLEncoder.encode(strings[2], "UTF-8");

            data += '&';

            data += URLEncoder.encode("key", "UTF-8")
                    + "=" + URLEncoder.encode(KEY, "UTF-8");

            Log.d("Web Service", data);

            URL urlObject = new URL(strings[0] + data);
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
//                wr.write(data);
//                wr.flush();

            InputStream content = urlConnection.getInputStream();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }

        } catch (Exception e) {
            response = "Invalid Request. Error Message: " + e.getMessage();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return response;

    }

    @Override
    protected void onPostExecute(String result) {
        try {
            Log.d("onPostExecute: ", result);
            JSONObject response = new JSONObject(result);
            Log.d("Web Service", response.getString("status"));
            switch(response.getString("status")) {
                case "OK":
                    //Query successful
                    mListener.onMapsMatrixTaskCompletion(result);
                    Log.d("onPostExecute: ", "WORKS!!");
                    break;
                case "INVALID_REQUEST":
                    //Error in parameters
//                    Toast.makeText(getActivity(), getString(R.string.invalid_request), Toast.LENGTH_LONG)
//                            .show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public interface  OnMapsMatrixTaskCompleteListener {
        void onMapsMatrixTaskCompletion(String message);
    }

}