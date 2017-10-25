package group11.tcss450.uw.edu.airportbuddy.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.activities.StartActivity;

/**
 * Creates a dialog to confirm whether or not a user wants to logout
 */
public class LogoutDialogFragment extends DialogFragment {


    /**
     * Required Public constructor
     */
    public LogoutDialogFragment() {}


    /**
     * If the user wants to log out, then a new activity is started and the activity back stack is
     * cleared, it also sends in a string that lets the activity know a logout occured, so that the
     * activity can delete all instances of the old user's information
     * If the user decides not to, it closes the dialog and does nothing
     * @param savedInstanceState Will get used if activity is being re-initialized
     *                           after previously being shut down.
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_logout)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(), StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(getString(R.string.logout), true);
                        startActivity(intent);
                        getActivity().finish(); // call this to finish the current activity
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
