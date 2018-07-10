package Winmms.ACT;

import java.io.FileOutputStream;
import java.util.ArrayList;

import Winmms.MainClass.FRSAKey;
import Winmms.MainClass.MsgEncrypt;
import Winmms.MainClass.UserString;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class ScanMsgACT extends Activity
{
	
	 //发件人电话 
	private EditText from = null;  
	private EditText fromlabel = null;      
	 //日期 
	 private EditText date = null;  
	 
	 //短信内容
	 private EditText mMessage = null;
	 
	 //发送与接收的广播 
	 private String SENT_SMS_ACTION = "SENT_SMS_ACTION";  
	 private String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION"; 
	 
	 public  static  final int ensure_friend = 0;
	 public  static  final int reply = 0;
	 public  static  final int sent_again = 0;
	 private boolean is_ensure_friend = false;
	 public static final int delete = 1;
	 public static final int exit = 2;
	 
	 private String address;
	 private String type;
	 private String _id;
	 private String value;
	 private String phoneNumber;
	 private String message;
		      
	
	public void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanmsg);
		
		from = (EditText)this.findViewById(R.id.from);
		fromlabel = (EditText)this.findViewById(R.id.fromlabel);
		date = (EditText)this.findViewById(R.id.date);
		
		this.setTitle(R.string.scan_message);
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		String showmsg = "";
		
		if (type.equals("receiver"))
		{
			ArrayList<String> msg = intent.getStringArrayListExtra("msg");
			address = UserString.getChinaNumber( msg.get(UserString._address) );
			_id = msg.get(UserString.__id);
			value = msg.get(UserString._body);
			from.setText(address);
			date.setText(msg.get(UserString._date));
		
			
			System.out.println("ScanMsgACT.java: in receiver" );
			if (UserString.isKey(value))
			{
				is_ensure_friend = true;
				if (!UserString.fileIsExists(UserString.headpath + "temp/" + address + ".dat"))
				{
					showmsg = this.getString(R.string.ensure_key);
				}
				else
				{
					showmsg = this.getString(R.string.ensure_key_too);
				}
			}
			else if (UserString.isdecryptf(value))
			{
				String message = value.substring(UserString.headencrypt.length(), value.length());
				System.out.println("ScanMsgACT.java: in decrypt" );
				MsgEncrypt decrypt = new MsgEncrypt();
				try 
				{
					if (UserString.fileIsExists(UserString.headpath + "friends/publicKey" + address + ".dat"))
					{	
						showmsg = decrypt.decrypt(UserString.hexStringToBytes( message ), address, UserString.thistelephone);
					}
				} 
				catch (Exception e) 
				{
					// TODO Auto-generated catch block
					System.out.println("ScanMsgACT.java:"  + e.toString());
					showmsg += "原文：\n" + value + "\nError：\n" + e.toString(); 
				}
			}
			else
			{
					showmsg = this.getString(R.string.secure_notice) + value;
			}
		}
		else if (type.equals("sent"))
		{
			ArrayList<String> msg = intent.getStringArrayListExtra("msg");
			address = UserString.getChinaNumber( msg.get(UserString._address) );
			_id = msg.get(UserString.__id);
			String value = msg.get(UserString._body);
			showmsg = value;
			from.setText(address);
			fromlabel.setText(R.string.to);
			date.setText(msg.get(UserString._date));
		}
		//System.out.println("ScanMsgACT.java: wwwwww" );
		
		mMessage = (EditText)this.findViewById(R.id.inputmsg);
		mMessage.append(showmsg);
		mMessage.setSelection(showmsg.length());
		setEditTextLines(10);
	}	
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{
    	// TODO Auto-generated method stub
		if (type.equals("receiver"))
		{
			if (is_ensure_friend)
			{
				menu.add(0, ensure_friend, ensure_friend, R.string.ensure_friend);
			}
			else
			{
				menu.add(0, reply, reply, R.string.reply);
			}
		}
		else
		{
			menu.add(0, sent_again, sent_again, R.string.send_again);
		}
    	menu.add(0, delete, delete, R.string.delete);
    	menu.add(0, exit, exit, R.string.exit);
    	System.out.println("onmenu");
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// TODO Auto-generated method stub
    	switch(item.getItemId())
    	{
    		case ensure_friend:
    			if (type.equals("receiver") && is_ensure_friend)
			    {
    				
    				//保存 对方公约
    				
    				String message = value.substring(UserString.headkey.length(), value.length());
    				
    				byte[] publickey = UserString.hexStringToBytes( message);
    				try
    				{
    					FileOutputStream publickeyfile = new FileOutputStream(UserString.headpath + "friends/publicKey" + address +".dat"); 
    					publickeyfile.write(publickey);
    					publickeyfile.close();
    					this.getContentResolver().delete( 
        						Uri.parse("content://sms"), "_id=?",new String[]{_id});
    				}
    				catch (Exception e)
    				{
    					System.out.println("ScanMsgACT.java:"  + e.toString());
    				}	
    				if ( !UserString.fileIsExists(UserString.headpath + "temp/" + address + ".dat"))
    				{
    					//发送自己的公约
    					new Thread(new Runnable()
    					{
							
							@Override
							public void run() 
							{
								// TODO Auto-generated method stub
								// ---sends an SMS message to another device---  
		    					SmsManager sms = SmsManager.getDefault();  
				      
		    					// create the sentIntent parameter  
		    					Intent sentIntent = new Intent(SENT_SMS_ACTION);  
		    					PendingIntent sentPI = PendingIntent.getBroadcast(ScanMsgACT.this, 0, sentIntent,  
		    							0);  
				      
		    					// create the deilverIntent parameter  
		    					Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);  
		    					PendingIntent deliverPI = PendingIntent.getBroadcast(ScanMsgACT.this, 0,  
		    							deliverIntent, 0);  
		    					if (UserString.fileIsExists(UserString.headpath + "mine/publicKey" + UserString.thistelephone + ".dat"))
		    					{
		    						//获得  send 公约编码
		    						byte[] bmessage = FRSAKey.getPublicKeyEncode(UserString.thistelephone, UserString.headpath + "mine/");
		    						String lvalue = UserString.bytesToHexString(bmessage);
		        			
		    						//sms.sendTextMessage(phoneNumber, null, value, sentPI, deliverPI);
		        			
		    						System.out.println(UserString.thistelephone + "length: " +  bmessage.length);
		    						System.out.println("length: " +  lvalue.length());
		    						//System.out.println("888888888:" + UserString.isKey(value));
		        			
		    						String[] text = UserString.createSendMessageByLength(lvalue, 138, 'k');
		        			
		    						for (String lmsg : text) 
		    						{
		    							sms.sendTextMessage(address, null, lmsg, sentPI, deliverPI);
		    						}
		    						insertMessageToSend(address,"Send key.");
		    					}
		    					else
		    					{
		    						System.out.println("NewMsgACT.java: doesn't exit send publickey");
		    					}
							}
						}).start();
    				}
    				else
    				{
    					UserString.deleteFile(UserString.headpath + "temp/" + address + ".dat");
    					System.out.println("Delivering public key is successful!");
    				}
    				this.finish();
			    }
    			else if (type.equals("receiver") && !is_ensure_friend)
    			{
    				Intent lintent = new Intent();
    				lintent.putExtra("address", address);
    				lintent.setClass(ScanMsgACT.this, NewMsgACT.class);
    				ScanMsgACT.this.startActivity(lintent);
    			}
    			else
    			{
    				phoneNumber = address;
    				message = UserString.deleteEnter(mMessage.getText().toString());
    				
    				if (!UserString.fileIsExists(UserString.headpath + "friends/publicKey" + phoneNumber + ".dat"))
            		{
        				mMessage.setText(this.getText( R.string.send_key ));
            			setEditTextLines(10);
            		}
    				
    				this.sendSMS();
    			}
    			break;
    		case delete:
    			{
    				this.getContentResolver().delete( 
    						Uri.parse("content://sms"), "_id=?",new String[]{_id}); 
    			}
    			Toast.makeText(this, "Delete success!", Toast.LENGTH_LONG).show();
    			this.finish();
    			break;	
    		case exit:
    			this.finish();
    			break;
    		default:
    			Toast.makeText(this, "Sorry,haven't selected item!",Toast.LENGTH_LONG).show();     				
    	}
    	return super.onOptionsItemSelected(item);
    }
    private void setEditTextLines(int counts)
    {
    	String str = "";
    	for (int i = 0;i < counts; i++)
    	{
    		str += '\n';
    	}
    	 mMessage.append(str);
    }
    
    /**
     * to execute Send message
     * @param phoneNumber
     * @param message
     */
    private void sendSMS() 
    {  
    	new Thread(new Runnable() 
    	{
			
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				
				 // ---sends an SMS message to another device---  
		        SmsManager sms = SmsManager.getDefault();  
		      
		        // create the sentIntent parameter  
		        Intent sentIntent = new Intent(SENT_SMS_ACTION);  
		        PendingIntent sentPI = PendingIntent.getBroadcast(ScanMsgACT.this, 0, sentIntent,  
		            0);  
		      
		        // create the deilverIntent parameter  
		        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);  
		        PendingIntent deliverPI = PendingIntent.getBroadcast(ScanMsgACT.this, 0,  
		            deliverIntent, 0);  
		        
		        if (UserString.fileIsExists(UserString.headpath + "mine/privateKey" + UserString.thistelephone + ".dat"))
		        {
		        	if (UserString.fileIsExists(UserString.headpath + "friends/publicKey" + phoneNumber + ".dat"))
		        	{
		        		System.out.println("send message normal!");
		        		MsgEncrypt msge = new MsgEncrypt();
		        		try 
		        		{
							byte[] bmessage = msge.encrypt(message, UserString.thistelephone, phoneNumber);
							//sms.sendTextMessage(phoneNumber, null, UserString.byteToString(bmessage), sentPI, deliverPI); 
							String[] text = UserString.createSendMessageByLength( 
									UserString.bytesToHexString( bmessage ), UserString.msg_length, 'o' );
		        			//Message.setText(UserString.bytesToHexString( bmessage ).length() + "<>" + UserString.aes);
		        			//setEditTextLines(10);
		        			for (String msg : text) 
		    				{
		    					sms.sendTextMessage(phoneNumber, null, msg, sentPI, deliverPI);
		    				}
		        			insertMessageToSend(phoneNumber, message);
		        		} 
		        		catch (Exception e) 
		        		{
							// TODO Auto-generated catch block
		        			System.out.println("send Error：in encrypting...."  + e.toString());
		        			//NewMsgACT.this.setTitle( "send Error：in encrypting...." );
						} 
		        	}
		        	else
		        	{
		        		if (UserString.fileIsExists(UserString.headpath + "mine/publicKey" + UserString.thistelephone + ".dat"))
		        		{
		        			//获得  send 公约编码
		        			byte[] bmessage = FRSAKey.getPublicKeyEncode(UserString.thistelephone, UserString.headpath + "mine/");
		        			String value = UserString.bytesToHexString(bmessage);
		        			
		        			//sms.sendTextMessage(phoneNumber, null, value, sentPI, deliverPI);
		        			
		        			System.out.println(UserString.thistelephone + "length: " +  bmessage.length);
		        			System.out.println("length: " +  value.length());
		        			//System.out.println("888888888:" + UserString.isKey(value));
		        			//Message.setText(NewMsgACT.this.getText( R.string.send_key ));
		        			//setEditTextLines(10);
		        			
		        			String[] text = UserString.createSendMessageByLength(value, UserString.msg_length, 'k');
		        			
		        			for (String msg : text) 
		    				{
		    					sms.sendTextMessage(phoneNumber, null, msg, sentPI, deliverPI);
		    				}
		        			insertMessageToSend(phoneNumber, "Send key");
		        			if ( UserString.createFile(UserString.headpath + "temp/" + phoneNumber+ ".dat")
		        					== -1 )
		        			{
		        				System.out.println("Haven't create Temp dirs!");
		        			}
		        		}
		        		else
		        		{
		        			System.out.println("NewMsgACT.java: doesn't exit send publickey");
		        		}
		        	}
		        }
		        else
		        {
		        	System.out.println("NewMsgACT.java: doesn't exit send privatekey");
		        }      
				
			}
		}).start();
    }  
    
    /**
     * to insert send Message into database.
     * @param phoneNumber
     * @param message
     */
    private void insertMessageToSend(String phoneNumber,String message)
    {
    	//将发送的短信插入数据库  
	    ContentValues values = new ContentValues();  
	        
	    //发送时间  
	    values.put("date", System.currentTimeMillis());  
	        
	    //阅读状态  
	    values.put("read", 0);  
	        
	    //1为收 2为发  
	    values.put("type", 2);  
	        
	    //送达号码  
	    values.put("address", phoneNumber); 
	        
	    //送达内容  
	    values.put("body", message);  
	        
	    //插入短信库  
	    getContentResolver().insert(Uri.parse("content://sms"),values);
    }
}
