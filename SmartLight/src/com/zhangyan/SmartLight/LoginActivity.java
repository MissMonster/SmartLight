package com.zhangyan.SmartLight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.zhangyan.SmartLight.R;
import com.zhangyan.SmartLight.Util.DeviceUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private EditText username_EditText,password_EditText;          //用户名和密码的EditText
	private Button login;                 //登录按钮
	private TextView register,login_setting,forget_password,result;          //注册 登录设置 忘记密码 和一个调试用的TextView
	private String RESULT = "",PERMISSION = "1";  //RESULT表示登录结果，PERMISSION默认权限为普通  1为普通 0为高级
	private Socket socket = null;                     //socket类
	private InetSocketAddress addr;                   //socket地址类
	public static final String HOST = "120.55.98.138";  //远程服务器的地址
	public static final int PORT = 1401;              //远程服务器的端口号
	private boolean connectResult = false;            //连接远程服务器的结果  默认连接失败
	private int retry = 0;                            //重试次数
	private int retryMax = 8; 						//重试最大次数
	private OutputStream os;						//输出流
	private BufferedReader br;						//输入流
	private CustomProgressDialog logining,registering;  //自定义的Dialog  用来显示正在登录框
	long currentsystemtime=0,k=2000; 				//用来控制按两次返回键退出程序
	private SharedPreferences sp;					//SharedPreferences用来保存登录的账号和密码以及登录设置参数
	private SharedPreferences.Editor edit;          //
	private String USERNAME,PASSWORD;				//用户名 密码
	private boolean AUTO_LOGIN,SAVE_USERNAME;       //自动登录 和保存账号密码选项  boolean格式
	private static final int LOGIN = 1;             //远程连接的类型为登录
	private static final int REGISTER = 2;			//远程连接的类型为注册
	private int socketType;                         //远程连接的类型
	private String register_username,register_password;   //注册的账号 密码
	private boolean []  multichoice = new  boolean[2];
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);                  //不需要标题栏
		setContentView(R.layout.login);                                 //设置layout
		getLoginSettings();                                             //得到登录的设置参数
		initView();  //初始化View
		setTranslucentStatusBar();
		setListeners();                                                 //设置监听器
		addr = new InetSocketAddress(HOST, PORT);                       //设置远程服务器的地址和端口号
		Intent intent = getIntent();                                    //得到其他Activity传入的Intent
		//如果intent是从SplashActivity传入  则自动登录  也可能是用户手动点击切换账号，此种情况不需要自动登录
		boolean isfromSplashActivity = intent.getBooleanExtra("isfromSplashActivity", false);     
		if(AUTO_LOGIN&&isfromSplashActivity)
			login.callOnClick();
			
	}
	
	
    private void setTranslucentStatusBar(){
    	System.out.println("statusbar");
    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
    		System.out.println(">4.4");
    		Window window = getWindow();
    		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    		//findViewById(R.id.title).setPadding(0, DeviceUtil.getStatusBarHeight(this), 0, 0);
    		
    	}
    }
	
	
	
	private void getLoginSettings(){
		//以下操作用于得到登录的设置参数
		sp = getSharedPreferences("latestlogin", Activity.MODE_PRIVATE);   //先得到SharedPreferences  实例
		edit = sp.edit();                                                  //得到Edit实例 用来保存参数
		USERNAME = sp.getString("username", null);                         //得到username  默认为空
		PASSWORD = sp.getString("password", null);                         //得到password  默认为空
		AUTO_LOGIN = sp.getBoolean("auto_login", false);                   //得到是否自动登录参数 默认为false 即不自动登录
		SAVE_USERNAME = sp.getBoolean("save_username", true);			  //得到是否保存账户密码 默认为true 即保存
		multichoice[0] = SAVE_USERNAME;                                  //该数组在下面遇到时进行解释
		multichoice[1] = AUTO_LOGIN;
	}
	
	
	//初始化View
	private void initView(){
		//下面是对界面的一些初始化工作
		username_EditText = (EditText) findViewById(R.id.username);
		password_EditText = (EditText) findViewById(R.id.password);
		login = (Button) findViewById(R.id.login);
		login_setting = (TextView) findViewById(R.id.login_setting);
		register = (TextView) findViewById(R.id.register);
		forget_password = (TextView) findViewById(R.id.forget_password);
		result = (TextView) findViewById(R.id.result);
		if(SAVE_USERNAME){
			username_EditText.setText(USERNAME);
			password_EditText.setText(PASSWORD);
		}
		else{
			username_EditText.setText("");
			password_EditText.setText("");
		}
	}
	
	
	
	//绑定监听器
	private void setListeners(){
		ClickListener listener = new ClickListener();
		login.setOnClickListener(listener);
		forget_password.setOnClickListener(listener);
		login_setting.setOnClickListener(listener);
		register.setOnClickListener(listener);
	}
	
	
	
	
	//定义监听器
	class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.login :
							RESULT = " "; 
							socketType = LOGIN;
							if(username_EditText.length()==0||password_EditText.length()==0){
								Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
							}
							else{
								if(socket == null){
									socket = new Socket();
									new connectServerThread().start();  
								}
								else{
									new verifyAccountThread().start(); 
									new ReadDataThread().start();	
								}
								logining = new CustomProgressDialog(LoginActivity.this,R.style.WaitingDialog);
								logining.titlename = "登录中，请稍候..";
								logining.setCancelable(false);
								logining.show(); 
							}
							break;
			case R.id.login_setting :
							new AlertDialog.Builder(LoginActivity.this)
							//.setView(login_setting_view)
							.setMultiChoiceItems(new String[]{"保存登录信息","自动登录" }, multichoice, new OnMultiChoiceClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which, boolean isChecked) {
									// TODO Auto-generated method stub
									multichoice[which] = isChecked;
									edit.putBoolean("auto_login",multichoice[1] );
									edit.putBoolean("save_username",multichoice[0] );
									edit.commit();
								}
							})
							.show();
							break;
			case R.id.register :
							View register_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.register, null);
							final EditText username_register_editText = (EditText) register_view.findViewById(R.id.username_register);
							final EditText password_register_editText = (EditText) register_view.findViewById(R.id.password_register);
							final EditText password_again_register_editText = (EditText) register_view.findViewById(R.id.password_again_register);
						    new AlertDialog.Builder(LoginActivity.this)
							.setView(register_view)
							.setPositiveButton("确认", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									if(username_register_editText.length()==0||password_register_editText.length()==0||password_again_register_editText.length()==0){
										Toast.makeText(LoginActivity.this, "用户名或密码没有填写！", Toast.LENGTH_SHORT).show();
									}
									else if(username_register_editText.length()<4||password_register_editText.length()<4){
										Toast.makeText(LoginActivity.this, "用户名或密码长度不能小于4位", Toast.LENGTH_SHORT).show();
									}
									else{
										if(!password_register_editText.getText().toString().equals(password_again_register_editText.getText().toString())){ 
											Toast.makeText(LoginActivity.this, "两次输入的密码不一致！", Toast.LENGTH_SHORT).show();
											password_again_register_editText.getText().clear();
											password_register_editText.getText().clear();
										}
										else{                       //开始注册
											register_username = username_register_editText.getText().toString();
											register_password = password_register_editText.getText().toString();
											RESULT = " "; 
											socketType = REGISTER; 
											if(socket == null){
												socket = new Socket();
												new connectServerThread().start();  
											}
											else{
												new registerAccountThread().start();
												new ReadDataThread().start();	
											}
											registering = new CustomProgressDialog(LoginActivity.this,R.style.WaitingDialog);
											registering.titlename = "注册中，请稍候..";
											registering.setCancelable(false);
											registering.show(); 
										}
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
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								
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
							})
							.show();
							break;
			case R.id.forget_password :
							View forget_password_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.forget_password, null);
							new AlertDialog.Builder(LoginActivity.this).setView(forget_password_view).show();
							break;
			default :
				break;
			}
		}
		
	}
	
	
	
	//登陆账号
	private void loginAccount(){
		String user,psw;
		user = username_EditText.getText().toString().trim();
		psw = password_EditText .getText().toString().trim();
		if(socket!=null){
			try {
				os = socket.getOutputStream();
				String tmp = "#LOGIN#user#"+user+"#password#"+psw+"#"; 
				os.write(tmp.getBytes());
				//os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			return;
		}
	}
	
	
	
	//注册账号
		private void registerAccount(){
			if(socket!=null){
				try {
					os = socket.getOutputStream();
					String tmp = "#REGISTER#username#"+register_username+"#password#"+register_password+"#"; 
					os.write(tmp.getBytes());
					//os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				return;
			}
		}
	
	
	
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case 1:                                  //联网失败返回的消息
				if(socketType == LOGIN)
					logining.dismiss();                  //联网失败关闭diglog
				else if(socketType == REGISTER)
					registering.dismiss();
				Toast.makeText(LoginActivity.this, "远程服务器连接失败", Toast.LENGTH_SHORT).show();        //Toast 提示联网失败
				break;
			case 2:
				if(RESULT.contains("true")){            //若登录成功，服务器返回类似 true#1# 的字符串  1为权限值 取值为0或1
					try {
						os.close();                           //关闭socket连接  输入流 输出流
						br.close();
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//以下代码用于保存登录信息
					if(multichoice[0]){
						edit.putString("username", username_EditText.getText().toString().trim());
						edit.putString("password", password_EditText.getText().toString().trim());
						edit.commit();
					}
					else{
						edit.putString("username", null);
						edit.putString("password", null);
						edit.commit();
					}
					//取得登录用户的权限  截取字符串的形式
					for(int i = 0;i<RESULT.length();i++){
						if(RESULT.charAt(i) == '#' && i != RESULT.length()-1 )
							PERMISSION = RESULT.substring(i+1,i+2);
					}
					result.setText(RESULT);    //调试用
					//关闭正在登陆的 dialog
					logining.dismiss();
					Toast.makeText(LoginActivity.this, "登录成功,欢迎您  "+username_EditText.getText().toString().trim()+"!", Toast.LENGTH_SHORT).show();
					RESULT = "";
					//打开MainActivity  并传递权限值的信息
					Intent intent = new Intent();
					intent.putExtra("permission", PERMISSION);
					intent.setClass(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					LoginActivity.this.finish(); 
				}
				else if(RESULT.contains("false")){              //登录失败  服务器返回false
					result.setText(RESULT);    //调试用
					logining.dismiss();
					Toast.makeText(LoginActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT).show(); 
					retry = 0; 
					RESULT = ""; 
				}
				else{                                                 //服务器未返回消息重试
					retry = retry+1;
					if(retry > retryMax){                             //如果重试次数大于最大字数
						logining.dismiss();							  //关闭登陆的dialog
						Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();   //提示消息
						retry = 0;
					}
					else{                                              //没有达到重试最大次数
						new verifyAccountThread().start();			   //重启发送账号线程
						//new ReadDataThread().start();				   //重启读数据线程
					}
				}
				break;
			case 3: 
				//new HeartThread().start(); 
				break;
			case 4:
				if(RESULT.contains("succeed")){            //若注册成功
					result.setText(RESULT);    //调试用
					//关闭正在注册的 dialog
					registering.dismiss(); 
					Toast.makeText(LoginActivity.this, "注册成功，现在你可以登录了 "+"!", Toast.LENGTH_SHORT).show();
					RESULT = "";
				}
				else if(RESULT.contains("exist")){               //注册失败  服务器返回failed
					result.setText(RESULT);    //调试用
					registering.dismiss();
					Toast.makeText(LoginActivity.this, "该用户名已被占用，请更换用户名重试！", Toast.LENGTH_SHORT).show(); 
					retry = 0; 
					RESULT = ""; 
				}
				else if(RESULT.contains("failed")){              //注册失败  服务器返回failed
					result.setText(RESULT);    //调试用
					registering.dismiss();
					Toast.makeText(LoginActivity.this, "注册失败，请重试！", Toast.LENGTH_SHORT).show(); 
					retry = 0; 
					RESULT = ""; 
				}
				else{                                                 //服务器未返回消息重试
					retry = retry+1;
					if(retry > retryMax){                             //如果重试次数大于最大字数
						registering.dismiss();							  //关闭注册的dialog
						Toast.makeText(LoginActivity.this, "注册失败，请重试！", Toast.LENGTH_SHORT).show();   //提示消息
						retry = 0;
					}
					else if(retry <= retryMax){   
						//new ReadDataThread().start();				   //重启读数据线程//没有达到重试最大次数
						new registerAccountThread().start();			   //重启发送账号线程
					}
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
			
		}
		
	};
	
	
	
	//连接远程服务器的线程
	class connectServerThread extends Thread{

		private boolean  socketConnect(){
			try {
				socket.connect(addr, 5*1000);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			connectResult = socketConnect();        //connectResult 联网结果 true为联网成功 false为联网失败
			if(!connectResult){ 					//如果联网失败
				socket = null;						//关闭socket
				Message msg = new Message();        //Message 返回消息 更新UI
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
			else{
				switch(socketType){
				case LOGIN :
						//new HeartThread().start();
						new ReadDataThread().start();
						new verifyAccountThread().start();
						break;
				case REGISTER :
						//new HeartThread().start();
						new registerAccountThread().start();
						new ReadDataThread().start();
						break;
				default :
						break;
				}
			}
			super.run();
		}
		
	}
	
	
	//发送账号的线程  联网操作必须在线程中完成
	class verifyAccountThread extends Thread{ 
		@Override
		public void run() {
		// TODO Auto-generated method stub
			loginAccount();
			try {
				Thread.sleep(800);
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		
	//注册账号的线程  联网操作必须在线程中完成
	class registerAccountThread extends Thread{ 
		@Override
		public void run() {
		// TODO Auto-generated method stub
			registerAccount();
			try {
				Thread.sleep(800);
				Message msg = new Message();
				msg.what = 4;
				mHandler.sendMessage(msg);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	class ReadDataThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				//RESULT = "";
				String result = "";
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));    //为什么用while（（line = br.readline（））！=null）不行
				result = br.readLine();
				//result = result + br.readLine();  
				RESULT = result;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); 
			}
		}
	}
	
	
	
	
	   class HeartThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				os = socket.getOutputStream();
				os.write("Client Online".getBytes());
				try {
					Thread.sleep(10*1000);                       //每10s发一次心跳
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message msg = new Message();
			msg.what = 3;
			mHandler.sendMessage(msg);
		}
		   
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
	            	    finish();
	            	    System.exit(0);
	            }
	            return true;
			}
			return super.onKeyDown(keyCode, event);
		}

}
