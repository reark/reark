[Initial documentation](http://tehmou.github.io/rx-android-architecture/)

Reference Architecture for Android using RxJava
-----------------------------------------------

This is a rough example project of what can be done with RxJava to create an app based on streams of data. It uses the open GitHub repositories API. You might hit a rate limit if you use the API extensively.

![High-level architecture](http://tehmou.github.io/rx-android-architecture/images/architecture.png "High-level architecture")

To use the app start writing a search in the text box of at least 3 characters. This will trigger a request, the five first results of which will be shown as a list.

You can run the test(s) on command line in the project folder:

`./gradlew test`

See related
[slides](http://www.slideshare.net/TimoTuominen1/rxjava-architectures-on-android-android-livecode-berlin)
