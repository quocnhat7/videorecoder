package com.nhatnq.videorecorder;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;

public class Util {
	public static String genFileName(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss");
		return format.format(new Date()).toString();
	}
	
	public static String getReadableFileSize(long size){
		DecimalFormat format = new DecimalFormat("###.##");
		if(size > 1024 * 1024){
			return format.format(size / (1024f * 1024f)) + " MB";
		}else if(size > 1024){
			return format.format(size / 1024f) + " KB";
		}else if(size > 1) return size + " Byte";
		else return "1 Byte";
	}
	
	public static String formatDuration(long seconds) {
		long hour, min, sec;
		hour = seconds / 3600;
		min = (seconds % 3600) / 60;
		sec = seconds % 60;
		String hourYesOrNo = "";
		if (hour > 0)
			hourYesOrNo = formatHMS(hour) + ":";

		StringBuffer sbf = new StringBuffer();
		sbf.append(hourYesOrNo).append(formatHMS(min)).append(":")
				.append(formatHMS(sec));
		return sbf.toString();

	}

	private static String formatHMS(long val) {
		if (val < 10)
			return "0" + val;
		return val + "";
	}

	public static String getFileSize(Context context, long length) {
		Resources resouces = context.getResources();
		float fileSize = length/(1024f * 1024f);// in MB
		if(fileSize < 0.1f){
			return resouces.getString(R.string.x_mbs, "0.1");
		}else{
			float x = Float.parseFloat(String.format("%.1f", fileSize));
			if((x * 10) % 10 == 0) return resouces.getString(R.string.x_mbs, (int)x);
			return resouces.getString(R.string.x_mbs, x);
		}
	}
}
