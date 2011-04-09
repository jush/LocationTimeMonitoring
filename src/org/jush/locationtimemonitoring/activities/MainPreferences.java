/**
   Copyright: 2011 Ramon Sadornil (jush)

   This file is part of LocationTimeMonitoring.

   LocationTimeMonitoring is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   LocationTimeMonitoring is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with LocationTimeMonitoring; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jush.locationtimemonitoring.activities;

import org.jush.locationtimemonitoring.R;
import org.jush.locationtimemonitoring.monitoring.MonitoringManager;
import org.jush.locationtimemonitoring.util.LocalLog;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class MainPreferences extends PreferenceActivity implements OnPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mainpreferences);
        Preference intervalPreference = findPreference(getText(R.string.updateIntervalID));
        intervalPreference.setOnPreferenceChangeListener(this);
    }

    /* (non-Javadoc)
     * @see android.preference.Preference.OnPreferenceChangeListener#onPreferenceChange(android.preference.Preference, java.lang.Object)
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        LocalLog.debug("Preference changed: " + preference.getKey() + " newValue type: " + newValue.getClass() + " value: " + newValue);
        boolean result;
        if (!(newValue instanceof String)) {
            result = false;
        } else {
            try {
                int newInterval = Integer.valueOf((String) newValue);
                if (newInterval > 0) {
                    result = true;
                    MonitoringManager monitoringManager = MonitoringManager.getInstance(getApplicationContext());
                    if (monitoringManager.getUpdateInterval() != newInterval) {
                        monitoringManager.startMonitoring(newInterval);
                    }
                } else {
                    result = false;
                }
            } catch (NumberFormatException e) {
                result = false;
            }
        }
        if (!result) {
            LocalLog.debug("Invalid interval value: use positive numbers.");
            Toast.makeText(getApplicationContext(), "Error: use positive minutes.", Toast.LENGTH_LONG).show();
        }
        return result;
    }
}
