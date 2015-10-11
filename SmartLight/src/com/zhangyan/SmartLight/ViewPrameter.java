package com.zhangyan.SmartLight;

 


	import java.util.ArrayList;
import java.util.HashMap;
	

import com.zhangyan.SmartLight.R;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
	public class ViewPrameter extends Fragment {
		private ListView viewparameter_listview;
		private Button viewparameter_confirm;
		private EditText viewparameter_lightnum;
		private int retry,retryMax;
		private CustomProgressDialog waiting;
		private SimpleAdapter viewparameter_adapter;
		ArrayList<HashMap<String,Object>> viewparameter_arraylist;
		private TextView viewparameter_state;
	
	
    
    
    
	    
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		return inflater.inflate(R.layout.viewprameter, null);
		
		
		}
	
		
		public void onActivityCreated(Bundle savedInstanceState) {  
		    super.onActivityCreated(savedInstanceState);  
		    viewparameter_listview = (ListView) getView().findViewById(R.id.viewparameter_listview);
		    viewparameter_lightnum = (EditText) getView().findViewById(R.id.viewparameter_lightnum);
		    viewparameter_confirm = (Button) getView().findViewById(R.id.viewparameter_confirm);
		    viewparameter_state = (TextView) getView().findViewById(R.id.viewparameter_state);
		    ClickListener listener = new ClickListener();
		    viewparameter_confirm.setOnClickListener(listener);
		    viewparameter_arraylist = new ArrayList<HashMap<String,Object>>();
		    viewparameter_adapter = new SimpleAdapter(getActivity(), viewparameter_arraylist, R.layout.viewparameter_listview, new String[]{"1","2"}, new int[]{R.id.parametername,R.id.parametervalue});
		    viewparameter_listview.setAdapter(viewparameter_adapter);
		    viewparameter_listview.setDividerHeight(0);
		    viewparameter_listview.setVisibility(View.GONE);
		}  
		
		
	
		class ClickListener implements OnClickListener{
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity Activity = (MainActivity)getActivity();
				if(Activity.msocket==null||!Activity.msocket.isConnected())
					Toast.makeText(getActivity(), "��û�����ӵ�ң����", Toast.LENGTH_SHORT).show();
				else{
						if(viewparameter_lightnum.getText().length()!=3){
							Toast.makeText(getActivity(), "��������ȷ��·�Ʊ�ţ�ӦΪ��������", Toast.LENGTH_SHORT).show();
							viewparameter_state.setText("");
						}
						else{
							Activity.DataReceived.setText("");
							Activity.Send("*VPM L"+viewparameter_lightnum.getText());
							waiting = new CustomProgressDialog(getActivity(),R.style.WaitingDialog);
							waiting.titlename = "���ڲ�ѯ�����Ժ�..";
							waiting.setCancelable(false);
							waiting.show();
							delay500msThread thread =new delay500msThread();
							thread.start();
							retry = 0;
							retryMax = 20;
							viewparameter_state.setText("");
						}
				}
			
			}
		}
		
		
		Handler mhandler = new Handler(){
			public void handleMessage(Message msg){
				MainActivity Activity = (MainActivity)getActivity();
				switch(msg.what){
				case 1:
					 //��ʽ���� #00#00#00#000#000#000#000#00#00#00#000#000#000#000#
					  String str = Activity.DataReceived.getText().toString().replaceAll("\n",""); 
					  if(str.contains("Remoter Receive:")){
						  retry = 0;
						  retryMax = 0;
						  //���³���������ȡ����
						  String [] tempParameter;
						  if(str.length() < 50)
			    			return;
						  else{
							  tempParameter = str.split("#");   //�������������
						  }
						  HandleParameter(tempParameter);
						  tempParameter[10] = Double.parseDouble(tempParameter[10])/10+"";   //�ͻص�ѹ
						  tempParameter[11] = Double.parseDouble(tempParameter[11])/10+"";   //������ʼ��ѹ
						  tempParameter[12] = Double.parseDouble(tempParameter[12])/10+"";   //������ֹ��ѹ
						  tempParameter[13] = Double.parseDouble(tempParameter[13])/10+"";   //���ݳ�����ѹ
						  tempParameter[14] = Double.parseDouble(tempParameter[14])/10+"";   //���ݷŵ�ر���ֵ��ѹ
						  waiting.dismiss();
						  viewparameter_arraylist.clear();
				    	  for(int i=0;i<14;i++){
				    		  HashMap<String, Object> map = new HashMap<String, Object>();
				    		  switch(i){
				    		  	case 0 : 
					    			  	map.put("1", "ǿ��ǿ�ȣ�");               
							    		map.put("2",tempParameter[1]+" %");
							    		viewparameter_arraylist.add(map);
							    		break;
				    		  	case 1 : 
					    			  	map.put("1", "����ǿ�ȣ�");
					    			    map.put("2",tempParameter[2]+" %");
					    			    viewparameter_arraylist.add(map);
					    			    break;
				    		  	case 2 : 
					    			  	map.put("1", "����ǿ��:");
							    		map.put("2",tempParameter[3]+" %");
							    		viewparameter_arraylist.add(map);
								    	break;
					    		case 3 : 
						    			map.put("1", "ǿ��ʱ�䣺");
								    	map.put("2",Integer.parseInt(tempParameter[4])+" M");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 4 : 
						    			map.put("1", "����ʱ�䣺");
							    		map.put("2",Integer.parseInt(tempParameter[5])+" M");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 5 : 
						    			map.put("1", "����ʱ�䣺");
							    		map.put("2",Integer.parseInt(tempParameter[6])+" M");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 6 : 
						    			map.put("1", "����ʱ�䣺");
							    		map.put("2",Integer.parseInt(tempParameter[7])+" M");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 7 : 
						    			map.put("1", "׼��ʱ�䣺");
							    		map.put("2",Integer.parseInt(tempParameter[8])+" M");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 8 : 
						    			map.put("1", "ҹ����ֵ��ѹ��");
							    		map.put("2",tempParameter[9]+" V");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 9 : 
						    			map.put("1", "�ͻص�ѹ");
							    		map.put("2",tempParameter[10]+" V");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 10 : 
						    			map.put("1", "������ʼ��ѹ");
							    		map.put("2",tempParameter[11]+" V");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 11 : 
						    			map.put("1", "������ֹ��ѹ");
							    		map.put("2",tempParameter[12]+" V");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 12 : 
						    			map.put("1", "���ݳ�����ֵ��ѹ��");
							    		map.put("2",tempParameter[13]+" V");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 13 : 
					    			 	map.put("1", "���ݷŵ�ر���ֵ��ѹ��");
							    		map.put("2",tempParameter[14]+" V");
							    		viewparameter_arraylist.add(map);
							    		break;
							    default:
							    		break;
				    		  }
				    		  viewparameter_listview.setAdapter(viewparameter_adapter);
				    		  viewparameter_listview.setVisibility(View.VISIBLE);
				    		  viewparameter_state.setText("");
					      }
					  }
					  else{
						    retry++;
					    	delay500msThread timedelay = new delay500msThread();
							timedelay.start();
							if(retry == 8){
								waiting.setMessage(" ���е�2�γ��ԡ���");
								Activity.Send("*VPM L"+viewparameter_lightnum.getText());
							}
							if(retry == 16){
								waiting.setMessage(" ���е�3�γ��ԡ���");
								Activity.Send("*VPM L"+viewparameter_lightnum.getText());
							}
							if(retry == retryMax){
								viewparameter_listview.setVisibility(View.GONE);
								viewparameter_state.setTextColor(Color.rgb(255, 0, 0));
  							    viewparameter_state.setText("�鿴����ʧ�ܣ������Ǹñ��·�Ʋ��������У�");
								waiting.dismiss();
								retry = 0;
							}
					  }
					  break;
			   default :
				   		break;
				      
				}
				
			}
		};
		
		
		
		
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
					   if(retry < retryMax)
				           mhandler.sendMessage(msg); 
					 }            
			} 
			
			
			
			
			public void HandleParameter(String [] str){                //�жϲ����Ƿ�Ϊ��  
				for(int j=0;j<str.length;j++){
					if(str[j].contains("255")){
						str[j]="null";
					}
				}
			}
		
		
		
		
	
	}
