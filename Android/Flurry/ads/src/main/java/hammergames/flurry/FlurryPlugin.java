package hammergames.flurry;

import android.app.Activity;
import android.util.Log;


//import com.unity3d.player.UnityPlayer;
//import com.unity3d.player.UnityPlayerActivity;

/**
 * Created by OscarLeif on 5/6/2017.
 */

public class FlurryPlugin {
    public static String tag = "FlurryPlugin";

    //public static String UnityObjName = "TwimlerAdRotator";
    public static String UnityObjName = "AmazonAds";

    private static final FlurryPlugin instance = new FlurryPlugin();

    private Activity activity;

    //private com.amazon.device.ads.InterstitialAd interstitialAd;

    private boolean IsInitialized = false;

    public static boolean interstitialAdLoaded = false;

    //private AdLayout adView = null;

    // Get instance of the AdRotator
    public static FlurryPlugin getInstance() {
        //FlurryPlugin.instance.activity = UnityPlayer.currentActivity;

        Log.d(tag, "Amazon Ads Plugin instantiated.");

        return FlurryPlugin.instance;
    }

    // Initialize Amazon Ads
    public void init(String appKey, boolean testMode) {
        Log.d(tag, "Initializing Amazon Ads plugin.");
        //AdRegistration.enableTesting( testMode );
        //AdRegistration.enableLogging( true );
        //AdRegistration.setAppKey( appKey );

        IsInitialized = true;
    }

    // Create a Banner
    public void createBanner(final String position) {
        /*// Check if the plugin is initialized
        if ( ! IsInitialized )
        {
            Log.d ( tag , "Amazon Ad plugin is not initialized yet!");

            return;
        }

        final FlurryPlugin self = this;

        // Create the ad view just once
        if ( adView == null )
        {
            // Run the thread on Unity activity
            activity.runOnUiThread (
                    new Runnable() {
                        public void run()
                        {
                            adView = new AdLayout( activity , AdSize.SIZE_AUTO);
                            adView.setListener( self );

                            LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                                    LayoutParams.WRAP_CONTENT , getGravity( position ) );

                            activity.addContentView( adView, layoutParams );
                            adView.bringToFront();
                            AdTargetingOptions adOptions = new AdTargetingOptions();
                            adView.loadAd(adOptions);
                        }
                    });
        }
        else
        {
            refresh();
        }*/
    }

    // Refresh banner for a new ad request
    public void refresh() {
        /*if ( adView != null )
        {
            Log.d ( tag , "Refreshing Amazon Ad banner.");

            // Run the thread on Unity activity
            activity.runOnUiThread (
                    new Runnable() {
                        public void run()
                        {
                            AdTargetingOptions adOptions = new AdTargetingOptions();
                            adView.loadAd(adOptions);
                        }
                    });
        }
        else
        {
            Log.d ( tag , "Amazon Ad plugin is not initialized yet!");
        }*/
    }

    // Hide the banner
    public void hideBanner(final boolean hide) {
        // Return if there is no ad view
       /* if ( adView == null )
        {
            return;
        }

        // Run the thread on Unity activity
        activity.runOnUiThread (
                new Runnable() {
                    public void run()
                    {
                        if ( hide )
                        {
                            adView.setVisibility( View.GONE );
                        }
                        else
                        {
                            adView.setVisibility( View.VISIBLE );
                        }
                    }
                });*/
    }

    // Destroy the banner ad view
    public void destroyBanner() {
        /*if ( adView != null )
        {
            // Run the thread on Unity activity
            activity.runOnUiThread (
                    new Runnable() {
                        public void run()
                        {
                            adView.destroy();

                            adView = null;
                        }
                    });
        }*/
    }

    // Request interstitials
    public void requestInterstital() {
        /*activity.runOnUiThread( new Runnable()
        {
            @Override public void run()
            {
                boolean shouldRequest = true;

                if ( interstitialAd != null )
                {
                    if ( interstitialAd.isLoading() )
                    {
                        shouldRequest = false;
                        Log.d(tag,"Amazon Interstitials is Loading");
                    }

                    if ( FlurryPlugin.interstitialAdLoaded )
                    {
                        shouldRequest = false;
                        Log.d(tag,"Amazon Interstitials is loaded. Should be ready");
                    }
                }

                if ( shouldRequest )
                {

                    Log.d ( tag , "Requesting Amazon Interstitials");

                    interstitialAd = new com.amazon.device.ads.InterstitialAd( activity  );

                    interstitialAd.setListener( new InterstitialsAdListener() );

                    AdTargetingOptions adOptions = new AdTargetingOptions();

                    interstitialAd.loadAd( adOptions );
                }
            }
        });*/

    }

    // Show interstitial ad if its loaded
    public void showInterstitial() {
        /*activity.runOnUiThread( new Runnable()
        {
            @Override public void run()
            {
                if ( interstitialAd != null )
                {
                    if ( FlurryPlugin.interstitialAdLoaded )
                    {
                        interstitialAd.showAd();

                        FlurryPlugin.interstitialAdLoaded = false;
                    }
                }
            }
        });*/

    }

    // Get gravity of the banner based on the position
    private int getGravity(String position) {
        /*int gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        // Top Left
        if ( position.equals( "TL") )
        {
            gravity = Gravity.TOP | Gravity.LEFT;
        }

        // Top Middle
        if ( position.equals( "TM") )
        {
            gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        }

        // Top Right
        if ( position.equals( "TR") )
        {
            gravity = Gravity.TOP | Gravity.RIGHT;
        }

        // Bottom Left
        if ( position.equals( "BL") )
        {
            gravity = Gravity.BOTTOM | Gravity.LEFT;
        }

        // Bottom Left
        if ( position.equals( "BM") )
        {
            gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        }

        // Bottom Right
        if ( position.equals( "BR") )
        {
            gravity = Gravity.BOTTOM | Gravity.RIGHT;
        }

        return gravity;*/
        return 0;
    }

    /**
     * This event is called when an interstitial ad has been dismissed by the user.
     */

    public void sendMessageToUnity() {
        //UnityPlayer.UnitySendMessage( UnityObjName, "OnAdEvent", "amazon-interstitial-dismiss");

    }
}
