package com.zhangyan.SmartLight;

import java.lang.ref.WeakReference;

import com.zhangyan.SmartLight.R;
import com.zhangyan.SmartLight.Interface.IUpdateInterface;
import com.zhangyan.SmartLight.Util.DeviceUtil;
import com.zhangyan.SmartLight.Util.FileUtil;

import android.os.Build;
import android.os.Bundle;  
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;  
import android.util.Log;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;  
  
public class FragmentMore extends Fragment implements IUpdateInterface {  
    private TextView tv;  
    public static final int PROGRESS_CHANGED = 1;
    int k = 0;
    ProgressBar updateProgressBar;
    
    
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {
    	
        return inflater.inflate(R.layout.fragment_more, container, false);  
    }  
  
    @Override 
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  
        tv = (TextView) getView().findViewById(R.id.titleTv);  
        tv.setText("¸ü¶à"); 
        updateProgressBar = (ProgressBar) getView().findViewById(R.id.updateprogressbar);
        setTranslucentStatusBar();
        FileUtil.getInstance().setOnProgressUpdateListener(FragmentMore.this);
    }
    
   
    private void setTranslucentStatusBar(){
    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
    		Window window = getActivity().getWindow();
    		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    		getView().findViewById(R.id.title).setPadding(0, DeviceUtil.getStatusBarHeight(getActivity()), 0, 0);
    		
    	}
    }
    
    
    
	@Override
	public void onResume() {
		// TODO Auto-generated m5ethod stub
		super.onResume();
		
	}
	
	
    private static class mHandler extends Handler{
    	WeakReference<FragmentMore> aRef;
    	
    	public mHandler(FragmentMore fm){
    		aRef = new WeakReference<FragmentMore>(fm);
    	}
    	
    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		switch (msg.what){
    		case PROGRESS_CHANGED :
    			Log.d("service1", "handle");
    			aRef.get().updateProgressBar.setVisibility(View.VISIBLE);;
    			aRef.get().updateProgressBar.setMax(msg.getData().getInt("total"));
    			aRef.get().updateProgressBar.setProgress(msg.getData().getInt("progress"));
    			Log.d("service1", msg.getData().getInt("progress")+"");
    			break;
    		}
    		super.handleMessage(msg);
    	}
    }
    
    mHandler handler = new mHandler(this);
	

	@Override
	public void onProgressUpdate(int progress, int total) {
		// TODO Auto-generated method stub
		Bundle data = new Bundle();
		data.putInt("total", total);
		data.putInt("progress", progress);
		Message msg = new Message();
		msg.setData(data);
		msg.what = PROGRESS_CHANGED;
		handler.sendMessage(msg);
	}

 
    
}  
