CWAC Updater: App Updates, No Market Required
=============================================

**Work on this project has been suspended &mdash; please seek alternative solutions at this time**

You might not be distributing your app through an online market
like ~~the Android Market~~ Google Play.
Perhaps your app is for internal-use
within a business, non-profit, or other organization. Perhaps
you are distributing a beta release to power users. Perhaps you
are selling your app directly to users rather than having a hunk
of your revenue go to markets.

In any of those cases, you will need to handle updating your
app yourself, as no market will do that work for you.

`Updater` is a library designed to allow your app to be self-updating.
While the library ships with a stock implementation of things
like detecting a new version and downloading that new APK, you can
plug in your own implementations (e.g., do all this over the
corporate VPN).

This is available as a JAR file from the downloads area of
this GitHub repo. The project itself is set up as an Android
library project, in case you wish to use the source code in
that fashion. Note that if you use the JAR, you will also
need the JARs for the dependencies, listed later in this
document.

Usage
-----
Compared to many CWAC components, this one requires a fair of
explaining, even if the actual implementation is not that
difficult. You will see a sample implementation in the `demo/`
sub-project, and portions of that sample will be referenced here.

### Strategies

The `Updater` library uses a pluggable strategy approach, to allow
you to extend the library with your own implementations. There
are three types of strategies presently in use:

- a strategy for determining if there is an update available
- a strategy for downloading the update
- a strategy for confirming with the user if an update should be
downloaded or if the downloaded update should now be installed

We will get into more details of the actual strategy interfaces,
the stock implementations, and how you can write your own later
in this document.

### Manifest

You will need to add the `INTERNET`, `WAKE_LOCK`, and
`WRITE_EXTERNAL_STORAGE` permissions to your manifest. In theory,
the latter one might not be required, if you implement your
own download strategy.

You will also need to add `com.commonsware.cwac.updater.UpdateService`
as a `<service>` to your manifest &mdash; no `<intent-filter>` is
required.

And, if you intend on using the `NotificationConfirmationStrategy`,
where the user will be prompted via a `Notification` to move to the
next phase of the update, you will also need to add
`com.commonsware.cwac.updater.WakefulReceiver` as a `<receiver>`
in your manifest. Once again, no `<intent-filter>` is
required.

### Timing

The sample application demonstrates checking for updates from
`onCreate()` of an activity. That's certainly possible in production.

