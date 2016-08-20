package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;

public class Setting4_TOA extends Activity {
	
	private TextView TOAText;
	
	ArrayList<TOAData> TOADataArray = null;
	 
	TOAData toaData = null;
    
	//Setting4_TOA.java

	

	private static final String BUNGAE_TOA_URL = BungaeActivity.MAIN_URL + "/auth_views_php/toa.php";
	
//	private static final String BUNGAE_TOA_URL = "http://www.hunect.com/testphp/auth_views_php/toa.php"; 
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.setting4_toa);
	    setTitle("이용약관");
	    // TODO Auto-generated method stub
	    
	    
	    TOAText = (TextView)findViewById(R.id.s4toaText);
	    
	    
	    TOADataArray = new ArrayList<TOAData>();
	    
	    TOAParserTask task = new TOAParserTask(this);   
		task.execute(BUNGAE_TOA_URL);

	}
	@Override
	public void onResume() {
		super.onResume();       // 액티비티가 화면에 보임, 필요한 모든 UI 변경 사항을 적용한다.   
		
		
	}
	
	   
    
    public class TOAParserTask extends AsyncTask<String, Integer, String>{  
    	private Setting4_TOA mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	boolean boolean_t_row = false;
    	
    	public TOAParserTask(Setting4_TOA activity) {  
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
    		

    		TOAText.setText(TOADataArray.get(0).getToa());
    		mProgressDialog.dismiss();      

    		
    		}         
    	
    	
    	public String parseXml(InputStream is) throws IOException, XmlPullParserException {    
    		XmlPullParser parser = Xml.newPullParser();     
    		try {
    			
    			parser.setInput(is, null);      
    			int eventType = parser.getEventType();     
    			TOAData toaData = null;              
    			while (eventType != XmlPullParser.END_DOCUMENT) {   
    				String tag = null;     
    				switch (eventType) {    
    				case XmlPullParser.START_TAG:        
    					tag = parser.getName(); 
    					
    					if(tag.equals("toa_row"))
    						toaData = new TOAData();
    					if(tag.equals("toa_content"))
    						boolean_t_row = true;
    					    						                                   
    					break;           
    					
    				case XmlPullParser.TEXT:
    					if(boolean_t_row){
    						toaData.setToa(parser.getText());
    						boolean_t_row = false;
    					}
    					
    					
    					break;
    					
    					
    					case XmlPullParser.END_TAG:  
    						
    						tag = parser.getName();
    						if (tag.equalsIgnoreCase("toa_row") && toaData != null){
    							TOADataArray.add(toaData);
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
