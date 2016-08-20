package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Setting2_Account extends Activity implements OnClickListener {
	
	private static final int DIALOG_OK_CANCEL = 0;
	private static final int DIALOG_OK_CANCEL2 = 1;
	
	private TextView UserIdText;
	private TextView UserSexText;
	private TextView UserAgeText;
	private EditText UserIntroText;
	private ImageButton introEditButton;
	private EditText introText;
	private String introTextBuffer;
	
	private Button deleteButton;
	
	ArrayList<AccountData> UserAccount = null;
	 
	AccountData userAccount = null;
    
	boolean boolean_u_num = false,
			boolean_u_id = false,
			boolean_u_sex = false,
			boolean_u_age = false,
			boolean_u_intro = false;
	//Setting2_Account.java

	private static final String BUNGAE_USER_URL = BungaeActivity.MAIN_URL + "/hostcall.php";
	private static final String BUNGAE_DELETEACCOUNT_URL = BungaeActivity.APNS_URL + "/deleteaccount-and.php";
//	private static final String BUNGAE_USER_URL = "http://www.hunect.com/testphp/hostcall.php";
//	private static final String BUNGAE_DELETEACCOUNT_URL = "http://www.hunect.com/testphp/deleteaccount-and.php"; 
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.setting2_account);
	    setTitle("계정정보");
	    
	    UserIdText = (TextView)findViewById(R.id.acategoryLabel);
	    UserSexText = (TextView)findViewById(R.id.atitleLabel);
	    UserAgeText = (TextView)findViewById(R.id.ahostLabel);
	    UserIntroText = (EditText)findViewById(R.id.acontentText);
	    introEditButton = (ImageButton)findViewById(R.id.introEditButton);
	    
	    UserIntroText.setFocusable(false);
	    UserIntroText.setClickable(false);
	    UserIntroText.setLongClickable(false);
	    
	    UserAccount = new ArrayList<AccountData>();
	    
	    deleteButton = (Button)findViewById(R.id.aenterButton);
	    
	    
	    deleteButton.setOnClickListener(Setting2_Account.this);
	    
	    UserParserTask task = new UserParserTask(this);   
		task.execute(BUNGAE_USER_URL);

		introEditButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(Setting2_Account.this, "자기소개를 수정합니다.", Toast.LENGTH_LONG).show();
				
				LayoutInflater inflator = LayoutInflater.from(Setting2_Account.this);
		    	View addView = inflator.inflate(R.layout.dialog_intro_edit, null);
		    	
		    	introText = (EditText)addView.findViewById(R.id.dialog_introText);
		    	introText.setText(UserIntroText.getText().toString());
		    	
		    	AlertDialog.Builder builder = new AlertDialog.Builder(Setting2_Account.this);
				builder.setTitle("자기소개 수정")
			    .setCancelable(true)
			    .setView(addView)
			    .setNegativeButton("취소", null)
			    .setPositiveButton("저장", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						introTextBuffer = new String();
						introTextBuffer = introText.getText().toString();
						UserIntroText.setText(introTextBuffer);
						
						IntroParserTask task = new IntroParserTask(Setting2_Account.this);   
						task.execute(BungaeActivity.MAIN_URL + "/modifyaccount.php");
						
					}
				})
			    .create().show();
			 
				
			}
		});

	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.aenterButton:
		{
			showDialog(DIALOG_OK_CANCEL);	
			
		}
		break;
		
		
		}
	}
	
	@Override
    protected Dialog onCreateDialog(int id){
    	switch(id){
    	case DIALOG_OK_CANCEL:
    		return new AlertDialog.Builder(this)
    			.setTitle("계정 삭제")
    			.setMessage("계정을 삭제하시겠습니까?")
    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showDialog(DIALOG_OK_CANCEL2);	
					}
				})
				.setNegativeButton("Cancel", null).create();
    		
    	case DIALOG_OK_CANCEL2:
    		return new AlertDialog.Builder(this)
    			.setTitle("계정 삭제")
    			.setMessage("계정 삭제시 회원 정보가 모두 삭제되며, 기존 아이디로 재가입이 불가능합니다.\n계정 삭제를 계속 하시겠습니까?")
    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

						DeleteAccountTask task = new DeleteAccountTask(Setting2_Account.this);   
						task.execute(BUNGAE_DELETEACCOUNT_URL);
						
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
											
					}
				}).create();
    		
    	}
    	
    	return null;
    	
    	
    	
    }

	
	@Override
	public void onResume() {
		super.onResume();       // 액티비티가 화면에 보임, 필요한 모든 UI 변경 사항을 적용한다.   
		
	}
	
	public class UserParserTask extends AsyncTask<String, Integer, String>{  
    	private Setting2_Account mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	boolean boolean_t_row = false;
    	
    	public UserParserTask(Setting2_Account activity) {  
    		mActivity = activity;     
    		}       
    	  
    	@Override    
    	protected void onPreExecute() { 
    		
    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("로딩중...");     
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
    	            buffer.append("id").append("=").append(UserInfoClass.getInstance().userInfo.get("u_id")); 
    	            
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
    		

    		if (UserAccount.size()==0)
    		{
    			
    		}
    		else
    		{
    			UserIdText.setText(UserAccount.get(0).getUserId());

        		if(UserAccount.get(0).getUserSex().equals("0"))
        		{
        			UserSexText.setText("남");
        		}
        		else
        		{
        			UserSexText.setText("여");
        		}
        		
        		UserAgeText.setText(UserAccount.get(0).getUserAge());
        		UserIntroText.setText(UserAccount.get(0).getUserIntro());
    		}
    		
    		mProgressDialog.dismiss();      

    		
    		}         
    	
    	
    	public String parseXml(InputStream is) throws IOException, XmlPullParserException {    
    		XmlPullParser parser = Xml.newPullParser();     
    		try {
    			
    			parser.setInput(is, null);      
    			int eventType = parser.getEventType();     
    			AccountData userAccount = null;              
    			while (eventType != XmlPullParser.END_DOCUMENT) {   
    				String tag = null;     
    				switch (eventType) {    
    				case XmlPullParser.START_TAG:
    					tag = parser.getName(); 
    					
    					if(tag.equals("message"))
    						userAccount = new AccountData();
    						Log.d("ParsingTest", "ParsingBool : " + boolean_u_num);
    					if(tag.equals("u_num"))
    						boolean_u_num = true;
    						Log.d("ParsingTest", "ParsingBool : " + boolean_u_num);
    					if(tag.equals("u_id"))
    						boolean_u_id = true;
    						Log.d("ParsingTest", "ParsingBool : " + boolean_u_num);
    					if(tag.equals("u_sex"))
    						boolean_u_sex = true;
    						Log.d("ParsingTest", "ParsingBool : " + boolean_u_num);
    					if(tag.equals("u_age"))
    						boolean_u_age = true;
    						Log.d("ParsingTest", "ParsingBool : " + boolean_u_num);
    					if(tag.equals("u_intro"))
    						boolean_u_intro = true;
    						Log.d("ParsingTest", "ParsingBool : " + boolean_u_num);
    					
    					break;           
    					
    				case XmlPullParser.TEXT:
    					if(boolean_u_num){
    						userAccount.setUserNum(parser.getText());
    						boolean_u_num = false;
    					}
    					if(boolean_u_id){
    						userAccount.setUserId(parser.getText());
    						boolean_u_id = false;
    					}
    					if(boolean_u_sex){
    						userAccount.setUserSex(parser.getText());
    						boolean_u_sex = false;
    					}
    					if(boolean_u_age){
    						userAccount.setUserAge(parser.getText());
    						boolean_u_age = false;
    					}
    					if(boolean_u_intro){
    						String enStr = parser.getText();
    						
    						String deStr = URLDecoder.decode(enStr, "utf-8");
    						
    						userAccount.setUserIntro(deStr);
    						Log.d("ParsingTest", deStr);
    						boolean_u_intro = false;
    					}
    					
    					
    					break;
    					
    					
    					case XmlPullParser.END_TAG:  
    						
    						tag = parser.getName();
    						if (tag.equalsIgnoreCase("message") && userAccount != null){
    							Log.d("ParsingTest", "ParsingEnd");
    							UserAccount.add(userAccount);
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
	
	
	/*************************************************************************************/
	
	
	public class IntroParserTask extends AsyncTask<String, Integer, String>{  
    	private Setting2_Account mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	boolean boolean_t_row = false;
    	
    	public IntroParserTask(Setting2_Account activity) {  
    		mActivity = activity;     
    		}       
    	  
    	@Override    
    	protected void onPreExecute() { 
    		
    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("로딩중...");     
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
    	            StringBuffer buffer = new StringBuffer();
    	            buffer.append("u_id=").append(UserInfoClass.getInstance().userInfo.get("u_id")).append("&")
	            	  .append("u_intro=").append(introText.getText().toString()); 
    	            
    	            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "utf-8"); 
    	            PrintWriter writer = new PrintWriter(outStream); 
    	            writer.write(buffer.toString()); 
    	            writer.flush(); 
    			
    	            http.getInputStream();
    	            
    			} catch (Exception e) {      
    				e.printStackTrace();        
    			}         
    		  
    		return completeString;        
    	}

    	
    	@Override    
    	protected void onPostExecute(String completeString) {   

    		mProgressDialog.dismiss();
    		Toast.makeText(Setting2_Account.this, "자기소개 수정 완료", Toast.LENGTH_LONG).show();
    		
   		}
	}
	
	
	public class DeleteAccountTask extends AsyncTask<String, Integer, String>{  
    	private Setting2_Account mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;

    	
    	public DeleteAccountTask(Setting2_Account activity) {  
    		mActivity = activity;     
    		}            
    	  
    	@Override    
    	protected void onPreExecute() { 

    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("계정 삭제중...");     
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
    	            
    	            String deviceId=GetDeviceId();
    	            
    	            StringBuffer buffer = new StringBuffer(); 
    	            buffer.append("u_id=").append(UserInfoClass.getInstance().userInfo.get("u_id")).append("&")
	            	  .append("deviceuid=").append(deviceId); 
    	           
    	            
    	            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "utf-8"); 
    	            PrintWriter writer = new PrintWriter(outStream); 
    	            writer.write(buffer.toString()); 
    	            writer.flush(); 
    			
    	          
    	            http.getInputStream();

        			} catch (Exception e) {      
        				e.printStackTrace();        
        				}            
        		  
        		return completeString;         
    		}       
    	
    	@Override    
    	protected void onPostExecute(String completeString) {   
    		final String PREF_FILE_NAME = "UserInfo";
    		SharedPreferences pref = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
    		SharedPreferences.Editor prefEditor = pref.edit();
			prefEditor.clear();
			prefEditor.commit();
    		
    		Intent intent = new Intent(Setting2_Account.this, Launch.class);
			startActivity(intent);
			finish();
			
    		mProgressDialog.dismiss();      

    	}
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
	
}