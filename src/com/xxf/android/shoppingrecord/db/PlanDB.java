package com.xxf.android.shoppingrecord.db;

import java.util.ArrayList;

import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.model.GoodsDetail;
import com.xxf.android.shoppingrecord.model.PlanGoods;
import com.xxf.android.shoppingrecord.model.PlanList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PlanDB {

    public final static String DB_NAME = "plan_db";
    public final static String TABEL_DETAIL = "detail";
    public final static String TABEL_LIST = "list";
    
    public final static String ID = "id";
    public final static String DETAIL_ID = "detail_id";
    public final static String GOODS_CATEGORY = "goods_category";
    public final static String GOODS_NAME = "goods_name";
    public final static String PRICE = "price";
    public final static String NUMBER = "number";
    public final static String FLAG = "flag";
    public final static String SHOP_CATEGORY = "shop_category";
    public final static String SHOP_NAME = "shop_name";
    public final static String PLAN_DATE = "plan_date";
    public final static String UPDATE_DATE = "update_date";
    public final static String DAN_WEI = "dan_wei";
    
    private Context mContext;
    private SQLiteDatabase mDatabase;
    
    private BaseDB mBaseDB;

    public PlanDB(Context context) {
        mBaseDB = BaseDB.getInstance(context);
        mDatabase = mBaseDB.getPlanDB();
        mContext = context;
    }
    
    public static String[] getCreateDBStrings(){
        String[] sql = new String[2];
        sql[0] = "CREATE TABLE IF NOT EXISTS " + TABEL_DETAIL + 
                "(" + ID + " INTEGER PRIMARY KEY," + 
                DETAIL_ID + " INTEGER," + 
                GOODS_CATEGORY + " VARCHAR," + 
                GOODS_NAME + " VARCHAR," + 
                PRICE + " INTEGER," + 
                NUMBER + " INTEGER," + 
                DAN_WEI + " INTEGER," + 
                FLAG + " INTEGER)";
        sql[1] = "CREATE TABLE IF NOT EXISTS " + TABEL_LIST + 
                "(" + ID + " INTEGER PRIMARY KEY," + 
                SHOP_CATEGORY + " VARCHAR," + 
                SHOP_NAME + " VARCHAR," + 
                PLAN_DATE + " VARCHAR," + 
                UPDATE_DATE + " INTEGER," + 
                FLAG + " INTEGER)";
        return sql;
    }
    
    public static String[] getUpgradeDBStrings(){
        String[] sql = new String[1];
        sql[0] = "ALTER TABLE " + TABEL_DETAIL + " ADD " + DAN_WEI + " INTEGER DEFAULT 0";
        return null;
    }

    public ArrayList<PlanList> getShopDetailByAll(String category, String name, long time, int offset, int count) {
        String selection = "";
        String[] selectionArgs = {};

        if (null == category || category.length() == 0) {
            if (-1 != time) {
                selection = PLAN_DATE + "=?";
                selectionArgs = new String[] {ShoppingRecord.longToStringForTimeDB(mContext, time)};
            }
        }
        else {
            if (null == name || name.length() == 0) {
                if (-1 == time) {
                    selection = SHOP_CATEGORY + "=?";
                    selectionArgs = new String[] {category};
                }
                else {
                    selection = SHOP_CATEGORY + "=? AND " + PLAN_DATE + "=?";
                    selectionArgs = new String[] {category, ShoppingRecord.longToStringForTimeDB(mContext, time)};
                }
            }
            else {
                if (-1 == time) {
                    selection = SHOP_CATEGORY + "=? AND " + SHOP_NAME + "=?";
                    selectionArgs = new String[] {category, name};
                }
                else {
                    selection = SHOP_CATEGORY + "=? AND " + SHOP_NAME +"=? AND " + PLAN_DATE + "=?";
                    selectionArgs = new String[] {category, name, ShoppingRecord.longToStringForTimeDB(mContext, time)};
                }
            }
        }
        
        ArrayList<PlanList> detail = new ArrayList<PlanList>();
        String[] columns = {ID, SHOP_CATEGORY, SHOP_NAME, PLAN_DATE, FLAG};
        String order = FLAG + " asc," + PLAN_DATE + " asc," + UPDATE_DATE + " desc";
        Cursor cursor = mDatabase.query(TABEL_LIST, columns, selection, selectionArgs, null, null, order);
        if (cursor.getCount() > offset) {
            cursor.moveToPosition(offset);
            if (-1 == count) {
                count = cursor.getCount();
            }
            if (cursor.getCount() < count && count != -1) {
                count = cursor.getCount();
            }
            while (count > 0 && !cursor.isAfterLast()) {
                PlanList tmp = new PlanList();
                tmp.id = cursor.getLong(cursor.getColumnIndex(ID));
                tmp.category = cursor.getString(cursor.getColumnIndex(SHOP_CATEGORY));
                tmp.name = cursor.getString(cursor.getColumnIndex(SHOP_NAME));
                tmp.planDate = ShoppingRecord.StringToLongForTimeDB(mContext, cursor.getString(cursor.getColumnIndex(PLAN_DATE)));
                tmp.flag = cursor.getInt(cursor.getColumnIndex(FLAG));
                detail.add(tmp);
                count--;
                cursor.moveToNext();
            }
        }
        cursor.close();
        return detail;
    }

    public void updateShopDetailTime(long id, long time) {
        String date = ShoppingRecord.longToStringForTimeDB(mContext, time);
        ContentValues values = new ContentValues();
        values.put(PLAN_DATE, date);
        mDatabase.update(TABEL_LIST, values, ID + "=?", new String[] {id + ""});
    }

    public void deleteGoodsDetailById(long id) {
        mDatabase.delete(TABEL_DETAIL, ID + "=?", new String[] {id + ""});
    }

    public long getShopDetailIdByShopAndDate(String category, String name, long time) {
        String date = ShoppingRecord.longToStringForTimeDB(mContext, time);
        long id = -1;
        String[] columns = {ID};
        String selection = SHOP_CATEGORY + "=? AND " + SHOP_NAME + "=? AND " + PLAN_DATE + "=?";
        String[] selectionArgs = new String[] {category, name, date};
        Cursor cursor = mDatabase.query(TABEL_LIST, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(ID));
        }
        cursor.close();
        return id;
    }

    public void insertShopDetail(String category, String name, long time) {
        String date = ShoppingRecord.longToStringForTimeDB(mContext, time);
        ContentValues values = new ContentValues();
        values.put(SHOP_CATEGORY, category);
        values.put(SHOP_NAME, name);
        values.put(PLAN_DATE, date);
        values.put(UPDATE_DATE, System.currentTimeMillis());
        values.put(FLAG, 1);
        mDatabase.insert(TABEL_LIST, null, values);
    }

    public ArrayList<PlanGoods> getGoodsDetailByShopId(long id) {
        ArrayList<PlanGoods> detail = new ArrayList<PlanGoods>();
        String[] columns = {ID, DETAIL_ID, GOODS_CATEGORY, GOODS_NAME, PRICE, NUMBER, FLAG};
        String selection = DETAIL_ID + "=?";
        String[] selectionArgs = new String[] {id + ""};
        String order = ID + " asc";
        Cursor cursor = mDatabase.query(TABEL_DETAIL, columns, selection, selectionArgs, null, null, order);
        while (cursor.moveToNext()) {
            PlanGoods goods = new PlanGoods();
            goods.id = cursor.getLong(cursor.getColumnIndex(ID));
            goods.detailId = cursor.getLong(cursor.getColumnIndex(DETAIL_ID));
            goods.goodsCategory = cursor.getString(cursor.getColumnIndex(GOODS_CATEGORY));
            goods.goodsName = cursor.getString(cursor.getColumnIndex(GOODS_NAME));
            goods.price = cursor.getInt(cursor.getColumnIndex(PRICE));
            goods.number = cursor.getInt(cursor.getColumnIndex(NUMBER));
            goods.flag = cursor.getInt(cursor.getColumnIndex(FLAG));
            detail.add(goods);
        }
        cursor.close();
        return detail;
    }

    public void insertNewGoodsDetail(GoodsDetail detail, boolean add) {
        String[] columns = new String[] {ID, NUMBER};
        String selection = DETAIL_ID + "=? AND " + GOODS_CATEGORY + "=? AND " + GOODS_NAME + "=? AND " + PRICE + "=?";
        String[] selectionArgs = new String[] {detail.detailId + "", detail.goodsCategory, detail.goodsName, detail.price + ""};
        String order = ID + " desc";
        Cursor cursor = mDatabase.query(TABEL_DETAIL, columns, selection, selectionArgs, null, null, order);

        ContentValues value = new ContentValues();
        value.put(DETAIL_ID, detail.detailId);
        value.put(GOODS_CATEGORY, detail.goodsCategory);
        value.put(GOODS_NAME, detail.goodsName);
        value.put(PRICE, detail.price);
        value.put(DAN_WEI, detail.danWei);

        if (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(ID));
            int number = detail.number;
            if (add) {
                number += cursor.getInt(cursor.getColumnIndex(NUMBER));
            }
            value.put(NUMBER, number);
            mDatabase.update(TABEL_DETAIL, value, ID + "=?", new String[] {id + ""});
        }
        else {
            value.put(NUMBER, detail.number);
            value.put(FLAG, 0);
            mDatabase.insert(TABEL_DETAIL, null, value);
        }
        cursor.close();
    }

    public GoodsDetail getGoodsDetailById(long id) {
        GoodsDetail detail = null;
        String[] columns = {ID, DETAIL_ID, GOODS_CATEGORY, GOODS_NAME, PRICE, NUMBER, DAN_WEI};
        String selection = ID + "=?";
        String[] selectionArgs = new String[] {id + ""};
        String order = ID + " asc";
        Cursor cursor = mDatabase.query(TABEL_DETAIL, columns, selection, selectionArgs, null, null, order);
        if (cursor.moveToNext()) {
            detail = new GoodsDetail();
            detail.id = cursor.getLong(cursor.getColumnIndex(ID));
            detail.detailId = cursor.getLong(cursor.getColumnIndex(DETAIL_ID));
            detail.goodsCategory = cursor.getString(cursor.getColumnIndex(GOODS_CATEGORY));
            detail.goodsName = cursor.getString(cursor.getColumnIndex(GOODS_NAME));
            detail.price = cursor.getInt(cursor.getColumnIndex(PRICE));
            detail.number = cursor.getInt(cursor.getColumnIndex(NUMBER));
            detail.danWei = cursor.getInt(cursor.getColumnIndex(DAN_WEI));
        }
        cursor.close();
        return detail;
    }

    public void deleteShopDetailById(long id) {
        mDatabase.delete(TABEL_LIST, ID + "=?", new String[] {id + ""});
        mDatabase.delete(TABEL_DETAIL, DETAIL_ID + "=?", new String[] {id + ""});
    }

    public void setGoodsStatusById(long shopId, long goodsId, int status) {
        ContentValues values = new ContentValues();
        values.put(FLAG, status);
        mDatabase.update(TABEL_DETAIL, values, ID + "=?", new String[] {goodsId + ""});
        String[] columns = new String[] {FLAG};
        String selection = DETAIL_ID + "=?";
        String[] selectionArgs = new String[] {shopId + ""};
        Cursor cursor = mDatabase.query(TABEL_DETAIL, columns, selection, selectionArgs, null, null, null);
        if (cursor.getColumnCount() > 0) {
            int isEmpty = 1;
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex(FLAG)) == 0) {
                    isEmpty = 0;
                    break;
                }
            }
            values = new ContentValues();
            values.put(FLAG, isEmpty);
            mDatabase.update(TABEL_LIST, values, ID+"=?", new String[] {shopId + ""});
        }
        else {
            values = new ContentValues();
            values.put(FLAG, 1);
            mDatabase.update(TABEL_LIST, values, ID+"=?", new String[] {shopId + ""});
        }
        cursor.close();
    }

    public int getGoodsFlagById(long id){
        int flag = 0;
        Cursor cursor = null;
        cursor = mDatabase.query(TABEL_DETAIL, new String[]{FLAG}, ID+"=?", new String[]{id+""}, null, null, null);
        if(cursor.moveToNext()){
            flag = cursor.getInt(cursor.getColumnIndex(FLAG));
        }
        cursor.close();
        return flag;
    }
}
