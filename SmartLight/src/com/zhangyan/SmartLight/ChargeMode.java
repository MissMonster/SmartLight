package com.zhangyan.SmartLight;

 

import com.zhangyan.SmartLight.R;

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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
 
public class ChargeMode extends Fragment {
	private Button chargemode_confirm;
	private EditText lightnum_chargemode;
    int retry =0 ,retryMax = 0;
    private TextView chargemode_receive,seekbar_value;
    private SeekBar seekbar;
    private CustomProgressDialog waiting;
    
    
    
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
	return inflater.inflate(R.layout.chargemode, null);
	
	
	}

	
	public void onActivityCreated(Bundle savedInstanceState) {  
	    super.onActivityCreated(savedInstanceState);  
	    chargemode_confirm =  (Button) getView().findViewById(R.id.chargemode_confirm) ;
	    ClickListener click =new ClickListener();
	    chargemode_confirm.setOnClickListener(click);
	    lightnum_chargemode =  (EditText) getView().findViewById(R.id.lightnum_chargemode);
	    chargemode_receive =  (TextView) getView().findViewById(R.id.chargemode_receive);
	    seekbar_value =  (TextView) getView().findViewById(R.id.seekbar_value);
	    chargemode_receive.setText("");
	    seekbar = (SeekBar) getView().findViewById(R.id.seekBar1 );
	    OnProgressChange Listenner = new OnProgressChange();
	    seekbar.setOnSeekBarChangeListener(Listenner);
	    seekbar.setKeyProgressIncrement(1);
	}  
	
	
	
	
	class OnProgressChange implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			seekbar_value.setText(progress+"V");
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
	
	
	//定义按钮的监听器
	class ClickListener  implements OnClickListener{
	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			lightnum_chargemode.clearFocus();
			MainActivity Activity = (MainActivity) getActivity();
			if(Activity.msocket==null||!Activity.msocket.isConnected())
				Toast.makeText(getActivity(), "还没有连接到遥控器", Toast.LENGTH_SHORT).show();
			else{
				if(lightnum_chargemode.length()!=3)
					Toast.makeText(getActivity(), "输入的数据不合法，路灯编号请输入三个数字", Toast.LENGTH_SHORT).show();
				else{
					Activity.DataReceived.setText("");
					Activity.OrderNum = 4;
					//Activity.Send("*SCM L"+lightnum_chargemode.getText().toString()+" "+String.format("%02d", Integer.parseInt(chargemode_value.getText().toString())));
					Activity.Send("*SCM L"+lightnum_chargemode.getText().toString()+" "+String.format("%02d", seekbar.getProgress()));
					delay500msThread thread =new delay500msThread();
					thread.start();
					retry = 0;
					retryMax = 16;
					waiting = new CustomProgressDialog(Activity,R.style.WaitingDialog);
					waiting.titlename = "正在设置，请稍候..";
					waiting.setCancelable(false);
					waiting.show();
					chargemode_receive.setText("");
				}
			}
			
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
							    	chargemode_receive.setText("设置成功!");
							    	retry = 0;
							    	Activity.OrderNum = 0;
							    }
							    else{
								    retry++;
								   	delay500msThread timedelay = new delay500msThread();
									timedelay.start();
									if(retry == 5){
										waiting.setMessage(" 进行第2次尝试...");
										Activity.Send("*SCM L"+lightnum_chargemode.getText().toString()+" "+String.format("%02d", seekbar.getProgress()));
									}
									if(retry == 10){
										waiting.setMessage(" 进行第3次尝试...");
										Activity.Send("*SCM L"+lightnum_chargemode.getText().toString()+" "+String.format("%02d", seekbar.getProgress()));
									}
									if(retry == retryMax){
										waiting.dismiss();
										Toast.makeText(getActivity(), "设置失败", Toast.LENGTH_SHORT).show();
										chargemode_receive.setText("设置失败!");
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
	
	 
		
		


}
