package com.scau.baitouwei.xrssfeed.fragment;

import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.scau.baitouwei.xrssfeed.R;
import com.scau.baitouwei.xrssfeed.activity.FeedActivity;
import com.scau.baitouwei.xrssfeed.activity.MainActivity;
import com.scau.baitouwei.xrssfeed.activity.refreshImpl;
import com.scau.baitouwei.xrssfeed.adapter.ItemsAdapter;
import com.scau.baitouwei.xrssfeed.adapter.PacketAdapter;
import com.scau.baitouwei.xrssfeed.loader.ItemsLoader;
import com.scau.baitouwei.xrssfeed.loader.PacketLoader;
import com.scau.baitouwei.xrssfeed.loader.StarItemLoader;
import com.scau.baitouwei.xrssfeed.service.RSSDBService;
import com.scau.baitouwei.xrssfeed.util.Key;

public class StarIitemFragment extends Fragment implements
		LoaderCallbacks<List<RSSItem>>,
		PullToRefreshAttacher.OnRefreshListener, refreshImpl {

	private static final String TAG = "UnclassifiedFragment";
	private ListView mListView;
	private ItemsAdapter mAdapter;
	private PullToRefreshAttacher attacher;
	/**
	 * 分组组名
	 */
	private static final int LOADER_ID = 3;

	public StarIitemFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.items_list, null);
		mListView = (ListView) view.findViewById(R.id.items_list);
		attacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
		attacher.setRefreshableView(mListView, this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new ItemsAdapter(getActivity(), getActivity().getResources()
				.getStringArray(R.array.left_menu_items)[2]);
		mListView.setAdapter(mAdapter);
//		mListView.setOnItemClickListener(this);
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshFromDB();
	}

	private void refreshFromDB() {
		if (!attacher.isRefreshing()) {
			attacher.setRefreshing(true);
		}
		getLoaderManager().getLoader(LOADER_ID).forceLoad();
	}

	@Override
	public void refresh() {
		// if (!attacher.isRefreshing()) {
		// attacher.setRefreshing(true);
		// }
		// new AsyncTask<Void, Void, Void>() {
		//
		// @Override
		// protected Void doInBackground(Void... params) {
		// Log.d(TAG, "++ begin refresh rssfeed ++");
		// RSSReader reader = new RSSReader();
		// List<RSSFeed> list = new ArrayList<RSSFeed>();
		// Log.d(TAG,
		// "++ ((PacketAdapter) mAdapter).getDataList().size():"
		// + ((PacketAdapter) mAdapter).getDataList()
		// .size() + " ++");
		// for (RSSFeed feed : ((PacketAdapter) mAdapter).getDataList()) {
		// try {
		// feed = reader.load(feed.getRssLink().toString());
		// Log.d(TAG, "++ feed.getTitle:" + feed.getTitle()
		// + " ++");
		// } catch (RSSReaderException e) {
		// e.printStackTrace();
		// }
		// list.add(feed);
		// }
		// RSSDBService.getInstance(getActivity())
		// .refreshRSSFeedList(list);
		// return null;
		// }
		//
		// @Override
		// protected void onPostExecute(Void result) {
		// getLoaderManager().getLoader(LOADER_ID).forceLoad();
		// super.onPostExecute(result);
		// }
		//
		// }.execute();
	}

	/**
	 * 实现loader
	 */
	@Override
	public Loader<List<RSSItem>> onCreateLoader(int id, Bundle arg) {
		return new StarItemLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<RSSItem>> loader, List<RSSItem> data) {
		mAdapter.setData(data);
		if (attacher.isRefreshing())
			attacher.setRefreshComplete();
		// progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<List<RSSItem>> arg0) {
		mAdapter.setData(null);
	}

	@Override
	public void onRefreshStarted(View view) {
		refresh();
	}
}