Other possibilities that should be supported (and represent bugs
if they don't work) include:

- Kicking off the update check from a custom `Application` object
or a static data member, to basically check every time the process
starts

- Scheduling an update check using `AlarmManager`, either at a
user-defined time/frequency or something likely to be reasonable
(e.g., daily at 4am)

- Using C2DM to alert devices of an available update and starting
the update process that way (though you should have some sort of
fallback mechanism, as C2DM is not 100% reliable)

### Requesting the Update Check

To have `Updater` check for new versions of your app and install
them, you need to create an instance of `UpdateRequest.Builder`
(in the `com.commonsware.cwac.updater` package), fill in the
strategies you want to use for the different phases, and tell
the `Builder` to `execute()` the work. The actual execution will all be done
on background threads, so it should be safe to do this work from
the main application thread if that is convenient.

For example, here is a sample `Builder` configuration and invocation:

```java
UpdateRequest.Builder builder=new UpdateRequest.Builder(this);

builder.setVersionCheckStrategy(buildVersionCheckStrategy())
       .setPreDownloadConfirmationStrategy(buildPreDownloadConfirmationStrategy())
       .setDownloadStrategy(buildDownloadStrategy())
       .setPreInstallConfirmationStrategy(buildPreInstallConfirmationStrategy())
       .execute();
```

### VersionCheckStrategy

You will need to supply the `Builder` with an implementation
of the `VersionCheckStrategy` interface. This object will be
responsible for determining if an update is available. This
interface requires two methods:

- `getVersionCode()` returns the `android:versionCode` of the
updated APK available for download
- `getUpdateURL()` returns a `String` that provides information
on where to download the update from, with the typical implementation
being an HTTP URL

Note that `getUpdateURL()` will not be called until after
`getVersionCode()` is called and returns. Hence, if you are
downloading information to determine update availability, do the
download in `getVersionCode()`, saving the URL for the update
in the `VersionCheckStrategy` object to return later via
`getUpdateURL()`. These methods are called on a background thread,
so they can take whatever time is needed and should return their
results synchronously.

There is a stock implementation of this interface, `SimpleHttpVersionCheckStrategy`,
that takes a URL of a JSON file to download. This JSON file needs
to be a JSON object (i.e., `{}`) with a `versionCode` integer and an
`updateURL` string property.

### ConfirmationStrategy

You will need to supply two `ConfirmationStrategy` objects to
the `Builder`. One will be used if `getVersionCode()` of the
`VersionCheckStrategy` indicates that there is an update available.
The other will be used once the `DownloadStrategy` has downloaded
the update.

The job of a `ConfirmationStrategy` is to confirm that we should
indeed move to the next phase of the work:

- If an update is available, the `setPreDownloadConfirmationStrategy()`
will be used to confirm we should continue and download the update

- If the update has been downloaded, the
`setPreInstallConfirmationStrategy()` will be used to confirm that
it is OK to go ahead and do the install

The only method required on `ConfirmationStrategy` is
`confirm()`. This returns a boolean, `true` indicating to go
ahead, `false` indicating that we don't know yet whether to go
ahead. `confirm()` is supplied two parameters:

- a generic `Context`
- a `PendingIntent`, suitable for asynchronously triggering the
next phase of the update process &mdash; use this if you return
`false` from `confirm()` and later determine that we should 
go ahead

`confirm()` is called on a background thread from a service,
so take that into account if you create a `ConfirmationStrategy`
that, say, wants to use a dialog &mdash; you will need to use
a dialog-themed `Activity` instead.

There are two stock implementations of `ConfirmationStrategy`
supplied:

- `ImmediateConfirmationStrategy` simply returns `true` from
`confirm()` and is to be used in cases where we do not need
user input to continue. For example, if you are using `AlarmManager`
to check for updates in the middle of the night, it is usually
safe to just go ahead and download now, without waiting for
user input.

- `NotificationConfirmationStrategy` raises a `Notification`
that you supply. If the user taps on the `Notification` in the
notification drawer, the process will continue. If the user
clears the `Notification`, the process is abandoned.

### DownloadStrategy

You will need to supply an instance of a `DownloadStrategy` to
the `Builder`. This object is responsible for taking the "update URL"
from the `VersionCheckStrategy` and downloading the update APK
to a local file.

The only method required on `DownloadStrategy` is `downloadAPK()`.
This returns a `Uri` to the downloaded APK. `downloadAPK()` receives
two parameters

- a generic `Context`
- the "update URL" from the `VersionCheckStrategy`

There are two stock implementations of `DownloadStrategy` supplied
by the library: `SimpleHttpDownloadStrategy`, which downloads
the APK to external storage, and `InternalHttpDownloadStrategy`, which
downloads the APK to a world-readable file on internal storage.
Presently, neither clean up the
APK, though they will get rid of the old APK before downloading a
fresh update.

Ideally, use `InternalHttpDownloadStrategy` only for small APK files or
on API Level 11 or higher (where internal and external storage share the
same data partition, so space concerns fall away).

Dependencies
------------
This project relies upon the [CWAC WakefulIntentService](https://github.com/commonsguy/cwac-wakeful) project.
A copy of compatible JARs can be found in the `libs/` directory
of the project, though you are welcome to try newer ones, or
ones that you have patched yourself.

This library at present requires Android 2.2 (API Level 8) or
higher. To *build* the library, you will need API Level 14, as the
library conditionally uses various newer APIs.

Version
-------
This is version v0.0.2 of this module, meaning it hasn't been
laughed into oblivion just yet.

Demo
----
In the `demo/` sub-project you will find
a sample activity that demonstrates the use of `Updater`.

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Questions
---------
If you have questions regarding the use of this code, please post a question
on [StackOverflow](http://stackoverflow.com/questions/ask) tagged with `commonsware` and `android`. Be sure to indicate
what CWAC module you are having issues with, and be sure to include source code 
and stack traces if you are encountering crashes.

If you have encountered what is clearly a bug, or a feature request,
please post an [issue](https://github.com/commonsguy/cwac-strictmodeex/issues).
Be certain to include complete steps for reproducing the issue.

Do not ask for help via Twitter.

Also, if you plan on hacking
on the code with an eye for contributing something back,
please open an issue that we can use for discussing
implementation details. Just lobbing a pull request over
the fence may work, but it may not.

Who Made This?
--------------
<a href="http://commonsware.com">![CommonsWare](http://commonsware.com/images/logo.png)</a>

Release Notes
-------------
- v0.0.2: added `InternalHttpDownloadStrategy`
- v0.0.1: initial release

