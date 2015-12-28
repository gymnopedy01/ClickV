/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.result;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.client.android.R;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.URIParsedResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Offers appropriate actions for URLS.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ClickVURIResultHandler extends ResultHandler {
  // URIs beginning with entries in this array will not be saved to history or copied to the
  // clipboard for security.
  private static final String[] SECURE_PROTOCOLS = {
    "otpauth:",
    "clickv:"
  };

  private static final int[] buttons = {
    R.string.clickv_btn1,
  };

  public ClickVURIResultHandler(Activity activity, ParsedResult result) {
    super(activity, result);
  }

  @Override
  public int getButtonCount() {
      return buttons.length;
  }

  @Override
  public int getButtonText(int index) {
    return buttons[index];
  }

  @Override
  public Integer getDefaultButtonID() {
    return 0;
  }

  @Override
  public void handleButtonPress(int index) {
    URIParsedResult uriResult = (URIParsedResult) getResult();
    String uri = uriResult.getURI();
    switch (index) {
      case 0:
//        openURL(uri);
        registerClickV(uri);
        break;
      case 1:
        shareByEmail(uri);
        break;
      case 2:
        shareBySMS(uri);
        break;
      case 3:
        searchBookContents(uri);
        break;
    }
  }
  public void registerClickV(String uri) {

    String host = "http://lit-taiga-5566.herokuapp.com/";
    String query = null, path = null;
    String telId = getTelNumber() == null ? getMacAddress() : getTelNumber();

    try {
      URL url = new URL(uri.replaceAll("clickv://", host));
      query = url.getQuery();
      path = url.getProtocol() + "://"+ url.getHost() + "/" + url.getPath();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    Toast.makeText(getActivity(), "인증을 연결합니다 :: " + query , Toast.LENGTH_SHORT).show();

    try {

      new ServerConnenctionTask().execute(path, query, telId);

      Toast.makeText(getActivity(), "호출완료" + uri , Toast.LENGTH_SHORT).show();

    }catch(Exception e){
      e.printStackTrace();
      Toast.makeText(getActivity(), "오류발생." + uri , Toast.LENGTH_SHORT).show();
    }

  }

  public String getTelNumber() {
    TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
    return telephonyManager.getLine1Number();
  }

  public String getMacAddress() {
    WifiManager wifiManager = (WifiManager) getActivity().getSystemService(getActivity().WIFI_SERVICE);
    return wifiManager.getConnectionInfo().getMacAddress();
  }

  public class ServerConnenctionTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {

      for (String ss : strings) {
        Log.d("ClickV", "strings : " + ss);
      }

      String command = strings[0];
      String query = strings[1] + "&telId=" + strings[2];

      HttpClient client = new DefaultHttpClient();
      HttpGet request = new HttpGet(command + "?" + query);

      HttpResponse response = null;

      String s = null;
      try {
        response = client.execute(request);
        HttpEntity entity = response.getEntity();
        s = EntityUtils.toString(entity);
      } catch (Exception e){
        e.printStackTrace();
      }

      Log.d("ClickV", "s : " + s);

      ObjectMapper jacksonMapper = new ObjectMapper();
      Result r = null;
      try {
        r = (Result) jacksonMapper.readValue(s,Result.class);
        Log.d("ClickV", "message : " + r.getMessage());

      } catch (IOException e) {
        e.printStackTrace();
      }

    return r.getMessage();

  }

    @Override
    protected void onPostExecute(String s) {

      super.onPostExecute(s);

      Toast.makeText(getActivity(), s , Toast.LENGTH_SHORT).show();

    }
  }


    @Override
  public int getDisplayTitle() {
    return R.string.result_uri;
  }

  @Override
  public boolean areContentsSecure() {
    URIParsedResult uriResult = (URIParsedResult) getResult();
    String uri = uriResult.getURI().toLowerCase(Locale.ENGLISH);
    for (String secure : SECURE_PROTOCOLS) {
      if (uri.startsWith(secure)) {
        return true;
      }
    }
    return false;
  }
}
