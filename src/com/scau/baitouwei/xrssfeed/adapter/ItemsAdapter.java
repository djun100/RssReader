package com.scau.baitouwei.xrssfeed.adapter;

import java.util.List;

import org.mcsoxford.rss.RSSItem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scau.baitouwei.xrssfeed.R;
import com.scau.baitouwei.xrssfeed.activity.ItemDetailActivity;
import com.scau.baitouwei.xrssfeed.service.RSSDBService;
import com.scau.baitouwei.xrssfeed.util.Key;

public class ItemsAdapter extends ArrayAdapter<RSSItem> {
	private final static String TAG = "PacketAdapter";
	private final static String BASE_URL = "";
	private final static String ENCODING = "UTF-8";
	private static Context ctx;
	private LayoutInflater inflater;
	private String rssName;

	public ItemsAdapter(Context context, String rssName) {
		super(context, android.R.layout.simple_list_item_1);
		ctx = context;
		this.rssName = rssName;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Handler handler;
		final int pos = position;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.items_list_item, parent,
					false);
			handler = new Handler();
			handler.item_bg = (RelativeLayout) convertView
					.findViewById(R.id.item_bg);
			handler.feedIco = (ImageView) convertView
					.findViewById(R.id.feed_ico);
			handler.itemStart = (CheckBox) convertView
					.findViewById(R.id.item_star);
			handler.feedTitle = (TextView) convertView
					.findViewById(R.id.feed_title);
			handler.itemSum = (TextView) convertView
					.findViewById(R.id.item_sum);
			handler.itemPubTime = (TextView) convertView
					.findViewById(R.id.item_pub_time);
			convertView.setTag(handler);
		} else {
			handler = (Handler) convertView.getTag();
		}

		handler.feedTitle.setText(rssName);
		handler.itemSum.setText(getItem(position).getTitle());
		handler.itemStart.setChecked(getItem(position).isStarted());
		if (getItem(position).isRead())
			handler.item_bg.setAlpha((float) 0.25);
		else {
			handler.item_bg.setAlpha(1);
		}

		handler.itemStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RSSDBService.getInstance(ctx).changeItemStar(getItem(pos));
				getItem(pos).setStarted(!getItem(pos).isStarted());
				Toast.makeText(
						ctx,
						getContext().getResources().getString(R.string.success),
						Toast.LENGTH_SHORT).show();
			}
		});

		handler.item_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RSSDBService.getInstance(ctx).changeItemIsReaded(getItem(pos));
				getItem(pos).setRead(true);
				Intent intent = new Intent(ctx, ItemDetailActivity.class);
				intent.putExtra(Key.ITEM_KEY_LINK, getItem(pos).getLink()
						.toString());
				ctx.startActivity(intent);
			}
		});
		// (getItem(position).getDescription().replaceAll("<[^>]*>",""));
		// handler.itemPubTime.setText(getItem(position).getPubDate().toString());
		return convertView;
	}

	private class Handler {
		RelativeLayout item_bg;
		ImageView feedIco;
		CheckBox itemStart;
		TextView feedTitle;
		TextView itemSum;
		TextView itemPubTime;
	}

	public void setData(List<RSSItem> data) {
		clear();
		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				add(data.get(i));
			}
		}
	}
}
