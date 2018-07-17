package ata.plugins;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.unity3d.player.UnityPlayer;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import static android.util.Log.VERBOSE;

/**
 * Created by OscarLeif on 5/6/2017.
 */

public class FlurryAnalytics extends Fragment
{
    // Constants
    public static final String TAG="Flurry_Analytics";

    //Singleton instance
    public static FlurryAnalytics instance;

    //Unity context

    private String gameObjectName;

    //flurry fields

    private String flurryKey;

    private boolean isTestMode;

    private String versionName;

    public static void start(String gameObjectName, String flurryKey, boolean testMode)
    {
        // Instantiate and add to Unity Player Activity.
        instance = new FlurryAnalytics();
        instance.gameObjectName = gameObjectName;
        instance.flurryKey = flurryKey;
        UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(instance,FlurryAnalytics.TAG).commit();
    }

    //region Fragment lifeCycle

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .withCaptureUncaughtExceptions(true)
                .withContinueSessionMillis(10000)
                .withLogLevel(VERBOSE).build(UnityPlayer.currentActivity, this.flurryKey);

        PackageInfo pInfo;
        try
        {
            pInfo = UnityPlayer.currentActivity.getPackageManager().getPackageInfo(UnityPlayer.currentActivity.getPackageName(), 0);
            versionName = pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        FlurryAgent.setVersionName(versionName);
        //Print (Get) Release version
        Log.i(TAG, FlurryAgent.getReleaseVersion());
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FlurryAgent.onStartSession(UnityPlayer.currentActivity);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(UnityPlayer.currentActivity);
    }

    //endregion

    // flurry Analytics functions

    public void logEvent(String eventName)
    {
        FlurryAgent.logEvent(eventName);
    }

    public void startLogEvent(String eventName, boolean recorded)
    {
        if(recorded)
        {
            FlurryAgent.logEvent(eventName, recorded);
        }
        else
        {
            this.logEvent(eventName);
        }
    }

    public void endTimeEvent(String eventName)
    {
        FlurryAgent.endTimedEvent(eventName);
    }
    //endregion

    public void sendMessageToUnity()
    {
        //(unityObjectName, methodName from previous Object, paramether string from previous method name)
        //UnityPlayer.UnitySendMessage( UnityObjName, "OnAndroidEvent", "amazon-interstitial-dismiss");
    }
}
