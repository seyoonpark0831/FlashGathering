package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AuthSend extends Activity {
	
	private static final String CRYPTO_SEED_PASSWORD = "abcdefghijuklmno0123456789012345";
	
	private EditText numText1, numText2, numText3;
	private Button authSendButton;
	public Map<String, String> authInfo;
	public String authCaseFromServer;
	
	public StringBuffer buffer;
	

	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.auth_send);
	    setTitle("휴대번호 인증");
	    
	    numText1 = (EditText)findViewById(R.id.numText1);
	    numText2 = (EditText)findViewById(R.id.numText2);
	    numText3 = (EditText)findViewById(R.id.numText3);
	    authSendButton = (Button)findViewById(R.id.authSendButton);

	    numText1.addTextChangedListener(new GenericTextWatcher(numText1));
	    numText2.addTextChangedListener(new GenericTextWatcher(numText2));
	    
	    //C2DMTestActivity에서 넘어온 pid를 받아 전역 class에 기록
	    authInfo = new HashMap<String, String>();
	    authInfo.put("auth_pid", getIntent().getStringExtra("pid"));
//	    Toast.makeText(AuthSend.this, "pid : "+authInfo.get("auth_pid"), 0).show();
	    
	    authSendButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//입력 자릿수 검사
				if(digitCheck()) {
					//네트워크에 연결되어 있는지 확인
					if(isOnline()) {
						String numSeg1 = numText1.getText().toString();
						String numSeg2 = numText2.getText().toString();
						String numSeg3 = numText3.getText().toString();
						StringBuffer phoneNumBuffer = new StringBuffer();
						String phoneNum = new String();
						
						//전송할 폰 번호와 기기식별자를 준비
						phoneNum = phoneNumBuffer.append(numSeg1).append(numSeg2).append(numSeg3).toString();
						String deviceId = GetDeviceId();
						String phoneNumEncrypted = new String();
						String phoneNumEncryptedEncoded = new String();
						
						try{
							phoneNumEncrypted = SimpleCrypto2.encrypt1(CRYPTO_SEED_PASSWORD, phoneNum);
							phoneNumEncryptedEncoded = URLEncoder.encode(phoneNumEncrypted, "UTF-8");
						}catch(Exception e){
						}
						
						//전송할 buffer data 생성
						buffer = new StringBuffer();
						buffer.append("phonenum").append("=").append(phoneNumEncryptedEncoded).append("&")
							  .append("udid").append("=").append(deviceId);
						
						
						//서버로 전송
			        	AuthSendParserTask task = new AuthSendParserTask(AuthSend.this);
			        	task.execute(BungaeActivity.MAIN_URL + "/auth_views_php/authRequest-R.php");
			            
						
						//후속 action: 파싱 메서드 내의 postExecute에서 실행
						
//			        	//auth_case에 따른 dialog 처리
//			    		showAuthAlert(authCaseFromServer);
						
					}
					
					else {
						AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
						builder.setTitle("네트워크 오류")
						.setMessage("네트워크 연결을 확인한 후 다시 시도해 주세요.")
						.setCancelable(true)
						.setNeutralButton("확인", null)
						.create().show();
					}
				} //if(digitCheck())
				
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
					builder.setTitle("잘못된 번호")
					.setMessage("올바른 번호를 입력해 주세요.")
					.setCancelable(true)
					.setNeutralButton("확인", null)
					.create().show();
				}
				
			}
		});
	    
	}
	
	public boolean digitCheck() {
		if(numText1.getText().length()==3 && 
				(numText2.getText().length()+numText3.getText().length()==7 || 
				 numText2.getText().length()+numText3.getText().length()==8))
			return true;
		else return false;
	}
	
	public void showAuthAlert(String authCase) {
		
		//auth_case==0or3인 경우 전송 완료 다이얼로그 띄우고 다음 액티비티로 넘어가기
		if(authCase.equals("0") || authCase.equals("3")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
			builder.setTitle("인증번호 전송")
			.setMessage("인증번호가 전송되었습니다.")
			.setCancelable(true)
			.setNeutralButton("다음 단계", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					
					Intent intent = new Intent(AuthSend.this, AuthConfirm.class);
					startActivity(intent);
					finish();
				}
			})
			.create().show();
		}
		
		else if(authCase.equals("1")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
			builder.setTitle("등록된 번호")
			.setMessage("이미 등록된 번호입니다. 한 번호로 하나의 계정만 이용 가능합니다.")
			.setCancelable(true)
			.setNeutralButton("확인", null)
			.create().show();
		}
		
		else if(authCase.equals("2") || authCase.equals("4")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
			builder.setTitle("등록된 기기")
			.setMessage("이미 등록된 기기입니다. 한 기기로 하나의 계정만 이용 가능합니다.")
			.setCancelable(true)
			.setNeutralButton("확인", null)
			.create().show();
		}
		
		else if(authCase == null || authCase.equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
			builder.setTitle("네트워크 오류")
			.setMessage("네트워크 오류입니다. 다시 시도해 주세요.")
			.setCancelable(true)
			.setNeutralButton("확인", null)
			.create().show();
		}
		
	}
	
	private class GenericTextWatcher implements TextWatcher{

	    private View view;
	    private GenericTextWatcher(View view) {
	        this.view = view;
	    }

	    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	    public void afterTextChanged(Editable editable) {
	        switch(view.getId()){
	            case R.id.numText1:
	            	if(numText1.getText().length()==3)
	            		numText2.requestFocus();
	                break;
	            case R.id.numText2:
	            	if(numText2.getText().length()==4)
	            		numText3.requestFocus();
	                break;
	            default:
	            	break;
	        }
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
	
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    else return false;
	}
	
	
	public class AuthSendParserTask extends AsyncTask<String, Integer, String>{  
    	private AuthSend mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	private boolean boolean_auth_num = false,
						boolean_auth_phone = false,
						boolean_auth_udid = false,
						boolean_auth_case = false;
    	
    	public AuthSendParserTask(AuthSend activity) {  
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
    		
    		authCaseFromServer = new String();
    		authCaseFromServer = AuthInfoClass.getInstance().authInfo.get("auth_case");
    		mProgressDialog.dismiss();
    		
    		//파싱 후속처리
    		//auth_case에 따른 dialog 처리
    		showAuthAlert(authCaseFromServer);
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
    					
//    					if(tag.equals("auth_form"))		authInfo = new HashMap<String, String>();
    					
    					if(tag.equals("auth_num"))		boolean_auth_num = true;
     					if(tag.equals("auth_phone"))	boolean_auth_phone = true;
     					if(tag.equals("auth_udid"))		boolean_auth_udid = true;
     					if(tag.equals("auth_case"))		boolean_auth_case = true;
     					    						                                   
    					break;
    					
    				case XmlPullParser.TEXT:
    					
    					if(boolean_auth_num){
     						authInfo.put("auth_num",parser.getText());
     						boolean_auth_num = false;
     					}
     					if(boolean_auth_phone){
     						authInfo.put("auth_phone",parser.getText());
     						boolean_auth_phone = false;
     					}
     					if(boolean_auth_udid){
     						authInfo.put("auth_udid",parser.getText());
     						boolean_auth_udid = false;
     					}
     					if(boolean_auth_case){
     						authInfo.put("auth_case",parser.getText());
     						boolean_auth_case = false;
     					}
     					
    					break;
    					
    					
					case XmlPullParser.END_TAG:  
						
						tag = parser.getName();
						if (tag.equalsIgnoreCase("auth_form") && !authInfo.isEmpty()){
							AuthInfoClass.getInstance().authInfo = authInfo;
							completeString = "complete";
						}

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
