package com.hunect.bungae;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Xml;
import android.widget.Toast;

public class C2DMTestActivity extends Activity {
	
	//C2DMTestActivity.java

	private static final String BUNGAE_C2DM_URL = BungaeActivity.MAIN_URL + "/c2dm/c2dm.php";
//	private static final String BUNGAE_C2DM_URL = "http://www.hunect.com/testphp/c2dm/c2dm.php";
	
//	private String c2dm_token;
	
	private String registration_c2dm_id;
	
	ArrayList<TokenData> tokenList = null;
	 
	TokenData tokenData = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.c2dmtest);
	    
//	    c2dm_token = new String();
	    registration_c2dm_id = new String();
	    
	    tokenList = new ArrayList<TokenData>();
	    
	    if(isOnline()) {
	    	Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
	        registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
	        registrationIntent.putExtra("sender", "hunect@gmail" +"" +".com"); 
	        startService(registrationIntent); 
		    
		    AuthTokenParserTask task = new AuthTokenParserTask(this);   
			task.execute(BUNGAE_C2DM_URL);
	    }
	    
	    else {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(C2DMTestActivity.this);
			builder.setTitle("네트워크 오류")
			.setMessage("네트워크 연결을 확인한 후 다시 시도해 주세요.")
			.setCancelable(true)
			.setNeutralButton("확인", null)
			.create().show();
	    }

	}
	
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    else return false;
	}
	
	
	public void sender(String regId,String authToken,String msg) throws Exception{
	     StringBuffer postDataBuilder = new StringBuffer();
	        
	     postDataBuilder.append("registration_id="+regId); 
	     postDataBuilder.append("&collapse_key=1"); 
	     postDataBuilder.append("&delay_while_idle=1");
	     postDataBuilder.append("&data.msg="+URLEncoder.encode(msg, "UTF-8"));


	        byte[] postData = postDataBuilder.toString().getBytes("UTF8");


	        URL url = new URL("https://android.apis.google.com/c2dm/send");
	        
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
	        conn.setRequestProperty("Authorization", "GoogleLogin auth=" + authToken);


	        OutputStream out = conn.getOutputStream();
	        out.write(postData);
	        out.close();


	        conn.getInputStream();




	    }
	    
	    
	
	
	
	
	
	public String GetDeviceId() {

		TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		
		//deviceId가 null이면 폰 기능이 없는 기기라는 뜻: ANDROID_ID를 반환
		if(deviceId==null){String androidId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID); 
			return androidId;
		}
		else {
			return deviceId;
		}
	}
	
	
	public class AuthTokenParserTask extends AsyncTask<String, Integer, String>{  
    	private C2DMTestActivity mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	
    	public AuthTokenParserTask(C2DMTestActivity activity) {  
    		mActivity = activity;     
    		}       
    	  
    	@Override    
    	protected void onPreExecute() { 
    		
    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("푸시 서버 연결중...");     
    		mProgressDialog.show();      
    		}      
    	
    	@Override    
    	protected String doInBackground(String... params) {   
    		//BungaeListAdapter result = null;          
    		try {
//    			URL url = new URL(params[0]);  
    			
    			getAuthToken();
    			
    			
    	                  
    			} catch (Exception e) {      
    				e.printStackTrace();        
    				}          
    		  
    		return completeString;        
    		}       
    	
    	@Override    
    	protected void onPostExecute(String completeString) {   

    		mProgressDialog.dismiss();
    		
    		if(registration_c2dm_id==null) {
    			Toast.makeText(C2DMTestActivity.this, "서버 연결 오류, 재시도 중...", 0).show();
    			AuthTokenParserTask task = new AuthTokenParserTask(C2DMTestActivity.this);   
    			task.execute(BUNGAE_C2DM_URL);
    			
    		}
    		
    		else {
//        		Toast.makeText(C2DMTestActivity.this, "토큰: "+registration_c2dm_id, 0).show();
        		C2DMParserTask task2 = new C2DMParserTask(C2DMTestActivity.this);   
        		task2.execute("http://www.hunect.com/c2dmphp/addc2dmtoken.php");
        		
    		}
    		
    	}
    	
    	
    	public String getAuthToken() throws Exception{
   	        
   	     StringBuffer postDataBuilder = new StringBuffer();
   	        postDataBuilder.append("accountType=HOSTED_OR_GOOGLE");
   	        postDataBuilder.append("&Email=hunect@gmail.com");
   	        postDataBuilder.append("&Passwd=110530admin");
   	        postDataBuilder.append("&service=ac2dm"); 
   	        postDataBuilder.append("&source=test-1.0");


   	        byte[] postData = postDataBuilder.toString().getBytes("UTF8");


   	        URL url = new URL("https://www.google.com/accounts/ClientLogin");
   	        
   	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
   	        conn.setDoOutput(true);
   	        conn.setUseCaches(false);
   	        conn.setRequestMethod("POST");
   	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
   	        conn.setRequestProperty("Content-Length", Integer.toString(postData.length));


   	        
   	        OutputStream out = conn.getOutputStream();
   	        out.write(postData);
   	        out.close();


   	        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
   	        
   	        String sidLine = br.readLine();
   	        String lsidLine = br.readLine();
   	        String authLine = br.readLine();
   	        
   	        System.out.println("sidLine----------->>>"+sidLine);
   	        System.out.println("lsidLine----------->>>"+lsidLine);
   	        System.out.println("authLine----------->>>"+authLine);
   	        System.out.println("AuthKey----------->>>"+authLine.substring(5, authLine.length()));
   	        
   	        authLine.substring(5, authLine.length());
   	        
   	     registration_c2dm_id = C2dm_BroadcastReceiver.registration_id;
   	        
   	     return registration_c2dm_id;
   	    }


    	
    }
	
	
	
	
	
	
	
	
	
	public class C2DMParserTask extends AsyncTask<String, Integer, String>{  
    	private C2DMTestActivity mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	boolean boolean_pid = false,
    			boolean_devicetoken = false;
    	
    	public C2DMParserTask(C2DMTestActivity activity) {  
    		mActivity = activity;     
    		}       
    	  
    	@Override    
    	protected void onPreExecute() { 
    		
    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("푸시 서비스 등록 중...");     
    		mProgressDialog.show();      
    		}      
    	
    	@Override    
    	protected String doInBackground(String... params) {   
 
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
    	            String deviceId=GetDeviceId();
    	            
    	            StringBuffer buffer = new StringBuffer(); 
    	            buffer.append("deviceuid").append("=").append(deviceId+"&")
    	            .append("devicetoken").append("=").append(registration_c2dm_id+"&")
    	            .append("devicemodel").append("=").append(Build.MODEL+"&")
    	            .append("deviceversion").append("=").append(Build.VERSION.RELEASE.toString());
    	           
    	            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "utf-8"); 
    	            PrintWriter writer = new PrintWriter(outStream); 
    	            writer.write(buffer.toString()); 
    	            writer.flush(); 
    			
    	          
    	        InputStream is = http.getInputStream();
    	        parseXml(is);       
    	        completeString = "complete";
    	        
    			} catch (Exception e) {      
    				e.printStackTrace();        
    				}          
    		  
    		return completeString;        
    		}       
    	
    	@Override    
    	protected void onPostExecute(String completeString) {   
    		mProgressDialog.dismiss();  

//    		Toast.makeText(C2DMTestActivity.this, "pid : "+tokenList.get(0).getPushId(), 0).show();
    		
    		Intent intent = new Intent(C2DMTestActivity.this, AuthSend.class);
    		intent.putExtra("pid", tokenList.get(0).getPushId());
			startActivity(intent);
			finish();
    		
    	}         
    	
    	
    	public String parseXml(InputStream is) throws IOException, XmlPullParserException {    
    		XmlPullParser parser = Xml.newPullParser();     
    		try {
    			
    			parser.setInput(is, null);      
    			int eventType = parser.getEventType();     
    			TokenData tokenData = null;     
    			tokenData = new TokenData();
    			while (eventType != XmlPullParser.END_DOCUMENT) {   
    				String tag = null;     
    				switch (eventType) {    
    				case XmlPullParser.START_TAG:        
    					tag = parser.getName(); 
    					
    					if(tag.equals("token"))
    						tokenData = new TokenData();
    					if(tag.equals("pid"))
    						boolean_pid = true;
    					if(tag.equals("devicetoken"))
    						boolean_devicetoken = true;
    					    						                                   
    					break;           
    					
    				case XmlPullParser.TEXT:
    					if(boolean_pid){
    						tokenData.setPushId(parser.getText());
    						boolean_pid = false;
    					}
    					if(boolean_devicetoken){
    						tokenData.setDeviceToken(parser.getText());
    						boolean_devicetoken = false;
    					}
    					    					
    					break;
    					
    					
    					case XmlPullParser.END_TAG:  
    						
    						tag = parser.getName();
    						if (tag.equalsIgnoreCase("token") && tokenData != null){
    							tokenList.add(tokenData);
    							completeString = "complete";
    							
    						}

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
