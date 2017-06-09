package hammergames.flurry;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryGamingAgent;
import com.unity3d.player.UnityPlayer;

import java.util.Dictionary;


//import com.unity3d.player.UnityPlayer;
//import com.unity3d.player.UnityPlayerActivity;

/**
 * Created by OscarLeif on 5/6/2017.
 */

public class FlurryPlugin extends Application
{
    public static String tag = "Flurry Plugin";

    public static String UnityObjName = "FlurryAnalytics";

    private static final FlurryPlugin instance = new FlurryPlugin();

    private Activity activity;

    private boolean IsInitialized = false;

    public static boolean interstitialAdLoaded = false;

    private String versionName;

    //private AdLayout adView = null;


    // Get the Main Activity
    // First Called From Unity
    public static FlurryPlugin getInstance(Activity mainActivity)
    {
        FlurryPlugin.instance.activity = mainActivity;
        Log.d(tag, "Flurry Analytics Plugin instantiated.");
        return FlurryPlugin.instance;
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

    public void setLogEvent(String eventName)
    {
        FlurryAgent.logEvent(eventName);
    }

    public void setLogEvent(String eventName, Dictionary<String, String> parameters)
    {

    }

    public void BegingLogEvent(String eventName, boolean timed)
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
        UnityPlayer.UnitySendMessage( UnityObjName, "OnAdEvent", "amazon-interstitial-dismiss");
    }
}
