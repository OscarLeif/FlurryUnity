package ata.plugins;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.flurry.android.FlurryConfig;
import com.flurry.android.FlurryConfigListener;
import com.unity3d.player.UnityPlayer;

import java.util.HashMap;
import java.util.Map;

import static android.util.Log.VERBOSE;

/**
 * Created by OscarLeif on 5/6/2017.
 * Update 10 April 2019
 */

public class FlurryAnalytics extends Fragment
{
    //Constants
    public static final String LOG_TAG = "Flurry Analytics";

    private String DEBUG_FLURRY_API_KEY = "NULL"; //Is set as empty the project crash
    private String GooglePlayStoreKey = "NULL";
    private String AmazonAppStoreKey = "NULL";
    private String SamsungGalaxyStoreKey = "NULL";

    //The name of the Unity game object that calls Flurry
    //Used for the Listener information form java side.
    private final static String UnityGameObjectName = "Flurry";

    //instance
    public static FlurryAnalytics instance;

    private FlurryPluginCallback unityCallbackReference;

    // Unity context.
    private String gameObjectName;

    //Remote Config
    private FlurryConfig mFlurryConfig;
    private FlurryConfigListener mFlurryConfigListener;

    public static void start(String DebugKey, String GooglePlayKey, String AmazonKey, String GalaxyKey, FlurryPluginCallback callback)
    {
        // Instantiate and add Unity Player Activity;
        if (instance == null)
        {
            instance = new FlurryAnalytics();
            instance.gameObjectName = "Flurry";
            instance.DEBUG_FLURRY_API_KEY = DebugKey;
            instance.AmazonAppStoreKey = AmazonKey;
            instance.SamsungGalaxyStoreKey = GalaxyKey;
            instance.GooglePlayStoreKey = GooglePlayKey;
            instance.unityCallbackReference = callback;

            Log.d(LOG_TAG, "start: Method Called");
            UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(instance, FlurryAnalytics.LOG_TAG).commit();
        }
        else
        {
            Toast.makeText(UnityPlayer.currentActivity, "Already initialize",Toast.LENGTH_LONG).show();
        }
    }

    //region Activity LifeCycle
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Map<String, String> consentStrings = new HashMap<>();
        //consentStrings.put("IAB", "yes");
        //By default debug app key is the default if the app is side loaded.

        final String getCurrentAppStore = this.returnCurrentStore();

        try
        {
            new FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .withCaptureUncaughtExceptions(true)
                    .withContinueSessionMillis(10000)
                    .withLogLevel(VERBOSE)
                    //.withConsent(new FlurryConsent(true, consentStrings)) //TODO check what is this for
                    .withListener(new FlurryAgentListener()
                    {
                        @Override
                        public void onSessionStarted()
                        {
                            unityCallbackReference.OnInitialize(true);
                            Log.d(LOG_TAG, "onSessionStarted: Flurry is working");
                            logEvent("Installer: " + returnCurrentStore());
                        }
                    })
                    .build(UnityPlayer.currentActivity, getCurrentAppStore);
        } catch(IllegalArgumentException e)
        {
            Log.e(LOG_TAG, "The API KEY Cannot be empty");
        }

