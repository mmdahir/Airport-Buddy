package group11.tcss450.uw.edu.airportbuddy.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * This service calls an interface method that other classes can implement for what to do with this
 * service. Currently, it updates the dashboard.
 */
public class UpdateDashService extends IntentService {

    /**
     * Listener for when the service runs
     */
    private onServiceInteractionListener mListener;

    /**
     * Binder given to clients
     */
    private final IBinder mBinder = new LocalBinder();

    /**
     * 60 seconds - 1 minute is the minimum...
     */
    private static final int POLL_INTERVAL = 60_000;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        /**
         * Gets the service and returns it for binding
         * @return
         */
        public UpdateDashService getService() {
            // Return this instance of LocalService so clients can call public methods
            return UpdateDashService.this;
        }
    }

    /**
     * Constructor, sets the mListener to null
     */
    public UpdateDashService() {
        super("UpdateDashService");
        mListener = null;
    }

    /**
     * Returns binder to help bind the service with an activity
     * @param intent the intent of the service or activity
     * @return the binder
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * What happens when the service runs
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mListener.onServiceInteraction();
        }
    }

    /**
     * Sets the service alarm so the service and run at set interval
     * @param context
     * @param isOn if the service is on
     */
    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, UpdateDashService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
                    , 10
                    , POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    /**
     * Attaches the listener
     * @param theListener the listener being attached
     */
    public void setOnServiceInteractionListener(onServiceInteractionListener theListener) {
        mListener = theListener;
    }

    /**
     * Inner interface to let other classes have their own code run when the service runs
     */
    public interface onServiceInteractionListener {
        void onServiceInteraction();
    }
}
