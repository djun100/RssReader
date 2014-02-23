package com.scau.baitouwei.xrssfeed.loader;

import java.util.List;

import org.mcsoxford.rss.RSSItem;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.scau.baitouwei.xrssfeed.service.RSSDBService;

public class ItemsLoader extends AsyncTaskLoader<List<RSSItem>> {
	private static final String TAG = "ItemsLoader";
	private Context mCtx;
	private String rssLink;
	private List<RSSItem> mData;

	public ItemsLoader(Context context, String rssLink) {
		super(context);
		this.mCtx = context;
		this.rssLink = rssLink;
	}

	@Override
	public List<RSSItem> loadInBackground() {
		return RSSDBService.getInstance(mCtx).getRssItemByRssLink(rssLink);
	}

	@Override
	public void deliverResult(List<RSSItem> data) {
		if (isReset()) {
			if (data != null) {
				releaseResources(data);
				return;
			}
		}

		List<RSSItem> oldDate = mData;
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
	}

	@Override
	public void onCanceled(List<RSSItem> data) {
		super.onCanceled(data);
		releaseResources(data);
	}

	private void releaseResources(List<RSSItem> data) {

	}

}