        //get version name
        try
        {
            PackageInfo pInfo = UnityPlayer.currentActivity
                    .getPackageManager()
                    .getPackageInfo(UnityPlayer.currentActivity.getPackageName(), 0);
            String version = pInfo.versionName;
            FlurryAgent.setVersionName(version);
            Log.d(LOG_TAG, "onCreate: versionName: " + version);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        FlurryAgent.onStartSession(UnityPlayer.currentActivity);

        Log.d(LOG_TAG, "onCreate: Method Called");
        Log.d(LOG_TAG, "onCreate: KEY :" + DEBUG_FLURRY_API_KEY);

        // flurry config
        mFlurryConfig = FlurryConfig.getInstance();
        // Setup Flurry Config
        mFlurryConfigListener = new FlurryConfigListener()
        {
            @Override
            public void onFetchSuccess()
            {
                //Toast.makeText(UnityPlayer.currentActivity, "Fetch - Success", Toast.LENGTH_SHORT).show();
                mFlurryConfig.activateConfig();
            }

            @Override
            public void onFetchNoChange()
            {
                // Use the Config cached data if available
                //Toast.makeText(UnityPlayer.currentActivity, "Fetch - No Change", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFetchError(boolean isRetrying)
            {
                // Use the Config cached data if available
                //Toast.makeText(UnityPlayer.currentActivity, "Fetch - Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActivateComplete(boolean isCache)
            {
                FlurryAgent.logEvent("Remote Config Activated");
                String message = "Config Activated: " + (isCache ? "Cache" : "Fetch");
                //Toast.makeText(UnityPlayer.currentActivity, message, Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "on Activate Complete");
            }
        };
        mFlurryConfig.registerListener(mFlurryConfigListener);
        mFlurryConfig.fetchConfig();
    }

    private String returnCurrentStore()
    {
        String currentKey = this.DEBUG_FLURRY_API_KEY;
        if (this.getInstallerPackageName() != null && !this.getInstallerPackageName().equals(""))
        {
            if (this.getInstallerPackageName().equals(APPSTORE.GOOGLE_PLAY))
            {
                Log.v(LOG_TAG, "App store google");
                currentKey = instance.GooglePlayStoreKey;

            } else if (this.getInstallerPackageName().equals(APPSTORE.AMAZON_APPSTORE))
            {
                currentKey = instance.AmazonAppStoreKey;
                Log.v(LOG_TAG, "App store Amazon");
            } else if (this.getInstallerPackageName().equals(APPSTORE.GALAXY_APPSTORE))
            {
                currentKey = instance.SamsungGalaxyStoreKey;
                Log.v(LOG_TAG, "App store Galaxy");
            }
        } else
        {
            Log.v(LOG_TAG, "Side loaded app - Test Mode or installed from external file");
        }
        return currentKey;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(UnityPlayer.currentActivity);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mFlurryConfig.unregisterListener(mFlurryConfigListener);
    }

    //endregion

    //null - developer
    //com.android.vending - google play
    //com.amazon.venezia - amazon app
    //com.sec.android.app.samsungapps - samsung app store
    public String getInstallerPackageName()
    {
        String appstore = null;
        try
        {
            String installer =
                    UnityPlayer.currentActivity.
                            getPackageManager().
                            getInstallerPackageName(UnityPlayer.currentActivity.getPackageName());
            appstore = installer;
        }
        catch (Throwable e)
        {

        }
        return appstore;
    }

    /**
     * Logs an event for analytics.
     *
     * @param eventName   name of the event
     * @param eventParams event parameters (can be null)
     * @param timed       <code>true</code> if the event should be timed, false otherwise
     */
    public void logEvent(String eventName, Map<String, String> eventParams,boolean timed)
    {
        FlurryAgent.logEvent(eventName, eventParams, timed);
    }

    public void logEvent(String eventName)
    {
        FlurryAgent.logEvent(eventName);
    }

    public void logEvent(String eventName, boolean timed)
    {
        FlurryAgent.logEvent(eventName,timed);
    }

    /**
     * Ends a timed event that was previously started.
     *
     * @param eventName   name of the event
     * @param eventParams event parameters (can be null)
     */
    public void endTimedEvent(String eventName, Map<String, String> eventParams)
    {
        FlurryAgent.endTimedEvent(eventName, eventParams);
    }
    /**
     * Ends a timed event without event parameters.
     *
     * @param eventName name of the event
     */
    public void endTimedEvent(String eventName)
    {
        FlurryAgent.endTimedEvent(eventName);
    }
    /**
     * Logs an error.
     *
     * @param errorId          error ID
     * @param errorDescription error description
     * @param throwable        a {@link Throwable} that describes the error
     */
    public void logError(String errorId, String errorDescription, Throwable throwable)
    {
        FlurryAgent.onError(errorId, errorDescription, throwable);
    }
    /**
        * Logs location.
        *
        * @param latitude  latitude of location
     * @param longitude longitude of location
     */
    public void logLocation(double latitude, double longitude)
    {
        FlurryAgent.setLocation((float) latitude, (float) longitude);
    }

    /**
     * Logs page view counts.
     */
    public void logPageViews()
    {
        FlurryAgent.onPageView();
    }

    // region Remote Config

    /**
     * Fetch remote config from Flurry
     * When fetching is OK return remote values
     * if not it will always return default values
     * Check if mFlurryConfig is not null
     */
    public void fetchConfig()
    {
        if (mFlurryConfig != null)
        {
            mFlurryConfig.fetchConfig();
        } else
        {
            Log.d(LOG_TAG, "fetch config is null");
        }
    }

    public String getRemoteString(String key, String defaultValue)
    {
        return mFlurryConfig.getString(key, defaultValue);
    }

    public boolean getRemoteBool(String key, boolean defaultValue)
    {
        return mFlurryConfig.getBoolean(key, defaultValue);
    }

    public int getRemoteInt(String key, int defaultValue)
    {
        return mFlurryConfig.getInt(key, defaultValue);
    }

    public float getRemoteFloat(String key, float defaultValue)
    {
        return mFlurryConfig.getFloat(key, defaultValue);
    }

    public long getRemoteLong(String key, long defaultValue)
    {
        return mFlurryConfig.getLong(key, defaultValue);
    }
    //endregion

    // region Unity Utilities

    private void SendUnityMessage(String methodName, String parameter)
    {
        Log.i(LOG_TAG, LOG_TAG + "SendUnityMessage(`" + methodName + "`, `" + parameter + "`)");
        UnityPlayer.UnitySendMessage(gameObjectName, methodName, parameter);
    }

// endregion

    enum APPSTORE
    {
        GOOGLE_PLAY("com.android.vending"),
        AMAZON_APPSTORE("com.amazon.venezia"),
        GALAXY_APPSTORE("com.sec.android.app.samsungapps");

        private final String name;

        /**
         * @param name
         */
        APPSTORE(final String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }
}

interface FlurryPluginCallback
{
    public void OnInitialize(boolean isInit);
}



