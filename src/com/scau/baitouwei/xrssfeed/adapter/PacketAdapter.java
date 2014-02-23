package com.scau.baitouwei.xrssfeed.adapter;

import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.RSSFeed;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scau.baitouwei.xrssfeed.R;

public class PacketAdapter extends ArrayAdapter<RSSFeed> {
	private static final String TAG = "PacketAdapter";
	private LayoutInflater inflater;

	public PacketAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Handler handler = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.pageket_list_item, parent,
					false);
			handler = new Handler();
			handler.ico = (ImageView) convertView
					.findViewById(R.id.menu_item_ico);
			handler.name = (TextView) convertView
					.findViewById(R.id.menu_item_name);
			handler.alert_num = (TextView) convertView
					.findViewById(R.id.menu_alert);
			convertView.setTag(handler);
		} else {
			handler = (Handler) convertView.getTag();
		}
		RSSFeed feed = getItem(position);

		// handler.ico.setImageResource(feed.getIco());
		handler.name.setText(feed.getTitle());
		if (feed.getAlert_num() == 0) {
			handler.alert_num.setVisibility(View.GONE);
		} else {
			handler.alert_num.setVisibility(View.VISIBLE);
			handler.alert_num.setText(String.valueOf(feed.getAlert_num()));
		}

		return convertView;

	}

	public void setData(List<RSSFeed> data) {
		clear();
		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				add(data.get(i));
			}
		}
	}

	public List<RSSFeed> getDataList() {
		List<RSSFeed> list = new ArrayList<RSSFeed>();
		if (!isEmpty()) {
			for (int i = 0; i < getCount(); i++) {
				Log.d(TAG, "++ addItem"+i+" ++");
				list.add(getItem(i));
			}
		}
		return list;
	}

	private class Handler {
		TextView name;
		ImageView ico;
		TextView alert_num;
	}
}
