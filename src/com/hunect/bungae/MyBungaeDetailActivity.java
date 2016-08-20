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

import net.daum.mobilead.AdConfig;
import net.daum.mobilead.AdHttpListener;
import net.daum.mobilead.MobileAdView;

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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class MyBungaeDetailActivity extends Activity implements OnClickListener, AdHttpListener {

	private ProgressDialog mProgressDialog;  
	
	private MobileAdView adView = null;
	
	private static final int DIALOG_OK_CANCEL = 0;
	
	private TextView CategoryText;
	private TextView TitleText;
	private TextView TimeText;
	private TextView HostText;
	private TextView LocationText;
	private TextView ContentText;
	private TextView CurrentText;
	private ListView MemberList;
	
	private TextView minNoticeText;
	private TextView openNoticeText;
	
	private ImageView openImage;
	
	private TextView memberIdText;
	private TextView memberSexText;
	private TextView memberAgeText;
	
	private Button myhostButton;
	private Button mymapButton;
	private Button outBungaeButton;

	private SimpleDateFormat dateFormat;
	
	private String timeStr;
	
	private String dialogTitle;
	private String dialogContent;
	
	private String enterInfoString;
	
	private ArrayList<String> memberTmpList;
	
	private String deletePushId;
	
	private int deleteNotEnterFlag;

	String NumStr;

	String memberStr;
	
	
	List<BungaeMember> SelectedMemberList = null;
	
	BungaeMember selectedBungaeMember = null;
	
	List<BungaeDetailData> BungaeDetail = null;
	 
    BungaeDetailData selectedBungaeData = null;
    
    ArrayList<NewBungaeDetailData> NewBungaeData = null;
    
    NewBungaeDetailData newBungae = null;
    
    
    ScrollView scrollView;
    ListView listView;
    
    
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
	
	//MyBungaeDetailActivity.java

	private static final String BUNGAE_DETAIL_URL = BungaeActivity.MAIN_URL + "/selectedbungae-r.php";
	private static final String BUNGAE_DELETE_URL = BungaeActivity.APNS_URL + "/delete-p.php"; 
	private static final String OUT_BUNGAE_URL = BungaeActivity.MAIN_URL + "/updateenter.php";
	
//	private static final String BUNGAE_DETAIL_URL = "http://www.hunect.com/testphp/selectedbungae-r.php";
//	private static final String BUNGAE_DELETE_URL = "http://www.hunect.com/apnsphp/delete-q.php"; 
//	private static final String OUT_BUNGAE_URL = "http://www.hunect.com/testphp/updateenter.php";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mybungae_detail);
	    // TODO Auto-generated method stub
	    
	    initAdam();
	    
	    scrollView = (ScrollView)findViewById(R.id.mscrollView1);
	    listView = (ListView)findViewById(R.id.mmemberListView);
	    
	    listView.setOnTouchListener(new OnTouchListener(){

	    	@Override
	    	 
	    	 public boolean onTouch(View v, MotionEvent event){
	    	 
	    	if(event.getAction() == MotionEvent.ACTION_UP)
	    		scrollView.requestDisallowInterceptTouchEvent(false);
	    	 
	    	else
	    	 	scrollView.requestDisallowInterceptTouchEvent(true);

	    	return false;
    	 
	    	}
	    });
	    
	    
	    CategoryText = (TextView)findViewById(R.id.mcategoryLabel);
	    TitleText = (TextView)findViewById(R.id.mtitleLabel);
	    HostText = (TextView)findViewById(R.id.mhostLabel);
	    TimeText = (TextView)findViewById(R.id.mtimeLabel);
	    LocationText = (TextView)findViewById(R.id.mlocationLabel);
	    ContentText = (TextView)findViewById(R.id.mcontentText);
	    CurrentText = (TextView)findViewById(R.id.mcurrentNumLabel);
	    MemberList = (ListView)findViewById(R.id.mmemberListView);
	    
	    minNoticeText = (TextView)findViewById(R.id.myminNoticeLabel);
	    openNoticeText = (TextView)findViewById(R.id.myopenNoticeLabel);
	    openImage = (ImageView)findViewById(R.id.myopenImageView);
	    
	    enterInfoString = new String();
	    
	    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    
	    //enterInfoString = "(id:seyoun, sex:0, pushid:A_8, age:26)";
	    enterInfoString = "(id:"+UserInfoClass.getInstance().userInfo.get("u_id")+", sex:"+UserInfoClass.getInstance().userInfo.get("u_sex")+", pushid:A_"+UserInfoClass.getInstance().userInfo.get("u_push_id")+", age:"+UserInfoClass.getInstance().userInfo.get("u_age")+")";
	    
	    myhostButton = (Button)findViewById(R.id.myhostButton);
	    mymapButton = (Button)findViewById(R.id.mymapButton);
	    outBungaeButton = (Button)findViewById(R.id.menterButton);
	    
	    NumStr = getIntent().getStringExtra("selectedNum"); //����Ʈ�� key���� ���� �ش� String�� �޴´�.

	    //Toast.makeText(this, str, Toast.LENGTH_LONG).show(); //�佺Ʈ ������� Ȯ���غ���.

	    //NumberText.setText(str);
	    
	    timeStr = new String();
	    
	    memberTmpList = new ArrayList<String>();
	    
	    myhostButton.setOnClickListener(this);
	    mymapButton.setOnClickListener(this);
	    outBungaeButton.setOnClickListener(this);
	    
	    BungaeDetail = new ArrayList<BungaeDetailData>();
	    
	    NewBungaeData = new ArrayList<NewBungaeDetailData>();
	    
	    dialogTitle = new String();
	    dialogContent = new String();
	    
	    deletePushId = new String();
	    
	    MyBungaeDetailParserTask task = new MyBungaeDetailParserTask(this);   
		task.execute(BUNGAE_DETAIL_URL);

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
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.myhostButton:
		{
			
			Intent intent = new Intent(this, BungaeDetailHostActivity.class);
			intent.putExtra("HostId", BungaeDetail.get(0).getBungaeHostId());
			startActivity(intent);
			
		}
		break;
		
		case R.id.mymapButton:
			{
				
				Intent intent = new Intent(this, BungaeDetailMap.class);
				intent.putExtra("Location", BungaeDetail.get(0).getBungaeLocation());
		    	intent.putExtra("Loca_Lon", BungaeDetail.get(0).getBungaeLocationLon());
		    	intent.putExtra("Loca_Lat", BungaeDetail.get(0).getBungaeLocationLat());
				startActivity(intent);
				
				
				//Toast.makeText(this, "�� ��ư �۵�!!", Toast.LENGTH_LONG).show();
			}
			break;
			
		
		case R.id.menterButton:
		{
//			Toast.makeText(this, "����/���� ��ư �۵�!!", Toast.LENGTH_LONG).show();
			showDialog(DIALOG_OK_CANCEL);	
		}
		break;
		
		}
	}
	
	@Override
    protected Dialog onCreateDialog(int id){
    	switch(id){
    	case DIALOG_OK_CANCEL:
    		return new AlertDialog.Builder(this)
    			.setTitle(dialogTitle)
    			.setMessage(dialogContent)
    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (dialogTitle.equals("���� ����"))
						{
							deleteNotEnterFlag = 0;
							//DeleteBungaeTask
							//DeleteBungaeTask task = new DeleteBungaeTask(this);   
							//task.execute(BUNGAE_DELETE_URL);
						}
						else if (dialogTitle.equals("���� ����"))
						{
							deleteNotEnterFlag = 1;
							//OutBungaeTask task = new OutBungaeTask(this);   
							//task.execute(OUT_BUNGAE_URL);
							
							
							
						}
						
						NewBungaeDetailParserTask task = new NewBungaeDetailParserTask(MyBungaeDetailActivity.this);   
		    			task.execute(BUNGAE_DETAIL_URL);
						
					}
				})
				.setNegativeButton("Cancel", null).create();
    	}
    	
    	return null;
    }

	
	@Override
	public void onResume() {
		super.onResume();       // ��Ƽ��Ƽ�� ȭ�鿡 ����, �ʿ��� ��� UI ���� ������ �����Ѵ�.   
		
		
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
				 memberSexText.setText("��");
			 }
			 else
			 {
				 memberSexText.setText("��");
			 }
			 
			 //memberSexText.setText(SelectedMemberList.get(position).getMemberSex());
			 memberAgeText.setText(SelectedMemberList.get(position).getMemberAge());
			 
			 
			 return customcell; 
		}
    }
    
    public class MyBungaeDetailParserTask extends AsyncTask<String, Integer, String>{  
    	private MyBungaeDetailActivity mActivity;    
//    	private ProgressDialog mProgressDialog;   
    	
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
    	
    	public MyBungaeDetailParserTask(MyBungaeDetailActivity activity) {  
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
    	            buffer.append("num").append("=").append(NumStr); 
    	           
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
    		
    		timeStr = (BungaeDetail.get(0).getBungaeTimeConvert().getMonth()+1)+"��"+BungaeDetail.get(0).getBungaeTimeConvert().getDate()+"��  "+BungaeDetail.get(0).getBungaeTimeConvert().getHours()+"��"+BungaeDetail.get(0).getBungaeTimeConvert().getMinutes()+"��";
    		
    		CategoryText.setText(BungaeDetail.get(0).getBungaeCategory());
			TitleText.setText(BungaeDetail.get(0).getBungaeTitle());
			HostText.setText(BungaeDetail.get(0).getBungaeHostId());
			TimeText.setText(timeStr);
			LocationText.setText(BungaeDetail.get(0).getBungaeLocation());
			ContentText.setText(BungaeDetail.get(0).getBungaeContent());
			
			minNoticeText.setText("�ּ� "+BungaeDetail.get(0).getBungaeMin()+"���� ���̸� ���� �˴ϴ�.");
			if (BungaeDetail.get(0).getBungaeOpenPrivate().equals("0"))
			{
				openNoticeText.setText("�� ������ ���� ���� �Դϴ�.");
				openImage.setImageResource(R.drawable.lightning1);
			}
			else
			{
				openNoticeText.setText("�� ������ ��� ���� �Դϴ�.");
				openImage.setImageResource(R.drawable.lock);
			}
			
			if ( BungaeDetail.get(0).getBungaeMax().equals("11") )
				CurrentText.setText(BungaeDetail.get(0).getBungaeCur()+"    /    "+"������");
			
			else 
				CurrentText.setText(BungaeDetail.get(0).getBungaeCur()+"    /    "+BungaeDetail.get(0).getBungaeMax());
    		
			if (BungaeDetail.get(0).getBungaeHostId().equals(UserInfoClass.getInstance().userInfo.get("u_id")))
			{
				outBungaeButton.setText("�� �� �� ��");
				dialogTitle = "���� ����";
				dialogContent = "������ �����Ͻðڽ��ϱ�?";
			}
			else
			{
				outBungaeButton.setText("�� �� �� ��");
				dialogTitle = "���� ����";
				dialogContent = "������ �����Ͻðڽ��ϱ�?";
			}
			
			classifyMember();
			
    		mProgressDialog.dismiss();      

    		setTitle("���� ���� - "+BungaeDetail.get(0).getBungaeTitle());
    		
//           Toast.makeText(MyBungaeDetailActivity.this, "���� �� ��� ����", 0).show(); 
           	scrollView.setSmoothScrollingEnabled(true);
   	    	scrollView.smoothScrollTo(0, 0);
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
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
    						
    						
    						if (enStr.equals("%0A%09"))
    						{
    							selectedBungaeData.setBungaeCategory("�Ϲ�");
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
    
    
    
    
    public class NewBungaeDetailParserTask extends AsyncTask<String, Integer, String>{  
    	private MyBungaeDetailActivity mActivity;    
//    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	boolean boolean_b_num = false,
    			boolean_b_cur = false,
    			boolean_b_members = false;
    	
    	public NewBungaeDetailParserTask(MyBungaeDetailActivity activity) {  
    		mActivity = activity;     
    		}       
    	  
    	@Override    
    	protected void onPreExecute() { 
    		if (NewBungaeData.size() != 0)
    		{
    			NewBungaeData.clear();
    		}
    		
    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("ó����...");     
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
    	            buffer.append("num").append("=").append(BungaeDetail.get(0).getBungaeNum()); 
    	           
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
    		
    		if (NewBungaeData.size() != 0)
    		{
//    			Toast.makeText(MyBungaeDetailActivity.this, "������ ����ֽ��ϴ�", 0).show(); 
    			
    			if (deleteNotEnterFlag == 0)
    			{
    				DeleteBungaeTask task = new DeleteBungaeTask();   
					task.execute(BUNGAE_DELETE_URL);
    			}
    			else if (deleteNotEnterFlag == 1)
    			{
    				OutBungaeTask task = new OutBungaeTask();   
					task.execute(OUT_BUNGAE_URL);
    			}
    			
    		}
    		else if (NewBungaeData.size() == 0)
    		{
//    			Toast.makeText(MyBungaeDetailActivity.this, "������ �����Ǿ����ϴ�", 0).show(); 
    			AlertDialog.Builder builder = new AlertDialog.Builder(MyBungaeDetailActivity.this);
				builder.setTitle("������ ����")
			    .setMessage("��� ������ �����Դϴ�.")
			    .setCancelable(true)
			    .setNeutralButton("Ȯ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onBackPressed();
					}
				})
			    .create().show();
				
				mProgressDialog.dismiss();	
    		}
			
    		

           
    		}         
    	
    	
    	public String parseXml(InputStream is) throws IOException, XmlPullParserException {    
    		XmlPullParser parser = Xml.newPullParser();     
    		try {
    			
    			parser.setInput(is, null);      
    			int eventType = parser.getEventType();     
    			NewBungaeDetailData newBungae = null;              
    			while (eventType != XmlPullParser.END_DOCUMENT) {   
    				String tag = null;     
    				switch (eventType) {    
    				case XmlPullParser.START_TAG:        
    					tag = parser.getName(); 
    					
    					if(tag.equals("bungae"))
    						newBungae = new NewBungaeDetailData();
    					if(tag.equals("b_num"))
    						boolean_b_num = true;
    					if(tag.equals("b_cur"))
    						boolean_b_cur = true;
    					if(tag.equals("b_members"))
    						boolean_b_members = true;
    						                                   
    					break;           
    					
    				case XmlPullParser.TEXT:
    					if(boolean_b_num){
    						newBungae.setBungaeNum(parser.getText());
    						Log.d("�Ľ� �׽�Ʈ", parser.getText());
    						boolean_b_num = false;
    					}
    					if(boolean_b_cur){
    						newBungae.setBungaeCur(parser.getText());
    						boolean_b_cur = false;
    					}
    					if(boolean_b_members){
    						newBungae.setBungaeMembers(parser.getText());
    						boolean_b_members = false;
    					}
    					
    					break;
    					
    					
    					case XmlPullParser.END_TAG:  
    						
    						tag = parser.getName();
    						if (tag.equalsIgnoreCase("bungae") && newBungae != null){
    							NewBungaeData.add(newBungae);
    							completeString = "complete";
    							
    						}
    						else if (tag.equalsIgnoreCase("nowbungae"))
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
    
    
    
    
    
    
    public class OutBungaeTask extends AsyncTask<String, Integer, String>{  
//    	private MyBungaeDetailActivity mActivity;     
//    	private ProgressDialog mProgressDialog;   

    	private String updateMemberStr;
    	
    	private String completeString;

//    	public OutBungaeTask(MyBungaeDetailActivity activity) {  
//    		mActivity = activity;     
//    		}    
    	  
    	@Override    
    	protected void onPreExecute() { 
    		
    		updateMemberStr = new String();
    		
//    		Log.d("����", BungaeDetail.get(0).getBungaeMembers());
    		
    		
    		if (NewBungaeData.get(0).getBungaeMembers().indexOf(", "+enterInfoString) != -1)
    		{
    			updateMemberStr = NewBungaeData.get(0).getBungaeMembers().replace(", "+enterInfoString, "");
    		}
    		
//    		Log.d("����", updateMemberStr);
    		
//    		mProgressDialog = new ProgressDialog(mActivity);     
//    		mProgressDialog.setMessage("���� ������...");     
//    		mProgressDialog.show();      
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
    	            buffer.append("num").append("=").append(NewBungaeData.get(0).getBungaeNum()+"&")
    	            .append("b_members").append("=").append(updateMemberStr+"&")
    	            .append("b_cur").append("=").append((Integer.parseInt(NewBungaeData.get(0).getBungaeCur())-1));
    	           
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
    		
    		onBackPressed();
    		
    		mProgressDialog.dismiss();      

    		
    		
    		}         
    	
    	
    	public String parseXml(InputStream is) throws IOException, XmlPullParserException {    
    		XmlPullParser parser = Xml.newPullParser();     
    		try {
    			completeString = "complete";
    			parser.setInput(is, null);      
    			int eventType = parser.getEventType();             
    			while (eventType != XmlPullParser.END_DOCUMENT) {   
 
    				switch (eventType) {    
    				case XmlPullParser.START_TAG:      
    					      
    					break;           
    					
    				case XmlPullParser.TEXT:
    					    					
    					break;
    					
    					
    				case XmlPullParser.END_TAG:  
    						    						
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
    
    
    
    
    
    public class DeleteBungaeTask extends AsyncTask<String, Integer, String>{  
//    	private MyBungaeDetailActivity mActivity;      
//    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;

//    	public DeleteBungaeTask(MyBungaeDetailActivity activity) {  
//    		mActivity = activity;     
//    		}      
    	  
    	@Override    
    	protected void onPreExecute() { 
    		
    		for (int i = 0; i<SelectedMemberList.size();i++)
    		{
    			if (i==0)
    			{
    				deletePushId = SelectedMemberList.get(i).getMemberPushId();
    			}
    			else
    			{
    				deletePushId = deletePushId+","+SelectedMemberList.get(i).getMemberPushId();
    			}
    		}
    		
//    		Log.d("���������", deletePushId);
    		
//    		mProgressDialog = new ProgressDialog(mActivity);     
//    		mProgressDialog.setMessage("���� ������...");     
//    		mProgressDialog.show();      
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
    	            buffer.append("num").append("=").append(BungaeDetail.get(0).getBungaeNum()+"&")
    	            .append("delete_push_id").append("=").append(deletePushId);
    	           
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
    		
    		onBackPressed();
    		
    		mProgressDialog.dismiss();      

    		
    		
    		}         
    	
    	
    	public String parseXml(InputStream is) throws IOException, XmlPullParserException {    
    		XmlPullParser parser = Xml.newPullParser();     
    		try {
    			completeString = "complete";
    			parser.setInput(is, null);      
    			int eventType = parser.getEventType();             
    			while (eventType != XmlPullParser.END_DOCUMENT) {   
 
    				switch (eventType) {    
    				case XmlPullParser.START_TAG:      
    					      
    					break;           
    					
    				case XmlPullParser.TEXT:
    					    					
    					break;
    					
    					
    				case XmlPullParser.END_TAG:  
    						    						
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