package group11.tcss450.uw.edu.airportbuddy.menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds all the flights.
 * Uses Singleton design pattern.
 */
public class Flights {

    private static final Flights instance = new Flights();

    private static List<Flight> mListFlights  = new ArrayList<>();

    /**
     * Private constructor to avoid client use.
     */
    private void Flights(){}

    /**
     * Gives client an instance of this object.
     * @return the instance
     */
    public static Flights getInstance() {
        return instance;
    }

    /**
     * Add a flight.
     * @param flight the flight to add
     */
    public static void addFlight(Flight flight) {
        mListFlights.add(flight);
    }

    /**
     * Clears all local flight data.
     */
    public static void clearFlights() {
        mListFlights.clear();
    }

    /**
     * Get all flights.
     * @return list of flights
     */
    public static List<Flight> getFlights() {
        return mListFlights;
    }
}
