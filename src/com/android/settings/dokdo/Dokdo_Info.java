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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.settings.R;

public class Dokdo_Info extends PreferenceFragment {

    private static final String LOG_TAG = "DokDo_Info";
    private static final String KEY_MOD_VERSION = "dokdo_version";
    private static final String KEY_BUILD_ID = "build_version";
    private static final String KEY_BUILD_USER = "build_user";

    private AlertDialog alertDialog;

    Preference mEmail;
    Preference mFacebook;

    long[] mHits = new long[4];
    int mDevHitCountdown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.dokdo_info);
        mEmail = findPreference("send_email");
        mFacebook = findPreference("go_facebook");

        setValueSummary(KEY_BUILD_ID, "ro.build.id");
        setValueSummary(KEY_BUILD_USER, "ro.build.user");
        setValueSummary(KEY_MOD_VERSION, "ro.dokdo.version");
        findPreference(KEY_MOD_VERSION).setEnabled(true);
    }
	
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mEmail) {
            /* Display the warning dialog */
            alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle(R.string.bugreport_warning_title);
            alertDialog.setMessage(getResources().getString(R.string.bugreport_warning_message));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    getResources().getString(com.android.internal.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        email("mailto:projectdokdoteam@gmail.com");
                        }
                    });
            alertDialog.show();
        } else if (preference == mFacebook) {
            facebook("https://www.facebook.com/projectdokdo");
        } else if (preference.getKey().equals(KEY_MOD_VERSION)) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
            mHits[mHits.length-1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("android",
                        com.android.internal.app.DokdoDream.class.getName());
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Unable to start activity " + intent.toString());
                }
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    
    private void email(String url) {
	Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
        getActivity().startActivity(Intent.createChooser(emailIntent, getString(R.string.select_email_application)));
    }

    private void facebook(String url) {
	Uri uri = Uri.parse(url);
	Intent facebook = new Intent(Intent.ACTION_VIEW, uri);
        getActivity().startActivity(facebook);
    }

    private void setValueSummary(String preference, String property) {
        try {
            findPreference(preference).setSummary(
                    SystemProperties.get(property,
                            getResources().getString(R.string.device_info_default)));
        } catch (RuntimeException e) {
            // No recovery
        }
    }
}
