package com.xxf.android.shoppingrecord;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceActivity;

public class ShoppingRecord extends Application {
    public static final long A_DAY_TO_MSECOND = 24 * 60 * 60 * 1000;

    public static final String BUNDLE_SHOP_CATEGORY = "shop_category";
    public static final String BUNDLE_SHOP_NAME = "shop_name";
    public static final String BUNDLE_RECORD_DATE = "record_date";
    public static final String BUNDLE_SHOP_ID = "shop_id";
    public static final String BUNDLE_GOODS_ID = "goods_id";
    public static final String BUNDLE_FLAG = "flag";
    public static final String BUNDLE_NAME = "name";
    public static final String BUNDLE_ID = "id";

    public static final String SHARE_SEARCH_CORDITION = "search_cordition";
    public static final String VALUE_SHOP_CATEGORY = "shop_category";
    public static final String VALUE_SHOP_NAME = "shop_name";
    public static final String VALUE_RECORD_DATE = "record_date";
    public static final String VALUE_START_DATE = "start_date";
    public static final String VALUE_END_DATE = "end_date";
    
    public static final String DB_TABEL_SHOP_CATEGORY = "shop_category";
    public static final String DB_TABEL_GOODS_CATEGORY = "goods_category";
    public static final String DB_TABEL_SHOP_NAME = "shop_name";
    public static final String DB_TABEL_GOODS_NAME = "goods_name";
    public static final String DB_TABLE_DETAIL = "detail";
    public static final String DB_TABLE_LIST = "list";
    public static final String DB_VALUE_ID = "id";
    public static final String DB_VALUE_NAME = "name";
    public static final String DB_VALUE_OWN = "own";

    public static String longToStringForTime(Context context, long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDate = new SimpleDateFormat(context.getString(R.string.date_format));
        return simpleDate.format(date);
    }

    public static String longToStringForTimeDB(Context context, long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM/dd");
        return simpleDate.format(date);
    }
    
    public static long StringToLongForTimeDB(Context context, String time) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = simpleDate.parse(time);
        }
        catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return date.getTime();
    }
    
    public static void setSearchShopSP(Context context, String category, String name, long time){
        SharedPreferences sp = context.getSharedPreferences(SHARE_SEARCH_CORDITION, PreferenceActivity.MODE_PRIVATE);
        Editor edit= sp.edit();
        edit.putString(VALUE_SHOP_CATEGORY, category);
        edit.putString(VALUE_SHOP_NAME, name);
        edit.putLong(VALUE_RECORD_DATE, time);
        edit.commit();
        
    }
    
    public static void setSearchStatisticsSP(Context context, String category, String name, long start, long end){
        SharedPreferences sp = context.getSharedPreferences(SHARE_SEARCH_CORDITION, PreferenceActivity.MODE_PRIVATE);
        Editor edit= sp.edit();
        edit.putString(VALUE_SHOP_CATEGORY, category);
        edit.putString(VALUE_SHOP_NAME, name);
        edit.putLong(VALUE_START_DATE, start);
        edit.putLong(VALUE_END_DATE, end);
        edit.commit();
    }
    
    public static String getSearchShopCategory(Context context){
        SharedPreferences sp = context.getSharedPreferences(SHARE_SEARCH_CORDITION, PreferenceActivity.MODE_PRIVATE);
        return sp.getString(VALUE_SHOP_CATEGORY, "");
    }
    
    public static String getSearchShopName(Context context){
        SharedPreferences sp = context.getSharedPreferences(SHARE_SEARCH_CORDITION, PreferenceActivity.MODE_PRIVATE);
        return sp.getString(VALUE_SHOP_NAME, "");
    }
    
    public static long getSearchShopTime(Context context){
        SharedPreferences sp = context.getSharedPreferences(SHARE_SEARCH_CORDITION, PreferenceActivity.MODE_PRIVATE);
        return sp.getLong(VALUE_RECORD_DATE, -1);
    }
    
    public static long getSearchStartTime(Context context){
        SharedPreferences sp = context.getSharedPreferences(SHARE_SEARCH_CORDITION, PreferenceActivity.MODE_PRIVATE);
        return sp.getLong(VALUE_START_DATE, -1);
    }
    
    public static long getSearchEndTime(Context context){
        SharedPreferences sp = context.getSharedPreferences(SHARE_SEARCH_CORDITION, PreferenceActivity.MODE_PRIVATE);
        return sp.getLong(VALUE_END_DATE, -1);
    }
}
