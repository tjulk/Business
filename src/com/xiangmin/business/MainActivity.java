package com.xiangmin.business;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements OnClickListener{
	private LinearLayout mWeixiuguanli;
	private LinearLayout mTongzhigonggao;
	private LinearLayout mAnzhuangguanli;
	private LinearLayout mTousuguanli;
	private Context mContext;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity);
        mWeixiuguanli = (LinearLayout) findViewById(R.id.dashboard_top_left);
        mTongzhigonggao = (LinearLayout) findViewById(R.id.dashboard_top_right);
        mAnzhuangguanli = (LinearLayout) findViewById(R.id.dashboard_bottom_left);
        mTousuguanli = (LinearLayout) findViewById(R.id.dashboard_bottom_right);
        mWeixiuguanli.setOnClickListener(this);
        mTongzhigonggao.setOnClickListener(this);
        mAnzhuangguanli.setOnClickListener(this);
        mTousuguanli.setOnClickListener(this);
        mContext  = this;
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dashboard_top_left:
			startActivity(new Intent(mContext, TodoTypeActivity.class));
			break;
		case R.id.dashboard_top_right:
			startActivity(new Intent(mContext, AnnounceActivity.class));
			break;
		case R.id.dashboard_bottom_left:
			startActivity(new Intent(mContext, StatisticsActivity.class));
			break;
		case R.id.dashboard_bottom_right:
			startActivity(new Intent(mContext, SystemSettingsActivity.class));
			break;

		default:
			break;
		}
	}
}
