package com.hunect.bungae;


import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import net.daum.mobilead.AdConfig;
import net.daum.mobilead.AdHttpListener;
import net.daum.mobilead.MobileAdView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Report extends Activity implements AdHttpListener {
	
	private TextView rTargetIdText;
	private TextView rReqestIdText;
	private EditText rDetailText;
	private Button rSendButton;
	private StringBuffer buffer;
	
	private MobileAdView adView = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.report);
	    
	    initAdam();
	    
	    //키보드가 올라와 있는 상태로 로드되는 것을 방지
	  	this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	  	
	  	rTargetIdText = (TextView)findViewById(R.id.rTargetId);
	  	rReqestIdText = (TextView)findViewById(R.id.rRequestId);
	  	rDetailText = (EditText)findViewById(R.id.rDetailText);
	  	rSendButton = (Button)findViewById(R.id.rSendButton);
	  	
	  	rTargetIdText.setText(getIntent().getStringExtra("bHostId"));
	  	rReqestIdText.setText(UserInfoClass.getInstance().userInfo.get("u_id"));
	  	
	  	rSendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(Report.this);
				builder.setTitle("신고하기")
				.setMessage("신고를 접수하시겠습니까?")
				.setCancelable(true)
				.setNegativeButton("취소", null)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int id) {

						buffer = new StringBuffer(); 
	    	            buffer.append("r_id=").append(rTargetIdText.getText().toString()).append("&")
	    	            	  .append("r_req_id=").append(rReqestIdText.getText().toString()).append("&")
	    	            	  .append("r_reason=").append(rDetailText.getText().toString());
	    	            
						ReportParserTask task = new ReportParserTask();   
						task.execute(BungaeActivity.MAIN_URL + "/reportuser.php");
					}
				})
				.create().show();
				
			}
		});
	  	
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
		
        // 할당 받은 clientId 설정
        AdConfig.setClientId("1c3aZ5ZT134b0fed431");
        
        // Ad@m sdk 초기화 시작
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
	
    public class ReportParserTask extends AsyncTask<String, Integer, String>{
    	
    	private ProgressDialog mProgressDialog;
    	private String completeString;
    	
    	@Override    
    	protected void onPreExecute() { 
    		
    		mProgressDialog = new ProgressDialog(Report.this);     
    		mProgressDialog.setMessage("전송중...");
    		mProgressDialog.show();      
    		}      
    	
    	@Override    
    	protected String doInBackground(String... params) {
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
    		
    		mProgressDialog.dismiss();
    		
    		Toast.makeText(Report.this, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
    		
    		finish();
    	}
    	

    }

}
