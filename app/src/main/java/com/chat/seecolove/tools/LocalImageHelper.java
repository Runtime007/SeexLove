package com.chat.seecolove.tools;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.view.activity.MyApplication;

public class LocalImageHelper {
    private static LocalImageHelper instance;
    private final MyApplication context;
    final List<LocalFile> checkedItems = new ArrayList<>();


    //大图遍历字段
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION
    };
    //小图遍历字段
    private static final String[] THUMBNAIL_STORE_IMAGE = {
            MediaStore.Images.Thumbnails._ID,
            MediaStore.Images.Thumbnails.DATA
    };

    final List<LocalFile> paths = new ArrayList<>();

    final Map<String, List<LocalFile>> folders = new HashMap<>();

    private LocalImageHelper(MyApplication context) {
        this.context = context;
    }

    public Map<String, List<LocalFile>> getFolderMap() {
        return folders;
    }

    public static LocalImageHelper getInstance() {
        return instance;
    }

    public static void init(MyApplication context) {
        instance = new LocalImageHelper(context);
        //        new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                instance.initImage();
        //            }
        //        }).start();
    }

    public boolean isInited() {
        return paths.size() > 0;
    }

    public void clearPaths() {
        if (paths != null && paths.size() > 0) {
            paths.clear();

        }

        if (folders != null && folders.size() > 0) {
            folders.clear();
        }
    }

    public List<LocalFile> getCheckedItems() {
        return checkedItems;
    }


    public List<File> getCheckedFiles(){
        List<File> rtnValue = new ArrayList<File>();
        if(checkedItems == null || checkedItems.size() == 0){
            return rtnValue;
        }
        for(LocalFile file : checkedItems){
            rtnValue.add(file.getFile());
        }
        clear();
        return rtnValue;
    }

    private boolean resultOk;

    public boolean isResultOk() {
        return resultOk;
    }

    public void setResultOk(boolean ok) {
        resultOk = ok;
    }

    private boolean isRunning = false;

    public void initImage() {
        //        if (isRunning)
        //            return;
        //        isRunning=true;
        if (isInited())
            return;
        //获取大图的游标
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  // 大图URI
                STORE_IMAGES,   // 字段
                null,         // No where clause
                null,         // No where clause
                MediaStore.Images.Media.DATE_TAKEN + " DESC"); //根据时间升序
        Log.e("eeeeeeeeeeeeeeeeeee", "------cursor:" + cursor);
        if (cursor == null)
            return;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);//大图ID
            String path = cursor.getString(1);//大图路径
            LogTool.setLog("大图路径1", path);
            File file = new File(path);
            //判断大图是否存在
            if (file.exists()) {
                //小图URI
                String thumbUri = getThumbnail(id, path);
                //获取大图URI
                String uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                        appendPath(Integer.toString(id)).build().toString();
                LogTool.setLog("大图路径2", uri);
                if (Tools.isEmpty(uri))
                    continue;
                if (Tools.isEmpty(thumbUri))
                    thumbUri = uri;
                //获取目录名
                String folder = file.getParentFile().getName();
                String appFile = context.getResources().getString(R.string.app_name);
                if (!folder.equals(appFile)) {
                    LocalFile localFile = new LocalFile();
                    localFile.setPath(path);
                    localFile.setOriginalUri(uri);
                    localFile.setThumbnailUri(thumbUri);
                    int degree = cursor.getInt(2);
                    if (degree != 0) {
                        degree = degree + 180;
                    }
                    localFile.setOrientation(360 - degree);


                    paths.add(localFile);


                    //判断文件夹是否已经存在
                    if (folders.containsKey(folder)) {
                        folders.get(folder).add(localFile);
                    } else {
                        List<LocalFile> files = new ArrayList<>();
                        files.add(localFile);
                        folders.put(folder, files);
                    }
                }
            }
        }
        folders.put("所有图片", paths);
        cursor.close();
        isRunning = false;
    }

    private String getThumbnail(int id, String path) {
        //获取大图的缩略图
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                THUMBNAIL_STORE_IMAGE,
                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
                new String[]{id + ""},
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int thumId = cursor.getInt(0);
            String uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon().
                    appendPath(Integer.toString(thumId)).build().toString();
            cursor.close();
            return uri;
        }
        cursor.close();
        return null;
    }

    public List<LocalFile> getFolder(String folder) {
        return folders.get(folder);
    }

    public void clear() {
        if (checkedItems != null) {
            checkedItems.clear();
        }
        String foloder = Tools.getCachePath()
                + "/PostPicture/";
        File savedir = new File(foloder);
        if (savedir.exists()) {
            deleteFile(savedir);
        }
    }

    public void deleteFile(File file) {

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
        } else {
        }
    }

    public static class LocalFile {
        private String path;//原图路径
        private String originalUri;//原图URI
        private String thumbnailUri;//缩略图URI
        private int orientation;//图片旋转角度

        private boolean checked = false;


        public String getPath() {
            return path;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getThumbnailUri() {
            return thumbnailUri;
        }

        public void setThumbnailUri(String thumbnailUri) {
            this.thumbnailUri = thumbnailUri;
        }

        public String getOriginalUri() {
            return originalUri;
        }

        public void setOriginalUri(String originalUri) {
            this.originalUri = originalUri;
        }


        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int exifOrientation) {
            orientation = exifOrientation;
        }

        public File getFile(){
            String path = this.getPath();
            return new File(path);
        }

    }
}
