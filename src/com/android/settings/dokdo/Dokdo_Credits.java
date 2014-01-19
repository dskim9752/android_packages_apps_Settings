/*
 * Copyright (C) 2013 The Korea Dokdo project
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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import com.android.settings.R;

public class Dokdo_Credits extends FragmentActivity {
	private String assetTxt;
	TextView text;
	Locale systemLocale;
	String strLanguage;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dokdo_credits); 
		
		text = (TextView) findViewById(R.id.textView1);                
		systemLocale = getResources().getConfiguration().locale;                
		strLanguage = systemLocale.getLanguage(); 
		
		try {           
			if (strLanguage.equals("ko")) {            
				assetTxt = readText("credits/Credits_Ko.txt"); 
			} else {           
				assetTxt = readText("credits/Credits_En.txt");    
			}
		} catch (Exception e) {
			
		}    
		text.setText(assetTxt);

	}

        private String readText(String file) throws IOException {
                InputStream is = getAssets().open(file);

                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String text = new String(buffer);
                return text;
        }
}
