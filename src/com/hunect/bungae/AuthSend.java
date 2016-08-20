package com.hunect.bungae;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AuthSend extends Activity {
	
	private static final String CRYPTO_SEED_PASSWORD = "abcdefghijuklmno0123456789012345";
	
	private EditText numText1, numText2, numText3;
	private Button authSendButton;
	public Map<String, String> authInfo;
	public String authCaseFromServer;
	
	public StringBuffer buffer;
	

	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.auth_send);
	    setTitle("�޴��ȣ ����");
	    
	    numText1 = (EditText)findViewById(R.id.numText1);
	    numText2 = (EditText)findViewById(R.id.numText2);
	    numText3 = (EditText)findViewById(R.id.numText3);
	    authSendButton = (Button)findViewById(R.id.authSendButton);

	    numText1.addTextChangedListener(new GenericTextWatcher(numText1));
	    numText2.addTextChangedListener(new GenericTextWatcher(numText2));
	    
	    //C2DMTestActivity���� �Ѿ�� pid�� �޾� ���� class�� ���
	    authInfo = new HashMap<String, String>();
	    authInfo.put("auth_pid", getIntent().getStringExtra("pid"));
//	    Toast.makeText(AuthSend.this, "pid : "+authInfo.get("auth_pid"), 0).show();
	    
	    authSendButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//�Է� �ڸ��� �˻�
				if(digitCheck()) {
					//��Ʈ��ũ�� ����Ǿ� �ִ��� Ȯ��
					if(isOnline()) {
						String numSeg1 = numText1.getText().toString();
						String numSeg2 = numText2.getText().toString();
						String numSeg3 = numText3.getText().toString();
						StringBuffer phoneNumBuffer = new StringBuffer();
						String phoneNum = new String();
						
						//������ �� ��ȣ�� ���ĺ��ڸ� �غ�
						phoneNum = phoneNumBuffer.append(numSeg1).append(numSeg2).append(numSeg3).toString();
						String deviceId = GetDeviceId();
						String phoneNumEncrypted = new String();
						String phoneNumEncryptedEncoded = new String();
						
						try{
							phoneNumEncrypted = SimpleCrypto2.encrypt1(CRYPTO_SEED_PASSWORD, phoneNum);
							phoneNumEncryptedEncoded = URLEncoder.encode(phoneNumEncrypted, "UTF-8");
						}catch(Exception e){
						}
						
						//������ buffer data ����
						buffer = new StringBuffer();
						buffer.append("phonenum").append("=").append(phoneNumEncryptedEncoded).append("&")
							  .append("udid").append("=").append(deviceId);
						
						
						//������ ����
			        	AuthSendParserTask task = new AuthSendParserTask(AuthSend.this);
			        	task.execute(BungaeActivity.MAIN_URL + "/auth_views_php/authRequest-R.php");
			            
						
						//�ļ� action: �Ľ� �޼��� ���� postExecute���� ����
						
//			        	//auth_case�� ���� dialog ó��
//			    		showAuthAlert(authCaseFromServer);
						
					}
					
					else {
						AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
						builder.setTitle("��Ʈ��ũ ����")
						.setMessage("��Ʈ��ũ ������ Ȯ���� �� �ٽ� �õ��� �ּ���.")
						.setCancelable(true)
						.setNeutralButton("Ȯ��", null)
						.create().show();
					}
				} //if(digitCheck())
				
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
					builder.setTitle("�߸��� ��ȣ")
					.setMessage("�ùٸ� ��ȣ�� �Է��� �ּ���.")
					.setCancelable(true)
					.setNeutralButton("Ȯ��", null)
					.create().show();
				}
				
			}
		});
	    
	}
	
	public boolean digitCheck() {
		if(numText1.getText().length()==3 && 
				(numText2.getText().length()+numText3.getText().length()==7 || 
				 numText2.getText().length()+numText3.getText().length()==8))
			return true;
		else return false;
	}
	
	public void showAuthAlert(String authCase) {
		
		//auth_case==0or3�� ��� ���� �Ϸ� ���̾�α� ���� ���� ��Ƽ��Ƽ�� �Ѿ��
		if(authCase.equals("0") || authCase.equals("3")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
			builder.setTitle("������ȣ ����")
			.setMessage("������ȣ�� ���۵Ǿ����ϴ�.")
			.setCancelable(true)
			.setNeutralButton("���� �ܰ�", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					
					Intent intent = new Intent(AuthSend.this, AuthConfirm.class);
					startActivity(intent);
					finish();
				}
			})
			.create().show();
		}
		
		else if(authCase.equals("1")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
			builder.setTitle("��ϵ� ��ȣ")
			.setMessage("�̹� ��ϵ� ��ȣ�Դϴ�. �� ��ȣ�� �ϳ��� ������ �̿� �����մϴ�.")
			.setCancelable(true)
			.setNeutralButton("Ȯ��", null)
			.create().show();
		}
		
		else if(authCase.equals("2") || authCase.equals("4")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
			builder.setTitle("��ϵ� ���")
			.setMessage("�̹� ��ϵ� ����Դϴ�. �� ���� �ϳ��� ������ �̿� �����մϴ�.")
			.setCancelable(true)
			.setNeutralButton("Ȯ��", null)
			.create().show();
		}
		
		else if(authCase == null || authCase.equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(AuthSend.this);
			builder.setTitle("��Ʈ��ũ ����")
			.setMessage("��Ʈ��ũ �����Դϴ�. �ٽ� �õ��� �ּ���.")
			.setCancelable(true)
			.setNeutralButton("Ȯ��", null)
			.create().show();
		}
		
	}
	
	private class GenericTextWatcher implements TextWatcher{

	    private View view;
	    private GenericTextWatcher(View view) {
	        this.view = view;
	    }

	    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	    public void afterTextChanged(Editable editable) {
	        switch(view.getId()){
	            case R.id.numText1:
	            	if(numText1.getText().length()==3)
	            		numText2.requestFocus();
	                break;
	            case R.id.numText2:
	            	if(numText2.getText().length()==4)
	            		numText3.requestFocus();
	                break;
	            default:
	            	break;
	        }
	    }
	}

	public String GetDeviceId() {

		TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		
		//deviceId�� null�̸� �� ����� ���� ����� ��: ANDROID_ID�� ��ȯ
		if(deviceId==null){String androidId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID); 
			return androidId;
		}
		else {
			return deviceId;
		}
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    else return false;
	}
	
	
	public class AuthSendParserTask extends AsyncTask<String, Integer, String>{  
    	private AuthSend mActivity;    
    	private ProgressDialog mProgressDialog;   
    	
    	private String completeString;
    	
    	private boolean boolean_auth_num = false,
						boolean_auth_phone = false,
						boolean_auth_udid = false,
						boolean_auth_case = false;
    	
    	public AuthSendParserTask(AuthSend activity) {  
    		mActivity = activity;     
    	}

    	@Override    
    	protected void onPreExecute() {
    		
    		mProgressDialog = new ProgressDialog(mActivity);     
    		mProgressDialog.setMessage("������...");     
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
    		
    		authCaseFromServer = new String();
    		authCaseFromServer = AuthInfoClass.getInstance().authInfo.get("auth_case");
    		mProgressDialog.dismiss();
    		
    		//�Ľ� �ļ�ó��
    		//auth_case�� ���� dialog ó��
    		showAuthAlert(authCaseFromServer);
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
    					
//    					if(tag.equals("auth_form"))		authInfo = new HashMap<String, String>();
    					
    					if(tag.equals("auth_num"))		boolean_auth_num = true;
     					if(tag.equals("auth_phone"))	boolean_auth_phone = true;
     					if(tag.equals("auth_udid"))		boolean_auth_udid = true;
     					if(tag.equals("auth_case"))		boolean_auth_case = true;
     					    						                                   
    					break;
    					
    				case XmlPullParser.TEXT:
    					
    					if(boolean_auth_num){
     						authInfo.put("auth_num",parser.getText());
     						boolean_auth_num = false;
     					}
     					if(boolean_auth_phone){
     						authInfo.put("auth_phone",parser.getText());
     						boolean_auth_phone = false;
     					}
     					if(boolean_auth_udid){
     						authInfo.put("auth_udid",parser.getText());
     						boolean_auth_udid = false;
     					}
     					if(boolean_auth_case){
     						authInfo.put("auth_case",parser.getText());
     						boolean_auth_case = false;
     					}
     					
    					break;
    					
    					
					case XmlPullParser.END_TAG:  
						
						tag = parser.getName();
						if (tag.equalsIgnoreCase("auth_form") && !authInfo.isEmpty()){
							AuthInfoClass.getInstance().authInfo = authInfo;
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
