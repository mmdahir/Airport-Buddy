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
 * This fragment is responsible for handling registration of users
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{

    /**
     * URL for connecting to web server.
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
     * Boolean that determines if username already exists in database.
     */
    private boolean mUserAlreadyExists;

    /**
     * Fragment interaction listener.
     */
    private OnStartInteractionListener mListener;

    /**
     * Default constructor.
     */
    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Creates the view for this fragment and sets up onClickListeners
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
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
        Log.d("Register", "creating view");
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        Button b = (Button) v.findViewById(R.id.submit);
        b.setOnClickListener(this);
        return v;
    }

    /**
     * Called once the fragment is associated with its activity
     * @param context Context
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
     * When the register button is pressed, the input is verified and passed on to the server.
     * @param view View.
     */
    @Override
    public void onClick(View view) {
        AsyncTask<String, Void, String> task = null;
        View v = this.getView();
        mUsername = ((EditText) v.findViewById(R.id.usernameEdit)).getText().toString();
        mPassword = ((EditText) v.findViewById(R.id.passwordEdit)).getText().toString();
        String PasswordConfirm = ((EditText) v.findViewById(R.id.confirmPasswordEdit)).getText()
                .toString();
        TextView tv;
        boolean ready = true;
        tv = (TextView) v.findViewById(R.id.usernameEdit);
        if (mUsername.length() == 0) {
            tv.setError(getString(R.string.warning_empty_username));
            ready = false;
        } else if (mUsername.matches("^.*[^a-zA-Z0-9].*$")) {
            tv.setError(getString(R.string.username_reqs));
            ready = false;
        } else if (mUsername.length() < 4) {
            tv.setError(getString(R.string.short_username));
            ready = false;
        } else {
            tv.setError(null);
        }
        tv = (TextView) v.findViewById(R.id.passwordEdit);

        if (mPassword.length() == 0) {
            tv.setError(getString(R.string.warning_empty_password));
            ready = false;
        } else if (!(mPassword.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}"))) {
            tv.setError(getString(R.string.password_reqs));
            ready = false;
        } else {
            tv.setError(null);
        }
        tv = (TextView) v.findViewById(R.id.confirmPasswordEdit);
        if (PasswordConfirm.length() == 0 || mPassword.compareTo(PasswordConfirm) != 0) {
            tv.setError(getString(R.string.warning_mismatch_password));
            ready = false;
        } else {
            tv.setError(null);
        }
        if (mListener != null && ready) {
            task = new CheckForExistingUserWebServiceTask();
            task.execute(PARTIAL_URL, mUsername);
        }
    }

    /**
     * Interface for handling registration. Communicates to other classes that implement this
     */
    public interface OnRegisterFragmentInteractionListener {
        void onRegisterFragmentInteraction(String response, String username, String password);
    }

    /**
     * Async task that checks if username is already in database.
     */
    private class CheckForExistingUserWebServiceTask extends AsyncTask<String, Void, String> {
        /**
         * PHP script
         */
        private final String SERVICE = "userexist.php";

        /**
         * Checking the username against database entries.
         * @param strings Username
         * @return status.
         */
        @Override
        protected String doInBackground(String... strings) {
            if (strings.length !=2) {
                throw new IllegalArgumentException("Two String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];
            try {
                URL urlObject = new URL(url + SERVICE);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                String data = URLEncoder.encode("username", "UTF-8")
                        + "=" + URLEncoder.encode(strings[1], "UTF-8");
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
         * Depending on the async task's output a message/action will be performed.
         * @param result Status
         */
        @Override
        protected void onPostExecute(String result) {
        // Something wrong with the network or the URL.
            switch(result) {
                case "201":
                    Toast.makeText(getActivity(), getString(R.string.username_already_exists)
                            , Toast.LENGTH_LONG).show();
                    break;
                case "199":
                    AsyncTask<String, Void, String> task = new RegisterUserWebServiceTask();
                    task.execute(PARTIAL_URL, mUsername, mPassword);
                    break;
                default:
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**
     * Registering a new user given their username doesn't already exist.
     */
    private class RegisterUserWebServiceTask extends AsyncTask<String, Void, String> {
        /**
         * PHP script.
         */
        private final String SERVICE = "register.php";

        /**
         * Connecting and communicating with the server.
         * @param strings Username nad password
         * @return status
         */
        @Override
        protected String doInBackground(String... strings) {
            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];
            try {
                URL urlObject = new URL(url + SERVICE);
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
         * Displays a status based on response from async task.
         * @param result status received.
         */
        @Override
        protected void onPostExecute(String result) {
        // Something wrong with the network or the URL.
            switch(result) {
                case "199":
                    mListener
                            .OnInteractionListener("register", null, getString(R.string.successful_register), mUsername, mPassword);
                    break;
                default:
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                    break;
            }

        }
    }
}
