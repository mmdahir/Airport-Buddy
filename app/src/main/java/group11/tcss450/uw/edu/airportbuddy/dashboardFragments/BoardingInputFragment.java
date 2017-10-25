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

        import group11.tcss450.uw.edu.airportbuddy.R;
        import group11.tcss450.uw.edu.airportbuddy.interfaces.OnDashboardInteractionListener;

/**
 *
 * This fragment allows the user to input their boarding information.
 */
public class BoardingInputFragment extends Fragment {

    /**
     * Fragment interaction listener.
     */
    private OnDashboardInteractionListener mListener;

    /**
     * Used for status messages.
     */
    public String TAG = "BoardingInputFragment:";

    /**
     * The gate that the user's flight is in
     */
    private String mGate;

    /**
     * The time of boarding for the user's flight
     */
    private String mBoardingTime;

    /**
     * Default constructor.
     */
    public BoardingInputFragment() {
        // Required empty public constructor
    }

    /**
     * Creates the view for this fragment and sets onClickListeners
     * @param inflater The LayoutInflater object that can be used to inflate
     *                 any views in the fragment,
     * @param container  This is the parent view that the fragment's UI should be attached to.
     * The fragment should not add the view itself, but this can be used to generate the
     *                   LayoutParams of the view.
     * @param savedInstanceState This fragment is being re-constructed from a previous
     *                           saved state as given here
     * @return Return the View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_boarding_input, container, false);
        mBoardingTime = "";
        mGate = "";

        v.findViewById(R.id.boarding_submit_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendLogistics();
                        mListener.OnInteractionListener("Boarding Fragment Visibility", null, null,
                                null, null, null);
                    }
                });
        return v;
    }


    /**
     * Passes the user inputs to interaction listener.
     */
    public void sendLogistics() {
        View v = this.getView();
        mGate =  ((TextView) v.findViewById(R.id.gate_text)).getText().toString();
        mBoardingTime = ((TextView)v.findViewById(R.id.boarding_text)).getText().toString();
        Log.i(TAG, "sendLogistics: " + mGate);
        Log.i(TAG, "sendLogistics: " + mBoardingTime);
        mListener.OnInteractionListener("Boarding Input", mGate, mBoardingTime, null, null, null);
    }

    /**
     * Gets the gate number of the user's flight
     * @return the gate numer
     */
    public String getGate() {
        return mGate;
    }

    /**
     * Gets the user flight's boarding time
     * @return the boarding time
     */
    public String getBoardingTime() {
        return mBoardingTime;
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
        }else {
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
}
