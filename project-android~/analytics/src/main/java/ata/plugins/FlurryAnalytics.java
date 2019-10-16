package ata.plugins;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryConfig;
import com.flurry.android.FlurryConfigListener;

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
            instance.onCreate(null);
        } else
        {
            Toast.makeText(unityActivity, "Already initialize", Toast.LENGTH_LONG).show();
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
        //final Application application = UnityPlayer.currentActivity.getApplication();
        if (unityActivity == null)
        {
            return;
        }
        //final String installerName = unityActivity.getPackageManager().getInstallerPackageName(unityActivity.getPackageName());

        String FlurryKey = this.flurryKey;
        if(FlurryKey.isEmpty())
        {
            FlurryKey = "NULL";
        }
        try
        {
            Log.d(LOG_TAG,"Initialize FLurry Before Build");
            new FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .withCaptureUncaughtExceptions(true)
                    .withContinueSessionMillis(10000)
                    .withLogLevel(VERBOSE)
                    //.withConsent(new FlurryConsent(true, consentStrings)) //TODO check what is this for
                    .withListener(() -> {
                        unityActivity.runOnUiThread(() -> unityCallbackReference.onInitialize(true));
                        Log.d(LOG_TAG, "onSessionStarted: Flurry is working");
                        //logEvent("Installer: " + installerName == null ? "" : installerName);
                        Log.d(LOG_TAG, "Flurry Initialize");
                    })
                    .build(unityActivity, FlurryKey);
        } catch (IllegalArgumentException e)
        {
            Log.e(LOG_TAG, "The API KEY Cannot be empty");
        } catch (NullPointerException e)
        {
            Log.e(LOG_TAG, e.getMessage());
        }

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
        FlurryAgent.onStartSession(unityActivity);

        Log.d(LOG_TAG, "onCreate: Method Called");
        Log.d(LOG_TAG, "onCreate: KEY :" + flurryKey);

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
        setRetainInstance(true); // Retain between configuration changes (like device rotation)
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



