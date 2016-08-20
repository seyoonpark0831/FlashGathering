package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FixedBungaeActivity extends Activity implements OnClickListener {

	private static final int DIALOG_DELETE = 0;
	
	long backKeyClick = 0;
	long backKeyClickTime;

	
	private TextView CategoryText;
	private TextView TitleText;
	private TextView TimeText;
	private TextView HostText;
	private TextView LocationText;
	private TextView ContentText;
	private TextView CurrentText;
	private ListView MemberList;
	private ListView ChatListView;
	
	
	private TextView memberIdText;
	private TextView memberSexText;
	private TextView memberAgeText;
	
	private TextView chatIdText;
	private TextView chatTimeText;
	private TextView chatTextText;
	
	private EditText AddChatText;
	private Button AddChatButton;
	
	private Button hostButton;
	private Button mapButton;
	
	private Button refreshButton;
	
	private SimpleDateFormat dateFormat;
	
	private String timeStr;
	
	private String deleteChatNum;

	private ArrayList<String> memberTmpList;

	String NumStr;

	String memberStr;
	
	private boolean tabChangedToHere;
	
	
	List<BungaeMember> SelectedMemberList = null;
	
	BungaeMember selectedBungaeMember = null;
	
	List<BungaeDetailData> BungaeDetail = null;
	 
    BungaeDetailData selectedBungaeData = null;
    
    List<ChatData> ChatList = null;
	 
    ChatData chatData = null;
    
    ScrollView scrollView;
    ListView listView;

	
    //FixedBungaeActivity.java

  	private static final String BUNGAE_DETAIL_URL = BungaeActivity.MAIN_URL + "/progressbungae.php";
  	private static final String BUNGAE_CHAT_URL = BungaeActivity.MAIN_URL + "/chat.php";
  	private static final String BUNGAE_ADDCHAT_URL = BungaeActivity.MAIN_URL + "/addchat.php";
  	private static final String BUNGAE_DELETECHAT_URL = BungaeActivity.MAIN_URL + "/deletechat.php";
    
//	private static final String BUNGAE_DETAIL_URL = "http://www.hunect.com/testphp/progressbungae.php";
//	private static final String BUNGAE_CHAT_URL = "http://www.hunect.com/testphp/chat.php";
//	private static final String BUNGAE_ADDCHAT_URL = "http://www.hunect.com/testphp/addchat.php";
//	private static final String BUNGAE_DELETECHAT_URL = "http://www.hunect.com/testphp/deletechat.php";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.fixed_empty);
	    setTitle("진행중 번개");

	    
	    deleteChatNum = new String();
	    
	    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    
	    timeStr = new String();
	    
	    memberTmpList = new ArrayList<String>();
	    
	    BungaeDetail = new ArrayList<BungaeDetailData>();
	    
	    ChatList = new ArrayList<ChatData>();

	}
	

	@Override
    protected Dialog onCreateDialog(int id){
    	switch(id){
    	case DIALOG_DELETE:
    		return new AlertDialog.Builder(this)
    			.setTitle("글 삭제")
    			.setMessage("올린 글을 삭제하시겠습니까?")
    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DeleteChatTask task = new DeleteChatTask(FixedBungaeActivity.this);   
		    			task.execute(BUNGAE_DELETECHAT_URL);
					}
				})
				.setNegativeButton("Cancel", null).create();
    	
    	}
    	
    	return null;
    	
    	
    }


	
	@Override    
	public void onResume() {
		super.onResume();       // 액티비티가 화면에 보임, 필요한 모든 UI 변경 사항을 적용한다.

	    tabChangedToHere = true;
		
		FixedBungaeDetailParserTask task = new FixedBungaeDetailParserTask(this);   
		task.execute(BUNGAE_DETAIL_URL);
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.sendbutton:
		{
			if (AddChatText.getText().length() != 0)
			{
				AddChatTask task = new AddChatTask(this);   
				task.execute(BUNGAE_ADDCHAT_URL);
			}
		}
		break;
		
		case R.id.fhostButton:
		{
			
			Intent intent = new Intent(this, FixedBungaeHostActivity.class);   
			intent.putExtra("HostId", BungaeDetail.get(0).getBungaeHostId());       
			startActivity(intent);  
			
			//Toast.makeText(this, BungaeDetail.get(0).getBungaeHostId(), Toast.LENGTH_LONG).show();
		}
		break;
		
		case R.id.fmapButton:
		{
			
			Intent intent = new Intent(this, BungaeDetailMap.class);
			intent.putExtra("Location", BungaeDetail.get(0).getBungaeLocation());
	    	intent.putExtra("Loca_Lon", BungaeDetail.get(0).getBungaeLocationLon());
	    	intent.putExtra("Loca_Lat", BungaeDetail.get(0).getBungaeLocationLat());
			startActivity(intent);
			
			
			//Toast.makeText(this, "맵 버튼 작동!!", Toast.LENGTH_LONG).show();
		}
		break;
		
		case R.id.fRefreshButton:
		{
			ChatParserTask task = new ChatParserTask(FixedBungaeActivity.this);   
			task.execute(BUNGAE_CHAT_URL);			
		}
		break;
		
		
		}
	}


	   
	public void classifyMember(){
		
		memberStr = BungaeDetail.get(0).getBungaeMembers();
		

		StringTokenizer tokens = new StringTokenizer(memberStr, ")");
		for (int x=0; tokens.hasMoreElements(); x++){
			
			memberTmpList.add(tokens.nextToken());
			Log.i("PullXML_1", memberTmpList.get(x));

		}
		
		int size = memberTmpList.size();
		
		String sizeStr = String.valueOf(size);
		
		Log.i("PullXML_1", sizeStr);
		
		SelectedMemberList = new ArrayList<BungaeMember>();
		
		for (int a=0; a<size ;a++)
		{
			selectedBungaeMember = new BungaeMember();
			
			int count = 0;
			
			StringTokenizer tokens2nd = new StringTokenizer(memberTmpList.get(a), ",");
			for (; tokens2nd.hasMoreElements();){
				
				//Log.i("PullXML_2", tokens2nd.nextToken());
				
				
				
				StringTokenizer tokens3rd = new StringTokenizer(tokens2nd.nextToken(), ":");
				for (int z=0; tokens3rd.hasMoreElements();z++){
					
					
					
					if (z==0)
					{
						//selectedBungaeMember.setMemberId(tokens3rd.nextToken());
						Log.i("PullXML_3", "Trash Token = " + tokens3rd.nextToken());
					}
					
					if (z==1)
					{
						
						//selectedBungaeMember.setMemberSex(tokens3rd.nextToken());
						//Log.i("PullXML_3", "Value Token = " + tokens3rd.nextToken());
						
						if (count==0)
						{
							selectedBungaeMember.setMemberId(tokens3rd.nextToken());
							Log.i("PullXML_Mem", "Id = " + selectedBungaeMember.getMemberId());
						}
						else if (count==1)
						{
							selectedBungaeMember.setMemberSex(tokens3rd.nextToken());
							Log.i("PullXML_Mem", "Sex = " + selectedBungaeMember.getMemberSex());
						}
						else if (count==2)
						{
							selectedBungaeMember.setMemberPushId(tokens3rd.nextToken());
							Log.i("PullXML_Mem", "PushId = " + selectedBungaeMember.getMemberPushId());
						}
						else if (count==3)
						{
							selectedBungaeMember.setMemberAge(tokens3rd.nextToken());
							Log.i("PullXML_Mem", "Age = " + selectedBungaeMember.getMemberAge());
						}
						
						count++;
					}
					
					
				
				}
				 
			}
			
			SelectedMemberList.add(selectedBungaeMember);
		}
		
		MemberList.setAdapter(new MemberCustomRow(this));
		
	}
	

	
    
    class MemberCustomRow extends ArrayAdapter<BungaeMember>{
    	Activity context;
		public MemberCustomRow(Activity c) {
			super(c,R.layout.bungae_detail_member_row,SelectedMemberList);
			this.context = c;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		
			 LayoutInflater inf=context.getLayoutInflater();
			 View customcell=inf.inflate(R.layout.bungae_detail_member_row, null);
			 
			 memberIdText = (TextView)customcell.findViewById(R.id.memberIdText);
			 memberSexText = (TextView)customcell.findViewById(R.id.memberSexText);
			 memberAgeText = (TextView)customcell.findViewById(R.id.memberAgeText);
			 
			 
			 memberIdText.setText(SelectedMemberList.get(position).getMemberId());
			 if (SelectedMemberList.get(position).getMemberSex().equals("0"))
			 {
				 memberSexText.setText("남");
			 }
			 else
			 {
				 memberSexText.setText("여");
			 }
			 
			 //memberSexText.setText(SelectedMemberList.get(position).getMemberSex());
			 memberAgeText.setText(SelectedMemberList.get(position).getMemberAge());
			 
			 
			 return customcell; 
		}
    }
    
    
    public class FixedBungaeDetailParserTask extends AsyncTask<String, Integer, String>{  
    	private FixedBungaeActivity mActivity;    
    	private ProgressDialog mProgressDialog;
    	
    	private String completeString;
    	
    	boolean boolean_b_num = false,
    			boolean_b_category = false,
    			boolean_b_title = false,
    			boolean_b_host_id = false,
    			boolean_b_time = false,
    			boolean_b_loca = false,
    			boolean_b_loca_lon = false,
    			boolean_b_loca_lat = false,
    			boolean_b_content = false,
    			boolean_b_cur = false,
    			boolean_b_max = false,
    			boolean_b_min = false,
    			boolean_b_members = false,
    			boolean_b_open_private = false;
    	
    	public FixedBungaeDetailParserTask(FixedBungaeActivity activity) {  
    		mActivity = activity;     
    		}       
    	  
    	@Override    
    	protected void onPreExecute() { 
    		
    		if (BungaeDetail.size() != 0)
    		{
    			memberTmpList.clear();
    			BungaeDetail.clear();
    		}
    		
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
    	            buffer.append("id").append("=").append(UserInfoClass.getInstance().userInfo.get("u_id")); 
    	           
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
    		
    		if (BungaeDetail.size()==0)
    		{
    			setContentView(R.layout.fixed_empty);
    		}
    		else
    		{
    			setContentView(R.layout.fixed_bungae_detail);
    			scrollView = (ScrollView)findViewById(R.id.fscrollView1);
    			CategoryText = (TextView)findViewById(R.id.fcategoryLabel);
    		    TitleText = (TextView)findViewById(R.id.ftitleLabel);
    		    HostText = (TextView)findViewById(R.id.fhostLabel);
    		    TimeText = (TextView)findViewById(R.id.ftimeLabel);
    		    LocationText = (TextView)findViewById(R.id.flocationLabel);
    		    ContentText = (TextView)findViewById(R.id.fcontentText);
    		    CurrentText = (TextView)findViewById(R.id.fcurrentNumLabel);
    		    MemberList = (ListView)findViewById(R.id.fmemberListView);
    		    ChatListView = (ListView)findViewById(R.id.fchatListView);
    		    AddChatText = (EditText)findViewById(R.id.chatText);
    		    AddChatButton = (Button)findViewById(R.id.sendbutton);
    		    
    		    hostButton = (Button)findViewById(R.id.fhostButton);
    		    mapButton = (Button)findViewById(R.id.fmapButton);
    		    refreshButton = (Button)findViewById(R.id.fRefreshButton);
    		    
    		    MemberList.setOnTouchListener(new OnTouchListener(){
    		    
	    	    	@Override
	    	    	 
	    	    	 public boolean onTouch(View v, MotionEvent event){
	    	    	 
	    	    	if(event.getAction() == MotionEvent.ACTION_UP)
	    	    		scrollView.requestDisallowInterceptTouchEvent(false);
	    	    	 
	    	    	else
	    	    	 	scrollView.requestDisallowInterceptTouchEvent(true);
	    
	    	    	return false;
	        	 
	    	    	}
	    	    });
	    	    
	    	    ChatListView.setOnTouchListener(new OnTouchListener(){
	    
	    	    	@Override
	    	    	 
	    	    	 public boolean onTouch(View v, MotionEvent event){
	    	    	 
	    	    	if(event.getAction() == MotionEvent.ACTION_UP)
	    	    		scrollView.requestDisallowInterceptTouchEvent(false);
	    	    	 
	    	    	else
	    	    	 	scrollView.requestDisallowInterceptTouchEvent(true);
	    
	    	    	return false;
	        	 
	    	    	}
	    	    });
	    	    
	    	    ChatListView.setOnItemClickListener(new OnItemClickListener() {
	    	    	 
	    	    	public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3) {
	    				if (ChatList.get(arg2).getChatId().equals(UserInfoClass.getInstance().userInfo.get("u_id")))
	    				{
	    					deleteChatNum = ChatList.get(arg2).getChatNum();
	    					showDialog(DIALOG_DELETE);	
	    				}
	    	    	}
	             });
	    
	    	    hostButton.setOnClickListener(FixedBungaeActivity.this);
	    	    mapButton.setOnClickListener(FixedBungaeActivity.this);
	    	    refreshButton.setOnClickListener(FixedBungaeActivity.this);
	    	    AddChatButton.setOnClickListener(FixedBungaeActivity.this);

    		    ////////////////////////////////////////////////////////////////////////////////////
    		    
    			timeStr = (BungaeDetail.get(0).getBungaeTimeConvert().getMonth()+1)+"월"+BungaeDetail.get(0).getBungaeTimeConvert().getDate()+"일  "+BungaeDetail.get(0).getBungaeTimeConvert().getHours()+"시"+BungaeDetail.get(0).getBungaeTimeConvert().getMinutes()+"분";
    			
    			CategoryText.setText(BungaeDetail.get(0).getBungaeCategory());
    			TitleText.setText(BungaeDetail.get(0).getBungaeTitle());
    			HostText.setText(BungaeDetail.get(0).getBungaeHostId());
    			TimeText.setText(timeStr);
    			LocationText.setText(BungaeDetail.get(0).getBungaeLocation());
    			ContentText.setText(BungaeDetail.get(0).getBungaeContent());
    			CurrentText.setText(BungaeDetail.get(0).getBungaeCur()+"       /       "+BungaeDetail.get(0).getBungaeMax());
        		
        		
    			classifyMember();
    			
    			ChatParserTask task = new ChatParserTask(FixedBungaeActivity.this);   
    			task.execute(BUNGAE_CHAT_URL);
    			
                scrollView.setSmoothScrollingEnabled(true);
       	    	scrollView.smoothScrollTo(0, 0);
    		}
    		
			
    		mProgressDialog.dismiss();      

    	
    	}
    	
    	
    	public String parseXml(InputStream is) throws IOException, XmlPullParserException {    
    		XmlPullParser parser = Xml.newPullParser();     
    		try {
    			
    			parser.setInput(is, null);      
    			int eventType = parser.getEventType();     
    			BungaeDetailData selectedBungaeData = null;              
    			while (eventType != XmlPullParser.END_DOCUMENT) {   
    				String tag = null;     
    				switch (eventType) {    
    				case XmlPullParser.START_TAG:        
    					tag = parser.getName(); 
    					
    					if(tag.equals("bungae"))
    						selectedBungaeData = new BungaeDetailData();
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
    					if(tag.equals("b_loca"))
    						boolean_b_loca = true;
    					if(tag.equals("b_loca_lon"))
    						boolean_b_loca_lon = true;
    					if(tag.equals("b_loca_lat"))
    						boolean_b_loca_lat = true;
    					if(tag.equals("b_content"))
    						boolean_b_content = true;
    					if(tag.equals("b_cur"))
    						boolean_b_cur = true;
    					if(tag.equals("b_max"))
    						boolean_b_max = true;
    					if(tag.equals("b_min"))
    						boolean_b_min = true;
    					if(tag.equals("b_members"))
    						boolean_b_members = true;
    					if(tag.equals("b_open_private"))
    						boolean_b_open_private = true;   
    						                                   
    					break;           
    					
    				case XmlPullParser.TEXT:
    					if(boolean_b_num){
    						selectedBungaeData.setBungaeNum(parser.getText());
    						boolean_b_num = false;
    					}
    					if(boolean_b_category){
    						String caStr = parser.getText();
    						
    						String enStr = new String();
    						
    						try {
    							enStr = URLEncoder.encode(caStr, "utf-8");
    						} catch (UnsupportedEncodingException e) {
    							e.printStackTrace();
    						}
    						
    						
    						if (enStr.equals("%0A%09"))
    						{
    							selectedBungaeData.setBungaeCategory("일반");
    						}
    						else
    						{
    							selectedBungaeData.setBungaeCategory(parser.getText());
    						}
    							
    						
    						boolean_b_category = false;
    					}
    					if(boolean_b_title){
    						
    						String enStr = parser.getText();
    						
    						String deStr = URLDecoder.decode(enStr, "utf-8");

    						
    						selectedBungaeData.setBungaeTitle(deStr);
    						
    						boolean_b_title = false;
    					}
    					if(boolean_b_host_id){
    						selectedBungaeData.setBungaeHostId(parser.getText());
    						
    						boolean_b_host_id = false;
    					}
    					if(boolean_b_time){
    						selectedBungaeData.setBungaeTime(parser.getText());
    						selectedBungaeData.setBungaeTimeConvert(dateFormat.parse(parser.getText()));
    						boolean_b_time = false;
    					}
    					if(boolean_b_loca){
    						
    						String enStr = parser.getText();
    						
    						String deStr = URLDecoder.decode(enStr, "utf-8");
    						
    						selectedBungaeData.setBungaeLocation(deStr);
    						boolean_b_loca = false;
    					}
    					if(boolean_b_loca_lon){
    						selectedBungaeData.setBungaeLocationLon(parser.getText());
    						boolean_b_loca_lon = false;
    					}
    					if(boolean_b_loca_lat){
    						selectedBungaeData.setBungaeLocationLat(parser.getText());
    						boolean_b_loca_lat = false;
    					}
    					if(boolean_b_content){
    						String enStr = parser.getText();
    						
    						String deStr = URLDecoder.decode(enStr, "utf-8");
    						
    						selectedBungaeData.setBungaeContent(deStr);
    						boolean_b_content = false;
    					}
    					if(boolean_b_cur){
    						selectedBungaeData.setBungaeCur(parser.getText());
    						boolean_b_cur = false;
    					}
    					if(boolean_b_max){
    						selectedBungaeData.setBungaeMax(parser.getText());
    						boolean_b_max = false;
    					}
    					if(boolean_b_min){
    						selectedBungaeData.setBungaeMin(parser.getText());
    						boolean_b_min = false;
    					}
    					if(boolean_b_members){
    						selectedBungaeData.setBungaeMembers(parser.getText());
    						boolean_b_members = false;
    					}
    					if(boolean_b_open_private){
    						selectedBungaeData.setBungaeOpenPrivate(parser.getText());
    						boolean_b_open_private = false;
    					}
    					
    					break;
    					
    					
    					case XmlPullParser.END_TAG:  
    						
    						tag = parser.getName();
    						if (tag.equalsIgnoreCase("bungae") && selectedBungaeData != null){
    							BungaeDetail.add(selectedBungaeData);
    							completeString = "complete";
    							
    						}
    						else if (tag.equalsIgnoreCase("nowbungae") && selectedBungaeData == null){
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
    
    
    public class ChatParserTask extends AsyncTask<String, Integer, String>{  
    	private FixedBungaeActivity mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	boolean boolean_c_num = false,
    			boolean_c_u_id = false,
    			boolean_c_text = false,
    			boolean_c_time = false;
    	
    	public ChatParserTask(FixedBungaeActivity activity) {  
    		mActivity = activity;     
    		}       
    	  
    	@Override    
    	protected void onPreExecute() { 
    		if (ChatList.size() != 0)
    		{
    			ChatList.clear();
    		}
    		
    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("채팅 업데이트...");     
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
    	            buffer.append("b_num").append("=").append(BungaeDetail.get(0).getBungaeNum()); 
    	           
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
    		
    		ChatListView.setAdapter(new ChatCustomRow(FixedBungaeActivity.this));
    		
    		mProgressDialog.dismiss();      

    	    if(tabChangedToHere) {
    	    	scrollView.smoothScrollTo(0, 0);
    	    	tabChangedToHere = false;
    	    }
    	}
    	
    	
    	public String parseXml(InputStream is) throws IOException, XmlPullParserException {    
    		XmlPullParser parser = Xml.newPullParser();     
    		try {
    			
    			parser.setInput(is, null);      
    			int eventType = parser.getEventType();     
    			ChatData chatData = null;              
    			while (eventType != XmlPullParser.END_DOCUMENT) {   
    				String tag = null;     
    				switch (eventType) {    
    				case XmlPullParser.START_TAG:        
    					tag = parser.getName(); 
    					
    					if(tag.equals("message"))
    						chatData = new ChatData();
    					if(tag.equals("ch_num"))
    						boolean_c_num = true;
    					if(tag.equals("ch_u_id"))
    						boolean_c_u_id = true;
    					if(tag.equals("ch_text"))
    						boolean_c_text = true;
    					if(tag.equals("ch_time"))
    						boolean_c_time = true;
    						                                   
    					break;           
    					
    				case XmlPullParser.TEXT:
    					if(boolean_c_num){
    						chatData.setChatNum(parser.getText());
    						boolean_c_num = false;
    					}
    					if(boolean_c_u_id){
    						chatData.setChatId(parser.getText());
    						boolean_c_u_id = false;
    					}
    					if(boolean_c_text){
    						String enStr = parser.getText();
    						
    						String deStr = URLDecoder.decode(enStr, "utf-8");
    						
    						chatData.setChatText(deStr);
    						boolean_c_text = false;
    					}
    					if(boolean_c_time){
    						chatData.setChatTime(parser.getText());
    						chatData.setChatTimeConvert(dateFormat.parse(parser.getText()));
    						boolean_c_time = false;
    					}
    					
    					break;
    					
    					
    					case XmlPullParser.END_TAG:  
    						
    						tag = parser.getName();
    						if (tag.equalsIgnoreCase("message") && chatData != null){
    							ChatList.add(chatData);
    							completeString = "complete";
    							
    						}
    						else if (tag.equalsIgnoreCase("chat"))
    						{
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
    
    
    class ChatCustomRow extends ArrayAdapter<ChatData>{
    	
    	String ChatTimeStr;
    	
    	Activity context;
		public ChatCustomRow(Activity c) {
			super(c,R.layout.chat_row,ChatList);
			this.context = c;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		
			 LayoutInflater inf=context.getLayoutInflater();
			 View customcell=inf.inflate(R.layout.chat_row, null);
		
			 ChatTimeStr = (ChatList.get(position).getChatTimeConvert().getMonth()+1)+"월"+ChatList.get(position).getChatTimeConvert().getDate()+"일  "+ChatList.get(position).getChatTimeConvert().getHours()+"시"+ChatList.get(position).getChatTimeConvert().getMinutes()+"분";
			 
			 chatIdText = (TextView)customcell.findViewById(R.id.Chat_ID);
			 chatTimeText = (TextView)customcell.findViewById(R.id.Chat_Time);
			 chatTextText = (TextView)customcell.findViewById(R.id.Chat_Text);
			 
			 
			 chatIdText.setText(ChatList.get(position).getChatId());
			 
			 chatTimeText.setText(ChatTimeStr);
			 
			 chatTextText.setText(ChatList.get(position).getChatText());
			 
			 
			 return customcell; 
		}
		
		
    }
    
    
    public class AddChatTask extends AsyncTask<String, Integer, String>{  
    	private FixedBungaeActivity mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;

    	
    	public AddChatTask(FixedBungaeActivity activity) {  
    		mActivity = activity;     
    		}            
    	  
    	@Override    
    	protected void onPreExecute() { 

    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("채팅 전송중...");     
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
    	            
    	            String encodeId = URLEncoder.encode(UserInfoClass.getInstance().userInfo.get("u_id"), "utf-8");
    	            String encodeText = URLEncoder.encode(AddChatText.getText().toString(), "utf-8");
    	            
    	            StringBuffer buffer = new StringBuffer(); 
    	            buffer.append("ch_b_num").append("=").append(BungaeDetail.get(0).getBungaeNum()+"&")
    	            .append("ch_u_id").append("=").append(encodeId+"&")
    	            .append("ch_u_text").append("=").append(encodeText);
    	           
    	            
    	            
    	            Log.d("BungaeEnter", buffer.toString());
    	            
    	            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "utf-8"); 
    	            PrintWriter writer = new PrintWriter(outStream); 
    	            writer.write(buffer.toString()); 
    	            writer.flush(); 
    			
    	          
    	            http.getInputStream();

        			} catch (Exception e) {      
        				e.printStackTrace();        
        				}            
        		  
        		return completeString;         
    		}       
    	
    	@Override    
    	protected void onPostExecute(String completeString) {   
    		
    		ChatParserTask task = new ChatParserTask(FixedBungaeActivity.this);   
			task.execute(BUNGAE_CHAT_URL);
    		
			AddChatText.setText("");
			
    		mProgressDialog.dismiss();      

    	}
    }
    
    
    public class DeleteChatTask extends AsyncTask<String, Integer, String>{  
    	private FixedBungaeActivity mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;

    	
    	public DeleteChatTask(FixedBungaeActivity activity) {  
    		mActivity = activity;     
    		}            
    	  
    	@Override    
    	protected void onPreExecute() { 

    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("채팅 전송중...");     
    		mProgressDialog.show();      
    		}      
    	
    	@Override    
    	protected String doInBackground(String... params) {   
    		//BungaeListAdapter result = null;          
    		try {               
    			
    			String newURL = new String();
    			newURL = params[0]+"?ch_num="+deleteChatNum;
    			
    			URL url = new URL(newURL);  
    			
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
    			
    	          
    	            http.getInputStream();
    	            
        			} catch (Exception e) {      
        				e.printStackTrace();        
        				}            
        		  
        		return completeString;         
    		}       
    	
    	@Override    
    	protected void onPostExecute(String completeString) {   
    		
    		ChatParserTask task = new ChatParserTask(FixedBungaeActivity.this);   
			task.execute(BUNGAE_CHAT_URL);
    					
    		mProgressDialog.dismiss();      

    	}	
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
