package com.chat.seecolove.tools;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.view.activity.MultiImageSelector;
import com.chat.seecolove.view.activity.UserInfoNewActivity;
import com.chat.seecolove.widget.ToastUtils;

import java.io.File;

/**
 * Created by 建成 on 2017-10-24.
 */

public class ActivityTransfer {
    public static void startPhotoAlbum(Activity from, int pickSize, int requestCode){
        MultiImageSelector.create()
                .showCamera(true)
                .single()
                .start(from, requestCode);
    }

    public static void startPicturing(Activity from, int requestCode, final File captured){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Tools.hasSDCard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(captured));
            from.startActivityForResult(intent, requestCode);
        } else {
            ToastUtils.makeTextAnim(from, R.string.seex_no_sdCard).show();
        }
    }

    public static void startActivity(Activity from, Class<?> clazz, Bundle bundle){
        Intent intent = new Intent(from, clazz);
        if(bundle != null)
            intent.putExtras(bundle);
        from.startActivity(intent);
    }

    public static void startActivityForResult(Activity from, Class<?> clazz, Bundle bundle, int requestCode){
        Intent intent = new Intent(from, clazz);
        if(bundle != null)
            intent.putExtras(bundle);
        from.startActivityForResult(intent, requestCode);
    }
}
