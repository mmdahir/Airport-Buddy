package group11.tcss450.uw.edu.airportbuddy.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import group11.tcss450.uw.edu.airportbuddy.interfaces.OnTimePickerListener;

/**
 * Dialog to pick a time.
 */
public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private OnTimePickerListener mListener;

    private String mTag;

    public TimePickerDialogFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
// Use the current time as the default values for the picker
        mTag = getArguments().getString("TAG");
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
// Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimePickerListener) {
            mListener = (OnTimePickerListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimePickerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
//        Toast.makeText(getActivity(), "You picked " + hourOfDay + ":" + minute
//                , Toast.LENGTH_LONG).show();
        String ampm = "am";
        if (hourOfDay > 12) {
            ampm = "pm";
            hourOfDay -= 12;
        }
        String sMinute = String.valueOf(minute);
        if (minute < 10) {
            sMinute = "0" + String.valueOf(minute);
        }
        String time = String.valueOf(hourOfDay) + ":" + sMinute + " " + ampm;
        mListener.onTimePicked(time, mTag);
    }
}
