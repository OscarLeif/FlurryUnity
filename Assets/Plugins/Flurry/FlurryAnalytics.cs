using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Flurry Object Only for Android
/// </summary>
public class FlurryAnalytics : PluginSingleton<FlurryAnalytics>
{
    AndroidJavaClass _class;
    AndroidJavaObject instance { get { return _class.GetStatic<AndroidJavaObject>("instance"); } }

    public string flurryKeyGooglePlay;
    public string flurryKeyAmazon;

    private AndroidJavaObject plugin;

    public bool testMode;

    public StoreVersion storeVersion;

    private bool m_isInit = false;

    public override void Awake()
    {
        base.Awake();

#if UNITY_ANDROID && !UNITY_EDITOR
        AndroidJavaClass jc = new AndroidJavaClass("hammergames.flurry.AnalyticsPlugin");
        plugin = jc.CallStatic<AndroidJavaObject>("getInstance");
#endif
    }

    private void Start()
    {
        Init();
    }

    public void Init()
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

    public void LogEvent(string eventName)
    {
        if (m_isInit)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            plugin.Call("LogEvent", eventName);
#endif
        }
    }

    public void BeginLogEvent(string eventName, bool record)
    {
        if (m_isInit)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            plugin.Call("BegingLogEvent", eventName, record);
#endif
        }
    }

    public void EndLogEvent(string eventName)
    {
        if (m_isInit)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            plugin.Call("EndLogEvent", eventName);
#endif
        }
    }

    public void LogEvent(string eventName, Dictionary<string, string> parameters, bool record = false)
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

public class PluginSingleton<T> : MonoBehaviour where T : MonoBehaviour
{
    static T m_instance;

    public static T Instance
    {
        get
        {
            if (m_instance == null)
            {
                m_instance = GameObject.FindObjectOfType<T>();

                if (m_instance == null)
                {
                    GameObject singleton = new GameObject(typeof(T).Name);
                    m_instance = singleton.AddComponent<T>();
                }
            }
            return m_instance;
        }
    }

    public virtual void Awake()
    {
        if (m_instance == null)
        {
            m_instance = this as T;
            DontDestroyOnLoad(this.gameObject);
        }
        else
        {
            Destroy(gameObject);
        }
    }
}
