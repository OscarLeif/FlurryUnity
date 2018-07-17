# Flurry plugin for Unity-3D
----
This is a free plugin only Android implementation for Unity 3D
This is based from this project:
https://github.com/Majchrzak/Flurry-Unity-3D

# Differences from base project and this one ?

The Main difference is the Plugin implementacion, the based plugin uses Java Calls directly from Unity to Android Java.
So we need the Flurry Analatycs Jar File, while this have first an Android Studio Project that generates Android Libraries so the final result is just a very simple and more portable aar file. Of course this will be only compatible for graddle (Not sure about the support of this in Eclipse Android) 

# How to use 

This is a plugin for Android Devices, this is just a wrapper from Java Code, using this plugin should work fine for multiple platforms since it will just compile on Android platform.

**Important:** Unity Android must compile a project using **Build System: Internal**  


**1.** First you will need to **Import the FlurryAnalytics.aar** file this one is created  using the Android Studio Project and also you will to import the **FlurryAnalytics.cs** in a little ans short future just import the unity package (In Progress).

<<<<<<< HEAD
**2.** Create an Unity Object and add the Component Flurry Analytics this will be a Singleton object. In this component you can set 2 analytics Keys one for Google play store and a second one for another app store in this case I use Amazon App store.

=======
**2.** Create an Unity Object and add the Component Flurry Analytics this will be a Singleton object
In this component you can set 2 analytics Keys one for Google play store and a second one for another app store in this case I use Amazon App store.

**3.** After have an Object with the component FlurryAnalytics you will need to call Setup.
 
        public void FirstMethod()
    	{
			//Initialize the Flurry Analytics Service
    		FlurryAnalytics.Instance.Setup();
    	}
>>>>>>> master
## Send Log Events 

Flurry Analytics service have Simple Log Event, Recorded Log Events, I don't remember the las one, need to check this.

**1.Simple Log Event just call**
		

    FlurryAnalytics.Instance.LogEvent("Game Begins");

**2.Recorded Log Event**

	//Warning If you set boolean value to false it will be Simple Log Event
    FlurryAnalytics.Instance.StartLogEvent("Game Begins", true);

3.TODO Complete the last event feature

    //Need to complete this

 

# Why don't support IOS ?

Right now I don't have any IOS device so it's not possible for me doing this.
Sorry 

<<<<<<< HEAD
## Oscar Leif
=======
## Oscar Leif
>>>>>>> master
