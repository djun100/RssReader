package com.scau.baitouwei.xrssfeed.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.scau.baitouwei.xrssfeed.R;
import com.scau.baitouwei.xrssfeed.bean.ReadabilityParserBack;
import com.scau.baitouwei.xrssfeed.readability.ReadabilityService;
import com.scau.baitouwei.xrssfeed.util.Key;

public class ItemDetailActivity extends SherlockActivity implements refreshImpl{
	private static final String TAG = "ItemDetailActivity";
	private final static String  BASE_URL = "";
	private final static String  ENCODING = "UTF-8";
	private WebView webView;
	private String link;
	private AlertDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_detail_activity);
		webView = (WebView) findViewById(R.id.detail_webview);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			link = bundle.getString(Key.ITEM_KEY_LINK);
		}
		loadingDialog = new LoadingDialog(this);
		refresh();
	}

	@Override
	public void refresh() {
		ReadabilityService.getInstance(this).parseUrl(Uri.parse(link),
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						super.onFailure(arg0, arg1);
						Log.d(TAG, "++ parse fail ++");
					}

					@Override
					public void onFinish() {
						super.onFinish();
						loadingDialog.hide();
					}

					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						try {
							JSONObject object = new JSONObject(arg0);
							webView.loadDataWithBaseURL(BASE_URL, object.getString("content"), "text/html", ENCODING, null);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});
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

}
