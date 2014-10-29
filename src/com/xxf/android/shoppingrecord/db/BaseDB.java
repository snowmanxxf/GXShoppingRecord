package com.xxf.android.shoppingrecord.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDB {
    
    private static final int DB_VERSION = 7;
    
    private static BaseDB sInstance;
    
    private DBHelper mInnerDBHelper;
    private DBHelper mOutDBHelper;
    private DBHelper mPlanDBHelper;
    
    private SQLiteDatabase mInnerDB;
    private SQLiteDatabase mOutDB;
    private SQLiteDatabase mPlanDB;
    
    public static BaseDB getInstance(Context context){
        if(sInstance == null){
            sInstance = new BaseDB(context);
        }
        return sInstance;
    }
    
    private BaseDB(Context context){
        mInnerDBHelper = new DBHelper(context, InnerDB.DB_NAME, null, DB_VERSION);
        mPlanDBHelper = new DBHelper(context, PlanDB.DB_NAME, null, DB_VERSION);
        mOutDBHelper = new DBHelper(context, OutDB.DB_NAME, null, DB_VERSION);
    }
    
    public SQLiteDatabase getInnerDB(){
        if(mInnerDB == null || !mInnerDB.isOpen()){
            mInnerDB = mInnerDBHelper.getWritableDatabase();
        }
        return mInnerDB;
    }
    
    public SQLiteDatabase getPlanDB(){
        if(mPlanDB == null || !mPlanDB.isOpen()){
            mPlanDB = mPlanDBHelper.getWritableDatabase();
        }
        return mPlanDB;
    }
    
    public SQLiteDatabase getOutDB(){
        if(mOutDB == null || !mOutDB.isOpen()){
            mOutDB = mOutDBHelper.getWritableDatabase();
        }
        return mOutDB;
    }
    
    public void closeAllDBs(){
        if(mOutDB != null && mOutDB.isOpen()){
            mOutDB.close();
        }
        if(mInnerDB != null && mInnerDB.isOpen()){
            mInnerDB.close();
        }
        if(mPlanDB != null && mPlanDB.isOpen()){
            mPlanDB.close();
        }
    }
    
    private class DBHelper extends SQLiteOpenHelper{

        private int mCurrentDBIndex;
        private final String[] DB_NAMES = new String[]{InnerDB.DB_NAME, PlanDB.DB_NAME, OutDB.DB_NAME};
        private final int INNER_DB_INDEX = 0;
        private final int PLAN_DB_INDEX = 1;
        private final int OUT_DB_INDEX = 2;
        
        public DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
            for(int i = 0; i < DB_NAMES.length; i ++){
                if(name.equals(DB_NAMES[i])){
                    mCurrentDBIndex = i;
                    break;
                }
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String[] sql = null;
            
            switch (mCurrentDBIndex) {
            case INNER_DB_INDEX:
                sql = InnerDB.getCreateDBStrings();
                break;
            case PLAN_DB_INDEX:
                sql = PlanDB.getCreateDBStrings();
                break;
            case OUT_DB_INDEX:
                sql = OutDB.getCreateDBStrings();
                break;
            }

            if(sql != null){
                for(int i = 0 ; i < sql.length; i ++){
                    db.execSQL(sql[i]);
                }
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String[] sql = null;
            
            switch (mCurrentDBIndex) {
            case INNER_DB_INDEX:
                sql = InnerDB.getUpgradteDBStrings();
                break;
            case PLAN_DB_INDEX:
                sql = PlanDB.getUpgradeDBStrings();
                break;
            case OUT_DB_INDEX:
                sql = OutDB.getUpgradeDBStrings();
                break;
            }
            
            if(oldVersion == 0){
                if(sql != null){
                    for(int i = 0; i < sql.length; i ++){
                        db.execSQL(sql[i]);
                    }
                }
            }
        }
        
    }
}
