# My Blocks

An Android library: Collection of reusable views, fragments, activities, drawables and other utilities.

It is divided into seven modules:

* myactivities
* myfragments
* myviews
* mydrawables
* myres
* myloggers
* myutils

You can use any of these through JITPack:

#### Gradle Dependencies

Add following repository to your app build.gradle file:

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```


Then add dependencies to My Blocks modules you want:

```gradle
dependencies {
    // ... other dependencies here
    compile 'com.github.langara.MyBlocks:myactivities:v1.0.1-alpha'
    }
}
```

Change `myactivities` to module you want. The `myactivities` module depends on all other modules, so you don't have to add anything else if you add this one.

#### Examples

Check out these applications to find out what you can do with My Blocks library:

* [My Blocks Sample (on github)](https://github.com/langara/MyBlocksSample) - the simplest app using My Blocks lib.
* [My Blocks Sample (on google play)](https://play.google.com/store/apps/details?id=pl.mareklangiewicz.myblockssample)
* [My Blocks Sample (on youtube)](https://www.youtube.com/watch?v=R-bpq55UYGI)
* [My Test App (on github)](https://github.com/langara/MyTestApp) 
* [My Test App (on google play)](https://play.google.com/store/apps/details?id=pl.mareklangiewicz.mytestapp) 
* [My Test App (on youtube)](https://www.youtube.com/watch?v=B9FPWpQYMuc) 
* [My Intent (on github)](https://github.com/langara/MyIntent)
* [My Intent (on google play)](https://play.google.com/store/apps/details?id=pl.mareklangiewicz.myintent)
* [My Intent (on youtube)](https://www.youtube.com/watch?v=-8N_B-Jpk8k)



TODO: real documentation...

