/*
 * Copyright (C) 2013-2014 Dokdo Project
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

package com.android.settings.dokdo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

import com.android.settings.R;

public class Dokdo_Platlogo extends Activity {
  final String LOG_TAG = "GraphicsSample";
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dokdo_plat);

    startActivity(new Intent(this, Dokdo_Splash.class));

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    
    ImageView view = (ImageView) findViewById(R.id.imageView1);
    String urlStr = "https://raw.github.com/neighbors28/Dokdo/master/image.png";
    Drawable draw = loadDrawable(urlStr);
    view.setImageDrawable(draw);
  }

  public Drawable loadDrawable(String urlStr) {
    Drawable drawable = null;
    try {
      URL url = new URL(urlStr);
      InputStream is = url.openStream();
      drawable = Drawable.createFromStream(is, "none");
    } catch (Exception e) {
      Log.e(LOG_TAG, "error, in loadDrawable \n" + e.toString());
    }
    return drawable;
  }
}
