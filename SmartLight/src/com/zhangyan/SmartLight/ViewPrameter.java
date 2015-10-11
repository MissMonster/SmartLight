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
					Toast.makeText(getActivity(), "还没有连接到遥控器", Toast.LENGTH_SHORT).show();
				else{
						if(viewparameter_lightnum.getText().length()!=3){
							Toast.makeText(getActivity(), "请输入正确的路灯编号，应为三个数字", Toast.LENGTH_SHORT).show();
							viewparameter_state.setText("");
						}
						else{
							Activity.DataReceived.setText("");
							Activity.Send("*VPM L"+viewparameter_lightnum.getText());
							waiting = new CustomProgressDialog(getActivity(),R.style.WaitingDialog);
							waiting.titlename = "正在查询，请稍候..";
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
					 //格式如下 #00#00#00#000#000#000#000#00#00#00#000#000#000#000#
					  String str = Activity.DataReceived.getText().toString().replaceAll("\n",""); 
					  if(str.contains("Remoter Receive:")){
						  retry = 0;
						  retryMax = 0;
						  //以下程序用来获取参数
						  String [] tempParameter;
						  if(str.length() < 50)
			    			return;
						  else{
							  tempParameter = str.split("#");   //保存参数的数组
						  }
						  HandleParameter(tempParameter);
						  tempParameter[10] = Double.parseDouble(tempParameter[10])/10+"";   //滞回电压
						  tempParameter[11] = Double.parseDouble(tempParameter[11])/10+"";   //节能起始电压
						  tempParameter[12] = Double.parseDouble(tempParameter[12])/10+"";   //节能终止电压
						  tempParameter[13] = Double.parseDouble(tempParameter[13])/10+"";   //超容充满电压
						  tempParameter[14] = Double.parseDouble(tempParameter[14])/10+"";   //超容放电关闭阈值电压
						  waiting.dismiss();
						  viewparameter_arraylist.clear();
				    	  for(int i=0;i<14;i++){
				    		  HashMap<String, Object> map = new HashMap<String, Object>();
				    		  switch(i){
				    		  	case 0 : 
					    			  	map.put("1", "强亮强度：");               
							    		map.put("2",tempParameter[1]+" %");
							    		viewparameter_arraylist.add(map);
							    		break;
				    		  	case 1 : 
					    			  	map.put("1", "半亮强度：");
					    			    map.put("2",tempParameter[2]+" %");
					    			    viewparameter_arraylist.add(map);
					    			    break;
				    		  	case 2 : 
					    			  	map.put("1", "弱亮强度:");
							    		map.put("2",tempParameter[3]+" %");
							    		viewparameter_arraylist.add(map);
								    	break;
					    		case 3 : 
						    			map.put("1", "强亮时间：");
								    	map.put("2",Integer.parseInt(tempParameter[4])+" M");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 4 : 
						    			map.put("1", "半亮时间：");
							    		map.put("2",Integer.parseInt(tempParameter[5])+" M");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 5 : 
						    			map.put("1", "弱亮时间：");
							    		map.put("2",Integer.parseInt(tempParameter[6])+" M");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 6 : 
						    			map.put("1", "黎明时间：");
							    		map.put("2",Integer.parseInt(tempParameter[7])+" M");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 7 : 
						    			map.put("1", "准备时间：");
							    		map.put("2",Integer.parseInt(tempParameter[8])+" M");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 8 : 
						    			map.put("1", "夜晚阈值电压：");
							    		map.put("2",tempParameter[9]+" V");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 9 : 
						    			map.put("1", "滞回电压");
							    		map.put("2",tempParameter[10]+" V");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 10 : 
						    			map.put("1", "节能起始电压");
							    		map.put("2",tempParameter[11]+" V");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 11 : 
						    			map.put("1", "节能终止电压");
							    		map.put("2",tempParameter[12]+" V");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 12 : 
						    			map.put("1", "超容充满阈值电压：");
							    		map.put("2",tempParameter[13]+" V");
							    		viewparameter_arraylist.add(map);
							    		break;
					    		case 13 : 
					    			 	map.put("1", "超容放电关闭阈值电压：");
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
								waiting.setMessage(" 进行第2次尝试。。");
								Activity.Send("*VPM L"+viewparameter_lightnum.getText());
							}
							if(retry == 16){
								waiting.setMessage(" 进行第3次尝试。。");
								Activity.Send("*VPM L"+viewparameter_lightnum.getText());
							}
							if(retry == retryMax){
								viewparameter_listview.setVisibility(View.GONE);
								viewparameter_state.setTextColor(Color.rgb(255, 0, 0));
  							    viewparameter_state.setText("查看参数失败，可能是该编号路灯不在网络中！");
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
					   if(retry < retryMax)
				           mhandler.sendMessage(msg); 
					 }            
			} 
			
			
			
			
			public void HandleParameter(String [] str){                //判断参数是否为空  
				for(int j=0;j<str.length;j++){
					if(str[j].contains("255")){
						str[j]="null";
					}
				}
			}
		
		
		
		
	
	}
