package com.xiangmin.business;

import com.xiangmin.business.service.RecorderService;
import com.xiangmin.business.utils.Utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BusinessApplication extends Application{
	
	private SharedPreferences mSetting;
    private static BusinessApplication instance;
	public double mLongitude = 0;
	public double mLatitude = 0;
	public String todoEngineer = "";

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		startService(new Intent(this, RecorderService.class));
		
		mSetting = getSharedPreferences(Utils.PREFERENCE_NAME, Context.MODE_PRIVATE);
		todoEngineer = mSetting.getString("account", "");
	}

    public static BusinessApplication getInstance() {
        return instance;
    }
}
