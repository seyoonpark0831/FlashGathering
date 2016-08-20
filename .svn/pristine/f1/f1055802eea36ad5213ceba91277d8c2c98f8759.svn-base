package com.hunect.bungae;

import java.util.Map;

public class AddBungaeMapClass {
	
	private static AddBungaeMapClass mInstance = null;
	
	public Map<String, String> addBungaeMapInfo;
	
	protected AddBungaeMapClass(){};
	
	public static synchronized AddBungaeMapClass getInstance(){
		if(mInstance == null){
			mInstance = new AddBungaeMapClass();
		}
		return mInstance;
	}
}
