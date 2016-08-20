package com.hunect.bungae;

import java.util.Map;

public class UserInfoClass {
	
	private static UserInfoClass mInstance = null;
	
	public Map<String, String> userInfo;
	
	protected UserInfoClass(){};
	
	public static synchronized UserInfoClass getInstance(){
		if(mInstance == null){
			mInstance = new UserInfoClass();
		}
		return mInstance;
	}
}
