package group11.tcss450.uw.edu.airportbuddy.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

import group11.tcss450.uw.edu.airportbuddy.BuildConfig;
import group11.tcss450.uw.edu.airportbuddy.com.yildizkabaran.twittersplash.view.ContentView;
import group11.tcss450.uw.edu.airportbuddy.com.yildizkabaran.twittersplash.view.MainView;
import group11.tcss450.uw.edu.airportbuddy.com.yildizkabaran.twittersplash.view.SplashView;

import group11.tcss450.uw.edu.airportbuddy.R;
import group11.tcss450.uw.edu.airportbuddy.interfaces.OnStartInteractionListener;
import group11.tcss450.uw.edu.airportbuddy.startFragments.HomeFragment;
import group11.tcss450.uw.edu.airportbuddy.startFragments.LoginFragment;
import group11.tcss450.uw.edu.airportbuddy.startFragments.RegisterFragment;

/**
 * The Main Activity for login/register activities. Handles interactions via interfaces.
 */
public class StartActivity extends AppCompatActivity implements OnStartInteractionListener{

    public static final String EXTRA_MESSAGE = "Username";

    public static final String TAG = "Main Activity";

    private static final boolean DO_XML = false;

    private boolean mLoggedOut = false;

    private Bundle mSavedInstanceState;

    private ViewGroup mMainView;
    private SplashView mSplashView;
    private View mContentView;
    private Handler mHandler = new Handler();

    /**
     * Initializing and setting up fragment.
     * @param savedInstanceState Previous instance if there are any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSavedInstanceState = savedInstanceState;

        // create and customize the view
        SplashView splashView = new SplashView(getApplicationContext());
        // the animation will last 0.5 seconds
        splashView.setDuration(500);
        // transparent hole will look white before the animation
        splashView.setHoleFillColor(Color.WHITE);
        // this is the Twitter blue color
        splashView.setIconColor(Color.rgb(23, 169, 229));
        // a Twitter icon with transparent hole in it
        splashView.setIconResource(R.drawable.ic_launcher);
        // remove the SplashView from MainView once animation is completed
        splashView.setRemoveFromParentOnEnd(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            mLoggedOut = extras.getBoolean(getString(R.string.logout));

        // change the DO_XML variable to switch between code and xml
        if(DO_XML){
            // inflate the view from XML and then get a reference to it
            setContentView(R.layout.activity_main);
            mMainView = (ViewGroup) findViewById(R.id.main_view);
            mSplashView = (SplashView) findViewById(R.id.splash_view);
        } else {
            // create the main view and it will handle the rest
            mMainView = new MainView(getApplicationContext());
            mSplashView = ((MainView) mMainView).getSplashView();
            setContentView(mMainView);
        }
        // pretend like we are loading data
        startLoadingData();

    }

    private void startLoadingData(){
        // finish "loading data" in a random time between 1 and 3 seconds
        Random random = new Random();
        mHandler.postDelayed(new Runnable(){
            @Override
            public void run(){
                onLoadingDataEnded();
            }
        }, 1000 + random.nextInt(2000));
    }

    private void onLoadingDataEnded(){
        Context context = getApplicationContext();
        // now that our data is loaded we can initialize the content view
        mContentView = new ContentView(context);
        // add the content view to the background
        mMainView.addView(mContentView, 0);

        // start the splash animation
        mSplashView.splashAndDisappear(new SplashView.ISplashListener(){
            @Override
            public void onStart(){
                // log the animation start event
                if(BuildConfig.DEBUG){
                    Log.d(TAG, "splash started");
                }
            }

            @Override
            public void onUpdate(float completionFraction){
                // log animation update events
                if(BuildConfig.DEBUG){
                    Log.d(TAG, "splash at " + String.format("%.2f", (completionFraction * 100)) + "%");
                }
            }

            @Override
            public void onEnd(){
                // log the animation end event
                if(BuildConfig.DEBUG){
                    Log.d(TAG, "splash ended");
                }
                // free the view so that it turns into garbage
                mSplashView = null;
                if(!DO_XML){
                    // if inflating from code we will also have to free the reference in MainView as well
                    // otherwise we will leak the View, this could be done better but so far it will suffice
                    ((MainView) mMainView).unsetSplashView();
                }

                SharedPreferences sharedPref = getSharedPreferences("PREF", Context.MODE_PRIVATE);

                if (mLoggedOut) {
                    Log.d(TAG, "Logout Worked!");
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.remove(getString(R.string.saved_username));
                    editor.remove(getString(R.string.saved_password));
                    editor.apply();
                }

                String username = sharedPref.getString(getString(R.string.saved_username), "null");
                String password = sharedPref.getString(getString(R.string.saved_password), "null");

                if (username.equals("null") && password.equals("null")) {
                    setContentView(R.layout.activity_start);
                }

                if (mSavedInstanceState == null) {
                    if (findViewById(R.id.fragmentContainer) != null) {
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.fragmentContainer, new HomeFragment())
                                .commit();
                    }

                    if (!username.equals("null") && !password.equals("null")) {
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(EXTRA_MESSAGE, username);
                        startActivity(intent);
                    }
                }

            }
        });
    }

    @Override
    public void OnInteractionListener(String switchCase, String startOptions,
                                      String response, String username, String password) {
        FragmentTransaction transaction;
        Log.d(TAG, "inside onInteraction method.");
        Log.d(TAG, "Switch Case: " + switchCase);
        Log.d(TAG, "Response: " + response);
        switch(switchCase) {
            case "launch":
                Log.d(TAG, "inside launch case");
                switch(startOptions) {
                    case "login":
                        Log.d(TAG, "inside login case in launch case");
                        LoginFragment loginFragment;
                        loginFragment = new LoginFragment();

                        transaction = getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter_right, R.anim.exit_left)
                                .replace(R.id.fragmentContainer, loginFragment)
                                .addToBackStack(null);
                        transaction.commit();
                        break;
                    case "register":
                        Log.d(TAG, "inside register case in launch case");
                        RegisterFragment registerFragment;
                        registerFragment = new RegisterFragment();
                        transaction = getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter_left, R.anim.exit_right)
                                .replace(R.id.fragmentContainer, registerFragment)
                                .addToBackStack(null);
                        transaction.commit();
                }
                break;
            case "login":
                Log.d(TAG, "inside login case");
                switch(response) {
                    case "Successful login":
                        Log.d(TAG, "inside successful login case in login");
                        //onRegisterFragmentInteraction(response, username, password);
                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(EXTRA_MESSAGE, username);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_below, R.anim.exit_above);
                        SharedPreferences sharedPref;
                        sharedPref = getSharedPreferences("PREF", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.saved_username), username);
                        editor.putString(getString(R.string.saved_password), password);
                        editor.commit();
                }
                break;
            case "register":
                Log.d(TAG, "inside register case");
                switch(response) {
                    case "Successful Register":
                        Log.d(TAG, "inside successful register case");
                        //        Intent intent = new Intent(this, DashboardActivity.class);
                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(EXTRA_MESSAGE, username);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_below, R.anim.exit_above);
                        Log.d(TAG, "" + this);
                        SharedPreferences sharedPref;
                        sharedPref = getSharedPreferences("PREF", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.saved_username), username);
                        editor.putString(getString(R.string.saved_password), password);
                        editor.commit();
                }
        }
    }
}
