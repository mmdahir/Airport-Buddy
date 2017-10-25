package group11.tcss450.uw.edu.airportbuddy.menu;

import java.sql.Time;

/**
 * A class that holds the data for each flight.
 */
public class Flight {

    private String flightNumber;
    private int gateNumber;
    private Time departure;
    private Time arrival;
    private Time boarding;
    private String locationTo;
    private String locationFrom;

    /**
     * Constructs a flight data object.
     *
     * @param flightNumber flight number
     * @param gateNumber gate number
     * @param departure departure time
     * @param arrival arrival time
     * @param boarding boarding time
     * @param locationTo location to
     * @param locationFrom location from
     */
    public Flight(String flightNumber, int gateNumber, Time departure, Time arrival, Time boarding, String locationTo,
                  String locationFrom) {
        this.flightNumber = flightNumber;
        this.gateNumber = gateNumber;
        this.departure = departure;
        this.arrival = arrival;
        this.boarding = boarding;
        this.locationTo = locationTo;
        this.locationFrom = locationFrom;
    }

    /**
     * Converts the flight into a human readable string.
     * @return flight as a human readable string
     */
    @Override
    public String toString() {
        return "FlightNumber: " + flightNumber + ", GateNumber: " + gateNumber
                + ", Departure: " + departure + ", Arrival: " + arrival
                + ", Boarding: " + boarding + ", Location To: " + locationTo
                + ", Location From: " + locationFrom;
    }

    private void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    private void setDeparture(Time departure) {
        this.departure = departure;
    }

    private void setArrival(Time arrival) {
        this.arrival = arrival;
    }

    private void setBoarding(Time boarding) {
        this.boarding = boarding;
    }

    private void setLocationTo(String locationTo) {
        this.locationTo = locationTo;
    }

    private void setLocationFrom(String locationFrom) {
        this.locationFrom = locationFrom;
    }

    public int getGateNumber() {
        return gateNumber;
    }

    public void setGateNumber(int gateNumber) {
        this.gateNumber = gateNumber;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public Time getDeparture() {
        return departure;
    }

    public Time getArrival() {
        return arrival;
    }

    public Time getBoarding() {
        return boarding;
    }

    public String getLocationTo() {
        return locationTo;
    }

    public String getLocationFrom() {
        return locationFrom;
    }
}
