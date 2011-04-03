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

package com.gotdns.jush.locationtimemonitoring.util;

import android.util.Log;

/**
 * @author jush
 */
public abstract class LocalLog {
    private static final String LOG_TAG = "com.gotdns.jush.locationtimemonitoring";

    public static void debug(String msg) {
        Log.d(LOG_TAG, msg);
    }

    public static void error(String msg) {
        Log.e(LOG_TAG, msg);
    }

    public static void warning(String msg) {
        Log.w(LOG_TAG, msg);
    }

    public static void info(String msg) {
        Log.i(LOG_TAG, msg);
    }
}
