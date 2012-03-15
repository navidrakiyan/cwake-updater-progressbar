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

/**
 * VersionCheckStrategy implementation that downloads
 * a public-visible JSON document via HTTP and extracts
 * information about the available version from it.
 * 
 * The JSON document needs to be a JSON object containing
 * a versionCode and an updateURL value. The versionCode
 * should be the android:versionCode value of the latest
 * APK available for download. The updateURL can provide
 * information to your chosen DownloadStrategy of where
 * to download the APK. For example, the updateURL could
 * be a URL to a publicly-visible APK for download. The
 * JSON document can have other contents if desired, but
 * they will be ignored.
 * 
 * This implementation is fairly simplistic, just blindly
 * downloading the document. In particular, it will not
 * handle a failover (e.g., drop off WiFi and fail over
 * to 3G).
 *
 */
public class SimpleHttpVersionCheckStrategy implements
    VersionCheckStrategy {
  private static final String JSON_VERSION_CODE="versionCode";
  private static final String JSON_UPDATE_URL="updateURL";
  protected String url=null;
  protected String updateURL=null;

  /**
   * Basic constructor
   * @param url Location of the JSON document to download
   */
  public SimpleHttpVersionCheckStrategy(String url) {
    this.url=url;
  }

  /**
   * Constructor for use with Parcelable
   * @param in Parcel from which to reconstitute this object
   */
  private SimpleHttpVersionCheckStrategy(Parcel in) {
    url=in.readString();
  }

  /* (non-Javadoc)
   * @see com.commonsware.cwac.updater.VersionCheckStrategy#getVersionCode()
   */
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
  
  /* (non-Javadoc)
   * @see com.commonsware.cwac.updater.VersionCheckStrategy#getUpdateURL()
   */
  @Override
  public String getUpdateURL() {
    return(updateURL);
  }

  /* (non-Javadoc)
   * @see android.os.Parcelable#describeContents()
   */
  @Override
  public int describeContents() {
    return(0);
  }

  /* (non-Javadoc)
   * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
   */
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(url);
  }

  /**
   * Required to complete Parcelable interface. Creates
   * an SimpleHttpVersionCheckStrategy instance or array
   * upon demand.
   */
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
