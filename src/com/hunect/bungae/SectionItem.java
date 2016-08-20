package com.hunect.bungae;

public class SectionItem implements MyBungaeItem{

	private final String title;
	
	public SectionItem(String title) {
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
	
	public boolean isSection() {
		return true;
	}
}
