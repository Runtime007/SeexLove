package com.chat.seecolove.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;
import com.chat.seecolove.R;
import com.chat.seecolove.view.activity.MyApplication;


public class ToastUtils extends Toast {

	public ToastUtils(Context context) {
		super(context);
	}
	public static Toast makeTextAnim(Context context, int id) {
		if(context!=null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.toast_layout_custom, null);
			view.getBackground().setAlpha(200);
			TextView textView = (TextView) view.findViewById(R.id.text);
			textView.setText(MyApplication.getContext().getResources().getText(id));
			Toast toast = new Toast(MyApplication.getContext());
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setView(view);
			return toast;
		}else{
			return new Toast(MyApplication.getContext());
		}
	}

	public static Toast makeTextAnim(Context context, String text ) {
		if(context!=null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.toast_layout_custom, null);
			view.getBackground().setAlpha(200);
			TextView textView = (TextView) view.findViewById(R.id.text);
			textView.setText(text);
			Toast toast = new Toast( MyApplication.getContext());
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setView(view);
			return toast;
		}else{
			return new Toast(MyApplication.getContext());
		}
	}


	public static void showMyToast(Toast toast, int cnt) {
		if (cnt < 0)
		return;
		toast.show();
		execToast(toast, cnt);
	}

	public static void execToast(final Toast toast, final int cnt) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				showMyToast(toast, cnt - 1);
			}
		}, 2000);
	}

	private static Object getField(Object object, String fieldName)
			throws NoSuchFieldException, IllegalAccessException {
		Field field = object.getClass().getDeclaredField(fieldName);
		if (field != null) {
			field.setAccessible(true);
			return field.get(object);
		}
		return null;
	}

}
