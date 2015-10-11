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
	
	private EditText username_EditText,password_EditText;          //�û����������EditText
	private Button login;                 //��¼��ť
	private TextView register,login_setting,forget_password,result;          //ע�� ��¼���� �������� ��һ�������õ�TextView
	private String RESULT = "",PERMISSION = "1";  //RESULT��ʾ��¼�����PERMISSIONĬ��Ȩ��Ϊ��ͨ  1Ϊ��ͨ 0Ϊ�߼�
	private Socket socket = null;                     //socket��
	private InetSocketAddress addr;                   //socket��ַ��
	public static final String HOST = "120.55.98.138";  //Զ�̷������ĵ�ַ
	public static final int PORT = 1401;              //Զ�̷������Ķ˿ں�
	private boolean connectResult = false;            //����Զ�̷������Ľ��  Ĭ������ʧ��
	private int retry = 0;                            //���Դ���
	private int retryMax = 8; 						//����������
	private OutputStream os;						//�����
	private BufferedReader br;						//������
	private CustomProgressDialog logining,registering;  //�Զ����Dialog  ������ʾ���ڵ�¼��
	long currentsystemtime=0,k=2000; 				//�������ư����η��ؼ��˳�����
	private SharedPreferences sp;					//SharedPreferences���������¼���˺ź������Լ���¼���ò���
	private SharedPreferences.Editor edit;          //
	private String USERNAME,PASSWORD;				//�û��� ����
	private boolean AUTO_LOGIN,SAVE_USERNAME;       //�Զ���¼ �ͱ����˺�����ѡ��  boolean��ʽ
	private static final int LOGIN = 1;             //Զ�����ӵ�����Ϊ��¼
	private static final int REGISTER = 2;			//Զ�����ӵ�����Ϊע��
	private int socketType;                         //Զ�����ӵ�����
	private String register_username,register_password;   //ע����˺� ����
	private boolean []  multichoice = new  boolean[2];
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);                  //����Ҫ������
		setContentView(R.layout.login);                                 //����layout
		getLoginSettings();                                             //�õ���¼�����ò���
		initView();  //��ʼ��View
		setTranslucentStatusBar();
		setListeners();                                                 //���ü�����
		addr = new InetSocketAddress(HOST, PORT);                       //����Զ�̷������ĵ�ַ�Ͷ˿ں�
		Intent intent = getIntent();                                    //�õ�����Activity�����Intent
		//���intent�Ǵ�SplashActivity����  ���Զ���¼  Ҳ�������û��ֶ�����л��˺ţ������������Ҫ�Զ���¼
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
		//���²������ڵõ���¼�����ò���
		sp = getSharedPreferences("latestlogin", Activity.MODE_PRIVATE);   //�ȵõ�SharedPreferences  ʵ��
		edit = sp.edit();                                                  //�õ�Editʵ�� �����������
		USERNAME = sp.getString("username", null);                         //�õ�username  Ĭ��Ϊ��
		PASSWORD = sp.getString("password", null);                         //�õ�password  Ĭ��Ϊ��
		AUTO_LOGIN = sp.getBoolean("auto_login", false);                   //�õ��Ƿ��Զ���¼���� Ĭ��Ϊfalse �����Զ���¼
		SAVE_USERNAME = sp.getBoolean("save_username", true);			  //�õ��Ƿ񱣴��˻����� Ĭ��Ϊtrue ������
		multichoice[0] = SAVE_USERNAME;                                  //����������������ʱ���н���
		multichoice[1] = AUTO_LOGIN;
	}
	
	
	//��ʼ��View
	private void initView(){
		//�����ǶԽ����һЩ��ʼ������
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
	
	
	
	//�󶨼�����
	private void setListeners(){
		ClickListener listener = new ClickListener();
		login.setOnClickListener(listener);
		forget_password.setOnClickListener(listener);
		login_setting.setOnClickListener(listener);
		register.setOnClickListener(listener);
	}
	
	
	
	
	//���������
	class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.login :
							RESULT = " "; 
							socketType = LOGIN;
							if(username_EditText.length()==0||password_EditText.length()==0){
								Toast.makeText(LoginActivity.this, "�û��������벻��Ϊ��", Toast.LENGTH_SHORT).show();
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
								logining.titlename = "��¼�У����Ժ�..";
								logining.setCancelable(false);
								logining.show(); 
							}
							break;
			case R.id.login_setting :
							new AlertDialog.Builder(LoginActivity.this)
							//.setView(login_setting_view)
							.setMultiChoiceItems(new String[]{"�����¼��Ϣ","�Զ���¼" }, multichoice, new OnMultiChoiceClickListener() {
								
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
							.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									if(username_register_editText.length()==0||password_register_editText.length()==0||password_again_register_editText.length()==0){
										Toast.makeText(LoginActivity.this, "�û���������û����д��", Toast.LENGTH_SHORT).show();
									}
									else if(username_register_editText.length()<4||password_register_editText.length()<4){
										Toast.makeText(LoginActivity.this, "�û��������볤�Ȳ���С��4λ", Toast.LENGTH_SHORT).show();
									}
									else{
										if(!password_register_editText.getText().toString().equals(password_again_register_editText.getText().toString())){ 
											Toast.makeText(LoginActivity.this, "������������벻һ�£�", Toast.LENGTH_SHORT).show();
											password_again_register_editText.getText().clear();
											password_register_editText.getText().clear();
										}
										else{                       //��ʼע��
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
											registering.titlename = "ע���У����Ժ�..";
											registering.setCancelable(false);
											registering.show(); 
										}
									}
									try  
							        {   
							            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
							            field.setAccessible(true);  
							             //����mShowingֵ����ƭandroidϵͳ  
							            field.set(dialog, false);  
							        } catch(Exception e) {  
							            e.printStackTrace();  
							        }  
								}
							})
							.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
									try  
							        {  
							            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
							            field.setAccessible(true);  
							             //����mShowingֵ����ƭandroidϵͳ  
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
	
	
	
	//��½�˺�
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
	
	
	
	//ע���˺�
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
			case 1:                                  //����ʧ�ܷ��ص���Ϣ
				if(socketType == LOGIN)
					logining.dismiss();                  //����ʧ�ܹر�diglog
				else if(socketType == REGISTER)
					registering.dismiss();
				Toast.makeText(LoginActivity.this, "Զ�̷���������ʧ��", Toast.LENGTH_SHORT).show();        //Toast ��ʾ����ʧ��
				break;
			case 2:
				if(RESULT.contains("true")){            //����¼�ɹ����������������� true#1# ���ַ���  1ΪȨ��ֵ ȡֵΪ0��1
					try {
						os.close();                           //�ر�socket����  ������ �����
						br.close();
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//���´������ڱ����¼��Ϣ
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
					//ȡ�õ�¼�û���Ȩ��  ��ȡ�ַ�������ʽ
					for(int i = 0;i<RESULT.length();i++){
						if(RESULT.charAt(i) == '#' && i != RESULT.length()-1 )
							PERMISSION = RESULT.substring(i+1,i+2);
					}
					result.setText(RESULT);    //������
					//�ر����ڵ�½�� dialog
					logining.dismiss();
					Toast.makeText(LoginActivity.this, "��¼�ɹ�,��ӭ��  "+username_EditText.getText().toString().trim()+"!", Toast.LENGTH_SHORT).show();
					RESULT = "";
					//��MainActivity  ������Ȩ��ֵ����Ϣ
					Intent intent = new Intent();
					intent.putExtra("permission", PERMISSION);
					intent.setClass(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					LoginActivity.this.finish(); 
				}
				else if(RESULT.contains("false")){              //��¼ʧ��  ����������false
					result.setText(RESULT);    //������
					logining.dismiss();
					Toast.makeText(LoginActivity.this, "�û������������", Toast.LENGTH_SHORT).show(); 
					retry = 0; 
					RESULT = ""; 
				}
				else{                                                 //������δ������Ϣ����
					retry = retry+1;
					if(retry > retryMax){                             //������Դ��������������
						logining.dismiss();							  //�رյ�½��dialog
						Toast.makeText(LoginActivity.this, "��¼ʧ�ܣ�", Toast.LENGTH_SHORT).show();   //��ʾ��Ϣ
						retry = 0;
					}
					else{                                              //û�дﵽ����������
						new verifyAccountThread().start();			   //���������˺��߳�
						//new ReadDataThread().start();				   //�����������߳�
					}
				}
				break;
			case 3: 
				//new HeartThread().start(); 
				break;
			case 4:
				if(RESULT.contains("succeed")){            //��ע��ɹ�
					result.setText(RESULT);    //������
					//�ر�����ע��� dialog
					registering.dismiss(); 
					Toast.makeText(LoginActivity.this, "ע��ɹ�����������Ե�¼�� "+"!", Toast.LENGTH_SHORT).show();
					RESULT = "";
				}
				else if(RESULT.contains("exist")){               //ע��ʧ��  ����������failed
					result.setText(RESULT);    //������
					registering.dismiss();
					Toast.makeText(LoginActivity.this, "���û����ѱ�ռ�ã�������û������ԣ�", Toast.LENGTH_SHORT).show(); 
					retry = 0; 
					RESULT = ""; 
				}
				else if(RESULT.contains("failed")){              //ע��ʧ��  ����������failed
					result.setText(RESULT);    //������
					registering.dismiss();
					Toast.makeText(LoginActivity.this, "ע��ʧ�ܣ������ԣ�", Toast.LENGTH_SHORT).show(); 
					retry = 0; 
					RESULT = ""; 
				}
				else{                                                 //������δ������Ϣ����
					retry = retry+1;
					if(retry > retryMax){                             //������Դ��������������
						registering.dismiss();							  //�ر�ע���dialog
						Toast.makeText(LoginActivity.this, "ע��ʧ�ܣ������ԣ�", Toast.LENGTH_SHORT).show();   //��ʾ��Ϣ
						retry = 0;
					}
					else if(retry <= retryMax){   
						//new ReadDataThread().start();				   //�����������߳�//û�дﵽ����������
						new registerAccountThread().start();			   //���������˺��߳�
					}
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
			
		}
		
	};
	
	
	
	//����Զ�̷��������߳�
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
			connectResult = socketConnect();        //connectResult ������� trueΪ�����ɹ� falseΪ����ʧ��
			if(!connectResult){ 					//�������ʧ��
				socket = null;						//�ر�socket
				Message msg = new Message();        //Message ������Ϣ ����UI
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
	
	
	//�����˺ŵ��߳�  ���������������߳������
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
		
	//ע���˺ŵ��߳�  ���������������߳������
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
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));    //Ϊʲô��while����line = br.readline��������=null������
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
					Thread.sleep(10*1000);                       //ÿ10s��һ������
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
	
	
	//�������η��ؼ��˳�����
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK) {
	            if ((System.currentTimeMillis() - currentsystemtime) > 2000) {
	                    Toast.makeText(this, "�ٰ�һ�η��ؼ��˳�����", Toast.LENGTH_SHORT).show();
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
