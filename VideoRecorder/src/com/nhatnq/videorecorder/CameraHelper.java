package com.nhatnq.videorecorder;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import com.nhatnq.videorecorder.core.DefaultVideoRecorder;

public class CameraHelper {
	private static String TAG = CameraHelper.class.getCanonicalName();
	
	public static boolean isCameraSupported(Context context) {
		return context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA);
	}

	public static CameraWrapper getCamera(int cameraId) {
		Camera.CameraInfo camInfo = new CameraInfo();
		try {
			Camera c = Camera.open(cameraId);
			Camera.getCameraInfo(cameraId, camInfo);
			if (c != null)
				return new CameraWrapper(c, camInfo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addCameraAttributes(Activity context, CameraWrapper cameraPair){
		if (cameraPair == null) {
			throw new IllegalStateException("No camera found to set!");
		}
		
		android.hardware.Camera.CameraInfo info = cameraPair.getCameraInfo();
		Display display = context.getWindowManager().getDefaultDisplay();
	    int screenWidth = display.getWidth();
	    int screenHeight = display.getHeight();
		int screenRotation = display.getRotation();
		int degrees = 0;
		switch (screenRotation) {
		case Surface.ROTATION_0:
			degrees = 0; break;
		case Surface.ROTATION_90:
			degrees = 90; break;
		case Surface.ROTATION_180:
			degrees = 180; break;
		case Surface.ROTATION_270:
			degrees = 270; break;
		}

		int rotation, orientation;
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
			rotation = 360 - result;
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
			rotation = result;
		}
		orientation = result;
		
		final Camera camera = cameraPair.getCamera();
		Parameters params = camera.getParameters();
		Size size = null;
		try{
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
				if(params.isVideoStabilizationSupported()){
					params.setVideoStabilization(true);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			size = getOptimalPreviewSize(params.getSupportedPictureSizes(), 
					screenWidth, screenHeight);
			params.setPictureSize(size.width, size.height);
			Log.i(TAG, "@Camera picture size = "+size.width+"_"+size.height);
			
			size = getOptimalPreviewSize(params.getSupportedPreviewSizes(), 
					screenWidth, screenHeight);
			params.setPreviewSize(size.width, size.height);
			Log.i(TAG, "@Camera preview size = "+size.width+"_"+size.height);
			
			//Auto focus mode
			List<String> supportedFocus = params.getSupportedFocusModes();
			if(supportedFocus != null && !supportedFocus.isEmpty()){
				if(supportedFocus.contains("continuous-video")){
					params.setFocusMode("continuous-video");
				}else if(supportedFocus.contains("auto")){
					params.setFocusMode("auto");
				}
				Log.i(TAG, "@Camera focusMode = "+params.getFocusMode()+" > "+supportedFocus.toString());
			}
//			params.setRecordingHint(true);
			//Set orientation for camera preview
			params.set("orientation", "portrait");
			params.setRotation(rotation);
			DefaultVideoRecorder.setRecorderRotation(rotation);
			camera.setParameters(params);
			
			Log.i(TAG, "@Camera ROTATION = "+rotation);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			//Set display orientation for video's picture
			camera.setDisplayOrientation(orientation);
			Log.i(TAG, "@Camera ORIENTATION = "+orientation);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			try{
				size = getOptimalPreviewSize(params.getSupportedVideoSizes(), screenWidth, screenHeight);
				DefaultVideoRecorder.setRecorderVideoSize(size);
				Log.i(TAG, "@Camera video size: w = "+size.width+", h = "+size.height);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private static Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		if (sizes == null || sizes.isEmpty()) return null;
		
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }

}
