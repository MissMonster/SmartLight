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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ThresholdVoltage extends Fragment {
	
	private ArrayList<HashMap<String,String>> list;
	private Spinner myspinner;
	private SimpleAdapter adapter;
	private Button confirmButton;
	private EditText lightnum_thresholdvoltage;
	private CustomProgressDialog waiting;
	private TextView thresholdvoltage_state,thresholdvoltage_seekbar_value;
	private SeekBar thresholdvoltage_seekbar;
	int retry,retryMax;
	String ThresholdSelected;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.thresholdvoltage, null);
	
	}
	
	
	public void onActivityCreated(Bundle savedInstanceState) {  
	    super.onActivityCreated(savedInstanceState);
	    confirmButton = (Button) getView().findViewById(R.id.thresholdvoltage_confirm);
	    lightnum_thresholdvoltage = (EditText) getView().findViewById(R.id.lightnum_thresholdvoltage);
	    thresholdvoltage_state = (TextView) getView().findViewById(R.id.thresholdvoltage_state);
	    thresholdvoltage_seekbar = (SeekBar) getView().findViewById(R.id.thresholdvoltage_seekbar);
	    thresholdvoltage_seekbar_value = (TextView) getView().findViewById(R.id.thresholdvoltage_seekbar_value);
	    list = new ArrayList<HashMap<String,String>>();
	    myspinner = (Spinner) getView().findViewById(R.id.spinner1);
	    adapter = new SimpleAdapter(getActivity(), list, R.layout.thresholdvoltageview, new String[]{"1"}, new int[]{R.id.threshold_item});
	    ClickListener listener = new ClickListener();
	    confirmButton.setOnClickListener(listener);
	    OnitemSelected listener2 = new OnitemSelected();
	    myspinner.setOnItemSelectedListener(listener2);
	    OnProgressChange listener3 = new OnProgressChange();
	    thresholdvoltage_seekbar.setOnSeekBarChangeListener(listener3);
	    ListThresholdVoltage();
	    myspinner.setAdapter(adapter);
	} 
	
	public void ListThresholdVoltage(){
		
		
		for(int i=0;i<6;i++){
    		HashMap<String,String> map=new HashMap<String, String>();
    		switch(i){
    		case 0: map.put("1", "夜晚阈值");
    				list.add(map); 
		    		break;
    		case 1: map.put("1", "滞回阈值");
    				list.add(map);
    				break;
    		case 2: map.put("1", "节能起始阈值");
					list.add(map);
					break;
    		case 3: map.put("1", "节能终止阈值");
					list.add(map);
					break;
    		case 4: map.put("1", "超容充满阈值");
					list.add(map); 
					break;
    		case 5: map.put("1", "超容放电关断阈值");
					list.add(map); 
					break;    		
    		}
    			
    	}
	}
	
	
	//定义按钮的监听器
	class ClickListener  implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			lightnum_thresholdvoltage.clearFocus();
			MainActivity Activity = (MainActivity) getActivity();
			if(Activity.msocket==null||!Activity.msocket.isConnected())
				Toast.makeText(getActivity(), "还没有连接到遥控器", Toast.LENGTH_SHORT).show();
			else{
				if(lightnum_thresholdvoltage.length()!=3)
					Toast.makeText(getActivity(), "输入的数据不合法，路灯编号请输入三个数字", Toast.LENGTH_SHORT).show();
				else{
					if(ThresholdSelected=="夜晚阈值"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*STN L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "正在设置，请稍候..";
						waiting.setCancelable(false);
						waiting.show();
						thresholdvoltage_state .setText("");
					}
					else if(ThresholdSelected=="滞回阈值"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*STV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "正在设置，请稍候..";
						waiting.setCancelable(false);
						waiting.show();
						thresholdvoltage_state .setText("");
					}
					else if(ThresholdSelected=="节能起始阈值"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SSV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "正在设置，请稍候..";
						waiting.setCancelable(false);
						waiting.show();
						thresholdvoltage_state .setText("");
					}
					else if(ThresholdSelected=="节能终止阈值"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SLV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "正在设置，请稍候..";
						waiting.setCancelable(false);
						waiting.show();
						thresholdvoltage_state .setText("");
					}
					else if(ThresholdSelected=="超容充满阈值"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SSE L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "正在设置，请稍候..";
						waiting.setCancelable(false);
						waiting.show();
						thresholdvoltage_state .setText("");
					}
					
					else if(ThresholdSelected=="超容放电关断阈值"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SDC L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "正在设置，请稍候..";
						waiting.setCancelable(false);
						waiting.show();
						thresholdvoltage_state .setText("");
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
			double p = progress*1D/10;
			
			if(ThresholdSelected=="夜晚阈值"){
				thresholdvoltage_seekbar_value.setText(progress+"V");
			}
			else if(ThresholdSelected=="滞回阈值"){
				thresholdvoltage_seekbar_value.setText(p+"V");
			}
			else if(ThresholdSelected=="节能起始阈值"){
				thresholdvoltage_seekbar_value.setText(p+"V");
			}
			else if(ThresholdSelected=="节能终止阈值"){
				thresholdvoltage_seekbar_value.setText(p+"V");
			}
			else if(ThresholdSelected=="超容充满阈值"){
				thresholdvoltage_seekbar_value.setText(p+"V");
			}
			else if(ThresholdSelected=="超容放电关断阈值"){
				thresholdvoltage_seekbar_value.setText(p+"V");
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
			ThresholdSelected = tv.getText().toString();
			if(ThresholdSelected=="夜晚阈值"){
				thresholdvoltage_seekbar.setMax(20);
				thresholdvoltage_seekbar.setProgress(4);
				thresholdvoltage_seekbar_value.setText("4V");
				thresholdvoltage_seekbar.setKeyProgressIncrement(1);
			}
			else if(ThresholdSelected=="滞回阈值"){
				thresholdvoltage_seekbar.setMax(20);
				thresholdvoltage_seekbar.setProgress(10);
				thresholdvoltage_seekbar_value.setText("1.0V");
				thresholdvoltage_seekbar.setKeyProgressIncrement(1);
			}
			else if(ThresholdSelected=="节能起始阈值"){
				thresholdvoltage_seekbar.setMax(500);
				thresholdvoltage_seekbar.setProgress(250);
				thresholdvoltage_seekbar_value.setText("25.0V");
				thresholdvoltage_seekbar.setKeyProgressIncrement(1);
			}
			else if(ThresholdSelected=="节能终止阈值"){
				thresholdvoltage_seekbar.setMax(500);
				thresholdvoltage_seekbar.setProgress(250);
				thresholdvoltage_seekbar_value.setText("25.0V");
				thresholdvoltage_seekbar.setKeyProgressIncrement(1);
			}
			else if(ThresholdSelected=="超容充满阈值"){
				thresholdvoltage_seekbar.setMax(500);
				thresholdvoltage_seekbar.setProgress(250);
				thresholdvoltage_seekbar_value.setText("25.0V");
				thresholdvoltage_seekbar.setKeyProgressIncrement(1);
			}
			else if(ThresholdSelected=="超容放电关断阈值"){
				thresholdvoltage_seekbar.setMax(500);
				thresholdvoltage_seekbar.setProgress(250);
				thresholdvoltage_seekbar_value.setText("25.0V");
				thresholdvoltage_seekbar.setKeyProgressIncrement(1);
			}	
		
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
								thresholdvoltage_state.setText("设置成功!");
								retry = 0;
								Activity.OrderNum = 0;
							}
							else{
								retry++;
								delay500msThread timedelay = new delay500msThread();
								timedelay.start();
								if(retry == 5){
									if(ThresholdSelected=="夜晚阈值"){
										Activity.Send("*STN L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="滞回阈值"){
										Activity.Send("*STV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="节能起始阈值"){
										Activity.Send("*SSV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="节能终止阈值"){
										Activity.Send("*SHV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="超容充满阈值"){
										Activity.Send("*SSE L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="超容放电关断阈值"){
										Activity.Send("*SDC L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									waiting.setMessage(" 进行第2次尝试 ...");
								}
								if(retry == 10){
									if(ThresholdSelected=="夜晚阈值"){
										Activity.Send("*STN L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="滞回阈值"){
										Activity.Send("*STV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="节能起始阈值"){
										Activity.Send("*SSV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="节能终止阈值"){
										Activity.Send("*SHV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="超容充满阈值"){
										Activity.Send("*SSE L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="超容放电关断阈值"){
										Activity.Send("*SDC L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									waiting.setMessage(" 进行第3次尝试 ...");
								}
								if(retry == retryMax){
									waiting.dismiss();
									Toast.makeText(getActivity(), "设置失败", Toast.LENGTH_SHORT).show();
									thresholdvoltage_state.setText("设置失败!");
									Activity.OrderNum = 0;
									retry = 0;
								}
							}
							break;
						}
				break;
			}
		}
	};
	
	
}

