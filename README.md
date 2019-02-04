# Flurry plugin for Unity-3D
----
This is a free plugin only Android implementation for Unity 3D
This is based from this project:
https://github.com/Majchrzak/Flurry-Unity-3D

# Differences from base project and this one ?

The Main difference is the Plugin implementacion, the based plugin uses Java Calls directly from Unity to Android Java.
So we need the Flurry Analatycs Jar File, while this have first an Android Studio Project that generates Android Libraries so the final result is just a very simple and more portable aar file. Of course this will be only compatible for graddle (Not sure about the support of this in Eclipse Android) 

# How to use 

1. Download the unity package and import to your own project.
[Flurry Plugin for Unity Android](../blob/master/Release-package/FlurryUnitySDK.unitypackage)
2. In your Unity use the prefab included in this package in your first scene.

...Work in progress.


Oscar Leif
