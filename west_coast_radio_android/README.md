# midwest_radio_android

Dependencies: MidwestRadio/app/build.gradle

/Audio:
	The Audio files AudioService.java and AudioPlayer.java are used to facilitate the App's use of ExoPlayer, and utilize a service architecture. 
	The audio player is responsible for managing the AudioPlayer instance, and managing a wifilock for the service. 


/fragments:
	Each java file within the fragments folder contains an instance subclassed from android.support.v4.app.DialogFragment or 
	android.support.v4.app.Fragment. each of these files is used to control the corresponding xml view found in app/src/main/res/layout 
	or layout-land if applicable. These files depend upon distinct util classes, models, Audio, and the network. 
	All fragments are initialized in the MainActivity class, and managed with android.support.v4.app.FragmentManager;

/models:
	This folder contains the crucial Station Object, along with it's factory to facilitate instantiation. The Station Object itself is parcelable to be used 
	within Android Bundles and contains redundant getter and setter methods that are required for Jackson json mapping. The StationList Factory itself 
	utilizes com.fasterxml.jackson.databind.ObjectMapper to map the json into an ArrayList of Station Objects. It is crucial that this Station object 
	contain every attribute received from the backend with the redundant getter and setter methods. Adding new attributes to the Staion model
	on the backend without updating the Station Object here could be detremental to the application.


/network:
	This folder manages the HTTP requests using a standard HTTP client, that is used in LoadingFragement and RadioFragment. Requests made by this
	client expect an HTTP response code of 200 in order for the callback to be considered successful. 


/utils:
	The Utils folder contains various front end components and callbacks that are neccesary for the App, especially in the xml fragment files. 





