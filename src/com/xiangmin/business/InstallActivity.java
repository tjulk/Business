package com.xiangmin.business;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class InstallActivity extends Activity{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_activity);
    }
}
