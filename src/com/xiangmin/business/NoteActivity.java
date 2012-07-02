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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.xiangmin.business.net.APIUtils;

public class NoteActivity  extends Activity implements OnClickListener{
	
	private TextView chanpinxinghao;
	private TextView guzhangleixing;
	private TextView baowaishoufeijine;
	private CheckBox shifoukaijufapiao;
	private TextView fapiaotaitou;
	private Button todo_submit;
	
	private Context mContext;
	private ProgressDialog progressDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.note_activity);
		mContext = this;
		chanpinxinghao = (TextView) findViewById(R.id.chanpinxinghao);
		guzhangleixing = (TextView) findViewById(R.id.guzhangleixing);
		baowaishoufeijine = (TextView) findViewById(R.id.baowaishoufeijine);
		shifoukaijufapiao = (CheckBox) findViewById(R.id.shifoukaijufapiao);
		fapiaotaitou = (TextView) findViewById(R.id.fapiaotaitou);
		todo_submit = (Button) findViewById(R.id.todo_submit);
		todo_submit.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.todo_submit:
			submitTodoDetail();
			break;

		default:
			break;
		}
	}
	
	private void submitTodoDetail() {
		final String chanpinxinghao_t = chanpinxinghao.getText().toString();
		final String guzhangleixing_t = guzhangleixing.getText().toString();
		final String baowaishoufeijine_t = baowaishoufeijine.getText().toString();
		final String shifoukaijufapiao_t = shifoukaijufapiao.isChecked()?"1":"0";
		final String fapiaotaitou_t = fapiaotaitou.getText().toString();
		
		if (chanpinxinghao_t==null||chanpinxinghao_t.equals("")||
				guzhangleixing_t==null||guzhangleixing_t.equals("")||
				baowaishoufeijine_t==null||baowaishoufeijine_t.equals("")||
				shifoukaijufapiao_t==null||shifoukaijufapiao_t.equals("")||
				fapiaotaitou_t==null||fapiaotaitou_t.equals(""))
			Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
		else {
			progressDialog = ProgressDialog.show(mContext,
					"",getResources().getString(R.string.login_waiting_text), true, false);
			new Thread() {
				@Override
				public void run() {
					int result = APIUtils.submitTodoDetail(chanpinxinghao_t,guzhangleixing_t,baowaishoufeijine_t,shifoukaijufapiao_t,fapiaotaitou_t);
					progressDialog.dismiss();
					loadHandler.sendEmptyMessage(result);
				}
			}.start();
		}
	}

	private Handler loadHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(mContext, "订单已提交成功", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 2:
				Toast.makeText(mContext,"订单提交失败",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
