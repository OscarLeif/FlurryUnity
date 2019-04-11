using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

[ExecuteInEditMode]
public class SceneDemo : MonoBehaviour
{
    bool m_Start = false;

    #region Analytics

    public string customEvent = "Hello event";

    private string remoteString = "NULL";

    private bool remoteBool = false;

    private int remoteInt = -1;

    private long remoteLong = -100000;

    private float remoteFloat = -12345;


    #endregion

    // Testing
    void OnGUI()
    {
        // Initialize
        GUIStyle customButtom = new GUIStyle("button");

        customButtom.fontSize = (Screen.width / Screen.height) > 1 ? (Screen.width / Screen.height) * 15 : (Screen.height / Screen.width) * 15;

        //GUI.Toggle(new Rect(Screen.width * 0.05f, Screen.height* 0.05f, 128,64), FlurryAnalytics.Instance.IsInitialize, "Flurry Initialize", toggleSize);

        GUI.Label(new Rect(Screen.width * 0.05f, Screen.height * 0.05f, Screen.width * 0.90f, Screen.height * 0.08f), "Flurry SDK Initialiaze: " + (FlurryAnalytics.Instance.IsInitialize ? "true" : "false"), customButtom);

        if (GUI.Button(new Rect(Screen.width * 0.05f, Screen.height * 0.15f, Screen.width * 0.4f, Screen.height * 0.10f), "Initialize Flurry SDK", customButtom))
        {
            if (FlurryAnalytics.Instance)
            {
                FlurryAnalytics.Instance.Setup();
            }
        }

        // Set Log Event
        if (GUI.Button(new Rect(Screen.width * 0.55f, Screen.height * 0.15f, Screen.width * 0.4f, Screen.height * 0.10f), "Set Log event: " + customEvent, customButtom))
        {
            if (FlurryAnalytics.Instance)
            {
                FlurryAnalytics.Instance.LogEvent("Hello World");
            }
        }

        if (GUI.Button(new Rect(Screen.width * 0.05f, Screen.height * 0.3f, Screen.width * 0.4f, Screen.height * 0.10f), "Get Remote data", customButtom))
        {
            if (FlurryAnalytics.Instance)
            {
                UpdateRemoteData();
            }
        }

        if (GUI.Button(new Rect(Screen.width * 0.55f, Screen.height * 0.3f, Screen.width * 0.4f, Screen.height * 0.10f), "Fetch Remote Data", customButtom))
        {
            if (FlurryAnalytics.Instance)
            {
                FlurryAnalytics.Instance.fetchConfig();
            }
        }

        GUI.Label(new Rect(Screen.width * 0.05f, Screen.height * 0.50f, Screen.width * 0.4f, Screen.height * 0.08f), "Remote String: " + this.remoteString, customButtom);

        GUI.Label(new Rect(Screen.width * 0.05f, Screen.height * 0.60f, Screen.width * 0.4f, Screen.height * 0.08f), "Remote Bool: " + this.remoteBool, customButtom);

        GUI.Label(new Rect(Screen.width * 0.05f, Screen.height * 0.70f, Screen.width * 0.4f, Screen.height * 0.08f), "Remote Int: " + this.remoteInt, customButtom);

        GUI.Label(new Rect(Screen.width * 0.05f, Screen.height * 0.80f, Screen.width * 0.4f, Screen.height * 0.08f), "Remote Float: " + this.remoteFloat, customButtom);

        GUI.Label(new Rect(Screen.width * 0.05f, Screen.height * 0.90f, Screen.width * 0.4f, Screen.height * 0.08f), "Remote Long: " + this.remoteLong, customButtom);
    }

    public void UpdateRemoteData()
    {
        //this.remoteString = FlurryAnalytics.Instance.getRemoteString("string_value", "No Remote String");
        //this.remoteBool = FlurryAnalytics.Instance.getRemoteBool("bool_value", false);
        //this.remoteInt = FlurryAnalytics.Instance.getRemoteInt("int_value", -1);
        //this.remoteFloat = FlurryAnalytics.Instance.getRemoteFloat("float_value", -2f);
        //this.remoteLong = FlurryAnalytics.Instance.getRemoteLong("long_value", 1111111111);

        this.remoteString = FlurryAnalytics.Instance.getRemoteString("string", "No Remote String");
        this.remoteBool = FlurryAnalytics.Instance.getRemoteBool("bool", false);
        this.remoteInt = FlurryAnalytics.Instance.getRemoteInt("int", -1);
        this.remoteFloat = FlurryAnalytics.Instance.getRemoteFloat("float", -2f);
        this.remoteLong = FlurryAnalytics.Instance.getRemoteLong("long", 1111111111);
    }
}
