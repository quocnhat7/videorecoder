package com.nhatnq.videorecorder.ui;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.nhatnq.videorecorder.R;
import com.nhatnq.videorecorder.Util;

public class VideoPreviewActivity extends Activity {
	ImageView ivPlayStop;
	VideoView vvVideo;
	TextView tvSize, tvDuration;
	String mVideoPath;
	int mVideoDuration;
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_video_preview);
		
		ivPlayStop = (ImageView)findViewById(R.id.video_playstop);
		ivPlayStop.setOnClickListener(onClick);
		ivPlayStop.setVisibility(View.VISIBLE);
		
		vvVideo = (VideoView)findViewById(R.id.video_view);
		tvDuration = (TextView)findViewById(R.id.video_duration);
		tvSize = (TextView)findViewById(R.id.video_size);
		findViewById(R.id.send).setOnClickListener(onClick);
		
		mVideoPath = getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
		if(!TextUtils.isEmpty(mVideoPath)){
			vvVideo.setVideoPath(mVideoPath);
			
			Bitmap bm = ThumbnailUtils.createVideoThumbnail(mVideoPath, Thumbnails.MINI_KIND);
			BitmapDrawable drawable = new BitmapDrawable(getResources(), bm);
			vvVideo.setBackground(drawable);
		}
		initVideoView();
	};
	
	void initVideoView(){
		vvVideo.setKeepScreenOn(true);
		
		vvVideo.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer arg0) {
				File file = new File(mVideoPath);
				tvSize.setText(Util.getFileSize(getBaseContext(), file.length()));
				
				mVideoDuration = arg0.getDuration();
				tvDuration.setText(getDurationText(mVideoDuration, false));
			}
		});
		
		vvVideo.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				//Set text by video's duration
				tvDuration.setText(getDurationText(mVideoDuration, false));
				mVideoPosition = 0;
				setUI(0);
			}
		});
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
			vvVideo.setOnInfoListener(new OnInfoListener() {
				
				@Override
				public boolean onInfo(MediaPlayer mp, int what, int extra) {
					if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
						vvVideo.setBackgroundColor(Color.TRANSPARENT);
					}
					return false;
				}
			});
		}
		
		vvVideo.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					handleClick(R.id.video_playstop);
					return false;
				}
				return false;
			}
		});
		
		setUI(-1);
	}
	
	private void setUI(int tag){
		if(! ivPlayStop.isShown()){
			ivPlayStop.setVisibility(View.VISIBLE);
		}
		if(tag < 1){
			//Video is not started yet or
			ivPlayStop.setImageResource(R.drawable.play);
//			mUIHandler.removeCallbacks(mUIRunnable);
		}else{
			//Video was playing and paused
			ivPlayStop.setImageResource(R.drawable.pause);
		}
		ivPlayStop.setTag(Integer.valueOf(tag));
		
		//Run auto-hide if video is paused OR is playing 
		if(tag >= 0) autoHideIcons();
	}
	
	private void autoHideIcons(){
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if((Integer)ivPlayStop.getTag() >= 0){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if(vvVideo.isPlaying()){
								ivPlayStop.setVisibility(View.INVISIBLE);
							}
						}
					});
				}
			}
		}, 2000);
	}
	
	int mVideoPosition = -1;
	
	View.OnClickListener onClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			handleClick(v.getId());
		}
	};
	
	private void handleClick(int viewId){
		if(viewId == R.id.video_playstop){
			Integer tag = (Integer)ivPlayStop.getTag();
			if(tag == 1){		//PAUSE
				vvVideo.pause(); mVideoPosition = vvVideo.getCurrentPosition();
				mUIHandler.removeCallbacks(mUIRunnable);
				setUI(0);
			}else if(tag == 0){	//RESUME
				vvVideo.start(); vvVideo.seekTo(mVideoPosition);
				setUI(1);
				int delta = durationCounter*1000 - mVideoPosition; 
				if(delta < 0) delta = 0;
				else delta = delta % 1000;
				mUIHandler.postDelayed(mUIRunnable, delta);
			}else if(tag == -1){//START
				vvVideo.start(); vvVideo.seekTo(mVideoPosition);
				setUI(1);
				mUIHandler.post(mUIRunnable);
			}
		}else if(viewId == R.id.send){
			if(vvVideo.isPlaying()){
				vvVideo.stopPlayback();
			}
			//TODO Send out your video to somewhere
			finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("position", vvVideo.getCurrentPosition());
		vvVideo.pause();
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int position = savedInstanceState.getInt("position");
		vvVideo.seekTo(position);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	private int durationCounter = 0;
	
	private Handler mUIHandler = new Handler();
	
	private Runnable mUIRunnable = new Runnable() {
		
		@Override
		public void run() {
			if(vvVideo.isPlaying()){
				tvDuration.setText(getPlayingDurationText(
						vvVideo.getCurrentPosition(), mVideoDuration));
				durationCounter++;
				mUIHandler.postDelayed(this, 1000);
			}else{
				durationCounter = 0;
				mUIHandler.removeCallbacks(mUIRunnable);
			}
		}
	};
	
	
	private String getDurationText(int durationInMili, boolean fixed){
		int seconds;
		if(durationInMili < 500 && !fixed) seconds = 1;
		else seconds = (durationInMili + 500)/1000;
		
		return Util.formatDuration(seconds);
	}
	
	private String getPlayingDurationText(int playingDurationInMili, int durationInMili){
		return getDurationText(playingDurationInMili, true)+"/"+getDurationText(durationInMili, false);
	}
	
}
