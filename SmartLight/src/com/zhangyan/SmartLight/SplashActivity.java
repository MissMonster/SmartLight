package com.zhangyan.SmartLight;


import com.zhangyan.SmartLight.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 2000; // ÑÓ³Ù2Ãë  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.splash);
		setTranslucentStatusBar();
		new Handler().postDelayed(new Runnable() {  
            public void run() {  
                Intent loginIntent = new Intent();  
                loginIntent.putExtra("isfromSplashActivity", true);
                loginIntent.setClass(SplashActivity.this, LoginActivity.class);
                SplashActivity.this.startActivity(loginIntent);  
                SplashActivity.this.finish();  
            }  
  
        }, SPLASH_DISPLAY_LENGHT);  
  
	}
	
    private void setTranslucentStatusBar(){
    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
    		Window window = getWindow();
    		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    		
    	}
    }
	

}


