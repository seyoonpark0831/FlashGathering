package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AuthConfirm extends Activity {

	
	private String caseFromServer;
	private EditText inputText;
	private Button authConfirmButton;
	private Button authConfirmBackButton;
	
	public StringBuffer authBuffer, infoBuffer;
	
	private String auth_pass;
	private Map<String, String> userInfoBuffer;
	
	private boolean parsingForPassFlag = false,
					parsingForUserInfo = false;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.auth_confirm);
	    setTitle("휴대번호 인증");
	    caseFromServer = new String();
	    caseFromServer = AuthInfoClass.getInstance().authInfo.get("auth_case");
	    
	    inputText = (EditText)findViewById(R.id.authConfirmText);
	    authConfirmButton = (Button)findViewById(R.id.authConfirmButton);
	    authConfirmBackButton = (Button)findViewById(R.id.authConfirmBackButton);
	    
	    //확인 클릭시 사용자가 입력한 auth_key를 auth_num과 함께 전송 >> auth_pass를 파싱받음
	    authConfirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(inputText.getText().length() != 6) {
					AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
					builder.setTitle("잘못된 인증키")
					.setMessage("인증키 6자리를 정확히 입력해 주세요.")
					.setCancelable(true)
					.setNeutralButton("확인", null)
					.create().show();
				}
				
				else if(isOnline()){
					
					authBuffer = new StringBuffer();
					authBuffer.append("auth_num=").append(AuthInfoClass.getInstance().authInfo.get("auth_num")).append("&")
						  .append("auth_key=").append(inputText.getText().toString());
					
					
					//서버로 전송
					parsingForPassFlag = true;
					parsingForUserInfo = false;
					AuthConfirmParserTask task = new AuthConfirmParserTask(AuthConfirm.this);
					task.execute(BungaeActivity.MAIN_URL + "/auth_views_php/authConfirm.php");
					
					//후속 action: 파싱 메서드 내의 postExecute에서 실행
					//showConfirmAlert
					
				}
				
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
					builder.setTitle("네트워크 오류")
					.setMessage("네트워크 연결을 확인한 후 다시 시도해 주세요.")
					.setCancelable(true)
					.setNeutralButton("확인", null)
					.create().show();
				}
				
			}
		});
	    
	    
	    authConfirmBackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
				builder.setTitle("인증키 재전송")
				.setMessage("인증키가 전송되지 않은 경우 이전으로 돌아가 인증과정을 다시 시작합니다.")
				.setCancelable(true)
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(AuthConfirm.this, C2DMTestActivity.class);
						startActivity(intent);
						finish();
					}
				})
				.create().show();
			}
		});

	}
	
	public void showConfirmAlert() {
		
		if(auth_pass.equals("1")){
			
			if(caseFromServer.equals("0")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
				builder.setTitle("인증 성공")
				.setMessage("휴대번호 인증에 성공하였습니다.")
				.setCancelable(true)
				.setNeutralButton("다음 단계", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						//Register activity 로드
						Intent intent = new Intent(AuthConfirm.this, Register.class);
						startActivity(intent);
						finish();
					}
				})
				.create().show();
			}
			
			else if(caseFromServer.equals("3")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
				builder.setTitle("인증 성공")
				.setMessage("휴대번호 인증에 성공하였습니다.")
				.setCancelable(true)
				.setNeutralButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
						builder.setTitle("계정 발견")
						.setMessage("이전에 가입한 게정을 발견했습니다. 해당 정보로 로그인합니다.")
						.setCancelable(true)
						.setNeutralButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								//기존 가입자 정보를 불러와 메인으로
								logOnWithExistingUserInfo1();
							}
						})
						.create().show();
					}
				})
				.create().show();
			}
			
		}
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
			builder.setTitle("인증 실패")
			.setMessage("인증키가 틀렸습니다.")
			.setCancelable(true)
			.setNeutralButton("재시도", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					inputText.setText("");
				}
			})
			.create().show();
		}
		
	}
	
	public void logOnWithExistingUserInfo1() {
		
		//전송할 번호 인코딩
		String phoneNumEncoded = new String();
		try {
			phoneNumEncoded = URLEncoder.encode(AuthInfoClass.getInstance().authInfo.get("auth_phone"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		infoBuffer = new StringBuffer();
		infoBuffer.append("u_phone=").append(phoneNumEncoded);
		
		//휴대폰 번호를 보내 해당 계정 정보를 받아옴
		parsingForPassFlag = false;
		parsingForUserInfo = true;
		AuthConfirmParserTask task = new AuthConfirmParserTask(AuthConfirm.this);
		task.execute(BungaeActivity.MAIN_URL + "/auth_views_php/userinfo-p.php");
	}
	
	public void logOnWithExistingUserInfo2() {
		
		//userInfoBuffer에 계정 정보가 파싱된 후 처리
		//preference에 기록
		boolean didSuccessCommit = false;
		if(!userInfoBuffer.isEmpty()) {
			final String PREF_FILE_NAME = "UserInfo";
			SharedPreferences pref = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
			SharedPreferences.Editor prefEditor = pref.edit();
			prefEditor.putString("grow_old", "2012");
			prefEditor.putString("u_num", userInfoBuffer.get("u_num"));
			prefEditor.putString("u_id", userInfoBuffer.get("u_id"));
			prefEditor.putString("u_phone", userInfoBuffer.get("u_phone"));
			prefEditor.putString("u_sex", userInfoBuffer.get("u_sex"));
			prefEditor.putString("u_age", userInfoBuffer.get("u_age"));
			prefEditor.putString("u_intro", userInfoBuffer.get("u_intro"));
			prefEditor.putString("u_push_id", userInfoBuffer.get("u_push_id"));
			prefEditor.putString("u_push_state", "1");
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
//			Toast.makeText(AuthConfirm.this, UserInfoClass.getInstance().userInfo.get("u_id"), Toast.LENGTH_LONG).show();
		}
		
		//class 기록이 되면 가입이 정상적으로 이루어진것으로 간주
		if(!(UserInfoClass.getInstance().userInfo.get("u_id").equals(""))) {
			
			AlertDialog.Builder builder2 = new AlertDialog.Builder(AuthConfirm.this);
			builder2.setTitle("로그인 성공")
			.setMessage("성공적으로 로그인되었습니다. 메인으로 이동합니다.")
			.setCancelable(true)
			.setNeutralButton("확인", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					
//					Toast.makeText(AuthConfirm.this, "로그인 아이디 : "+UserInfoClass.getInstance().userInfo.get("u_id"), Toast.LENGTH_LONG).show();
					//메인 로드
					Intent intent = new Intent(AuthConfirm.this, BungaeActivity.class);
					startActivity(intent);
					finish();
				}
			})
			.create().show();
		}
		else {
			AlertDialog.Builder builder2 = new AlertDialog.Builder(AuthConfirm.this);
			builder2.setTitle("로그인 오류")
			.setMessage("로그인 중 오류가 발생했습니다. 다시 시도해 주세요.")
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

	public class AuthConfirmParserTask extends AsyncTask<String, Integer, String>{  
    	private AuthConfirm mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	private boolean boolean_u_num = false,
						boolean_u_id = false,
						boolean_u_phone = false,
						boolean_u_sex = false,
						boolean_u_age = false,
						boolean_u_intro = false,
						boolean_u_push_id = false,
						boolean_auth_pass = false;
    	
    	public AuthConfirmParserTask(AuthConfirm activity) {  
    		mActivity = activity;     
    	}

    	@Override    
    	protected void onPreExecute() {
    		
    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("전송중...");     
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
	            if(parsingForPassFlag && !parsingForUserInfo) buffer = authBuffer;
	            if(!parsingForPassFlag && parsingForUserInfo) buffer = infoBuffer;
	            
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
    		
    		mProgressDialog.dismiss();
    		
    		//파싱 후속 처리
    		if(parsingForPassFlag && !parsingForUserInfo) {
    			showConfirmAlert();
    			parsingForPassFlag = false;
    		}
    		if(!parsingForPassFlag && parsingForUserInfo) {
    			logOnWithExistingUserInfo2();
    			parsingForUserInfo = false;
    		}
   		}         
    	
    	
    	public String parseXml(InputStream is) throws IOException, XmlPullParserException {    
    		XmlPullParser parser = Xml.newPullParser();     
    		try {
    			parser.setInput(is, null);      
    			int eventType = parser.getEventType();
    			while (eventType != XmlPullParser.END_DOCUMENT) {   
    				String tag = null;     
    				switch (eventType) {    
    				case XmlPullParser.START_TAG:        
    					tag = parser.getName(); 
    					
    					if(parsingForPassFlag && !parsingForUserInfo) {
    						
    						if(tag.equals("auth_form"))		auth_pass = new String();
    					
    						if(tag.equals("auth_pass"))		boolean_auth_pass = true;
    					}
    					
    					if(!parsingForPassFlag && parsingForUserInfo) {
    						
    						if(tag.equals("user_info")) userInfoBuffer = new HashMap<String, String>();
    	  					
    	  					if(tag.equals("u_num"))		boolean_u_num = true;
    	  					if(tag.equals("u_id"))		boolean_u_id = true;
    	  					if(tag.equals("u_phone"))	boolean_u_phone = true;
    	  					if(tag.equals("u_sex"))		boolean_u_sex = true;
    	  					if(tag.equals("u_age"))		boolean_u_age = true;
    	  					if(tag.equals("u_intro"))	boolean_u_intro = true;
    	  					if(tag.equals("u_push_id"))	boolean_u_push_id = true;
    					}
    					
     					    						                                   
    					break;
    					
    				case XmlPullParser.TEXT:
    					
    					if(parsingForPassFlag && !parsingForUserInfo) {
    						
    						if(boolean_auth_pass){
	     						auth_pass = parser.getText();
	     						boolean_auth_pass = false;
    						}
    					}
    					
    					if(!parsingForPassFlag && parsingForUserInfo) {
    						
    						if(boolean_u_num){
    	  						userInfoBuffer.put("u_num", parser.getText());
    	  						boolean_u_num = false;
    	  					}
    	  					if(boolean_u_id){
    	  						userInfoBuffer.put("u_id", parser.getText());
    	  						boolean_u_id = false;
    	  					}
    	  					if(boolean_u_phone){
    	  						userInfoBuffer.put("u_phone", parser.getText());
    	  						boolean_u_phone = false;
    	  					}
    	  					if(boolean_u_sex){
    	  						userInfoBuffer.put("u_sex", parser.getText());
    	  						boolean_u_sex = false;
    	  					}
    	  					if(boolean_u_age){
    	  						userInfoBuffer.put("u_age", parser.getText());
    	  						boolean_u_age = false;
    	  					}
    	  					if(boolean_u_intro){
    	  						userInfoBuffer.put("u_intro", parser.getText());
    	  						boolean_u_intro = false;
    	  					}
    	  					if(boolean_u_push_id){
    	  						userInfoBuffer.put("u_push_id", parser.getText());
    	  						boolean_u_push_id = false;
    	  					}
    					}
    					
    					break;
    					
    					
					case XmlPullParser.END_TAG:  
						tag = parser.getName();
						
						if(parsingForPassFlag && !parsingForUserInfo)
							if(tag.equalsIgnoreCase("auth_form") && auth_pass != null)
								completeString = "complete";
						
						if(!parsingForPassFlag && parsingForUserInfo)
							if (tag.equalsIgnoreCase("user_info") && !userInfoBuffer.isEmpty())
								completeString = "complete";
						
						break;
    				} //switch
    				eventType = parser.next();           
    			} //while

    		} catch (Exception e) {     
    			e.printStackTrace();    
    		}
    		return completeString;
    	}
    }

}
