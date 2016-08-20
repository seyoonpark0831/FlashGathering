package com.hunect.bungae;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;



public class BungaeListActivity extends ListActivity {   
	
	//BungaeListActivity.java

	private static final String BUNGAE_LIST_URL = BungaeActivity.MAIN_URL + "/nowbungae.php";
//	private static final String BUNGAE_LIST_URL = "http://www.hunect.com/testphp/nowbungae.php"; 
	
	long backKeyClick = 0;
	long backKeyClickTime;
	
	private String bungaePassword;
	private String selectedNum;


	private ArrayList<BungaeListData> mItems;
	private BungaeListAdapter mAdapter;



	@Override   
	public void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);   
		setContentView(R.layout.bungae_list);     
		setTitle("번개 목록");

		bungaePassword = new String();
		selectedNum = new String();
		
		getListView().setBackgroundColor(Color.rgb(216,221,224)); 

		
		mItems = new ArrayList<BungaeListData>();  
		mAdapter = new BungaeListAdapter(this, mItems);   

		BungaeParserTask task = new BungaeParserTask(this, mAdapter);   
		task.execute(BUNGAE_LIST_URL);
		
	}     

	
	@Override    
	public void onResume() {
		super.onResume();       // 액티비티가 화면에 보임, 필요한 모든 UI 변경 사항을 적용한다.   

//		 NotificationManager nm =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//
//	     nm.cancel(1);
	     
		
	}

	@Override       
	protected void onListItemClick(ListView l, View v, int position, long id) {    
		 // TODO Auto-generated method stub
		
		
		BungaeListData item = mItems.get(position);        
		
		if (item.getBungaeOpenPrivate().toString().equals("0"))
		{
			Intent intent = new Intent(this, BungaeDetailActivity.class);   
			intent.putExtra("selectedNum", item.getBungaeNum());       
			startActivity(intent);
		}
		else
		{
			selectedNum = item.getBungaeNum();
			bungaePassword = item.getBungaePassword();
			passwordEnter();	
		}
		     
		
	}        
	
	// Password Dialog 부분 입니다.
	
		private void passwordEnter()
		{
			LayoutInflater inflator = LayoutInflater.from(this);
	    	View addView2 = inflator.inflate(R.layout.password_dialog, null);
	    	
	    	final EditText passwordEdit;
	    	 
	    	
	    	passwordEdit = (EditText)addView2.findViewById(R.id.passwordEdit);
	    	
	    	AlertDialog.Builder alert3 =  new AlertDialog.Builder(this);
	    	alert3.setTitle("비밀번호 입력");
	    	alert3.setView(addView2);
	    	
	    	alert3.setPositiveButton("확인", new DialogInterface.OnClickListener() 
	    	{	
	    		
				@Override
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					if ( passwordEdit.length() == 4 )
					{
						
						if (bungaePassword.equals(passwordEdit.getText().toString()))
						{
							Intent intent = new Intent(BungaeListActivity.this, BungaeDetailActivity.class);   
							intent.putExtra("selectedNum", selectedNum);       
							startActivity(intent);
						}
						else
						{
							passwordEnter();
							Toast.makeText(BungaeListActivity.this, "비밀번호가 틀렸습니다 다시 입력하세요!!", 0).show();
						}
					}
					else
					{
						passwordEnter();
						Toast.makeText(BungaeListActivity.this, "비밀번호를 4자리 입력하세요!!", 0).show();
					}
				}
			});
	    	alert3.setNegativeButton("취소", new DialogInterface.OnClickListener() 
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
	    	alert3.show();
		}
	

	@Override   
	public boolean onCreateOptionsMenu(Menu menu) {  
		boolean result = super.onCreateOptionsMenu(menu);        
		
		menu.add(0,1,0,"번개 만들기").setIcon(R.drawable.add_button);
		menu.add(0,2,0,"새로 고침").setIcon(R.drawable.blue_refresh);    
		return result;       
	}       

	@Override    
	public boolean onOptionsItemSelected(MenuItem item) {  
		switch (item.getItemId()) {
		
		case 1:
			Intent intent = new Intent(this, AddBungae.class);       
			startActivity(intent);
			return true;
		
		case 2:     

			mItems = new ArrayList<BungaeListData>();      
			mAdapter = new BungaeListAdapter(this, mItems);    

			BungaeParserTask task = new BungaeParserTask(this, mAdapter);   
			task.execute(BUNGAE_LIST_URL);       
			return true;    
		}             
		return super.onOptionsItemSelected(item);     
	} 


	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if ((keyCode == KeyEvent.KEYCODE_BACK) ) {

	        long currentTime = System.currentTimeMillis();
	        final int duration = 2000;
	         
	        backKeyClick ++; 
	         
	        if (backKeyClick == 1){
		        backKeyClickTime = System.currentTimeMillis();
		          
		        Toast t =  Toast.makeText(this, "'뒤로'버튼을 한번 더 누르면 종료됩니다.",
		            Toast.LENGTH_SHORT);
		        	t.setDuration(duration);
		        	t.show();
		          
		         new Thread(new Runnable() {    
		
				     @Override
				     public void run() {
				    	 try {
				    		 	Thread.sleep(duration);
				    	 } catch (InterruptedException e) {
				    		 	e.printStackTrace();
				    	 }
				    	 backKeyClick=0;
				    }	
			    }).start();
		    }else if(backKeyClick == 2){
	
		    	if(currentTime - backKeyClickTime <= duration  ){
		        	return super.onKeyDown(keyCode, event);
		        }
		        backKeyClick = 0;
		    } 
	        return true;   
		}     
		return super.onKeyDown(keyCode, event);    
	}
	
	
}
