using JetBrains.Annotations;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Flurry Object Only for Android
/// </summary>
public class FlurryAnalytics : MonoBehaviour
{
    public bool PluginEnable = false;

    private AndroidJavaClass androidJavaClass;

    private AndroidJavaObject javaObject { get { return androidJavaClass.GetStatic<AndroidJavaObject>("instance"); } }

    public string flurryKeyGooglePlay;

    public string flurryKeyAmazon;

    public bool testMode;

    public StoreVersion storeVersion;

    private bool m_isInit = false;

    #region Fields

    public static bool Quitting { get; private set; }

    [CanBeNull]
    private static FlurryAnalytics _instance = null;

    [SerializeField]
    private bool _persistent = true;

    [NotNull]
    private static readonly object Lock = new object();

    #endregion

    #region Properties
    [NotNull]
    public static FlurryAnalytics Inst
    {
        get
        {
            if (Quitting)
            {
                return null;
            }
            lock (Lock)
            {
                if (_instance != null)
                {
                    return _instance;
                }
                var instances = FindObjectsOfType<FlurryAnalytics>();
                var count = instances.Length;
                if (count > 0)
                {
                    if (count == 1)
                    {
                        return _instance = instances[0];
                    }
                    for (var i = 1; i < instances.Length; i++)
                    {
                        Destroy(instances[i]);
                    }
                    return _instance = instances[0];
                }
                return _instance = new GameObject(typeof(FlurryAnalytics).Name).AddComponent<FlurryAnalytics>();
            }
        }
    }
    #endregion

    #region Methods

    public void Awake()
    {
        if (_persistent)
        {
            DontDestroyOnLoad(gameObject);
        }
    }

    private void Start()
    {
        if (PluginEnable)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
        AndroidJavaClass jc = new AndroidJavaClass("hammergames.flurry.AnalyticsPlugin");
        plugin = jc.CallStatic<AndroidJavaObject>("getInstance");
#endif
            Init();
        }
    }

    private void OnApplicationQuit()
    {
        Quitting = true;
    }

    #endregion


    #region Flurry Fields



    #endregion
    //Flurry Methods

    public void Init()
    {
        if (PluginEnable)
        {
            if (Debug.isDebugBuild)
            {
                testMode = true;
            }
            string finalKey = "";

            switch (storeVersion)
            {
                case StoreVersion.AmazonStore:
                    finalKey = flurryKeyAmazon;
                    break;
                case StoreVersion.GooglePlay:
                    finalKey = flurryKeyGooglePlay;
                    break;
                case StoreVersion.disable:
                    finalKey = "";
                    break;
            }
#if UNITY_ANDROID && !UNITY_EDITOR
        plugin.Call("init", finalKey, testMode);
#endif
            m_isInit = true;
        }
    }

    public void LogEvent(string eventName)
    {
        if (PluginEnable && m_isInit)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            plugin.Call("LogEvent", eventName);
#endif
        }
    }

    public void BeginLogEvent(string eventName, bool record)
    {
        if (PluginEnable && m_isInit)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            plugin.Call("BegingLogEvent", eventName, record);
#endif
        }
    }

    public void EndLogEvent(string eventName)
    {
        if (PluginEnable && m_isInit)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            plugin.Call("EndLogEvent", eventName);
#endif
        }
    }

    public void LogEvent(string eventName, Dictionary<string, string> parameters, bool record = false)
    {
        if (PluginEnable)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
    using (var hashMap = DictionaryToJavaHashMap(parameters))
	    {
            if(record)
            {
                plugin.Call("SetLogEventRecord", eventName, hashMap);
            }
            else
            {
                plugin.Call("SetLogEvent", eventName, hashMap);
            }
		}
#endif
        }
    }

    //Reciver Messages from the plugin
    //This should be called from Android
    public void OnAndroidEvent(string message)
    {
        Debug.Log(message);
    }

    #region [Helpers]
#if UNITY_ANDROID && !UNITY_EDITOR
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

        /// <summary>
        /// Converts java EventRecordStatus to EventRecordStatus
        /// </summary>
        /// <param name="javaObject">java object</param>
        /// <returns></returns>
	    /*private static EventRecordStatus JavaObjectToEventRecordStatus(AndroidJavaObject javaObject)
        {
            return (EventRecordStatus) javaObject.Call<int>("ordinal");
        }*/
#endif
    #endregion
}

public enum StoreVersion
{
    GooglePlay, AmazonStore, disable
}

