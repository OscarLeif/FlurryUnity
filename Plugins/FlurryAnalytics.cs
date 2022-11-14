using JetBrains.Annotations;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace FlurrySDK
{
    /// <summary>
    /// Flurry Analytics Singleton.
    /// Better Initialize this by using Only Script.
    /// So Don't create A Gameobject with this component.
    /// </summary>
    public class FlurryAnalytics : MonoBehaviour
    {
        [RuntimeInitializeOnLoadMethod(RuntimeInitializeLoadType.SubsystemRegistration)]
        public static void AutoInitialize()
        {
            m_Instance = null;
        }

        /// <summary>
        /// Global Singleton. We must Auto Initialize after call
        /// </summary>
        public static FlurryAnalytics Instance
        {
            get
            {
                if (m_Instance == null)
                {
                    GameObject go = new GameObject(nameof(FlurryAnalytics));
                    m_Instance = go.AddComponent<FlurryAnalytics>();
                    DontDestroyOnLoad(go);
                }
                return m_Instance;
            }
        }

        private static FlurryAnalytics m_Instance;

        #region Flurry Fields
        public bool PluginEnable = false;

        private AndroidJavaClass _javaClass;

        private AndroidJavaObject _javaObject { get { return _javaClass.GetStatic<AndroidJavaObject>("instance"); } }

        public string flurryKey;

        [Tooltip("This should be set True when called from Android Side")]
        public bool Initialize = false;
        #endregion

        /// <summary>
        /// Initializer. Just call this once.
        /// </summary>
        public void Init(string flurryKey)
        {
            this.PluginEnable = true;
            if (PluginEnable == false)
                return;
            if (this.Initialize)
            {
                Debug.Log("Already Initialize");
            }
            this.flurryKey = flurryKey;

#if UNITY_ANDROID
            if (Application.platform == RuntimePlatform.Android)
            {
                AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
                AndroidJavaObject activity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
                AndroidJavaObject context = activity.Call<AndroidJavaObject>("getApplicationContext");

                _javaClass = new AndroidJavaClass("ata.plugins.FlurryAnalytics");
                _javaClass.CallStatic("start", activity, flurryKey, new AndroidPluginCallback(this));
            }
#endif
        }

        #region Analytics Implementation
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

        #region Remote Configuration Implementation

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

        #region Android Extras

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

        #region Android Callback

        /// <summary>
        /// Warning Calling From Java to C# may cause error cannot execute 
        /// on Main Thread
        /// </summary>
        public class AndroidPluginCallback : AndroidJavaProxy
        {
            //private FlurryAnalytics reference = null;

            public AndroidPluginCallback(FlurryAnalytics reference) : base("ata.plugins.FlurryCallback")
            {
                //this.reference = reference;
            }

            public void onInitialize(bool isInit)
            {
                Debug.Log("Initiaze is: " + isInit);
                FlurryAnalytics.Instance.Initialize = true;
                //reference.Initialize = isInit;
                //reference.Enqueue(() => reference.AndroidShowToast("Plugin Initialize " + isInit));
            }
        }

        #endregion
    }
}
