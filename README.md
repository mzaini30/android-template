# Android Vue

Bring Vue SPA to Android app.

## Have to Modified

1. Vue app

	```
	app/src/main/assets/
	```
	
2. Title

	```
	app/src/main/res/values/strings.xml
	```
	
3. Admob banner `(test: ca-app-pub-3940256099942544/6300978111)`

	```
	app/src/main/res/layout/activity_main.xml
	```

4. Admob application id `(test: ca-app-pub-3940256099942544~3347511713)`

	```
	app/src/main/AndroidManifest.xml
	```

4. Start.io Ads

	```
	app/src/main/AndroidManifest.xml
	app/src/main/java/com/user/app/MainActivity.java
	```
	
5. Version

	```
	app/build.gradle
	```
	
6. Package id

	```
	app/build.gradle
	app/src/main/AndroidManifest.xml
	app/src/main/java/com/user/app/
	app/src/main/java/com/user/app/MainActivity.java
	```
	
7. Keystore
8. Logo

## Before Build in Android Studio

Delete `node_modules`