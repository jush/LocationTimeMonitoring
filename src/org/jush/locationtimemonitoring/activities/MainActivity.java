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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import org.jush.locationtimemonitoring.R;
import org.jush.locationtimemonitoring.monitoring.MonitoringManager;
import org.jush.locationtimemonitoring.util.LocalLog;

public class MainActivity extends Activity {

    private MonitoringManager monitoringManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        monitoringManager = MonitoringManager.getInstance(getApplicationContext());
        setContentView(R.layout.main);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        setMonitoringBtStatus();
        super.onResume();
    }

    /**
     * 
     */
    private void setMonitoringBtStatus() {
        CheckBox monitoringBt = (CheckBox) findViewById(R.id.mointoringBt);
        if (monitoringBt != null) {
            boolean status = monitoringManager.isMonitoring();
            monitoringBt.setChecked(status);
            monitoringBt.setText((status ? R.string.main_stop_button : R.string.main_start_button));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_options:
                Intent settingsActivity = new Intent(getBaseContext(), MainPreferences.class);
                startActivity(settingsActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void monitoringBtClicked(View view) {
        CheckBox monitoringBt = (CheckBox) view;
        LocalLog.debug("Monitoring toggle button clicked!" + monitoringBt.isChecked());
        if (monitoringBt.isChecked()) {
            monitoringBt.setText(R.string.main_stop_button);
            monitoringManager.startMonitoring();
        } else {
            monitoringBt.setText(R.string.main_start_button);
            monitoringManager.stopMonitoring();
        }
    }

    public void resetBtClicked(View v) {
        monitoringManager.resetCounter();
    }
}
