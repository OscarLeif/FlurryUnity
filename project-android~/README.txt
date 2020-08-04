This is an Android Studio Project Folder.

Basically what I do to create the plugin is to Just call One method via Unity then Do the Initialize stuff in Java.
Of course doing this have some issues like Threading and more stuf like that.
For now the only issue is this one.
When I create the aar plugin file I cannot join the Flurry library from some reason you cannot do that on aar libraries.
Instead you must put the flurry library plugin in the plugin Android folder, with that everything will work fine.
I write that because for some weird reason some libraries are still released in Jar file.
Also every time I update the plugin I need to update the jar files and test if this works fine.
After check test test project (Just Change The ID) you should see that on the flurry website.

After test just copy and pate the plugin "project-unity-FlurryDemo~\Assets\Plugins" to "FlurryUnity\Plugins"
Then commit. I will try to simply more this. 
Need to update more plugins.

Oscar Leif.