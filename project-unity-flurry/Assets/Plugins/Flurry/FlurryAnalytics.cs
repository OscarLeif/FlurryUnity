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
        this.Setup();
    }

    private void OnApplicationQuit()
    {
        Quitting = true;
    }

    #endregion

    #region Flurry Fields

    public bool PluginEnable = false;

    private AndroidJavaClass _javaClass;

    private AndroidJavaObject _javaObject { get { return _javaClass.GetStatic<AndroidJavaObject>("instance"); } }

    private bool m_isInit = false;

    private bool isTestMode = false;

    public string flurryKeyAmazon;

    public string flurryKeyGoogle;

    public StoreVersion storeVersion;

    #endregion

    #region Flurry Methods

    public void Setup()
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
                    Debug.Log("Probably is Test Mode or Key store is empty");
                    break;
            }
            _javaClass = new AndroidJavaClass("ata.plugins.FlurryAnalytics");
            _javaClass.CallStatic("start", finalKey);
            this.fetchConfig();//You can fetch later, but lets say everytime the user is online. For now
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

    public void StartLogEvent(string eventName, bool recorded)

    {
        if (PluginEnable && m_isInit)
        {
#if UNITY_ANDROID 
            _javaObject.Call("logEvent", eventName, recorded);
#endif
        }
    }


    public void EndTimeEvent(string eventName)

    {
        if (PluginEnable && m_isInit)
        {
#if UNITY_ANDROID 
            _javaObject.Call("endTimedEvent", eventName);
#endif
        }
    }

    //TODO completed this event
    //WARNING NOT Impletemented.
    public void LogEvent(string eventName, Dictionary<string, string> parameters, bool record = false)
    {
        if (PluginEnable && m_isInit)
        {
#if UNITY_ANDROID
            using (var hashMap = DictionaryToJavaHashMap(parameters))
            {
                if (record)
                {
                    _javaObject.Call("logEvent", eventName, hashMap);
                }
                else
                {
                    _javaObject.Call("logEvent", eventName, hashMap);
                }
            }
#endif
        }
    }

    #endregion

    #region Remote Config

    /// <summary>
    /// Used to Fetech remote config from Flurry Servers
    /// </summary>
    public void fetchConfig()
    {
#if UNITY_ANDROID
        if (Application.platform == RuntimePlatform.Android)
        {
            _javaObject.Call("fetchConfig");
        }
#endif
    }

    /// <summary>
    /// Get remote Value from Flurry config, if cannot get remote value
    /// we will return a default. A fetch and remote values should exist.
    /// </summary>
    /// <param name="key"></param>
    /// <param name="defaultValue"></param>
    /// <returns></returns>
    public string getRemoteString(string key, string defaultValue)
    {
        string returnString = defaultValue;
#if UNITY_ANDROID
        if (Application.platform == RuntimePlatform.Android)
        {
            returnString = _javaObject.Call<string>("getRemoteString", key, defaultValue);
        }
#endif
        return returnString;
    }

    public bool getRemoteBool(string key, bool defaultValue)
    {
        bool returnBool = defaultValue;
#if UNITY_ANDROID
        if (Application.platform == RuntimePlatform.Android)
        {
            returnBool = _javaObject.Call<bool>("getRemoteBool", key, defaultValue);
        }
#endif
        return returnBool;
    }

    /// <summary>
    /// 
    /// </summary>
    /// <param name="key"></param>
    /// <param name="defaultValue"></param>
    /// <returns></returns>
    public int getRemoteInt(string key, int defaultValue)
    {
        int returnInt = defaultValue;
#if UNITY_ANDROID
        if (Application.platform == RuntimePlatform.Android)
        {
            returnInt = _javaObject.Call<int>("getRemoteInt", key, defaultValue);
        }
#endif
        return returnInt;
    }

    public float getRemoteFloat(string key, float defaultValue)
    {
        float returnFloat = defaultValue;
#if UNITY_ANDROID
        if (Application.platform == RuntimePlatform.Android)
        {
            returnFloat = _javaObject.Call<float>("getRemoteFloat", key, defaultValue);
        }
#endif
        return returnFloat;
    }

    public float getRemoteLong(string key, long defaultValue)
    {
        long returnLong = defaultValue;
#if UNITY_ANDROID
        if (Application.platform == RuntimePlatform.Android)
        {
            returnLong = _javaObject.Call<long>("getRemoteLong", key, defaultValue);
        }
#endif
        return returnLong;
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