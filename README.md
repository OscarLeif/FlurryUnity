# Flurry plugin for Unity-3D

This is a free plugin only Android implementation for Unity 3D.

# Features
- Analytics and Remote configuration
- Lightweight 
- Support most of all Unity android project
- No override of the Unity Main activity
- Warning Unity have this feature incompleted.  :/ Added Preprocessor "USE_FLURRY" When you Add this package

----
# Requierements
- Unity 2018 and Higher (Via Unity Package Manager)

# How to Install
### From package manager (Unity 2018+)

- You need an Unity Project with Package Manager (Unity 2019.+)
- In Unity Package Manager Window, just Click "+" Button, Add Git URL use the next link "https://github.com/OscarLeif/FlurryUnity.git"

- Old Way: Open the manifest.json file in the Packages folder inside of the Project
  ```
  "com.atagames.flurry": "https://github.com/OscarLeif/FlurryUnity.git",
  ```


# Whe Using Minify

In order to use this plugin with minify you need to copy the next instructions to your own proguard-user.txt
If you dont do this when using minify the code will be stripped making the plugin useless
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

install as Submodule

git submodule add https://github.com/OscarLeif/FlurryUnity Assets/Submodules/Flurry

Oscar Leif
