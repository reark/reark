Reference Architecture for Android using RxJava
===============================================
[![](https://jitpack.io/v/reark/reark.svg)](https://jitpack.io/#reark/reark)
[![Build Status](https://travis-ci.org/reark/reark.svg?branch=master)](https://travis-ci.org/reark/reark)

This is an ambitious reference project of what can be done with RxJava to create an app based on streams of data.

![High-level architecture](http://reark.github.io/reark/images/architecture2.1.png "High-level architecture")

In the repository you will find several library-like solutions that might or might not be reusable. Parts of the architecture are, however, already used in real applications, and as the parts mature they are likely to be extracted into separate libraries. If you follow the general guidelines illustrated here, your application should be in a position to have portions of it easily replaced as more or less official solutions emerge. The architecture is designed to support large-scale applications with remote processes, such as those used in widgets.

The project uses the open GitHub repositories API. You might hit a rate limit if you use the API extensively.


Including in an Android Project
===============================

```groovy
allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.reark:reark:0.3'
}
```

Alternatively, you may consider including Reark as a Git submodule.


Application Structure
=====================

To use the app start writing a search in the text box of at least 3 characters. This will trigger a network request and the five first results of which will be shown as a list. The input also throttled in a way that makes it trigger when the user stops typing. This is a very good basic example of Rx streams.

`.filter((string) -> string.length() > 2)
.debounce(500, TimeUnit.MILLISECONDS)`


The Basic Data Flow
===================

As seen above, the user input is turned into a series of strings in the View layer, which the View Model then processes.

When we are ready to execute the actual search, an Intent is broadcast and the NetworkService handles it, starting a network request in its own remote process. Once the network request is finished, NetworkService inserts the fetched data into the ContentProviders of GitHubRepositorySearchStore and GitHubRepositoryStore. All of the interested processes are notified through the onChange mechanism of ContentProviders.

The search store contains only the ids of the results of the query, while the actual repository POJOs are written in the repository store. A repository POJO can thus be contained in multiple searches, which can often be the case if the first match stays the same, for instance.

This structure nevertheless enables us to keep the data consistent across the application&mdash;even if the same data object is updated from multiple APIs.


Tests
=====

You can run the test(s) on command line in the project folder:

`./gradlew test`

Currently the tests are not extensive, but we are working on it. The View Models in particular will be fully unit tested.


View Models
===========

This architecture makes use of View Models, which are not commonly used in Android applications.

_View Model (VM) encapsulates the state of a view and the logic related to it into a separate class. This class is independent of the UI platform and can be used as the “engine” for several different fragments. The view is a dumb rendering component that displays the state as constructed in the VM._

Possible values that the view model can expose:

  * Background color
  * Page title text resource id
  * Selected tab index
  * Animation state of a sliding menu

The same VM can be used with entirely different views&mdash;for instance one on tablet and another one on mobile. The VM is also relatively easy to unit test and immune to changes in the platform.


An Android Approach to View Models
----------------------------------

For our purposes let us treat fragments as the entry point to the view layer. The view layer is an abstraction and is not to be confused with the Android View class, the descendants of which will still be used for displaying the data once the Fragment has received the latest values from the VM. The point is our fragments will not contain any program logic.

_The goal is to take all data retrieval and data processing away from the fragments and encapsulate it into a nice and clean View Model._ This VM can then be unit tested and re-used where ever applicable.

In this example project we use RxJava to establish the necessary observable/observer patterns to connect the view model to the view, but an alternative solution could be used as well.


The View Model Life Cycle
-------------------------

View Model has two important responsibilities:

  * Retrieve data from a data source
  * Expose the data as simple values to the view layer (fragments)

The suggested VM life cycle is much like that of fragment, and not by coincidence.

![View Model life cycle](http://reark.github.io/reark/images/android_rx_viewmodel_lifecycle.svg "View Model life cycle")

The data layer offers a permanent subscription to a data source, and it pushes a new item whenever new data arrives. This connection should be established in onViewCreated and released in onViewDestroyed. **More details of a good data layer architecture will follow in another article.**

It is a good idea to separate the data subscription and the data retrieval. You first passively subscribe to a data stream, and then you use another method to start the retrieval of new items for that particular stream (most commonly via http). This way, if desired, you are able to refresh the data multiple times without needing to break the subscription—or in some cases not to refresh it at all but use the latest cached value.


Exposing View Model Properties
------------------------------

Strictly speaking there are two kinds of properties

  * Actual properties, such as boolean flags
  * Events

In RxJava the actual properties would typically be stored as BehaviorSubjects and exposed as Observables. This way whoever subscribes to the property will immediately receive the latest value and update the UI, even if the value had been updated while paused.

In the second case, where the property is used for events, you might want to opt for a simple PublishSubject that has no “memory”. When onResume is called the UI would not receive any events that occurred while paused.


Threading and Testing View Models
---------------------------------

While it is possible to use TestSchedulers, I believe it is best to keep the VMs simple and not to include threading in them. If there is a risk some of the inputs to the VM come from different threads you can use thread-safe types or declare fields as volatile.

The safest way to ensure the main thread is to use `.observeOn(AndroidSchedulers.mainThread())` when the fragment subscribes to the VM properties. This makes the subscription to always trigger asynchronously, but usually it is more of a benefit than a disadvantage.


Data Layer
==========

The application specific Data Layer is responsible for fetching and storing data. A fetch operation updates the centrally stored values in ContentProviders and everyone who is interested will get the update.

For a stream of data we use the DataStreamNotification, which differs slightly from the typical RxJava Notification.

* The observable never completes: there could always be new data
* Errors in fetching the data do not break the subscription: once the problem is resolved the data starts to flow again
* FetchStart event is sent when a network operation is start and it can be used to display loading state in the UI

Currently only the GitHubRepositorySearch supports DataStreamNotifications, though it is not difficult to add to any data type.


Consuming Data in the View
--------------------------

When data comes in we render it in the view, simple as that. It does not actually matter what triggered the update of the data—it could be a background sync, user pressing a button or a change in another part of the UI. The data can arrive at any given time and **any number of times**.


Store
-----

Store is a container that allows subscribing to data of a particular type and identification. Whenever data requested becomes available, a new immutable value is pushed as onNext to all subscribers. The Store does not concern itself with where the data comes from.

Conceptually the concrete backing of a Store can be a static in-memory hash, plain SQLite or a ContentProvider. In case the same data is to be used from multiple <i>processes</i> there needs to be a mechanism that detects changes in the persisted data and propagates them to all subscribers. Android ContentProviders, of course, do this automatically, making them the perfect candidates.

In this example project the Stores are backed by SQLite ContentProviders, which serializes the values as JSON strings with an id column for queries. This could be optimized by adding columns for all fields of the POJO, but on the other hand it adds boilerplate code that has to be maintained. In case of performance problems the implementation can be changed without affecting the rest of the application.

For caching purposes it is usually good to include timestamps into the structure of the Store. Determining when to update the data is not conceptually not a concern of the Store, though, it simply holds the data. Cache-expiry of the data is still on the list of things-to-do in this reference project.



Fetcher
-------

The responsibilities of the Fetcher are:

* Send and process the network requests
* Populate Stores with the received values
* Update the NetworkRequestState of fetch operations
* Filter duplicate ongoing requests

The Fetchers run exclusively in the NetworkService, making them hidden from the rest of the application. NetworkService has its own ServiceDataLayer that allows it to trigger fetch operations.


License
=======

    The MIT License

    Copyright (c) 2013-2017 reark project contributors

    https://github.com/reark/reark/graphs/contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
