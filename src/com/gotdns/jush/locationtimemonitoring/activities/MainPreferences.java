package com.gotdns.jush.locationtimemonitoring.activities;

import com.gotdns.jush.locationtimemonitoring.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MainPreferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mainpreferences);
    }

}
