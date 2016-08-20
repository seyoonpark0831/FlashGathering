package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.widget.ExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;

public class Setting1_Notice_Expand extends ExpandableListActivity {
	
	private ExpandableListAdapter mAdapter;
	
	private List<Map<String, String>> groupData;		//adapter�� ���� group,child data�� ���� list
	private List<List<Map<String, String>>> childData;
	
	private Map<String, String> curGroupMap;	//group,child data�� �߰��Ǵ� �� ���
	private List<Map<String, String>> children;
	private Map<String, String> curChildMap;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
//	    setContentView(R.layout.setting1_notice);
	    
	    setTitle("��������");
	    
	    getExpandableListView().setBackgroundColor(Color.rgb(216,221,224));
	    
	    groupData = new ArrayList<Map<String, String>>();
	    childData = new ArrayList<List<Map<String, String>>>();
	    
	    NoticeParserTask task = new NoticeParserTask(Setting1_Notice_Expand.this);   
	    task.execute(BungaeActivity.MAIN_URL + "/notice_android.php");

	}
	
	public void setAdapter() {
		mAdapter = new SimpleExpandableListAdapter(this,
	    		groupData,
	    		R.layout.notice_list_row,
	    		new String[] {"noticeTitle", "noticeTime"},
	    		new int[] {R.id.noticeTitleLabel, R.id.noticeTimeLabel},
	    		
	    		childData,
	    		R.layout.child_row,
	    		new String[] {"noticeContentTitle", "noticeContentText"},
	    		new int[] {R.id.childName, R.id.childContent}
	    		);
		
	    setListAdapter(mAdapter);
	    
	}
	
	
	public class NoticeParserTask extends AsyncTask<String, Integer, String>{  
    	private Setting1_Notice_Expand mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	private boolean boolean_n_num = false,
						boolean_n_time = false,
						boolean_n_title = false,
						boolean_n_content = false;
    	
    	public NoticeParserTask(Setting1_Notice_Expand activity) {  
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
//	            StringBuffer buffer = new StringBuffer();
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
    		
    		mProgressDialog.dismiss();
    		
    		//�Ľ� �ļ�ó��
    	    setAdapter();
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
    					
    					if(tag.equals("notice")) {
    						curGroupMap = new HashMap<String, String>();
    						children = new ArrayList<Map<String,String>>();
    						curChildMap = new HashMap<String, String>();
    					}
    					
    					if(tag.equals("n_num"))		boolean_n_num = true;
     					if(tag.equals("n_time"))	boolean_n_time = true;
     					if(tag.equals("n_title"))	boolean_n_title = true;
     					if(tag.equals("n_content"))	boolean_n_content = true;
     					
     					break;
    					
    				case XmlPullParser.TEXT:
    					
    					if(boolean_n_num){
     						boolean_n_num = false;
     					}
     					if(boolean_n_time){
    				    	curGroupMap.put("noticeTime", parser.getText());
     						boolean_n_time = false;
     					}
     					if(boolean_n_title){
     						String ntDecoded = new String();
     						ntDecoded = URLDecoder.decode(parser.getText(), "UTF-8");
    						curGroupMap.put("noticeTitle", ntDecoded);
    						curChildMap.put("noticeContentTitle", ntDecoded);
     						boolean_n_title = false;
     					}
     					if(boolean_n_content){
     						String nctDecoded = new String();
     						nctDecoded = URLDecoder.decode(parser.getText(), "UTF-8");
    						curChildMap.put("noticeContentText", nctDecoded);
    						boolean_n_content = false;
     					}
     					
    					break;
    					
    					
					case XmlPullParser.END_TAG:
						
						tag = parser.getName();
						if (tag.equalsIgnoreCase("notice") && !curGroupMap.isEmpty() && !curChildMap.isEmpty()){
							groupData.add(curGroupMap);
							children.add(curChildMap);
					    	childData.add(children);
//					    	Toast.makeText(Setting1_Notice_Expand.this, groupData.get(0).get("noticeTitle"), 0).show();
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
