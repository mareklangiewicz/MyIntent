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

* [My Blocks Sample](https://github.com/langara/MyBlocksSample) - the simplest app using My Blocks lib.
* [My Test App](https://github.com/langara/MyTestApp) 
* [My Intent](https://github.com/langara/MyIntent)

* TODO: links go Google Play

* TODO: real documentation...

