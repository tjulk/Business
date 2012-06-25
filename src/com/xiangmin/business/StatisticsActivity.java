package com.xiangmin.business;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class StatisticsActivity extends Activity{
	
	private Spinner start_month;
	private Spinner end_month;
	private ArrayAdapter<String> adapter; 
	
	private int  thisYear;
	private String[] months={"-01","-02","-03","-04","-05","-06","-07","-08","-09","-10","-11","-12"}; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.statistics_activity);
        Time time = new Time("GMT+8");
        time.setToNow();
        thisYear = time.year;
        for (int i=0;i<months.length;i++) {
        	months[i] =thisYear + months[i];
        }
        start_month = (Spinner) findViewById(R.id.start_month);
        end_month = (Spinner) findViewById(R.id.end_month);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,months);
        start_month.setAdapter(adapter);
        end_month.setAdapter(adapter);
    }
}
