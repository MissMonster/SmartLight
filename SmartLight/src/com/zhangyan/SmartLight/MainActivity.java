package com.zhangyan.SmartLight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.zhangyan.SmartLight.R;
import com.zhangyan.SmartLight.Service.UpdateService;
import com.zhangyan.SmartLight.Util.FileUtil;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;  
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;  
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;  
import android.widget.RadioGroup;  
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
  
public class MainActivity extends FragmentActivity { 
	public static MainActivity instance = null;       //设置一个静态值，在AboutActivity 里结束 MainActivity
    private Fragment[] mFragments;  
    private RadioGroup bottomRg,OnlineQuery_RadioGroup;  
    private FragmentManager fragmentManager;  
    private FragmentTransaction fragmentTransaction;  
    private RadioButton rbOne, rbTwo, rbThree,singleonlinequery,allonlinequery;; 
    private HttpResponse httpResponse;
    private HttpEntity httpEntity;         
    private SimpleAdapter listdeviceadapter,listmenuadapter,listupdateadapter,listparameteradapter,listrunstatusadapter;       //显示设备  显示菜单 显示升级菜单 显示参数 的SimpleAdapter
    private ArrayList<HashMap<String,String>> listdevice,listrunstatus;
    private ArrayList<HashMap<String,Object>> listmenu,listupdate,listparameter;       
    private ScrollDisabledListView ListMenuView,ListUpdateView;   //自己定义的不能滚动的ListView  用于更多Fragment的菜单
    private ListView ListDeviceView,ListParameterView,ListRunStatusView;      //显示蓝牙设备和要修改的参数的ListView
    private ProgressBar scanprogress,scanstate;             //扫描蓝牙设备时的ProgressBar
    public BluetoothSocket msocket;                        //蓝牙socket
    private long currentsystemtime=0;                //连按两次返回键退出程序
    private BluetoothAdapter adapter;						//蓝牙
    private String NewVersion,DownloadAddr,MAC,Devicename;  //新版本 新版本下载地址  MAC地址  设备名称
    private int versioncode;
    private EditText edit_light_num_edittext,onlinequery_lightnum,viewrunstatus_edittext;
    private TextView setnum,onlinequerystate,bondstate,currentstate,viewrunstatus_state;
    public  TextView DataReceived;
    private int retry = 0,retryMax=0;;    //延时0.5s的重试次数和最大限制次数
    public int OrderNum = 0;             //命令标号
    private DrawerLayout mDrawer_layout;//DrawerLayout容器
    private RelativeLayout mMenu_layout_left;//左边抽屉
    private SwitchButton BluetoothSwitchButton,ScanDeviceSwitchButton;
    private Boolean ViewPrameter_Open_Status = false;
    private ProgressBar onlinequery_progressbar;
    private Drawable connect_notpressed,parametersetting_notpressed,more_notpressed,connect_pressed,parametersetting_pressed,more_pressed;  //底部导航栏图片
    private String Permission;   //权限
    TextView tv;
    
    
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        //去除原title  使用自定义title
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        setContentView(R.layout.activity_main);  
        //得到蓝牙设备
        adapter=BluetoothAdapter.getDefaultAdapter();
        //新建三个fragment
        mFragments = new Fragment[3];  
        fragmentManager = getSupportFragmentManager();  
        mFragments[0] = fragmentManager.findFragmentById(R.id.fragement_bluetooth) ;
        mFragments[1] = fragmentManager.findFragmentById(R.id.fragement_parameter);  
        mFragments[2] = fragmentManager.findFragmentById(R.id.fragement_more);
        fragmentTransaction = fragmentManager.beginTransaction()  
                .hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]);  
        fragmentTransaction.show(mFragments[0]).commit();  
        //打开和扫描蓝牙设备的开关
        
        BluetoothSwitchButton = (SwitchButton)fragmentManager.findFragmentById(R.id.fragement_bluetooth) .getView().findViewById(R.id.BluetoothSwitchButton);
        ScanDeviceSwitchButton = (SwitchButton)fragmentManager.findFragmentById(R.id.fragement_bluetooth) .getView().findViewById(R.id.ScanDeviceSwitchButton);
        
        
        //扫描设备的progressbar
        scanprogress=(ProgressBar) fragmentManager.findFragmentById(R.id.fragement_bluetooth) .getView().findViewById(R.id.scanprogress);
        
        
        ListDeviceView=(ListView) fragmentManager.findFragmentById(R.id.fragement_bluetooth) .getView().findViewById(R.id.listDeviceView);
        ListMenuView=(ScrollDisabledListView)fragmentManager.findFragmentById(R.id.fragement_more).getView().findViewById(R.id.listmenu);
        ListUpdateView=(ScrollDisabledListView)fragmentManager.findFragmentById(R.id.fragement_more).getView().findViewById(R.id.listupdate);
        
        //抽屉
        mDrawer_layout = (DrawerLayout) fragmentManager.findFragmentById(R.id.fragement_parameter).getView().findViewById(R.id.drawer_layout);
        mMenu_layout_left = (RelativeLayout)fragmentManager.findFragmentById(R.id.fragement_parameter). getView().findViewById(R.id.menu_layout_left);
        ListParameterView = (ListView) mMenu_layout_left.findViewById(R.id.menu_listView_l);
        DrawerItemClickListener mDrawerItemClickListener = new DrawerItemClickListener();
        ListParameterView.setOnItemClickListener(mDrawerItemClickListener);
        
        onitemclick onitemclicklistener=new onitemclick();             //定义itemclick接口
        updateonitemclick updateclick=new updateonitemclick();
        menuonitemclick menuclick = new menuonitemclick();
       
        
        ListUpdateView.setOnItemClickListener(updateclick);
        ListMenuView.setOnItemClickListener(menuclick);
        ListDeviceView.setOnItemClickListener(onitemclicklistener);
       
        
        listdevice=new ArrayList<HashMap<String,String>>();
        listrunstatus=new ArrayList<HashMap<String,String>>();
        listmenu=new ArrayList<HashMap<String,Object>>();
        listupdate=new ArrayList<HashMap<String,Object>>();
        listparameter=new ArrayList<HashMap<String,Object>>();
       
        
        listdeviceadapter=new SimpleAdapter(MainActivity.this, listdevice, R.layout.bluetooth_listview, new String[]{"1","2","3"}, new int[]{R.id.devicename,R.id.deviceaddr,R.id.bondstate});
        listmenuadapter=new SimpleAdapter(MainActivity.this, listmenu, R.layout.menu, new String[]{"1","2","3"}, new int[]{R.id.menupng,R.id.menu,R.id.rightpng});
        listupdateadapter=new SimpleAdapter(MainActivity.this, listupdate, R.layout.menu, new String[]{"1","2","3","4"}, new int[]{R.id.menupng,R.id.menu,R.id.rightpng,R.id.version});
        listparameteradapter=new SimpleAdapter(MainActivity.this, listparameter, R.layout.menu, new String[]{"1","2","3","4"}, new int[]{R.id.menupng,R.id.menu,R.id.rightpng,R.id.version});
        listrunstatusadapter = new SimpleAdapter(MainActivity.this,listrunstatus, R.layout.viewrunstatus_listview, new String[]{"1","2"}, new int[]{R.id.statusname,R.id.statusvalue});
        
        currentstate=(TextView)fragmentManager.findFragmentById(R.id.fragement_bluetooth).getView().findViewById(R.id.currentstate);
        currentstate.setText("当前状态：未与任何设备连接");
        scanstate=(ProgressBar) fragmentManager.findFragmentById(R.id.fragement_bluetooth).getView().findViewById(R.id.scanstate);
        tv = (TextView) fragmentManager.findFragmentById(R.id.fragement_parameter).getView().findViewById(R.id.permission_low);
        
        Intent intent = getIntent();
        Permission = intent.getStringExtra("permission");
        //System.out.println(Permission);
        
        setFragmentIndicator();
        getBluetoothStatus();                  //得到蓝牙状态
        ListMenu();                            //列出菜单
        DataReceived=(TextView) fragmentManager.findFragmentById(R.id.fragement_more).getView().findViewById(R.id.textView1);
    }  
    
    
    
    
    
    class DrawerItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			// TODO Auto-generated method stub
			Fragment fragment = null;
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			//根据item点击行号判断启用哪个Fragment
			switch (position){
				case 0: 
						fragment = new ViewPrameter();
						break;
				case 1: 
						fragment = new PowerSetting();
						break;
				case 2:
						fragment = new TimeSetting();
			     		break;		
				case 3: 
						fragment = new ThresholdVoltage();
						break;
//				case 4:
//						fragment = new BestPower();
//						break;
//				case 5:
//					    fragment = new ChargeMode();
//						break;
				default:
						break;
		  }
			ft.replace(R.id.content_frame,fragment);
			ft.commit();
			mDrawer_layout.closeDrawer(mMenu_layout_left);//关闭mMenu_layout
     
     }
     }
    
    

    
    //菜单
    public void ListMenu(){
    	String versionName = "";
    	try {
	    	PackageInfo info = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0);
	    	versionName = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	for(int i=0;i<3;i++){
    		HashMap<String,Object> map=new HashMap<String, Object>();
    		switch(i){
    		case 0: map.put("1", R.drawable.edit); 
		    		map.put("2","设置路灯编号");
		    		map.put("3", R.drawable.right);
		    		listmenu.add(map);
		    		ListMenuView.setAdapter(listmenuadapter); 
		    		break;
    		case 1: map.put("1", R.drawable.find);
		    		map.put("2","联网状态查询");
		    		map.put("3", R.drawable.right);
		    		listmenu.add(map);
		    		ListMenuView.setAdapter(listmenuadapter); 
		    		break;
    		case 2: map.put("1", R.drawable.query);
		    		map.put("2","路灯运行状态查看");
		    		map.put("3", R.drawable.right);
		    		listmenu.add(map);
		    		ListMenuView.setAdapter(listmenuadapter); 
		    		break;
    		}
    			
    	}
    	
    	for(int i=0;i<2;i++){
    		HashMap<String,Object> map=new HashMap<String, Object>();
    		switch(i){
    		case 0: map.put("1", R.drawable.update);
		    		map.put("2","检查更新");
		    	    map.put("4","当前版本：v"+versionName);
		    		listupdate.add(map);
		    		ListUpdateView.setAdapter(listupdateadapter); 
		    		break;
    		case 1: map.put("1", R.drawable.about); 
		    		map.put("2","关于本软件");
		    		listupdate.add(map);
		    		ListUpdateView.setAdapter(listupdateadapter); 
		    		break;
    		}
    	}
    	
    	if(Permission.contains("0")){
    		tv.setVisibility(View.GONE);
    		for(int i=0;i<4;i++){
        		HashMap<String,Object> map=new HashMap<String, Object>();
        		switch(i){
        		case 0: map.put("1", R.drawable.number1);
        				map.put("2","路灯参数查看");
        				map.put("3", R.drawable.right);
        				listparameter.add(map);
        				ListParameterView.setAdapter(listparameteradapter); 
        				break;
        		case 1: map.put("1", R.drawable.number2);
    		    		map.put("2","亮灯强度设置");
    		    		map.put("3", R.drawable.right);
    		    		listparameter.add(map);
    		    		ListParameterView.setAdapter(listparameteradapter); 
    		    		break;
        		case 2: map.put("1", R.drawable.number3);
    		    		map.put("2","亮灯时长设置");
    		    		map.put("3", R.drawable.right);
    		    		listparameter.add(map);
    		    		ListParameterView.setAdapter(listparameteradapter);  
    		    		break;
        		case 3: map.put("1", R.drawable.number4);
    		    		map.put("2","阈值设置");
    		    		map.put("3", R.drawable.right);
    		    		listparameter.add(map);
    		    		ListParameterView.setAdapter(listparameteradapter);  
    		    		break;
//        		case 4: map.put("1", R.drawable.number5);
//    		    		map.put("2","最佳功率点设置");
//    		    		map.put("3", R.drawable.right);
//    		    		listparameter.add(map);
//    		    		ListParameterView.setAdapter(listparameteradapter); 
//    		    		break;
//        		case 5: map.put("1", R.drawable.number6);
//			    		map.put("2","充电方式切换电压设置");
//			    		map.put("3", R.drawable.right);
//			    		listparameter.add(map);
//			    		ListParameterView.setAdapter(listparameteradapter); 
//			    		break;
        		}
        	}
    	}
    	else{
    		tv.setVisibility(View.VISIBLE);
    		for(int i=0;i<1;i++){
        		HashMap<String,Object> map=new HashMap<String, Object>();
        		switch(i){
        		case 0: map.put("1", R.drawable.number1);
        				map.put("2","路灯参数查看");
        				map.put("3", R.drawable.right);
        				listparameter.add(map);
        				ListParameterView.setAdapter(listparameteradapter); 
        				break;
        		default :
        				break;
        		}
    		}
    	}
    }
	 
    


    
  
    
    //菜单的点击事件
    class menuonitemclick  implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			TextView string;
			string = (TextView) view.findViewById(R.id.menu);
			if(string.getText()=="设置路灯编号"){
				if(Permission.contains("1")){
					Toast.makeText(MainActivity.this, "对不起，您是普通用户，无法使用本功能", Toast.LENGTH_SHORT).show();
				}
				else{
					DataReceived.setText("");
					if(msocket==null||!msocket.isConnected())
						Toast.makeText(MainActivity.this, "还没有连接到遥控器", Toast.LENGTH_SHORT).show();
					else{
						View edit_light_num_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.edit_light_num, null);
						setnum = (TextView) edit_light_num_view.findViewById(R.id.setnum);
						edit_light_num_edittext=(EditText) edit_light_num_view.findViewById(R.id.edit_light_num_edittext);
						Dialog alertDialog = new AlertDialog.Builder(MainActivity.this)
								.setView(edit_light_num_view)
								.setPositiveButton("设置", new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
											if(edit_light_num_edittext.length()!=3)
												Toast.makeText(MainActivity.this, "请输入三个数字", Toast.LENGTH_SHORT).show();
											else{
												Send("*SN L"+edit_light_num_edittext.getText());
												OrderNum = 1;
												setnum.setVisibility(View.VISIBLE);
												setnum.setTextColor(Color.rgb(0, 0, 0));
												setnum.setText("正在设置编号，请稍候..");
												DataReceived.setText("");
												retry = 0;
												retryMax = 16;
												timedelay500msThread timedelay = new timedelay500msThread();
												timedelay.start();
											}
											try  
									        {  
									            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
									            field.setAccessible(true);  
									             //设置mShowing值，欺骗android系统  
									            field.set(dialog, false);  
									        } catch(Exception e) {  
									            e.printStackTrace();  
									        }  
												
									}
								})
								.setNegativeButton("返回",new OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										try  
								        {  
								            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
								            field.setAccessible(true);  
								             //设置mShowing值，欺骗android系统  
								            field.set(dialog, true);  
								        } catch(Exception e) {  
								            e.printStackTrace();  
								        }  
									}
								}).
				                //setIcon(R.drawable.ic_launcher). 
				                create(); 
		               alertDialog.show();
					}
				}
			}
			if(string.getText()=="联网状态查询"){
				if(msocket==null||!msocket.isConnected())
					Toast.makeText(MainActivity.this, "还没有连接到遥控器", Toast.LENGTH_SHORT).show();
				else{
					View online_query_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.onlinequery, null);
					onlinequery_progressbar = (ProgressBar) online_query_view.findViewById(R.id.onlinequery_progressBar);
					singleonlinequery = (RadioButton) online_query_view.findViewById(R.id.singquery);
					allonlinequery = (RadioButton) online_query_view.findViewById(R.id.allquery);
					onlinequerystate = (TextView) online_query_view.findViewById(R.id.onlinequerystate);
					onlinequery_lightnum = (EditText) online_query_view.findViewById(R.id.onlinequery_lightnum);
					OnlineQuery_RadioGroup = (RadioGroup) online_query_view.findViewById(R.id.OnlineQuery_RadioGroup);
					OnRadioCheckedChanged CheckedChangedListener = new OnRadioCheckedChanged();
					OnlineQuery_RadioGroup.setOnCheckedChangeListener(CheckedChangedListener);
					Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).
							setView(online_query_view).
							setPositiveButton("查询", new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									onlinequerystate.setTextColor(Color.rgb(0, 0, 0));
									// TODO Auto-generated method stub
									if(singleonlinequery.isChecked()){
										if(onlinequery_lightnum.length()!=3)
											Toast.makeText(MainActivity.this, "请输入三个数字", Toast.LENGTH_SHORT).show();
										else{
											DataReceived.setText("");
											Send("*GS L"+onlinequery_lightnum.getText());
											OrderNum = 2;
											onlinequerystate.setVisibility(View.VISIBLE);
											onlinequerystate.setText("正在查询编号为L"+onlinequery_lightnum.getText()+"的网络状态，请稍候..");
											onlinequery_progressbar.setVisibility(View.VISIBLE);
											retry = 0;
											retryMax = 16;
											timedelay500msThread timedelay = new timedelay500msThread();
											timedelay.start();
										}
									}
									else if(allonlinequery.isChecked()){
										onlinequery_lightnum.setVisibility(View.GONE);
										DataReceived.setText("");
										Send("*GS");
										OrderNum = 3;
										onlinequerystate.setVisibility(View.VISIBLE);
										onlinequerystate.setText("正在查询所有路灯的网络状态，请稍候..");
										onlinequery_progressbar.setVisibility(View.VISIBLE);
										retry = 0;
										retryMax = 40;
										timedelay500msThread timedelay = new timedelay500msThread();
										timedelay.start();
												
									}
									
									try  
							        {  
							            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
							            field.setAccessible(true);  
							             //设置mShowing值，欺骗android系统  
							            field.set(dialog, false);  
							        } catch(Exception e) {  
							            e.printStackTrace();  
							        }  
								}
							}).
							setNegativeButton("返回", new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									try  
							        {  
							            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
							            field.setAccessible(true);  
							             //设置mShowing值，欺骗android系统  
							            field.set(dialog, true);  
							        } catch(Exception e) {  
							            e.printStackTrace();  
							        }  
								}
							}).create();
					alertDialog.show();
				}
			}
			if(string.getText()=="路灯运行状态查看"){
				if(msocket==null||!msocket.isConnected())
					Toast.makeText(MainActivity.this, "还没有连接到遥控器", Toast.LENGTH_SHORT).show();
				else{
					View viewrunstatus_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.viewrunstatus, null);
					viewrunstatus_edittext = (EditText) viewrunstatus_view.findViewById(R.id.viewrunstatus_edittext);
					viewrunstatus_state = (TextView) viewrunstatus_view.findViewById(R.id.viewrunstatus_state);
					ListRunStatusView = (ListView) viewrunstatus_view.findViewById(R.id.liststatusview);
					Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).
							setView(viewrunstatus_view).
							setPositiveButton("查询", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									   viewrunstatus_state.setTextColor(Color.rgb(0, 0, 0));
									   viewrunstatus_edittext.clearFocus();
										if(viewrunstatus_edittext.length()!=3)
											Toast.makeText(MainActivity.this, "请输入三个数字", Toast.LENGTH_SHORT).show();
										else{
											DataReceived.setText("");
											Send("*VS L"+viewrunstatus_edittext.getText());
											OrderNum = 9;
											viewrunstatus_state.setVisibility(View.VISIBLE);
											viewrunstatus_state.setText("正在查询编号为L"+viewrunstatus_edittext.getText()+"的运行状态，请稍候..");
											//onlinequery_progressbar.setVisibility(View.VISIBLE);
											retry = 0;
											retryMax = 16;
											InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
											if (imm.isActive()) { 
												//如果开启 
												imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
												//关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的 
											} 
											ListRunStatusView.setVisibility(View.GONE);
											timedelay500msThread timedelay = new timedelay500msThread();
											timedelay.start();
										}
									try  
							        {  
							            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
							            field.setAccessible(true);  
							             //设置mShowing值，欺骗android系统  
							            field.set(dialog, false);  
							        } catch(Exception e) {  
							            e.printStackTrace();  
							        }  
								}
							}).
							setNegativeButton("返回", new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									try  
							        {  
							            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
							            field.setAccessible(true);  
							             //设置mShowing值，欺骗android系统  
							            field.set(dialog, true);  
							        } catch(Exception e) {  
							            e.printStackTrace();  
							        }  
								}
							}).create();
					alertDialog.show();
				}
				
			}
			
		}
    	
    }
    
    
    
    class OnRadioCheckedChanged implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			switch(checkedId){
			case R.id.singquery :onlinequery_lightnum.setVisibility(View.VISIBLE);
			                     onlinequerystate.setVisibility(View.GONE);
								 break;
			case R.id.allquery : onlinequery_lightnum.setVisibility(View.GONE);
			 					 onlinequerystate.setVisibility(View.GONE);
				 			     break;
		    default :
		    					 break;
			}
		}
    	
    }
    
    
    
    
    //菜单的点击事件
    class updateonitemclick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			TextView string;
			string=(TextView) view.findViewById(R.id.menu);
			if(string.getText()=="检查更新"){
				Toast.makeText(MainActivity.this, "正在检查更新，请稍候..", Toast.LENGTH_SHORT).show();
				NewVersion="";
				checkupdateThread Checkupdate=new checkupdateThread();
				Checkupdate.start();
				timedelay10sThread TimeDelay=new timedelay10sThread();
				TimeDelay.start();
			}
			if(string.getText()=="关于本软件"){
				Intent intent =new Intent();
				intent.setClass(MainActivity.this, About.class);
				startActivity(intent);
			}
		}
    	
    }
        
   
    //listdeviceView的监听器  
    class onitemclick implements OnItemClickListener{


		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			bondstate=(TextView) view.findViewById(R.id.bondstate);
			if(bondstate.getText()=="已连接"){
				Toast.makeText(MainActivity.this, "已经连接到该设备", Toast.LENGTH_SHORT).show();
			}
			if(bondstate.getText()=="正在连接"){
				return;
			}
			if(bondstate.getText()!="已连接"){
				if(msocket!=null&&msocket.isConnected()){
					Toast.makeText(MainActivity.this, "存在未断开的连接，请先断开", Toast.LENGTH_SHORT).show();
				}
				else{
					currentstate.setText("当前状态：未与任何设备连接");
					TextView addr=(TextView)view.findViewById(R.id.deviceaddr);
					scanstate=(ProgressBar) view.findViewById(R.id.scanstate);
					MAC=addr.getText().toString();
					bondstate=(TextView) view.findViewById(R.id.bondstate);
					bondstate.setText("正在连接");
					bondstate.setTextColor(Color.rgb(0, 0, 0));
					scanstate.setVisibility(View.VISIBLE);
					ConnectDevice();
					ScanDeviceSwitchButton.setEnabled(false);
				}
			}
		}
	}
	
    
    //打开蓝牙
	public void EnableBluetooth(){
		listdevice.clear();
		ListDeviceView.setAdapter(listdeviceadapter);
		Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(intent, 1);
		
	} 
	
	
	
	//返回蓝牙打开的状态
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if(arg0==1){
			if(arg1==RESULT_OK){
				BluetoothSwitchButton.setChecked(true);
				Toast.makeText(MainActivity.this, "蓝牙开启成功", Toast.LENGTH_SHORT).show();
			}
			
			else{
				BluetoothSwitchButton.setChecked(false);
				Toast.makeText(MainActivity.this, "蓝牙开启失败，请重试", Toast.LENGTH_SHORT).show();
			}
		}
	}




	
	
	//关闭蓝牙
	public void DisableBluetooth(){
		adapter.disable();
		listdevice.clear();
		ListDeviceView.setAdapter(listdeviceadapter);
		scanprogress.setVisibility(View.INVISIBLE);
		ScanDeviceSwitchButton.setChecked(false);
		try {
			if(msocket!=null){
				msocket.close();
				msocket = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	
	
	
	
	//扫描设备
	public void ScanDevice(){
			if(adapter.isEnabled()){
				//注册广播
				IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
			  	registerReceiver(mBroadcastReceiver, filter);
			  	filter=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			  	registerReceiver(mBroadcastReceiver, filter);
			  	filter=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
			  	registerReceiver(mBroadcastReceiver, filter);
			  	//开始扫描
				listdevice.clear();
			  	adapter.startDiscovery();
			  	try {
					if(msocket!=null)
						msocket.close();
					    msocket = null;
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			else{
				Toast.makeText(MainActivity.this, "还没有打开蓝牙哦！", Toast.LENGTH_SHORT).show();
			 	ScanDeviceSwitchButton.setChecked(false);
			}
		}
	
	
	
	
	
	//连接设备
	public void ConnectDevice(){
	    	currentstate.setText("当前状态：未与任何设备连接");
			if(adapter.isEnabled()){
				if(msocket!=null){
					try {
						msocket.close();
						msocket=null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				connectThread connect=new connectThread();
				connect.start();
			}
			else{
				Toast.makeText(MainActivity.this, "还没有打开蓝牙哦！", Toast.LENGTH_SHORT).show();
			}
		}
     

		
	
	//取消扫描设备
	public void CancelScanDevice(){
		adapter.cancelDiscovery();
	}
	 
	
	
	
	
	//定义广播	用来接收系统关于蓝牙的广播
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action=intent.getAction();
				if(BluetoothDevice.ACTION_FOUND.equals(action)){
					BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					if (device.getBondState()!=BluetoothDevice.BOND_BONDED){
						HashMap<String, String> map=new HashMap<String, String>();
						map.put("1", device.getName());
						map.put("2",device.getAddress());
						map.put("3", "未配对");
						listdevice.add(map);	
					}
					ListDeviceView.setAdapter(listdeviceadapter); 
					if(device.getBondState()==BluetoothDevice.BOND_BONDED){
						HashMap<String, String> map=new HashMap<String, String>();
						map.put("1", device.getName());
						map.put("2", device.getAddress());
						map.put("3", "已配对但未连接");
						listdevice.add(map);
					}
					ListDeviceView.setAdapter(listdeviceadapter);
				}
				else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
					Toast.makeText(MainActivity.this, "正在扫描附近的蓝牙设备，请稍候...", Toast.LENGTH_SHORT).show();
					scanprogress.setVisibility(View.VISIBLE);
				}
				else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
					scanprogress.setVisibility(View.INVISIBLE);
					ScanDeviceSwitchButton.setChecked(false);
					if(listdeviceadapter.getCount()==0)
						Toast.makeText(MainActivity.this, "扫描结束,没有找到附近的蓝牙设备，请重试", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(MainActivity.this, "扫描结束,找到了"+listdeviceadapter.getCount()+"个蓝牙设备", Toast.LENGTH_SHORT).show();
				}
			}
	    };
    
	    
	    
	      
	    
	    
	//dip转换成px
    private  int dip2px(Context context, float dpValue) {  
            final float scale = context.getResources().getDisplayMetrics().density;  
            return (int) (dpValue * scale + 0.5f);  
     }  
   
  
    
    
    
    
    
    //配置Fragment
    public void setFragmentIndicator() {  
  
        bottomRg = (RadioGroup) findViewById(R.id.bottomRg);  
        rbOne = (RadioButton) findViewById(R.id.rbOne);  
        rbTwo = (RadioButton) findViewById(R.id.rbTwo);  
        rbThree = (RadioButton) findViewById(R.id.rbThree);
        int heightPx= dip2px(this, 30);
        //以下程序用来设置导航栏的三个图标
        connect_notpressed=getResources().getDrawable(R.drawable.connect_notpressed);
        parametersetting_notpressed=getResources().getDrawable(R.drawable.parametersetting_notpressed);
        more_notpressed=getResources().getDrawable(R.drawable.more_notpressed);
        connect_notpressed.setBounds(0, 0, heightPx, heightPx);
        parametersetting_notpressed.setBounds(0, 0, heightPx, heightPx);
        more_notpressed.setBounds(0, 0, heightPx, heightPx);
        connect_pressed=getResources().getDrawable(R.drawable.connect_pressed);
        parametersetting_pressed=getResources().getDrawable(R.drawable.parametersetting_pressed);
        more_pressed=getResources().getDrawable(R.drawable.more_pressed);
        connect_notpressed.setBounds(0, 0, heightPx, heightPx);
        parametersetting_notpressed.setBounds(0, 0, heightPx, heightPx);
        more_notpressed.setBounds(0, 0, heightPx, heightPx);
        connect_pressed.setBounds(0, 0, heightPx, heightPx);
        parametersetting_pressed.setBounds(0, 0, heightPx, heightPx);
        more_pressed.setBounds(0, 0, heightPx, heightPx);
        
        rbOne.setCompoundDrawables(null, connect_pressed, null, null);
        rbTwo.setCompoundDrawables(null, parametersetting_notpressed, null, null);
        rbThree.setCompoundDrawables(null, more_notpressed, null, null);
        rbOne.setTextColor(Color.rgb(0, 0, 0));
        bottomRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
  
            @Override  
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                fragmentTransaction = fragmentManager.beginTransaction()  
                        .hide(mFragments[0]).hide(mFragments[1])  
                        .hide(mFragments[2]);  
                switch (checkedId) {  
                case R.id.rbOne:  
                    fragmentTransaction.show(mFragments[0]).commit(); 
                    fragmentTransaction.hide(mFragments[1]);
                    fragmentTransaction.hide(mFragments[2]);
                    rbOne.setCompoundDrawables(null, connect_pressed, null, null);
                    rbTwo.setCompoundDrawables(null, parametersetting_notpressed, null, null);
                    rbThree.setCompoundDrawables(null, more_notpressed, null, null);
                    rbOne.setTextColor(Color.rgb(0, 0, 0));
                    rbTwo.setTextColor(Color.rgb(168, 168, 168));
                    rbThree.setTextColor(Color.rgb(168, 168, 168));
                    break;  
  
                case R.id.rbTwo:  
                    fragmentTransaction.show(mFragments[1]).commit();   //显示第二个fragment
                    fragmentTransaction.hide(mFragments[0]);
                    fragmentTransaction.hide(mFragments[2]);
                    //设置底部导航栏图标样式
                    rbOne.setCompoundDrawables(null, connect_notpressed, null, null);  
                    rbTwo.setCompoundDrawables(null, parametersetting_pressed, null, null);
                    rbThree.setCompoundDrawables(null, more_notpressed, null, null);
                    rbTwo.setTextColor(Color.rgb(0, 0, 0));
                    rbOne.setTextColor(Color.rgb(168, 168, 168));
                    rbThree.setTextColor(Color.rgb(168, 168, 168));
                    
                    if(!ViewPrameter_Open_Status){
                    	Fragment fragment = null;
             			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
             			fragment =  new ViewPrameter();
             			ft.replace(R.id.content_frame,fragment);
             			ft.commit();
             			ViewPrameter_Open_Status = true;
                    }
                    else{};
                    break;  
  
                case R.id.rbThree:  
                    fragmentTransaction.show(mFragments[2]).commit();
                    fragmentTransaction.hide(mFragments[0]);
                    fragmentTransaction.hide(mFragments[1]);
                    rbOne.setCompoundDrawables(null, connect_notpressed, null, null);
                    rbTwo.setCompoundDrawables(null, parametersetting_notpressed, null, null);
                    rbThree.setCompoundDrawables(null, more_pressed, null, null);
                    rbThree.setTextColor(Color.rgb(0, 0, 0));
                    rbTwo.setTextColor(Color.rgb(168, 168, 168));
                    rbOne.setTextColor(Color.rgb(168, 168, 168));
                    break;  
                    	
                default:  
                    break;  
                }  
            }  
        });  
    } 
    
  
      
    
    //启动程序时检测蓝牙的状态
    public void getBluetoothStatus(){
    	//本机没有蓝牙
    	if(adapter==null){
    		Toast.makeText(this, "没有检测到蓝牙设备", Toast.LENGTH_LONG).show();
    		BluetoothSwitchButton.setChecked(false);
    		BluetoothSwitchButton.setEnabled(false);
    		ScanDeviceSwitchButton.setEnabled(false);
    	}
    	//本机有蓝牙但是未开启
    	else if(!adapter.isEnabled()){
    		BluetoothSwitchButton.setChecked(false);
    		BluetoothSwitchButton.setEnabled(true);
    		ScanDeviceSwitchButton.setEnabled(true);
    	}
    	//本机有蓝牙且已开启
    	else if(adapter.isEnabled()){
    		BluetoothSwitchButton.setEnabled(true);
    		BluetoothSwitchButton.setChecked(true);
    		ScanDeviceSwitchButton.setEnabled(true);
    	}
    }
    
    
    
    
   
    //handler处理消息
    Handler mhandler=new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what){
			case 1: //蓝牙连接到设备返回的消息
					currentstate.setText("当前状态：已连接到"+Devicename);
					bondstate.setTextColor(Color.rgb(0, 0, 0));
					bondstate.setText("已连接");
					scanstate.setVisibility(View.INVISIBLE);
					ScanDeviceSwitchButton.setEnabled(true);
					timedelay500msThread timedelay1 = new timedelay500msThread();
					timedelay1.start();
					break;
			case 2: //延时500ms线程返回的消息
				    switch(OrderNum){
				    case 0:
				    		break;
				    case 1: //OrderNum = 1;    *SN L***    设置编号
					    	String str1;
						    str1 = DataReceived.getText().toString().replaceAll("\n","");
						    if(str1.contains("Success")){
						    	Toast.makeText(MainActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
						    	retry = 0;
						    	setnum.setText("设置成功！");
						    	setnum.setTextColor(Color.rgb(0, 0, 0));
						    	OrderNum = 0;
						    }
						    else{
						    	retry++;
						    	timedelay500msThread timedelay = new timedelay500msThread();
								timedelay.start();
								if(retry == 5){
									Send("*SN L"+edit_light_num_edittext.getText());
								}
								if(retry == 10){
									Send("*SN L"+edit_light_num_edittext.getText());
								}
								if(retry == retryMax){
									Toast.makeText(MainActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
									setnum.setText("设置失败，请重试！");
									setnum.setTextColor(Color.rgb(255, 0, 0));
									OrderNum = 0;
									retry = 0;
								}
						    }
							break;
				    case 2: //OrderNum = 2;  *GS L***  得到某个路灯联网状态
					    	String str2;
						    str2 = DataReceived.getText().toString().replaceAll("\n","");
						    if(str2.contains("Remoter Receive:")){
						    	Toast.makeText(MainActivity.this, onlinequery_lightnum.getText()+"：在线", Toast.LENGTH_SHORT).show();
						    	retry = 0;
						    	onlinequerystate.setText(onlinequery_lightnum.getText()+"：在线");
						    	onlinequery_progressbar.setVisibility(View.INVISIBLE);
						    	OrderNum = 0;
						    }
						    else{
						    	retry++;
						    	timedelay500msThread timedelay = new timedelay500msThread();
								timedelay.start();
								if(retry == 5){
									onlinequerystate.setText("尝试第2次查询路灯状态..");
									Send("*GS L"+onlinequery_lightnum.getText());
								}
								if(retry == 10){
									onlinequerystate.setText("尝试第3次查询路灯状态..");
									Send("*GS L"+onlinequery_lightnum.getText());
								}
								if(retry == retryMax){
									Toast.makeText(MainActivity.this, onlinequery_lightnum.getText()+"：不在线", Toast.LENGTH_SHORT).show();
									onlinequerystate.setText(onlinequery_lightnum.getText()+"：不在线，请重试！");
									onlinequery_progressbar.setVisibility(View.INVISIBLE);
									onlinequerystate.setTextColor(Color.rgb(255, 0, 0));
									OrderNum = 0;
									retry = 0;
								}
						    }
						    break;
				    case 3: // *GS  得到所有路灯联网状态
					    	String str3;
					    	str3 = DataReceived.getText().toString().replaceAll("\n","").replaceAll(" ","");
						    if(str3.contains("RemoterReceive:")){
						    	timedelay500msThread timedelay = new timedelay500msThread();
								timedelay.start();
						    	retry++;
						    	retryMax = 40;
						    	if(retry == 30){ //15s
						    		onlinequerystate.setText("正在对返回数据进行分析..");
						    		Send("*GS");
						    	}
						    	if(retry == retryMax){ //20s
						    		GetLightNumfromString(str3.replaceAll("RemoterReceive:", "").replaceAll("RemoterSend:", ""));
						    		OrderNum = 0;
						    		retryMax = 0;
						    		
						    	}
						    }
						    else{
						    	retry++;
						    	timedelay500msThread timedelay = new timedelay500msThread();
								timedelay.start();
								if(retry == 8){
									Send("*GS");
								}
								if(retry == 14){
									Send("*GS");
									retryMax = 20;
								}
								if(retry == retryMax){
									onlinequerystate.setText("没有路灯设备在线");
									onlinequerystate.setTextColor(Color.rgb(255, 0, 0));
									onlinequery_progressbar.setVisibility(View.INVISIBLE);
									OrderNum = 0;
									retryMax = 0;
								}
					     }
				    		break;
				    case 9: // *VS  得到路灯运行状态     返回结果如下面这种结构  :#13.20# 0.46# 0.00# 0.79#  0.0#00000   每个#号后面都有5个字符
				    	String str4;
					    str4 = DataReceived.getText().toString().replaceAll("\n","");
					    if(str4.contains("Remoter Receive:")){
					    	int c=0;
					    	String [] tempRunStatus = new String[10];
					    	try {
					    		for(int i=0;i<str4.length();i++){
						    		char k = str4.charAt(i);
						    		if(k=='#'){
						    			tempRunStatus[c] = str4.substring(i+1, i+6);
						    			c++;
						    		}
						    	}
							} catch (Exception e) {
								// TODO: handle exception
							}
					    	
					    	String [] StrongTimeStatus = new String[3];      //强亮时间的数组  传过来的是连续的五个数 需要将其拆分成 时：分：秒
					    	try {
					    		StrongTimeStatus[0] = tempRunStatus[5].substring(0,1);
					    		StrongTimeStatus[1] = tempRunStatus[5].substring(1,3);
					    		StrongTimeStatus[2] = tempRunStatus[5].substring(3,5);
							} catch (Exception e) {
								// TODO: handle exception
							}
					    	 
					    	String [] HalfTimeStatus = new String[3];
					    	try {
					    		HalfTimeStatus[0] = tempRunStatus[6].substring(0,1);
					    		HalfTimeStatus[1] = tempRunStatus[6].substring(1,3);
					    		HalfTimeStatus[2] = tempRunStatus[6].substring(3,5);
							} catch (Exception e) {
								// TODO: handle exception
							}
					    	
					    	String [] WeakTimeStatus = new String[3];
					    	try {
					    		WeakTimeStatus[0] = tempRunStatus[7].substring(0,1);
					    		WeakTimeStatus[1] = tempRunStatus[7].substring(1,3);
					    		WeakTimeStatus[2] = tempRunStatus[7].substring(3,5);
							} catch (Exception e) {
								// TODO: handle exception
							}
					    	
					    	String [] DawnTimeStatus = new String[3];
					    	try {
					    		DawnTimeStatus[0] = tempRunStatus[8].substring(0,1);
					    		DawnTimeStatus[1] = tempRunStatus[8].substring(1,3);
					    		DawnTimeStatus[2] = tempRunStatus[8].substring(3,5);
							} catch (Exception e) {
								// TODO: handle exception
							}
					    	
					    	
					    	listrunstatus.clear();
					    	viewrunstatus_state.setText("查询成功，结果如下：");
					    	ListRunStatusView.setVisibility(View.VISIBLE);
					    	try {
					    		for(int i=0;i<9;i++){
						        	HashMap<String, String> map = new HashMap<String, String>();
						        	switch(i){
						        	case 0: map.put("1","太阳板电压：");
								    		map.put("2",tempRunStatus[0]+" V");
								    		listrunstatus.add(map);
								    		ListRunStatusView.setAdapter(listrunstatusadapter);
								    		break;
						    		case 1: map.put("1", "超容电压:");
								    		map.put("2",tempRunStatus[1]+" V");
								    		listrunstatus.add(map);
								    		ListRunStatusView.setAdapter(listrunstatusadapter);
								    		break;
//						    		case 2: map.put("1","超容电流：");
//								    		map.put("2",tempRunStatus[2]+" A");
//								    		listrunstatus.add(2, map);
//								    		ListRunStatusView.setAdapter(listrunstatusadapter);
//								    		break;
						    		case 3: map.put("1", "锂电池电压：");
								    		map.put("2",tempRunStatus[3]+" V");
								    		listrunstatus.add(map);
								    		ListRunStatusView.setAdapter(listrunstatusadapter);
								    		break;
						    		case 4: map.put("1", "PWM占空比：");
								    		map.put("2",tempRunStatus[4]+" %");
								    		listrunstatus.add(map);
								    		ListRunStatusView.setAdapter(listrunstatusadapter);
								    		break;
						    		case 5: map.put("1", "强亮时间：");
								    		map.put("2",StrongTimeStatus[0]+":"+StrongTimeStatus[1]+":"+StrongTimeStatus[2]);
								    		listrunstatus.add(map);
								    		ListRunStatusView.setAdapter(listrunstatusadapter);
								    		break;
						    		case 6: map.put("1", "半亮时间：");
								    		map.put("2",HalfTimeStatus[0]+":"+HalfTimeStatus[1]+":"+HalfTimeStatus[2]);
								    		listrunstatus.add(map);
								    		ListRunStatusView.setAdapter(listrunstatusadapter);
								    		break;
						    		case 7: map.put("1", "弱亮时间：");
								    		map.put("2",WeakTimeStatus[0]+":"+WeakTimeStatus[1]+":"+WeakTimeStatus[2]);
								    		listrunstatus.add(map);
								    		ListRunStatusView.setAdapter(listrunstatusadapter);
								    		break;
						    		case 8: map.put("1", "黎明时间：");
								    		map.put("2",DawnTimeStatus[0]+":"+DawnTimeStatus[1]+":"+DawnTimeStatus[2]);
								    		listrunstatus.add(map);
								    		ListRunStatusView.setAdapter(listrunstatusadapter);
						    		break;
								    default :
								    	break;
						        	}
						    	}
							} catch (Exception e) {
								// TODO: handle exception
							}
					    	retry = 0;
					    	OrderNum = 0;
					    }
					    else{
					    	retry++;
					    	timedelay500msThread timedelay = new timedelay500msThread();
							timedelay.start();
							if(retry == 5){
								viewrunstatus_state.setText("尝试第2次查询路灯状态..");
								Send("*VS L"+viewrunstatus_edittext.getText());
							}
							if(retry == 10){
								viewrunstatus_state.setText("尝试第3次查询路灯状态..");
								Send("*VS L"+viewrunstatus_edittext.getText());
							}
							if(retry == retryMax){
								viewrunstatus_state.setTextColor(Color.rgb(255, 0, 0));
								Toast.makeText(MainActivity.this, viewrunstatus_edittext.getText()+"：查询失败", Toast.LENGTH_SHORT).show();
								viewrunstatus_state.setText(viewrunstatus_edittext.getText()+"：查询失败！");
								OrderNum = 0;
								retry = 0;
							}
					    }
			    		break;
					default :
							break;
				    }
					break;
			case 3:	//蓝牙连接失败返回的消息
					currentstate.setText("当前状态：未与任何设备连接");
					bondstate.setTextColor(Color.rgb(255, 0, 0));
					bondstate.setText("连接失败");
					msocket=null;
					scanstate.setVisibility(View.INVISIBLE);
					ScanDeviceSwitchButton.setEnabled(true);
					break;
			case 4: //延时10s线程返回的消息
					if(NewVersion=="")
						Toast.makeText(MainActivity.this, "网络连接超时", Toast.LENGTH_SHORT).show();
					break;
			case 5: //串口收到数据返回的消息
				   byte[] arrayOfByte = (byte[])msg.obj;
				   DataReceived.append(bytestoString(arrayOfByte, msg.arg1));
			       break;
			default:break;
			}
		}
		
	};
	
	
	
    
	 //得到最新版本号和下载地址的handler
	 Handler handler=new Handler(){
		 public void handleMessage(Message msg){
			    Bundle content=msg.getData();
			    NewVersion=content.getString("version");                //得到最新版本号
			    DownloadAddr=content.getString("addr");				    //得到最新版本的下载地址
			    //DataReceived.setText(String.valueOf(DownloadAddr.length()));   调试用
			    try {
			    	PackageInfo info = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0);
			    	versioncode = info.versionCode;
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    if(Integer.parseInt(NewVersion)>versioncode*10&&DownloadAddr.length()<600){         // 版本号比较  下载地址长度不超过600

			    	View updateview = LayoutInflater.from(getApplicationContext()).inflate(R.layout.update, null);   
					Dialog alertDialog = new AlertDialog.Builder(MainActivity.this)            //弹出对话框有新版本
							.setView(updateview)
			                //setMessage("检测到新版本，是否更新")
							.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									if (FileUtil.getInstance().hasSdCard()){
										Intent intent = new Intent();
										intent.putExtra("downloadURL", DownloadAddr);
										intent.setClass(MainActivity.this, UpdateService.class);
									    MainActivity.this.startService(intent);
									    Toast.makeText(MainActivity.this, "程序正在后台进行下载，请留意上方进度条", Toast.LENGTH_SHORT).show();
									}else{
										Uri uri=Uri.parse(DownloadAddr);
										Intent download=new Intent(Intent.ACTION_VIEW,uri);
										startActivity(download);
									}

								}
							})
							.setNegativeButton("取消",null)
			                //setIcon(R.drawable.ic_launcher). 
			                .create(); 
			        alertDialog.show();    
				}
			    else{
			    	Toast.makeText(MainActivity.this, "当前已是最新版", Toast.LENGTH_SHORT).show();         //没有新版本
			    }
			    super.handleMessage(msg);	 
		 }
	 };
	
	
	//延时10s线程(用于检测网络连接是否超时）
	class timedelay10sThread  extends Thread{ 
	        public void run() { 
	        	try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            Message msg = new Message();      
	            msg.what = 4;
	            mhandler.sendMessage(msg);    
	        }            
	    };  
	    
	    
	    
	    
	    
	    
	  //延时500ms线程(用于检测指令是否设置成功）
	class timedelay500msThread  extends Thread{    
		        public void run() { 
		        	try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            Message msg = new Message(); 
		            msg.what = 2;
		            if(retry != retryMax)
			           mhandler.sendMessage(msg);  
		        }            
		    };  
		    
		    
		    
	public void GetLightNumfromString(String str){
		int c=0;
		String tempstr[]= new String[100];
		//得到字符串数组   最后得到的结果类似（000,001,004）
		for(int i=0;i<str.length();i++){
			char k =str.charAt(i);
			if(k=='L'){
				tempstr[c] = str.substring(i+1, i+4);
				c++;
			}
		}
		 
		 if(tempstr.length!=0){
			 //去除字符串中重复
			 TreeSet<String> tr = new TreeSet<String>();
			 for(int i=0;i<c;i++){
			 System.out.print(tempstr[i]+" ");
			 tr.add(tempstr[i]);
			 
			 }
			 String[] s2= new String[tr.size()];
			 for(int i=0;i<s2.length;i++){
			 s2[i]=tr.pollFirst();//从TreeSet中取出元素重新赋给数组
			 System.out.print(s2[i]+" ");
			 }
			 
			 
			 
			//排序
			 for(int i=0;i<s2.length;i++){
				 for(int j=i;j<s2.length;j++){
					 if(Integer.parseInt(s2[j])<Integer.parseInt(s2[i]))
						 s2[i] = s2[j];
				 }
			 }
			 String succeedstr[]= new String[100];
			 String failedstr[]= new String[100];
			 int succeedcnt=0,failedcnt=0,tempcnt=0;
			 for(int i=0;i<=Integer.parseInt(s2[s2.length-1]);i++){
				 if(Integer.parseInt(s2[s2.length-1])-Integer.parseInt(s2[0])+1 == s2.length){
						failedcnt = 0;
					}
				else{
					if(i != Integer.parseInt(s2[tempcnt]) ){
						failedstr[failedcnt] = i+"";
						failedcnt++;
					}
					else{
						tempcnt++;
						succeedstr[succeedcnt] = i+"";
						succeedcnt++;
					}
				}
			 }
			 //返回设置的结果
			 onlinequerystate.setText("");
			 onlinequery_progressbar.setVisibility(View.INVISIBLE);
			 onlinequerystate.append("共查询到"+s2.length+"个路灯设备在线\n");
			 onlinequerystate.append("其中最大编号为:L"+String.format("%03d", Integer.parseInt(s2[s2.length-1]))+"\n");
			 onlinequerystate.append("最小编号为:L"+String.format("%03d", Integer.parseInt(s2[0]))+"\n");
				 
			 if(succeedcnt<failedcnt){
				 for(int t=0;t<succeedcnt;t++)
					 onlinequerystate.append("L"+String.format("%03d", Integer.parseInt(succeedstr[t]))+" ");
				 onlinequerystate.append("路灯在线，其余编号路灯不在线");
			 }
			 else {
				 if(failedcnt == 0){
					 onlinequerystate.append("全部路灯在线！");
				 }
				 else{
					 for(int t=0;t<failedcnt;t++)
						 onlinequerystate.append("L"+String.format("%03d", Integer.parseInt(failedstr[t]))+" ");
					 onlinequerystate.append("路灯不在线，其余编号路灯在线");
				 }
			 }
		 }
		 else return;
	}
	
	
	//软件在线升级线程
	class checkupdateThread  extends Thread{
		HttpGet httpGet=new HttpGet("http://www.cnblogs.com/ffanjing/articles/4451724.html");        //将最新版本号和下载地址存于该网页
		HttpClient httpClient=new DefaultHttpClient();
		InputStream inputStream=null;
		public void run(){
			try {
				httpResponse = httpClient.execute(httpGet);                              //htttpGet 网页内容
				httpEntity = httpResponse.getEntity();
				inputStream = httpEntity.getContent();
				char[] version = new char[3];
				int a=0,b=0,c=0;
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));           //读网页内容
				String result = "";
				String line = "";
				while((line = reader.readLine())!= null){
					result =result + line;
				}
				//String result = EntityUtils.toString(httpEntity);
				System.out.println(result);
				//以下操作为了得到存于网页上的新版本下载地址和版本号
				a=result.indexOf("^^^^");                                  
				b=result.indexOf("####");
				c=result.lastIndexOf("####");
				char[] addr = new char[c-b-4];
				result.getChars(a+4,a+7,version,0);
				result.getChars(b+4,c,addr,0);
				Bundle content=new Bundle();
				content.putString("version",String.valueOf(version));
				content.putString("addr",String.valueOf(addr));
				Message msg=new Message();
				msg.setData(content);           //设置msg携带的数据
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			   }
			finally{
				try{
					inputStream.close();
				   }catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				   }
			}
			
		}
		
	}
	
	
    //蓝牙配对线程
    class connectThread  extends Thread{
    	//根据MAC地址得到远程DCVICE（即要连接的DEVICE）
    	private BluetoothDevice mdevice=adapter.getRemoteDevice(MAC);
    	public void run(){
    		//配对前应取消对周围蓝牙设备的扫描
    		adapter.cancelDiscovery();
    		//串口的UUID
    		try {
    			String uuid ="00001101-0000-1000-8000-00805F9B34FB";                 //蓝牙串口的UUID
				msocket=mdevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
				msocket.connect();
			} catch (Exception e) {
				// TODO: handle exception
			}
    		
    		if(msocket.isConnected()){
				Devicename=mdevice.getName();                                //得到已连接蓝牙设备的名称
			}
    		
		if(mdevice.getBondState()==BluetoothDevice.BOND_BONDED){                 //连接成功
    			if(msocket.isConnected()){
    				Message msg=new Message();
    				msg.what=1;
    				mhandler.sendMessage(msg);
  				    ReadThread readdata=new ReadThread();
   				    readdata.start();
    			}                                   
    			else{
    				Message msg=new Message();                         //连接失败
    				msg.what=3;
    				mhandler.sendMessage(msg);
    			}
    		}
    	}
    }
    
    
   
    //发送数据函数
    public void Send(String text){
    	
    	
    	try {
  			 OutputStream os=msocket.getOutputStream();
  			 //os.flush();
  			 os.write(text.getBytes()); 
	   		} catch (Exception e) {
	   			// TODO: handle exception
	   		   e.printStackTrace();
	   		} 
    	
    	
    }
    
    
    
    
    //发送数据线程
    class SendMessageThread extends Thread{
	  String msend;
	  public void run(){
		  try {
   			 OutputStream os=msocket.getOutputStream();
   			 os.flush();
   			 os.write(msend.getBytes()); 
	   		} catch (Exception e) {
	   			// TODO: handle exception
	   		   e.printStackTrace();
	   		} 
	  }
	  
	  public void getString(String str){
		  msend = str;
	  }
  }
    
  
  
  
  
  
    //定义接受数据的线程
    class ReadThread extends Thread{
	  InputStream mmInStream;
  	public void run(){
  		try {
  			mmInStream=msocket.getInputStream();
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		  }
  		byte[] arrayOfByte1 = new byte[1024];
  		byte[] arrayOfByte2 = new byte[1024];
        while (true){
          int i;
          try{
              i = this.mmInStream.read(arrayOfByte1);
              if (i >= 1){
            	  System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
                  mhandler.obtainMessage(5, i, -1, arrayOfByte2).sendToTarget();
              }
              Thread.sleep(100L);
           }catch (Exception localException){
        	   
            }
          try {
			Thread.sleep(100L);
		  } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
        }
     }
  }
  
  
    //字节转换为字符串
    public static String bytestoString(byte[] paramArrayOfByte, int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer("");
    for (int i = 0; ; i++)
    {
      if (i >= paramInt)
        return localStringBuffer.toString();
      localStringBuffer.append((char)paramArrayOfByte[i]);
    }
  }
  
    
    	
    	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(this,UpdateService.class);
		stopService(intent);
		super.onDestroy();
	}                           


	
	
	//连按两次返回键退出程序
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - currentsystemtime) > 2000) {
                    Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
                    currentsystemtime = System.currentTimeMillis();
            } 
            else {
            	    try {
						unregisterReceiver(mBroadcastReceiver);
						 if(msocket != null){
							 msocket.close(); 
							 msocket = null;
						 }
					} catch (Exception e) {
						// TODO: handle exception
					}
            	    finish();
            	    System.exit(0);
            }
            return true;
		}
		return super.onKeyDown(keyCode, event);
	}



	
	

    
    
}  