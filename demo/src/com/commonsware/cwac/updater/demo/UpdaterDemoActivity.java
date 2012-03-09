/***
Copyright (c) 2012 CommonsWare, LLC

Licensed under the Apache License, Version 2.0 (the "License"); you may
not use this file except in compliance with the License. You may obtain
a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.commonsware.cwac.updater.demo;

import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.commonsware.cwac.updater.ConfirmationStrategy;
import com.commonsware.cwac.updater.DownloadStrategy;
import com.commonsware.cwac.updater.NotificationConfirmationStrategy;
import com.commonsware.cwac.updater.SimpleHttpDownloadStrategy;
import com.commonsware.cwac.updater.SimpleHttpVersionCheckStrategy;
import com.commonsware.cwac.updater.UpdateRequest;
import com.commonsware.cwac.updater.VersionCheckStrategy;

public class UpdaterDemoActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    TextView versionCodeLabel=(TextView)findViewById(R.id.versionCode);

    try {
      int currentVersionCode=
          getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

      versionCodeLabel.setText(String.valueOf(currentVersionCode));
    }
    catch (Exception e) {
      Log.e("UpdaterDemoActivity", "An impossible exception", e);
    }

    UpdateRequest.Builder builder=new UpdateRequest.Builder(this);

    builder.setVersionCheckStrategy(buildVersionCheckStrategy())
           .setPreDownloadConfirmationStrategy(buildPreDownloadConfirmationStrategy())
           .setDownloadStrategy(buildDownloadStrategy())
           .setPreInstallConfirmationStrategy(buildPreInstallConfirmationStrategy())
           .execute();
  }

  VersionCheckStrategy buildVersionCheckStrategy() {
    return(new SimpleHttpVersionCheckStrategy(
                                              "http://misc.commonsware.com/update.json"));
  }

  ConfirmationStrategy buildPreDownloadConfirmationStrategy() {
    // return(new ImmediateConfirmationStrategy());
    Notification n=
        new Notification(android.R.drawable.stat_notify_chat,
                         "Update availalble", System.currentTimeMillis());

    n.setLatestEventInfo(this, "Update Available",
                         "Click to download the update!", null);
    n.flags|=Notification.FLAG_AUTO_CANCEL;

    return(new NotificationConfirmationStrategy(n));
  }

  DownloadStrategy buildDownloadStrategy() {
    return(new SimpleHttpDownloadStrategy());
  }

  ConfirmationStrategy buildPreInstallConfirmationStrategy() {
//    return(new ImmediateConfirmationStrategy());
    Notification n=
        new Notification(android.R.drawable.stat_notify_chat,
                         "Update ready to install", System.currentTimeMillis());

    n.setLatestEventInfo(this, "Update Ready to Install",
                         "Click to install the update!", null);
    n.flags|=Notification.FLAG_AUTO_CANCEL;

    return(new NotificationConfirmationStrategy(n));
  }
}