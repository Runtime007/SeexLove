package com.chat.seecolove.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;

import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.tools.LogTool;


/**
 * 聊天回话列表的管理
 *
 * @author Administrator
 */
public class SessionDao {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public SessionDao(Context context) {
        dbHelper = DBHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
    }


    public void closeDB() {
        if (db != null) {
            db.close();
        }
    }

    /**
     * 当有相同数据时，先删除以前的数据，然后保存
     *
     * @param friendBean
     * @return
     */
    public long saveMail(FriendBean friendBean) {

        if (friendBean != null && dbHelper != null) {
            /*db = dbHelper.getWritableDatabase();*/
            db.beginTransaction();

            String selection = DBcolumns.MAIL_TARGETID + " = ?";
            String[] selectionArgs = {friendBean.getTargetId() + ""};
            int row = db.delete(DBcolumns.TABLE_MAIL, selection, selectionArgs);

            LogTool.setLog("db--delete--result--->", row);
            ContentValues values = new ContentValues();
            values.put(DBcolumns.MAIL_NICKNAME, friendBean.getNickName());
            values.put(DBcolumns.MAIL_PHOTO, friendBean.getPhoto());
            values.put(DBcolumns.MAIL_TARGETID, friendBean.getTargetId());
            values.put(DBcolumns.MAIL_UNITPRICE, friendBean.getUnitPrice());
            values.put(DBcolumns.MAIL_YUNXIN_ACCID, friendBean.getYunxinAccid());
            values.put(DBcolumns.MAIL_REMARK_NAME, friendBean.getRemarkName());
            long result = db.insert(DBcolumns.TABLE_MAIL, null, values);
            db.setTransactionSuccessful();
            db.endTransaction();
            /*db.close();*/
            return result;
        }
        return 0L;
    }

    public boolean deleteMailItem(String targetId) {
        if (dbHelper != null && !TextUtils.isEmpty(targetId)) {
           /* db = dbHelper.getWritableDatabase();*/
            db.beginTransaction();
            String selection = DBcolumns.MAIL_TARGETID + " = ?";
            String[] selectionArgs = {targetId};

            int row = db.delete(DBcolumns.TABLE_MAIL, selection, selectionArgs);
            db.setTransactionSuccessful();
            db.endTransaction();
            /*db.close();*/
            if (row == 0) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void deleteMailAll() {
        if (dbHelper != null) {
            /*db = dbHelper.getWritableDatabase();*/
            db.beginTransaction();
            db.delete(DBcolumns.TABLE_MAIL, null, null);
            //        db.execSQL("DROP TABLE IF EXISTS "+DBcolumns.TABLE_MAIL);
            db.setTransactionSuccessful();
            db.endTransaction();
            /*db.close();*/
        }

    }

    public int updateMail(String nickName, String targetId, String photo) {
        if (dbHelper != null) {
         /*   db = dbHelper.getWritableDatabase();*/
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DBcolumns.MAIL_NICKNAME, nickName);
            values.put(DBcolumns.MAIL_PHOTO, photo);

            String selection = DBcolumns.MAIL_TARGETID + " = ?";
            String[] selectionArgs = {targetId};

            int count = db.update(
                    DBcolumns.TABLE_MAIL,
                    values,
                    selection,
                    selectionArgs);
            db.setTransactionSuccessful();
            db.endTransaction();
            /*db.close();*/
            return count;
        }
        return 0;

    }


    public int updateMail(String nickName, String targetId) {
        if (dbHelper != null) {
            /*db = dbHelper.getWritableDatabase();*/
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DBcolumns.MAIL_NICKNAME, nickName);

            String selection = DBcolumns.MAIL_TARGETID + " = ?";
            String[] selectionArgs = {targetId};

            int count = db.update(
                    DBcolumns.TABLE_MAIL,
                    values,
                    selection,
                    selectionArgs);
            db.setTransactionSuccessful();
            db.endTransaction();
            /*db.close();*/
            return count;
        }
        return 0;

    }

