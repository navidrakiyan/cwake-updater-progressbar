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

import android.os.Parcelable;

/**
 * Interface for checking to see if an update is available
 * for this particular application.
 * 
 */
public interface VersionCheckStrategy extends Parcelable {
  /**
   * Retrieves the version code of any available update.
   * This method will be called first on this object, so it
   * can (and should) also cache any other information to be
   * returned by other getters (e.g., the update URL for
   * getUpdateURL()).
   * 
   * @return the version code of the application, or -1 if
   *         there is no update
   * @throws Exception
   *           in case something goes wrong
   */
  int getVersionCode() throws Exception;

  /**
   * Returns a String to be (possibly) used by the
   * DownloadStrategy to retrieve this update. In theory,
   * this is a URL (hence the method name); in practice, it
   * can be whatever is necessary, or even the empty string
   * if nothing is required.
   * 
   * @return a URL or other download information
   * @throws Exception
   *           in case something goes wrong
   */
  String getUpdateURL() throws Exception;
}
