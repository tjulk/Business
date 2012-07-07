package com.xiangmin.business;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.xiangmin.business.utils.Utils;

public class BusinessApplication extends Application{
	
	private SharedPreferences mSetting;
    private static BusinessApplication instance;
	public double mLongitude = 0;
	public double mLatitude = 0;
	public String todoEngineer = "";
	
	public int mRecordState = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		mSetting = getSharedPreferences(Utils.PREFERENCE_NAME, Context.MODE_PRIVATE);
		todoEngineer = mSetting.getString("account", "");
	}

    public static BusinessApplication getInstance() {
        return instance;
    }
}
