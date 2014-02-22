/*
 * Copyright (C) 2012 The CyanogenMod project
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

package com.android.settings.cyanogenmod;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManagerGlobal;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.cyanogenmod.colorpicker.ColorPickerPreference;
import com.android.settings.Utils;

public class SystemUiSettings extends SettingsPreferenceFragment  implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "SystemSettings";

    private static final String KEY_EXPANDED_DESKTOP = "expanded_desktop";
    private static final String KEY_EXPANDED_DESKTOP_NO_NAVBAR = "expanded_desktop_no_navbar";
    private static final String CATEGORY_NAVBAR = "navigation_bar";
    private static final String KEY_SCREEN_GESTURE_SETTINGS = "touch_screen_gesture_settings";
    private static final String KEY_NAVIGATION_BAR_LEFT = "navigation_bar_left";
    private static final String OVERSCROLL_GLOW_COLOR = "overscroll_glow_color";
    private static final String NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String NAVIGATION_BAR_WIDTH = "navigation_bar_width";
    private static final String NAVIGATION_BAR_TOGGLE = "navigation_bar_toggle";
    private static final String KEY_TOAST_ANIMATION = "toast_animation";

    private ListPreference mExpandedDesktopPref;
    private ListPreference mToastAnimation;
    private CheckBoxPreference mExpandedDesktopNoNavbarPref;
    private CheckBoxPreference mNavigationBarLeftPref;
    private CheckBoxPreference mNavigation_bar_toggle;
    private ColorPickerPreference mOverScrollGlowColor;
    private SeekBarPreference mNavigation_bar_height;
    private SeekBarPreference mNavigation_bar_width;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system_ui_settings);
        PreferenceScreen prefScreen = getPreferenceScreen();

        // Overscroll customize
        mOverScrollGlowColor = (ColorPickerPreference) findPreference(OVERSCROLL_GLOW_COLOR);
        mOverScrollGlowColor.setOnPreferenceChangeListener(this);
        int defaultColor = Color.rgb(255, 255, 255);
        int intColor = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.OVERSCROLL_GLOW_COLOR, defaultColor);
        mOverScrollGlowColor.setNewPreviewColor(intColor);

	mNavigation_bar_height = (SeekBarPreference) findPreference(NAVIGATION_BAR_HEIGHT);
        mNavigation_bar_width = (SeekBarPreference) findPreference(NAVIGATION_BAR_WIDTH);
	mNavigation_bar_toggle = (CheckBoxPreference) findPreference(NAVIGATION_BAR_TOGGLE);

	// Expanded desktop
        mExpandedDesktopPref = (ListPreference) findPreference(KEY_EXPANDED_DESKTOP);
        mExpandedDesktopNoNavbarPref =
                (CheckBoxPreference) findPreference(KEY_EXPANDED_DESKTOP_NO_NAVBAR);

        // Navigation bar left
        mNavigationBarLeftPref = (CheckBoxPreference) findPreference(KEY_NAVIGATION_BAR_LEFT);

        Utils.updatePreferenceToSpecificActivityFromMetaDataOrRemove(getActivity(),
                getPreferenceScreen(), KEY_SCREEN_GESTURE_SETTINGS);

        int expandedDesktopValue = Settings.System.getInt(getContentResolver(),
                Settings.System.EXPANDED_DESKTOP_STYLE, 0);

        try {
            boolean hasNavBar = WindowManagerGlobal.getWindowManagerService().hasNavigationBar();
	    if (hasNavBar) {
                mExpandedDesktopPref.setOnPreferenceChangeListener(this);
                mExpandedDesktopPref.setValue(String.valueOf(expandedDesktopValue));
                updateExpandedDesktop(expandedDesktopValue);
                prefScreen.removePreference(mExpandedDesktopNoNavbarPref);

                if (!Utils.isPhone(getActivity())) {
                    PreferenceCategory navCategory =
                            (PreferenceCategory) findPreference(CATEGORY_NAVBAR);
                    navCategory.removePreference(mNavigationBarLeftPref);
                }
            } else {
                // Hide no-op "Status bar visible" expanded desktop mode
                mExpandedDesktopNoNavbarPref.setOnPreferenceChangeListener(this);
                mExpandedDesktopNoNavbarPref.setChecked(expandedDesktopValue > 0);
                prefScreen.removePreference(mExpandedDesktopPref);
                // Hide navigation bar category
                prefScreen.removePreference(findPreference(CATEGORY_NAVBAR));
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error getting navigation bar status");
        }

	//NavBar control
	int DefaultNavbarStatus = (mContext.getResources().getBoolean(com.android.internal.R.bool.config_showNavigationBar) ? 1:0);
	int NavbarStatus = Settings.System.getInt(getContentResolver(), Settings.System.NAVIGATION_BAR_TOGGLE, DefaultNavbarStatus);
	if ( NavbarStatus == 1 ) {
		mNavigation_bar_toggle.setChecked(true);
	} else {
		mNavigation_bar_toggle.setChecked(false);
	}
	mNavigation_bar_toggle.setOnPreferenceChangeListener(this);
        int setvaluenavheight = Settings.System.getInt(getContentResolver(), Settings.System.NAVIGATION_BAR_HEIGHT, 48);
        int setvaluenavwidth = Settings.System.getInt(getContentResolver(), Settings.System.NAVIGATION_BAR_WIDTH, 42);

        mNavigation_bar_height.setValue(setvaluenavheight);
        mNavigation_bar_height.setOnPreferenceChangeListener(this);

        mNavigation_bar_width.setValue(setvaluenavwidth);
        mNavigation_bar_width.setOnPreferenceChangeListener(this);

	mToastAnimation = (ListPreference)findPreference(KEY_TOAST_ANIMATION);
	mToastAnimation.setSummary(mToastAnimation.getEntry());
	int CurrentToastAnimation = Settings.System.getInt(getContentResolver(), Settings.System.TOAST_ANIMATION, 1);
	mToastAnimation.setValueIndex(CurrentToastAnimation);
	mToastAnimation.setOnPreferenceChangeListener(this);

	}

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mExpandedDesktopPref) {
            int expandedDesktopValue = Integer.valueOf((String) newValue);
            updateExpandedDesktop(expandedDesktopValue);
            return true;
        } else if (preference == mExpandedDesktopNoNavbarPref) {
            boolean value = (Boolean) newValue;
            updateExpandedDesktop(value ? 2 : 0);
            return true;
        } else if (preference == mOverScrollGlowColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.OVERSCROLL_GLOW_COLOR, intHex);
            return true;
        } else if (preference == mNavigation_bar_height) {
	    int NavHeight = ((Integer)newValue).intValue();
	    Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_HEIGHT, NavHeight);
	    return true;
	} else if (preference == mNavigation_bar_width) {
            int NavWidth = ((Integer)newValue).intValue();
            Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_WIDTH, NavWidth);
	    return true;
        } else if (preference == mNavigation_bar_toggle) {
	    boolean value = mNavigation_bar_toggle.isChecked();
	    Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_TOGGLE, value ? 0:1);
	    System.out.println(value ? 1:0);
	    return true;
	} else if (preference == mToastAnimation) {
	    int index = mToastAnimation.findIndexOfValue((String) newValue);
	    Settings.System.putString(getContentResolver(), Settings.System.TOAST_ANIMATION, (String) newValue);
	    mToastAnimation.setSummary(mToastAnimation.getEntries()[index]);
	    return true;
	}
        return false;
    }

    private void updateExpandedDesktop(int value) {
        ContentResolver cr = getContentResolver();
        Resources res = getResources();
        int summary = -1;

        Settings.System.putInt(cr, Settings.System.EXPANDED_DESKTOP_STYLE, value);

        if (value == 0) {
            // Expanded desktop deactivated
            Settings.System.putInt(cr, Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 0);
            Settings.System.putInt(cr, Settings.System.EXPANDED_DESKTOP_STATE, 0);
            summary = R.string.expanded_desktop_disabled;
        } else if (value == 1) {
            Settings.System.putInt(cr, Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 1);
            summary = R.string.expanded_desktop_status_bar;
        } else if (value == 2) {
            Settings.System.putInt(cr, Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 1);
            summary = R.string.expanded_desktop_no_status_bar;
        }

        if (mExpandedDesktopPref != null && summary != -1) {
            mExpandedDesktopPref.setSummary(res.getString(summary));
        }
    }
}
