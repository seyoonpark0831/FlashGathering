package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import net.daum.mobilead.AdConfig;
import net.daum.mobilead.AdHttpListener;
import net.daum.mobilead.MobileAdView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;

public class FixedBungaeHostActivity extends Activity implements AdHttpListener {
	
	private MobileAdView adView = null;
	
	private TextView UserIdText;
	private TextView UserPhoneText;
	private TextView UserSexText;
	private TextView UserAgeText;
	private TextView UserIntroText;
	
	private String hostId;
	
	private static final String CRYPTO_SEED_PASSWORD = "abcdefghijuklmno0123456789012345";
	
	ArrayList<FixedAccountData> UserAccount = null;
	 
	FixedAccountData userAccount = null;
    
	boolean boolean_u_num = false,
			boolean_u_id = false,
			boolean_u_phone = false,
			boolean_u_sex = false,
			boolean_u_age = false,
			boolean_u_intro = false;
	//FixedBungaeHostActivity.java

	private static final String BUNGAE_USER_URL = BungaeActivity.MAIN_URL + "/hostcall_fixed.php";
//	private static final String BUNGAE_USER_URL = "http://www.hunect.com/testphp/hostcall_fixed.php"; 
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.fixed_bungae_host);
	    setTitle("������ ����");
	    // TODO Auto-generated method stub
	    
	    initAdam();
	    
	    hostId = new String();
	    
	    hostId = getIntent().getStringExtra("HostId"); //����Ʈ�� key���� ���� �ش� String�� �޴´�.
	    
//	    Toast.makeText(this, hostId, Toast.LENGTH_LONG).show();
	    
	    UserIdText = (TextView)findViewById(R.id.fhcategoryLabel);
	    UserPhoneText = (TextView)findViewById(R.id.fhphoneLabel);
	    UserSexText = (TextView)findViewById(R.id.fhtitleLabel);
	    UserAgeText = (TextView)findViewById(R.id.fhhostLabel);
	    UserIntroText = (TextView)findViewById(R.id.fhcontentText);
	    
	    
	    
	    UserAccount = new ArrayList<FixedAccountData>();
	    
	    HostInfoParserTask task = new HostInfoParserTask(this);   
		task.execute(BUNGAE_USER_URL);
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
	
	@Override
	public void onResume() {
		super.onResume();       // ��Ƽ��Ƽ�� ȭ�鿡 ����, �ʿ��� ��� UI ���� ������ �����Ѵ�.   
		
		
	}
	
	public class HostInfoParserTask extends AsyncTask<String, Integer, String>{  
    	private FixedBungaeHostActivity mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	boolean boolean_t_row = false;
    	
    	public HostInfoParserTask(FixedBungaeHostActivity activity) {  
    		mActivity = activity;     
    		}       
    	  
    	@Override    
    	protected void onPreExecute() { 
    		
    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("�ε���...");     
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
    	            buffer.append("id").append("=").append(hostId); 
    	            
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

    			UserPhoneText.setText(UserAccount.get(0).getUserPhone());
    			
        		if(UserAccount.get(0).getUserSex().equals("0"))
        		{
        			UserSexText.setText("��");
        		}
        		else
        		{
        			UserSexText.setText("��");
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
    			FixedAccountData userAccount = null;              
    			while (eventType != XmlPullParser.END_DOCUMENT) {   
    				String tag = null;     
    				switch (eventType) {    
    				case XmlPullParser.START_TAG:
    					tag = parser.getName(); 
    					
    					if(tag.equals("message"))
    						userAccount = new FixedAccountData();
    					if(tag.equals("u_num"))
    						boolean_u_num = true;
    					if(tag.equals("u_id"))
        					boolean_u_id = true;
    					if(tag.equals("u_phone"))
    						boolean_u_phone = true;
    					if(tag.equals("u_sex"))
    						boolean_u_sex = true;
    					if(tag.equals("u_age"))
    						boolean_u_age = true;
    					if(tag.equals("u_intro"))
    						boolean_u_intro = true;
    					
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
    					if(boolean_u_phone){
    						
    						String enStr = parser.getText();
    						
    						String deStr = URLDecoder.decode(enStr, "utf-8");
    						
    						String phoneNumDecrypted = SimpleCrypto2.decrypt1(CRYPTO_SEED_PASSWORD, deStr);
    						
    						userAccount.setUserPhone(phoneNumDecrypted);
    						boolean_u_phone = false;
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
    
    
}