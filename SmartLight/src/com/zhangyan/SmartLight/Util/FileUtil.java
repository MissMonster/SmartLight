package com.zhangyan.SmartLight.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.zhangyan.SmartLight.Interface.IUpdateInterface;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileUtil implements IUpdateInterface {
	private HttpGet httpGet;
	private HttpClient httpClient = new DefaultHttpClient();
	private HttpResponse httpResponse;
	private IUpdateInterface listener;
	private static FileUtil instance = null;
	
	
	public static FileUtil getInstance(){
		if (instance == null){
			synchronized (FileUtil.class) {
				if (instance == null)
					instance = new FileUtil();
			}
		}
		return instance;
	}
	
	public  boolean hasSdCard(){
		Log.d("service1", Environment.getExternalStorageState());
		Log.d("service1", Environment.MEDIA_MOUNTED);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return true;
		}
		else
			return false;
	}
	
	public void openApkFile(File file,Context context){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    //设置intent的data和Type属性。 
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive"); 
		context.startActivity(intent);
	}
	
	public  File saveFileFromUrl(String url,File file){
		if (url != null){
			httpGet = new HttpGet(url);
			InputStream is = null;
			try {
				httpResponse = httpClient.execute(httpGet);
				is = httpResponse.getEntity().getContent();
				int total = (int) httpResponse.getEntity().getContentLength();
				int len = 0;
				int hasdownloaded = 0;
				byte[] data = new byte[1024];
				FileOutputStream fos = new FileOutputStream(file);
				while ((len = is.read(data)) != -1){
					hasdownloaded += len;
					onProgressUpdate(hasdownloaded,total);
					Log.d("service1", hasdownloaded+"  " + total+"");
					fos.write(data, 0, len);
				}
				fos.close();
				is.close();
				return file;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void setOnProgressUpdateListener(IUpdateInterface listener){
	      this.listener = listener;
	      Log.d("service1", "listener");
	}

	@Override
	public void onProgressUpdate(int progress, int total) {
		// TODO Auto-generated method stub
		if (listener != null){
			listener.onProgressUpdate(progress, total);
		}
	}
	

}
