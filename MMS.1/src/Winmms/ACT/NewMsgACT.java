package Winmms.ACT;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class NewMsgACT extends Activity
{
	
	 /**收件人电话**/   
	private EditText receiverNumber = null;
	      
	 /**编辑信息**/  
	 private EditText Message = null;  
	 
	 public  static  final int send = 0;
	 public static final int exit = 1;
	 
	 /**发送与接收的广播**/  
	 private final String SENT_SMS_ACTION = "SENT_SMS_ACTION";  
	 private final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
	 
	 private String phoneNumber;
	 private String message;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newmsgmain);
		
		this.setTitle(R.string.new_message);
		Message = (EditText)this.findViewById(R.id.inputmsg);
		receiverNumber = (EditText)this.findViewById(R.id.receiverphone);
		Intent intent = this.getIntent();
		if (intent != null)
		{
			receiverNumber.setText(intent.getStringExtra("address"));
		}
		receiverNumber.addTextChangedListener(new TextWatcher() 
		{
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				// TODO Auto-generated method stub
				System.out.println("CharSequence: " + s +",int :" + start + ", int :" + before + ", int:" + count);
				if (before == 0)
				{
					if (s.charAt(start) < '0' || s.charAt(start) > '9')
					{
						receiverNumber.setText(s.subSequence(0, start));
						receiverNumber.setSelection(receiverNumber.getText().toString().length());
					}
					/*else
					{
						for (int i = 0; i < UserString.phoneremark.length; i++)
						{
							if ( (start + 1) == UserString.phoneremark[i])
							{
								receiverNumber.setText(s.subSequence(0, start + 1) + " ");
								//s = s + " ";
								receiverNumber.setSelection(receiverNumber.getText().toString().length());
							}
							else if ( (start + 1) < UserString.phoneremark[i])
							{
								break;
							}
						}
					}*/
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) 
			{
				// TODO Auto-generated method stub
				System.out.println("CharSequence: " + s +",int :" + start + ", int :" + after + ", int:" + count);
			}
			
			@Override
			public void afterTextChanged(Editable s) 
			{
				// TODO Auto-generated method stub
				
			}
		});
		Message.setOnFocusChangeListener(new OnFocusChangeListener() 
		{
			
			public void onFocusChange(View v, boolean hasFocus) 
			{
				// TODO Auto-generated method stub
				if (hasFocus)
				{
					if (receiverNumber.getText().toString().equals(""))
					{
						Message.clearFocus();
						receiverNumber.setFocusable(true);
						receiverNumber.setFocusableInTouchMode(true);
						receiverNumber.requestFocus();
						Toast.makeText(NewMsgACT.this, R.string.please_input_your_right_sending_telephone, Toast.LENGTH_LONG).show();
					}
					//System.out.println("ruuuu");
				}
			}
		});
		receiverNumber.requestFocus();
		
		setEditTextLines(10);
	}
	public boolean onCreateOptionsMenu(Menu menu) 
	{
    	// TODO Auto-generated method stub
    	menu.add(0, 0, send,R.string.send);
    	menu.add(0, 1, exit,R.string.exit);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// TODO Auto-generated method stub
    	switch(item.getItemId())
    	{
    		case send:
    			String text = Message.getText().toString(); 
    			NewMsgACT.this.message = UserString.deleteEnter(text);
    			String phoneNumber = receiverNumber.getText().toString();
    			NewMsgACT.this.phoneNumber = UserString.getChinaNumber(phoneNumber);
    			for (int i = 0; i < phoneNumber.length(); i++)
    			{
    				if (phoneNumber.charAt(i) < '0' || phoneNumber.charAt(i) > '9')
    				{
    					Toast.makeText(this, "您的手机号码含有非数字字\n请确认后再发送", Toast.LENGTH_LONG).show();
    					return false;
    				}
    			}
    			if (!UserString.fileIsExists(UserString.headpath + "friends/publicKey" + phoneNumber + ".dat"))
        		{
    				Message.setText(NewMsgACT.this.getText( R.string.send_key ));
        			setEditTextLines(10);
        		}
    			sendSMS(); 
    			break;
    		case exit:
    			this.finish();
    			break;
    		default:
    			Toast.makeText(this, "Sorry,haven't selected item!",Toast.LENGTH_LONG).show();     				
    	}
    	return super.onOptionsItemSelected(item);
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
		        PendingIntent sentPI = PendingIntent.getBroadcast(NewMsgACT.this, 0, sentIntent,  
		            0);  
		      
		        // create the deilverIntent parameter  
		        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);  
		        PendingIntent deliverPI = PendingIntent.getBroadcast(NewMsgACT.this, 0,  
		            deliverIntent, 0);  
		        final Calendar d1 = Calendar.getInstance();
		        String strDate = "";
		        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");   
                strDate = dateFormat.format(d1.getTime()); 
                System.out.println("\n before date:" + strDate);
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
		        //输出测试数据
		        final Calendar d2 = Calendar.getInstance(); 
		        strDate = dateFormat.format(d2.getTime()); 
                System.out.println("\n after date:" + strDate);
				long t = d2.getTime().getTime() - d1.getTime().getTime();
				System.out.println("\n seconeds:" + t);
				
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
	    getContentResolver().insert(Uri.parse("content://sms"), values);
    }
    
    /**
     * To draw the line for EditText.
     * @param counts
     */
    private void setEditTextLines(int counts)
    {
    	String str = "";
    	for (int i = 0;i < counts; i++)
    	{
    		str += '\n';
    	}
    	 Message.append(str);
    }
}
