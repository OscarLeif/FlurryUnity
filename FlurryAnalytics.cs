using JetBrains.Annotations;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

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
            //this.gameObject.name = "@FlurryAnalytics";
        }
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

    public string flurryKeyDebug;

    public string flurryKeyAmazon;

    public string flurryKeyGoogle;

    public string flurryKeyGalaxy;

    public bool Initialize = false;

    #endregion

    #region Flurry Analytics Methods()

    // Add an advertising network and Initialize the Plugin
    // Called just once
    public void Init()
    {
        if (PluginEnable == false)
            return;
#if UNITY_ANDROID
        if (Application.platform == RuntimePlatform.Android)
        {
            _javaClass = new AndroidJavaClass("ata.plugins.FlurryAnalytics");
            _javaClass.CallStatic("start", flurryKeyDebug, flurryKeyGoogle, flurryKeyAmazon, flurryKeyGalaxy, new AndroidPluginCallback(this));
        }
#endif
    }

    // Non prefab Initialize
    public void Init(string flurryKeyDebug, string flurryKeyGoogle, string flurryKeyAmazon, string flurryKeyGalaxy)
    {
        this.PluginEnable = true;
        this.flurryKeyDebug = flurryKeyDebug;
        this.flurryKeyGoogle = flurryKeyGoogle;
        this.flurryKeyAmazon = flurryKeyAmazon;
        this.Init();
    }

    public void LogEvent(string eventName, Dictionary<string, string> dictionary = null, bool record = false)
    {
        if (PluginEnable && Initialize)
        {
#if UNITY_ANDROID
            if (Application.platform == RuntimePlatform.Android)
                if (dictionary != null)
                {
                    var hashMap = DictionaryToJavaHashMap(dictionary);
                    _javaObject.Call("logEvent", eventName, hashMap, record);
                }
                else
                {
                    if (!record)
                        _javaObject.Call("logEvent", eventName);
                    else
                        _javaObject.Call("logEvent", eventName, record);
                }
#endif
        }
    }

    public void EndTimeEvent(string eventName)
    {
        if (PluginEnable && Initialize)
        {
#if UNITY_ANDROID
            _javaObject.Call("endTimedEvent", eventName);
#endif
        }
    }

    #endregion

    #region Remote Config Methods

    /// <summary>
    /// Used to Fetech remote config from Flurry Servers
    /// </summary>
    public void FetchConfig()
    {
#if UNITY_ANDROID
        if (Application.platform == RuntimePlatform.Android && Initialize)
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
        if (Application.platform == RuntimePlatform.Android && Initialize)
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
        if (Application.platform == RuntimePlatform.Android && Initialize)
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
        if (Application.platform == RuntimePlatform.Android && Initialize)
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
        if (Application.platform == RuntimePlatform.Android && Initialize)
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
        if (Application.platform == RuntimePlatform.Android && Initialize)
        {
            returnLong = _javaObject.Call<long>("getRemoteLong", key, defaultValue);
        }
#endif
        return returnLong;
    }
    #endregion

    #region AndroidExtras

    /// <summary>
    /// Can Return null, actually is not necessary to have this init
    /// </summary>
    /// <returns></returns>
    public string GetInstallerPackageName()
    {
        if (Application.platform == RuntimePlatform.Android && Initialize)
        {
            string installerPackageName = _javaObject.Call<string>("getInstallerPackageName");
            if (installerPackageName != null)
                return installerPackageName;
            else
                return "";
            
        }
        else
        {
            return null;
        }
    }

    // Don't call this from using the Android callback, Need Safe Thread.
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
        Debug.Log("FlurryAnalytics NonAndroid Message: " + message);
#endif
    }

    public static void AndroidShowToastMessage(String message, bool useShortDuration = true)
    {
        FlurryAnalytics.Instance.AndroidShowToast(message, useShortDuration);
    }

    /// <summary>
    /// Converts Dictionary<string, string> to java HashMap object
    /// </summary>
    private static AndroidJavaObject DictionaryToJavaHashMap(Dictionary<string, string> dictionary)
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
    #endregion

    #region ThreadDispatcher
    private static readonly Queue<Action> _executionQueue = new Queue<Action>();

    public static bool Exist()
    {
        return FlurryAnalytics._instance != null;
    }

    /// <summary>
    /// Used from Android Java Proxy Calls
    /// </summary>
    /// <returns></returns>
    public static FlurryAnalytics _Instance()
    {
        if (!Exist())
        {
            throw new Exception("Amazon as MainThreadDispatcher could not find the FlurryAnalytics Object. Please make ensure it exist");
        }
        return _instance;
    }

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

    /// If Platform is android check device.
    /// Other platform return false
    public bool IsFireTV()
    {
        if (Application.platform == RuntimePlatform.Android)
        {
            if (_javaClass.CallStatic<bool>("IsAmazonFireTv"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    #endregion

    #region Callbackclass
    /// <summary>
    /// Warning Calling From Java to C# may cause error cannot execute 
    /// on Main Thread
    /// </summary>
    public class AndroidPluginCallback : AndroidJavaProxy
    {
        private FlurryAnalytics reference = null;

        public AndroidPluginCallback(FlurryAnalytics reference) : base("ata.plugins.FlurryCallback")
        {
            this.reference = reference;
        }

        public void onInitialize(bool isInit)
        {
            reference.Initialize = isInit;
            reference.Enqueue(() => reference.AndroidShowToast("Plugin Initialize " + isInit));
        }

    }
    #endregion

    public static readonly String AppStoreGoogle = "com.android.vending";
    public static readonly String AmazonAppStore = "com.amazon.venezia";
    public static readonly String SamsungGalaxyAppStore= "com.sec.android.app.samsungapps";
}


