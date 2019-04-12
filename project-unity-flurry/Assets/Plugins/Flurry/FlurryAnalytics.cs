#define FlurrySDK

using JetBrains.Annotations;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Flurry Object Only for Android
/// Error 400 in Fetch remote config if remote config doesn't exist in the flurry (console?) platform
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
        //this.Setup();
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

    public bool IsInitialize { get; set; }

    private bool isTestMode = false;

    public string flurryKeyDebug;

    public string flurryKeyAmazon;

    public string flurryKeyGoogle;

    public string flurryKeyGalaxy;
    #endregion

    #region Flurry Methods

    public void Setup()
    {
        if (PluginEnable)
        {
            if (Debug.isDebugBuild)
            {
                this.isTestMode = true;
            }
            _javaClass = new AndroidJavaClass("ata.plugins.FlurryAnalytics");
            _javaClass.CallStatic("start",
                flurryKeyDebug, flurryKeyGoogle, flurryKeyAmazon, flurryKeyGalaxy, new AndroidPluginCallback());
            this.fetchConfig();//You can fetch later, but lets say everytime the user is online. For now
        }
    }

    public void LogEvent(string eventName)
    {
        if (PluginEnable && IsInitialize)
        {
#if UNITY_ANDROID 
            if (Application.platform == RuntimePlatform.Android)
                _javaObject.Call("logEvent", eventName);
#endif
        }
    }

    public void StartLogEvent(string eventName, bool recorded)
    {
        if (PluginEnable && IsInitialize)
        {
#if UNITY_ANDROID 
            if (Application.platform == RuntimePlatform.Android)
                _javaObject.Call("logEvent", eventName, recorded);
#endif
        }
    }

    public void EndTimeEvent(string eventName)
    {
        if (PluginEnable && IsInitialize)
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
        if (PluginEnable && IsInitialize)
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
        if (Application.platform == RuntimePlatform.Android && IsInitialize)
        {
            _javaObject.Call("fetchConfig");
        }
#endif
    }

    public string GetInstallerPackageName()
    {
        if (Application.platform == RuntimePlatform.Android)
        {
            return _javaClass.Call<string>("getInstallerPackageName");
        }
        else
        {
            return null;
        }
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
        if (Application.platform == RuntimePlatform.Android && IsInitialize)
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
        if (Application.platform == RuntimePlatform.Android && IsInitialize)
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
        if (Application.platform == RuntimePlatform.Android && IsInitialize)
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
        if (Application.platform == RuntimePlatform.Android && IsInitialize)
        {
            returnFloat = _javaObject.Call<float>("getRemoteFloat", key, defaultValue);
        }
#endif
        return returnFloat;
    }

    public long getRemoteLong(string key, long defaultValue)
    {
        long returnLong = defaultValue;
#if UNITY_ANDROID
        if (Application.platform == RuntimePlatform.Android && IsInitialize)
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

    #region ThreadDispatcher
    private static readonly Queue<Action> _executionQueue = new Queue<Action>();

    private void Update()
    {
        lock (_executionQueue)
        {
            while (_executionQueue.Count > 0)
            {
                _executionQueue.Dequeue().Invoke();
            }
        }
    }

    /// <summary>
    /// Locks the queue and adds the IEnumerator to the queue
    /// </summary>
    /// <param name="action">IEnumerator function that will be executed from the main thread.</param>
    public void Enqueue(IEnumerator action)
    {
        lock (_executionQueue)
        {
            _executionQueue.Enqueue(() =>
            {
                StartCoroutine(action);
            });
        }
    }

    /// <summary>
    /// Locks the queue and adds the Action to the queue
    /// </summary>
    /// <param name="action">function that will be executed from the main thread.</param>
    public void Enqueue(Action action)
    {
        Enqueue(ActionWrapper(action));
    }

    IEnumerator ActionWrapper(Action a)
    {
        a();
        yield return null;
    }

    #endregion

    public void AndroidShowToast(string message, bool useShortDuration = true)
    {

#if UNITY_ANDROID
        //Safe Thread problems
        if (!Debug.isDebugBuild)
            return;
        AndroidJavaClass toastClass = new AndroidJavaClass("android.widget.Toast");
        object[] toastParams = new object[3];
        AndroidJavaClass unityActivity = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        toastParams[0] = unityActivity.GetStatic<AndroidJavaObject>("currentActivity");
        toastParams[1] = message;
        toastParams[2] = useShortDuration ? toastClass.GetStatic<int>("LENGTH_SHORT") : toastClass.GetStatic<int>("LENGTH_LONG");
        AndroidJavaObject toastObject = toastClass.CallStatic<AndroidJavaObject>("makeText", toastParams);
        toastObject.Call("show");
#else
        Debug.Log("AmazonAds NonAndroid Message: " + message);
#endif
    }

    /// <summary>
    /// Called from Java.
    /// Warning everything called from Java must have to be safe thread if not 
    /// it will fails. 
    /// </summary>
    private class AndroidPluginCallback : AndroidJavaProxy
    {
        public AndroidPluginCallback() : base("ata.plugins.FlurryPluginCallback") { }

        public void OnInitialize(bool isInit)
        {
            FlurryAnalytics.Instance.Enqueue(() => FlurryAnalytics.Instance.IsInitialize = isInit);
            FlurryAnalytics.Instance.Enqueue( ()=> FlurryAnalytics.Instance.AndroidShowToast("Initialize"));
        }
    }
}