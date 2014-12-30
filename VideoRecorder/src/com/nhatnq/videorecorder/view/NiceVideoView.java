package com.nhatnq.videorecorder.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class NiceVideoView extends VideoView {

	private int mVideoWidth;
	private int mVideoHeight;

	public NiceVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NiceVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public NiceVideoView(Context context) {
		super(context);
	}

	public void setVideoSize(int width, int height) {
		mVideoWidth = width;
		mVideoHeight = height;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		if (mVideoWidth > 0 && mVideoHeight > 0) {
			if (mVideoWidth * height > width * mVideoHeight) {
				height = width * mVideoHeight / mVideoWidth;
			} else if (mVideoWidth * height < width * mVideoHeight) {
				width = height * mVideoWidth / mVideoHeight;
			}
		}
		
		setMeasuredDimension(width, height);
	}
}