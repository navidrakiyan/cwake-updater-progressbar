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

package com.commonsware.cwac.updater;

import java.security.InvalidParameterException;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.commonsware.cwac.wakeful.WakefulIntentService;

public class UpdateRequest {
  public static final int PHASE_VERSION_CHECK=1;
  public static final int PHASE_PRE_DOWNLOAD=2;
  public static final int PHASE_DOWNLOAD=3;
  public static final int PHASE_PRE_INSTALL=4;
  public static final int PHASE_INSTALL=5;

  private static final String EXTRA_VCS=
      "com.commonsware.cwac.updater.EXTRA_VCS";
  private static final String EXTRA_CONFIRM_DOWNLOAD=
      "com.commonsware.cwac.updater.EXTRA_CONFIRM_DOWNLOAD";
  private static final String EXTRA_PHASE=
      "com.commonsware.cwac.updater.EXTRA_PHASE";
  private static final String EXTRA_DS=
      "com.commonsware.cwac.updater.EXTRA_DS";
  private static final String EXTRA_CONFIRM_INSTALL=
      "com.commonsware.cwac.updater.EXTRA_CONFIRM_INSTALL";
  private static final String EXTRA_UPDATE_URL=
      "com.commonsware.cwac.updater.EXTRA_UPDATE_URL";
  private static final String EXTRA_INSTALL_URI=
      "com.commonsware.cwac.updater.EXTRA_INSTALL_URI";
  static final String EXTRA_COMMAND=
      "com.commonsware.cwac.updater.EXTRA_COMMAND";
  protected Intent cmd=null;

  UpdateRequest(Intent cmd) {
    this.cmd=cmd;
  }

  VersionCheckStrategy getVersionCheckStrategy() {
    return((VersionCheckStrategy)cmd.getParcelableExtra(EXTRA_VCS));
  }

  ConfirmationStrategy getPreDownloadConfirmationStrategy() {
    return((ConfirmationStrategy)cmd.getParcelableExtra(EXTRA_CONFIRM_DOWNLOAD));
  }

  ConfirmationStrategy getPreInstallConfirmationStrategy() {
    return((ConfirmationStrategy)cmd.getParcelableExtra(EXTRA_CONFIRM_INSTALL));
  }

  DownloadStrategy getDownloadStrategy() {
    return((DownloadStrategy)cmd.getParcelableExtra(EXTRA_DS));
  }

  int getPhase() {
    return(cmd.getIntExtra(EXTRA_PHASE, PHASE_VERSION_CHECK));
  }

  String getUpdateURL() {
    return(cmd.getStringExtra(EXTRA_UPDATE_URL));
  }

  Uri getInstallUri() {
    return(Uri.parse(cmd.getStringExtra(EXTRA_INSTALL_URI)));
  }

  public static class Builder {
    protected Context ctxt=null;
    protected Intent cmd=null;

    public Builder(Context ctxt) {
      this.ctxt=ctxt;
      cmd=new Intent(ctxt, UpdateService.class);
    }

    Builder(Context ctxt, Intent cmd) {
      this.ctxt=ctxt;
      this.cmd=new Intent(cmd);
    }

    public Builder setVersionCheckStrategy(VersionCheckStrategy strategy) {
      cmd.putExtra(EXTRA_VCS, strategy);

      return(this);
    }

    public Builder setPreDownloadConfirmationStrategy(ConfirmationStrategy strategy) {
      cmd.putExtra(EXTRA_CONFIRM_DOWNLOAD, strategy);

      return(this);
    }

    public Builder setPreInstallConfirmationStrategy(ConfirmationStrategy strategy) {
      cmd.putExtra(EXTRA_CONFIRM_INSTALL, strategy);

      return(this);
    }

    public Builder setDownloadStrategy(DownloadStrategy strategy) {
      cmd.putExtra(EXTRA_DS, strategy);

      return(this);
    }

    public void execute() {
      Bundle b=cmd.getExtras();
      
      if (!b.containsKey(EXTRA_VCS) || !b.containsKey(EXTRA_DS)) {
        throw new InvalidParameterException("Missing a strategy!");
      }

      WakefulIntentService.sendWakefulWork(ctxt, cmd);
    }

    void setPhase(int phase) {
      cmd.putExtra(EXTRA_PHASE, phase);
    }

    void setUpdateURL(String updateURL) {
      cmd.putExtra(EXTRA_UPDATE_URL, updateURL);
    }

    void setInstallUri(Uri apk) {
      cmd.putExtra(EXTRA_INSTALL_URI, apk.toString());
    }

    PendingIntent buildPendingIntent() {
      Intent i=new Intent(ctxt, WakefulReceiver.class);

      i.putExtra(EXTRA_COMMAND, cmd);

      return(PendingIntent.getBroadcast(ctxt,
                                        0,
                                        i,
                                        PendingIntent.FLAG_UPDATE_CURRENT));
    }
  }
}
