package com.scau.baitouwei.xrssfeed.rss;

public class RssAddressInfo {
	public int id;
	public String title;
	public String address;
	public RssAddressInfo(String title,String address){
		this.address=address;
		this.title=title;
	}
	public RssAddressInfo(){
		super();
	}
}
