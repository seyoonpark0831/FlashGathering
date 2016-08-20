package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.daum.mobilead.AdConfig;
import net.daum.mobilead.AdHttpListener;
import net.daum.mobilead.MobileAdView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

public class BungaeActivity extends TabActivity implements AdHttpListener {

	//BungaeActivity.java�� ����

//	public static final String MAIN_URL = "http://www.hunect.com/testphp";
	public static final String MAIN_URL = "http://www.hunect.com/bungae_1_1";
	public static final String APNS_URL = "http://www.hunect.com/apnsphp";
	
	private TabHost mTabHost;
	
	private String newAge;
	private Map<String, String> userInfoBuffer;

	private MobileAdView adView = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initAdam();
        
        setTitle("���ϰ� ����");

        mTabHost=getTabHost();

        
        mTabHost.addTab(mTabHost.newTabSpec("tab1")
        		.setContent(new Intent().setClass(this, BungaeListActivity.class))
        		.setIndicator("����", getResources().getDrawable(R.drawable.ic_tab_bungae)));
        
        mTabHost.addTab(mTabHost.newTabSpec("tab2")
        		.setContent(new Intent().setClass(this, MyBungaeListActivity.class))
        		.setIndicator("�� ����", getResources().getDrawable(R.drawable.ic_tab_mybungae)));
        
        mTabHost.addTab(mTabHost.newTabSpec("tab3")
        		.setIndicator("������", getResources().getDrawable(R.drawable.ic_tab_ing))
        		.setContent(new Intent().setClass(this, FixedBungaeActivity.class)
                ));
        
        mTabHost.addTab(mTabHost.newTabSpec("tab4")
        		.setIndicator("����", getResources().getDrawable(R.drawable.ic_tab_setting))
        		.setContent(new Intent().setClass(this, SettingActivity.class)
                ));
        
        if(UserInfoClass.getInstance().userInfo.get("u_age").length() == 1) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(BungaeActivity.this);
			builder.setTitle("���� �߰�")
		    .setMessage("���� ���� ������ ���̰� �߸� �ԷµǾ����ϴ�. ���̸� �����մϴ�.")
		    .setCancelable(true)
		    .setNeutralButton("Ȯ��", new DialogInterface.OnClickListener(){
		    	@Override
				public void onClick(DialogInterface dialog, int whichButton) {
		    		modifyAge();
				}
		    })
		    .create().show();
        }
        
        
    }
    
    @Override
	public void onDestroy() {
        super.onDestroy();     
        
        if( adView != null ) {
        	adView.destroy();
            adView = null;
        }
    }
	
	
	@SuppressWarnings("deprecation")
	private void initAdam() {
		
        // �Ҵ� ���� clientId ����
        AdConfig.setClientId("1c3aZ5ZT134b0fed431");
        
        // Ad@m sdk �ʱ�ȭ ����
        adView = (MobileAdView)findViewById(R.id.adview);
    	adView.setRequestInterval(15);
    	adView.setAdListener(this);
       	adView.setVisibility(View.VISIBLE);
	}
   
	@Override
	public void failedDownloadAd_AdListener(int errorno, String errMsg) {
		// fail to receive Ad
		Log.d("AdSample", errorno +":"+ errMsg);
		
	}

	
	@Override
	public void didDownloadAd_AdListener() {
	   // success to receive Ad
		
	}
    
    private void modifyAge()
	{
		LayoutInflater inflator = LayoutInflater.from(this);
    	View addView2 = inflator.inflate(R.layout.password_dialog, null);
    	
    	final EditText passwordEdit;
    	 
    	
    	passwordEdit = (EditText)addView2.findViewById(R.id.passwordEdit);
    	passwordEdit.setHint("���̸� �Է��ϼ���");
    	
    	AlertDialog.Builder alert3 =  new AlertDialog.Builder(this);
    	alert3.setTitle("���� ����");
    	alert3.setView(addView2);
    	
    	alert3.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() 
    	{	
    		
			@Override
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				if(passwordEdit.getText().toString().length()!=2) {
					modifyAge();
					Toast.makeText(BungaeActivity.this, "�� �ڸ����� ���̸� �ùٸ��� �Է��� �ּ���.", 0).show();
				}
				
				else if(Integer.parseInt(passwordEdit.getText().toString())<15) {
					modifyAge();
					Toast.makeText(BungaeActivity.this, "15�� �̻� �Է��Ͻ� �� �ֽ��ϴ�.", 0).show();
				}
				
				else {
					newAge = new String();
					newAge = passwordEdit.getText().toString();
					
					ModifyAgeParserTask task = new ModifyAgeParserTask(BungaeActivity.this);
					task.execute(BungaeActivity.MAIN_URL + "/updateage_android.php");
				}
				
			}
		});
    	alert3.setNegativeButton("���",  new DialogInterface.OnClickListener(){
	    	@Override
			public void onClick(DialogInterface dialog, int whichButton) {
	    		AlertDialog.Builder builder = new AlertDialog.Builder(BungaeActivity.this);
				builder.setTitle("����")
			    .setMessage("������ �����մϴ�.")
			    .setCancelable(true)
			    .setNeutralButton("Ȯ��", new DialogInterface.OnClickListener(){
			    	@Override
					public void onClick(DialogInterface dialog, int whichButton) {
			    		finish();
					}
			    })
			    .create().show();
			}
	    });
    	alert3.show();
	}
    
    

	public class ModifyAgeParserTask extends AsyncTask<String, Integer, String>{  
    	private BungaeActivity mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	private boolean boolean_u_num = false,
						boolean_u_id = false,
						boolean_u_phone = false,
						boolean_u_sex = false,
						boolean_u_age = false,
						boolean_u_intro = false,
						boolean_u_push_id = false;
    	
    	public ModifyAgeParserTask(BungaeActivity activity) {  
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
	            
	            //Log.d("test","SENDING DATA, newAge = "+newAge);
	            buffer.append("u_age=").append(newAge)
	            	  .append("&u_num=").append(UserInfoClass.getInstance().userInfo.get("u_num"));
	            Log.d("test","SENDING DATA : "+buffer.toString());
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
    		
    		Boolean didSuccessCommit = false;
    		
    		//pref�� �ٽ� ���
    		if(!userInfoBuffer.isEmpty()) {
    			final String PREF_FILE_NAME = "UserInfo";
    			SharedPreferences pref = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
    			SharedPreferences.Editor prefEditor = pref.edit();
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
    			
    			if(UserInfoClass.getInstance().userInfo.get("u_age").length() == 2) {
    				Toast.makeText(BungaeActivity.this, "���� ������ ���������� �����Ǿ����ϴ�.", 0).show();
    			}
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