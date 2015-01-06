---
layout: page
title: Data Layer (draft)
permalink: /data-layer/
---


Data layer is responsible for fetching and storing data. More precisely, the fetch operation updates the centrally stored values and everyone who is interested will get the update.


Consuming data in the view
--------------------------

When data comes in we render it in the view, simple as that. It does not actually matter what triggered the update of the data&mdash;it could be a background sync, user pressing a button or a change in another part of the UI. The data can arrive at any given time and <i>any number of times</i>.

In RxJava we can use a continuous subscription to establish the stream of data. This means the subscription for incoming data is not disconnected until the view is hidden and it never errors or completes.

Errors in fetching the data are handled through other mechanisms, as explained in the fetch section.


Store
-----

Store is a container that allows subscribing to data of a particular type. Whenever data of the requested type becomes available, a new immutable value is pushed as onNext to all applicable subscribers.

The concrete backing of a Store can be a static in-memory hash, plain SQLite or a content provider. In case the same data is to be used from multiple <i>processes</i> there needs to be a mechanism that detects changes in the persisted data and propagates them to all subscribers. Android content providers, of course, do this automatically, making them the perfect candidates.

For caching purposes it is usually good to include timestamps into the structure of the Store. Determining when to update the data is not conceptually a concern of the Store, though, it simply holds the data.


Fetcher
-------

Fetching data has a few separate considerations. We need to keep track of requests which are already ongoing and attach to them whenever a duplicate request is initiated. The Fetcher thus needs to contain the state of all fetch operations.

It is advisable not to expose any data directly from the fetcher, but instead associate the fetcher with a data store into which it inserts any results. The fetch operation returns an observable that simply completes or errors but never gives the data directly to the caller.

For global error handling the Fetcher can additionally have a way to subscribing to all errors.

An interesting aspect of this design is that the Fetcher can run in a separate service/process, as long as the Store uses a backing that is shared across all subscribers. This enables integrating seamlessly to the Android platform.



