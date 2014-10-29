package com.xxf.android.shoppingrecord.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.model.Category;
import com.xxf.android.shoppingrecord.model.Name;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class OutDB {

    public final static String DB_NAME = "out_db";
    public final static String TABEL_SHOP_CATEGORY = "shop_category";
    public final static String TABEL_SHOP_NAME = "shop_name";
    public final static String TABEL_GOODS_CATEGORY = "goods_category";
    public final static String TABEL_GOODS_NAME = "goods_name";
    
    public final static String ID = "id";
    public final static String NAME = "name";
    public final static String OWN = "own";
    
    private SQLiteDatabase mDatabase;

    private final int DB_SIZE = 30 * 1024;
    private final String PACKAGE_NAME = "com.xxf.android.shoppingrecord";
    private final String TABEL_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/databases";

    private BaseDB mBaseDB;
    
    private void openDatabase(Context context) {
        try {
            File fileDir = new File(TABEL_PATH);
            File file = new File(TABEL_PATH + "/" + DB_NAME);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            if (!file.exists()) {
                InputStream is = context.getResources().openRawResource(R.raw.out_db);
                FileOutputStream os = new FileOutputStream(TABEL_PATH + "/" + DB_NAME);
                byte[] buffer = new byte[DB_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    os.write(buffer, 0, count);
                }
                os.close();
                is.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public OutDB(Context context) {
        openDatabase(context);
        mBaseDB = BaseDB.getInstance(context);
        mDatabase = mBaseDB.getOutDB();
    }
    
    public static String[] getCreateDBStrings(){
        return null;
    }
    
    public static String[] getUpgradeDBStrings(){
        return null;
    }

    public ArrayList<Category> getShopCategory() {
        ArrayList<Category> ret = new ArrayList<Category>();
        Cursor cursor = null;
        String[] columns = new String[] {ID, NAME};
        String order = NAME + " asc";
        cursor = mDatabase.query(TABEL_SHOP_CATEGORY, columns, null, null, null, null, order, null);
        while (cursor.moveToNext()) {
            Category category = new Category();
            category.id = cursor.getLong(cursor.getColumnIndex(ID));
            category.name = cursor.getString(cursor.getColumnIndex(NAME));
            ret.add(category);
        }
        cursor.close();
        return ret;
    }

    public void insertShopCategory(String name) {
        ArrayList<Category> list = getShopCategory();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            if (name.equals(list.get(i).name)) {
                return;
            }
        }
        ContentValues value = new ContentValues();
        value.put(NAME, name);
        mDatabase.insert(TABEL_SHOP_CATEGORY, null, value);
    }

    public long getCategoryShopIdByName(String name) {
        long id = -1;
        Cursor cursor = null;
        String[] columns = new String[] {ID};
        String selection = NAME + "=?";
        String[] selectionArgs = new String[] {name};
        String order = NAME + " asc";
        cursor = mDatabase.query(TABEL_SHOP_CATEGORY, columns, selection, selectionArgs, null, null, order, null);
        while (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(ID));
        }
        cursor.close();
        return id;
    }

    public void delShopByCategoryId(long id) {
        mDatabase.delete(TABEL_SHOP_CATEGORY, ID + "=?", new String[] {id + ""});
        mDatabase.delete(TABEL_SHOP_NAME, OWN + "=?", new String[] {id + ""});
    }

    public void delGoodsByCategoryId(long id) {
        mDatabase.delete(TABEL_GOODS_CATEGORY, ID + "=?", new String[] {id + ""});
        mDatabase.delete(TABEL_GOODS_NAME, OWN + "=?", new String[] {id + ""});
    }

    public void delShopNameById(long id) {
        mDatabase.delete(TABEL_SHOP_NAME, ID + "=?", new String[] {id + ""});
    }

    public void delGoodsNameById(long id) {
        mDatabase.delete(TABEL_GOODS_NAME, ID + "=?", new String[] {id + ""});
    }

    public ArrayList<Name> getShopName(long id) {
        ArrayList<Name> ret = new ArrayList<Name>();
        String[] columns = new String[] {ID, NAME, OWN};
        String selection = OWN + "=?";
        String[] selectionArgs = new String[] {id + ""};
        String order = NAME + " asc";
        Cursor cursor = mDatabase.query(TABEL_SHOP_NAME, columns, selection, selectionArgs, null, null, order, null);
        while (cursor.moveToNext()) {
            Name name = new Name();
            name.id = cursor.getLong(cursor.getColumnIndex(ID));
            name.name = cursor.getString(cursor.getColumnIndex(NAME));
            name.own = cursor.getLong(cursor.getColumnIndex(OWN));
            ret.add(name);
        }
        cursor.close();
        return ret;
    }

    public void insertShopName(String name, long own) {
        ArrayList<Name> list = getShopName(own);
        int length = list.size();
        for (int i = 0; i < length; i++) {
            if (name.equals(list.get(i).name)) {
                return;
            }
        }
        ContentValues value = new ContentValues();
        value.put(NAME, name);
        value.put(OWN, own);
        mDatabase.insert(TABEL_SHOP_NAME, null, value);
    }

    public ArrayList<Category> getGoodsCategory() {
        ArrayList<Category> ret = new ArrayList<Category>();
        String[] columns = new String[] {ID, NAME };
        String order = NAME + " asc";
        Cursor cursor = mDatabase.query(TABEL_GOODS_CATEGORY, columns, null, null, null, null, order, null);
        while (cursor.moveToNext()) {
            Category category = new Category();
            category.id = cursor.getLong(cursor.getColumnIndex(ID));
            category.name = cursor.getString(cursor.getColumnIndex(NAME));
            ret.add(category);
        }
        cursor.close();
        return ret;
    }

    public ArrayList<Name> getGoodsName(long id) {
        ArrayList<Name> ret = new ArrayList<Name>();
        String[] columns = new String[] {ID, NAME, OWN};
        String selection = OWN + "=?";
        String[] selectionArgs = new String[] {id + ""};
        String order = NAME + " asc";
        Cursor cursor = mDatabase.query(TABEL_GOODS_NAME, columns, selection, selectionArgs, null, null, order, null);
        while (cursor.moveToNext()) {
            Name name = new Name();
            name.id = cursor.getLong(cursor.getColumnIndex(ID));
            name.name = cursor.getString(cursor.getColumnIndex(NAME));
            name.own = cursor.getLong(cursor.getColumnIndex(OWN));
            ret.add(name);
        }
        cursor.close();
        return ret;
    }

    public void insertGoodsCategory(String name) {
        ArrayList<Category> list = getGoodsCategory();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            if (name.equals(list.get(i).name)) {
                return;
            }
        }
        ContentValues value = new ContentValues();
        value.put(NAME, name);
        mDatabase.insert(TABEL_GOODS_CATEGORY, null, value);
    }

    public long getCategoryGoodsIdByName(String name) {
        long id = -1;
        String[] columns = new String[] {ID, NAME};
        String selection = NAME + "=?";
        String[] selectionArgs = new String[] {name};
        String order = NAME + " asc";
        Cursor cursor = mDatabase.query(TABEL_GOODS_CATEGORY, columns, selection, selectionArgs, null, null, order, null);
        while (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(ID));
        }
        cursor.close();
        return id;
    }

    public void insertGoodsName(String name, long own) {
        ArrayList<Name> list = getGoodsName(own);
        int length = list.size();
        for (int i = 0; i < length; i++) {
            if (name.equals(list.get(i).name)) {
                return;
            }
        }
        ContentValues value = new ContentValues();
        value.put(NAME, name);
        value.put(OWN, own);
        mDatabase.insert(TABEL_GOODS_NAME, null, value);
    }

    public ArrayList<Category> nameToCategory(ArrayList<Name> name) {
        ArrayList<Category> category = new ArrayList<Category>();
        for (int i = 0; i < name.size(); i++) {
            Category tmp = new Category();
            tmp.id = name.get(i).id;
            tmp.name = name.get(i).name;
            category.add(tmp);
        }
        return category;
    }
}
