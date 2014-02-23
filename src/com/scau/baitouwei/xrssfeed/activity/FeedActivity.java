package com.scau.baitouwei.xrssfeed.activity;

import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.scau.baitouwei.xrssfeed.R;
import com.scau.baitouwei.xrssfeed.adapter.ItemsAdapter;
import com.scau.baitouwei.xrssfeed.loader.ItemsLoader;
import com.scau.baitouwei.xrssfeed.service.RSSDBService;
import com.scau.baitouwei.xrssfeed.util.Key;

public class FeedActivity extends SherlockFragmentActivity implements
		LoaderCallbacks<List<RSSItem>>, refreshImpl,
		OnRefreshListener {
	private static final String TAG = "FeedActivity";
	private static final int LOADER_ID = 2;
	private static String rssLink;
	private static String feedTitle;
	private ListView mListView;
	private ItemsAdapter mAdapter;
	private PullToRefreshAttacher attacher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		options.headerInAnimation = R.anim.fade_in;
		options.headerInAnimation = R.anim.fade_out;
		options.refreshScrollDistance = 0.3f;
		options.headerLayout = R.layout.pulldown_header;
		attacher = new PullToRefreshAttacher(this, options);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			rssLink = getIntent().getExtras().getString(Key.FEED_KEY_RSS_LINK);
			feedTitle = getIntent().getExtras().getString(Key.FEED_KEY_TITLE);
		} else {
			rssLink = "";
			feedTitle = "";
		}

		setContentView(R.layout.items_list);
		mListView = (ListView) findViewById(R.id.items_list);
		mAdapter = new ItemsAdapter(this, feedTitle);
		mListView.setAdapter(mAdapter);
//		mListView.setOnItemClickListener(this);
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
		attacher.setRefreshableView(mListView, this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getSupportLoaderManager().getLoader(LOADER_ID).forceLoad();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getSupportLoaderManager().destroyLoader(LOADER_ID);
			this.finish();
			break;
		case R.id.action_refresh:
			refresh();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void refresh() {
		if (!attacher.isRefreshing())
			attacher.setRefreshing(true);
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				RSSReader reader = new RSSReader();
				try {
					RSSFeed feed = reader.load(rssLink);
					RSSDBService.getInstance(FeedActivity.this).refreshRSSFeed(
							feed);
				} catch (RSSReaderException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				getSupportLoaderManager().getLoader(LOADER_ID).forceLoad();
				super.onPostExecute(result);
			}

		}.execute();
	}

	@Override
	public Loader<List<RSSItem>> onCreateLoader(int arg0, Bundle arg1) {
		return new ItemsLoader(this, rssLink);
	}

	@Override
	public void onLoadFinished(Loader<List<RSSItem>> arg0, List<RSSItem> data) {
		if (attacher.isRefreshing())
			attacher.setRefreshComplete();
		mAdapter.setData(data);
	}

	@Override
	public void onLoaderReset(Loader<List<RSSItem>> arg0) {
		mAdapter.setData(null);
	}

//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		Toast.makeText(this, "sd", Toast.LENGTH_SHORT).show();
//		Intent intent = new Intent(this, ItemDetailActivity.class);
//		intent.putExtra(Key.ITEM_KEY_LINK, ((ItemsAdapter) arg0.getAdapter())
//				.getItem(arg2).getLink().toString());
//		((ItemsAdapter) arg0.getAdapter()).getItem(arg2).setRead(true);
//		startActivity(intent);
//	}

	@Override
	public void onRefreshStarted(View view) {
		refresh();
	}
}
