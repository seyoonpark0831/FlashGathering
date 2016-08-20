package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MyBungaeListActivity extends ListActivity {

	private static final String BUNGAE_LIST_URL = BungaeActivity.MAIN_URL + "/mynowbungae.php";
	private static final String BUNGAE_HISTORY_URL = BungaeActivity.MAIN_URL + "/historybungae.php";
	
	//private static final String BUNGAE_LIST_URL = "http://www.hunect.com/testphp/mynowbungae.php";
	//private static final String BUNGAE_HISTORY_URL = "http://www.hunect.com/testphp/historybungae.php";

	long backKeyClick = 0;
	long backKeyClickTime;
	
	private ArrayList<MyBungaeItem> mItems;
	private MyBungaeListAdapter mAdapter;
	private ProgressDialog mProgressDialog;
	
	private int myBungaeCount = 0;
	private int totalCount = 0;

	@Override   
	public void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);   
		setContentView(R.layout.mybungae_list);     
		setTitle("내 번개 목록");

		getListView().setBackgroundColor(Color.rgb(216,221,224));
	}     

	@Override    
	public void onResume() {
		super.onResume();       // 액티비티가 화면에 보임, 필요한 모든 UI 변경 사항을 적용한다.   

		mItems = new ArrayList<MyBungaeItem>();  
		mAdapter = new MyBungaeListAdapter(this, mItems);   
		
//		HistoryBungaeParserTask task = new HistoryBungaeParserTask(this, mAdapter);
//		task.execute(BUNGAE_HISTORY_URL);
		
		MyBungaeParserTask task = new MyBungaeParserTask(this, mAdapter);   
		task.execute(BUNGAE_LIST_URL);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		MyBungaeItem item = mItems.get(position);
		BungaeListData ei = (BungaeListData)item;
		Intent intent = new Intent(this, MyBungaeDetailActivity.class);   
		intent.putExtra("selectedNum", ei.getBungaeNum());       
		startActivity(intent);     
		
	}        

	@Override   
	public boolean onCreateOptionsMenu(Menu menu) {  
		boolean result = super.onCreateOptionsMenu(menu);        
		
		menu.add(0,1,0,"번개 만들기").setIcon(R.drawable.add_button);
		menu.add(0,2,0,"새로 고침").setIcon(R.drawable.blue_refresh);    
		return result;       
	}       

	@Override    
	public boolean onOptionsItemSelected(MenuItem item) {  
		switch (item.getItemId()) {

		case 1:
			Intent intent = new Intent(this, AddBungae.class);       
			startActivity(intent);
			return true;
		
		case 2:     

			mItems = new ArrayList<MyBungaeItem>();      
			mAdapter = new MyBungaeListAdapter(this, mItems);    

			
			MyBungaeParserTask task = new MyBungaeParserTask(this, mAdapter);   
			task.execute(BUNGAE_LIST_URL);       
			return true;    
		}             
		return super.onOptionsItemSelected(item);     
	} 
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	
	
	public class MyBungaeParserTask extends AsyncTask<String, Integer, MyBungaeListAdapter>{  
		
		
		private MyBungaeListActivity mActivity;    
		public MyBungaeListAdapter mAdapter;    
//		private ProgressDialog mProgressDialog;   
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
		
		public MyBungaeParserTask(MyBungaeListActivity activity, MyBungaeListAdapter adapter) {  
			mActivity = activity;       
			mAdapter = adapter;     
			}       
		  
		@Override    
		protected void onPreExecute() { 
			         
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			mProgressDialog = new ProgressDialog(mActivity);     
			mProgressDialog.setMessage("로딩중...");     
			mProgressDialog.show();
			
//			mAdapter.add(new SectionItem("현재 번개"));
			}      
		
		@Override    
		protected MyBungaeListAdapter doInBackground(String... params) {   
			MyBungaeListAdapter result = null;          
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
		protected void onPostExecute(MyBungaeListAdapter result) {   
//			mProgressDialog.dismiss();
			
			if(!mAdapter.isEmpty()) mAdapter.insert(new SectionItem("현재 번개"), 0);
			
			myBungaeCount = mAdapter.getCount();
			
			HistoryBungaeParserTask task = new HistoryBungaeParserTask(MyBungaeListActivity.this, mAdapter);
			task.execute(BUNGAE_HISTORY_URL);
			
			
		}
		
		
		private MyBungaeListAdapter parseXml(InputStream is) throws IOException, XmlPullParserException {    
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
								Log.d("Date", "날짜는 : "+nowBungaeData.getBungaeTimeConvert());
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
	
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	
	
	public class HistoryBungaeParserTask extends AsyncTask<String, Integer, MyBungaeListAdapter>{  
		
		private MyBungaeListActivity mActivity;    
		public MyBungaeListAdapter mAdapter;    
//		private ProgressDialog mProgressDialog;
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
		
		private HistoryBungaeParserTask(MyBungaeListActivity activity, MyBungaeListAdapter adapter) {
			mActivity = activity;
			mAdapter = adapter;
		}
		  
		@Override    
		protected void onPreExecute() {
			         
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
//			mProgressDialog = new ProgressDialog(mActivity);
//			mProgressDialog.setMessage("History 로딩중...");
//			mProgressDialog.show();
			
		}
		
		@Override
		protected MyBungaeListAdapter doInBackground(String... params) {   
			
			MyBungaeListAdapter result = null;
			
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
		protected void onPostExecute(MyBungaeListAdapter result) {
			mProgressDialog.dismiss();
			
			totalCount = result.getCount();
			
			//둘 중 하나는 row가 존재
			if(totalCount!=0) {
				//history는 없는 경우
				if(totalCount-myBungaeCount==0) {
					//history에 empty 표시
				}
				//history만 있는 경우
				else if(myBungaeCount==0){
					//내번개에 empty 표시(선택적, 일단 초기 버전에서는 생략하기로 결정)
					//history 섹션 헤더 삽입
					result.insert(new SectionItem("History"), myBungaeCount);
				}
				//둘 다 있는 경우
				else {
					//history 섹션 헤더 삽입
					result.insert(new SectionItem("History"), myBungaeCount);
				}
			}
			
			mActivity.setListAdapter(result);
			

		}
		
		
		private MyBungaeListAdapter parseXml(InputStream is) throws IOException, XmlPullParserException {    
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
						
						if(tag.equals("history"))
							nowBungaeData = new BungaeListData();
						if(tag.equals("h_num"))
							boolean_b_num = true;
						if(tag.equals("h_category"))
							boolean_b_category = true;
						if(tag.equals("h_title"))
							boolean_b_title = true;
						if(tag.equals("h_host_id"))
							boolean_b_host_id = true;
						if(tag.equals("h_time"))
							boolean_b_time = true;
						if(tag.equals("h_cur"))
							boolean_b_cur = true;
						if(tag.equals("h_max"))
							boolean_b_max = true;
						if(tag.equals("h_open_private"))
							boolean_b_open_private = true;
						if(tag.equals("h_password"))
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
							
//							Toast.makeText(MyBungaeListActivity.this, deStr, Toast.LENGTH_LONG).show();
													
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
							if (tag.equalsIgnoreCase("history") && nowBungaeData != null){
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

	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if ((keyCode == KeyEvent.KEYCODE_BACK) ) {

	        long currentTime = System.currentTimeMillis();
	        final int duration = 2000;
	         
	        backKeyClick ++; 
	         
	        if (backKeyClick == 1){
		        backKeyClickTime = System.currentTimeMillis();
		          
		        Toast t =  Toast.makeText(this, "'뒤로'버튼을 한번 더 누르면 종료됩니다.",
		            Toast.LENGTH_SHORT);
		        	t.setDuration(duration);
		        	t.show();
		          
		         new Thread(new Runnable() {    
		
				     @Override
				     public void run() {
				    	 try {
				    		 	Thread.sleep(duration);
				    	 } catch (InterruptedException e) {
				    		 	e.printStackTrace();
				    	 }
				    	 backKeyClick=0;
				    }	
			    }).start();
		    }else if(backKeyClick == 2){
	
		    	if(currentTime - backKeyClickTime <= duration  ){
		        	return super.onKeyDown(keyCode, event);
		        }
		        backKeyClick = 0;
		    } 
	        return true;   
		}     
		return super.onKeyDown(keyCode, event);    
	}

	
}
