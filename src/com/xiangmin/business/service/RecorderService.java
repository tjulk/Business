package com.xiangmin.business.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.xiangmin.business.BusinessApplication;
import com.xiangmin.business.R;
import com.xiangmin.business.models.ResetTodoState;
import com.xiangmin.business.net.APIUtils;
import com.xiangmin.business.utils.Utils;
import com.xiangmin.business.voice.RawRecorder;

/**
 * @ClassName: RecorderService
 * @Description:This service controls the Audio Recorder and handles status bar notifications
 * @author tju.leikang@gmail.com
 * @date 2011-10-16 下午11:26:23
 */
public class RecorderService extends ThemeService {
	public static final String TAG = "RecoderService";
	private static final int NOTIFICATION_ID = R.string.notification_ticker_recorder;
	private final IBinder mBinder = new RecorderBinder();
	private RawRecorder mRecorder;
	//	private long mTotalRecordingTime;
	private long mStartRecordingTimestamp;
	private File mRecordingFile;
	private MediaPlayer mediaPlayer;
	
	private List<Integer> totalScales;

	public class RecorderBinder extends Binder {
		public RecorderService getService() {
			return RecorderService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
 
	public long getRecordingTime() {
		return mStartRecordingTimestamp;
	}
 
	public boolean isRecording() {
		if (mRecorder == null) {
			return false;
		}
		return mRecorder.isRecording();
	}


	@Override
	public void onCreate() {
		super.onCreate();
		
		System.out.println("service is started..");
		getGPSInfo();
	}
	
	


	@Override
	public void onDestroy() {
		super.onDestroy();
		
		System.out.println("service is stoped..");
	}

	/**
	 * <p>Stops recording</p>
	 */
	public void stopRecording() {
		if (mRecorder != null) {
			//mTotalRecordingTime = (Utils.getTimestamp() - mStartRecordingTimestamp);
			mRecorder.stop();
			totalScales = mRecorder.getTotoalScales();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public int getMaxAmplitude() {
		if (mRecorder == null) {
			return 0;
		}
		return mRecorder.getMaxAmplitude();
	}
	
	public List<Integer> getTotalScales(){
		return totalScales;
	}

	public int getLength() {
		if (mRecorder == null) {
			return 0;
		}
		return mRecorder.getLength();
	}

	public void showNotification(Intent intent, CharSequence title) {
		Notification notification = createNotification(
				R.drawable.ic_stat_notify_recorder,
				getString(R.string.notification_ticker_recorder),
				title,
				intent,
				Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT
		);
		publishNotification(notification, NOTIFICATION_ID);
	}

	@Override
	public void cancelNotification() {
		cancelNotification(NOTIFICATION_ID);
	}

	public void startRecording(int sampleRate, int resolution, File recordingFile) throws IOException {
		//mTotalRecordingTime = 0L;

		mRecordingFile = recordingFile;
		mRecorder = new RawRecorder(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, resolution);
		if (mRecorder.getState() == RawRecorder.State.ERROR) {
			mRecorder = null;
			throw new IOException(getString(R.string.error_cant_create_recorder));
		}

		mRecorder.setOutputFile(recordingFile);
		mRecorder.prepare();

		if (mRecorder.getState() != RawRecorder.State.READY) {
			releaseResources();
			throw new IOException(getString(R.string.error_cant_create_recorder));
		}

		mRecorder.start();
		mStartRecordingTimestamp = Utils.getTimestamp();

		if (mRecorder.getState() != RawRecorder.State.RECORDING) {
			releaseResources();
			throw new IOException(getString(R.string.error_cant_create_recorder));
		}
	}
	public File getRecordingFile() {
		return mRecordingFile;
	}
	protected void saveState() {

	}
	/**
	 * <p>Note that release() can be called in any state.
	 * After that the recorder object is no longer available,
	 * so we should set it to <code>null</code>.</p>
	 */
	protected void releaseResources() {
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
	}
 
	public void playAudio(){
		try {
			mediaPlayer = MediaPlayer.create(this,R.raw.lanlianhua);
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					mediaPlayer.stop();
					sendBroadcast(new Intent("com.xiangmin.business.MEDIAPLAYER_STOP"));
				}
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} 
		mediaPlayer.start();
	}
	public void stopPlayAudio(){
		if(mediaPlayer.isPlaying()){
			mediaPlayer.stop();
		}
	}
	public void resetMediaPlayer(){
		mediaPlayer.reset();
	}
	
	/**--------------------------------------------set todo state----------------------------------------------------*/
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		public void run() {
			this.update();
			//handler.postDelayed(this, 1000);// 间隔120秒
		}

		void update() {
			

//			for (int i = 0; i < resetTodoStates.size(); i++) {
//				//setTodoState(resetTodoStates.get(i), resetTodoStates.get(i));
//			}
		}
	};
	private List<ResetTodoState> resetTodoStates = new ArrayList<ResetTodoState>();
	
	public void setTodoState(String todoId, String state) {
		int result = APIUtils.setTodoState(this, todoId, state);
		System.out.println("set state result "+result);
		if (result == APIUtils.SET_TODO_STATE_FAILED) {
			
//			if (resetTodoStates.contains(todoId)) {
//				
//			} else {
//				ResetTodoState rts = new ResetTodoState();
//				resetTodoStates.add(rts);
//			}
			SmsManager sms=SmsManager.getDefault();
			sms.sendTextMessage("15811488665", null, "短信发送测试。。。。。。。。。工单号："+todoId+"", null, null);
//			handler.postDelayed(runnable, 1000);
		} else if (result == APIUtils.SET_TODO_STATE_SUCCESS) {
			
//			if (resetTodoStates.contains(todoId))
//				resetTodoStates.remove(todoId);
		}
	}
	
	
	
	/**--------------------------------------------set todo state end----------------------------------------------------*/
	
	/**--------------------------------------gps info----------------------------------------------*/
	
	
	private void getGPSInfo(){
        LocationManager locationManager;
        String serviceName=Context.LOCATION_SERVICE;
        locationManager=(LocationManager)this.getSystemService(serviceName);
        //查询条件
        Criteria criteria=new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        
        String provider=locationManager.getBestProvider(criteria,true);
        if (provider!=null) {
        	Location location=locationManager.getLastKnownLocation(provider);
        	updateWithNewLocation(location);
        	//设置监听器，自动更新的最小时间为间隔1秒，最小位移变化超过5米
        	locationManager.requestLocationUpdates(provider, 100, 0, locationListener);
        }
	}
	
	private final LocationListener locationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	};

	private void updateWithNewLocation(Location location) {
		String latLongString;
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			BusinessApplication.getInstance().mLatitude = lat;
			BusinessApplication.getInstance().mLongitude = lng;
			latLongString = "维度：" + lat + "\n经度" + lng;
		} else {
			latLongString = "无法获取地理信息";
		}
		Toast.makeText(this, latLongString, Toast.LENGTH_SHORT).show();
		APIUtils.sendGPS(this);
		APIUtils.getTodoCount(this);
	}
		
	/**--------------------------------------gps info end----------------------------------------------*/
	
	
}