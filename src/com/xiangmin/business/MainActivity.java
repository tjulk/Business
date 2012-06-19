package com.xiangmin.business;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.xiangmin.business.utils.Utils;

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
			Utils.showFunctionUnComplete(mContext,getResources().getString(R.string.notification_text));
			//startActivity(new Intent(mContext, NotificationActivity.class));
			break;
		case R.id.dashboard_bottom_left:
			Utils.showFunctionUnComplete(mContext,getResources().getString(R.string.todo_count_text));
			//startActivity(new Intent(mContext, InstallActivity.class));
			break;
		case R.id.dashboard_bottom_right:
			Utils.showFunctionUnComplete(mContext,getResources().getString(R.string.system_settings_text));
			//startActivity(new Intent(mContext, ComplaintsActivity.class));
			break;

		default:
			break;
		}
	}
}
