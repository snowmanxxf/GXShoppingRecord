package com.xxf.android.shoppingrecord.adapter;

import java.util.ArrayList;

import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.model.GoodsDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DetailAdapter extends BaseAdapter {

    private int mCount;
    private Context mContext;
    private ArrayList<GoodsDetail> mGoodsList = new ArrayList<GoodsDetail>();

    public DetailAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(int position) {
        return mGoodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mGoodsList.get(position).id;
    }

    @Override
    public View getView(int position, View conview, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.detail_list_item, null);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(mGoodsList.get(position).goodsName);
        TextView price = (TextView) view.findViewById(R.id.price);
        String tmp;
        tmp = mContext.getString(R.string.sign_for_yuan) + getStringFromNumber(mGoodsList.get(position).price) + " X " + getStringFromNumber(mGoodsList.get(position).number) + " X "
                + getStringFromNumber(mGoodsList.get(position).discount) + " = " + mContext.getString(R.string.sign_for_yuan)
                + getStringFromNumber(mGoodsList.get(position).price * mGoodsList.get(position).number * mGoodsList.get(position).discount / 10000);
        price.setText(tmp);

        return view;
    }

    public void refresh(ArrayList<GoodsDetail> list) {
        mGoodsList.clear();
        mCount = list.size();
        for (int i = 0; i < mCount; i++) {
            mGoodsList.add(list.get(i));
        }
    }

    private String getStringFromNumber(int number) {
        String tmp;
        int inter = number / 100;
        int deci = number % 100;
        if (deci < 10) {
            tmp = inter + ".0" + deci;
        }
        else {
            tmp = inter + "." + deci;
        }
        return tmp;
    }
}