    public ArrayList<FriendBean> queryMailAll() {
        ArrayList<FriendBean> friendsList = new ArrayList<>();

        if (dbHelper != null) {
            /*db = dbHelper.getWritableDatabase();*/
            db.beginTransaction();
            Cursor cursor = db.rawQuery("select * from " + DBcolumns.TABLE_MAIL, null);

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    FriendBean friendBean = null;
                    while (cursor.isAfterLast() == false) {
                        friendBean = new FriendBean();
                        String nickName = cursor.getString(cursor.getColumnIndex(DBcolumns.MAIL_NICKNAME));
                        String remmarkName = cursor.getString(cursor.getColumnIndex(DBcolumns.MAIL_REMARK_NAME));
                        String photo = cursor.getString(cursor.getColumnIndex(DBcolumns.MAIL_PHOTO));
                        int targetId = cursor.getInt(cursor.getColumnIndex(DBcolumns.MAIL_TARGETID));
                        String yunxinAccId = cursor.getString(cursor.getColumnIndex(DBcolumns.MAIL_YUNXIN_ACCID));
                        float unitPrice = cursor.getFloat(cursor.getColumnIndex(DBcolumns.MAIL_UNITPRICE));
                        int status = cursor.getInt(cursor.getColumnIndex(DBcolumns.MAIL_STATUS));
                        friendBean.setNickName(nickName);
                        friendBean.setPhoto(photo);
                        friendBean.setStatus(status);
                        friendBean.setTargetId(targetId);
                        friendBean.setUnitPrice(unitPrice);
                        friendBean.setYunxinAccid(yunxinAccId);
                        friendBean.setRemarkName(remmarkName);
                        friendsList.add(friendBean);
                        cursor.moveToNext();
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            LogTool.setLog("SessionDao---queryAll->", friendsList.size());
            db.setTransactionSuccessful();
            db.endTransaction();
            /*db.close();*/
        }

        return friendsList;
    }

    public ArrayList<FriendBean> queryMail(String mTargetId) {
        ArrayList<FriendBean> friendsList = new ArrayList<>();
        if (dbHelper != null) {
            /*db = dbHelper.getWritableDatabase();*/
            db.beginTransaction();

            String[] projection = {
                    DBcolumns.MAIL_TARGETID
            };
            String selection = DBcolumns.MAIL_TARGETID + " = ?";
            String[] selectionArgs = {mTargetId};

            Cursor cursor = db.query(
                    DBcolumns.TABLE_MAIL,                     // The table to query
                    null,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );
            if (cursor != null && cursor.getCount() > 0) {
                FriendBean friendBean = null;

                if (cursor.moveToFirst()) {
                    if (cursor.isAfterLast() == false) {
                        friendBean = new FriendBean();
                        String nickName = cursor.getString(cursor.getColumnIndex(DBcolumns.MAIL_NICKNAME));
                        String remmarkName = cursor.getString(cursor.getColumnIndex(DBcolumns.MAIL_REMARK_NAME));
                        String photo = cursor.getString(cursor.getColumnIndex(DBcolumns.MAIL_PHOTO));
                        int targetId = cursor.getInt(cursor.getColumnIndex(DBcolumns.MAIL_TARGETID));
                        String yunxinAccId = cursor.getString(cursor.getColumnIndex(DBcolumns.MAIL_YUNXIN_ACCID));
                        float unitPrice = cursor.getFloat(cursor.getColumnIndex(DBcolumns.MAIL_UNITPRICE));
                        int status = cursor.getInt(cursor.getColumnIndex(DBcolumns.MAIL_STATUS));
                        friendBean.setNickName(nickName);
                        friendBean.setPhoto(photo);
                        friendBean.setStatus(status);
                        friendBean.setTargetId(targetId);
                        friendBean.setUnitPrice(unitPrice);
                        friendBean.setYunxinAccid(yunxinAccId);
                        friendBean.setRemarkName(remmarkName);
                        friendsList.add(friendBean);
                        cursor.moveToFirst();
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            /*db.close();*/
        }

        return friendsList;
    }

}
