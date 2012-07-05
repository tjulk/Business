package com.xiangmin.business;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiangmin.business.models.PersonSkill;
import com.xiangmin.business.net.APIUtils;
import com.xiangmin.business.utils.Utils;

public class SystemSettingsActivity extends Activity implements OnClickListener{
	
	private LinearLayout person_panel;
	private LinearLayout password_panel;
	
	private LinearLayout personnal_btn;
	private LinearLayout password_btn;
	
	private ProgressDialog progressDialog;
	
	private Context mContext;
	private String mPersonSkill;

	private WebView personnal_info;
	
	
	private TextView old_password;
	private TextView new_password;
	private TextView reinput_password;
	private Button password_submit;
	
	private SharedPreferences mSetting;
	
	private TextView password_text;
	private TextView personal_text;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.system_settings_activity);
        mContext = this;
		mSetting = mContext.getSharedPreferences(Utils.PREFERENCE_NAME, Context.MODE_PRIVATE);
        personnal_btn = (LinearLayout) findViewById(R.id.personnal_btn);
        password_btn = (LinearLayout) findViewById(R.id.password_btn);
        person_panel = (LinearLayout) findViewById(R.id.person_panel);
        password_panel = (LinearLayout) findViewById(R.id.password_panel);
        personnal_btn.setOnClickListener(this);
        password_btn.setOnClickListener(this);
        
        password_text = (TextView) findViewById(R.id.password_text);
        personal_text = (TextView) findViewById(R.id.personal_text);

        personnal_info = (WebView) findViewById(R.id.personnal_info);
        
        old_password = (TextView) findViewById(R.id.old_password);
        new_password = (TextView) findViewById(R.id.new_password);
        reinput_password = (TextView) findViewById(R.id.reinput_password);
        password_submit = (Button) findViewById(R.id.password_submit);
        password_submit.setOnClickListener(this);
        
		password_text.setTextColor(getResources().getColor(R.color.white));
		personal_text.setTextColor(Color.RED);
        getPersonList();
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personnal_btn:
			password_panel.setVisibility(View.GONE);
			person_panel.setVisibility(View.VISIBLE);
			password_text.setTextColor(getResources().getColor(R.color.white));
			personal_text.setTextColor(Color.RED);
			getPersonList();
			break;
		case R.id.password_btn:
			password_panel.setVisibility(View.VISIBLE);
			person_panel.setVisibility(View.GONE);
			password_text.setTextColor(Color.RED);
			personal_text.setTextColor(getResources().getColor(R.color.white));
			break;
		case R.id.password_submit:
			checkPassword();
			break;
		default:
			break;
		}
	}
	
	private void checkPassword() {
		final String oldPwd = old_password.getText().toString();
		final String newPwd = new_password.getText().toString();
		final String newPwd2 = reinput_password.getText().toString();
		
		if (oldPwd==null||oldPwd.equals("")||newPwd==null||newPwd.equals("")||newPwd2==null||newPwd2.equals(""))
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
		else if (oldPwd.equals(mSetting.getString("password", "")))
			Toast.makeText(this, "当前密码错误", Toast.LENGTH_SHORT).show();
		else if (!newPwd.equals(newPwd2)) 
			Toast.makeText(this, "请输入相同新密码", Toast.LENGTH_SHORT).show();
		else {
			progressDialog = ProgressDialog.show(mContext,"","正在修改密码，请稍后...", true, false);
			new Thread() {
				@Override
				public void run() {
					final int result =  APIUtils.changePassword(newPwd);
			        progressDialog.dismiss();
				    loadHandler.sendEmptyMessage(result);
				}
			}.start();
		}
	}
	
	private void getPersonList() {
		progressDialog = ProgressDialog.show(mContext,
				"", getResources().getString(R.string.todo_type_checking_text), true, false);
		new Thread() {
			@Override
			public void run() {
				mPersonSkill =  APIUtils.getPersonSkill();
		        progressDialog.dismiss();
		        if(mPersonSkill!=null) {
			        	loadHandler.sendEmptyMessage(0);
		        } else
		        	loadHandler.sendEmptyMessage(1);
			}
		}.start();
	}
	
	private Handler loadHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
		        String mPersonSkillHtml = "<html><head><meta http-equiv=”Content-Type” content=”text/html; charset=UTF-8″ /></head><body>" + mPersonSkill + "</body></html>";
		        personnal_info.loadDataWithBaseURL("",mPersonSkillHtml, "text/html", "utf-8",""); 
		        
				password_panel.setVisibility(View.GONE);
				person_panel.setVisibility(View.VISIBLE);
				break;
			case 1:
				
				break;
			case APIUtils.CHANGE_PASSWORD_SUCCESS:
				Toast.makeText(mContext, "密码修改成功", Toast.LENGTH_SHORT).show();
				mSetting.edit().putString("password",new_password.getText().toString() ).commit();
				old_password.setText("");
				new_password.setText("");
				reinput_password.setText("");
				break;
			case APIUtils.CHANGE_PASSWORD_FAILED:
				Toast.makeText(mContext, "密码修改失败", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
