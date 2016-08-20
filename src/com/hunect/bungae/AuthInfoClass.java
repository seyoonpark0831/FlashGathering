package com.hunect.bungae;

import java.util.Map;

public class AuthInfoClass{
	
	private static AuthInfoClass mInstance = null;
	
	public Map<String, String> authInfo;
	
	protected AuthInfoClass(){};
	
	public static synchronized AuthInfoClass getInstance(){
		if(mInstance == null){
			mInstance = new AuthInfoClass();
		}
		return mInstance;
	}
}
