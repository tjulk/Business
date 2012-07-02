package com.xiangmin.business;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.xiangmin.business.models.Announce;

public class AnnounceDetailActivity extends Activity{
	
	private WebView announceDetail;
	
	private Announce mAccounce;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.announce_detail_activity);
        announceDetail  =(WebView) findViewById(R.id.announce_detail);
        mAccounce = (Announce)getIntent().getSerializableExtra(AnnounceActivity.SER_KEY);
        announceDetail.getSettings().setJavaScriptEnabled(true);
        String html = "<html><head><meta http-equiv=”Content-Type” content=”text/html; charset=UTF-8″ /></head><body>" + mAccounce.announceDetail + "</body></html>";
        Log.d("AnnounceDetailActivity", html);
        
        announceDetail.loadDataWithBaseURL("",html, "text/html", "utf-8",""); 
    }
}
