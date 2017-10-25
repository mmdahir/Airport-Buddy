package group11.tcss450.uw.edu.airportbuddy.com.yildizkabaran.twittersplash.view;

import android.content.Context;

import group11.tcss450.uw.edu.airportbuddy.R;


/**
 * Nothing but an ImageView with a preset image resource
 * @author yildizkabaran
 *
 */
public class ContentView extends android.support.v7.widget.AppCompatImageView {
  
  public ContentView(Context context){
    super(context);
//    initialize();
  }
  
  private void initialize(){
    // set the dummy content image here
    setImageResource(R.drawable.content);
  }
}
