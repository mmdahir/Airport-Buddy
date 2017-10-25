package group11.tcss450.uw.edu.airportbuddy.interfaces;

/**
 * Created by matthewwu on 5/9/17.
 */

public interface OnDashboardInteractionListener {
    void OnInteractionListener(String switchCase, String gate, String boarding, String airportCode, String airportETA, String earliness);
}
