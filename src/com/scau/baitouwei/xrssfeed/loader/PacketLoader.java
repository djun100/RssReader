package com.scau.baitouwei.xrssfeed.loader;

import java.util.List;

import org.mcsoxford.rss.RSSFeed;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.scau.baitouwei.xrssfeed.R;
import com.scau.baitouwei.xrssfeed.observer.FeedsUpdateObserver;
import com.scau.baitouwei.xrssfeed.service.RSSDBService;

public class PacketLoader extends AsyncTaskLoader<List<RSSFeed>> {
	private static final String TAG = "PacketLoader";
	private String packet;
	private String[] entry;
	private List<RSSFeed> mData;

	private FeedsUpdateObserver feedObserver;

	public PacketLoader(Context context, String packet) {
		super(context);
		this.packet = packet;
		entry = getContext().getResources().getStringArray(
				R.array.left_menu_items);
	}

	@Override
	public List<RSSFeed> loadInBackground() {
		if (packet != null) {
			if (packet.equals(entry[0])) {
				Log.d(TAG, "++ getAllFeeds ++");
				return RSSDBService.getInstance(getContext()).getAllFeeds();
			} else if (packet.equals(entry[1])) {
				Log.d(TAG, "++ getItems ++");
				// return
				// RSSDBService.getInstance(getContext()).getItemsStared();
			} else if (packet.equals(entry[2])) {
				Log.d(TAG, "++ getFeedsUnclassfied ++");
				return RSSDBService.getInstance(getContext())
						.getFeedsUnclassified();
			} else {
				Log.d(TAG, "++ getFeedsByPacket ++");
				return RSSDBService.getInstance(getContext()).getFeedsByPacket(
						packet);
			}
		}
		Log.d(TAG, "++ packet is null. getAllFeeds ++");
		return RSSDBService.getInstance(getContext()).getAllFeeds();
	}

	public void reLoad(String packet) {
		this.packet = packet;
		super.forceLoad();
	}

	@Override
	public void deliverResult(List<RSSFeed> data) {
		if (isReset()) {
			if (data != null) {
				releaseResources(data);
				return;
			}
		}

		List<RSSFeed> oldDate = mData;
		mData = data;

		if (isStarted()) {
			super.deliverResult(data);
		}

		if (oldDate != null && oldDate != data) {
			releaseResources(oldDate);
		}
	}

	@Override
	protected void onStartLoading() {
		if (mData != null)
			deliverResult(mData);
		if (feedObserver == null) {
			// feedObserver = new FeedsUpdateObserver(this);
		}
		if (takeContentChanged()) {
			forceLoad();
		} else if (mData == null) {
			forceLoad();
		}
	}

	@Override
	public void stopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		onStopLoading();
		if (mData != null) {
			releaseResources(mData);
			mData = null;
		}

		if (feedObserver != null) {
			// getContext().unregisterReceiver(feedObserver);
			// feedObserver = null;
		}
	}

	@Override
	public void onCanceled(List<RSSFeed> data) {
		super.onCanceled(data);
		releaseResources(data);
	}

	private void releaseResources(List<RSSFeed> data) {

	}
}
