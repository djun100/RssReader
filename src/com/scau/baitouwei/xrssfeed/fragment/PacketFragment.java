package com.scau.baitouwei.xrssfeed.fragment;

import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.RSSFeed;
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
import com.scau.baitouwei.xrssfeed.adapter.PacketAdapter;
import com.scau.baitouwei.xrssfeed.loader.PacketLoader;
import com.scau.baitouwei.xrssfeed.service.RSSDBService;
import com.scau.baitouwei.xrssfeed.util.Key;

public class PacketFragment extends Fragment implements
		LoaderCallbacks<List<RSSFeed>>, OnItemClickListener,
		PullToRefreshAttacher.OnRefreshListener,refreshImpl {

	private static final String TAG = "PacketFragment";
	private ListView mListView;
	private PacketAdapter mAdapter;
	// private View progressBar;
	private PullToRefreshAttacher attacher;
	/**
	 * 分组组名
	 */
	private static String packet;
	private static final int LOADER_ID = 1;

	public PacketFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.channel_list, null);
		mListView = (ListView) view.findViewById(R.id.channel_list);
		attacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
		attacher.setRefreshableView(mListView, this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new PacketAdapter(getActivity());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		getLoaderManager().initLoader(LOADER_ID, null, this);
		packet = getActivity().getResources().getStringArray(
				R.array.left_menu_items)[0];

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
		if (!attacher.isRefreshing()) {
			attacher.setRefreshing(true);
		}
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				Log.d(TAG, "++ begin refresh rssfeed ++");
				RSSReader reader = new RSSReader();
				List<RSSFeed> list = new ArrayList<RSSFeed>();
				Log.d(TAG,
						"++ ((PacketAdapter) mAdapter).getDataList().size():"
								+ ((PacketAdapter) mAdapter).getDataList()
										.size() + " ++");
				for (RSSFeed feed : ((PacketAdapter) mAdapter).getDataList()) {
					try {
						feed = reader.load(feed.getRssLink().toString());
						Log.d(TAG, "++ feed.getTitle:" + feed.getTitle()
								+ " ++");
					} catch (RSSReaderException e) {
						e.printStackTrace();
					}
					list.add(feed);
				}
				RSSDBService.getInstance(getActivity())
						.refreshRSSFeedList(list);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				getLoaderManager().getLoader(LOADER_ID).forceLoad();
				super.onPostExecute(result);
			}

		}.execute();
	}

	/**
	 * 实现loader
	 */
	@Override
	public Loader<List<RSSFeed>> onCreateLoader(int id, Bundle arg) {
		return new PacketLoader(getActivity(), packet);
	}

	@Override
	public void onLoadFinished(Loader<List<RSSFeed>> loader, List<RSSFeed> data) {
		mAdapter.setData(data);
		if (attacher.isRefreshing())
			attacher.setRefreshComplete();
		// progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<List<RSSFeed>> arg0) {
		mAdapter.setData(null);
	}

	public String getPacket() {
		return packet;
	}

	public void setPacket(String packet) {
		Log.d(TAG, "change packet:" + packet);
		this.packet = packet;
	}

	public void changePacket(String packet) {
		if (!attacher.isRefreshing())
			attacher.setRefreshing(true);
		getLoaderManager().restartLoader(LOADER_ID, null, this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(getActivity(), FeedActivity.class);
		intent.putExtra(Key.FEED_KEY_RSS_LINK,
				((PacketAdapter) arg0.getAdapter()).getItem(arg2).getRssLink()
						.toString());
		intent.putExtra(Key.FEED_KEY_TITLE,
				((PacketAdapter) arg0.getAdapter()).getItem(arg2).getTitle()
						.toString());
		startActivity(intent);
	}

	@Override
	public void onRefreshStarted(View view) {
		refresh();
	}
}
