package com.xiangmin.business;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiangmin.business.models.PersonSkill;
import com.xiangmin.business.net.APIUtils;

public class SystemSettingsActivity extends Activity implements OnClickListener{
	
	private LinearLayout person_panel;
	private LinearLayout password_panel;
	
	private LinearLayout personnal_btn;
	private LinearLayout password_btn;
	
	private ProgressDialog progressDialog;
	
	private Context mContext;
	private PersonSkill mPersonSkill;
	
	private TextView peixun_number;
	private WebView jineng_number;
	private TextView fuwu_nengli;
	private TextView geren_zili;
	private WebView gongdan_tongji;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.system_settings_activity);
        
        personnal_btn = (LinearLayout) findViewById(R.id.personnal_btn);
        password_btn = (LinearLayout) findViewById(R.id.password_btn);
        person_panel = (LinearLayout) findViewById(R.id.person_panel);
        password_panel = (LinearLayout) findViewById(R.id.password_panel);
        personnal_btn.setOnClickListener(this);
        password_btn.setOnClickListener(this);
        mContext = this;
        
        peixun_number = (TextView) findViewById(R.id.peixun_number);
        jineng_number = (WebView) findViewById(R.id.jishunnegli);
        fuwu_nengli = (TextView) findViewById(R.id.fuwu_nengli);
        geren_zili = (TextView) findViewById(R.id.geren_zili);
        gongdan_tongji = (WebView) findViewById(R.id.gongdantongji);
        
        getPersonList();
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personnal_btn:
			password_panel.setVisibility(View.GONE);
			person_panel.setVisibility(View.VISIBLE);
			getPersonList();
			break;
		case R.id.password_btn:
			password_panel.setVisibility(View.VISIBLE);
			person_panel.setVisibility(View.GONE);
		default:
			break;
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
		        }
		        else
		        	loadHandler.sendEmptyMessage(1);
			}
		}.start();
	}
	
	private Handler loadHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
		        String gongdantongjiHtml = "<html><head><meta http-equiv=”Content-Type” content=”text/html; charset=UTF-8″ /></head><body>" + mPersonSkill.gongdantongji + "</body></html>";
		        String jishunengliHtml = "<html><head><meta http-equiv=”Content-Type” content=”text/html; charset=UTF-8″ /></head><body>" + mPersonSkill.jishunengli + "</body></html>";
		        peixun_number.setText(mPersonSkill.peixunshuliang);
		        fuwu_nengli.setText(mPersonSkill.fuwunengli);
		        geren_zili.setText(mPersonSkill.gerenzili);
		        jineng_number.loadDataWithBaseURL("",jishunengliHtml, "text/html", "utf-8",""); 
		        gongdan_tongji.loadDataWithBaseURL("",gongdantongjiHtml, "text/html", "utf-8",""); 
				password_panel.setVisibility(View.GONE);
				person_panel.setVisibility(View.VISIBLE);
				break;
			case 1:
				break;
			case 2:
				break;
			}
		}
	};
}
