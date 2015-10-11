package com.zhangyan.SmartLight;

import java.util.ArrayList;
import java.util.HashMap;



import com.zhangyan.SmartLight.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class TimeSetting extends Fragment {
	
	private ArrayList<HashMap<String,String>> list;
	private Spinner myspinner;
	private SimpleAdapter adapter;
	private Button confirmButton;
	private EditText timesetting_lightnum;
	private CustomProgressDialog waiting;
	private TextView timesetting_state,timesetting_seekbar_value;
	private SeekBar timesetting_seekbar;
	int retry,retryMax;
	String TimeSelected;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.timesetting, null);
	
	}
	
	
	public void onActivityCreated(Bundle savedInstanceState) {  
	    super.onActivityCreated(savedInstanceState);
	    confirmButton = (Button) getView().findViewById(R.id.timesetting_confirm);
	    timesetting_lightnum = (EditText) getView().findViewById(R.id.timesetting_lightnum);
	    timesetting_state = (TextView) getView().findViewById(R.id.timesetting_state);
	    timesetting_seekbar = (SeekBar) getView().findViewById(R.id.timesetting_seekbar);
	    timesetting_seekbar_value = (TextView) getView().findViewById(R.id.timesetting_seekbar_value);
	    list = new ArrayList<HashMap<String,String>>();
	    myspinner = (Spinner) getView().findViewById(R.id.timesetting_spinner);
	    adapter = new SimpleAdapter(getActivity(), list, R.layout.thresholdvoltageview, new String[]{"1"}, new int[]{R.id.threshold_item});
	    ClickListener listener = new ClickListener();
	    confirmButton.setOnClickListener(listener);
	    OnitemSelected listener2 = new OnitemSelected();
	    myspinner.setOnItemSelectedListener(listener2);
	    OnProgressChange listener3 = new OnProgressChange();
	    timesetting_seekbar.setOnSeekBarChangeListener(listener3);
	    ListTime();
	    myspinner.setAdapter(adapter);
	} 
	
	public void ListTime(){
		
		
		for(int i=0;i<5;i++){
    		HashMap<String,String> map=new HashMap<String, String>();
    		switch(i){
    		case 0: map.put("1", "强亮时长");
    				list.add(map); 
		    		break;
    		case 1: map.put("1", "半亮时长");
    				list.add(map);
    				break;
    		case 2: map.put("1", "弱亮时长");
					list.add(map); 
					break;
    		case 3: map.put("1", "黎明时长");
					list.add(map); 
					break;
    		case 4: map.put("1", "准备时长");
					list.add(map); 
					break;
    		default :
    				break;
    		
    		}
    			
    	}
	}
	
	
	//定义按钮的监听器
	class ClickListener  implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			timesetting_lightnum.clearFocus(); 
			MainActivity Activity = (MainActivity) getActivity();
			if(Activity.msocket==null||!Activity.msocket.isConnected())
				Toast.makeText(getActivity(), "还没有连接到遥控器", Toast.LENGTH_SHORT).show();
			else{
				if(timesetting_lightnum.length()!=3)
					Toast.makeText(getActivity(), "输入的数据不合法，路灯编号请输入三个数字", Toast.LENGTH_SHORT).show();
				else{
					if(TimeSelected=="强亮时长"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SST L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "正在设置强亮时长，请稍候..";
						waiting.setCancelable(false);
						waiting.show();
						timesetting_state .setText("");
					}
					else if(TimeSelected=="半亮时长"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SHT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "正在设置半亮时长，请稍候..";
						waiting.setCancelable(false);
						waiting.show();
						timesetting_state .setText("");
					}
					else if(TimeSelected=="弱亮时长"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SWT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "正在设置弱亮时长，请稍候..";
						waiting.setCancelable(false);
						waiting.show();
						timesetting_state .setText("");
					}
					else if(TimeSelected=="黎明时长"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SDT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "正在设置黎明时长，请稍候..";
						waiting.setCancelable(false);
						waiting.show();
						timesetting_state .setText("");
					}
					else if(TimeSelected=="准备时长"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SMC L"+timesetting_lightnum.getText().toString()+" "+String.format("%02d", timesetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "正在设置准备时长，请稍候..";
						waiting.setCancelable(false);
						waiting.show();
						timesetting_state .setText("");
					}
				}
			}
		}
				
	}
	
	
	class OnProgressChange implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			
			if(TimeSelected=="强亮时长"){
				timesetting_seekbar_value.setText(progress+"分钟");
			}
			else if(TimeSelected=="半亮时长"){
				timesetting_seekbar_value.setText(progress+"分钟");
			}
			else if(TimeSelected=="弱亮时长"){
				timesetting_seekbar_value.setText(progress+"分钟");
			}
			else if(TimeSelected=="黎明时长"){
				timesetting_seekbar_value.setText(progress+"分钟");
			}
			else if(TimeSelected=="准备时长"){
				timesetting_seekbar_value.setText(progress+"分钟");
			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	class OnitemSelected implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			TextView tv;
			tv = (TextView) view.findViewById(R.id.threshold_item);
			TimeSelected = tv.getText().toString();
			if(TimeSelected=="强亮时长"){
				timesetting_seekbar.setMax(720);
				timesetting_seekbar.setProgress(240);
				timesetting_seekbar_value.setText("240分钟");
				timesetting_seekbar.setKeyProgressIncrement(1);
			}
			else if(TimeSelected=="半亮时长"){
				timesetting_seekbar.setMax(720);
				timesetting_seekbar.setProgress(90);
				timesetting_seekbar_value.setText("90分钟");
				timesetting_seekbar.setKeyProgressIncrement(1);
			}
			else if(TimeSelected=="弱亮时长"){
				timesetting_seekbar.setMax(720);
				timesetting_seekbar.setProgress(330);
				timesetting_seekbar_value.setText("330分钟");
				timesetting_seekbar.setKeyProgressIncrement(1);
			}
			else if(TimeSelected=="黎明时长"){
				timesetting_seekbar.setMax(240);
				timesetting_seekbar.setProgress(90);
				timesetting_seekbar_value.setText("90分钟");
				timesetting_seekbar.setKeyProgressIncrement(1);
			}
			else if(TimeSelected=="准备时长"){
				timesetting_seekbar.setMax(99);
				timesetting_seekbar.setProgress(30);
				timesetting_seekbar_value.setText("30分钟");
				timesetting_seekbar.setKeyProgressIncrement(1);
			}
//		
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
	
	
	
			
			//延时500ms线程(用于检测指令是否设置成功）
	class delay500msThread  extends Thread{    
		public void run() { 
		 try {
				Thread.sleep(500);
		} catch (InterruptedException e) {
					// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 Message msg = new Message(); 
		 msg.what = 1;
	     if(retry != retryMax)
		     mhandler.sendMessage(msg);   
	     }            
	} 
					
	//Handler处理消息
	Handler mhandler =new Handler(){
		public void handleMessage(Message msg){
			MainActivity Activity = (MainActivity) getActivity();
			switch(msg.what){
			case 1:
					switch(Activity.OrderNum){
					case 4:		   
						    String str1;
						    str1 = Activity.DataReceived.getText().toString().replaceAll("\n","");
							if(str1.contains("Remoter Receive:")){
								waiting.dismiss();
								Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
								timesetting_state.setText("设置成功!");
								retry = 0;
								Activity.OrderNum = 0;
							}
							else{
								retry++;
								delay500msThread timedelay = new delay500msThread();
								timedelay.start();
								if(retry == 5){
									if(TimeSelected=="强亮时长"){
										Activity.Send("*SST L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="半亮时长"){
										Activity.Send("*SHT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="渐亮时长"){
										Activity.Send("*SWT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="黎明时长"){
										Activity.Send("*SDT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="准备时长"){
										Activity.Send("*SMC L"+timesetting_lightnum.getText().toString()+" "+String.format("%02d", timesetting_seekbar.getProgress()));
									}
									waiting.setMessage(" 进行第2次尝试...");
								}
								if(retry == 10){
									if(TimeSelected=="强亮时长"){
										Activity.Send("*SST L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="半亮时长"){
										Activity.Send("*SHT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="渐亮时长"){
										Activity.Send("*SWT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="黎明时长"){
										Activity.Send("*SDT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="准备时长"){
										Activity.Send("*SMC L"+timesetting_lightnum.getText().toString()+" "+String.format("%02d", timesetting_seekbar.getProgress()));
									}
									waiting.setMessage(" 进行第3次尝试 ...");
								}
								if(retry == retryMax){
									waiting.dismiss();
									Toast.makeText(getActivity(), "设置失败", Toast.LENGTH_SHORT).show();
									timesetting_state.setText("设置失败!");
									Activity.OrderNum = 0;
									retry = 0;
								}
							}
							break;
					default:
						break;
					}
			}
		}	
	};
	
//	 //得到路灯编号
//	private void GetlightNum(String str){
//		 int c=0;
//		 String tempstr[]= new String[100];
//		//得到字符串数组   最后得到的结果类似（000,001,004）
//		for(int i=0;i<str.length();i++){
//			char k =str.charAt(i);
//			if(k=='L'){
//				tempstr[c] = str.substring(i+1, i+4);
//				c++;
//			}
//		}
//		 
//		//去除字符串中重复
//		 TreeSet<String> tr = new TreeSet<String>();
//		 for(int i=0;i<c;i++){
//		 System.out.print(tempstr[i]+" ");
//		 tr.add(tempstr[i]);
//		 
//		 }
//		 String[] s2= new String[tr.size()];
//		 for(int i=0;i<s2.length;i++){
//		 s2[i]=tr.pollFirst();//从TreeSet中取出元素重新赋给数组
//		 System.out.print(s2[i]+" ");
//		 }
//		 
//         //排序
//		 for(int i=0;i<s2.length;i++){
//			 for(int j=i;j<s2.length;j++){
//				 if(Integer.parseInt(s2[j])<Integer.parseInt(s2[i]))
//					 s2[i] = s2[j];
//			 }
//		 }
//		 String succeedstr[]= new String[100];
//		 String failedstr[]= new String[100];
//		 int succeedcnt=0,failedcnt=0,tempcnt=0;
//		 for(int i=0;i<=Integer.parseInt(s2[s2.length-1]);i++){
//			if(Integer.parseInt(s2[s2.length-1])-Integer.parseInt(s2[0])+1 == s2.length){
//				failedcnt = 0;
//			}
//			else{
//				if(i != Integer.parseInt(s2[tempcnt]) ){
//					failedstr[failedcnt] = i+"";
//					failedcnt++;
//				}
//				else{
//					tempcnt++;
//					succeedstr[succeedcnt] = i+"";
//					succeedcnt++;
//				}
//			}
//		 }
//		 //返回设置的结果
//		 waiting.dismiss();
//		 timesetting_state.setText("");
//		 timesetting_state.append("共设置了"+s2.length+"个路灯设备\n");
//		 timesetting_state.append("其中最大编号为:L"+String.format("%03d", Integer.parseInt(s2[s2.length-1]))+"\n");
//		 timesetting_state.append("最小编号为:L"+String.format("%03d", Integer.parseInt(s2[0]))+"\n");
//			 
//		 if(succeedcnt<failedcnt){
//			 for(int t=0;t<succeedcnt;t++)
//				 timesetting_state.append("L"+String.format("%03d", Integer.parseInt(succeedstr[t]))+" ");
//			 timesetting_state.append("设置成功,其余设置失败\n");
//		 }
//		 else {
//			 if(failedcnt == 0){
//				 timesetting_state.append("全部设置成功！\n");
//			 }
//			 else{
//				 for(int t=0;t<failedcnt;t++)
//					 timesetting_state.append("L"+String.format("%03d", Integer.parseInt(failedstr[t]))+" ");
//				 timesetting_state.append("设置失败,其余设置成功\n");
//			 }
//		 }
//	 }
	
	
}
