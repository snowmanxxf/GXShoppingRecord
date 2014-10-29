package com.xxf.android.shoppingrecord.adapter;

import java.util.ArrayList;

import com.xxf.android.shoppingrecord.model.ShopDetail;

import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShopAdapter extends BaseAdapter {

    private boolean mIsEnd = true;
    private int mCount;
    private ArrayList<ShopDetail> mDetailList = new ArrayList<ShopDetail>();
    private Context mContext;

    public ShopAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        if(mIsEnd){
            return mCount;
        }
        return mCount+1;
    }

    @Override
    public Object getItem(int position) {
        if (position < mCount) {
            return mDetailList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (position < mCount) {
            return mDetailList.get(position).id;
        }
        return -1;
    }

    @Override
    public View getView(int position, View conView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.shop_row, null);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.recent_article_row);
        TextView load = (TextView) view.findViewById(R.id.result_load_more_text);

        if (position < mCount) {
            layout.setVisibility(View.VISIBLE);
            load.setVisibility(View.GONE);
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(mDetailList.get(position).name);
            TextView cate = (TextView) view.findViewById(R.id.cate);
            cate.setText("--" + mDetailList.get(position).category);
            TextView time = (TextView) view.findViewById(R.id.time);
            time.setText(mContext.getString(R.string.last_shop_time) + ShoppingRecord.longToStringForTime(mContext, (mDetailList.get(position).date)));
        }
        else {
            layout.setVisibility(View.GONE);
            load.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public void refreshList(ArrayList<ShopDetail> list) {
        mCount = list.size();
        mDetailList.clear();
        for (int i = 0; i < mCount; i++) {
            mDetailList.add(list.get(i));
        }
    }
    
    public void setEnd(boolean end) {
        mIsEnd = end;
    }
}
