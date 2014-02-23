package com.scau.baitouwei.xrssfeed.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.scau.baitouwei.xrssfeed.R;

public class LoadingDialog extends AlertDialog{

	protected LoadingDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setCanceledOnTouchOutside(false);
		setContentView(R.layout.loading_dialog);
	}

}
