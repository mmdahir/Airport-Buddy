package group11.tcss450.uw.edu.airportbuddy.interfaces;

import group11.tcss450.uw.edu.airportbuddy.menu.Flight;


/**
 * Interface for when saved flights is interacted with.
 */
public interface OnSavedFlightInteractionListener {
    /**
     * A flight is being loaded to the dash.
     *
     * @param flight flight to load to dash
     */
    public void onSavedFlightInteraction(Flight flight);
}
