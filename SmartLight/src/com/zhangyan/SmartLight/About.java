package com.zhangyan.SmartLight;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.zhangyan.SmartLight.R;
import com.zhangyan.SmartLight.Util.DeviceUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class About extends Activity {
	private TextView tv,version;
	private ImageView back;
	private SimpleAdapter adapter;
	private ArrayList<HashMap<String, Object>>  list;
	private ScrollDisabledListView listview;
	private String versionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.about);
		initView();
	    tv.setText("关于本软件"); 
	    setListener();
	    listMenu();
	    listview.setAdapter(adapter);
	    setVersionName();
	    setTranslucentStatusBar();
		super.onCreate(savedInstanceState);
	}
	
	
	
	private void initView(){
		tv = (TextView) findViewById(R.id.titleTv);
		version = (TextView) findViewById(R.id.version);
		back = (ImageView) findViewById(R.id.back);
		listview = (ScrollDisabledListView) findViewById(R.id.aboutlist);
		list = new ArrayList<HashMap<String,Object>>();
		adapter = new SimpleAdapter(About.this, list, R.layout.menu, new String[]{"1","2","3"}, new int[]{R.id.menupng,R.id.menu,R.id.rightpng});
	}
	
	  private void setTranslucentStatusBar(){
	    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
	    		Window window = getWindow();
	    		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	    		findViewById(R.id.title).setPadding(0, DeviceUtil.getStatusBarHeight(About.this), 0, 0);
	    	}
	    }
	
	private void setListener(){
		 OnClickListener_ImageView Listener = new OnClickListener_ImageView();
		 back.setOnClickListener(Listener);
		 ItemClickListener listener1 = new ItemClickListener();
		 listview.setOnItemClickListener(listener1);
	}
	
	private void listMenu(){
		for(int i = 0;i<3;i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			switch(i){
			case 0 :
				map.put("1", R.drawable.contact);
				map.put("2", "联系我们");
				map.put("3", R.drawable.right);
				list.add(map);
				break;
			case 1 :
				map.put("1", R.drawable.exit);
				map.put("2", "切换账户");
				list.add(map);
				break;
			case 2 :
				map.put("1", R.drawable.introduction);
				map.put("2", "全天候智能光伏照明系统介绍");
				map.put("3", R.drawable.right);
				list.add(map);
				break;
			}
		}
	}
	
	
	private void setVersionName(){
		try {
	    	PackageInfo info = About.this.getPackageManager().getPackageInfo(About.this.getPackageName(), 0);
	    	versionName = info.versionName;
	    	version.setText("v"+versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class ItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			TextView tv = (TextView) view.findViewById(R.id.menu);
			if(tv.getText()=="切换账户"){
				Intent intent = new Intent();
				intent.putExtra("isfromAboutActivity", true);
				intent.setClass(About.this, LoginActivity.class);
				startActivity(intent);
				About.this.finish();
				if(MainActivity.instance.msocket!=null){
					try {
						MainActivity.instance.msocket.close();
						MainActivity.instance.msocket = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				MainActivity.instance.finish();
			}
			else if(tv.getText()=="联系我们"){
			}
		}
		
	}
	
	class OnClickListener_ImageView implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
		
	}
    
	
}
