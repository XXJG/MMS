package Winmms.ACT;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import Winmms.MainClass.FRSAKey;
import Winmms.MainClass.UserString;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MMSACT extends Activity
{
    /** Called when the activity is first created. */
	
	private ListView listView;
	private ArrayList<Map<String, Object>> data;
	private Intent intent;
	static public Handler updateActivity = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.mmsmain);
        initACTivity();
        updateActivity  = new Handler()
		{
			 
	            public void handleMessage(Message msg)
	            {
	            	
	            	switch(msg.arg1)
	            	{
	            		case 0:
	            		{
	            			switch(msg.arg2)
	            			{
	            				case 1:
	            				{
	            					MMSACT.this.setTitle("请稍后，正在初始化用户信息中...");
	            				}
	            				break;
	            				case 2:
	            				{
	            					MMSACT.this.setTitle("请稍后，用户信息损坏，正在恢复用户信息中...");
	            				}
	            				break;
	            			}
	            		}
	            		break;
	            		case 1:
	        			{
	        				switch(msg.arg2)
	            			{
	            				case 1:
	            				{
	            					Toast.makeText(MMSACT.this, "恭喜，初始化用户信息成功", Toast.LENGTH_LONG).show();
	            				}
	            				break;
	            				case 2:
	            				{
	            					Toast.makeText(MMSACT.this,  "恭喜，恢复用户信息成功", Toast.LENGTH_LONG).show();
	            				}
	            				break;
	            			}
	        				MMSACT.this.setTitle(R.string.app_name);
	        			}
	        			break;	
	            	}
	            }
	    };
	    checkAndUpdateUserMsg();
    }
	
	private void initACTivity()
	{
		listView = (ListView)MMSACT.this.findViewById(R.id.listview);
    	this.getData();
    
    	SimpleAdapter Adapter = new SimpleAdapter(this, data, R.layout.list_fileselect,
				new String[] { "picture", "title", "content"}, new int[] {
				R.id.listitem_pic, R.id.listitem_title, R.id.listitem_content});
		listView.setAdapter(Adapter);
    
    	listView.setOnItemClickListener(new OnItemClickListener() 
    	{

    		@Override
    		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
    				long arg3) 
    		{
    			// TODO Auto-generated method stub
    			if(data.get(arg2).get("title") == MMSACT.this.getString(R.string.new_message))
    			{
    				intent = new Intent();
    				intent.setClass(MMSACT.this,NewMsgACT.class);
    				MMSACT.this.startActivity(intent);
				
    				//System.out.println(data.get(arg2) + "==" + MMSACT.this.getString(R.string.new_message));
    			}
			
    			else if(data.get(arg2).get("title") == MMSACT.this.getString(R.string.receive_box))
    			{
    				intent = new Intent();
    				intent.setClass(MMSACT.this,ReceiverBoxACT.class);
    				MMSACT.this.startActivity(intent);
				
				//System.out.println(data.get(arg2) + "==" + MMSACT.this.getString(R.string.receive_box));
    			}
			
    			else if(data.get(arg2).get("title") == MMSACT.this.getString(R.string.send_box))
    			{
    				intent = new Intent();
    				intent.setClass(MMSACT.this,SendBoxACT.class);
    				MMSACT.this.startActivity(intent);
				
				//System.out.println(data.get(arg2) + "==" + MMSACT.this.getString(R.string.send_box));
    			}
    		} 
    	});
    	
    	registerReceiver();
	}
	//向listView导入数据
	 private ArrayList<Map<String, Object>> getData()
	 {
		    data = new ArrayList<Map<String, Object>>(); 
	        HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("picture", R.drawable.receive);
			map.put("title", this.getString(R.string.receive_box));
			map.put("content", this.getString(R.string.receive_box_exption));
			data.add(map);
			
			HashMap<String, Object> map1 = new HashMap<String, Object>();
			map1.put("picture", R.drawable.sent);
			map1.put("title", this.getString(R.string.send_box));
			map1.put("content", this.getString(R.string.send_box_exption));
			data.add(map1);
			
			HashMap<String, Object> map2 = new HashMap<String, Object>();
			map2.put("picture", R.drawable.sms);
			map2.put("title", this.getString(R.string.new_message));
			//int[] date = UserString.currentDate();
			//map2.put("content", this.getString(R.string.new_message_exption) + date[0] +"," + date[1] +"," + date[2] +"," + date[3] +","+ date[4]);
			map2.put("content", this.getString(R.string.new_message_exption));
			data.add(map2);
	         
	        return data;
	 }
	private void checkAndUpdateUserMsg()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				Looper.prepare();
				
				int i = 0;
				 final Calendar d1 = Calendar.getInstance();
				if ( (!UserString.fileIsExists(UserString.headpath + "mine/privateKeythis.dat") ||
			        		!UserString.fileIsExists(UserString.headpath + "mine/publicKeythis.dat")) &&
			        		(!UserString.fileIsExists(UserString.headpath + "remine/privateKeythis.dat") ||
			        		!UserString.fileIsExists(UserString.headpath + "remine/publicKeythis.dat")))//生成公私玥
			    {
					Message toupdate = MMSACT.updateActivity .obtainMessage();
					toupdate.arg1 = 0;
					toupdate.arg2 = 1;
					i = 1;
					MMSACT.updateActivity .sendMessage(toupdate);
					 
			        FRSAKey createkey = new FRSAKey();
			        createkey.createRSAKey(UserString.thistelephone, UserString.headpath + "mine/");
			    }
				else if ( ( !UserString.fileIsExists(UserString.headpath + "mine/privateKeythis.dat") ||
			        		!UserString.fileIsExists(UserString.headpath + "mine/publicKeythis.dat") ) &&
			        		( UserString.fileIsExists(UserString.headpath + "remine/privateKeythis.dat") &&
			        		UserString.fileIsExists(UserString.headpath + "remine/publicKeythis.dat") ))//恢复公私玥
			    {
					 Message toupdate = MMSACT.updateActivity .obtainMessage();
					 toupdate.arg1 = 0;
					 toupdate.arg2 = 2;
					 i = 2;
					 MMSACT.updateActivity .sendMessage(toupdate);
			         byte[] privateKeyEncode = FRSAKey.getPrivateKeyEncode(UserString.thistelephone, UserString.headpath + "remine/");
			         byte[] publicKeyEncode = FRSAKey.getPublicKeyEncode(UserString.thistelephone, UserString.headpath + "remine/");
			        	
					 try
					 {
							FileOutputStream outputprivatefile = new FileOutputStream( UserString.headpath + "mine/privateKeythis.dat" ); 
							FileOutputStream outputpublicfile = new FileOutputStream( UserString.headpath + "mine/publicKeythis.dat" );
						
							outputprivatefile.write(privateKeyEncode);
							outputpublicfile.write(publicKeyEncode);
							outputprivatefile.close();
							outputpublicfile.close();
					  }
					  catch (Exception ex)
				      {
							System.out.println("00:" + ex.toString());
					  }
			    }
				else if ( ( !UserString.fileIsExists(UserString.headpath + "remine/privateKeythis.dat") ||
		        		!UserString.fileIsExists(UserString.headpath + "remine/publicKeythis.dat") ) &&
		        		( UserString.fileIsExists(UserString.headpath + "mine/privateKeythis.dat") &&
		        		UserString.fileIsExists(UserString.headpath + "mine/publicKeythis.dat") ))//恢复公私玥
				{
					Message toupdate = MMSACT.updateActivity .obtainMessage();
					toupdate.arg1 = 0;
					toupdate.arg2 = 2;
					i = 2;
					MMSACT.updateActivity .sendMessage(toupdate);
					byte[] privateKeyEncode = FRSAKey.getPrivateKeyEncode(UserString.thistelephone, UserString.headpath + "mine/");
					byte[] publicKeyEncode = FRSAKey.getPublicKeyEncode(UserString.thistelephone, UserString.headpath + "mine/");
		        	
					try
					{
						FileOutputStream outputprivatefile = new FileOutputStream( UserString.headpath + "remine/privateKeythis.dat" ); 
						FileOutputStream outputpublicfile = new FileOutputStream( UserString.headpath + "remine/publicKeythis.dat" );
					
						outputprivatefile.write(privateKeyEncode);
						outputpublicfile.write(publicKeyEncode);
						outputprivatefile.close();
						outputpublicfile.close();
					}
					catch (Exception ex)
					{
						System.out.println("01:" + ex.toString());
					}
				}
				else if ( ( !UserString.fileIsExists(UserString.headpath + "remine/privateKeythis.dat") &&
		        		UserString.fileIsExists(UserString.headpath + "remine/publicKeythis.dat") ) &&
		        		( UserString.fileIsExists(UserString.headpath + "mine/privateKeythis.dat") &&
		        		!UserString.fileIsExists(UserString.headpath + "mine/publicKeythis.dat") ))//恢复公私玥
				{
					Message toupdate = MMSACT.updateActivity .obtainMessage();
					toupdate.arg1 = 0;
					toupdate.arg2 = 2;
					i = 2;
					MMSACT.updateActivity .sendMessage(toupdate);
					byte[] privateKeyEncode = FRSAKey.getPrivateKeyEncode(UserString.thistelephone, UserString.headpath + "mine/");
					byte[] publicKeyEncode = FRSAKey.getPublicKeyEncode(UserString.thistelephone, UserString.headpath + "remine/");
		        	
					try
					{
						FileOutputStream outputprivatefile = new FileOutputStream( UserString.headpath + "remine/privateKeythis.dat" ); 
						FileOutputStream outputpublicfile = new FileOutputStream( UserString.headpath + "mine/publicKeythis.dat" );
					
						outputprivatefile.write(privateKeyEncode);
						outputpublicfile.write(publicKeyEncode);
						outputprivatefile.close();
						outputpublicfile.close();
					}
					catch (Exception ex)
					{
						System.out.println("02:" + ex.toString());
					}
				}
					else if ( ( !UserString.fileIsExists(UserString.headpath + "mine/privateKeythis.dat") &&
			        		UserString.fileIsExists(UserString.headpath + "mine/publicKeythis.dat") ) &&
			        		( UserString.fileIsExists(UserString.headpath + "remine/privateKeythis.dat") &&
			        		!UserString.fileIsExists(UserString.headpath + "remine/publicKeythis.dat") ))//恢复公私玥
					{
						Message toupdate = MMSACT.updateActivity .obtainMessage();
						toupdate.arg1 = 0;
						toupdate.arg2 = 2;
						i = 2;
						MMSACT.updateActivity .sendMessage(toupdate);
						byte[] privateKeyEncode = FRSAKey.getPrivateKeyEncode(UserString.thistelephone, UserString.headpath + "remine/");
						byte[] publicKeyEncode = FRSAKey.getPublicKeyEncode(UserString.thistelephone, UserString.headpath + "mine/");
			        	
						try
						{
							FileOutputStream outputprivatefile = new FileOutputStream( UserString.headpath + "mine/privateKeythis.dat" ); 
							FileOutputStream outputpublicfile = new FileOutputStream( UserString.headpath + "remine/publicKeythis.dat" );
						
							outputprivatefile.write(privateKeyEncode);
							outputpublicfile.write(publicKeyEncode);
							outputprivatefile.close();
							outputpublicfile.close();
						}
						catch (Exception ex)
						{
							System.out.println("03:" + ex.toString());
						}
				}
				final Calendar d2 = Calendar.getInstance();
				long t = d2.getTime().getTime() - d1.getTime().getTime();
				System.out.println("\nMMM seconds:" + t);
				Message toupdate = MMSACT.updateActivity .obtainMessage();
				toupdate.arg1 = 1;
				toupdate.arg2 = i;
				MMSACT.updateActivity .sendMessage(toupdate);
				Looper.loop();
			}
		}).start();
	}
	
	 public void registerReceiver()
	 {
		 /**发送与接收的广播**/  
		 final String SENT_SMS_ACTION = "SENT_SMS_ACTION";  
		 final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION"; 
		
		 
		 BroadcastReceiver sendMessage = new BroadcastReceiver() 
		 {  
			  
			    public void onReceive(Context context, Intent intent) 
			    {  
			        //判断短信是否发送成功  
			        switch (getResultCode()) 
			        {  
			        	case Activity.RESULT_OK:  
			        		Toast.makeText(context, R.string.notice_send_success, Toast.LENGTH_SHORT).show();  
			        		break;  
			        	default:  
			        		Toast.makeText(context,R.string.notice_send_falied, Toast.LENGTH_LONG).show();  
			        		break;  
			        }  
			    }  
		 };  
			      
			     
		BroadcastReceiver receiver = new BroadcastReceiver() 
		{    
				public void onReceive(Context context, Intent intent) 
			    {  
			        //表示对方成功收到短信  
			        Toast.makeText(context,R.string.notice_receiver_success,Toast.LENGTH_LONG).show();  
			    }  
		}; 
		registerReceiver(sendMessage, new IntentFilter(SENT_SMS_ACTION));  
        registerReceiver(receiver, new IntentFilter(DELIVERED_SMS_ACTION));
	 }
}