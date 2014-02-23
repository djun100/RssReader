package com.scau.baitouwei.xrssfeed.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.scau.baitouwei.xrssfeed.R;
import com.scau.baitouwei.xrssfeed.activity.AddRssActivity;
import com.scau.baitouwei.xrssfeed.activity.MainActivity;
import com.scau.baitouwei.xrssfeed.service.RSSDBService;

public class MenuFragment extends Fragment implements OnItemClickListener,
		OnClickListener {
	private static final String TAG = "MenuFragment";
	private ListView menu_list;
	private ImageView add_rss_iv;
	private ArrayAdapter<CharSequence> menu_adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu, null);
		menu_adapter = new ArrayAdapter<CharSequence>(getActivity(),
				R.layout.menu_list_item1, R.id.menu_packet_name,
				new ArrayList<CharSequence>());
		add_rss_iv = (ImageView) view.findViewById(R.id.menu_add);
		menu_list = (ListView) view.findViewById(R.id.menu_list);
		menu_list.setAdapter(menu_adapter);
		menu_list.setOnItemClickListener(this);
		add_rss_iv.setOnClickListener(this);
		updateMenu();
		return view;
	}

	private void updateMenu() {
		new getPacketAsyncTask().execute();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateMenu();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (((ArrayAdapter<String>) arg0.getAdapter()).getItem(arg2).equals(
				getActivity().getResources().getStringArray(
						R.array.left_menu_items)[1])) {
			((MainActivity) getActivity())
					.switchContent(new StarIitemFragment());
			((MainActivity) getActivity()).setActionBarTitle(getActivity()
					.getResources().getStringArray(R.array.left_menu_items)[1]);
		} else {
			((MainActivity) getActivity())
					.selectPacket(((ArrayAdapter<String>) arg0.getAdapter())
							.getItem(arg2));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_add:
			startActivity(new Intent(getActivity(), AddRssActivity.class));
			break;

		default:
			break;
		}
	}

	private class getPacketAsyncTask extends
			AsyncTask<Void, Void, List<CharSequence>> {

		@Override
		protected List<CharSequence> doInBackground(Void... params) {
			ArrayList<CharSequence> menuItems = new ArrayList<CharSequence>();
			for (String string : getActivity().getResources().getStringArray(
					R.array.left_menu_items)) {
				menuItems.add(string);
			}
			menuItems.addAll(RSSDBService.getInstance(getActivity())
					.getAllPacket());
			Log.d(TAG, "++ menuItems size:" + menuItems.size() + " ++");
			return menuItems;
		}

		@Override
		protected void onPostExecute(List<CharSequence> result) {
			super.onPostExecute(result);
			Log.d(TAG, "++ result size:" + result.size() + " ++");
			if (result != null && result.size() > 0) {
				menu_adapter.clear();
				menu_adapter.addAll(result);
			}
			menu_adapter.notifyDataSetChanged();
		}
	}
}
