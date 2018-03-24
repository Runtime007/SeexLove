package com.chat.seecolove.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;


/**
 * @author
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "coquettish.db";
    private static final int DB_VERSION = 1;
    private static DBHelper mInstance;

    private static final String sql_mail = "Create table IF NOT EXISTS " + DBcolumns.TABLE_MAIL
            + "(" + DBcolumns.MAIL_ID + " integer primary key autoincrement,"
            + DBcolumns.MAIL_NICKNAME + " text,"
            + DBcolumns.MAIL_PHOTO + " text,"
            + DBcolumns.MAIL_STATUS + " integer,"
            + DBcolumns.MAIL_UNITPRICE + " real,"
            + DBcolumns.MAIL_YUNXIN_ACCID + " text,"
            + DBcolumns.MAIL_REMARK_NAME + " text,"
            + DBcolumns.MAIL_TARGETID + " integer)";

    private DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void cloaseInstance() {
        if (mInstance != null) {
            mInstance.close();
            mInstance = null;
        }
    }

    public synchronized static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            int userId = (int) SharedPreferencesUtils.get(context, Constants.USERID, -1);
            LogTool.setLog("DBHelper--userId->",userId);
            mInstance = new DBHelper(context, userId + DB_NAME, null, DB_VERSION);
            //            mInstance = new DBHelper(context, DB_NAME, null, DB_VERSION);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql_mail);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogTool.setLog("DataBase_upgrade--->","");
        String sql = "DROP TABLE IF EXISTS " + DBcolumns.TABLE_MAIL;
        db.execSQL(sql);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + DBcolumns.TABLE_MAIL;
        db.execSQL(sql);
        onCreate(db);
    }

}
