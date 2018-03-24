package com.chat.seecolove.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.chat.seecolove.R;
import com.chat.seecolove.view.activity.MyApplication;

public class DialogTool {


    public static AlertDialog createDogDialogSingle(Activity activity, View layout, int textID, int yesID) {
        TextView dialog_text = (TextView) layout.findViewById(R.id.dialog_text);
        TextView dialog_sure = (TextView) layout.findViewById(R.id.dialog_sure);
        dialog_text.setText(textID);
        dialog_sure.setText(yesID);
        dialog_sure.setTag(1);
        final android.app.AlertDialog myDialog = new android.app.AlertDialog.Builder(activity).create();
        myDialog.setCanceledOnTouchOutside(false);
        Window window = myDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        myDialog.show();
        params.width = (int) (MyApplication.screenWidth * 0.8);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setContentView(layout);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        return myDialog;
    }

    public static AlertDialog createDogDialog(Context activity, View layout, Object textID, int cancleID, int yesID) {
        TextView dialog_text = (TextView) layout.findViewById(R.id.dialog_text);
        TextView dialog_cancle = (TextView) layout.findViewById(R.id.dialog_cancle);
        TextView dialog_sure = (TextView) layout.findViewById(R.id.dialog_sure);
//        ImageView dialog_close = (ImageView) layout.findViewById(R.id.dialog_close);
        if(textID instanceof String){
            dialog_text.setText(textID+"");
        }else{
            dialog_text.setText(Integer.parseInt(textID+""));
        }
        dialog_cancle.setText(cancleID);
        dialog_sure.setText(yesID);
        dialog_cancle.setTag(0);
        dialog_sure.setTag(1);
        final android.app.AlertDialog myDialog = new android.app.AlertDialog.Builder(activity).create();
        myDialog.setCanceledOnTouchOutside(false);
        Window window = myDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        myDialog.show();
        params.width = (int) (MyApplication.screenWidth * 0.85);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setContentView(layout);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
//        dialog_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                myDialog.dismiss();
//            }
//        });
        return myDialog;
    }


    public static AlertDialog createDialogRed(final Activity context, View layout, String text) {
        TextView btn_get = (TextView) layout.findViewById(R.id.btn_get);
        //ImageView btn_close = (ImageView) layout.findViewById(R.id.btn_close);
        TextView tv = (TextView) layout.findViewById(R.id.tv_get);
        tv.setText(String.format("%s", text));
        //btn_close.setTag(0);
        btn_get.setTag(1);
        final AlertDialog myDialog = new AlertDialog.Builder(context,R.style.transDialog).create();

        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);
        myDialog.setView(layout);
        //myDialog.getWindow().setBackgroundDrawableResource();
        myDialog.show();
        return myDialog;
    }

    public static void dialogConfirm(Context mContext, int resStringOk, int resStringCancel, String message, DialogInterface.OnClickListener onOk, DialogInterface.OnClickListener onCancel){
        new android.support.v7.app.AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton(resStringOk, onOk).setNegativeButton(resStringCancel, onCancel)
                .create()
                .show();
    }

    public static void dialogAlert(Context mContext, int resStringOk, String message, DialogInterface.OnClickListener onOk){
        new android.support.v7.app.AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton(resStringOk, onOk)
                .create()
                .show();
    }

}
