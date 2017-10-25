package group11.tcss450.uw.edu.airportbuddy.startFragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnStartInteractionListener;


/**
 * Fragment displayed when login button is pressed. Allows the user to login with their credentials.
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    /**
     * Partial url string that connects to the web server.
     */
    private static final String PARTIAL_URL
            = "http://cssgate.insttech.washington.edu/"
            + "~brianjor/";

    /**
     * Username string
     */
    private String mUsername;

    /**
     * Password string
     */
    private String mPassword;

    /**
     * Fragment interaction listener.
     */
    private OnStartInteractionListener mListener;

    /**
     * Default constructor.
     */
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Creates the view for this fragment and sets on click listeners
     * @param inflater The LayoutInflater object is used to inflate any views in the fragment,
     * @param container  This is the parent view that the fragment's UI should be attached to.
     * The fragment should not add the view itself, but this can be used to generate the
     * LayoutParams of the view.
     * @param savedInstanceState This fragment is being re-constructed from a previous saved
     * state as given here
     * @return Return the View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Login", "creating view");
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        Button b = (Button) v.findViewById(R.id.loginButton);
        b.setOnClickListener(this);
        return v;
    }

    /**
     * Called once the fragment is associated with its activity
     * @param context The context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStartInteractionListener) {
            mListener = (OnStartInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStartInteractionListener");
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
     * When user submit their username and password. Error checks for empty fields.
     * @param view The view
     */
    @Override
    public void onClick(View view) {
        Log.d("LoginFragment", "button clicked");
        View v = this.getView();
        mUsername = ((EditText) v.findViewById(R.id.usernameEdit)).getText().toString();
        mPassword = ((EditText) v.findViewById(R.id.passwordEdit)).getText().toString();
        TextView tv;
        boolean ready = true;
        tv = (TextView) v.findViewById(R.id.usernameEdit);
        if (mUsername.length() == 0) {
            tv.setError(getString(R.string.warning_empty_username));
            ready = false;
        } else {
            tv.setError(null);
        }
        tv = (TextView) v.findViewById(R.id.passwordEdit);
        if (mPassword.length() == 0) {
            tv.setError(getString(R.string.warning_empty_password));
            ready = false;
        } else {
            tv.setError(null);
        }
        if (mListener != null && ready) {
            checkUserName();
        }
    }

    /**
     * Setting up async task and validate credentials against the database,
     */
    private void checkUserName() {
        AsyncTask<String, Void, String> task = null;
        task = new ValidateCredentialsWebServiceTask();
        task.execute(PARTIAL_URL, mUsername, mPassword, "login.php");
    }


    /**
     * AsyncTask used to validate log in credentials. Connects to php script
     * and gets status codes in return
     */
    private class ValidateCredentialsWebServiceTask extends AsyncTask<String, Void, String> {


        /**
         * Checking the input for validity and sending it to the server.
         * @param strings username and password.
         * @return Status message
         */
        @Override
        protected String doInBackground(String... strings) {
            String service = strings[3];
            if (strings.length !=4) {
                throw new IllegalArgumentException("Two String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];
            try {
                URL urlObject = new URL(url + service);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                String data = URLEncoder.encode("username", "UTF-8")
                        + "=" + URLEncoder.encode(strings[1], "UTF-8")
                        + "&"
                        + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(strings[2], "UTF-8");
                wr.write(data);
                wr.flush();
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
         * Toast will be displayed based on the status message.
         * @param result Status message.
         */
        @Override
        protected void onPostExecute(String result) {
            switch (result) {
                case "199":
                    Toast.makeText(getActivity(), getString(R.string.invalid_username), Toast.LENGTH_LONG).show();
                    break;
                case "200":
                    mListener.OnInteractionListener("login", null, "Successful login", mUsername, mPassword);
                    break;
                case "202":
                    Toast.makeText(getActivity(), getString(R.string.invalid_password), Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
