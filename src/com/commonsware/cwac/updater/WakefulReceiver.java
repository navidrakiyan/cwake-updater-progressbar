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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.commonsware.cwac.wakeful.WakefulIntentService;

/**
 * Forwards a command (an Intent packaged as an extra in the
 * broadcast Intent) along to a WakefulIntentService,
 * specifically UpdateService.
 * 
 * This is necessary to support asynchronous user
 * confirmation, such as via a Notification. We need the
 * UpdateService to be able to continue to the next phase,
 * but the UpdateService needs to be a WakefulIntentService
 * to have a shot at completing its work. Hence, we cannot
 * simply have the Notification use a getService()
 * PendingIntent pointing at UpdateService -- instead, we
 * need to go through the standard WakefulIntentService
 * process, involving a BroadcastReceiver calling
 * sendWakefulWork().
 * 
 */
public class WakefulReceiver extends BroadcastReceiver {
  /* (non-Javadoc)
   * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
   */
  @Override
  public void onReceive(Context context, Intent intent) {
    Intent cmd=
        (Intent)intent.getParcelableExtra(UpdateRequest.EXTRA_COMMAND);

    WakefulIntentService.sendWakefulWork(context, cmd);
  }
}
