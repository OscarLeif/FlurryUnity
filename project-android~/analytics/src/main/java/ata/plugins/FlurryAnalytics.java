package ata.plugins;

// Features.

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.Map;

// Unity.
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.flurry.android.FlurryConfig;
import com.flurry.android.FlurryConfigListener;
import com.unity3d.player.UnityPlayer;

// Debug.
import android.util.Log;


// Flurry

/**
 * Created by OscarLeif on 5/6/2017.
 * Update 14 August 2019
 */

public class FlurryAnalytics extends Fragment
{
    // Constants.
    public static final String TAG = "Flurry_Analytics_Frag";

    //Singleton instance.
    private static FlurryAnalytics instance;
    private FlurryCallback unityCallbackReference;

    private String FLURRY_API_KEY = "NULL";

    //Remote Config
    private FlurryConfig mFlurryConfig;
    private FlurryConfigListener mFlurryConfigListener;
    private boolean OnFetchSuccess = false;

    // Unity context.
    String gameObjectName;

    public static void start(String gameObjectName, String FlurryKey,FlurryCallback callback)
    {
        instance = new FlurryAnalytics();
        instance.FLURRY_API_KEY = FlurryKey;
        instance.gameObjectName = gameObjectName; // Store `GameObject` reference
        instance.unityCallbackReference = callback;
        UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(instance, FlurryAnalytics.TAG).commit();
    }

    //region Android LifeCycle

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // Retain between configuration changes (like device rotation)

        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .withCaptureUncaughtExceptions(true)
                .withContinueSessionMillis(10000)
                .withLogLevel(Log.VERBOSE)
                .withListener
                        (
                                new FlurryAgentListener()
                                {
                                    @Override
                                    public void onSessionStarted()
                                    {
                                        unityCallbackReference.onInitialize(true);
                                    }
                                }
                        )
                .build(UnityPlayer.currentActivity.getApplicationContext(), FLURRY_API_KEY);
        Log.d(TAG, "Flurry Initialize");
        PackageInfo pInfo = null;
        try
        {
            pInfo = UnityPlayer.currentActivity
                    .getPackageManager()
                    .getPackageInfo(UnityPlayer.currentActivity.getPackageName(), 0);
            String version = pInfo.versionName;
            FlurryAgent.setVersionName(version);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        // flurry config
        mFlurryConfig = FlurryConfig.getInstance();
        // Setup Flurry Config
        mFlurryConfigListener = new

                FlurryConfigListener()
                {
                    //Called after config data is successfully loaded from server.
                    @Override
                    public void onFetchSuccess()
                    {
                        //Toast.makeText(UnityPlayer.currentActivity, "Fetch - Success", Toast.LENGTH_SHORT).show();
                        mFlurryConfig.activateConfig();
                        Log.d(LOG_TAG, "Remote on fetch Success");
                        FlurryAnalytics.this.OnFetchSuccess = true;
                    }

                    //Called with a fetch completes but no changes from server.
                    @Override
                    public void onFetchNoChange()
                    {
                        // Use the Config cached data if available
                        //Toast.makeText(UnityPlayer.currentActivity, "Fetch - No Change", Toast.LENGTH_SHORT).show();
                        Log.d(LOG_TAG, "Remote on fetch no changes");
                        FlurryAnalytics.this.OnFetchSuccess = true;
                    }

                    //Called after config data is failed to load from server.
                    @Override
                    public void onFetchError(boolean isRetrying)
                    {
                        // Use the Config cached data if available
                        //Toast.makeText(UnityPlayer.currentActivity, "Fetch - Error", Toast.LENGTH_SHORT).show();
                        //FlurryAnalytics.instance.logEvent("Fetch Error");
                        FlurryAnalytics.this.OnFetchSuccess = false;
                        Log.d(LOG_TAG, "Remote on fetch error");
                    }

                    //Called after config data is activated.
                    @Override
                    public void onActivateComplete(boolean isCache)
                    {
                        //getConfigData();//Update remote values ?
                        FlurryAgent.logEvent("Remote Config Activated");
                        String message = "Config Activated: " + (isCache ? "Cache" : "Fetch");
                        //Toast.makeText(UnityPlayer.currentActivity, message, Toast.LENGTH_SHORT).show();
                        Log.d(LOG_TAG, "Remote on Activate Complete");
                    }
                }
        ;
        mFlurryConfig.registerListener(mFlurryConfigListener);
        mFlurryConfig.fetchConfig();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    //endregion

    public static final String LOG_TAG = "Flurry Analytics";

    /**
     * Logs an event for analytics.
     *
     * @param eventName   name of the event
     * @param eventParams event parameters (can be null)
     * @param timed       <code>true</code> if the event should be timed, false otherwise
     */

    //endegion
    public void logEvent(String eventName, Map<String, String> eventParams, boolean timed)
    {
        FlurryAgent.logEvent(eventName, eventParams, timed);
    }

    public void logEvent(String eventName)
    {
        FlurryAgent.logEvent(eventName);
    }

    public void logEvent(String eventName, boolean timed)
    {
        FlurryAgent.logEvent(eventName, timed);
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
        if (this.OnFetchSuccess)
        {
            return mFlurryConfig.getString(key, defaultValue);
        } else
        {
            return defaultValue;
        }
    }

    public boolean getRemoteBool(String key, boolean defaultValue)
    {
        if (this.OnFetchSuccess)
        {
            return mFlurryConfig.getBoolean(key, defaultValue);
        } else
        {
            return defaultValue;
        }
    }

    public int getRemoteInt(String key, int defaultValue)
    {
        if (this.OnFetchSuccess)
        {
            return mFlurryConfig.getInt(key, defaultValue);
        } else
        {
            return defaultValue;
        }
    }

    public float getRemoteFloat(String key, float defaultValue)
    {
        if (this.OnFetchSuccess)
        {
            return mFlurryConfig.getFloat(key, defaultValue);
        } else
        {
            return defaultValue;
        }
    }

    public long getRemoteLong(String key, long defaultValue)
    {
        if (this.OnFetchSuccess)
        {
            return mFlurryConfig.getLong(key, defaultValue);
        } else
        {
            return defaultValue;
        }
    }
    //endregion

    // region Unity Utilities

    private void SendUnityMessage(String methodName, String parameter)
    {
        Log.i(LOG_TAG, LOG_TAG + "SendUnityMessage(`" + methodName + "`, `" + parameter + "`)");
        UnityPlayer.UnitySendMessage(gameObjectName, methodName, parameter);
    }

    // endregion

    public static class AndroidAppStoresID
    {
        public static final String GOOGLE_PLAY = "com.android.vending";
        public static final String AMAZON_APP_STORE = "com.amazon.venecia";
        public static final String GALAXY_APP_STORE = "com.sec.android.app.samsungapps";
    }
}

interface FlurryCallback
{
    void onInitialize(boolean isInit);
}