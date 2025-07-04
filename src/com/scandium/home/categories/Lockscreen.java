/*
 * Copyright (C) 2014-2016 The Dirty Unicorns Project
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

package com.scandium.home.categories;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import android.view.IWindowManager;
import android.view.View;
import android.view.WindowManagerGlobal;
import com.android.internal.util.everest.udfps.CustomUdfpsUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class Lockscreen extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "Lockscreen";
    private static final String UDFPS_CATEGORY = "udfps_category";
    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";

    private FingerprintManager mFingerprintManager;
    private SwitchPreference mFingerprintVib;

    private PreferenceCategory mUdfpsCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen);

        ContentResolver resolver = getActivity().getContentResolver();
	final PreferenceScreen prefScreen = getPreferenceScreen();
	mUdfpsCategory = findPreference(UDFPS_CATEGORY);
	//Handle NPE on UdfpsCategory being null
        if (mUdfpsCategory != null && !CustomUdfpsUtils.hasUdfpsSupport(getContext())) {
            prefScreen.removePreference(mUdfpsCategory);
        }

	mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SwitchPreference) findPreference(FINGERPRINT_VIB);
        if (mFingerprintManager == null) {
            prefScreen.removePreference(mFingerprintVib);
        } else {
            mFingerprintVib.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FINGERPRINT_SUCCESS_VIB, 1) == 1));
            mFingerprintVib.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SCANDIUM_HOME;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mFingerprintVib) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FINGERPRINT_SUCCESS_VIB, value ? 1 : 0);
            return true;
        }
        return true;
    }
}
