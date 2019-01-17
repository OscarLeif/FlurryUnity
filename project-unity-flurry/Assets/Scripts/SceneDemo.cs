using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class SceneDemo : MonoBehaviour
{
    bool m_Start = false;

    #region Analytics

    public string stringToEdit = "Hello event";

    #endregion


    #region Remote Configuration
    public TextMeshProUGUI labelRemoteString;

    public TextMeshProUGUI labelRemoteBool;

    public TextMeshProUGUI labelRemoteInt;

    public TextMeshProUGUI labelRemoteFloat;

    public TextMeshProUGUI labelRemoteLong;
    #endregion


    // Use this for initialization
    private void Start()
    {

    }

    public void UpdateRemoteData()
    {
        labelRemoteString.text = FlurryAnalytics.Instance.getRemoteString("string_value", "Default String");
        labelRemoteBool.text = FlurryAnalytics.Instance.getRemoteBool("bool_value", false).ToString();
        labelRemoteInt.text = FlurryAnalytics.Instance.getRemoteInt("int_value", -1).ToString();
        labelRemoteFloat.text = FlurryAnalytics.Instance.getRemoteFloat("float_value", -2f).ToString();
        labelRemoteLong.text = FlurryAnalytics.Instance.getRemoteLong("long_value", 1111111111).ToString();
    }

    private void OnGUI()
    {
        //GUILayout.BeginHorizontal();
        //GUILayout.Toggle(m_Start, "Start Analytics", GUILayout.Width(Screen.width));
        //GUILayout.EndHorizontal();

        //// Create the banner
        //if (GUI.Button(new Rect(Screen.width * 0.05f, Screen.height * 0.1f, Screen.width * 0.4f, Screen.height * 0.15f), "Set Log Event"))
        //{
        //    FlurryAnalytics.Instance.LogEvent("Button Click Event");
        //}
        ////Create input text field 

        //GUILayout.BeginHorizontal();
        //stringToEdit = GUI.TextField(new Rect(Screen.width * 0.55f, Screen.height * 0.1f, Screen.width * 0.4f, Screen.height * 0.15f), stringToEdit, 25);
        //GUILayout.EndHorizontal();

        //if (GUI.Button(new Rect(Screen.width * 0.05f, Screen.height * 0.3f, Screen.width * 0.4f, Screen.height * 0.15f), "Set Log Event (Input Name) "))
        //{
        //    FlurryAnalytics.Instance.LogEvent(stringToEdit);
        //}

    }
}
