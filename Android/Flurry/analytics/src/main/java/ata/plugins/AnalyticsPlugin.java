package ata.plugins;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.unity3d.player.UnityPlayer;

import java.util.HashMap;

/**
 * Created by OscarLeif on 5/6/2017.
 */

public class AnalyticsPlugin extends Application
{
    public static String tag = "Flurry Plugin";

    public static String UnityObjName = "FlurryAnalytics";

    private static final AnalyticsPlugin instance = new AnalyticsPlugin();

    private Activity activity;

    private boolean IsInitialized = false;

    private String versionName;


    // Get the Main Activity
    // First Called From Unity
    public static AnalyticsPlugin getInstance()
    {
        AnalyticsPlugin.instance.activity = UnityPlayer.currentActivity;

        Log.d(tag, "Flurry Analytics Get Main Instance");

        return AnalyticsPlugin.instance;
    }

    // Initialize Flurry Analytics
    // Second Called from Unity
    public void init(String appKey, boolean testMode)
    {
        Log.i(tag,"Flurry SDK initialized");

        new FlurryAgent.Builder().withLogEnabled(testMode).build(activity, appKey);
        PackageInfo pInfo;
        try
        {
            pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            versionName = pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        FlurryAgent.setVersionName(versionName);
        IsInitialized = true;
        //Print (Get) Release version
        Log.i(tag, FlurryAgent.getReleaseVersion());
        //Start Session
        FlurryAgent.onStartSession(instance);
    }

    public void LogEvent(String eventName)
    {
        FlurryAgent.logEvent(eventName);
    }

    public void SetLogEvent(String eventName, HashMap<String, String> hashMap)
    {
        //TODO Need to do a Test
        FlurryAgent.logEvent(eventName, hashMap);
    }

    public void SetLogEventRecord(String eventName, HashMap<String,String> hashMap)
    {
        FlurryAgent.logEvent(eventName,hashMap,true);
    }

    //Begging Log Event Duration
    public void BeggingLogEvent(String eventName, boolean timed)
    {
        FlurryAgent.logEvent(eventName, timed);
    }

    public void EndLogEvent(String eventName)
    {
        FlurryAgent.endTimedEvent(eventName);
    }
    /**
     * param1: ObjectName in Unity
     * param2: methodName
     * param3: String message
     */

    public void sendMessageToUnity()
    {
        //(unityObjectName, methodName from previous Object, paramether string from previous method name)
        UnityPlayer.UnitySendMessage( UnityObjName, "OnAndroidEvent", "amazon-interstitial-dismiss");
    }
}
