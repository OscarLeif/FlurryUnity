# Flurry plugin for Unity-3D
----
This is a free plugin only Android implementation for Unity 3D.
The plugin is updated and also have support for remote configuration from Flurry.

# How to use - Analytics

1. Download the unity package and import to your own project.
[Flurry Plugin for Unity Android](../blob/master/Release-package/UnOfficialFlurrySDK_0.41.unitypackage)

2. After Install the package you must create your first scene A Game object For example called "@Flurry" then Add the components 

- FlurryAnalytics
- UnityFlurryMainThreadDispatcher

3. After add the 2 components you must then fill the information

[Here goes a screenshot]

4. After fill the information you only need to call.

FlurryAnalytics.Instace.LogEvent("Hello World");



# How to use - Remote Configuration

1. Setup you flurry remote configuration

2. Call it.


This is based from this project:
https://github.com/Majchrzak/Flurry-Unity-3D

Oscar Leif
