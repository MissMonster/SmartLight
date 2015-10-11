package com.zhangyan.SmartLight.Util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DeviceUtil {

	public static int getDeviceWitdh(Context context){
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService("window");
	    wm.getDefaultDisplay().getMetrics(dm);
	    return dm.widthPixels;
	}
	
	public static int getDeviceHeight(Context context){
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService("window");
	    wm.getDefaultDisplay().getMetrics(dm);
	    return dm.heightPixels;
	}
	
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
		     result = context.getResources().getDimensionPixelSize(resourceId);
		}
		 return result;
	}
}
