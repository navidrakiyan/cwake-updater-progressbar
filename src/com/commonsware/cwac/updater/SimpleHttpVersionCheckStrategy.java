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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

public class SimpleHttpVersionCheckStrategy implements
    VersionCheckStrategy {
  private static final String JSON_VERSION_CODE="versionCode";
  private static final String JSON_UPDATE_URL="updateURL";
  protected String url=null;
  protected String updateURL=null;

  public SimpleHttpVersionCheckStrategy(String url) {
    this.url=url;
  }

  private SimpleHttpVersionCheckStrategy(Parcel in) {
    url=in.readString();
  }

  @Override
  public int getVersionCode() throws Exception {
    HttpURLConnection conn=
        (HttpURLConnection)new URL(url).openConnection();
    int result=-1;

    try {
      conn.connect();

      int status=conn.getResponseCode();

      if (status == 200) {
        InputStream is=conn.getInputStream();
        BufferedReader in=new BufferedReader(new InputStreamReader(is));
        StringBuilder buf=new StringBuilder();
        String str;

        while ((str=in.readLine()) != null) {
          buf.append(str);
          buf.append('\n');
        }

        in.close();

        JSONObject json=new JSONObject(buf.toString());

        result=json.getInt(JSON_VERSION_CODE);
        updateURL=json.getString(JSON_UPDATE_URL);
      }
      else {
        throw new RuntimeException(
                                   String.format("Received %d from server",
                                                 status));
      }
    }
    finally {
      conn.disconnect();
    }

    return(result);
  }
  
  @Override
  public String getUpdateURL() {
    return(updateURL);
  }

  @Override
  public int describeContents() {
    return(0);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(url);
  }

  public static final Parcelable.Creator<SimpleHttpVersionCheckStrategy> CREATOR=
      new Parcelable.Creator<SimpleHttpVersionCheckStrategy>() {
        public SimpleHttpVersionCheckStrategy createFromParcel(Parcel in) {
          return(new SimpleHttpVersionCheckStrategy(in));
        }

        public SimpleHttpVersionCheckStrategy[] newArray(int size) {
          return(new SimpleHttpVersionCheckStrategy[size]);
        }
      };
}
