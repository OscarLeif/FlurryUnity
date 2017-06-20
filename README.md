# Flurry plugin for Unity-3D
----
This is a free plugin only Android implementation for Unity 3D
This is based from this project:
https://github.com/Majchrzak/Flurry-Unity-3D

# Differences from base project and this one ?

The Main difference is the Plugin implementacion, the based plugin uses Java Calls directly from Unity to Android Java.
So we need the Flurry Analatycs Jar File, while this have first an Android Studio Project that generates Android Libraries so the final result is just a very simple and more portable aar file. Of course this will be only compatible for graddle (Not sure about the support of this in Eclipse Android) 

# How to use 

Sorry for now I'm still testing.
My idea is just to implement less files possible (of course that's is the purpose of the Android Studio Project) 
So only import a package and using a prefab will be good.

Why don't support IOS ?

Right now I don't have any IOS device so it's not possible for me doing this.
Sorry 

Oscar Leif
