package com.nhatnq.videorecorder;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

public class CameraWrapper {
	private Camera camera;
	private CameraInfo camInfo;
	private boolean release = false;
	
	public CameraWrapper(Camera camera){
		this.camera = camera;
	}
	
	public CameraWrapper(Camera camera, CameraInfo info){
		this.camera = camera;
		this.camInfo = info;
	}
	
	public void setCameraInfo(CameraInfo info){
		this.camInfo = info;
	}
	
	public Camera getCamera(){
		return camera;
	}
	
	public CameraInfo getCameraInfo(){
		return camInfo;
	}
	
	public void startPreview(){
		camera.startPreview();
	}
	
	public void stopPreview(){
		camera.stopPreview();
	}
	
	public void release(){
		if(camera != null){
			camera.release();
			release = true;
		}
	}
	
	public boolean isReleased(){
		return release;
	}
}
