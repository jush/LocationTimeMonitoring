
package com.gotdns.jush.locationtimemonitoring.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.gotdns.jush.locationtimemonitoring.R;
import com.gotdns.jush.locationtimemonitoring.monitoring.MonitoringManager;
import com.gotdns.jush.locationtimemonitoring.util.LocalLog;

public class MainActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
            MonitoringManager.startMonitoring(this);
        } else {
            monitoringBt.setText(R.string.main_start_button);
            MonitoringManager.stopMonitoring(this);
        }
    }

    public void resetBtClicked(View v) {
        // TODO: reset values
    }
}
