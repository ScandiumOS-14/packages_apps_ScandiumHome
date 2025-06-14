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

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.development.SystemPropPoker;

public class System extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "System";

    private static final String KEY_PIXEL_SPOOF = "use_pixel_spoof";
    private static final String KEY_GAMES_SPOOF = "use_games_spoof";
    private static final String KEY_PHOTOS_SPOOF = "use_photos_spoof";
    private static final String KEY_NETFLIX_SPOOF = "use_netflix_spoof";

    private static final String SYS_PIXEL_SPOOF = "persist.sys.pixelprops.pi";
    private static final String SYS_GAMES_SPOOF = "persist.sys.pixelprops.games";
    private static final String SYS_PHOTOS_SPOOF = "persist.sys.pixelprops.gphotos";
    private static final String SYS_NETFLIX_SPOOF = "persist.sys.spoof_netflix";

    private val KEYBOX_DATA_KEY = "keybox_data_setting"
    private val KEYBOX_DELETE_KEY = "keybox_data_delete"
    private val REQUEST_KEYBOX_FILE = 10003

    private lateinit var mKeyboxDataPreference: Preference
    private lateinit var mKeyboxDeletePreference: Preference

    private val mKeyboxFilePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri = result.data?.data
            if (uri != null) {Add commentMore actions
                loadKeyboxFile(uri)
            }
        }
    }

    private SwitchPreference mPixelSpoof;
    private SwitchPreference mGamesSpoof;
    private SwitchPreference mPhotosSpoof;
    private SwitchPreference mNetFlixSpoof;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system);

        final PreferenceScreen prefScreen = getPreferenceScreen();

        ContentResolver resolver = getActivity().getContentResolver();

        mPixelSpoof = (SwitchPreference) prefScreen.findPreference(KEY_PIXEL_SPOOF);
        mPixelSpoof.setChecked(SystemProperties.getBoolean(SYS_PIXEL_SPOOF, true));
        mPixelSpoof.setOnPreferenceChangeListener(this);

        mGamesSpoof = (SwitchPreference) prefScreen.findPreference(KEY_GAMES_SPOOF);
        mGamesSpoof.setChecked(SystemProperties.getBoolean(SYS_GAMES_SPOOF, false));
        mGamesSpoof.setOnPreferenceChangeListener(this);

        mPhotosSpoof = (SwitchPreference) prefScreen.findPreference(KEY_PHOTOS_SPOOF);
        mPhotosSpoof.setChecked(SystemProperties.getBoolean(SYS_PHOTOS_SPOOF, true));
        mPhotosSpoof.setOnPreferenceChangeListener(this);

        mNetFlixSpoof = (SwitchPreference) findPreference(KEY_NETFLIX_SPOOF);
        mNetFlixSpoof.setChecked(SystemProperties.getBoolean(SYS_NETFLIX_SPOOF, false));
        mNetFlixSpoof.setOnPreferenceChangeListener(this);
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

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.system)

        mKeyboxDataPreference = findPreference(KEYBOX_DATA_KEY)!!
        mKeyboxDeletePreference = findPreference(KEYBOX_DELETE_KEY)!!

        mKeyboxDataPreference.setOnPreferenceClickListener {
            openKeyboxFileSelector()
            true
        }

        mKeyboxDeletePreference.setOnPreferenceClickListener {
            deleteKeyboxData()
            true
        }
    }

    private fun openKeyboxFileSelector() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "text/xml"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        mKeyboxFilePickerLauncher.launch(intent)
    }

    private fun deleteKeyboxData() {
        Settings.Secure.putString(
            requireContext().contentResolver,
            Settings.Secure.KEYBOX_DATA,
            null
        )
        Toast.makeText(context, R.string.keybox_data_cleared, Toast.LENGTH_SHORT).show()
    }

    private fun loadKeyboxFile(uri: Uri) {
        Log.d(TAG, "Loading Keybox XML file from URI: ${uri.toString()}")
        
        if (uri.toString().endsWith(".xml") || 
            "text/xml" == requireContext().contentResolver.getType(uri)) {
            try {
                requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val xmlContent = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        xmlContent.append(line).append('\n')
                    }

                    val xml = xmlContent.toString()
                    if (validateKeyboxXml(xml)) {
                        Settings.Secure.putString(
                            requireContext().contentResolver,
                            Settings.Secure.KEYBOX_DATA,
                            xml
                        )
                        Toast.makeText(context, R.string.keybox_data_loaded, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, R.string.keybox_data_invalid, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read keybox XML file", e)
                Toast.makeText(context, R.string.keybox_data_error, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, R.string.keybox_data_invalid, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateKeyboxXml(xml: String): Boolean {
        var hasPrivKey = false
        var hasEcdsaKey = false
        var hasRsaKey = false
        var ecdsaCertCount = 0
        var rsaCertCount = 0
        var numberOfKeyboxes = -1
        var currentAlg: String? = null

        try {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(xml.reader())

            var eventType = parser.next()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    when (parser.name) {
                        "NumberOfKeyboxes" -> {
                            parser.next()
                            if (parser.eventType == XmlPullParser.TEXT) {
                                numberOfKeyboxes = parser.text.trim().toIntOrNull() ?: -1
                            }
                        }
                        "Key" -> {
                            currentAlg = parser.getAttributeValue(null, "algorithm")
                            when (currentAlg?.lowercase()) {
                                "ecdsa" -> hasEcdsaKey = true
                                "rsa" -> hasRsaKey = true
                                else -> currentAlg = null
                            }
                        }
                        "PrivateKey" -> hasPrivKey = true
                        "Certificate" -> {
                            when (currentAlg?.lowercase()) {
                                "ecdsa" -> ecdsaCertCount++
                                "rsa" -> rsaCertCount++
                            }
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && parser.name == "Key") {
                    currentAlg = null
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e(TAG, "XML validation failed", e)
            return false
        }

        return numberOfKeyboxes == 1 &&
               hasPrivKey &&
               hasEcdsaKey && hasRsaKey &&
               ecdsaCertCount == 3 && rsaCertCount == 3
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (preference == mPixelSpoof) {
            boolean value = (Boolean) objValue;
            SystemProperties.set(SYS_PIXEL_SPOOF, value ? "true" : "false");
            SystemPropPoker.getInstance().poke();
            return true;
        } else if (preference == mGamesSpoof) {
            boolean value = (Boolean) objValue;
            SystemProperties.set(SYS_GAMES_SPOOF, value ? "true" : "false");
            SystemPropPoker.getInstance().poke();
            return true;
        } else if (preference == mPhotosSpoof) {
            boolean value = (Boolean) objValue;
            SystemProperties.set(SYS_PHOTOS_SPOOF, value ? "true" : "false");
            SystemPropPoker.getInstance().poke();
            return true;
        } else if (preference == mNetFlixSpoof) {
            boolean value = (Boolean) objValue;
            SystemProperties.set(SYS_NETFLIX_SPOOF, value ? "true" : "false");
            SystemPropPoker.getInstance().poke();
            return true;
        }
        return true;
    }
}