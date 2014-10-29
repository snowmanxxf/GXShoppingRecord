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

public class ResultAdapter extends BaseAdapter {

    private int mCount;
    private ArrayList<ShopDetail> mDetailList = new ArrayList<ShopDetail>();
    private ArrayList<Integer> mSumList = new ArrayList<Integer>();
    private Context mContext;

    public ResultAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(int position) {
        return mDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDetailList.get(position).id;
    }

    @Override
    public View getView(int position, View conView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.result_row, null);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.recent_article_row);

        layout.setVisibility(View.VISIBLE);
        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(mDetailList.get(position).name);
        TextView cate = (TextView) view.findViewById(R.id.sum);
        String tmp;
        if (mSumList.get(position) % 100 < 10) {
            tmp = mSumList.get(position) / 100 + ".0" + mSumList.get(position) % 100;
        }
        else {
            tmp = mSumList.get(position) / 100 + "." + mSumList.get(position) % 100;
        }
        cate.setText(mContext.getString(R.string.sign_for_yuan) + tmp);
        TextView time = (TextView) view.findViewById(R.id.time);
        time.setText(mContext.getString(R.string.last_shop_time)
                + ShoppingRecord.longToStringForTime(mContext, (mDetailList.get(position).date)));
        return view;
    }

    public void refreshList(ArrayList<ShopDetail> list, ArrayList<Integer> sum) {
        mCount = list.size();
        mDetailList.clear();
        for (int i = 0; i < mCount; i++) {
            mDetailList.add(list.get(i));
        }

        mSumList.clear();
        for (int i = 0; i < mCount; i++) {
            mSumList.add(sum.get(i));
        }
    }
}
