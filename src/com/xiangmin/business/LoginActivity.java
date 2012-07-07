package com.xiangmin.business;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xiangmin.business.net.APIUtils;
import com.xiangmin.business.service.RecorderService;
import com.xiangmin.business.utils.Utils;
import com.xiangmin.business.R;
public class LoginActivity extends Activity {

	private Context mContext;
	private EditText mUserName;
	private EditText mPassword;
	private ProgressDialog progressDialog;
	private SharedPreferences mSetting;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.login_activity);
		mContext = this;
		mSetting = mContext.getSharedPreferences(Utils.PREFERENCE_NAME, Context.MODE_PRIVATE);
		setupUI();
	}

	protected void setupUI() {
		mUserName = (EditText) findViewById(R.id.login_username);
		mPassword = (EditText) findViewById(R.id.login_password);
		
		if(mSetting.getBoolean("is_logined", false)) {
			mUserName.setText(mSetting.getString("account", ""));
			mPassword.setText(mSetting.getString("password", ""));
		}
		
		Button login_active_btn = (Button) findViewById(R.id.login_login);
		login_active_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String account = mUserName.getText().toString();
				final String password = mPassword.getText().toString();
				if (!APIUtils.isNetworkAvailable(mContext)&& !APIUtils.isWiFiActive(mContext)){
					Toast.makeText(mContext, getResources().getString(R.string.login_network_failed_text),Toast.LENGTH_SHORT).show();
					return;
				}
				else if (account == null||account .equals("")){
					Toast.makeText(mContext, getResources().getString(R.string.login_username_null_text),Toast.LENGTH_SHORT).show();
					return;
				}
				else if (password == null||password .equals("")){
					Toast.makeText(mContext, getResources().getString(R.string.login_password_null_text),Toast.LENGTH_SHORT).show();
					return;
				}
				else {
					progressDialog = ProgressDialog.show(mContext,
							"",getResources().getString(R.string.login_waiting_text), true, false);
					new Thread() {
						@Override
						public void run() {
							int result = APIUtils.login(account,password);
							progressDialog.dismiss();
							switch (result) {
							case APIUtils.LOGIN_RESULT_SUCCESS:
								startService(new Intent(mContext, RecorderService.class));
								loadHandler.sendEmptyMessage(0);
								break;
							case APIUtils.LOGIN_RESULT_NORESPONSE:
								loadHandler.sendEmptyMessage(1);
								break;
							case APIUtils.LOGIN_RESULT_FAILED:
								loadHandler.sendEmptyMessage(2);
								break;
							}
						}
					}.start();
				}
			}

		});
	}

	private Handler loadHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mSetting.edit().putBoolean("is_logined", true).commit();
				mSetting.edit().putString("account",mUserName.getText().toString() ).commit();
				mSetting.edit().putString("password",mPassword.getText().toString() ).commit();
				BusinessApplication.getInstance().todoEngineer = mUserName.getText().toString();
				startActivity(new Intent(mContext, MainActivity.class));
				break;
			case 1:
				Toast.makeText(mContext,getResources().getString(R.string.login_connect_timeout_text) ,
						Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(mContext,getResources().getString(R.string.login_failed_text),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};


	@Override
	protected void onPause() {
		super.onPause();
		this.finish();
	}
 
}


