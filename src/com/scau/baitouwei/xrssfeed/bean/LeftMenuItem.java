package com.scau.baitouwei.xrssfeed.bean;

public class LeftMenuItem {
	private int ico;
	private String name;
	private String key;
	private int alert_num;

	public LeftMenuItem(int ico, String name, int alert_num, String key) {
		this.ico = ico;
		this.name = name;
		this.alert_num = alert_num;
		this.key = key;
	}

	public int getIco() {
		return ico;
	}

	public void setIco(int ico) {
		this.ico = ico;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getAlert_num() {
		return alert_num;
	}

	public void setAlert_num(int alert_num) {
		this.alert_num = alert_num;
	}

}
