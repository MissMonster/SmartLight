package com.zhangyan.SmartLight.Service;

import java.io.File;

import com.zhangyan.SmartLight.Util.FileUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	
	private String downloadURL = null;
	private File Dir;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Dir = Environment.getExternalStorageDirectory();
		Log.d("service1",Dir.toString());
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d("service1","start");
		downloadURL = intent.getStringExtra("downloadURL");
		if (downloadURL != null){
			new Thread(new updateThread()).start();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	class updateThread implements Runnable{
        @Override
        public void run() {
        	// TODO Auto-generated method stub
        	File file = new File(Dir + "/Smart Light.apk");
        	FileUtil.getInstance().openApkFile(FileUtil.getInstance().saveFileFromUrl(downloadURL, file),UpdateService.this);
        }
	}
	



}
