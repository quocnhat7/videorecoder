package com.nhatnq.videorecorder.core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public abstract class BaseRecorder {
	protected static final String AUDIO_EXTENSION = ".m4a";
	protected static final String VIDEO_EXTENSION = ".mp4";
	protected File mOutputFile;
	protected boolean isRecording = false;
	protected OnRecordingListener mRecordingListener;
	public static final String FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	static{
		File folder = new File(FOLDER);
		if(! folder.exists()){
			folder.mkdirs();
		}
	}
	
	public interface OnRecordingListener{
		public void onPrepared();
		public void onStart();
		public void onError(String error);
		public void onFailure();
		public void onSuccess();
		public void onCancel();
	}
	
	public BaseRecorder(){
		mRecordingListener = new OnRecordingListener() {
			
			@Override
			public void onPrepared() {}
			
			@Override
			public void onSuccess() {}
			
			@Override
			public void onStart() {}
			
			@Override
			public void onFailure() {}
			
			@Override
			public void onError(String error) {}

			@Override
			public void onCancel() {}
		};
	}
	
	protected String getBaseRecorderFolder(){
		return FOLDER;
	}
	
	protected void setOutputFile(File outputFile){
		this.mOutputFile = outputFile;
	}
	
	public void setOnRecordingListener(OnRecordingListener listener){
		mRecordingListener = listener;
	}
	
	public File getOutputFile(){
		return mOutputFile;
	}
	
	public boolean deleteOutputFile(){
		if(mOutputFile != null && mOutputFile.exists()){
			return mOutputFile.delete();
		}
		return false;
	}
	
	public boolean isRecording(){
		return isRecording;
	}
	
	/**
	 * Generate audio file name
	 * @return
	 */
	protected static String genFileName(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
		return format.format(new Date()).toString();
	}
	
	public abstract boolean prepare();
	public abstract void startRecording();
	public abstract void stopRecording(boolean isCancel);
	public abstract void release();
	
}
