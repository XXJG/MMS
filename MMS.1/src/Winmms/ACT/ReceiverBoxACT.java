package Winmms.ACT;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Winmms.MainClass.UserString;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ReceiverBoxACT extends Activity
{
	private ListView listView;
	ArrayList<Map<String, Object>> data = null;
	private ArrayList<ArrayList<String>> allmessage;
	private Intent intent;
	
	private final int delete_all = 0;
	private final int exit = 1;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		ReceiverBoxACT.this.setContentView(R.layout.fileselect);
		
		listView = ( ListView )this.findViewById(R.id.list_file);
		data = new ArrayList<Map<String, Object>>();
		allmessage = new ArrayList<ArrayList<String>>();
		
		ReceiverBoxACT.this.setTitle(R.string.receive_box);
	}
	
	/**
	 * 从短信箱中获得所有短信数据
	 */
	 private void getData()
	 {
		 
		String strDate = "";
		Uri uri = Uri.parse("content://sms/inbox");        
		//Cursor cur = this.managedQuery(uri, UserString.projection, null, null, UserString.aes);  
		Cursor cur = this.managedQuery(uri, UserString.projection, null, null, UserString.sortOrderdesc);
			
		if (cur.moveToFirst()) 
		{        
			do
			{		
				ArrayList<String> value = new ArrayList<String>();
				value.add(cur.getString(UserString.__id));
				value.add(cur.getString(UserString._address));
				
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
                Date d = new Date(cur.getLong(UserString._date));  
                strDate = dateFormat.format(d); 
                
				value.add(strDate);
				value.add(cur.getString(UserString._body));
				
				allmessage.add(value);
			    }while(cur.moveToNext());    
		}
	 }
	 private void initListView()
	 {
			if (dealWithData().equals("success"))
			{
			
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
						if (allmessage.get( arg2 ).get( allmessage.get(arg2).size() - 1 ).equals("wait"))
						{
							Toast.makeText(ReceiverBoxACT.this, R.string.receive_box_exception, Toast.LENGTH_LONG).show();
							return;
						}
						if (allmessage.size() != 0 )
						{
							intent = new Intent();
							intent.putExtra("type", "receiver");
							intent.putStringArrayListExtra("msg", allmessage.get( arg2 )); 
							intent.setClass(ReceiverBoxACT.this,ScanMsgACT.class);
							ReceiverBoxACT.this.startActivity(intent);
						//Toast.makeText(ReceiverBoxACT.this, "back", Toast.LENGTH_LONG).show();
						}
					} 
				});
			}
	 }
	 protected void onStart() 
	 {
	 		// TODO Auto-generated method stub
			 if (!data.isEmpty() || !allmessage.isEmpty())
			 {
				 data.clear();
				 allmessage.clear();
			 }
			// dealWithData();
			 this.initListView();
			 super.onStart();
	 }
	 private String dealWithData()
	 {
		 getData();
		 HashMap<String, Object> map;
		 final Calendar d1 = Calendar.getInstance();
		 //收件箱为空
		 if (allmessage.size() == 0)
		 {
			 map = new HashMap<String, Object>();
			 map.put("picture", R.drawable.receive);
			 map.put("title", "InBox is Empty.");
			 data.add(map);
			 return "success";
		 }
		 
		 for (int i = 0; i < allmessage.size(); i++)
		 {
			 ArrayList<String> value = allmessage.get(i);
			 String str = value.get(UserString._body);
			 if (str.length() < 6)
			 {
				 map = new HashMap<String, Object>();
				 map.put("picture", R.drawable.receive);
				 map.put("title", "From: " + value.get(UserString._address));
				 map.put("content", value.get(UserString._date) + "|" + this.getString( R.string.other_file ));
				 data.add(map);
				 continue;
			 }
			 if (str.charAt(5) == 'o')//被分割的加密文件
			 {
				 for (int j = i + 1; j < allmessage.size();) //查找所有属于一个整体的被分割的文件
				 {
					 ArrayList<String> valuenext = allmessage.get(j);
					 String strnext = valuenext.get(UserString._body);
					 if ( value.get( UserString._address ).equals( valuenext.get( UserString._address ) ) )
					 {
						 if (strnext.length() < 6)
						 {
							 j++; //不可能是同一个整体直接跳过
							 System.out.println("0:" + i + "," + j);
							 continue;
						 }
						 if (strnext.charAt(5) != 'o')
						 {
							 System.out.println("1:" + i + "," + j);
							 j++; //不是同一类型直接跳过
							 continue;
						 }
						 if ( UserString.isFromEqualMsg( value.get(UserString._body), 
							 valuenext.get(UserString._body) ) ) //如果是属于一个整体的被分割文件
						 { 
							 System.out.println("2:" + i + "," + j);
							 value.set(UserString.__id, 
						 				value.get(UserString.__id) + "," + valuenext.get(UserString.__id));//记录其_id
							 
						 	 value.add( valuenext.get(UserString._body) );//添加其正文body
							 allmessage.remove(j); //并在allmessage中删除该选项
						 }
						 else
						 {
							 j++; //不是同一个整体直接跳过
						 }
					 }
					 else
					 {
						 j++; // 不是同一个人发送的短信直接跳过
					 }
				 }
				 // 对收集到的同一个整体的被分割文件，进行拼接
				 
				 String[] msg = new String[value.size() - UserString._body]; //收集所有被分割文件
				 for (int ii = 0; ii < msg.length; ii++)
				 {
						msg[ii]= value.get(UserString._body + ii);
				 }
				 
				 String linkmsg = UserString.linkStrings(msg); //试图拼接所有被分割文件
				 
				 if ( linkmsg == null ) //拼接失败
				 {
					 map = new HashMap<String, Object>();
					 map.put("picture", R.drawable.receive);
					 map.put("title", "From: " + value.get(UserString._address));
					 map.put("content", value.get(UserString._date) + "|" + 
							 this.getString( R.string.incomplete_enctypt_file ));
					 value.add("wait"); //标志其为 等待 完善文件
					 data.add(map);
				 }
				 else //拼接成功
				 {
					 String _id = value.get(UserString.__id); //得到所有文件id 并作进一步处理
					 if (_id.indexOf(",") != -1) 
					 {
						//对多文件进行处理
		    			String[] ids= _id.split(",");
		    			//对短信箱进行修改
		    			deleteMsg(ids, "content://sms");
		    			updateMsg(ids[0], UserString.headencrypt + linkmsg, "content://sms/"); 
		    			//对allmessage进行修改	
		    			for (int ii = 0; ii < msg.length; ii++)
						{
								value.remove(UserString._body);
						} 
						value.add(UserString.headencrypt + linkmsg); 
						value.set(UserString.__id, ids[0]);
		    		}
					else
					{
						String strr = value.get(UserString._body);
						strr = UserString.headencrypt + strr.substring(UserString.headencrypt.length() + 1, strr.length());
						System.out.println(UserString.headencrypt + strr.substring(UserString.headencrypt.length() + 1, strr.length()));
						
						updateMsg( value.get(UserString.__id), strr, "content://sms/" ); 
						
						value.set(UserString._body, strr);
					}
					map = new HashMap<String, Object>();
					map.put("picture", R.drawable.receive);
					map.put("title", "From: " + value.get(UserString._address));
					map.put("content", value.get(UserString._date) + "|" + this.getString(R.string.enctypt_file));
					data.add(map);
				 }
			 }
			 else if (str.charAt(5) == 'k')//被分割的密玥文件
			 {
				 for (int j = i + 1; j < allmessage.size();) //查找所有属于一个整体的被分割的密钥文件
				 {
					 ArrayList<String> valuenext = allmessage.get(j);
					 String strnext = valuenext.get(UserString._body);
					 
					 if ( value.get( UserString._address ).equals( valuenext.get( UserString._address ) ) )
					 {
						 if (strnext.length() < 6)
						 {
							 j++;//不可能是同一个整体直接跳过
							 continue;
						 }
						 if (strnext.charAt(5) != 'k')
						 {
							 j++;//不是同一类型直接跳过
							 continue;
						 }
						 
						 if ( UserString.isFromEqualMsg( value.get(UserString._body), 
							 valuenext.get(UserString._body) ) ) //如果是属于一个整体的被分割密钥文件
						 { 
							 value.set(UserString.__id, 
						 				value.get(UserString.__id) + "," + valuenext.get(UserString.__id));//记录其_id
						 	 value.add( valuenext.get(UserString._body) );//添加其正文body
							 allmessage.remove(j); //并在allmessage中删除该选项
						 }
						 else
						 {
							 j++;//不是同一个整体直接跳过
						 }
					 }
					 else
					 {
						 j++;// 不是同一个人发送的短信直接跳过
					 }
				 }
				 // 对收集到的同一个整体的被分割密钥文件，进行拼接
				 String[] msg = new String[value.size() - UserString._body];//收集所有被分割文件
				 
				 for (int ii = 0; ii < msg.length; ii++)
				 {
						msg[ii]= value.get(UserString._body + ii);
				 }
				 
				 String linkmsg = UserString.linkStrings(msg); //试图拼接所有被分割文件
				 
				 if (linkmsg == null)
				 {
					//拼接失败
					 map = new HashMap<String, Object>();
					 map.put("picture", R.drawable.receive);
					 map.put("title", "From: " + value.get(UserString._address));
					 map.put("content", value.get(UserString._date) + "|" +
							 this.getString(R.string.incomplete_key_file));//标志其为 等待 完善文件
					 value.add("wait"); //标志其为 等待 完善文件
					 data.add(map);
				 }
				 else
				 {
					 String _id = value.get(UserString.__id);
					 if (_id.indexOf(",") != -1)
					 {
		    				String[] ids= _id.split(",");//得到所有文件id 并作进一步处理
		    				//对多文件进行处理
		    				deleteMsg(ids, "content://sms");
		    				updateMsg(ids[0], UserString.headkey + linkmsg, "content://sms/"); 
		    				//对allmessage进行修改	
		    				for (int ii = 0; ii < msg.length; ii++)
							{
									value.remove(UserString._body);
							} 
		    				value.set(UserString.__id, ids[0]);
							value.add( UserString.headkey + linkmsg );
		    		}
					else
					{
						String strr = value.get(UserString._body);
						strr = UserString.headkey + strr.substring(UserString.headkey.length() + 1, strr.length());
						updateMsg( value.get(UserString.__id), strr, "content://sms/" );
						value.set(UserString._body, strr);
					}
					map = new HashMap<String, Object>();
					map.put("picture", R.drawable.receive);
					map.put("title", "From: " + value.get(UserString._address));
					map.put("content", value.get(UserString._date) + "|" + 
							this.getString(R.string.key_file));
					data.add(map);
				 }
			 }
			 else if (str.charAt(5) == 'K')
			 {
				 map = new HashMap<String, Object>();
				 map.put("picture", R.drawable.receive);
				 map.put("title", "From: " + value.get(UserString._address));
				 map.put("content", value.get(UserString._date) + "|" + 
						 this.getString(R.string.independent_key_file));
				 data.add(map);
			 }
			 else if (str.charAt(5) == 'O')
			 {
				 //System.out.println("a:" + str);
				 map = new HashMap<String, Object>();
				 map.put("picture", R.drawable.receive);
				 map.put("title", "From: " + value.get(UserString._address));
				 map.put("content", value.get(UserString._date) + "|" +
						 this.getString(R.string.independet_enctypt_file));
				 data.add(map);
			 }
			 else
			 {
				 map = new HashMap<String, Object>();
				 map.put("picture", R.drawable.receive);
				 map.put("title", "From: " + value.get(UserString._address));
				 map.put("content", value.get(UserString._date) + "|" +
						 this.getString(R.string.other_file));
				 data.add(map);
			 }
		 }
		 final Calendar d2 = Calendar.getInstance();
		 long t = d2.getTime().getTime() - d1.getTime().getTime();
		 System.out.println("\nReceiver seconds:" + t);
		 return "success";
	 }
	 private boolean deleteMsg(String[] ids, String uri)
	 {
		 for ( int ii = 1; ii < ids.length; ii++)
		 {
			this.getContentResolver().delete( 
					Uri.parse(uri), "_id=?",new String[]{ids[ii]}); 
		 }
		 return true;
	 }
	 private boolean updateMsg(String id, String linkmsg, String uri)
	 {
		 ContentValues cv = new ContentValues();    
		// cv.put("_id", ids[0]);    
		 //cv.put("address", value.get(UserString._address));       
		 cv.put("body", linkmsg); 
			
		 this.getContentResolver().update(Uri.parse(uri), cv, "_id=?", new String[]{id}); 
		 return true;
	 }
	 public boolean onCreateOptionsMenu(Menu menu) 
	 {
	    	// TODO Auto-generated method stub
	    	menu.add(0, delete_all, delete_all, R.string.delete_all);
	    	menu.add(0, exit, exit, R.string.exit);
	    	return super.onCreateOptionsMenu(menu);
	 }
	    
	    @Override
	 public boolean onOptionsItemSelected(MenuItem item) 
	 {
	    	// TODO Auto-generated method stub
	    	switch(item.getItemId())
	    	{
	    		case delete_all:
	    			this.getContentResolver().delete( 
    						Uri.parse("content://sms"), "type=?", new String[]{"1"}); 
	    			//data.clear();
	    			//data.add("Receiver is Empty.");
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
}
