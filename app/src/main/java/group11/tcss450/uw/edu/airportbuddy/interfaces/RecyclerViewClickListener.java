package group11.tcss450.uw.edu.airportbuddy.interfaces;

import group11.tcss450.uw.edu.airportbuddy.menu.Flight;

/**
 * Created by Brian on 5/29/2017.
 */

public interface RecyclerViewClickListener {
    /**
     * Event for when load button is clicked
     *
     * @param flight flight to load
     */
    public void recyclerViewLoadClicked(Flight flight);

    /**
     * Event for when remove button is clicked.
     *
     * @param flight flight to remove
     */
    public void recyclerViewRemoveClicked(Flight flight);
}
