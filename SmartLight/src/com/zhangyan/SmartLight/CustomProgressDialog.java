package com.zhangyan.SmartLight;

import com.zhangyan.SmartLight.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {
	
	TextView title_TextView;
	String titlename;
	public CustomProgressDialog(Context context,int style) {
		super(context,style);
		// TODO Auto-generated constructor stub
	}
	
	
	protected CustomProgressDialog(Context context, boolean cancelable,  
            OnCancelListener cancelListener) {  
        super(context, cancelable, cancelListener);  
        
        // TODO Auto-generated constructor stub  
    }  
  
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(getContext()).inflate(R.layout.waiting, null);
		title_TextView = (TextView) view.findViewById(R.id.progressdialog_title);
		setMessage(titlename);
		setContentView(view);
	}
	
	
	

	 @Override
	public void setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		super.setTitle(title);
	}


	public void setMessage(String str) {
		title_TextView.setText(str);
		//title.invalidate();
	}
	 
	 @Override  
	    public void show() {  
	        // TODO Auto-generated method stub  
	        super.show();  
	    }  

	
	
}
