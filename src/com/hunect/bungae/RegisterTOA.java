package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;

public class RegisterTOA extends Activity {
	
	public TextView toaText;
	public String toaContent;
	
	//RegisterTOA.java

	private static final String TOA_URL = BungaeActivity.MAIN_URL + "/auth_views_php/toa.php";
//	private static final String TOA_URL = "http://www.hunect.com/testphp/auth_views_php/toa.php"; 
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.register_toa);
	    setTitle("가입 약관");
	    
	    toaText = (TextView)findViewById(R.id.register_toaText);
	    
	    TOAParserTask task = new TOAParserTask(this);   
		task.execute(TOA_URL);

	}
	   
    
    public class TOAParserTask extends AsyncTask<String, Integer, String>{  
    	private RegisterTOA mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	boolean boolean_t_row = false;
    	
    	public TOAParserTask(RegisterTOA activity) {  
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
//	            StringBuffer buffer = new StringBuffer();
	            //buffer.append("id").append("=").append("alice721"); 
//	           
//	            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "utf-8"); 
//	            PrintWriter writer = new PrintWriter(outStream); 
//	            writer.write(buffer.toString()); 
//	            writer.flush(); 
    			
    	          
    	        InputStream is = http.getInputStream();
    			parseXml(is);          
    			} catch (Exception e) {      
    				e.printStackTrace();        
    				}          
    		  
    		return completeString;        
    		}       
    	
    	@Override    
    	protected void onPostExecute(String completeString) {   
    		
    		toaText.setText(toaContent);
    		mProgressDialog.dismiss();
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
    					
    					if(tag.equals("toa_row"))
    						toaContent = new String();
    					if(tag.equals("toa_content"))
    						boolean_t_row = true;
    					    						                                   
    					break;           
    					
    				case XmlPullParser.TEXT:
    					if(boolean_t_row){
    						toaContent = parser.getText();
    						boolean_t_row = false;
    					}
    					
    					break;
    					
    					
					case XmlPullParser.END_TAG:  
						
						tag = parser.getName();
						if (tag.equalsIgnoreCase("toa_row") && toaContent != null){
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
