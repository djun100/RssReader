package com.scau.baitouwei.xrssfeed.readability;

import android.content.Context;
import android.net.Uri;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ReadabilityService {
	private static final String TAG = "WebService";
	private static final String READABILITY_API = "https://readability.com/api/content/v1/";
	private static final String READABILITY_PARSER_API = READABILITY_API + "parser";
	private static final String TOKEN = "a5b30ca52c9e4b6763c49a2fd3d8c232ebf83b5e";
	private static Context mCtx;
	private AsyncHttpClient client;
	
	
	private static class SingleHolder {
		private static final ReadabilityService instance = new ReadabilityService();
	}

	private ReadabilityService() {
		super();
		client = new AsyncHttpClient();
	}

	public static ReadabilityService getInstance(Context context) {
		mCtx = context;
		return SingleHolder.instance;
	}

	public void parseUrl(Uri uri,AsyncHttpResponseHandler handler){
		RequestParams params = new RequestParams();
		params.put("url", uri.toString());
		params.put("token", TOKEN);
		client.get(mCtx, READABILITY_PARSER_API, params, handler);
	}
}
