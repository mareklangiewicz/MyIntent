# My Intent

This Android project written in Kotlin contains two parts: a library and an app.

### My Intent Library

An Android library: Collection of reusable views, fragments, activities, drawables, loggers, and other utilities.

It is divided into nine modules:

* myactivities (mostly: MyActivity - predefined base main activity / app ui manager)
* myfragments (some reusable fragments; base fragment: MyFragment with predefined communication with MyActivity)
* myviews (some custom reusable views like MyNavigationView or MyPie)
* mydrawables (some animated drawables)
* myrx (some kotlin extensions for rxjava)
* myloggers (pretty logging ui on android)
* myutils (a lot of different utility functions etc)
* myres (collection of android resources ready to use)
* myintent (the myintent app - see below)

You can use any of these through JITPack:

#### Gradle Dependencies

Add following repository at the end, to your root build.gradle file:

```gradle
allprojects {
    repositories {
        // ... other repositories here
        maven { url "https://jitpack.io" }
    }
}
```


Then add dependencies to My Intent modules you want - in your app module build.gradle file:

```gradle
dependencies {
    // ... other dependencies here
    implementation'com.github.langara.MyIntent:myactivities:1.0.6-alpha'
    }
}
```

Change `myactivities` to module you want.
Usually including the `myactivities` module is enough because it depends on almost all others.

#### Examples

To see features of this library check out the My Intent App (described below)
(especially the MyTestActivity embedded inside the app that just presents most of the features of this library)

