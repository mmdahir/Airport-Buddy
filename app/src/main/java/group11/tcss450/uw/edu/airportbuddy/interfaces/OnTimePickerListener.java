package group11.tcss450.uw.edu.airportbuddy.interfaces;


/**
 * Interface for TimePickerDialogs.
 */
public interface OnTimePickerListener {
    /**
     * Takes a time in hh:mm and a tag from the originator.
     *
     * @param time time in hh:mm
     * @param tag originator tag
     */
    public void onTimePicked(String time, String tag);
}
