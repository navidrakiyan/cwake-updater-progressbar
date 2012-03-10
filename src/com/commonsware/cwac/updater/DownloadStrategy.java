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

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;

/**
 * Interface for downloading an update APK
 */
public interface DownloadStrategy extends Parcelable {
  /**
   * Called to have the strategy download an update APK
   * from somewhere. Will be called on a background thread,
   * so time is not an issue. The resulting download
   * needs to be a world-readable file, so the Android
   * installer can do its work.
   * 
   * @param ctxt a generic Context for use in accessing resources, etc.
   * @param updateURL value retrieved from VersionCheckStrategy to identify what you should download
   * @return Uri pointing to downloaded update APK
   * @throws Exception in case something goes wrong
   */
  Uri downloadAPK(Context ctxt, String updateURL) throws Exception;
}
