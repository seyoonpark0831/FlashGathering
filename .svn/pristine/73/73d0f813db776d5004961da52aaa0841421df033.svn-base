package com.hunect.bungae;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BungaeListAdapter extends ArrayAdapter<BungaeListData>{ 
	private LayoutInflater mInflater;   
	private TextView category;
	private TextView title;         
	private TextView time; 
	private TextView host;
	private TextView memberNum;
	private ImageView bungaeIcon;
	
	public BungaeListAdapter(Context context, List<BungaeListData> objects) {    
		super(context, 0, objects);       
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);     
		}      
	
	
	@Override   
	public View getView(int position, View convertView, ViewGroup parent) {  
		View view = convertView;           
		if (convertView == null) {       
			view = mInflater.inflate(R.layout.bungae_list_row, null);    
			}                  

		BungaeListData item = this.getItem(position);
		if (item != null) {
			
			this.bungaeIcon = (ImageView) view.findViewById(R.id.bungaeIcon);
			if (item.getBungaeOpenPrivate().toString().equals("0"))
			{
				this.bungaeIcon.setImageResource(R.drawable.lightning1);
			}
			else
			{
				this.bungaeIcon.setImageResource(R.drawable.lock);
			}
			
			
			
			String category = item.getBungaeCategory().toString();
			String categoryString = new String();
			
			String enStr = new String();
			
			try {
				enStr = URLEncoder.encode(category, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if (enStr.equals("%0A%09"))
			{
				category = "일반";
			}
			
			categoryString = "["+category+" 번개]";

			
			
			this.category = (TextView) view.findViewById(R.id.categoryLabel);
			this.category.setText(categoryString);
			
			String title = item.getBungaeTitle().toString();     
			this.title = (TextView) view.findViewById(R.id.titleLabel);    
			this.title.setPaintFlags(this.title.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
			this.title.setText(title);              
			
			//String time = item.getBungaeTime().toString();   
			
						
			String timeStr = (item.getBungaeTimeConvert().getMonth()+1)+"월"+item.getBungaeTimeConvert().getDate()+"일  "+item.getBungaeTimeConvert().getHours()+"시"+item.getBungaeTimeConvert().getMinutes()+"분";
			this.time = (TextView) view.findViewById(R.id.timeLabel);       
			this.time.setText(timeStr);
			
			String host = item.getBungaeHostId().toString();       
			this.host = (TextView) view.findViewById(R.id.hostLabel);       
			this.host.setText(host);
			
			String memberNum;
			
			if ( item.getBungaeMax().equals("11") )
				memberNum = item.getBungaeCur() + " / " + "∞";
			else
				memberNum = item.getBungaeCur() + " / " + item.getBungaeMax();
			
			this.memberNum = (TextView) view.findViewById(R.id.numLabel);       
			this.memberNum.setText(memberNum);
			
			}                
		return view;      
		} 
	//} 
	}
//}
