package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Xml;

public class Launch extends Activity {

	private SharedPreferences pref;
	
	private String userIdForAgeUpdate;
	public Map<String, String> userInfoBuffer;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.launch);
	    setTitle("���ϰ� ����");
	    
	    pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
	    
	    Handler h = new Handler();
	    h.postDelayed(new splashhandler(), 2000);
	}
	
	class splashhandler implements Runnable {
    	public void run() {
    		
		    if(pref.getString("u_id", null) == null) {
//    			    	Toast.makeText(Launch.this, "UserInfo not found", Toast.LENGTH_LONG).show();
		    	
		    	Intent intent = new Intent(Launch.this, C2DMTestActivity.class);
				startActivity(intent);
				finish();
		    }
		    else {
		    	//preference�� �߰��ϸ� "grow_old"�� �ִ��� �˻�, ������ userInfo�� �ٽ� �޾ƿ��� pref�� �ٽ� ���
		    	if(!pref.contains("grow_old")) {
		    		userIdForAgeUpdate = new String();
		    		userIdForAgeUpdate = pref.getString("u_id", null);
		    		RegisterParserTask task = new RegisterParserTask(Launch.this);
		    		task.execute(BungaeActivity.MAIN_URL + "/auth_views_php/new_year_age_android.php");
		    	}
		    	
		    	else {
		    		//�ش� ������ �о� ���� activity���� �� class�� ���
			    	UserInfoClass.getInstance().userInfo = new HashMap<String, String>();
					UserInfoClass.getInstance().userInfo.put("u_num", pref.getString("u_num", null));
					UserInfoClass.getInstance().userInfo.put("u_id", pref.getString("u_id", null));
					UserInfoClass.getInstance().userInfo.put("u_phone", pref.getString("u_phone", null));
					UserInfoClass.getInstance().userInfo.put("u_sex", pref.getString("u_sex", null));
					UserInfoClass.getInstance().userInfo.put("u_age", pref.getString("u_age", null));
					UserInfoClass.getInstance().userInfo.put("u_intro", pref.getString("u_intro", null));
					UserInfoClass.getInstance().userInfo.put("u_push_id", pref.getString("u_push_id", null));
					UserInfoClass.getInstance().userInfo.put("u_push_state", pref.getString("u_push_state", null));
					
			    	//Toast.makeText(Launch.this, "�ڵ� �α��� : "+UserInfoClass.getInstance().userInfo.get("u_id"), Toast.LENGTH_LONG).show();
			    	
			    	Intent intent = new Intent(Launch.this, BungaeActivity.class);
					startActivity(intent);
					finish();
		    	}
		    	
		    }
    	}
    }
	
	
	public void updatePreference() {
		//preference�� ���
		boolean didSuccessCommit = false;
		if(!userInfoBuffer.isEmpty()) {
			final String PREF_FILE_NAME = "UserInfo";
//			Toast.makeText(Launch.this, "pid(userInfoBuffer) : "+userInfoBuffer.get("u_push_id"), Toast.LENGTH_LONG).show();
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
//			Toast.makeText(Launch.this, UserInfoClass.getInstance().userInfo.get("u_id"), Toast.LENGTH_LONG).show();
		}
		
		if(!(UserInfoClass.getInstance().userInfo.get("u_id").equals(""))) {
			Intent intent = new Intent(Launch.this, BungaeActivity.class);
			startActivity(intent);
			finish();
		}
		else {
			AlertDialog.Builder builder2 = new AlertDialog.Builder(Launch.this);
			builder2.setTitle("��Ʈ��ũ ����")
			.setMessage("���� ���� ���� �� ������ �߻��߽��ϴ�. ���� �ٽ� ������ �ּ���.")
			.setCancelable(true)
			.setNeutralButton("Ȯ��", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					finish();
				}
			})
			.create().show();
		}
	}
	
	
	
	public class RegisterParserTask extends AsyncTask<String, Integer, String>{  
    	private Launch mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	private boolean boolean_u_num = false,
						boolean_u_id = false,
						boolean_u_phone = false,
						boolean_u_sex = false,
						boolean_u_age = false,
						boolean_u_intro = false,
						boolean_u_push_id = false;
    	
    	public RegisterParserTask(Launch activity) {  
    		mActivity = activity;     
    	}

    	@Override    
    	protected void onPreExecute() {
    		
    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("������Ʈ��...");     
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
	            buffer.append("u_id=").append(userIdForAgeUpdate);
	            
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
    		
    		//�Ľ� ��� ó��
    		updatePreference();
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
    					
    					if(tag.equals("user_info")) userInfoBuffer = new HashMap<String, String>();
	  					
	  					if(tag.equals("u_num"))		boolean_u_num = true;
	  					if(tag.equals("u_id"))		boolean_u_id = true;
	  					if(tag.equals("u_phone"))	boolean_u_phone = true;
	  					if(tag.equals("u_sex"))		boolean_u_sex = true;
	  					if(tag.equals("u_age"))		boolean_u_age = true;
	  					if(tag.equals("u_intro"))	boolean_u_intro = true;
	  					if(tag.equals("u_push_id"))	boolean_u_push_id = true;
     					    						                                   
    					break;
    					
    				case XmlPullParser.TEXT:
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
    					
    					
    					break;
    					
    					
					case XmlPullParser.END_TAG:  
						
						tag = parser.getName();
						
						if (tag.equalsIgnoreCase("user_info") && !userInfoBuffer.isEmpty()){
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
