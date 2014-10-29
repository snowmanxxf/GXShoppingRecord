package com.xxf.android.shoppingrecord.db;

import java.util.ArrayList;

import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.model.GoodsDetail;
import com.xxf.android.shoppingrecord.model.ShopDetail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class InnerDB {

    public final static String DB_NAME = "inner_db";
    public final static String TABEL_DETAIL = "detail";
    public final static String TABEL_LIST = "list";

    public final static String ID = "id";
    public final static String DETAIL_ID = "detail_id";
    public final static String GOODS_CATEGORY = "goods_category";
    public final static String GOODS_NAME = "goods_name";
    public final static String PRICE = "price";
    public final static String NUMBER = "number";
    public final static String DISCOUNT = "discount";
    public final static String SHOP_CATEGORY = "shop_category";
    public final static String SHOP_NAME = "shop_name";
    public final static String DATE = "date";
    public final static String DAN_WEI = "dan_wei";
    
    private BaseDB mBaseDb;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public InnerDB(Context context) {
        mContext = context;
        mBaseDb = BaseDB.getInstance(context);
        mDatabase = mBaseDb.getInnerDB();
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
                 DISCOUNT + " INTEGER)";
        sql[1] = "CREATE TABLE IF NOT EXISTS " + TABEL_LIST + 
                 "(" + ID + " INTEGER PRIMARY KEY," + 
                 SHOP_CATEGORY + " VARCHAR," + 
                 SHOP_NAME + " VARCHAR," + 
                 DATE + " VARCHAR)";
        return sql;
    }
    
    public static String[] getUpgradteDBStrings(){
        String[] sql = new String[1];
        sql[0] = "ALTER TABLE " + TABEL_DETAIL + " ADD " + DAN_WEI + " INTEGER DEFAULT 0";
        return sql;
    }

    public GoodsDetail getGoodsDetailById(long id) {
        GoodsDetail detail = null;
        String[] columns = {ID, DETAIL_ID, GOODS_CATEGORY, GOODS_NAME, PRICE, NUMBER, DISCOUNT, DAN_WEI };
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
            detail.discount = cursor.getInt(cursor.getColumnIndex(DISCOUNT));
            detail.danWei = cursor.getInt(cursor.getColumnIndex(DAN_WEI));
        }
        cursor.close();
        return detail;
    }

    public ArrayList<GoodsDetail> getGoodsDetailByShopId(long id) {
        ArrayList<GoodsDetail> detail = new ArrayList<GoodsDetail>();
        String[] columns = {ID, DETAIL_ID, GOODS_CATEGORY, GOODS_NAME, PRICE, NUMBER, DISCOUNT};
        String selection = DETAIL_ID + "=?";
        String[] selectionArgs = new String[] {id + ""};
        String order = ID + " asc";
        Cursor cursor = mDatabase.query(TABEL_DETAIL, columns, selection, selectionArgs, null, null, order);
        while (cursor.moveToNext()) {
            GoodsDetail goods = new GoodsDetail();
            goods.id = cursor.getLong(cursor.getColumnIndex(ID));
            goods.detailId = cursor.getLong(cursor.getColumnIndex(DETAIL_ID));
            goods.goodsCategory = cursor.getString(cursor.getColumnIndex(GOODS_CATEGORY));
            goods.goodsName = cursor.getString(cursor.getColumnIndex(GOODS_NAME));
            goods.price = cursor.getInt(cursor.getColumnIndex(PRICE));
            goods.number = cursor.getInt(cursor.getColumnIndex(NUMBER));
            goods.discount = cursor.getInt(cursor.getColumnIndex(DISCOUNT));
            detail.add(goods);
        }
        cursor.close();
        return detail;
    }

    public long getShopDetailIdByShopAndDate(String category, String name, long time) {
        String date = ShoppingRecord.longToStringForTimeDB(mContext, time);
        long id = -1;
        String[] columns = new String[]{ID+""};
        String selection = SHOP_CATEGORY + "=? AND " + SHOP_NAME + "=? AND " + DATE + "=?";
        String[] selectionArgs = new String[] {category, name, date};
        Cursor cursor = mDatabase.query(TABEL_LIST, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(ID));
        }
        cursor.close();
        return id;
    }

    public ArrayList<ShopDetail> getShopDetailByAll(String category, String name, long time, int offset, int count) {
        String selection = "";
        String[] selectionArgs = {};

        if (null == category || category.length() == 0) {
            if (-1 != time) {
                selection = DATE + "=?";
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
                    selection = SHOP_CATEGORY + "=? AND " + DATE + "=?";
                    selectionArgs = new String[] {category, ShoppingRecord.longToStringForTimeDB(mContext, time) };
                }
            }
            else {
                if (-1 == time) {
                    selection = SHOP_CATEGORY + "=? AND " + SHOP_NAME + "=?";
                    selectionArgs = new String[] {category, name};
                }
                else {
                    selection = SHOP_CATEGORY + "=? AND " + SHOP_NAME + "=? AND " + DATE + "=?";
                    selectionArgs = new String[] {category, name, ShoppingRecord.longToStringForTimeDB(mContext, time) };
                }
            }
        }

        String[] columns = {ID, SHOP_CATEGORY, SHOP_NAME, DATE};
        ArrayList<ShopDetail> detail = new ArrayList<ShopDetail>();
        String order = DATE + " desc";
        Cursor cursor = mDatabase.query(TABEL_LIST, columns, selection, selectionArgs, null, null, order);
        cursor.moveToPosition(offset);
        if (-1 == count) {
            count = cursor.getCount();
        }
        if (cursor.getCount() < count && count != -1) {
            count = cursor.getCount();
        }
        while (count > 0 && !cursor.isAfterLast()) {
            ShopDetail tmp = new ShopDetail();
            tmp.id = cursor.getLong(cursor.getColumnIndex(ID));
            tmp.category = cursor.getString(cursor.getColumnIndex(SHOP_CATEGORY));
            tmp.name = cursor.getString(cursor.getColumnIndex(SHOP_NAME));
            tmp.date = ShoppingRecord.StringToLongForTimeDB(mContext, cursor.getString(3));
            detail.add(tmp);
            count--;
            cursor.moveToNext();
        }
        cursor.close();
        return detail;
    }

    public void insertShopDetail(String category, String name, long time) {
        String date = ShoppingRecord.longToStringForTimeDB(mContext, time);
        ContentValues values = new ContentValues();
        values.put(SHOP_CATEGORY, category);
        values.put(SHOP_NAME, name);
        values.put(DATE, date);
        mDatabase.insert(TABEL_LIST, null, values);
    }

    public void updateShopDetailTime(long id, long time) {
        String date = ShoppingRecord.longToStringForTimeDB(mContext, time);
        ContentValues values = new ContentValues();
        values.put(DATE, date);
        mDatabase.update(TABEL_LIST, values, ID + "=?", new String[]{id + "" });
    }

    public void deleteShopDetailById(long id) {
        mDatabase.delete(TABEL_LIST, ID + "=?", new String[] {id + ""});
        mDatabase.delete(TABEL_DETAIL, DETAIL_ID + "=?", new String[] {id + ""});
    }

    public long getGoodsIdByDetail(GoodsDetail detail) {
        long id = -1;
        String[] columns = new String[] {ID};
        String selection = DETAIL_ID + "=? AND " + GOODS_CATEGORY + "=? AND " + 
                GOODS_NAME + "=? AND " + PRICE + "=? AND " + DISCOUNT + "=?";
        String[] selectionArgs = new String[] {detail.detailId + "", 
                detail.goodsCategory, 
                detail.goodsName, 
                detail.price + "", 
                detail.discount + ""};
        String order = ID + " desc";
        Cursor cursor = mDatabase.query(TABEL_DETAIL, columns, selection, selectionArgs, null, null, order);
        if(cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(ID));
        }
        cursor.close();
        return id;
    }

    public void insertNewGoodsDetail(GoodsDetail detail, boolean add) {
        String[] columns = new String[] {ID, NUMBER};
        String selection = DETAIL_ID + "=? AND " + GOODS_CATEGORY + "=? AND " + 
                    GOODS_NAME + "=? AND " + PRICE + "=? AND " + DISCOUNT + "=?";
        String[] selectionArgs = new String[] {detail.detailId + "", detail.goodsCategory, 
                    detail.goodsName, detail.price + "", detail.discount + "" };
        String order = ID + " desc";
        Cursor cursor = mDatabase.query(TABEL_DETAIL, columns, selection, selectionArgs, null, null, order);

        ContentValues value = new ContentValues();
        value.put(DETAIL_ID, detail.detailId);
        value.put(GOODS_CATEGORY, detail.goodsCategory);
        value.put(GOODS_NAME, detail.goodsName);
        value.put(PRICE, detail.price);
        value.put(DISCOUNT, detail.discount);
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
            mDatabase.insert(TABEL_DETAIL, null, value);
        }
        cursor.close();
    }

    public void deleteGoodsDetailById(long id) {
        mDatabase.delete(TABEL_DETAIL, ID + "=?", new String[] {id + ""});
    }

    public int getSumGoodsByOwnId(long id) {
        int sum = 0;
        String[] columns = new String[] {PRICE, NUMBER, DISCOUNT};
        String selection = DETAIL_ID + "=?";
        String[] selectionArgs = new String[] {id + ""};
        String order = ID + " desc";
        Cursor cursor = mDatabase.query(TABEL_DETAIL, columns, selection, selectionArgs, null, null, order);
        if (cursor.moveToNext()) {
            int price = cursor.getInt(0);
            int number = cursor.getInt(1);
            int discount = cursor.getInt(2);
            sum += price * number * discount / 10000;
        }
        cursor.close();
        return sum;
    }
}
