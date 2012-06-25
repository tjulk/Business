package com.xiangmin.business;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.xiangmin.business.adapters.AnnounceListAdapter;
import com.xiangmin.business.models.Announce;
import com.xiangmin.business.net.APIUtils;
import com.xiangmin.business.utils.Utils;

public class AnnounceActivity extends Activity{
	
	private ListView mAnnouncelist;
	
	private Context mContext;
	
	private List<Announce> mAnnounces;
	
	private ProgressDialog progressDialog;
	
	private AnnounceListAdapter mAnnounceListAdapter;
	
	private TextView announceListTitle;
	
	public final static String SER_KEY = "com.xiangin.business.announce";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notification_activity);
        mContext = this;
        announceListTitle = (TextView) findViewById(R.id.announce_list_title);
        mAnnouncelist = (ListView) findViewById(R.id.notificationlist);
        mAnnouncelist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent mIntent = new Intent(mContext, AnnounceDetailActivity.class); 
			 	Bundle mBundle = new Bundle();   
		    	mBundle.putSerializable(SER_KEY,(Serializable) mAnnounces.get(arg2));   
		    	mIntent.putExtras(mBundle);
				startActivity(mIntent);
			}
		});
        Utils.showNetWorkError(mContext);
        getAnnounceList();
    }
    
	private void getAnnounceList() {
		progressDialog = ProgressDialog.show(mContext,
				"", "正在加载，请稍等...", true, false);
		new Thread() {
			@Override
			public void run() {
				mAnnounces = APIUtils.getAnnounceList();
				System.out.println("======"+mAnnounces.toString());
		        progressDialog.dismiss();
		        if(mAnnounces!=null&&mAnnounces.size()!=0) {
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
				mAnnounceListAdapter = new AnnounceListAdapter(getLayoutInflater(), mAnnounces);
				mAnnouncelist.setAdapter(mAnnounceListAdapter);
		        announceListTitle.setText(announceListTitle.getText()+"("+mAnnounces.size()+")");
				break;
			case 1:
				break;
			case 2:
				break;
			}
		}
	};
}
