package com.scau.baitouwei.xrssfeed.activity;

import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.scau.baitouwei.xrssfeed.R;
import com.scau.baitouwei.xrssfeed.fragment.MenuFragment;
import com.scau.baitouwei.xrssfeed.fragment.PacketFragment;
import com.scau.baitouwei.xrssfeed.service.RSSDBService;

public class MainActivity extends SlidingFragmentActivity implements
		refreshAttacherImpl {
	private static final String TAG = "MainActivity";
	private Fragment mContent;
	private String DEFAULT_PACKET;
	private Context mCtx = this;
	private SlidingMenu sm;
	private long exitTime = 0;
	
	/*
	 * 下拉更新
	 */
	private PullToRefreshAttacher pullToRefreshAttacher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);
		setBehindContentView(R.layout.menu_frame);

//		Log.d(TAG, "++ Async execute ++");
//		new getFeedsFromNetAsyncTask().execute();

		getSlidingMenu().setSlidingEnabled(true);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(true);

		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		options.headerInAnimation = R.anim.fade_in;
		options.headerInAnimation = R.anim.fade_out;
		options.refreshScrollDistance = 0.3f;
		options.headerLayout = R.layout.pulldown_header;
		pullToRefreshAttacher = new PullToRefreshAttacher(this, options);

		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		}
		DEFAULT_PACKET = getResources().getStringArray(R.array.left_menu_items)[0];
		if (mContent == null) {
			mContent = new PacketFragment();
		}
		setActionBarTitle(DEFAULT_PACKET);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MenuFragment()).commit();
		sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(sm.isMenuShowing())
			return super.onKeyUp(keyCode, event);
		else {
			if(keyCode == KeyEvent.KEYCODE_BACK){
				if ((System.currentTimeMillis() - exitTime) > 2000) {
		            Toast.makeText(getApplicationContext(), "再按一次退出程序",
		                    Toast.LENGTH_SHORT).show();
		            exitTime = System.currentTimeMillis();
		        } else {
		            finish();
		            System.exit(0);
		        }
			}
			return true;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		RSSDBService.getInstance(mCtx).closeDB();
	}

	/**
	 * 切換content fragment
	 * 
	 * @param fragment
	 */
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();
		getSlidingMenu().showContent();
	}

	/**
	 * 进入不同的feeds分组
	 * 
	 * @param packet
	 */
	public void selectPacket(String packet) {
		if (mContent instanceof PacketFragment) {
			((PacketFragment) mContent).changePacket(packet);
			getSlidingMenu().showContent();
		} else {
			mContent = new PacketFragment();
			((PacketFragment) mContent).setPacket(packet);
			switchContent(mContent);
		}
		setActionBarTitle(packet);
	}

	public void setActionBarTitle(CharSequence title) {
		getSupportActionBar().setTitle(title);
	}

	/**
	 * 每次启动更新数据
	 * 
	 * @author baitouwei
	 * 
	 */
	private class getFeedsFromNetAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			List<RSSFeed> feeds = new ArrayList<RSSFeed>();
			RSSFeed feed;
			List<Uri> addresses = RSSDBService.getInstance(mCtx)
					.getAllAddresses();
			RSSReader reader = new RSSReader();
			for (Uri uri : addresses) {
				try {
					feed = reader.load(uri.toString());
					RSSDBService.getInstance(mCtx).refreshRSSFeed(feed);
				} catch (RSSReaderException e) {
					e.printStackTrace();
				}
			}
			reader.close();
			return null;
		}
	}

	@Override
	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return pullToRefreshAttacher;
	}
}
