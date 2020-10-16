package ata.plugins;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.flurry.android.FlurryConfig;
import com.flurry.android.FlurryConfigListener;
import com.flurry.android.FlurryPerformance;

import java.util.Map;

/**
 * Created by OscarLeif on 5/6/2017.
 * Update 16 October 2020
 */

public class FlurryAnalytics extends Fragment
{
    //Constants
    public static final String LOG_TAG = "Flurry Analytics";

    private Activity unityActivity;
    private String flurryKey = "NULL"; //Is set as empty the project crash


    //The name of the Unity game object that calls Flurry
    //Used for the Listener information form java side.
    private final static String UnityGameObjectName = "Flurry";

    //instance
    public static FlurryAnalytics instance;

    private FlurryCallback unityCallbackReference;

    // Unity context.
    private String gameObjectName;

    //Remote Config
    private FlurryConfig mFlurryConfig;
    private FlurryConfigListener mFlurryConfigListener;
    private boolean OnFetchSuccess = false;

    private boolean Initialize = false;

    public static void start(Activity unityActivity, String flurryKey, FlurryCallback callback)
    {
        // Instantiate and add Unity Player Activity;
        if (instance == null)
        {
            instance = new FlurryAnalytics();
            instance.unityActivity = unityActivity;
            instance.gameObjectName = "Flurry";
            instance.flurryKey = flurryKey;
            instance.unityCallbackReference = callback;

            Log.d(LOG_TAG, "start: Method Called");

            unityActivity.getFragmentManager().beginTransaction().add(instance, FlurryAnalytics.LOG_TAG).commit();
            instance.InitializeFlurryOnce();
            //instance.onCreate(null);
        }
    }

    public void InitializeFlurryOnce()
    {
        new FlurryAgent.Builder()
                .withDataSaleOptOut(false) //CCPA - the default value is false
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(Log.VERBOSE)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .withLogEnabled(true)
                .withListener(new FlurryAgentListener() {
                    @Override
                    public void onSessionStarted() {
                        // Session handling code
                        Log.v(LOG_TAG, "Oscar The Plugin is working");
                        instance.unityCallbackReference.onInitialize(true);
                        instance.Initialize = true;
                    }
                })
                .withSessionForceStart(true)// Issue solved, If cannot start on Application this should be used
                .build(unityActivity.getBaseContext(), instance.flurryKey);

        //get version name
        try
        {
            PackageInfo pInfo = unityActivity
                    .getPackageManager()
                    .getPackageInfo(unityActivity.getPackageName(), 0);
            String version = pInfo.versionName;
            FlurryAgent.setVersionName(version);
            Log.d(LOG_TAG, "onCreate: versionName: " + version);
        } catch (
                PackageManager.NameNotFoundException e)
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
                        FlurryAnalytics.instance.logEvent("Fetch Error");
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


    //region Activity LifeCycle
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
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

    @Override
    public void onStop()
    {
        super.onStop();
        if(unityActivity!=null)
        {
            FlurryAgent.onEndSession(unityActivity);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(unityActivity!=null)
        {
            mFlurryConfig.unregisterListener(mFlurryConfigListener);
        }
    }

    //endregion

    /**
     * Logs an event for analytics.
     *
     * @param eventName   name of the event
     * @param eventParams event parameters (can be null)
     * @param timed       <code>true</code> if the event should be timed, false otherwise
     */
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

    //Since we don't use UnityActivity this cannot be done
    private void SendUnityMessage(String methodName, String parameter)
    {
        Log.i(LOG_TAG, LOG_TAG + "SendUnityMessage(`" + methodName + "`, `" + parameter + "`)");
        //UnityPlayer.UnitySendMessage(gameObjectName, methodName, parameter);
    }

    // endregion
}

interface FlurryCallback
{
    void onInitialize(boolean isInit);
}



