package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;


public class SettingActivity extends Activity implements OnClickListener {

	private static final int DIALOG_PUSHON = 0;
	private static final int DIALOG_PUSHOFF = 1;
	
	private Button setting1Button;
	private Button setting2Button;
	private Button setting5Button;

	private String pushState;
	private Boolean pushFlag;

	long backKeyClick = 0;
	long backKeyClickTime;
	

	private ToggleButton tb;
	
	//SettingActivity.java

	private static final String BUNGAE_PUSH_URL = BungaeActivity.MAIN_URL + "/updatepushstate-a.php";
//	private static final String BUNGAE_PUSH_URL = "http://www.hunect.com/testphp/updatepushstate-a.php";
	

	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.setting_list);
	    setTitle("설정");
	    // TODO Auto-generated method stub

	    pushState = new String();

	    		
	    
	    setting1Button = (Button)findViewById(R.id.setting1Button);
	    setting2Button = (Button)findViewById(R.id.setting2Button);
	    setting5Button = (Button)findViewById(R.id.setting5Button);
	    	    
	    setting1Button.setOnClickListener(this);
	    setting2Button.setOnClickListener(this);
	    setting5Button.setOnClickListener(this);

	    
	    tb = (ToggleButton)findViewById(R.id.push_toggleButton);
	    
	    if (UserInfoClass.getInstance().userInfo.get("u_push_state").equals("0"))
	    {
	    	tb.setChecked(false);
	    }
	    else
	    {
	    	tb.setChecked(true);
	    }
	    
	    
	    
	    if (tb.isChecked())
	    {
	    	tb.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_toggle_on));
	    	pushFlag = true;
	    }
	    else
	    {
	    	tb.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_toggle_off));
	    	pushFlag = false;
	    }
	    
        tb.setOnClickListener(this);
		

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){

		case R.id.setting1Button:      
			Intent intent = new Intent(this, Setting1_Notice_Expand.class); 
			startActivity(intent);   
			break;
		
		case R.id.setting2Button:      
			Intent intent2 = new Intent(this, Setting2_Account.class); 
			startActivity(intent2);   
			break;
			
		case R.id.setting5Button:      
			Intent intent5 = new Intent(this, Setting4_TOA.class); 
			startActivity(intent5);   
			break;
		
		case R.id.push_toggleButton:
			{
				
				if (pushFlag == true)
			    {
		            pushState = "uninstalled";
		            showDialog(DIALOG_PUSHOFF);
			    }
			    
				if (pushFlag == false)
			    {
		            pushState = "active";
		            showDialog(DIALOG_PUSHON);	
			    }
			}
			break;
		
		}
	}
	
	
	@Override
    protected Dialog onCreateDialog(int id){
		
		
		
    	switch(id){
    	case DIALOG_PUSHON:
    		return new AlertDialog.Builder(this)
    			.setTitle("푸시 알림")
    			.setMessage("푸시를 켜시겠습니까?")
    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
												
						UpdatePushTask task = new UpdatePushTask(SettingActivity.this);   
						task.execute(BUNGAE_PUSH_URL);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						
						if (pushFlag == true)
						{
							pushFlag = true;
							tb.setChecked(false);
							
						}
						else
						{
							pushFlag = false;
							tb.setChecked(true);
						}
						
					}
				}).create();
    		
    		
    	case DIALOG_PUSHOFF:
    		return new AlertDialog.Builder(this)
    			.setTitle("푸시 알림")
    			.setMessage("푸시를 끄시겠습니까?")
    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
												
						UpdatePushTask task = new UpdatePushTask(SettingActivity.this);   
						task.execute(BUNGAE_PUSH_URL);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						
						if (pushFlag == true)
						{
							pushFlag = true;
							tb.setChecked(false);
							
						}
						else
						{
							pushFlag = false;
							tb.setChecked(true);
						}
						
					}
				}).create();	
    		
    		
    	}
    	
    	return null;

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
	
	
	
	public class UpdatePushTask extends AsyncTask<String, Integer, String>{  
    	private SettingActivity mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;

    	
    	public UpdatePushTask(SettingActivity activity) {  
    		mActivity = activity;     
    		}            
    	  
    	@Override    
    	protected void onPreExecute() { 
    		
    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("푸시 상태 변경중...");     
    		mProgressDialog.show();      
    		}      
    	
    	@Override    
    	protected String doInBackground(String... params) {   
    		//BungaeListAdapter result = null;          
    		try {               
    			
    			URL url = new URL(params[0]);  
    			
    			 HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속 
    	            //-------------------------- 
    	            //   전송 모드 설정 - 기본적인 설정이다 
    	            //-------------------------- 
    	            http.setDefaultUseCaches(false);                                            
    	            http.setDoInput(true);                         // 서버에서 읽기 모드 지정 
    	            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정  
    	            http.setRequestMethod("POST");         // 전송 방식은 POST 

    	            // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다 
    	            http.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
    	            //-------------------------- 
    	            //   서버로 값 전송 
    	            //-------------------------- 
    	            StringBuffer buffer = new StringBuffer(); 
    	            buffer.append("pushid").append("=").append(UserInfoClass.getInstance().userInfo.get("u_push_id")+"&")
    	            .append("status").append("=").append(pushState);
    	           
    	            
    	            
    	            Log.d("BungaeEnter", buffer.toString());
    	            
    	            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "utf-8"); 
    	            PrintWriter writer = new PrintWriter(outStream); 
    	            writer.write(buffer.toString()); 
    	            writer.flush(); 
    			
    	          
    	            InputStream is = http.getInputStream();
        			parseXml(is);          
        			} catch (Exception e) {      
        				e.printStackTrace();        
        				}            
        		  
        		return completeString;         
    		}       
    	
    	@Override    
    	protected void onPostExecute(String completeString) {   

    		String pushStr = new String();
    		
    		if (pushState.equals("uninstalled"))
			{
				tb.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_toggle_off));
				pushFlag = false;
				pushStr = "0";
				tb.setChecked(true);
			}
			else
			{
				tb.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_toggle_on));
				pushFlag = true;
				pushStr = "1";
				tb.setChecked(false);
			}
    		
    		
    		
    		boolean didSuccessCommit = false;
    		if(didSuccessCommit == false) {
    			final String PREF_FILE_NAME = "UserInfo";
//    			Toast.makeText(Register.this, "pid(userInfoBuffer) : "+userInfoBuffer.get("u_push_id"), Toast.LENGTH_LONG).show();
    			SharedPreferences pref = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
    			SharedPreferences.Editor prefEditor = pref.edit();
    			prefEditor.putString("u_num", UserInfoClass.getInstance().userInfo.get("u_num"));
    			prefEditor.putString("u_id", UserInfoClass.getInstance().userInfo.get("u_id"));
    			prefEditor.putString("u_phone", UserInfoClass.getInstance().userInfo.get("u_phone"));
    			prefEditor.putString("u_sex", UserInfoClass.getInstance().userInfo.get("u_sex"));
    			prefEditor.putString("u_age", UserInfoClass.getInstance().userInfo.get("u_age"));
    			prefEditor.putString("u_intro", UserInfoClass.getInstance().userInfo.get("u_intro"));
    			prefEditor.putString("u_push_id", UserInfoClass.getInstance().userInfo.get("u_push_id"));
    			prefEditor.putString("u_push_state", pushStr);
    			didSuccessCommit = prefEditor.commit();
    		}
    		
    		//다른 액티비티에서 받아 쓸 class기록
    		if(didSuccessCommit) {
    			SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
    			UserInfoClass.getInstance().userInfo = new HashMap<String, String>();
    			UserInfoClass.getInstance().userInfo.put("u_num", pref.getString("u_num", null));
    			UserInfoClass.getInstance().userInfo.put("u_id", pref.getString("u_id", null));
    			UserInfoClass.getInstance().userInfo.put("u_phone", pref.getString("u_phone", null));
    			UserInfoClass.getInstance().userInfo.put("u_sex", pref.getString("u_sex", null));
    			UserInfoClass.getInstance().userInfo.put("u_age", pref.getString("u_age", null));
    			UserInfoClass.getInstance().userInfo.put("u_intro", pref.getString("u_intro", null));
    			UserInfoClass.getInstance().userInfo.put("u_push_id", pref.getString("u_push_id", null));
    			UserInfoClass.getInstance().userInfo.put("u_push_state", pref.getString("u_push_state", null));
    		}
    		
    		
    		
    		
    		mProgressDialog.dismiss();      

    		}         
    	
    	
    	public String parseXml(InputStream is) throws IOException, XmlPullParserException {    
    		XmlPullParser parser = Xml.newPullParser();     
    		try {
    			
    			completeString = "complete";
    			
    			parser.setInput(is, null);      
    			int eventType = parser.getEventType();     
    			          
    			while (eventType != XmlPullParser.END_DOCUMENT) {   
    				
    				switch (eventType) {    
    				case XmlPullParser.START_TAG:        
    					
    					
    						                                   
    					break;           
    					
    				case XmlPullParser.TEXT:
    					
    						break;                       
    						}                         
    				eventType = parser.next();           
    				}     

    			
    			
    			} catch (Exception e) {     
    				e.printStackTrace();    
    				}          
    		return completeString;     
    	} 
    	
    	
    }
	


}
