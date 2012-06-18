package com.xiangmin.business.voice;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;

public class Recorder implements OnCompletionListener, OnErrorListener {
    static final String SAMPLE_PREFIX = "recording";
    static final String SAMPLE_PATH_KEY = "sample_path";
    static final String SAMPLE_LENGTH_KEY = "sample_length";
    
    static final int AUDIO_OUTPUT_FORMAT = MediaRecorder.OutputFormat.DEFAULT;
	static final String SAMPLE_AUDIO_EXTENSION = ".mp3";

    public static final int IDLE_STATE = 0;
    public static final int RECORDING_STATE = 1;
    public static final int PLAYING_STATE = 2;
    
    int mState = IDLE_STATE;

    public static final int NO_ERROR = 0;
    public static final int SDCARD_ACCESS_ERROR = 1;
    public static final int INTERNAL_ERROR = 2;
    public static final int IN_CALL_RECORD_ERROR = 3;
    
    public interface OnStateChangedListener {
        public void onStateChanged(int state);
        public void onProgress(long progress);
        public void onError(int error);
    }
    OnStateChangedListener mOnStateChangedListener = null;
    
    long mAudiotart = 0;       // time at which latest record or play operation started
    int mAudioLength = 0;      // length of current sample
    File mAudioFile = null;
    
    
    MediaRecorder mRecorder = null;
    MediaPlayer mPlayer = null;
    
    public static String AUDIO_BASEDIR = "/Android/data/com.supersonic/audio";

    private File recordStorageDirectory;
    
    public Recorder() {
    }
    
    public void saveState(Bundle recorderState) {
        recorderState.putString(SAMPLE_PATH_KEY, mAudioFile.getAbsolutePath());
        recorderState.putInt(SAMPLE_LENGTH_KEY, mAudioLength);
    }
    
    public int getMaxAmplitude() {
        if (mState != RECORDING_STATE)
            return 0;
        return mRecorder.getMaxAmplitude();
    }
    
    public void restoreState(Bundle recorderState) {
        String samplePath = recorderState.getString(SAMPLE_PATH_KEY);
        if (samplePath == null)
            return;
        int sampleLength = recorderState.getInt(SAMPLE_LENGTH_KEY, -1);
        if (sampleLength == -1)
            return;

        File file = new File(samplePath);
        if (!file.exists())
            return;
        if (mAudioFile != null
                && mAudioFile.getAbsolutePath().compareTo(file.getAbsolutePath()) == 0)
            return;
        
        delete();
        mAudioFile = file;
        mAudioLength = sampleLength;

        signalStateChanged(IDLE_STATE);
    }
    
    public void setOnStateChangedListener(OnStateChangedListener listener) {
        mOnStateChangedListener = listener;
    }
    
    public boolean isPlaying() {
        return mState == PLAYING_STATE;
    }
    
    public int state() {
        return mState;
    }
    
    public int progress() {
        if (mState == RECORDING_STATE || mState == PLAYING_STATE)
            return (int) ((System.currentTimeMillis() - mAudiotart)/1000);
        return 0;
    }
    
    public int audioLength() {
        return mAudioLength;
    }

    public File audioFile() {
        return mAudioFile;
    }
    
    public void resetFile() {
    	mAudioFile = null;
    	mAudioLength = 0;
    	clear();
    }
    
    /**
     * Resets the recorder state. If a sample was recorded, the file is deleted.
     */
    public void delete() {
        stop();
        
        if (mAudioFile != null)
            mAudioFile.delete();

        mAudioFile = null;
        mAudioLength = 0;
        
        signalStateChanged(IDLE_STATE);
    }
    
    /**
     * Resets the recorder state. If a sample was recorded, the file is left on disk and will 
     * be reused for a new recording.
     */
    public void clear() {
        stop();
        
        mAudioLength = 0;
        
        signalStateChanged(IDLE_STATE);
    }
    
    public void startRecording(Context context) {
    	startRecording(AUDIO_OUTPUT_FORMAT, SAMPLE_AUDIO_EXTENSION, context);
    }
    
    public void startRecording(int outputfileformat, String extension, Context context) {
        stop();
        
        if (mAudioFile == null) { 
            recordStorageDirectory = new File(Environment.getExternalStorageDirectory(), AUDIO_BASEDIR);
            recordStorageDirectory.mkdirs();
            
            if (!recordStorageDirectory.canWrite()) // Workaround for broken sdcard support on the device.
            	recordStorageDirectory = new File("/sdcard/sdcard");   
//            try {
//                mAudioFile = File.createTempFile(SAMPLE_PREFIX, extension, recordStorageDirectory);              
//            } catch (IOException e) {
//                setError(SDCARD_ACCESS_ERROR);
//                return;
//            }
      
            mAudioFile = new File(recordStorageDirectory,System.currentTimeMillis() + extension);
        }
        
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
        mRecorder.setOutputFormat(outputfileformat);       
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mAudioFile.getAbsolutePath());

        // Handle IOException
        try {
            mRecorder.prepare();
        } catch(IOException exception) {
            setError(INTERNAL_ERROR);
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            return;
        }
        // Handle RuntimeException if the recording couldn't start
        try {
            mRecorder.start();
//            mHandler.sendMessage(mHandler.obtainMessage(MSG));
        } catch (RuntimeException exception) {
            AudioManager audioMngr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            boolean isInCall = audioMngr.getMode() == AudioManager.MODE_IN_CALL;
            if (isInCall) {
                setError(IN_CALL_RECORD_ERROR);
            } else {
                setError(INTERNAL_ERROR);
            }
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            return;
        }
        mAudiotart = System.currentTimeMillis();
        setState(RECORDING_STATE);
    }
    
    public void stopRecording() {
        if (mRecorder == null)
            return;

        try {
            mRecorder.stop();
            mRecorder.release();
        } catch (Exception e) {
		} finally {
	        mRecorder = null;

	        mAudioLength = (int)( (System.currentTimeMillis() - mAudiotart)/1000 );
	        setState(IDLE_STATE);
		}

    }
    
    public void startPlayback(File file) {
      stop();
        
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(file.getAbsolutePath());
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.prepare();
            mPlayer.start();
            
        } catch (IllegalArgumentException e) {
            setError(INTERNAL_ERROR);
            mPlayer = null;
            return;
        } catch (IOException e) {
            setError(SDCARD_ACCESS_ERROR);
            mPlayer = null;
            return;
        }
        
        mAudiotart = System.currentTimeMillis();
        setState(PLAYING_STATE);
    }
    
    public void startPlayback() {
    	startPlayback(mAudioFile);
    }
    
    public void stopPlayback() {
        if (mPlayer == null) // we were not in playback
            return;

        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        setState(IDLE_STATE);
    }
    
    public MediaPlayer getPlayer() {
		return mPlayer;
	}

	public MediaRecorder getRecorder() {
		return mRecorder;
	}

	public void stop() {
        stopRecording();
        stopPlayback();
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        stop();
        setError(SDCARD_ACCESS_ERROR);
        return true;
    }

    public void onCompletion(MediaPlayer mp) {
        stop();
    }
    
    private void setState(int state) {
        if (state == mState)
            return;
        
        mState = state;
        signalStateChanged(mState);
    }
    
    private void signalStateChanged(int state) {
        if (mOnStateChangedListener != null)
            mOnStateChangedListener.onStateChanged(state);
    }
    
    private void setError(int error) {
        if (mOnStateChangedListener != null)
            mOnStateChangedListener.onError(error);
    }

}
