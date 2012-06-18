package com.xiangmin.business.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.xiangmin.business.R;

public abstract class ThemeService extends Service {

	private NotificationManager mNotificationMngr;
	private SharedPreferences mPreferences;

	private boolean mNotificationIsShowing = false;

	public abstract void cancelNotification();
	protected abstract void saveState();
	protected abstract void releaseResources();


	@Override
	public void onCreate() {
		mPreferences = getSharedPreferences("suppersonicPre", 0);
		mNotificationMngr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}


	/**
	 * <p>If the service is destroyed (incl. via the general Android
	 * service-management menu), then</p>
	 * 
	 * <ul>
	 * <li>cancel the notifications</li>
	 * <li>save the state</li>
	 * <li>release the resources (e.g. Media Player, Audio Recorder)</li>
	 * </ul>
	 */
	@Override
	public void onDestroy() {
		Log.i(ThemeService.class.getName(), "onDestroy");
		cancelNotification();
		saveState();
		releaseResources();
	}


	public void cancelNotification(int notificationId) {
		mNotificationMngr.cancel(notificationId);
		mNotificationIsShowing = false;
	}


	protected SharedPreferences getPreferences() {
		return mPreferences;
	}


	protected boolean isNotificationShowing() {
		return mNotificationIsShowing;
	}


	protected void publishNotification(Notification notification, int notificationId) {
		mNotificationMngr.notify(notificationId, notification);
		mNotificationIsShowing = true;
	}


	/**
	 * @param tickerText Ticker text (shown briefly)
	 * @param contentText Content text (shown when the notification is pulled down)
	 * @param intent Intent to be launched if notification is clicked
	 * @param flags Notification flags
	 * @return Notification object
	 */
	protected Notification createNotification(int icon, CharSequence tickerText, CharSequence contentText, Intent intent, int flags) {
		CharSequence contentTitle = getString(R.string.app_name);
		Notification notification = new Notification(icon, tickerText, System.currentTimeMillis());
		//notification.flags |= flags;
		notification.flags = flags;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		return notification;
	}
}