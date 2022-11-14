Flurry Unity Plugin

What are these files? 

FlurryAnalytics.java is a Helper File. Instead of creating an Android Studio Project then export an "aar" library plugin
we can just simply write the source code and Unity will make the rest. Is more easy to maintain and more faster to update and customize.

flurryAnalytics_14.0.0.aar
Any file with extension "aar" is an Android library in this case this one is downloaded from the Flurry website.
The idea is just to change this file to update Flurry very fast.

Singleton file is a "Singleton" used exclusive for the Flurry Plugin. It have hiw own namescape to avoid conflict with Other Singleton Files.
Android will make Calls to Android, while in other platforms it just do nothing.

How To Use ?

This will only work on Android Platform.