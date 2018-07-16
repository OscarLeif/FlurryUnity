using JetBrains.Annotations;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Flurry Object Only for Android
/// </summary>
public class FlurryAnalytics : MonoBehaviour
{
    #region Fields

    [CanBeNull]
    private static FlurryAnalytics _instance;

    [NotNull]
    private static readonly object Lock = new object();

    [SerializeField]
    private bool _persistent = true;

    public static bool Quitting { get; private set; }

    #endregion

    #region  Properties

    [NotNull]
    public static FlurryAnalytics Instance
    {
        get
        {
            if (Quitting)
            {
                Debug.LogWarning(typeof(FlurryAnalytics).Name + " Instance will not be returned because the application is quitting.");
                // ReSharper disable once AssignNullToNotNullAttribute
                return null;
            }
            lock (Lock)
            {
                if (_instance != null)
                    return _instance;
                var instances = FindObjectsOfType<FlurryAnalytics>();
                var count = instances.Length;
                if (count > 0)
                {
                    if (count == 1)
                        return _instance = instances[0];
                    Debug.LogWarning(typeof(FlurryAnalytics).Name + " There should never be more than one {nameof(Singleton)} of type {typeof(T)} in the scene, but {count} were found. The first instance found will be used, and all others will be destroyed.");
                    for (var i = 1; i < instances.Length; i++)
                        Destroy(instances[i]);
                    return _instance = instances[0];
                }

                Debug.Log(typeof(FlurryAnalytics).Name + "An instance is needed in the scene and no existing instances were found, so a new instance will be created.");
                return _instance = new GameObject(typeof(FlurryAnalytics).Name).AddComponent<FlurryAnalytics>();
            }
        }
    }
    #endregion

    #region Monobehaviour Methods

    private void Awake()
    {
        if (_persistent)
        {
            DontDestroyOnLoad(this.gameObject);
        }
        this.Init();
    }

    private void OnApplicationQuit()
    {
        Quitting = true;
    }

    #endregion

    #region Flurry Fields

    public bool PluginEnable = false;

    private AndroidJavaClass _javaClass;

    private AndroidJavaObject _javaObject { get { return _javaClass.GetStatic<AndroidJavaObject>("singleton"); } }

    private bool m_isInit = false;

    private bool isTestMode = false;

    public string flurryKeyAmazon;

    public string flurryKeyGoogle;

    public StoreVersion storeVersion;

    #endregion

    #region Flurry Methods

    private void Init()
    {
        string finalKey = "";//TODO Clean up
        if (PluginEnable)
        {
            if (Debug.isDebugBuild)
            {
                this.isTestMode = true;
            }
            switch (storeVersion)
            {
                case StoreVersion.AmazonStore:
                    finalKey = this.flurryKeyAmazon;
                    break;
                case StoreVersion.GooglePlay:
                    finalKey = this.flurryKeyGoogle;
                    break;
                default:
                    Debug.Log("Probably is Test Mode or Key store is not setup");
                    break;
            }

            this._javaClass = new AndroidJavaClass("ata.plugins.AnalyticsPlugin");
            this._javaClass.CallStatic("start", this.gameObject.name, finalKey);
            this.m_isInit = true;
        }

    }

    public void LogEvent(string eventName)
    {
        if (PluginEnable && m_isInit)
        {
#if UNITY_ANDROID 
            _javaObject.Call("logEvent", eventName);
#endif
        }
    }

    public void logEvent(string eventName, bool record)
    {
        if (PluginEnable && m_isInit)
        {
#if UNITY_ANDROID 
            _javaObject.Call("logEvent", eventName, record);
#endif
        }
    }

    public void endTimedEvent(string eventName)
    {
        if (PluginEnable && m_isInit)
        {
#if UNITY_ANDROID 
            _javaObject.Call("endTimedEvent", eventName);
#endif
        }
    }

    // TODO Need to completed how to send the dicctionary to java
    public void LogEvent(string eventName, Dictionary<string, string> parameters, bool record = false)
    {
        if (PluginEnable && m_isInit)
        {
#if UNITY_ANDROID
            using (var hashMap = DictionaryToJavaHashMap(parameters))
            {
                if (record)
                {
                    _javaObject.Call("SetLogEventRecord", eventName, hashMap);
                }
                else
                {
                    _javaObject.Call("SetLogEvent", eventName, hashMap);
                }
            }
#endif
        }

    }

    #endregion

    #region Helpers

#if UNITY_ANDROID
    /// <summary>
    /// Converts Dictionary<string, string> to java HashMap object
    /// </summary>
    public static AndroidJavaObject DictionaryToJavaHashMap(Dictionary<string, string> dictionary)
    {
        var javaObject = new AndroidJavaObject("java.util.HashMap");
        var put = AndroidJNIHelper.GetMethodID(javaObject.GetRawClass(), "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

        foreach (KeyValuePair<string, string> entry in dictionary)
        {
            using (var key = new AndroidJavaObject("java.lang.String", entry.Key))
            {
                using (var value = new AndroidJavaObject("java.lang.String", entry.Value))
                {
                    AndroidJNI.CallObjectMethod(javaObject.GetRawObject(), put, AndroidJNIHelper.CreateJNIArgArray(new object[] { key, value }));
                }
            }
        }
        return javaObject;
    }
#endif
    /// <summary>
    /// Converts java EventRecordStatus to EventRecordStatus
    /// </summary>
    /// <param name="javaObject">java object</param>
    /// <returns></returns>
    /*private static EventRecordStatus JavaObjectToEventRecordStatus(AndroidJavaObject javaObject)
    {
        return (EventRecordStatus) javaObject.Call<int>("ordinal");
    }*/

    #endregion
}


public enum StoreVersion
{
    GooglePlay, AmazonStore, disable
}