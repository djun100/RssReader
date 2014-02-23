package com.scau.baitouwei.xrssfeed.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "DBHelper";
	public static final String DB_NAME = "scau_feed.db";
	public static final int VERNAME = 1;

	public static final String DB_TABLE_PACKETS = "packets_table";
	public static final String DB_TABLE_FEEDS = "feeds_table";
	public static final String DB_TABLE_ITEMS = "items_table";

	public static final String ID = "id";
	public static final String FEED_ID = "feed_id";
	public static final String ITEM_ID = "item_id";
	public static final String PACKET_ID = "packet_id";
	public static final String PACKET_NAME="packet_name";	
	public static final String TITLE = "title"; 
	public static final String LINK = "link";
	public static final String DESCRIPTION ="description";
	public static final String PUBDATE = "pubdate";
	public static final String FEED_LASTBUILDDATE = "lastbuilddate";
	public static final String FEED_RSS_LINK = "rss_link";
	public static final String FEED_ADDRESS = "address";
	public static final String FEED_ICO = "ico";
//	public static final String FEED_ALERT_NUM = "alert_num";
	public static final String ITEM_IS_READ = "is_read";
	public static final String ITEM_IS_STARED = "is_stared";
	public static final String ITEM_THUMBNAIL = "thumbnail";
	
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERNAME);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table if not exists " + DB_TABLE_PACKETS + 
				" ("+
				ID+" integer primary key autoincrement,"+
				PACKET_NAME+" nvarchar(50) not null unique"
				+");"; 
		Log.d(TAG, "sql:"+sql);
		db.execSQL(sql);
		sql = "create table if not exists " + DB_TABLE_FEEDS + 
				" ("+
				ID+" integer primary key autoincrement,"+
				PACKET_ID +" integer," +
				TITLE + " nvarchar(200),"+
				LINK + " nvarchar(500),"+
				DESCRIPTION + " text,"+
				PUBDATE + " datetime default (datetime('now', 'localtime')),"+
				FEED_RSS_LINK + " nvarchar(500) not null unique,"+
				FEED_LASTBUILDDATE + " datetime default (datetime('now', 'localtime')),"+
				FEED_ICO + " nvarchar(50)"
//				FEED_ALERT_NUM + " integer default 0 check(" + FEED_ALERT_NUM + " >= 0" + ")"
//				"foreign key("+PACKET_ID +") references "+ DB_TABLE_PACKETS+"( "+ID+" )"
				+");"; 
		db.execSQL(sql);
		sql = "create table if not exists " + DB_TABLE_ITEMS + 
				" ("+
				ID+" integer primary key autoincrement,"+
				FEED_ID +" integer," +
				TITLE + " nvarchar(200),"+
				LINK + " nvarchar(500),"+
				DESCRIPTION + " text,"+
				PUBDATE + " datetime default (datetime('now','localtime')),"+
				ITEM_IS_READ + " integer,"+
				ITEM_IS_STARED + " integer,"+
				ITEM_THUMBNAIL + " text,"+
				"foreign key("+FEED_ID +") references "+ DB_TABLE_FEEDS+"( "+ID+" )"
				+");"; 
		db.execSQL(sql);
		
		/*
		 *创建触发器  feed自动更新未读数目
		 */
		
//		sql = "create trigger item_Insert after insert on " + DB_TABLE_ITEMS
//				+" for each row begin "+" update " + DB_TABLE_FEEDS +
//				" set " + FEED_ALERT_NUM + " = " + 
//				FEED_ALERT_NUM +"+1"+
//				" where " + ID + " = " + "new."+FEED_ID + ";"+
//				" end;";
//		db.execSQL(sql);
//
//		sql = "create trigger item_Delect after delete on " + DBHelper.DB_TABLE_ITEMS
//				+" for each row begin "+" update " + DB_TABLE_FEEDS +
//				" set " + FEED_ALERT_NUM + " = " + 
//				FEED_ALERT_NUM +"-1"+
//				" where " + ID + " = " + "new."+FEED_ID + ";"+
//				" end;";
//		db.execSQL(sql);
//		
//		sql = "create trigger item_Update_add after update on " + DBHelper.DB_TABLE_ITEMS
//				+" when new."+ITEM_IS_READ+" = '0' and old." + ITEM_IS_READ +" = 1"
//				+" begin "+" update " + DB_TABLE_FEEDS +
//				" set " + FEED_ALERT_NUM + " = " + 
//				FEED_ALERT_NUM +"+1"+
//				" where " + ID + " = " + "new."+FEED_ID + ";"+
//				" end;";
//		db.execSQL(sql);
//		
//		sql = "create trigger item_Update_cut after update on " + DBHelper.DB_TABLE_ITEMS
//				+" when new."+ITEM_IS_READ+" = '1' and old." + ITEM_IS_READ +" = 0"
//				+" begin "+" update " + DB_TABLE_FEEDS +
//				" set " + FEED_ALERT_NUM + " = " + 
//				FEED_ALERT_NUM +"-1"+
//				" where " + ID + " = " + "new."+FEED_ID + ";"+
//				" end;";
//		db.execSQL(sql);
		
}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
