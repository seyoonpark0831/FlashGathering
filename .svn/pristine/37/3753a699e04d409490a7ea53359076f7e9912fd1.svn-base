package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Xml;

public class BungaeParserTask extends AsyncTask<String, Integer, BungaeListAdapter>{  
	private BungaeListActivity mActivity;    
	private BungaeListAdapter mAdapter;    
	private ProgressDialog mProgressDialog;   
	
	
	private SimpleDateFormat dateFormat;
	
	boolean boolean_b_num = false,
			boolean_b_category = false,
			boolean_b_title = false,
			boolean_b_host_id = false,
			boolean_b_time = false,
			boolean_b_cur = false,
			boolean_b_max = false,
			boolean_b_open_private = false,
			boolean_b_password = false;
	
	public BungaeParserTask(BungaeListActivity activity, BungaeListAdapter adapter) {  
		mActivity = activity;       
		mAdapter = adapter;     
		}       
	  
	@Override    
	protected void onPreExecute() { 
		         
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		mProgressDialog = new ProgressDialog(mActivity);     
		mProgressDialog.setMessage("로딩중...");     
		mProgressDialog.show();      
		}      
	
	@Override    
	protected BungaeListAdapter doInBackground(String... params) {   
		BungaeListAdapter result = null;          
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
			
	          
			//InputStream is = url.openConnection().getInputStream();
	        InputStream is = http.getInputStream();
			result = parseXml(is);          
			} catch (Exception e) {      
				e.printStackTrace();        
				}          
		  
		return result;        
		}       
	
	@Override    
	protected void onPostExecute(BungaeListAdapter result) {   
		mProgressDialog.dismiss();      
		mActivity.setListAdapter(result);  
		}         
	
	
	public BungaeListAdapter parseXml(InputStream is) throws IOException, XmlPullParserException {    
		XmlPullParser parser = Xml.newPullParser();     
		try {        
			parser.setInput(is, null);      
			int eventType = parser.getEventType();     
			BungaeListData nowBungaeData = null;              
			while (eventType != XmlPullParser.END_DOCUMENT) {   
				String tag = null;     
				switch (eventType) {    
				case XmlPullParser.START_TAG:        
					tag = parser.getName(); 
					
					if(tag.equals("bungae"))
						nowBungaeData = new BungaeListData();
					if(tag.equals("b_num"))
						boolean_b_num = true;
					if(tag.equals("b_category"))
						boolean_b_category = true;
					if(tag.equals("b_title"))
						boolean_b_title = true;
					if(tag.equals("b_host_id"))
						boolean_b_host_id = true;
					if(tag.equals("b_time"))
						boolean_b_time = true;
					if(tag.equals("b_cur"))
						boolean_b_cur = true;
					if(tag.equals("b_max"))
						boolean_b_max = true;
					if(tag.equals("b_open_private"))
						boolean_b_open_private = true;
					if(tag.equals("b_password"))
						boolean_b_password = true;      
						                                   
					break;           
					
				case XmlPullParser.TEXT:
					if(boolean_b_num){
						nowBungaeData.setBungaeNum(parser.getText());
						boolean_b_num = false;
					}
					if(boolean_b_category){
						nowBungaeData.setBungaeCategory(parser.getText());
						boolean_b_category = false;
					}
					if(boolean_b_title){
						
						String enStr = parser.getText();
						
						String deStr = URLDecoder.decode(enStr, "utf-8");

						nowBungaeData.setBungaeTitle(deStr);
												
						boolean_b_title = false;
					}
					if(boolean_b_host_id){
						nowBungaeData.setBungaeHostId(parser.getText());
						
						boolean_b_host_id = false;
					}
					if(boolean_b_time){
						nowBungaeData.setBungaeTime(parser.getText());
						nowBungaeData.setBungaeTimeConvert(dateFormat.parse(parser.getText()));
						boolean_b_time = false;
					}
					if(boolean_b_cur){
						nowBungaeData.setBungaeCur(parser.getText());
						boolean_b_cur = false;
					}
					if(boolean_b_max){
						nowBungaeData.setBungaeMax(parser.getText());
						boolean_b_max = false;
					}
					if(boolean_b_open_private){
						nowBungaeData.setBungaeOpenPrivate(parser.getText());
						boolean_b_open_private = false;
					}
					if(boolean_b_password){
						nowBungaeData.setBungaePassword(parser.getText());
						boolean_b_password = false;
					}
					
					break;
					
					
					case XmlPullParser.END_TAG:  
						
						tag = parser.getName();
						
						if (tag.equalsIgnoreCase("bungae") && nowBungaeData != null){
							mAdapter.add(nowBungaeData);
							//mAdapter.notifyDataSetChanged();
							mActivity.runOnUiThread(updateUI);
						}

						break;                       
						}                         
				eventType = parser.next();           
				}            
			} catch (Exception e) {     
				e.printStackTrace();    
				}          
		return mAdapter;     
	} 

	//UI에 데이터가 변경된 경우 다시 그려주게될 Runnable interface. 데이터를 갱신하고, 이를 화면에
	 //반영해야할 경우에는 반드시 Activity의 runOnUiThread(Runnable r) 메쏘드를 이용해서 호출해야
	 //실시간으로 화면에 반영됩니다.
	 private Runnable updateUI = new Runnable() {
	  public void run() {
		  mAdapter.notifyDataSetChanged();
	  }

	 };


	
}