package com.zhangyan.SmartLight;



import com.zhangyan.SmartLight.R;
import com.zhangyan.SmartLight.Util.DeviceUtil;

import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.Bundle;   
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;  
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


  
public class FragmentParameter extends Fragment {  
    private TextView tv;  
    private DrawerLayout mDrawer_layout;//DrawerLayoutÈÝÆ÷
    private RelativeLayout mMenu_layout_left;//×ó±ß³éÌë
    private ImageView canshushezhi;
    private boolean isDrawOpened = false;
     
    
    
    
    
    
    
    
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
        
    	return inflater.inflate(R.layout.fragment_parameter,container, false);  
        
    }  
  
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  
        tv = (TextView) getView().findViewById(R.id.titleTv);  
        tv.setText("Â·µÆ²ÎÊý");  
        mDrawer_layout = (DrawerLayout)getView().findViewById(R.id.drawer_layout);
        mMenu_layout_left = (RelativeLayout)getView().findViewById(R.id.menu_layout_left);
        canshushezhi = (ImageView) getView().findViewById(R.id.canshushezhi);
        imageclicklistener listener = new imageclicklistener();
        canshushezhi.setOnClickListener(listener);
        setTranslucentStatusBar();
        mDrawer_layout.setDrawerListener(new DrawerListener() {
			
			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService("input_method");
				imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(),0);
			}
			
			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub
				canshushezhi.setRotation(arg1*(-180));          //ÉèÖÃÍ¼Æ¬µÄÐý×ª
			}
			
			@Override
			public void onDrawerOpened(View arg0) {
				// TODO Auto-generated method stub
				isDrawOpened = true;
			}
			
			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub
				isDrawOpened = false;
			}
		});
    }  
    
   
    private void setTranslucentStatusBar(){
    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
    		Window window = getActivity().getWindow();
    		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    		getView().findViewById(R.id.title).setPadding(0, DeviceUtil.getStatusBarHeight(getActivity()), 0, 0);
    		
    	}
    }
    
   
   class imageclicklistener implements OnClickListener{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (isDrawOpened)
			mDrawer_layout.closeDrawer(mMenu_layout_left);
		else
			mDrawer_layout.openDrawer(mMenu_layout_left);
	}
	   
   }
   
   
     
    
    
    
    
    

    
  
}  
