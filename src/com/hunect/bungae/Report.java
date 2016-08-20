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
	    
	    //Ű���尡 �ö�� �ִ� ���·� �ε�Ǵ� ���� ����
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
				builder.setTitle("�Ű��ϱ�")
				.setMessage("�Ű� �����Ͻðڽ��ϱ�?")
				.setCancelable(true)
				.setNegativeButton("���", null)
				.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
					
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
	
    public class ReportParserTask extends AsyncTask<String, Integer, String>{
    	
    	private ProgressDialog mProgressDialog;
    	private String completeString;
    	
    	@Override    
    	protected void onPreExecute() { 
    		
    		mProgressDialog = new ProgressDialog(Report.this);     
    		mProgressDialog.setMessage("������...");
    		mProgressDialog.show();      
    		}      
    	
    	@Override    
    	protected String doInBackground(String... params) {
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
    		
    		Toast.makeText(Report.this, "�Ű� �����Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
    		
    		finish();
    	}
    	

    }

}