There is also very simple example aplication using MyIntent lib. here:
[My Intent Sample](https://github.com/langara/MyIntentSample)

Also some old Java versions of example apps using this library (its old Java version) are still available
here:

* [DEPRECATED: My Blocks Sample (on github)](https://github.com/langara/MyBlocksSample) - the simplest app using My Blocks lib.
* [DEPRECATED: My Blocks Sample (on google play)](https://play.google.com/store/apps/details?id=pl.mareklangiewicz.myblockssample)
* [DEPRECATED: My Blocks Sample (on youtube)](https://www.youtube.com/watch?v=R-bpq55UYGI)
* [DEPRECATED: My Test App (on github)](https://github.com/langara/MyTestApp)
* [DEPRECATED: My Test App (on google play)](https://play.google.com/store/apps/details?id=pl.mareklangiewicz.mytestapp)
* [DEPRECATED: My Test App (on youtube)](https://www.youtube.com/watch?v=B9FPWpQYMuc)



TODO: real documentation...



### My Intent App

An app that allows the user to start **any** android **intent** easily.

Documentation is available here: http://mareklangiewicz.pl/mi


#### Google Play

* [Old version in Java: My Intent (on google play)](https://play.google.com/store/apps/details?id=pl.mareklangiewicz.myintent)

#### Youtube

* [Old version in Java: My Intent (on youtube)](https://www.youtube.com/watch?v=-8N_B-Jpk8k)



#### Screenshots (Old version in Java)



###### Nexus 4 portrait

[![device-nexus4-port-2015-11-19-021911.png](screenshots/thumbnails/device-nexus4-port-2015-11-19-021911.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus4-port-2015-11-19-021911.png)
[![device-nexus4-port-2015-11-19-022005.png](screenshots/thumbnails/device-nexus4-port-2015-11-19-022005.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus4-port-2015-11-19-022005.png)
[![device-nexus4-port-2015-11-19-022042.png](screenshots/thumbnails/device-nexus4-port-2015-11-19-022042.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus4-port-2015-11-19-022042.png)
[![device-nexus4-port-2015-11-19-022205.png](screenshots/thumbnails/device-nexus4-port-2015-11-19-022205.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus4-port-2015-11-19-022205.png)
[![device-nexus4-port-2015-11-19-022253.png](screenshots/thumbnails/device-nexus4-port-2015-11-19-022253.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus4-port-2015-11-19-022253.png)
[![device-nexus4-port-2015-11-19-022327.png](screenshots/thumbnails/device-nexus4-port-2015-11-19-022327.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus4-port-2015-11-19-022327.png)
[![device-nexus4-port-2015-11-19-022349.png](screenshots/thumbnails/device-nexus4-port-2015-11-19-022349.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus4-port-2015-11-19-022349.png)
[![device-nexus4-port-2015-11-19-022509.png](screenshots/thumbnails/device-nexus4-port-2015-11-19-022509.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus4-port-2015-11-19-022509.png)



###### Nexus 7 portrait

[![device-nexus7-port-2015-11-16-191053.png](screenshots/thumbnails/device-nexus7-port-2015-11-16-191053.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-port-2015-11-16-191053.png)
[![device-nexus7-port-2015-11-16-191142.png](screenshots/thumbnails/device-nexus7-port-2015-11-16-191142.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-port-2015-11-16-191142.png)
[![device-nexus7-port-2015-11-16-191213.png](screenshots/thumbnails/device-nexus7-port-2015-11-16-191213.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-port-2015-11-16-191213.png)
[![device-nexus7-port-2015-11-16-191252.png](screenshots/thumbnails/device-nexus7-port-2015-11-16-191252.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-port-2015-11-16-191252.png)
[![device-nexus7-port-2015-11-16-192039.png](screenshots/thumbnails/device-nexus7-port-2015-11-16-192039.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-port-2015-11-16-192039.png)



###### Nexus 7 landscape

[![device-nexus7-land-2015-11-16-190317.png](screenshots/thumbnails/device-nexus7-land-2015-11-16-190317.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-land-2015-11-16-190317.png)
[![device-nexus7-land-2015-11-16-190351.png](screenshots/thumbnails/device-nexus7-land-2015-11-16-190351.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-land-2015-11-16-190351.png)
[![device-nexus7-land-2015-11-16-190528.png](screenshots/thumbnails/device-nexus7-land-2015-11-16-190528.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-land-2015-11-16-190528.png)
[![device-nexus7-land-2015-11-16-190558.png](screenshots/thumbnails/device-nexus7-land-2015-11-16-190558.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-land-2015-11-16-190558.png)
[![device-nexus7-land-2015-11-16-190637.png](screenshots/thumbnails/device-nexus7-land-2015-11-16-190637.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-land-2015-11-16-190637.png)
[![device-nexus7-land-2015-11-16-190704.png](screenshots/thumbnails/device-nexus7-land-2015-11-16-190704.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-land-2015-11-16-190704.png)
[![device-nexus7-land-2015-11-16-190752.png](screenshots/thumbnails/device-nexus7-land-2015-11-16-190752.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-land-2015-11-16-190752.png)
[![device-nexus7-land-2015-11-16-190953.png](screenshots/thumbnails/device-nexus7-land-2015-11-16-190953.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-nexus7-land-2015-11-16-190953.png)



###### My Tab 10 Q portrait

[![device-mytab-port-2015-11-16-165101.png](screenshots/thumbnails/device-mytab-port-2015-11-16-165101.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-mytab-port-2015-11-16-165101.png)
[![device-mytab-port-2015-11-16-165159.png](screenshots/thumbnails/device-mytab-port-2015-11-16-165159.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-mytab-port-2015-11-16-165159.png)
[![device-mytab-port-2015-11-16-165251.png](screenshots/thumbnails/device-mytab-port-2015-11-16-165251.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-mytab-port-2015-11-16-165251.png)
[![device-mytab-port-2015-11-16-165417.png](screenshots/thumbnails/device-mytab-port-2015-11-16-165417.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-mytab-port-2015-11-16-165417.png)




###### My Tab 10 Q landscape

[![device-mytab-land-2015-11-16-164715.png](screenshots/thumbnails/device-mytab-land-2015-11-16-164715.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-mytab-land-2015-11-16-164715.png)
[![device-mytab-land-2015-11-16-164859.png](screenshots/thumbnails/device-mytab-land-2015-11-16-164859.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-mytab-land-2015-11-16-164859.png)
[![device-mytab-land-2015-11-16-164929.png](screenshots/thumbnails/device-mytab-land-2015-11-16-164929.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-mytab-land-2015-11-16-164929.png)
[![device-mytab-land-2015-11-16-165547.png](screenshots/thumbnails/device-mytab-land-2015-11-16-165547.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-mytab-land-2015-11-16-165547.png)
[![device-mytab-land-2015-11-16-165624.png](screenshots/thumbnails/device-mytab-land-2015-11-16-165624.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-mytab-land-2015-11-16-165624.png)
[![device-mytab-land-2015-11-16-165916.png](screenshots/thumbnails/device-mytab-land-2015-11-16-165916.png)](https://raw.githubusercontent.com/langara/MyIntent/myintent/screenshots/device-mytab-land-2015-11-16-165916.png)


