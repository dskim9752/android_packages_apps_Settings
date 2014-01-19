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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.android.settings.R;

public class Dokdo_Splash extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dokdo_plat_splash);
	    VideoView vd = (VideoView)findViewById(R.id.videoView1);  
	    vd.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	        @Override
	        public void onCompletion(MediaPlayer vmp) {
	        	finish();
	        }
	    });     
	    Uri uri = Uri.parse("android.resource://com.android.settings/raw/dokdo");
	    MediaController mc = new MediaController(this);
	    vd.setMediaController(mc);
	    vd.setVideoURI(uri); 
	    vd.start();
	}
}
