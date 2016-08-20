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
	    setTitle("�޴��ȣ ����");
	    caseFromServer = new String();
	    caseFromServer = AuthInfoClass.getInstance().authInfo.get("auth_case");
	    
	    inputText = (EditText)findViewById(R.id.authConfirmText);
	    authConfirmButton = (Button)findViewById(R.id.authConfirmButton);
	    authConfirmBackButton = (Button)findViewById(R.id.authConfirmBackButton);
	    
	    //Ȯ�� Ŭ���� ����ڰ� �Է��� auth_key�� auth_num�� �Բ� ���� >> auth_pass�� �Ľ̹���
	    authConfirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(inputText.getText().length() != 6) {
					AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
					builder.setTitle("�߸��� ����Ű")
					.setMessage("����Ű 6�ڸ��� ��Ȯ�� �Է��� �ּ���.")
					.setCancelable(true)
					.setNeutralButton("Ȯ��", null)
					.create().show();
				}
				
				else if(isOnline()){
					
					authBuffer = new StringBuffer();
					authBuffer.append("auth_num=").append(AuthInfoClass.getInstance().authInfo.get("auth_num")).append("&")
						  .append("auth_key=").append(inputText.getText().toString());
					
					
					//������ ����
					parsingForPassFlag = true;
					parsingForUserInfo = false;
					AuthConfirmParserTask task = new AuthConfirmParserTask(AuthConfirm.this);
					task.execute(BungaeActivity.MAIN_URL + "/auth_views_php/authConfirm.php");
					
					//�ļ� action: �Ľ� �޼��� ���� postExecute���� ����
					//showConfirmAlert
					
				}
				
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
					builder.setTitle("��Ʈ��ũ ����")
					.setMessage("��Ʈ��ũ ������ Ȯ���� �� �ٽ� �õ��� �ּ���.")
					.setCancelable(true)
					.setNeutralButton("Ȯ��", null)
					.create().show();
				}
				
			}
		});
	    
	    
	    authConfirmBackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
				builder.setTitle("����Ű ������")
				.setMessage("����Ű�� ���۵��� ���� ��� �������� ���ư� ���������� �ٽ� �����մϴ�.")
				.setCancelable(true)
				.setNegativeButton("���", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
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
				builder.setTitle("���� ����")
				.setMessage("�޴��ȣ ������ �����Ͽ����ϴ�.")
				.setCancelable(true)
				.setNeutralButton("���� �ܰ�", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						//Register activity �ε�
						Intent intent = new Intent(AuthConfirm.this, Register.class);
						startActivity(intent);
						finish();
					}
				})
				.create().show();
			}
			
			else if(caseFromServer.equals("3")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
				builder.setTitle("���� ����")
				.setMessage("�޴��ȣ ������ �����Ͽ����ϴ�.")
				.setCancelable(true)
				.setNeutralButton("Ȯ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						AlertDialog.Builder builder = new AlertDialog.Builder(AuthConfirm.this);
						builder.setTitle("���� �߰�")
						.setMessage("������ ������ ������ �߰��߽��ϴ�. �ش� ������ �α����մϴ�.")
						.setCancelable(true)
						.setNeutralButton("Ȯ��", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								//���� ������ ������ �ҷ��� ��������
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
			builder.setTitle("���� ����")
			.setMessage("����Ű�� Ʋ�Ƚ��ϴ�.")
			.setCancelable(true)
			.setNeutralButton("��õ�", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					inputText.setText("");
				}
			})
			.create().show();
		}
		
	}
	
	public void logOnWithExistingUserInfo1() {
		
		//������ ��ȣ ���ڵ�
		String phoneNumEncoded = new String();
		try {
			phoneNumEncoded = URLEncoder.encode(AuthInfoClass.getInstance().authInfo.get("auth_phone"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		infoBuffer = new StringBuffer();
		infoBuffer.append("u_phone=").append(phoneNumEncoded);
		
		//�޴��� ��ȣ�� ���� �ش� ���� ������ �޾ƿ�
		parsingForPassFlag = false;
		parsingForUserInfo = true;
		AuthConfirmParserTask task = new AuthConfirmParserTask(AuthConfirm.this);
		task.execute(BungaeActivity.MAIN_URL + "/auth_views_php/userinfo-p.php");
	}
	
	public void logOnWithExistingUserInfo2() {
		
		//userInfoBuffer�� ���� ������ �Ľ̵� �� ó��
		//preference�� ���
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
		
		//�ٸ� ��Ƽ��Ƽ���� �޾� �� class���
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
		
		//class ����� �Ǹ� ������ ���������� �̷���������� ����
		if(!(UserInfoClass.getInstance().userInfo.get("u_id").equals(""))) {
			
			AlertDialog.Builder builder2 = new AlertDialog.Builder(AuthConfirm.this);
			builder2.setTitle("�α��� ����")
			.setMessage("���������� �α��εǾ����ϴ�. �������� �̵��մϴ�.")
			.setCancelable(true)
			.setNeutralButton("Ȯ��", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					
//					Toast.makeText(AuthConfirm.this, "�α��� ���̵� : "+UserInfoClass.getInstance().userInfo.get("u_id"), Toast.LENGTH_LONG).show();
					//���� �ε�
					Intent intent = new Intent(AuthConfirm.this, BungaeActivity.class);
					startActivity(intent);
					finish();
				}
			})
			.create().show();
		}
		else {
			AlertDialog.Builder builder2 = new AlertDialog.Builder(AuthConfirm.this);
			builder2.setTitle("�α��� ����")
			.setMessage("�α��� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���.")
			.setCancelable(true)
			.setNeutralButton("Ȯ��", null)
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
    		mProgressDialog.setMessage("������...");     
    		mProgressDialog.show();      
    	}
    	
    	@Override    
    	protected String doInBackground(String... params) {   
    		//BungaeListAdapter result = null;          
    		try {               
    			
    			URL url = new URL(params[0]);  
    			
    			 HttpURLConnection http = (HttpURLConnection) url.openConnection();   // ���� 
	            //-------------------------- 
	            //   ���� ��� ���� - �⺻���� �����̴� 
	            //-------------------------- 
	            http.setDefaultUseCaches(false);                                            
	            http.setDoInput(true);                         // �������� �б� ��� ���� 
	            http.setDoOutput(true);                       // ������ ���� ��� ����  
	            http.setRequestMethod("POST");         // ���� ����� POST 

	            // �������� ������ <Form>���� ���� �Ѿ�� �Ͱ� ���� ������� ó���϶�� �� �˷��ش� 
	            http.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
	            //-------------------------- 
	            //   ������ �� ���� 
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
    		
    		//�Ľ� �ļ� ó��
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
