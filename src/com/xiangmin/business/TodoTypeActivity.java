package com.xiangmin.business;

import java.util.List;

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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.xiangmin.business.adapters.TodoListAdapter;
import com.xiangmin.business.models.Todo;
import com.xiangmin.business.net.APIUtils;
import com.xiangmin.business.utils.Utils;

public class TodoTypeActivity extends Activity implements OnClickListener{
	
	public final static String SER_KEY = "com.xiangin.business.todo";
	
	private Context mContext;
	private ListView mTodolist;
	private TodoListAdapter mTodoListAdapter;
	
	private LinearLayout mTodayTodoList;
	private LinearLayout mTomorrowTodoList;
	
	private TextView todoListTitle;
	private ProgressDialog progressDialog;
	private SharedPreferences mSetting;
	private List<Todo> todos;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.todotype_activity);
        mContext = this;
        mSetting = mContext.getSharedPreferences(Utils.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mTodolist = (ListView) findViewById(R.id.todolist);
        mTodolist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent mIntent = new Intent(mContext, ToDoDetailActivity.class); 
			 	Bundle mBundle = new Bundle();   
		    	mBundle.putSerializable(SER_KEY,todos.get(arg2));   
		    	mIntent.putExtras(mBundle);
				startActivity(mIntent);
			}
		});
        mTodayTodoList = (LinearLayout) findViewById(R.id.today_todo_list);
        mTomorrowTodoList = (LinearLayout) findViewById(R.id.tomorrow_todo_list);
        mTodayTodoList.setOnClickListener(this);
        mTomorrowTodoList.setOnClickListener(this);
        todoListTitle = (TextView) findViewById(R.id.todo_list_title);
        Utils.showNetWorkError(mContext);
		getTodoList(APIUtils.TYPE_TODAY);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.today_todo_list:
			Utils.showNetWorkError(mContext);
			getTodoList(APIUtils.TYPE_TODAY);
			break;
		case R.id.tomorrow_todo_list:
			Utils.showNetWorkError(mContext);
			getTodoList(APIUtils.TYPE_TOMORROW);
			break;
		default:
			break;
		}
		
	}
    
	private void getTodoList(final int type) {
		progressDialog = ProgressDialog.show(mContext,
				"", "正在查询，请稍候...", true, false);
		new Thread() {
			@Override
			public void run() {
				todos = APIUtils.getTodoList(type, mSetting.getString("account", ""));
				System.out.println(todos.toString());
		        progressDialog.dismiss();
		        if(todos!=null&&todos.size()!=0) {
			        if(type==APIUtils.TYPE_TODAY)
			        	loadHandler.sendEmptyMessage(0);
			        else 
			        	loadHandler.sendEmptyMessage(1);
		        }
		        else
		        	loadHandler.sendEmptyMessage(2);
			}
		}.start();
	}
	
	private Handler loadHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
		        mTodoListAdapter = new TodoListAdapter(getLayoutInflater(), todos);
		        mTodolist.setAdapter(mTodoListAdapter);
		        todoListTitle.setText("今日订单列表("+todos.size()+")");
				break;
			case 1:
		        mTodoListAdapter = new TodoListAdapter(getLayoutInflater(), todos);
		        mTodolist.setAdapter(mTodoListAdapter);
				todoListTitle.setText("明日订单列表("+todos.size()+")");
				break;
			case 2:
				Toast.makeText(mContext, "获取信息错误",Toast.LENGTH_SHORT).show();
				todoListTitle.setText("订单列表");
				break;
			}
		}
	};
    
}
