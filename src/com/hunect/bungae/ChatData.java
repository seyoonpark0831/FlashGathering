package com.hunect.bungae;

import java.util.Date;

public class ChatData {
	 
    private String chatNum;

    private String chatId;

    private String chatText;
    
    private String chatTime;
    
    private Date chatTimeConvert;
    

    

    public String getChatNum() {

          return chatNum;

    }

    public void setChatNum(String chatNum) {

          this.chatNum = chatNum;

    }

    public String getChatId() {

        return chatId;

    }

    public void setChatId(String chatId) {

        this.chatId = chatId;

    }
    
    public String getChatText() {

        return chatText;

    }

    public void setChatText(String chatText) {

        this.chatText = chatText;

    }
    
    public String getChatTime() {

        return chatTime;

    }

    public void setChatTime(String chatTime) {

        this.chatTime = chatTime;

    }
    
    public Date getChatTimeConvert() {

    	return chatTimeConvert;

    }

    public void setChatTimeConvert(Date chatTimeConvert) {

    	this.chatTimeConvert = chatTimeConvert;

    }
    
}