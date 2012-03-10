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

import android.app.PendingIntent;
import android.content.Context;
import android.os.Parcelable;

/**
 * Interface for confirming a step (download an APK,
 * install an APK). 
 *
 */
public interface ConfirmationStrategy extends Parcelable {
  /**
   * Called by UpdateService to confirm whether or not
   * it is OK to proceed to the next phase in the
   * update process. Will be called on a background thread
   * and so cannot directly affect the UI. If you know
   * that it is OK to proceed (e.g., always or based on
   * a preference), return true. If you need to get 
   * input from the user (e.g., Notification, dialog-themed
   * Activity), return false, but then execute the supplied
   * PendingIntent if the user agrees to go ahead.
   * 
   * @param ctxt generic Context object for raising Notification, etc.
   * @param contentIntent PendingIntent to be used if confirmation is asynchronous
   * @return true if we can go ahead, false if we don't know yet
   */
  public boolean confirm(Context ctxt, PendingIntent contentIntent);
}
