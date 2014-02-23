package com.scau.baitouwei.xrssfeed.util;

public class Utils {

	public static boolean IsStringNUll(String data) {
		if (data == null || data.trim().equals(""))
			return true;
		return false;
	}

	public static String AddSingleQuotes(String data) {
		return "'" + data + "'";
	}
}
