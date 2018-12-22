using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SceneDemo : MonoBehaviour
{
    bool m_Start = false;

    public string stringToEdit = "Hello event";
    // Use this for initialization
    void Start()
    {

    }

    private void OnGUI()
    {
        GUILayout.BeginHorizontal();
        GUILayout.Toggle(m_Start, "Start Analytics", GUILayout.Width(Screen.width));
        GUILayout.EndHorizontal();

        // Create the banner
        if (GUI.Button(new Rect(Screen.width * 0.05f, Screen.height * 0.1f, Screen.width * 0.4f, Screen.height * 0.15f), "Set Log Event"))
        {
            FlurryAnalytics.Instance.LogEvent("Button Click Event");
        }
        //Create input text field 

        GUILayout.BeginHorizontal();
        stringToEdit = GUI.TextField(new Rect(Screen.width * 0.55f, Screen.height * 0.1f, Screen.width * 0.4f, Screen.height * 0.15f), stringToEdit, 25);
        GUILayout.EndHorizontal();

        if (GUI.Button(new Rect(Screen.width * 0.05f, Screen.height * 0.3f, Screen.width * 0.4f, Screen.height * 0.15f), "Set Log Event (Input Name) "))
        {
            FlurryAnalytics.Instance.LogEvent(stringToEdit);
        }

    }
}
