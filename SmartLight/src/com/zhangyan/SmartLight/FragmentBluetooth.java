package com.zhangyan.SmartLight;


import android.os.Build;
import android.os.Bundle;  
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;  

import com.zhangyan.SmartLight.R;
import com.zhangyan.SmartLight.SwitchButton.OnChangeListener;
import com.zhangyan.SmartLight.Util.DeviceUtil;
  
public class FragmentBluetooth extends Fragment {  
    private TextView tv;  
	TextView currentstate;
	SwitchButton BlueToothSwitchButton,ScanDeviceSwitchButton;
	
	
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
    	return inflater.inflate(R.layout.fragment_bluetooth, container, false);  
    }  
  
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  
        tv = (TextView) getView().findViewById(R.id.titleTv);  
        tv.setText("连接");  
        currentstate=(TextView) getView().findViewById(R.id.currentstate);
        currentstate.setText("当前状态：未与任何设备连接");
        BlueToothSwitchButton = (SwitchButton) getView().findViewById(R.id.BluetoothSwitchButton);
        ScanDeviceSwitchButton = (SwitchButton) getView().findViewById(R.id.ScanDeviceSwitchButton);
        OnCheckedChangeListener_SwitchButton Listener =new OnCheckedChangeListener_SwitchButton();
        BlueToothSwitchButton.setOnChangeListener(Listener);
        ScanDeviceSwitchButton.setOnChangeListener(Listener);
        setTranslucentStatusBar();
    }
    
    private void setTranslucentStatusBar(){
    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
    		Window window = getActivity().getWindow();
    		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    		getView().findViewById(R.id.title).setPadding(0, DeviceUtil.getStatusBarHeight(getActivity()), 0, 0);
    		
    	}
    }
    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	class OnCheckedChangeListener_SwitchButton implements OnChangeListener{

		@Override
		public void onChange(SwitchButton sb, boolean state) {
			// TODO Auto-generated method stub
			MainActivity Activity = (MainActivity) getActivity();
			if(sb == BlueToothSwitchButton){
				if(state)
					Activity.EnableBluetooth();
				else
					Activity.DisableBluetooth();
			}
			else if(sb == ScanDeviceSwitchButton){
				if(state){
					Activity.ScanDevice();
					currentstate.setText("当前状态：未与任何设备连接");
				}
				else
					Activity.CancelScanDevice();
			}
		}
		
	}
	
	
    
}
    	

