package Winmms.ACT;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Winmms.MainClass.UserString;
import android.app.Activity;
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

public class SendBoxACT extends Activity
{
	private ListView listView;
	ArrayList<Map<String, Object>> data;
	private ArrayList<ArrayList<String>> allmessage;
	private Intent intent;
	private final int delete_all = 0;
	private final int exit = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SendBoxACT.this.setContentView(R.layout.fileselect);
		
		listView = ( ListView )this.findViewById(R.id.list_file);
		data = new ArrayList<Map<String, Object>>();
		allmessage = new ArrayList<ArrayList<String>>();
		SendBoxACT.this.setTitle(R.string.send_box);
		
	}
	private void initListView()
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
				if (data.size() != 0)
				{
					intent = new Intent();
					intent.putExtra("type", "sent");
					intent.putStringArrayListExtra("msg", allmessage.get( arg2 )); 
					intent.setClass(SendBoxACT.this,ScanMsgACT.class);
					SendBoxACT.this.startActivity(intent);
				}
			} 
		});
	}
	@Override
	protected void onStart() 
	{
		// TODO Auto-generated method stub
		if (!data.isEmpty() || !allmessage.isEmpty())
		{
			 data.clear();
			 allmessage.clear();
		}
		getData();
		this.initListView();
		super.onStart();
	}
	private void getData()
	{
		String strDate = "";
		Uri uri = Uri.parse("content://sms/sent");        
		Cursor cur = this.managedQuery(uri, UserString.projection, null, null, UserString.sortOrderdesc); 
		HashMap<String, Object> map;
		
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
				
				map = new HashMap<String, Object>();
				map.put("picture", R.drawable.sent);
				map.put("title", "To : " + cur.getString(UserString._address));
				map.put("content", strDate);
				data.add(map);
				
			    }while(cur.moveToNext());    
		}
		
		if (allmessage.size() == 0)
		 {
			 map = new HashMap<String, Object>();
			 map.put("picture", R.drawable.sent);
			 map.put("title", "SentBox is Empty.");
			 data.add(map);
			 return;
		 }
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
    						Uri.parse("content://sms"), "type=?", new String[]{"2"}); 
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
