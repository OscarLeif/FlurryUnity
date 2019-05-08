# Flurry plugin for Unity-3D

This is a free plugin only Android implementation for Unity 3D.

# Features
- Analytics and Remote configuration
- Lightweight 
- Support most of all Untiy android project
- No override of the Unity Main activity
----
# Requierements
- Unity 2018 and Higher (Via Unity Package Manager)
- Unity 5+ (Via Custom Package File)

# How to Install
### From package manager (Unity 2018+)
- Create a new Unity Project
- Open the manifest.json file in the Packages folder inside of the Project
  ```sh
  Add  "com.ata.flurry": "https://github.com/OscarLeif/FlurryUnity.git",
  ```
- Press Tools->Flurry->Update Framework[GIT] in Unity to get new update when needed
### From Package file (Unity 5+):
- Download and install this package in your Unity Project: Here (TODO Add Link to package)


# How to Use 
## Analytics

- You must first Initialize the Plugin.
There's two ways to initialize:
  - Create a GameObejct and add the Component "FlurryAnalytics"
    - Call the method ```FlurryAnalytics.Init();```
  - You can call 
    ```sh
    FlurryAnalytics.Instance.Init(string flurryKeyDebug, string flurryKeyGoogle, string flurryKeyAmazon, string flurryKeyGalaxy)
    ```
- Set Logs to Flurry
  - ```Call FlurryAnalytics.Instance.LogEvent(string eventName, Dictionary<string, string> dictionary = null, bool record = false) ```
  - ```Call FlurryAnalytics.Instance.EndTimeEvent(string eventName)```
 
## Remote Configuration
- You must setup you own flurry remote configuration. Each app key have his own configuration
- If for any reason it's not possible to get remote data the Plugin will return default value. Check flurry website for more instrucctions
- ```Call FlurryAnalytics.Instance.getRemoteString(string key, string defaultValue)```
- ```Call FlurryAnalytics.Instance.getRemoteBool(string key, bool defaultValue)```
- ```Call FlurryAnalytics.Instance.getRemoteInt(string key, int defaultValue)```
- ```Call FlurryAnalytics.Instance.getRemoteFloat(string key,float defaultValue)```
- ```Call FlurryAnalytics.Instance.getRemoteLong(string key, long defaultValue)```


This is based from this project:
https://github.com/Majchrzak/Flurry-Unity-3D

Oscar Leif
