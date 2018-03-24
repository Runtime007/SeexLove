package com.chat.seecolove.tools;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.chat.seecolove.view.activity.MultiImageSelector;
import com.im.Constant;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import com.chat.seecolove.R;
import com.chat.seecolove.widget.ToastUtils;

/**
 */
public class UploadImage {

    private static Activity mactivity;

    /**
     * type 0:相册 1：拍照
     * *
     */
    public static void upload(Activity activity, int type,int requestCode) {
        mactivity = activity;
        if (type == 0) {
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
//            activity.startActivityForResult(intent, requestCode);
            MultiImageSelector.create()
                    .showCamera(true) // show camera or not. true by default
                    .single() // max select image size, 9 by default. used width #.multi()
//                    .multi() // multi mode, default mode;
                    .start(activity, requestCode);
        } else {
            //图片文件
            final File imageFile = Tools.initUploadFile();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Tools.hasSDCard()) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                /***
                 * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
                 * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
                 * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
                 */
                activity.startActivityForResult(intent, requestCode);
            } else {
                ToastUtils.makeTextAnim(activity, R.string.seex_no_sdCard).show();
            }
        }

    }


//    public static void doPhoto(File imageFile, int requestCode, Intent data) {
//        Uri photoUri;
//        if (requestCode == Constants.REQUEST_CODE_PHOTO_ALBUM) {//相册获取图片
//            photoUri = data.getData();
//        } else {//拍照图片
//            photoUri = Uri.fromFile(imageFile);
//        }
////        comp(picPath);
//
//        cropImage(photoUri);
//    }


    //截取图片
    public static void cropImage(Uri uri,Uri croppedImage) {
        //裁剪图片意图
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        //裁剪框的比例，1：1
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        //裁剪后输出图片的尺寸大小
//        intent.putExtra("outputX", 200);
//        intent.putExtra("outputY", 200);
//        //图片格式
//        intent.putExtra("outputFormat", "JPEG");
//        intent.putExtra("noFaceDetection", true);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        intent.putExtra("return-data", false);
//        mactivity.startActivityForResult(intent, Constants.REQUEST_CODE_PHOTO_CUT);

        Crop.of(uri, croppedImage).asSquare().start(mactivity);


//        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(1000, 1000, croppedImage);
//        cropImage.setOutlineColor(0xffe200);
//        cropImage.setSourceImage(uri);
//        mactivity.startActivityForResult(cropImage.getIntent(mactivity), Constants.REQUEST_CODE_PHOTO_CUT);



    }

}
