package com.scau.baitouwei.xrssfeed.observer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.scau.baitouwei.xrssfeed.loader.PacketLoader;

public class FeedsUpdateObserver extends BroadcastReceiver{
	private PacketLoader mLoader;
	
	public FeedsUpdateObserver(PacketLoader loader) {
		super();
		this.mLoader = loader;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	}

}
