# Flurry plugin for Unity-3D

This is a free plugin only Android implementation for Unity 3D.

# Features
- Analytics and Remote configuration
- Lightweight 
- Support most of all Unity android project
- No override of the Unity Main activity
----
# Requierements
- Unity 2018 and Higher (Via Unity Package Manager)
- Unity 5+ (Via Custom Package File)

# How to Install
### From package manager (Unity 2018+)
- Create a new Unity Project
- Open the manifest.json file in the Packages folder inside of the Project
  ```
  "com.atagames.flurry": "https://github.com/OscarLeif/FlurryUnity.git",
  ```
- Unity Package Manager. Just Click Add Git URL use the next link "https://github.com/OscarLeif/FlurryUnity.git"
- To Update you will need to remove and add again (I will check if I can make this more simpler). 


### From Package file (Unity 5+):
- Download and install this package in your Unity Project: Here (TODO Add Link to package)

# Minify

In order to use this plugin with minify you need to copy the next instructions to your own proguard-user.txt
```
-keep class ata.plugins.** { *; }
-dontwarn ata.plugins
-keep class com.flurry.** { *; }
-dontwarn com.flurry
```

# How to Use 

You should first create a Flurry developer account and setup your app in the website.

## Analytics

- You must first Initialize the Plugin.
  - Call this only once.
    ```sh
    FlurryAnalytics.Instance.Init(string flurryKeyDebug);
    //Never use Empty String ("") or string.Empty. Will make flurry fail.
    ```
- Set Logs to Flurry
  - ```Call FlurryAnalytics.Instance.LogEvent(string eventName, Dictionary<string, string> dictionary = null, bool record = false) ```
  - ```Call FlurryAnalytics.Instance.EndTimeEvent(string eventName)```

## Important

Note If for some reason you use an empty value the plugin will crash.
When Testing you can create a Test Key, and after complete it's a good idea to remove that Key from the Flurry Analytics console.
When not using a Flurry Key just write a random value, Initialize the plugin with any value it will just make sure Minify is working fine.
 
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
