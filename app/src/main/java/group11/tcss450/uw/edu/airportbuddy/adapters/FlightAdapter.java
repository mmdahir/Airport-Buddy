package group11.tcss450.uw.edu.airportbuddy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import group11.tcss450.uw.edu.airportbuddy.interfaces.RecyclerViewClickListener;
import group11.tcss450.uw.edu.airportbuddy.menu.Flight;
import group11.tcss450.uw.edu.airportbuddy.R;

/**
 * An adapter for recycler views using data from the Flight class.
 */
public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.ViewHolder> {

    private List<Flight> listFlights;
    private Context context;
    private RecyclerViewClickListener mItemListener;

    /**
     * Constructor.
     * @param listFlights a list of flights
     * @param context context.
     */
    public FlightAdapter(List<Flight> listFlights, Context context, RecyclerViewClickListener listener) {
        this.listFlights = listFlights;
        this.context = context;
        this.mItemListener = listener;
    }

    /**
     * Inflates the view on creation.
     *
     * @param parent parent
     * @param viewType viewType
     * @return view
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flight_cardview, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Binds data to the view holder.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Flight flight = listFlights.get(position);
        holder.mFlightNumber.setText(context.getString(R.string.flight_number) + ": "
                + flight.getFlightNumber());
        holder.mGateNumber.setText(context.getString(R.string.gate_number) + ": "
                + flight.getGateNumber());
        holder.mBoarding.setText(context.getString(R.string.saved_boarding_time) + ": "
                + formatTime(flight.getBoarding().getTime()));
        holder.mDeparture.setText(context.getString(R.string.departure_time) + ": "
                + formatTime(flight.getDeparture().getTime()));
        holder.mArrival.setText(context.getString(R.string.arrival_time) + ": "
                + formatTime(flight.getArrival().getTime()));
        holder.mLocationFrom.setText(context.getString(R.string.departing_from) + ": "
                + flight.getLocationFrom());
        holder.mLocationTo.setText(context.getString(R.string.landing_at) + ": "
                + flight.getLocationTo());

    }

    /**
     * Formats a long time to a 12 hours format.
     * Uses the format "hh:mm a"
     *
     * @param time time to format
     * @return formatted time "hh:mm a"
     */
    private String formatTime(long time) {
        Date dt = new Date(time);
        DateFormat format = new SimpleDateFormat("hh:mm a");
        return format.format(dt);
    }

    @Override
    public int getItemCount() {
        return listFlights.size();
    }

    /**
     * Connects to the textviews in the view.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        public TextView mText;
        public TextView mFlightNumber;
        public TextView mGateNumber;
        public TextView mDeparture;
        public TextView mArrival;
        public TextView mBoarding;
        public TextView mLocationTo;
        public TextView mLocationFrom;

        /**
         * Creates the view.
         * @param itemView the view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            mFlightNumber = (TextView) itemView.findViewById(R.id.flight_number);
            mGateNumber = (TextView) itemView.findViewById(R.id.gate_number);
            mDeparture = (TextView) itemView.findViewById(R.id.departure);
            mArrival = (TextView) itemView.findViewById(R.id.arrival);
            mBoarding = (TextView) itemView.findViewById(R.id.boarding);
            mLocationTo = (TextView) itemView.findViewById(R.id.location_to);
            mLocationFrom = (TextView) itemView.findViewById(R.id.location_from);
            ((Button) itemView.findViewById(R.id.load_button)).setOnClickListener(this);
            ((Button) itemView.findViewById(R.id.remove_button)).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (context.getString(R.string.load).equals(((Button)v).getText().toString())) {
                mItemListener.recyclerViewLoadClicked(listFlights.get(this.getLayoutPosition()));
            } else if (context.getString(R.string.remove).equals(((Button)v).getText().toString())) {
                mItemListener.recyclerViewRemoveClicked(listFlights.get(this.getLayoutPosition()));
            }
        }
    }
}
