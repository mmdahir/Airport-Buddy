package group11.tcss450.uw.edu.airportbuddy.dashboardFragments;


import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.adapters.FlightAdapter;
import group11.tcss450.uw.edu.airportbuddy.dialogs.DeleteFlightConfirmationDialogFragment;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnSavedFlightInteractionListener;
import group11.tcss450.uw.edu.airportbuddy.interfaces.RecyclerViewClickListener;
import group11.tcss450.uw.edu.airportbuddy.menu.Flight;
import group11.tcss450.uw.edu.airportbuddy.menu.Flights;
import group11.tcss450.uw.edu.airportbuddy.tasks.GetSavedFlightsWebServiceTask;

/**
 * Displays all the saved flight information to the user.
 */
public class SavedFlightsFragment extends Fragment implements RecyclerViewClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private List<Flight> mListFlight;

    private OnSavedFlightInteractionListener mListener;

    /**
     * Constructor.
     */
    public SavedFlightsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSavedFlightInteractionListener) {
            mListener = (OnSavedFlightInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSavedFlightInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_saved_flights, container, false);

        Button b = (Button) v.findViewById(R.id.add_flight);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFlightFragment addFlightFragment;
                addFlightFragment = new AddFlightFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.dash_container, addFlightFragment)
                        .addToBackStack(null).commit();
            }
        });
        b = (Button) v.findViewById(R.id.refresh_saved_flights);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<Void, Void, String> task;
                task = new GetSavedFlightsWebServiceTask(getActivity(), mAdapter);
                task.execute();
            }
        });

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mListFlight = new ArrayList<>();
        mListFlight = Flights.getFlights();

        mAdapter = new FlightAdapter(mListFlight, getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);

        b.callOnClick(); // refresh saved flights from DB

        return v;
    }

    /**
     * The load button from the recyclerview was clicked.
     * @param flight the flight to load
     */
    @Override
    public void recyclerViewLoadClicked(Flight flight) {
        mListener.onSavedFlightInteraction(flight);
        getActivity().getSupportFragmentManager().popBackStack(null, getActivity()
                .getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
        Log.d("Load Flight", flight.toString());
    }

    /**
     * The remove button from the recyclerview was clicked.
     * @param flight
     */
    @Override
    public void recyclerViewRemoveClicked(Flight flight) {
        DeleteFlightConfirmationDialogFragment fragment = new DeleteFlightConfirmationDialogFragment();
        if (fragment != null) {
            fragment.data(mListFlight, flight, mAdapter);
            fragment.show(getActivity().getFragmentManager(), "Remove flight dialog");
        }
//        AsyncTask<Flight, Void, String> task =
//                new RemoveFlightWebServiceTask(getActivity(), mListFlight, mAdapter);
//        task.execute(flight);
    }

}
