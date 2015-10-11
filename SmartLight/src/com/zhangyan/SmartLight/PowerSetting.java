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


public class PowerSetting extends Fragment{

	private ArrayList<HashMap<String,String>> list;
	private Spinner myspinner;
	private SimpleAdapter adapter;
	private Button confirmButton;
	private EditText powersetting_lightnum;
	private CustomProgressDialog waiting;
	private TextView powersetting_state,powersetting_seekbar_value;
	private SeekBar powersetting_seekbar;
	int retry,retryMax;
	String TimeSelected;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.powersetting, null);
	
	}
	
	
	public void onActivityCreated(Bundle savedInstanceState) {  
	    super.onActivityCreated(savedInstanceState);
	    confirmButton = (Button) getView().findViewById(R.id.powersetting_confirm);
	    powersetting_lightnum = (EditText) getView().findViewById(R.id.powersetting_lightnum);
	    powersetting_state = (TextView) getView().findViewById(R.id.powersetting_state);
	    powersetting_seekbar = (SeekBar) getView().findViewById(R.id.powersetting_seekbar);
	    powersetting_seekbar_value = (TextView) getView().findViewById(R.id.powersetting_seekbar_value);
	    list = new ArrayList<HashMap<String,String>>();
	    myspinner = (Spinner) getView().findViewById(R.id.powersetting_spinner);
	    adapter = new SimpleAdapter(getActivity(), list, R.layout.thresholdvoltageview, new String[]{"1"}, new int[]{R.id.threshold_item});
	    ClickListener listener = new ClickListener();
	    confirmButton.setOnClickListener(listener);
	    OnitemSelected listener2 = new OnitemSelected();
	    myspinner.setOnItemSelectedListener(listener2);
	    OnProgressChange listener3 = new OnProgressChange();
	    powersetting_seekbar.setOnSeekBarChangeListener(listener3);
	    ListTime();
	    myspinner.setAdapter(adapter);
	} 
	
	public void ListTime(){
		
		
		for(int i=0;i<3;i++){
    		HashMap<String,String> map=new HashMap<String, String>();
    		switch(i){
    		case 0: map.put("1", "ǿ��ǿ��");
    				list.add(map); 
		    		break;
    		case 1: map.put("1", "����ǿ��");
    				list.add(map);
    				break;
    		case 2: map.put("1", "����ǿ��");
					list.add(map); 
					break;
    		default :
    				break;
    		
    		}
    			
    	}
	}
	
	
	//���尴ť�ļ�����
	class ClickListener  implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			powersetting_lightnum.clearFocus(); 
			MainActivity Activity = (MainActivity) getActivity();
			if(Activity.msocket==null||!Activity.msocket.isConnected())
				Toast.makeText(getActivity(), "��û�����ӵ�ң����", Toast.LENGTH_SHORT).show();
			else{
				if(powersetting_lightnum.length()!=3)
					Toast.makeText(getActivity(), "��������ݲ��Ϸ���·�Ʊ����������������", Toast.LENGTH_SHORT).show();
				else{
					if(TimeSelected=="ǿ��ǿ��"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SSP L"+powersetting_lightnum.getText().toString()+" "+String.format("%02d", powersetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "��������ǿ��ǿ�ȣ����Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						powersetting_state .setText("");
					}
					else if(TimeSelected=="����ǿ��"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SHP L"+powersetting_lightnum.getText().toString()+" "+String.format("%02d", powersetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "�������ð���ǿ�ȣ����Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						powersetting_state .setText("");
					}
					else if(TimeSelected=="����ǿ��"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SWP L"+powersetting_lightnum.getText().toString()+" "+String.format("%02d", powersetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "������������ǿ�ȣ����Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						powersetting_state .setText("");
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
			
			if(TimeSelected=="ǿ��ǿ��"){
				powersetting_seekbar_value.setText(progress+"%");
			}
			else if(TimeSelected=="����ǿ��"){
				powersetting_seekbar_value.setText(progress+"%");
			}
			else if(TimeSelected=="����ǿ��"){
				powersetting_seekbar_value.setText(progress+"%");
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
			if(TimeSelected=="ǿ��ǿ��"){
				powersetting_seekbar.setMax(100);
				powersetting_seekbar.setProgress(70);
				powersetting_seekbar_value.setText("70%");
				powersetting_seekbar.setKeyProgressIncrement(1);  //����seekbar ÿ�ε���ƶ�����   ���������  ��ֵ�����maxֵ�õ�
			}
			else if(TimeSelected=="����ǿ��"){
				powersetting_seekbar.setMax(100);
				powersetting_seekbar.setProgress(40);
				powersetting_seekbar_value.setText("40%");
				powersetting_seekbar.setKeyProgressIncrement(1);
			}
			else if(TimeSelected=="����ǿ��"){
				powersetting_seekbar.setMax(100);
				powersetting_seekbar.setProgress(10);
				powersetting_seekbar_value.setText("10%");
				powersetting_seekbar.setKeyProgressIncrement(1);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
	
	
	
			
			//��ʱ500ms�߳�(���ڼ��ָ���Ƿ����óɹ���
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
					
	//Handler������Ϣ
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
								Toast.makeText(getActivity(), "���óɹ�", Toast.LENGTH_SHORT).show();
								powersetting_state.setText("���óɹ�!");
								retry = 0;
								Activity.OrderNum = 0;
							}
							else{
								retry++;
								delay500msThread timedelay = new delay500msThread();
								timedelay.start();
								if(retry == 5){
									if(TimeSelected=="ǿ��ǿ��"){
										Activity.Send("*SSP L"+powersetting_lightnum.getText().toString()+" "+String.format("%02d", powersetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="����ǿ��"){
										Activity.Send("*SHP L"+powersetting_lightnum.getText().toString()+" "+String.format("%02d", powersetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="����ǿ��"){
										Activity.Send("*STP L"+powersetting_lightnum.getText().toString()+" "+String.format("%03d", powersetting_seekbar.getProgress()));
									}
									waiting.setMessage(" ���е�2�γ��� ...");
								}
								if(retry == 10){
									if(TimeSelected=="ǿ��ǿ��"){
										Activity.Send("*SSP L"+powersetting_lightnum.getText().toString()+" "+String.format("%02d", powersetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="����ǿ��"){
										Activity.Send("*SHP L"+powersetting_lightnum.getText().toString()+" "+String.format("%02d", powersetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="����ǿ��"){
										Activity.Send("*STP L"+powersetting_lightnum.getText().toString()+" "+String.format("%03d", powersetting_seekbar.getProgress()));
									}
									waiting.setMessage(" ���е�3�γ��� ...");
								}
								if(retry == retryMax){
									waiting.dismiss();
									Toast.makeText(getActivity(), "����ʧ��", Toast.LENGTH_SHORT).show();
									powersetting_state.setText("����ʧ��!");
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
}
