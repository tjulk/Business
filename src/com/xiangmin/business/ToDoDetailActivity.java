package com.xiangmin.business;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangmin.business.models.Todo;
import com.xiangmin.business.service.RecorderService;
import com.xiangmin.business.utils.Utils;
import com.xiangmin.business.voice.Dirs;

public class ToDoDetailActivity extends Activity implements OnClickListener{
	
	
	private static final int TODO_SET_STATE_GO = 0;//动身
	private static final int TODO_SET_STATE_START = 1;//开始
	private static final int TODO_SET_STATE_OVER = 2;//完成
	private static final int TODO_SET_STATE_WAIT = 3;//挂起

	private static final String TAG = "ToDoDetailActivity";
	public static final String TODO_STATE_GOT = "已派工";
	public static final String TODO_STATE_GO = "已接受";
	public static final String TODO_STATE_START = "已开始";
	public static final String TODO_STATE_NOTE = "state_note";
	public static final String TODO_STATE_WAIT = "已挂起";
	public static final String TODO_STATE_OVER = "已结束";
	public static final String TODO_STATE_CONTINUE = "state_continue";
	
	
	private LinearLayout todo_detail_go;
	private LinearLayout todo_detail_start;
	private LinearLayout todo_detail_note;
	private LinearLayout todo_detail_wait;
	private LinearLayout todo_detail_over;
	private LinearLayout todo_detail_continue;
	
	private TextView todo_detail_go_text;
	private TextView todo_detail_start_text;
	private TextView todo_detail_note_text;
	private TextView todo_detail_wait_text;
	private TextView todo_detail_over_text;
	private TextView todo_detail_continue_text;
	
	private TextView todo_id;
	private TextView todo_time;
	private TextView todo_name;
	private TextView todo_address;
	private TextView todo_phonenumber;
	private TextView todo_state;
	
	private Todo mTodo;
	
	private Context mContext;
	
	private boolean isPlayingMusic = false;
	
	private RecoderReceiver receiver;
	
