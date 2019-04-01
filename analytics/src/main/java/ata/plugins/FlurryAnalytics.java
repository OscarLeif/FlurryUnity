package ata.plugins;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

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
 * Update 2019
 */

public class FlurryAnalytics extends Fragment
{
    //Constants
    public static final String LOG_TAG = "Flurry Analytics";

    private String DEBUG_FLURRY_API_KEY = "NULL"; //Is set as empty the project crash
    private String GooglePlayStoreKey= "NULL";
    private String AmazonAppStoreKey="NULL";
    private String SamsungGalaxyStoreKey="NULL";



    //The name of the Unity game object that calls Flurry
    //Used for the Listener information form java side.
    private final static String UnityGameObjectName = "Flurry";

    //instance instance
    public static FlurryAnalytics instance;

    // Unity context.
    private String gameObjectName;

    //Remote Config
    private FlurryConfig mFlurryConfig;
    private FlurryConfigListener mFlurryConfigListener;

    public static void start(String flurryKey)
    {
        // Instantiate and add Unity Player Activity;
        instance = new FlurryAnalytics();
        instance.gameObjectName = "Flurry";
        instance.DEBUG_FLURRY_API_KEY = flurryKey;
        Log.d(LOG_TAG, "start: Method Called");
        UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(instance, FlurryAnalytics.LOG_TAG).commit();
    }

    public static void start(String DebugKey, String GooglePlayKey, String AmazonKey, String GalaxyKey)
    {
        // Instantiate and add Unity Player Activity;
        instance = new FlurryAnalytics();
        instance.gameObjectName = "Flurry";
        instance.DEBUG_FLURRY_API_KEY = DebugKey;
        instance.AmazonAppStoreKey = AmazonKey;
        instance.SamsungGalaxyStoreKey = GalaxyKey;
        instance.GooglePlayStoreKey = GooglePlayKey;
        Log.d(LOG_TAG, "start: Method Called");
        UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(instance, FlurryAnalytics.LOG_TAG).commit();
    }

    //region Activity LifeCycle
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Map<String, String> consentStrings = new HashMap<>();
        //consentStrings.put("IAB", "yes");
        super.onCreate(savedInstanceState);

        if(UnityPlayer.currentActivity == null)
        {
            Log.d(LOG_TAG, "Warning Current Activity is null");
        }

        //By default debug app key is the default if the app is sideloaded.
        String getCurrentAppStore= DEBUG_FLURRY_API_KEY;
        if(this.getInstallerPackageName()!=null && !this.getInstallerPackageName().equals(""))
        {
            if(this.getInstallerPackageName().equals(APPSTORE.GOOGLE_PLAY))
            {
                getCurrentAppStore = instance.GooglePlayStoreKey;
            }
            else if(this.getInstallerPackageName().equals(APPSTORE.AMAZON_APPSTORE))
            {
                getCurrentAppStore = instance.AmazonAppStoreKey;
            }
            else if(this.getInstallerPackageName().equals(APPSTORE.GALAXY_APPSTORE))
            {
                getCurrentAppStore = instance.SamsungGalaxyStoreKey;
            }
        }

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
                        Log.d(LOG_TAG, "onSessionStarted: Flurry is working");
                    }
                })
                .build(UnityPlayer.currentActivity, getCurrentAppStore);
        //get version name
        try
        {
            PackageInfo pInfo = UnityPlayer.currentActivity
                    .getPackageManager()
                    .getPackageInfo(UnityPlayer.currentActivity.getPackageName(), 0);
            String version = pInfo.versionName;
            FlurryAgent.setVersionName(version);
            Log.d(LOG_TAG, "onCreate: versionName: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        FlurryAgent.onStartSession(UnityPlayer.currentActivity);
        Log.d(LOG_TAG, "onCreate: Method Called");
        Log.d(LOG_TAG, "onCreate: KEY :" + DEBUG_FLURRY_API_KEY);

        // flurry config
        mFlurryConfig = FlurryConfig.getInstance();
        // Setup Flurry Config
        mFlurryConfigListener = new FlurryConfigListener() {
            @Override
            public void onFetchSuccess() {
                //Toast.makeText(UnityPlayer.currentActivity, "Fetch - Success", Toast.LENGTH_SHORT).show();
                mFlurryConfig.activateConfig();
            }

            @Override
            public void onFetchNoChange() {
                // Use the Config cached data if available
                //Toast.makeText(UnityPlayer.currentActivity, "Fetch - No Change", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFetchError(boolean isRetrying) {
                // Use the Config cached data if available
                //Toast.makeText(UnityPlayer.currentActivity, "Fetch - Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActivateComplete(boolean isCache) {
                FlurryAgent.logEvent("Remote Config Activated");
                String message = "Config Activated: " + (isCache ? "Cache" : "Fetch");
                //Toast.makeText(UnityPlayer.currentActivity, message, Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG,"on Activate Complete");
            }
        };
        mFlurryConfig.registerListener(mFlurryConfigListener);
        mFlurryConfig.fetchConfig();
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

    public String getInstallerPackageName()
    {
        String appstore = "";
        try
        {
            String installer = UnityPlayer.currentActivity.
                    getPackageManager().getInstallerPackageName(UnityPlayer.currentActivity.getPackageName());
            appstore = installer;
        }
        catch (Throwable e)
        {

        }
        return appstore;
    }


    public void logEvent(String eventName)
    {
        FlurryAgent.logEvent(eventName);
    }

    public void logEvent(String eventName, boolean timed)
    {
        FlurryAgent.logEvent(eventName,timed);
    }

    public void logEvent(String eventName, Map<String, String> eventParams, boolean timed )
    {
        FlurryAgent.logEvent(eventName, eventParams, timed);
    }

    public void endTimedEvent(String eventName)
    {
        FlurryAgent.endTimedEvent(eventName);
    }

    public void endTimedEvent(String eventName, Map<String,String> eventParams)
    {
        FlurryAgent.endTimedEvent(eventName,eventParams);
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
        if(mFlurryConfig!=null)
        {
            mFlurryConfig.fetchConfig();
        }
        else
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
        Log.i(LOG_TAG, LOG_TAG +"SendUnityMessage(`"+methodName+"`, `"+parameter+"`)");
        UnityPlayer.UnitySendMessage(gameObjectName, methodName, parameter);
    }

// endregion

    public enum APPSTORE
    {
        GOOGLE_PLAY("com.android.vending"),
        AMAZON_APPSTORE("com.amazon.venezia"),
        GALAXY_APPSTORE("com.sec.android.app.samsungapps");

        private final String name;

        /**
         * @param name
         */
        private APPSTORE(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
