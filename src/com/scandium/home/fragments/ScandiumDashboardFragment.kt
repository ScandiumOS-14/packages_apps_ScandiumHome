/*
 * Copyright (C) 2021 AOSP-Krypton Project
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

package com.scandium.home.fragments

import androidx.preference.Preference

import com.android.internal.logging.nano.MetricsProto
import com.android.settings.dashboard.DashboardFragment

abstract class ScandiumDashboardFragment: DashboardFragment() {
    override fun getMetricsCategory(): Int = MetricsProto.MetricsEvent.SCANDIUM_HOME

    override fun onDisplayPreferenceDialog(preference: Preference) {
        super.onDisplayPreferenceDialog(preference)
    }

    companion object {
        const val REQUEST_KEY = "ScandiumDashboardFragment#RequestKey"
    }
}
