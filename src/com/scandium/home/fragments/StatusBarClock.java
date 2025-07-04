/*
 * Copyright (C) 2019 AospExtended ROM Project
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

 package com.scandium.home.fragments;

 import android.app.AlertDialog;
 import android.content.ContentResolver;
 import android.content.Context;
 import android.content.DialogInterface.OnCancelListener;
 import android.content.DialogInterface;
 import android.content.res.Resources;
 import android.database.ContentObserver;
 import android.os.Bundle;
 import android.provider.SearchIndexableResource;
 import android.provider.Settings;
 import android.text.format.DateFormat;
 import android.util.Log;
 import android.view.View;
 import android.widget.EditText;

 import androidx.preference.ListPreference;
 import androidx.preference.Preference.OnPreferenceChangeListener;
 import androidx.preference.Preference;
 import androidx.preference.PreferenceCategory;
 import androidx.preference.PreferenceScreen;
 import androidx.preference.SwitchPreference;

 import com.android.internal.logging.nano.MetricsProto;

 import com.android.settings.R;
 import com.android.settings.SettingsPreferenceFragment;
 import com.android.settings.Utils;
 import com.android.settings.search.BaseSearchIndexProvider;

 import com.android.settingslib.search.Indexable;
 import com.android.settingslib.search.SearchIndexable;

 import com.scandium.home.preferences.SecureSettingListPreference;

 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;

 @SearchIndexable
 public class StatusBarClock extends SettingsPreferenceFragment
         implements Preference.OnPreferenceChangeListener, Indexable {

     private static final String TAG = "StatusBarClock";

     private static final String KEY_STATUS_BAR_AM_PM = "status_bar_am_pm";

     private SecureSettingListPreference mStatusBarAmPm;

     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         addPreferencesFromResource(R.xml.clock_date);

         final ContentResolver resolver = getActivity().getContentResolver();
         final PreferenceScreen screen = getPreferenceScreen();

	 mStatusBarAmPm = findPreference(KEY_STATUS_BAR_AM_PM);
     }

     @Override
     public void onResume() {
         super.onResume();
         if (DateFormat.is24HourFormat(requireContext())) {
         mStatusBarAmPm.setEnabled(false);
         mStatusBarAmPm.setSummary(R.string.status_bar_am_pm_unavailable);
         }
     }

     @Override
     public boolean onPreferenceChange(Preference preference, Object objValue) {
         return false;
     }

     @Override
     public int getMetricsCategory() {
         return MetricsProto.MetricsEvent.SCANDIUM_HOME;
     }

     public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
             new BaseSearchIndexProvider() {
                 @Override
                 public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                         boolean enabled) {
                     final ArrayList<SearchIndexableResource> result = new ArrayList<>();
                     final SearchIndexableResource sir = new SearchIndexableResource(context);
                     sir.xmlResId = R.xml.clock_date;
                     result.add(sir);
                     return result;
                 }

                 @Override
                 public List<String> getNonIndexableKeys(Context context) {
                     final List<String> keys = super.getNonIndexableKeys(context);
                     return keys;
                 }
     };
 }
