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

    public void UpdateRemoteData()
    {
        labelRemoteString.text = FlurryAnalytics.Instance.getRemoteString("string_value", "Default String");
        labelRemoteBool.text = FlurryAnalytics.Instance.getRemoteBool("bool_value", false).ToString();
        labelRemoteInt.text = FlurryAnalytics.Instance.getRemoteInt("int_value", -1).ToString();
        labelRemoteFloat.text = FlurryAnalytics.Instance.getRemoteFloat("float_value", -2f).ToString();
        labelRemoteLong.text = FlurryAnalytics.Instance.getRemoteLong("long_value", 1111111111).ToString();
    }
}
