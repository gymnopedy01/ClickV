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
import android.widget.Toast;

import com.google.zxing.client.android.R;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.URIParsedResult;

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
    "otpauth:"
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
    Toast.makeText(getActivity(), "인증을 연결합니다.", Toast.LENGTH_SHORT).show();
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
