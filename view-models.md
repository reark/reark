---
layout: page
title: View Models
permalink: /view-models/
---

View Model (VM) encapsulates the state of a view and the logic related to it into a separate class. This class is independent of the UI platform and can be used as the "engine" for several different fragments. The view is a dumb rendering component that displays the state as constructed in the VM.

Possible values that the view model can expose:

- Background color
- Page title text resource id
- Selected tab index
- Animation state of a sliding menu

The same VM can be used with entirely different views&mdash;for instance one on tablet and another one on mobile. The VM is also relatively easy to unit test and immune to changes in the platform.


The Android Approach
--------------------

For our purposes let us treat fragments as the entry point to the view layer. The view layer is an abstraction and is not to be confused with the Android View class, the descendants of which will still be used for displaying the data once the Fragment has received the latest values from the VM. The point is our fragments will not contain any program logic.

<b>The goal is to take all data retrieval and data processing away from the fragments and encapsulate it into a nice and clean View Model.</b> This VM can then be unit tested and re-used where ever applicable.

In this example project we use RxJava to establish the necessary observable/observer patterns to connect the view model to the view, but an alternative solution could be used as well.


The View Model Life Cycle
---------------------

View Model has two important responsibilities:

  1. Retrieve data from a data source
  2. Expose the data as simple values to the view layer (fragments)

The suggested VM life cycle is much like that of fragment, and not by coincidence.

<img src="{{ "/images/android_rx_viewmodel_lifecycle.svg" | prepend: site.baseurl }}"/>

The data layer offers a permanent subscription to a data source, and it pushes a new item whenever new data arrives. This connection should be established in onViewCreated and released in onViewDestroyed. <i>More details of a good data layer architecture will follow in another article.</i>

It is a good idea to separate the data subscription and the data retrieval. You first passively subscribe to a data stream, and then you use another method to start the retrieval of new items for that particular stream (most commonly via http). This way, if desired, you are able to refresh the data multiple times without needing to break the subscription&mdash;or in some cases not to refresh it at all but use the latest cached value.


Exposing View Model Properties
---------------------

Strictly speaking there are two kinds of properties

- Actual properties, such as boolean flags
- Events

In RxJava the actual properties would typically be stored as BehaviorSubjects and exposed as Observables. This way whoever subscribes to the property will immediately receive the latest value and update the UI, even if the value had been updated while paused.

In the second case, where the property is used for events, you might want to opt for a simple PublishSubject that has no "memory". When onResume is called the UI would not receive any events that occurred while paused.


Threading and Testing
-------------------

While it is possible to use TestSchedulers, I believe it is best to keep the VMs simple and not to include threading in them. If there is a risk some of the inputs to the VM come from different threads you can use thread-safe types or declare fields as volatile.

The safest way to ensure the main thread is to use .observeOn(AndroidSchdulers.mainThread()) when the fragment subscribes to the VM properties. This makes the subscription to always trigger asynchronously, but usually it is more of a benefit than a disadvantage.


Bridging platform components
----------------------------

Most of Android components are quite hideous and of poor quality, so for an experienced developer finding the excuse to write them from scratch is not difficult. However, there are cases where it is necessary.

With a ListView the easiest way to use a view model is have a fragment pass the list values to the list adapter when ever new ones appear. The entire adapter is thus considered to be part of the view layer. Usually this approach is enough - if your need is more complex you may need to expose the entire view model to a custom adapter and add values as necessary.

ViewPager with fragments is a little trickier, and I would not recommend using a VM to orchestrate it. It is not easy to keep track of the selected page, let alone to change it through a VM. In my ideal world even all of the state and logic regarding the page change dragging would be in the VM and the component itself would only send the touch events to the VM. However, because of the defensive style in which the platform components are written, this is virtually impossible to do. I would love to see a completely custom ViewPager with a proper VM, but until this happens keep it simple.

Tabs with fragments are not even officially supported by the SDK component, and I have had good experiences writing custom ones&mdash;as long as you remember that the "replace" method in FragmentTransaction does not call all of the fragment life cycle methods you might assume it to call.