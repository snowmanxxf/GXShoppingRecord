package com.xxf.android.shoppingrecord.adapter;

import java.util.ArrayList;

import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.activity.NameActivity;
import com.xxf.android.shoppingrecord.db.OutDB;
import com.xxf.android.shoppingrecord.model.Category;
import com.xxf.android.shoppingrecord.model.Name;
import com.xxf.android.shoppingrecord.view.LinearLayoutForListView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingAdapter extends BaseAdapter {

    private long mId;
    private int mCate;
    private int mCount;
    private String mName;
    private Context mContext;
    private ArrayList<Category> mGoodsList = new ArrayList<Category>();
    private LinearLayoutForListView mListView;

    public SettingAdapter(Context context, String name, int cate, long id) {
        mContext = context;
        mName = name;
        mCate = cate;
        mId = id;
    }

    public SettingAdapter(Context context, String name, int cate, LinearLayoutForListView listView) {
        mContext = context;
        mName = name;
        mCate = cate;
        mListView = listView;
    }

    @Override
    public int getCount() {
        return mCount + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position > 0) {
            return mGoodsList.get(position - 1);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (position > 0) {
            return mGoodsList.get(position - 1).id;
        }
        return -1;
    }

    @Override
    public View getView(final int position, View conview, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.setting_row, null);

        Button detail = (Button) view.findViewById(R.id.detail);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
        TextView text = (TextView) view.findViewById(R.id.first_line);
        if (position > 0) {
            layout.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(mGoodsList.get(position - 1).name);
            Button delete = (Button) view.findViewById(R.id.delete);
            delete.setOnClickListener(new ClickListener(position - 1));
            if (1 == mCate) {
                detail.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(ShoppingRecord.BUNDLE_FLAG, 3);
                        bundle.putLong(ShoppingRecord.BUNDLE_ID, mGoodsList.get(position - 1).id);
                        bundle.putString(ShoppingRecord.BUNDLE_NAME, mGoodsList.get(position - 1).name);
                        Intent intent = new Intent(mContext, NameActivity.class);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                });
            }
            if (2 == mCate) {
                detail.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(ShoppingRecord.BUNDLE_FLAG, 4);
                        bundle.putLong(ShoppingRecord.BUNDLE_ID, mGoodsList.get(position - 1).id);
                        bundle.putString(ShoppingRecord.BUNDLE_NAME, mGoodsList.get(position - 1).name);
                        Intent intent = new Intent(mContext, NameActivity.class);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                });
            }
        }
        else {
            layout.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
            text.setText(mName);
        }
        if (3 == mCate || 4 == mCate) {
            detail.setVisibility(View.GONE);
        }

        return view;
    }

    public String getItemName(int position) {
        if (position > 0) {
            return mGoodsList.get(position - 1).name;
        }
        return "";
    }

    public void refresh(ArrayList<Category> list) {
        mGoodsList.clear();
        mCount = list.size();
        for (int i = 0; i < mCount; i++) {
            mGoodsList.add(list.get(i));
        }
    }

    private class ClickListener implements OnClickListener {

        private int index;

        public ClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View arg0) {
            OutDB db = new OutDB(mContext);
            if (1 == mCate) {
                db.delShopByCategoryId(mGoodsList.get(index).id);
                mGoodsList.clear();
                mGoodsList = db.getShopCategory();
            }
            else if (2 == mCate) {
                db.delGoodsByCategoryId(mGoodsList.get(index).id);
                mGoodsList.clear();
                mGoodsList = db.getGoodsCategory();
            }
            else if (3 == mCate) {
                db.delShopNameById(mGoodsList.get(index).id);
                mGoodsList.clear();
                ArrayList<Name> name = db.getShopName(mId);
                mGoodsList = db.nameToCategory(name);
            }
            else if (4 == mCate) {
                db.delGoodsNameById(mGoodsList.get(index).id);
                mGoodsList.clear();
                ArrayList<Name> name = db.getGoodsName(mId);
                mGoodsList = db.nameToCategory(name);
            }
            mCount = mGoodsList.size();
            if(mListView == null){
                SettingAdapter.this.notifyDataSetChanged();
            }
            else{
                mListView.setVisibility(View.GONE);
                mListView.setAdapter(SettingAdapter.this);
                mListView.setVisibility(View.VISIBLE);
            }
        }
    }
}
