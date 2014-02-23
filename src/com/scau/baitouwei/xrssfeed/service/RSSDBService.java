package com.scau.baitouwei.xrssfeed.service;

import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.Dates;
import org.mcsoxford.rss.RSSConfig;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.scau.baitouwei.xrssfeed.db.DBHelper;
import com.scau.baitouwei.xrssfeed.util.Utils;

public class RSSDBService {
	private static final String TAG = "RSSDBService";
	private DBHelper helper;
	private SQLiteDatabase db;
	private static Context mContext;

	private static class SingleHolder {
		private static final RSSDBService instance = new RSSDBService();
	}

	private RSSDBService() {
		super();
		helper = new DBHelper(mContext);
	}

	public static RSSDBService getInstance(Context context) {
		mContext = context;
		return SingleHolder.instance;
	}

	public void addPacket(String packet) {
		if (!Utils.IsStringNUll(packet)) {
			db = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DBHelper.PACKET_NAME, packet);
			db.insert(DBHelper.DB_TABLE_PACKETS, null, values);

		}
	}

	public List<String> getAllPacket() {
		List<String> data = new ArrayList<String>();
		db = helper.getReadableDatabase();
		Cursor cursor = db.query(DBHelper.DB_TABLE_PACKETS, null, null, null,
				null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					data.add(cursor.getString(cursor
							.getColumnIndex(DBHelper.PACKET_NAME)));
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

		return data;
	}

	public void addRSSFeed(RSSFeed feed) {
		Log.d(TAG, "++ addRSSFeed ++");
		if (feed != null) {
			// insert RSSFeed
			db = helper.getWritableDatabase();
			Log.d(TAG, "++ feed.getTitle():" + feed.getTitle() + " ++");
			ContentValues values = RSSFeed2ContentValues(feed);
			if (!Utils.IsStringNUll(feed.getPacket())) {
				int packetId = getID(
						db,
						DBHelper.DB_TABLE_PACKETS,
						DBHelper.PACKET_NAME + " = "
								+ Utils.AddSingleQuotes(feed.getPacket()),
						DBHelper.ID);
				if (packetId < 0) {
					values.put(DBHelper.PACKET_ID, "");
				}
				values.put(DBHelper.PACKET_ID, packetId);
				Log.d(TAG, "++ packetId:" + packetId + " ++");
			} else {
				values.put(DBHelper.PACKET_ID, "");
			}
			db.insert(DBHelper.DB_TABLE_FEEDS, null, values);

			// get feed id
			int feedId = getID(
					db,
					DBHelper.DB_TABLE_FEEDS,
					DBHelper.FEED_RSS_LINK
							+ " = "
							+ Utils.AddSingleQuotes(feed.getRssLink()
									.toString()), DBHelper.ID);
			if (feedId < 0) {

				return;
			}

			// insert RSSItems
			if (feed.getItems() != null) {
				List<RSSItem> items = feed.getItems();
				for (int i = 0; i < items.size(); i++) {
					db.insert(DBHelper.DB_TABLE_ITEMS, null,
							RSSItem2ContentValues(items.get(i), feedId));
				}
			}

		}
	}

	public void refreshRSSFeed(RSSFeed feed) {
		if (feed != null) {
			// update RSSFeed
			db = helper.getWritableDatabase();
			Log.d(TAG, "++ feed.getTitle():" + feed.getTitle() + " ++");
			ContentValues values = RSSFeed2ContentValues(feed);
			values.remove(DBHelper.PACKET_ID);
			db.update(
					DBHelper.DB_TABLE_FEEDS,
					values,
					DBHelper.FEED_RSS_LINK
							+ " = "
							+ Utils.AddSingleQuotes(feed.getRssLink()
									.toString()), null);

		}
	}

	public void refreshRSSFeedList(List<RSSFeed> list) {
		if (list != null) {
			Log.d(TAG, "++ refreshFeedByList ++");
			db = helper.getWritableDatabase();
			db.beginTransaction();
			for (RSSFeed feed : list) {
				ContentValues values = RSSFeed2ContentValues(feed);
				values.remove(DBHelper.PACKET_ID);
				db.update(
						DBHelper.DB_TABLE_FEEDS,
						values,
						DBHelper.FEED_RSS_LINK
								+ " = "
								+ Utils.AddSingleQuotes(feed.getRssLink()
										.toString()), null);
			}
			db.endTransaction();

		}
	}

	public void changeItemStar(RSSItem item) {
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBHelper.ITEM_IS_STARED, !item.isStarted());
		db.update(DBHelper.DB_TABLE_ITEMS, values, DBHelper.TITLE + " = "
				+ Utils.AddSingleQuotes(item.getTitle()), null);

	}

	public void changeItemIsReaded(RSSItem item) {
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBHelper.ITEM_IS_READ, true);
		db.update(DBHelper.DB_TABLE_ITEMS, values, DBHelper.TITLE + " = "
				+ Utils.AddSingleQuotes(item.getTitle()), null);

	}

	public List<RSSFeed> getFeedsByPacket(String packet) {
		List<RSSFeed> feeds = new ArrayList<RSSFeed>();
		db = helper.getReadableDatabase();
		int packetId = getID(db, DBHelper.DB_TABLE_PACKETS,
				DBHelper.PACKET_NAME + " = " + Utils.AddSingleQuotes(packet),
				DBHelper.ID);
		if (packetId < 0) {

			return feeds;
		}
		Cursor cursor = db.query(
				DBHelper.DB_TABLE_FEEDS,
				null,
				DBHelper.PACKET_ID + " = "
						+ Utils.AddSingleQuotes(packetId + ""), null, null,
				null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					feeds.add(Cursor2RSSFeed(cursor));
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

		return feeds;
	}

	public List<RSSItem> getRssItemByRssLink(String rssLink) {
		List<RSSItem> items = new ArrayList<RSSItem>();
		db = helper.getReadableDatabase();
		int feedId = getID(db, DBHelper.DB_TABLE_FEEDS, DBHelper.FEED_RSS_LINK
				+ " = " + Utils.AddSingleQuotes(rssLink), DBHelper.ID);
		Log.d(TAG, "feedId " + feedId);
		if (feedId < 0) {

			return items;
		}
		Cursor cursor = db.query(DBHelper.DB_TABLE_ITEMS, null,
				DBHelper.FEED_ID + " = " + Utils.AddSingleQuotes(feedId + ""),
				null, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					items.add(Cursor2RSSItem(cursor));
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

		return items;
	}

	public List<Uri> getAddressesByPacket(String packet) {
		List<Uri> list = new ArrayList<Uri>();
		db = helper.getReadableDatabase();
		// int packetId = getID(db, DBHelper.DB_TABLE_PACKETS,
		// DBHelper.PACKET_NAME + " = " + Utils.AddSingleQuotes(packet),
		// DBHelper.ID);
		String[] columns = { DBHelper.FEED_RSS_LINK };
		Cursor cursor = db.query(DBHelper.DB_TABLE_FEEDS + ","
				+ DBHelper.DB_TABLE_PACKETS, columns, DBHelper.PACKET_NAME
				+ " = " + Utils.AddSingleQuotes(packet) + " and "
				+ DBHelper.DB_TABLE_PACKETS + "." + DBHelper.ID + " = "
				+ DBHelper.PACKET_ID, null, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					list.add(Uri.parse(cursor.getString(cursor
							.getColumnIndex(DBHelper.FEED_RSS_LINK))));
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

		return list;
	}

	public List<Uri> getAllAddresses() {
		List<Uri> list = new ArrayList<Uri>();
		db = helper.getReadableDatabase();
		String[] columns = { DBHelper.FEED_RSS_LINK };
		Cursor cursor = db.query(DBHelper.DB_TABLE_FEEDS, columns, null, null,
				null, null, null);
		if (cursor != null) {
			if (!db.isOpen())
				Log.d(TAG, "db is closed");
			if (cursor.moveToFirst()) {
				do {
					list.add(Uri.parse(cursor.getString(cursor
							.getColumnIndex(DBHelper.FEED_RSS_LINK))));
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

		return list;
	}

	public List<RSSFeed> getAllFeeds() {
		List<RSSFeed> feeds = new ArrayList<RSSFeed>();
		db = helper.getReadableDatabase();
		Cursor cursor = db.query(DBHelper.DB_TABLE_FEEDS, null, null, null,
				null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					feeds.add(Cursor2RSSFeed(cursor));
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

		Log.d(TAG, "++ return getAllFeeds ++");
		return feeds;
	}

	public List<RSSItem> getItemsStared() {
		List<RSSItem> items = new ArrayList<RSSItem>();
		db = helper.getReadableDatabase();
		Cursor cursor = db.query(DBHelper.DB_TABLE_ITEMS, null,
				DBHelper.ITEM_IS_STARED + " = '1'", null, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					items.add(Cursor2RSSItem(cursor));
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

		return items;
	}

	public List<RSSFeed> getFeedsUnclassified() {
		List<RSSFeed> feeds = new ArrayList<RSSFeed>();
		db = helper.getReadableDatabase();
		Cursor cursor = db.query(DBHelper.DB_TABLE_FEEDS, null,
				DBHelper.PACKET_ID + " = '-1'", null, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					feeds.add(Cursor2RSSFeed(cursor));
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

		return feeds;
	}

	private int getFeedAlertNum(RSSFeed feed) {
		db = helper.getReadableDatabase();
		int feedId = getID(db, DBHelper.DB_TABLE_FEEDS, DBHelper.FEED_RSS_LINK
				+ " = " + Utils.AddSingleQuotes(feed.getRssLink().toString()),
				DBHelper.ID);
		if (feedId < 0) {
			return 0;
		}

		String column = "count(*)";
		Cursor cursor = db.query(DBHelper.DB_TABLE_ITEMS,
				new String[] { column },
				DBHelper.FEED_ID + " = " + Utils.AddSingleQuotes(feedId + "")
						+ " and " + DBHelper.ITEM_IS_READ + " = " + "'0'",
				null, null, null, null);
		if (cursor == null) {
			cursor.close();
			return 0;
		}
		if (cursor.moveToFirst()) {
			int num = cursor.getInt(cursor.getColumnIndex(column));
			cursor.close();
			return num;
		}
		return 0;
	}

	private ContentValues RSSItem2ContentValues(RSSItem item, int feedId) {
		ContentValues val = new ContentValues();
		val.put(DBHelper.FEED_ID, feedId);
		val.put(DBHelper.TITLE, item.getTitle());
		val.put(DBHelper.LINK, item.getLink().toString());
		val.put(DBHelper.DESCRIPTION, item.getDescription());
		// if (item.getPubDate() != null && !item.getPubDate().equals(""))
		// val.put(DBHelper.PUBDATE, item.getPubDate().toString());
		val.put(DBHelper.ITEM_IS_READ, item.isRead());
		val.put(DBHelper.ITEM_IS_STARED, item.isStarted());
		return val;
	}

	private ContentValues RSSFeed2ContentValues(RSSFeed feed) {
		ContentValues values = new ContentValues();
		// values.put(DBHelper.PACKET_NAME, feed.getPacket());
		values.put(DBHelper.TITLE, feed.getTitle());
		values.put(DBHelper.LINK, feed.getLink().toString());
		values.put(DBHelper.DESCRIPTION, feed.getDescription());
		// if (feed.getPubDate() != null)
		// values.put(DBHelper.PUBDATE, feed.getPubDate().toString());
		values.put(DBHelper.FEED_RSS_LINK, feed.getRssLink().toString());
		// if (feed.getLastBuildDate() != null)
		// values.put(DBHelper.FEED_LASTBUILDDATE, feed.getLastBuildDate()
		// .toString());
		// values.put(DBHelper.FEED_ALERT_NUM, feed.getAlert_num());
		return values;
	}

	private int getID(SQLiteDatabase db, String table, String selection,
			String column) {
		Cursor cursor = db.query(table, new String[] { column }, selection,
				null, null, null, null);
		if (cursor == null) {
			cursor.close();

			return -1;
		}
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(column));
			cursor.close();
			return id;
		}
		return -1;
	}

	private RSSItem Cursor2RSSItem(Cursor cursor) {
		RSSConfig config = new RSSConfig();
		RSSItem item = new RSSItem(config.categoryAvg, config.thumbnailAvg);
		item.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.TITLE)));
		item.setLink(Uri.parse(cursor.getString(cursor
				.getColumnIndex(DBHelper.LINK))));
		item.setDescription(cursor.getString(cursor
				.getColumnIndex(DBHelper.DESCRIPTION)));
		String dateString = cursor.getString(cursor
				.getColumnIndex(DBHelper.PUBDATE));
		// if (!Utils.IsStringNUll(dateString))
		// item.setPubDate(Dates.parseRfc822(dateString));
		item.setRead(cursor.getInt(cursor.getColumnIndex(DBHelper.ITEM_IS_READ)) == 1 ? true
				: false);
		item.setStarted(cursor.getInt(cursor
				.getColumnIndex(DBHelper.ITEM_IS_STARED)) == 1 ? true : false);
		return item;
	}

	private RSSFeed Cursor2RSSFeed(Cursor cursor) {
		RSSFeed feed = new RSSFeed();
		// feed.setPacket(cursor.getString(cursor
		// .getColumnIndex(DBHelper.PACKET_NAME)));
		feed.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.TITLE)));
		feed.setLink(Uri.parse(cursor.getString(cursor
				.getColumnIndex(DBHelper.LINK))));
		feed.setDescription(cursor.getString(cursor
				.getColumnIndex(DBHelper.DESCRIPTION)));
		String dateString = cursor.getString(cursor
				.getColumnIndex(DBHelper.PUBDATE));
		// if (!Utils.IsStringNUll(dateString))
		// feed.setPubDate(Dates.parseRfc822(dateString));
		feed.setRssLink(Uri.parse(cursor.getString(cursor
				.getColumnIndex(DBHelper.FEED_RSS_LINK))));
		dateString = cursor.getString(cursor
				.getColumnIndex(DBHelper.FEED_LASTBUILDDATE));
		// if (!Utils.IsStringNUll(dateString))
		// feed.setLastBuildDate(Dates.parseRfc822(dateString));
		feed.setAlert_num(getFeedAlertNum(feed));
		return feed;
	}

	public void closeDB() {
		db.close();
	}
}
