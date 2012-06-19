package com.xiangmin.business.utils;

import com.xiangmin.business.R;
import com.xiangmin.business.net.APIUtils;

import android.content.Context;
import android.os.SystemClock;
import android.widget.Toast;

public class Utils {
	
	public final static String PREFERENCE_NAME = "xiang_min_business";
	
	public static void showFunctionUnComplete(Context context, String msg) {
		Toast.makeText(context, msg+"continue...", Toast.LENGTH_SHORT).show();
	}

	public static void showNetWorkError(Context context) {
		if (!APIUtils.isNetworkAvailable(context)
				&& !APIUtils.isWiFiActive(context)) {
			Toast.makeText(context, R.string.login_network_failed_text, Toast.LENGTH_SHORT).show();
			return;
		}
	}
	
	// Returns milliseconds since boot, including time spent in sleep.
	public static long getTimestamp() {
		return SystemClock.elapsedRealtime();
	}

}