	private LinearLayout todo_detail_panel;
	private RelativeLayout recoder_panel;
	private Button stop_play_music;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tododetail_activity);
        mContext = this;
        doBindService();
        mTodo = (Todo)getIntent().getSerializableExtra(TodoTypeActivity.SER_KEY);
        
        todo_detail_go = (LinearLayout)findViewById(R.id.todo_detail_go);
        todo_detail_start = (LinearLayout)findViewById(R.id.todo_detail_start);
        todo_detail_note = (LinearLayout)findViewById(R.id.todo_detail_note);
        todo_detail_wait = (LinearLayout)findViewById(R.id.todo_detail_wait);
        todo_detail_over = (LinearLayout)findViewById(R.id.todo_detail_over);
        todo_detail_continue = (LinearLayout)findViewById(R.id.todo_detail_continue);
        todo_detail_go.setOnClickListener(this);
        todo_detail_start.setOnClickListener(this);
        todo_detail_note.setOnClickListener(this);
        todo_detail_wait.setOnClickListener(this);
        todo_detail_over.setOnClickListener(this);
        todo_detail_continue.setOnClickListener(this);
        
        todo_detail_go_text = (TextView)findViewById(R.id.todo_detail_go_text);
        todo_detail_start_text = (TextView)findViewById(R.id.todo_detail_start_text);
        todo_detail_note_text = (TextView)findViewById(R.id.todo_detail_note_text);
        todo_detail_wait_text = (TextView)findViewById(R.id.todo_detail_wait_text);
        todo_detail_over_text = (TextView)findViewById(R.id.todo_detail_over_text);
        todo_detail_continue_text = (TextView)findViewById(R.id.todo_detail_continue_text);
        
    	todo_id = (TextView) findViewById(R.id.todo_id);
        todo_time = (TextView) findViewById(R.id.todo_time);
    	todo_name = (TextView) findViewById(R.id.todo_name);
    	todo_address = (TextView) findViewById(R.id.todo_address);
    	todo_phonenumber = (TextView) findViewById(R.id.todo_phonenumber);
    	todo_state = (TextView) findViewById(R.id.todo_state);
        
        todo_detail_panel = (LinearLayout) findViewById(R.id.todo_detail_panel);
        recoder_panel = (RelativeLayout) findViewById(R.id.recoder_panel);
        stop_play_music = (Button) findViewById(R.id.stop_play_music);
        stop_play_music.setOnClickListener(this);
        
        initUI();
        initRecoderUI();
        
		receiver=new RecoderReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("com.xiangmin.business.MEDIAPLAYER_STOP");
		registerReceiver(receiver,filter);
    }

    private void initUI() {
    	final String state = mTodo.state;
    	if (state.equals(TODO_STATE_GOT)) {
    		todo_detail_go.setClickable(true);
    		todo_detail_start.setClickable(true);
    		todo_detail_note.setClickable(true);
    		todo_detail_wait.setClickable(true);
    		todo_detail_over.setClickable(true);
    		todo_detail_continue.setClickable(true);
    		
    		todo_detail_go_text.setTextColor(Color.RED);
    	} else if (state.equals(TODO_STATE_GO)) {
    		todo_detail_go.setClickable(true);
    		todo_detail_start.setClickable(true);
    		todo_detail_note.setClickable(true);
    		todo_detail_wait.setClickable(true);
    		todo_detail_over.setClickable(true);
    		todo_detail_continue.setClickable(true);
    		
    		todo_detail_start_text.setTextColor(Color.RED);
    	} else if (state.equals(TODO_STATE_START)) {
    		todo_detail_go.setClickable(true);
    		todo_detail_start.setClickable(true);
    		todo_detail_note.setClickable(true);
    		todo_detail_wait.setClickable(true);
    		todo_detail_over.setClickable(true);
    		todo_detail_continue.setClickable(true);
    		
    		todo_detail_wait_text.setTextColor(Color.RED);
    		todo_detail_over_text.setTextColor(Color.RED);
    	} else if (state.equals(TODO_STATE_NOTE)) {
    		todo_detail_go.setClickable(true);
    		todo_detail_start.setClickable(true);
    		todo_detail_note.setClickable(true);
    		todo_detail_wait.setClickable(true);
    		todo_detail_over.setClickable(true);
    		todo_detail_continue.setClickable(true);
    		
    		todo_detail_note_text.setTextColor(Color.RED);
    	} else if (state.equals(TODO_STATE_WAIT)) {
    		todo_detail_go.setClickable(true);
    		todo_detail_start.setClickable(true);
    		todo_detail_note.setClickable(true);
    		todo_detail_wait.setClickable(true);
    		todo_detail_over.setClickable(true);
    		todo_detail_continue.setClickable(true);
    		
    		todo_detail_note_text.setTextColor(Color.RED);
    	} else if (state.equals(TODO_STATE_OVER)) {
    		todo_detail_go.setClickable(true);
    		todo_detail_start.setClickable(true);
    		todo_detail_note.setClickable(true);
    		todo_detail_wait.setClickable(true);
    		todo_detail_over.setClickable(true);
    		todo_detail_continue.setClickable(true);
    		
    		todo_detail_note_text.setTextColor(Color.RED);
    	} else if (state.equals(TODO_STATE_NOTE)) {
    		todo_detail_go.setClickable(true);
    		todo_detail_start.setClickable(true);
    		todo_detail_note.setClickable(true);
    		todo_detail_wait.setClickable(true);
    		todo_detail_over.setClickable(true);
    		todo_detail_continue.setClickable(true);
    		
    		todo_detail_continue_text.setTextColor(Color.RED);
    	} else {
    		todo_detail_go.setClickable(true);
    		todo_detail_start.setClickable(true);
    		todo_detail_note.setClickable(true);
    		todo_detail_over.setClickable(true);
    		todo_detail_wait.setClickable(true);
    		todo_detail_continue.setClickable(true);
    		
    		todo_detail_start_text.setTextColor(Color.RED);
    	}
    	todo_id = (TextView) findViewById(R.id.todo_id);
        todo_time = (TextView) findViewById(R.id.todo_time);
    	todo_name = (TextView) findViewById(R.id.todo_name);
    	todo_address = (TextView) findViewById(R.id.todo_address);
    	todo_phonenumber = (TextView) findViewById(R.id.todo_phonenumber);
    	todo_state = (TextView) findViewById(R.id.todo_state);
    	
    	todo_id.setText(mTodo.todoId);
    	todo_time.setText(mTodo.todoTime);
    	todo_name.setText(mTodo.clientName);
    	todo_address.setText(mTodo.address);
    	todo_phonenumber.setText(mTodo.phoneNumber);
    	todo_state.setText(mTodo.state);
    	
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.todo_detail_go:
			 Dialog dialog_go = new AlertDialog.Builder(this)
	         .setIcon(android.R.drawable.btn_star).setTitle("动身")
	         .setMessage("确定动身执行任务？")
	         .setNegativeButton(R.string.todo_detail_cancle_text, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).setNeutralButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					loadHandler.sendEmptyMessage(5);
				}
			}).create();
			 dialog_go.show();
			break;
		case R.id.todo_detail_start:
			 Dialog dialog_start = new AlertDialog.Builder(this)
			         .setIcon(android.R.drawable.btn_star).setTitle(R.string.todo_detail_start_text)
			         .setMessage(R.string.todo_detail_play_music_text)
			         .setNegativeButton(R.string.todo_detail_cancle_text, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).setNeutralButton(R.string.todo_detail_display_text, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							loadHandler.sendEmptyMessage(0);
							
						}
					}).setPositiveButton(R.string.todo_detail_start_after_play_text, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							loadHandler.sendEmptyMessage(1);
						}
					}).create();
			 dialog_start.show();
			break;
		case R.id.todo_detail_over:
			 Dialog dialog_over = new AlertDialog.Builder(this)
	         .setIcon(android.R.drawable.btn_star).setTitle(R.string.todo_detail_over_text)
	         .setMessage(R.string.todo_detail_ask_over_text)
	         .setNegativeButton(R.string.todo_detail_cancle_text, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).setPositiveButton(R.string.todo_detail_ok_over_text, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					loadHandler.sendEmptyMessage(2);
				}
			}).create();
			dialog_over.show();
			break;
		case R.id.todo_detail_continue:
			finish();
			break;
		case R.id.todo_detail_wait:
			 Dialog dialog_wait = new AlertDialog.Builder(this)
	         .setIcon(android.R.drawable.btn_star).setTitle("挂起工单")
	         .setMessage("确定挂起工单，等待下次继续？")
	         .setNegativeButton(R.string.todo_detail_cancle_text, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).setNeutralButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					loadHandler.sendEmptyMessage(3);
				}
			}).create();
			 dialog_wait.show();
			break;
			
		case R.id.todo_detail_note:
			 Dialog dialog_note = new AlertDialog.Builder(this)
	         .setIcon(android.R.drawable.btn_star).setTitle("填写工单")
	         .setMessage("确定已完成（或挂起）工单，填写工单信息？")
	         .setNegativeButton(R.string.todo_detail_cancle_text, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).setNeutralButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(mContext, NoteActivity.class));
				}
			}).create();
			 dialog_note.show();
			break;
		case R.id.stop_play_music:
			loadHandler.sendEmptyMessage(4);
		default:
			break;
		}
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mService != null) {
			if (mService.isRecording()) 
				mService.cancelNotification();
		}
		Log.d(TAG, "state==="+mRecordState);
		
		switch (mRecordState) {
		case 0:
		case 2:
			setGUIPreRecord();
			break;
		case 1:
			startTasks();
			break;

		default:
			break;
		}
		mButtonPauseResumeRecorder.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mService == null) {
					doBindService();
				} else {
					if (mService.isRecording()) {
						if(mRecordState == 1){
							 Dialog dialog = new AlertDialog.Builder(mContext)
					         .setIcon(android.R.drawable.btn_star).setTitle(R.string.todo_detail_recoder_end_text)
					         .setMessage(R.string.todo_detail_ask_save_recoder_text)
					         .setNegativeButton(R.string.todo_detail_cancle_text, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).setPositiveButton(R.string.todo_detail_ask_save_recoder_text, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									mService.stopRecording();
									setGUIPreRecord();
									mRecordState = 2;
								}
							}).create();
							dialog.show();
						}
					} else if (mRecordState == 2) {
							 Dialog dialog = new AlertDialog.Builder(mContext)
					         .setIcon(android.R.drawable.btn_star).setTitle(R.string.todo_detail_recoder_again_text)
					         .setMessage(R.string.todo_detail_ask_recover_text)
					         .setNegativeButton(R.string.todo_detail_cancle_text, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).setPositiveButton(R.string.todo_detail_ok_recover_text, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									setGuiRecording();
								}
							}).create();
							dialog.show();
						}
					}
				
			}
		});
	}
 
	public void onPause() {
		super.onPause();

	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		mStatusHandler.removeCallbacks(mShowStatusTask);
		mVolumeHandler.removeCallbacks(mShowVolumeTask);
		mButtonPauseResumeRecorder.setOnClickListener(null);
		stopChronometer();
		
		if (mService != null && !isFinishing()) {
			String recorderText = getString(R.string.notification_text_recorder_pausing);
			if (mService.isRecording()) {
				recorderText = getString(R.string.notification_text_recorder_recording);
				Intent notificationIntent = new Intent(this, ToDoDetailActivity.class);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				mService.showNotification(notificationIntent, recorderText);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		doUnbindService();
	}

	@Override
	public void onBackPressed() {
		if (mService!=null&&mService.isRecording()) {
			if(mRecordState == 1){
				 Dialog dialog = new AlertDialog.Builder(mContext)
		         .setIcon(android.R.drawable.btn_star).setTitle(R.string.todo_detail_recoding_text)
		         .setMessage(R.string.todo_detail_ask_back_during_recoder_text)
		         .setNegativeButton(R.string.todo_detail_cancle_text, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setPositiveButton(R.string.todo_detail_ok_back_text, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).create();
				dialog.show();
			} else
				super.onBackPressed();
		} else if (isPlayingMusic) {
				 Dialog dialog = new AlertDialog.Builder(mContext)
		         .setIcon(android.R.drawable.btn_star).setTitle(R.string.todo_detail_is_playing_music_text)
		         .setMessage(R.string.todo_detail_ask_back_during_recoder_text)
		         .setNegativeButton(R.string.todo_detail_cancle_text, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setPositiveButton(R.string.todo_detail_ok_back_text, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mService.stopPlayAudio();
						finish();
					}
				}).create();
				 dialog.show();
		} else 
			super.onBackPressed();
	}

	/**-------------------------------------recoder about-------------------------------------------------------**/
	
	private void initRecoderUI() {
		mButtonPauseResumeRecorder = (ImageView) findViewById(R.id.buttonPauseResumeRecording);
		mRecorderStatus = (ImageView) findViewById(R.id.image);
		mStatusbar = (TextView) findViewById(R.id.statusbar);
		mChronometer = (Chronometer) findViewById(R.id.chronometer);
		mVolume = (TextView) findViewById(R.id.volume);
		Dirs.setBaseDir(getPackageName());
		mRecordingsDir = Dirs.getRecorderDir();
		
		if (!mHighResolution) {
			mResolution = AudioFormat.ENCODING_PCM_8BIT;
		}
		mShowStatusTask = new Runnable() {
			public void run() {
				if (mService != null&&mService.isRecording()) {
					//mStatusbar.setText(MyFileUtils.getSizeAsStringExact((long) mService.getLength()));
					mStatusHandler.postDelayed(this, STATUS);
				}
			}
		};
		mShowVolumeTask = new Runnable() {
			public void run() {
				if (mService != null&&mService.isRecording()) {
					//System.out.println("+++++mShowVolumeTask"+makeBar(scaleVolume(mService.getMaxAmplitude())));
					mVolume.setText(makeBar(scaleVolume(mService.getMaxAmplitude())));
					mVolumeHandler.postDelayed(this, VOLUME);
				}
			}
		};
	}
	private String makeBar(int len) {
		if (len <= 0) return "";
		if (len >= MAX_BAR.length()) return MAX_BAR;
		return MAX_BAR.substring(0, len);
	}
	
	void doBindService() {
		Log.i(TAG, "Binding to RecorderService");
		getApplicationContext().bindService(new Intent(this, RecorderService.class), mConnection,Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			getApplicationContext().unbindService(mConnection);
			mIsBound = false;
			mService = null;
		}
	}
	
	private static final String MAX_BAR = "||||||||||||||||||||";
	private File mRecordingsDir = null;
	private Handler mStatusHandler = new Handler();
	private Handler mVolumeHandler = new Handler();
	private Runnable mShowStatusTask;
	private Runnable mShowVolumeTask;
	private boolean mHighResolution = true;
	private int mResolution = AudioFormat.ENCODING_PCM_16BIT;
	private int mSampleRate = 16000;
	private final static int  STATUS = 1000;
	private final static int  VOLUME = 10;
	private static final double LOG_OF_MAX_VOLUME = Math.log10((double) Short.MAX_VALUE);
	private RecorderService mService;
	private boolean mIsBound = false;
	
	private TextView mStatusbar;
	private Chronometer mChronometer;
	private ImageView mButtonPauseResumeRecorder;
	private ImageView mRecorderStatus;
	private TextView mVolume;
	
	private int mRecordState = 0;
	
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((RecorderService.RecorderBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
			Log.i(TAG, "Service disconnected");
		}
	};
	
	private void setGuiRecording() {
		todo_detail_panel.setVisibility(View.GONE);
		recoder_panel.setVisibility(View.VISIBLE);
		mRecordState = 1;
		
		setRecorderStyle(getResources().getColor(R.color.processing));

		mRecorderStatus.setBackgroundResource(R.drawable.listen_outer_glow);
		mButtonPauseResumeRecorder.setBackgroundResource(R.drawable.ic_mic_cd);
		
		try {
			mService.startRecording(mSampleRate,mResolution, getRecordingFile());
			mService.setTodoState(mTodo.todoId,TODO_SET_STATE_START);
		} catch (IOException e) {
			e.printStackTrace();
		}
		startTasks();

	}
	
	private void setGUIPreRecord() {
		mStatusbar.setText(R.string.todo_detail_click_for_start_text);
		mChronometer.setBase(Utils.getTimestamp());
		stopChronometer();
		mVolume.setText(makeBar(20));
		setRecorderStyle(getResources().getColor(R.color.d_fg_text_faded));
		mRecorderStatus.setBackgroundResource(R.drawable.error_ring);
		mButtonPauseResumeRecorder.setBackgroundResource(R.drawable.ic_mic_cd);
	}
	
	private void setRecorderStyle(int color) {
		mStatusbar.setTextColor(color);
		mChronometer.setTextColor(color);
	}
	
	private File getRecordingFile() throws IOException {
		Dirs.setBaseDir(getPackageName());
		if (!mRecordingsDir.exists() && !mRecordingsDir.mkdirs()) {
			throw new IOException(getString(R.string.error_cant_create_dir) + ": " + mRecordingsDir);
		}
		String path = mRecordingsDir.getAbsolutePath() + "/"+ mTodo.todoId + ".wav";
		return new File(path);
	}
	
	private void startTasks() {
		mStatusbar.setText(R.string.todo_detail_click_for_end_text);
		mStatusHandler.postDelayed(mShowStatusTask, STATUS);
		mVolumeHandler.postDelayed(mShowVolumeTask, VOLUME);
		startChronometer();
	}
	private void startChronometer() {
		mChronometer.setBase(mService.getRecordingTime());
		mChronometer.start();
	}
	private int scaleVolume(int volume) {
		return (int) (MAX_BAR.length() * Math.log10((double) volume) / LOG_OF_MAX_VOLUME);
	}

	private void stopChronometer() {
		mChronometer.stop();
	}
	
	/** ------------------------------------------receive serive--------------------------------------------------*/
	public class RecoderReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			isPlayingMusic = false;
			setGuiRecording();
		}
	}
	
	private Handler loadHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (isPlayingMusic) {
					mService.playAudio();
				} else {
					setGuiRecording();
				}
	    		todo_detail_start_text.setTextColor(Color.WHITE);
	    		todo_detail_start.setClickable(true);
	    		todo_detail_over_text.setTextColor(Color.RED);
	    		todo_detail_over.setClickable(true);
	    		
	    		todo_detail_wait_text.setTextColor(Color.RED);
				break;
			case 1:
				isPlayingMusic = true;
				stop_play_music.setVisibility(View.VISIBLE);
				if (isPlayingMusic) {
					mService.playAudio();
				} else {
					setGuiRecording();
				}
				
	    		todo_detail_start_text.setTextColor(Color.WHITE);
	    		todo_detail_start.setClickable(true);
	    		todo_detail_over_text.setTextColor(Color.RED);
	    		todo_detail_over.setClickable(true);
	    		
	    		todo_detail_wait_text.setTextColor(Color.RED);
				break;
			case 2:
				mService.setTodoState(mTodo.todoId,TODO_SET_STATE_OVER);
				mService.stopRecording();
				setGUIPreRecord();
				todo_detail_panel.setVisibility(View.VISIBLE);
				recoder_panel.setVisibility(View.GONE);
	    		todo_detail_wait_text.setTextColor(Color.WHITE);
	    		todo_detail_over_text.setTextColor(Color.WHITE);
	    		todo_detail_note_text.setTextColor(Color.RED);
				break;
			case 3:					
				mService.setTodoState(mTodo.todoId, TODO_SET_STATE_WAIT);
				mService.stopRecording();
				setGUIPreRecord();
				todo_detail_panel.setVisibility(View.VISIBLE);
				recoder_panel.setVisibility(View.GONE);
    			todo_detail_wait_text.setTextColor(Color.WHITE);
    			todo_detail_over_text.setTextColor(Color.WHITE);
    			todo_detail_note_text.setTextColor(Color.RED);
    			break;
			case 4:
				if (isPlayingMusic) {
					mService.stopPlayAudio();
					setGuiRecording();
					stop_play_music.setVisibility(View.GONE);
				isPlayingMusic = false;
				}
				break;
			case 5:
				mService.setTodoState(mTodo.todoId, TODO_SET_STATE_GO);
	    		todo_detail_go_text.setTextColor(Color.WHITE);
	    		todo_detail_go.setClickable(true);
	    		todo_detail_start_text.setTextColor(Color.RED);
	    		todo_detail_start.setClickable(true);
				break;
			}
		}
	};
}
