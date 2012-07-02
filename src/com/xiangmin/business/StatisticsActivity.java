package com.xiangmin.business;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.xiangmin.business.adapters.StatisticsListAdapter;
import com.xiangmin.business.models.Statistics;
import com.xiangmin.business.net.APIUtils;

public class StatisticsActivity extends Activity implements OnClickListener{
	
	private static final int CHECK_MONTH = 2;
	private static final int CHECK_DAY = 1;
	
	private Spinner start_month;
	private Spinner end_month;
	private ArrayAdapter<String> adapter; 
	
	private int  thisYear;
	private int thisMonth;
	private List<String> months = new ArrayList<String>(); 
	
	private Button startCheckBtn ;
	private Button start_day_check_btn;
	
	private ProgressDialog progressDialog;
	
	private Context mContext;
	
	private ListView statistics_list;
	
	private StatisticsListAdapter mStatisticsListAdapter;
	
	private LinearLayout month_panel;
	private LinearLayout day_panel;
	
	private LinearLayout statistics_month_btn;
	private LinearLayout statistics_day_btn;
	
	private Time mStartTime;
	private Time mEndTime;
	
	private Button mDayStart;
	private Button mDayEnd;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.statistics_activity);
        Time time = new Time("GMT+8");
        time.setToNow();
        thisYear = time.year;
        thisMonth = time.month;
        mContext = this;
        for (int i = thisMonth+2 ; i<=12 ; i++) {
        	months.add((thisYear - 1 + "-" + (i>9? i: ("0"+i))));
        }
        
        for (int i=1 ; i<=thisMonth +1 ;i++) {
        	months.add((thisYear + "-" + (i>9? i: ("0"+i))));
        }
        
        start_month = (Spinner) findViewById(R.id.start_month);
        end_month = (Spinner) findViewById(R.id.end_month);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        start_month.setAdapter(adapter);
        end_month.setAdapter(adapter);
        start_month.setSelection(10);
        end_month.setSelection(11);
        
        statistics_list = (ListView) findViewById(R.id.statistics_list);
        month_panel = (LinearLayout) findViewById(R.id.month_panel);
        day_panel = (LinearLayout) findViewById(R.id.day_panel);
        statistics_month_btn = (LinearLayout) findViewById(R.id.statistics_month_btn);
        statistics_day_btn = (LinearLayout) findViewById(R.id.statistics_day_btn);
        startCheckBtn = (Button) findViewById(R.id.start_check_btn);
        start_day_check_btn = (Button) findViewById(R.id.start_day_check_btn);
        statistics_month_btn.setOnClickListener(this);
        statistics_day_btn.setOnClickListener(this);
        startCheckBtn.setOnClickListener(this);
        start_day_check_btn.setOnClickListener(this);
        
        mDayStart  = (Button) findViewById(R.id.start_day);
        mDayEnd = (Button) findViewById(R.id.end_day);
        
		mStartTime = new Time();
		mEndTime = new Time();

		if (Time.isEpoch(mStartTime) && Time.isEpoch(mEndTime)) {
			mStartTime.setToNow();
			mStartTime.second = 0;
			int minute = mStartTime.minute;
			if (minute == 0) {
			} else if (minute > 0 && minute <= 30) {
				mStartTime.minute = 30;
			} else {
				mStartTime.minute = 0;
				mStartTime.hour += 1;
			}

			long startMillis = mStartTime.normalize(true);
			mEndTime.set(startMillis + DateUtils.HOUR_IN_MILLIS);
		}
		mDayStart.setOnClickListener(new DateClickListener(mStartTime));
		mDayEnd.setOnClickListener(new DateClickListener(mEndTime));
		populateWhen();
    }
    
	private void populateWhen() {
		mDayStart.setText(formatString(mStartTime));
		mDayEnd.setText(formatString(mEndTime));
	}
    
	private class DateClickListener implements View.OnClickListener {
		private Time mTime;

		public DateClickListener(Time time) {
			mTime = time;
		}

		public void onClick(View v) {
			new DatePickerDialog(mContext, new DateListener(v),	mTime.year, mTime.month, mTime.monthDay).show();
		}
	}
	
	private class DateListener implements OnDateSetListener {
		View mView;

		public DateListener(View view) {
			mView = view;
		}

		public void onDateSet(DatePicker view, int year, int month, int monthDay) {
			Time startTime = mStartTime;
			Time endTime = mEndTime;
			long startMillis;
			long endMillis;
			if (mView == mDayStart) {
				int yearDuration = endTime.year - startTime.year;
				int monthDuration = endTime.month - startTime.month;
				int monthDayDuration = endTime.monthDay - startTime.monthDay;
				startTime.year = year;
				startTime.month = month;
				startTime.monthDay = monthDay;
				startMillis = startTime.normalize(true);
				endTime.year = year + yearDuration;
				endTime.month = month + monthDuration;
				endTime.monthDay = monthDay + monthDayDuration;
				endMillis = endTime.normalize(true);
			} else {
				startMillis = startTime.toMillis(true);
				endTime.year = year;
				endTime.month = month;
				endTime.monthDay = monthDay;
				endMillis = endTime.normalize(true);
				if (endTime.before(startTime)) {
					endTime.set(startTime);
					endMillis = startMillis;
				}
			}
			mStartTime = startTime;
			mEndTime = endTime;
			
			mDayStart.setText(formatString(mStartTime));
			mDayEnd.setText(formatString(mEndTime));
		}
	}
	
	private String formatString(Time time) {
		return time.year+"-"+String.format("%02d", time.month+1) + "-" + String.format("%02d", time.monthDay);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_check_btn:
			progressDialog = ProgressDialog.show(mContext,"","正在查询，请稍候...", true, false);
			new Thread() {
				public void run() {
					final List<Statistics> list = APIUtils.getStatisticsList(CHECK_MONTH,start_month.getSelectedItem().toString(),end_month.getSelectedItem().toString());
					if (list!=null&& list.size()!=0) {
						mStatisticsListAdapter = new StatisticsListAdapter(getLayoutInflater(),list);
						loadHandler.sendEmptyMessage(0);
					} else {
						loadHandler.sendEmptyMessage(2);
					}
					progressDialog.dismiss();
				}
			}.start();
			break;
		case R.id.start_day_check_btn:
			progressDialog = ProgressDialog.show(mContext,"","正在查询，请稍候...", true, false);
			new Thread() {
				public void run() {
					final List<Statistics> list = APIUtils.getStatisticsList(CHECK_DAY,formatString(mStartTime)
							,formatString(mEndTime));
					if (list!=null&& list.size()!=0) {
						mStatisticsListAdapter = new StatisticsListAdapter(getLayoutInflater(),list);
						loadHandler.sendEmptyMessage(0);
					} else {
						loadHandler.sendEmptyMessage(2);
					}
					progressDialog.dismiss();
				}
			}.start();
			break;
		case R.id.statistics_month_btn:
			month_panel.setVisibility(View.VISIBLE);
			day_panel.setVisibility(View.GONE);
			statistics_list.setVisibility(View.GONE);
			break;
		case R.id.statistics_day_btn:
			month_panel.setVisibility(View.GONE);
			day_panel.setVisibility(View.VISIBLE);
			statistics_list.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
	
	
	private Handler loadHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				statistics_list.setAdapter(mStatisticsListAdapter);
				month_panel.setVisibility(View.GONE);
				day_panel.setVisibility(View.GONE);
				statistics_list.setVisibility(View.VISIBLE);
				break;
			case 1:
				Toast.makeText(mContext,getResources().getString(R.string.login_connect_timeout_text) ,
						Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(mContext,"链接错误",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}

