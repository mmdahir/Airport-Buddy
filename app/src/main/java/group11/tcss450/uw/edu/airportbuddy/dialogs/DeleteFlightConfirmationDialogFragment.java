package group11.tcss450.uw.edu.airportbuddy.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.menu.Flight;
import group11.tcss450.uw.edu.airportbuddy.tasks.RemoveFlightWebServiceTask;

/**
 * Dialog that pops up to confirm flight deletion.
 */
public class DeleteFlightConfirmationDialogFragment extends DialogFragment {
    private List<Flight> mListFlight;
    private Flight flight;
    private RecyclerView.Adapter mAdapter;

    /**
     * Constructor.
     */
    public DeleteFlightConfirmationDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Data that is needed to delete a flight.
     *
     * @param mListFlight list of flights
     * @param flight the flight to delete
     * @param mAdapter recycler view adapter
     */
    public void data(List<Flight> mListFlight, Flight flight, RecyclerView.Adapter mAdapter) {
        this.mListFlight = mListFlight;
        this.flight = flight;
        this.mAdapter = mAdapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AsyncTask<Flight, Void, String> task =
                                new RemoveFlightWebServiceTask(getActivity(), mListFlight, mAdapter);
                        task.execute(flight);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
