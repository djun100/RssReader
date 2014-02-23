package com.scau.baitouwei.xrssfeed.activity;

import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.scau.baitouwei.xrssfeed.R;
import com.scau.baitouwei.xrssfeed.service.RSSDBService;
import com.scau.baitouwei.xrssfeed.util.Utils;

public class AddRssActivity extends SherlockActivity implements OnClickListener {
	private static final String TAG = "AddRssActivity";
	private Button addRssBt;
	private Button addTypeBt;
	private Button selectRssBt;
	private Button selectTypeBt;
	private TextView rssTypeEt;
	private EditText rssLinkEt;
	private EditText addTypeEt;
	private AlertDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_rss_activity);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		addRssBt = (Button) findViewById(R.id.add_rss_bt);
		rssTypeEt = (TextView) findViewById(R.id.rss_type_et);
		rssLinkEt = (EditText) findViewById(R.id.rss_link_et);
		addTypeEt = (EditText) findViewById(R.id.add_type_et);
		addTypeBt = (Button) findViewById(R.id.add_type_bt);
		selectRssBt = (Button) findViewById(R.id.select_rss_bt);
		selectTypeBt = (Button) findViewById(R.id.select_type_bt);
		addRssBt.setOnClickListener(this);
		addTypeBt.setOnClickListener(this);
		loadingDialog = new LoadingDialog(this);
		selectRssBt.setOnClickListener(this);
		selectTypeBt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_rss_bt:
			if (!Utils.IsStringNUll(rssLinkEt.getText().toString())
					&& !Utils.IsStringNUll(rssTypeEt.getText().toString())) {
					new AddRssAsyncTask().execute(rssLinkEt.getText()
							.toString(), rssTypeEt.getText().toString());
			}
			break;
		case R.id.add_type_bt:
			if (!Utils.IsStringNUll(addTypeEt.getText().toString())) {
				new AddTypeAsyncTask().execute(addTypeEt.getText().toString());
			}
			break;
		case R.id.select_type_bt:
			List<String> list = new ArrayList<String>();
			list.add(getResources().getStringArray(R.array.left_menu_items)[2]);
			list.addAll(RSSDBService.getInstance(this).getAllPacket());
			new AlertDialog.Builder(this)
					.setTitle(getResources().getString(R.string.plear_select))
					.setSingleChoiceItems(
							list.toArray(new String[list.size()]), 0,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									rssTypeEt.setText(((AlertDialog) dialog)
											.getListView().getAdapter()
											.getItem(which).toString());
									dialog.dismiss();
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.cancel), null)
					.show();

			break;
		case R.id.select_rss_bt:
			new AlertDialog.Builder(this)
					.setTitle(getResources().getString(R.string.plear_select))
					.setSingleChoiceItems(
							getResources().getStringArray(R.array.rss_address),
							0, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									rssLinkEt.setText(((AlertDialog) dialog)
											.getListView().getAdapter()
											.getItem(which).toString());
									dialog.dismiss();
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.cancel), null)
					.show();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;

		default:
			break;
		}
		return true;
	}

	private class AddRssAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!loadingDialog.isShowing())
				loadingDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (loadingDialog.isShowing())
				loadingDialog.dismiss();
		}

		@Override
		protected Void doInBackground(String... params) {
			Log.d(TAG, "++ params.length" + params.length + " ++");
			if (params.length > 1 && !Utils.IsStringNUll(params[0])
					&& !Utils.IsStringNUll(params[1])) {
				RSSReader reader = new RSSReader();
				try {
					RSSFeed feed = reader.load(params[0]);
					Log.d(TAG, "++ feed.title:" + feed.getTitle() + " ++");
					feed.setPacket(params[1]);
					Log.d(TAG, "++ feed.type:" + feed.getPacket() + " ++");
					RSSDBService.getInstance(AddRssActivity.this).addRSSFeed(
							feed);
				} catch (RSSReaderException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

	}

	private class AddTypeAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!loadingDialog.isShowing())
				loadingDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (loadingDialog.isShowing())
				loadingDialog.dismiss();
		}

		@Override
		protected Void doInBackground(String... params) {
			if (params.length == 1 && !Utils.IsStringNUll(params[0])) {
				RSSDBService.getInstance(AddRssActivity.this).addPacket(
						params[0]);
			}
			return null;
		}

	}
}
