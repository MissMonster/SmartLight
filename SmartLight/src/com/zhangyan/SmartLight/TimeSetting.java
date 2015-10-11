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
    		case 0: map.put("1", "ǿ��ʱ��");
    				list.add(map); 
		    		break;
    		case 1: map.put("1", "����ʱ��");
    				list.add(map);
    				break;
    		case 2: map.put("1", "����ʱ��");
					list.add(map); 
					break;
    		case 3: map.put("1", "����ʱ��");
					list.add(map); 
					break;
    		case 4: map.put("1", "׼��ʱ��");
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
			timesetting_lightnum.clearFocus(); 
			MainActivity Activity = (MainActivity) getActivity();
			if(Activity.msocket==null||!Activity.msocket.isConnected())
				Toast.makeText(getActivity(), "��û�����ӵ�ң����", Toast.LENGTH_SHORT).show();
			else{
				if(timesetting_lightnum.length()!=3)
					Toast.makeText(getActivity(), "��������ݲ��Ϸ���·�Ʊ����������������", Toast.LENGTH_SHORT).show();
				else{
					if(TimeSelected=="ǿ��ʱ��"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SST L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "��������ǿ��ʱ�������Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						timesetting_state .setText("");
					}
					else if(TimeSelected=="����ʱ��"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SHT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "�������ð���ʱ�������Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						timesetting_state .setText("");
					}
					else if(TimeSelected=="����ʱ��"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SWT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "������������ʱ�������Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						timesetting_state .setText("");
					}
					else if(TimeSelected=="����ʱ��"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SDT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "������������ʱ�������Ժ�..";
						waiting.setCancelable(false);
						waiting.show();
						timesetting_state .setText("");
					}
					else if(TimeSelected=="׼��ʱ��"){
						Activity.DataReceived.setText("");
						Activity.OrderNum = 4;
						Activity.Send("*SMC L"+timesetting_lightnum.getText().toString()+" "+String.format("%02d", timesetting_seekbar.getProgress()));
						delay500msThread thread =new delay500msThread();
						thread.start();
						retry = 0;
						retryMax = 16;
						waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
						waiting.titlename = "��������׼��ʱ�������Ժ�..";
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
			
			if(TimeSelected=="ǿ��ʱ��"){
				timesetting_seekbar_value.setText(progress+"����");
			}
			else if(TimeSelected=="����ʱ��"){
				timesetting_seekbar_value.setText(progress+"����");
			}
			else if(TimeSelected=="����ʱ��"){
				timesetting_seekbar_value.setText(progress+"����");
			}
			else if(TimeSelected=="����ʱ��"){
				timesetting_seekbar_value.setText(progress+"����");
			}
			else if(TimeSelected=="׼��ʱ��"){
				timesetting_seekbar_value.setText(progress+"����");
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
			if(TimeSelected=="ǿ��ʱ��"){
				timesetting_seekbar.setMax(720);
				timesetting_seekbar.setProgress(240);
				timesetting_seekbar_value.setText("240����");
				timesetting_seekbar.setKeyProgressIncrement(1);
			}
			else if(TimeSelected=="����ʱ��"){
				timesetting_seekbar.setMax(720);
				timesetting_seekbar.setProgress(90);
				timesetting_seekbar_value.setText("90����");
				timesetting_seekbar.setKeyProgressIncrement(1);
			}
			else if(TimeSelected=="����ʱ��"){
				timesetting_seekbar.setMax(720);
				timesetting_seekbar.setProgress(330);
				timesetting_seekbar_value.setText("330����");
				timesetting_seekbar.setKeyProgressIncrement(1);
			}
			else if(TimeSelected=="����ʱ��"){
				timesetting_seekbar.setMax(240);
				timesetting_seekbar.setProgress(90);
				timesetting_seekbar_value.setText("90����");
				timesetting_seekbar.setKeyProgressIncrement(1);
			}
			else if(TimeSelected=="׼��ʱ��"){
				timesetting_seekbar.setMax(99);
				timesetting_seekbar.setProgress(30);
				timesetting_seekbar_value.setText("30����");
				timesetting_seekbar.setKeyProgressIncrement(1);
			}
//		
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
								timesetting_state.setText("���óɹ�!");
								retry = 0;
								Activity.OrderNum = 0;
							}
							else{
								retry++;
								delay500msThread timedelay = new delay500msThread();
								timedelay.start();
								if(retry == 5){
									if(TimeSelected=="ǿ��ʱ��"){
										Activity.Send("*SST L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="����ʱ��"){
										Activity.Send("*SHT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="����ʱ��"){
										Activity.Send("*SWT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="����ʱ��"){
										Activity.Send("*SDT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="׼��ʱ��"){
										Activity.Send("*SMC L"+timesetting_lightnum.getText().toString()+" "+String.format("%02d", timesetting_seekbar.getProgress()));
									}
									waiting.setMessage(" ���е�2�γ���...");
								}
								if(retry == 10){
									if(TimeSelected=="ǿ��ʱ��"){
										Activity.Send("*SST L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="����ʱ��"){
										Activity.Send("*SHT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="����ʱ��"){
										Activity.Send("*SWT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="����ʱ��"){
										Activity.Send("*SDT L"+timesetting_lightnum.getText().toString()+" "+String.format("%03d", timesetting_seekbar.getProgress()));
									}
									else if(TimeSelected=="׼��ʱ��"){
										Activity.Send("*SMC L"+timesetting_lightnum.getText().toString()+" "+String.format("%02d", timesetting_seekbar.getProgress()));
									}
									waiting.setMessage(" ���е�3�γ��� ...");
								}
								if(retry == retryMax){
									waiting.dismiss();
									Toast.makeText(getActivity(), "����ʧ��", Toast.LENGTH_SHORT).show();
									timesetting_state.setText("����ʧ��!");
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
	
//	 //�õ�·�Ʊ��
//	private void GetlightNum(String str){
//		 int c=0;
//		 String tempstr[]= new String[100];
//		//�õ��ַ�������   ���õ��Ľ�����ƣ�000,001,004��
//		for(int i=0;i<str.length();i++){
//			char k =str.charAt(i);
//			if(k=='L'){
//				tempstr[c] = str.substring(i+1, i+4);
//				c++;
//			}
//		}
//		 
//		//ȥ���ַ������ظ�
//		 TreeSet<String> tr = new TreeSet<String>();
//		 for(int i=0;i<c;i++){
//		 System.out.print(tempstr[i]+" ");
//		 tr.add(tempstr[i]);
//		 
//		 }
//		 String[] s2= new String[tr.size()];
//		 for(int i=0;i<s2.length;i++){
//		 s2[i]=tr.pollFirst();//��TreeSet��ȡ��Ԫ�����¸�������
//		 System.out.print(s2[i]+" ");
//		 }
//		 
//         //����
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
//		 //�������õĽ��
//		 waiting.dismiss();
//		 timesetting_state.setText("");
//		 timesetting_state.append("��������"+s2.length+"��·���豸\n");
//		 timesetting_state.append("���������Ϊ:L"+String.format("%03d", Integer.parseInt(s2[s2.length-1]))+"\n");
//		 timesetting_state.append("��С���Ϊ:L"+String.format("%03d", Integer.parseInt(s2[0]))+"\n");
//			 
//		 if(succeedcnt<failedcnt){
//			 for(int t=0;t<succeedcnt;t++)
//				 timesetting_state.append("L"+String.format("%03d", Integer.parseInt(succeedstr[t]))+" ");
//			 timesetting_state.append("���óɹ�,��������ʧ��\n");
//		 }
//		 else {
//			 if(failedcnt == 0){
//				 timesetting_state.append("ȫ�����óɹ���\n");
//			 }
//			 else{
//				 for(int t=0;t<failedcnt;t++)
//					 timesetting_state.append("L"+String.format("%03d", Integer.parseInt(failedstr[t]))+" ");
//				 timesetting_state.append("����ʧ��,�������óɹ�\n");
//			 }
//		 }
//	 }
	
	
}
