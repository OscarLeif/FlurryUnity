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

    public static void start(String gameObjectName, String flurryKey)
    {
        // Instantiate and add to Unity Player Activity.
        instance = new FlurryAnalytics();
        instance.gameObjectName = gameObjectName;
        UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(instance,FlurryAnalytics.TAG).commit();
    }

    //region Fragment lifeCycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    //endregion

    // flurry Analytics functions

    //endregion


    public void sendMessageToUnity()
    {
        //(unityObjectName, methodName from previous Object, paramether string from previous method name)
        //UnityPlayer.UnitySendMessage( UnityObjName, "OnAndroidEvent", "amazon-interstitial-dismiss");
    }
}
