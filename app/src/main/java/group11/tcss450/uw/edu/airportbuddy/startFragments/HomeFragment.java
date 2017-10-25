package group11.tcss450.uw.edu.airportbuddy.startFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnStartInteractionListener;


/**
 * This is the fragment that is displayed when app is first launched.
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStartInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    /**
     * Fragment interaction listener.
     */
    private OnStartInteractionListener mListener;

    /**
     * Default constructor.
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Creates the view for this fragment  and set onClickListeners
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container  This is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState This fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        Button b = (Button) v.findViewById(R.id.loginButton);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.registerButton);
        b.setOnClickListener(this);
        return v;
    }

    /**
     * Called when a button press is detected and signals whether or not the login button was pressed or
     * the register button is pressed. Redirects through mListener accordingly
     * @param view The view
     */
    @Override
    public void onClick(View view){
        if (mListener != null) {
            switch (view.getId()) {
                case R.id.loginButton:
                    mListener.OnInteractionListener("launch","login", null, null, null);
                    break;
                case R.id.registerButton:
                    mListener.OnInteractionListener("launch","register", null, null, null);
                    break;
            }
        }
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
                    + " must implement OnFirstFragmentInteractionListener");
        }
    }

    /**
     * Called on detach
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
