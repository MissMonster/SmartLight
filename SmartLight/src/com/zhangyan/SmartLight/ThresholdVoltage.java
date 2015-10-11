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
    		case 0: map.put("1", "ҹ����ֵ");
    				list.add(map); 
		    		break;
    		case 1: map.put("1", "�ͻ���ֵ");
    				list.add(map);
    				break;
    		case 2: map.put("1", "������ʼ��ֵ");
					list.add(map);
					break;
    		case 3: map.put("1", "������ֹ��ֵ");
					list.add(map);
					break;
    		case 4: map.put("1", "���ݳ�����ֵ");
					list.add(map); 
					break;
    		case 5: map.put("1", "���ݷŵ�ض���ֵ");
					list.add(map); 
					break;    		
    		}
    			
    	}
	}
	
	
	//���尴ť�ļ�����
	class ClickListener  implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			lightnum_thresholdvoltage.clearFocus();
			MainActivity Activity = (MainActivity) getActivity();
			if(Activity.msocket==null||!Activity.msocket.isConnected())
				Toast.makeText(getActivity(), "��û�����ӵ�ң����", Toast.LENGTH_SHORT).show();
			else{
				if(lightnum_thresholdvoltage.length()!=3)
					Toast.makeText(getActivity(), "��������ݲ��Ϸ���·�Ʊ����������������", Toast.LENGTH_SHORT).show();
				else{
					if(ThresholdSelected=="ҹ����ֵ"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*STN L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "�������ã����Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						thresholdvoltage_state .setText("");
					}
					else if(ThresholdSelected=="�ͻ���ֵ"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*STV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "�������ã����Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						thresholdvoltage_state .setText("");
					}
					else if(ThresholdSelected=="������ʼ��ֵ"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SSV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "�������ã����Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						thresholdvoltage_state .setText("");
					}
					else if(ThresholdSelected=="������ֹ��ֵ"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SLV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "�������ã����Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						thresholdvoltage_state .setText("");
					}
					else if(ThresholdSelected=="���ݳ�����ֵ"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SSE L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "�������ã����Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						thresholdvoltage_state .setText("");
					}
					
					else if(ThresholdSelected=="���ݷŵ�ض���ֵ"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SDC L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "�������ã����Ժ�..";
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
			
			if(ThresholdSelected=="ҹ����ֵ"){
				thresholdvoltage_seekbar_value.setText(progress+"V");
			}
			else if(ThresholdSelected=="�ͻ���ֵ"){
				thresholdvoltage_seekbar_value.setText(p+"V");
			}
			else if(ThresholdSelected=="������ʼ��ֵ"){
				thresholdvoltage_seekbar_value.setText(p+"V");
			}
			else if(ThresholdSelected=="������ֹ��ֵ"){
				thresholdvoltage_seekbar_value.setText(p+"V");
			}
			else if(ThresholdSelected=="���ݳ�����ֵ"){
				thresholdvoltage_seekbar_value.setText(p+"V");
			}
			else if(ThresholdSelected=="���ݷŵ�ض���ֵ"){
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
			if(ThresholdSelected=="ҹ����ֵ"){
				thresholdvoltage_seekbar.setMax(20);
				thresholdvoltage_seekbar.setProgress(4);
				thresholdvoltage_seekbar_value.setText("4V");
				thresholdvoltage_seekbar.setKeyProgressIncrement(1);
			}
			else if(ThresholdSelected=="�ͻ���ֵ"){
				thresholdvoltage_seekbar.setMax(20);
				thresholdvoltage_seekbar.setProgress(10);
				thresholdvoltage_seekbar_value.setText("1.0V");
				thresholdvoltage_seekbar.setKeyProgressIncrement(1);
			}
			else if(ThresholdSelected=="������ʼ��ֵ"){
				thresholdvoltage_seekbar.setMax(500);
				thresholdvoltage_seekbar.setProgress(250);
				thresholdvoltage_seekbar_value.setText("25.0V");
				thresholdvoltage_seekbar.setKeyProgressIncrement(1);
			}
			else if(ThresholdSelected=="������ֹ��ֵ"){
				thresholdvoltage_seekbar.setMax(500);
				thresholdvoltage_seekbar.setProgress(250);
				thresholdvoltage_seekbar_value.setText("25.0V");
				thresholdvoltage_seekbar.setKeyProgressIncrement(1);
			}
			else if(ThresholdSelected=="���ݳ�����ֵ"){
				thresholdvoltage_seekbar.setMax(500);
				thresholdvoltage_seekbar.setProgress(250);
				thresholdvoltage_seekbar_value.setText("25.0V");
				thresholdvoltage_seekbar.setKeyProgressIncrement(1);
			}
			else if(ThresholdSelected=="���ݷŵ�ض���ֵ"){
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
								thresholdvoltage_state.setText("���óɹ�!");
								retry = 0;
								Activity.OrderNum = 0;
							}
							else{
								retry++;
								delay500msThread timedelay = new delay500msThread();
								timedelay.start();
								if(retry == 5){
									if(ThresholdSelected=="ҹ����ֵ"){
										Activity.Send("*STN L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="�ͻ���ֵ"){
										Activity.Send("*STV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="������ʼ��ֵ"){
										Activity.Send("*SSV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="������ֹ��ֵ"){
										Activity.Send("*SHV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="���ݳ�����ֵ"){
										Activity.Send("*SSE L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="���ݷŵ�ض���ֵ"){
										Activity.Send("*SDC L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									waiting.setMessage(" ���е�2�γ��� ...");
								}
								if(retry == 10){
									if(ThresholdSelected=="ҹ����ֵ"){
										Activity.Send("*STN L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="�ͻ���ֵ"){
										Activity.Send("*STV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%02d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="������ʼ��ֵ"){
										Activity.Send("*SSV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="������ֹ��ֵ"){
										Activity.Send("*SHV L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="���ݳ�����ֵ"){
										Activity.Send("*SSE L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									else if(ThresholdSelected=="���ݷŵ�ض���ֵ"){
										Activity.Send("*SDC L"+lightnum_thresholdvoltage.getText().toString()+" "+String.format("%03d", thresholdvoltage_seekbar.getProgress()));
									}
									waiting.setMessage(" ���е�3�γ��� ...");
								}
								if(retry == retryMax){
									waiting.dismiss();
									Toast.makeText(getActivity(), "����ʧ��", Toast.LENGTH_SHORT).show();
									thresholdvoltage_state.setText("����ʧ��!");
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

