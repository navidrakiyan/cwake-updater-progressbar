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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import android.content.Context;

public class InternalHttpDownloadStrategy extends
    SimpleHttpDownloadStrategy {
  @Override
  protected File getDownloadFile(Context ctxt) {
    File updateDir=new File(ctxt.getFilesDir(), ".CWAC-Update");

    updateDir.mkdirs();

    return(new File(updateDir, "update.apk"));
  }

  @Override
  protected OutputStream openDownloadFile(Context ctxt, File apk)
                                                                 throws FileNotFoundException {
    return(ctxt.openFileOutput("update.apk",
                               Context.MODE_WORLD_READABLE));
  }
}
