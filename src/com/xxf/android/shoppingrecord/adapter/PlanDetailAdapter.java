package com.xxf.android.shoppingrecord.adapter;

import java.util.ArrayList;

import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.activity.GoodsActivity;
import com.xxf.android.shoppingrecord.db.InnerDB;
import com.xxf.android.shoppingrecord.db.PlanDB;
import com.xxf.android.shoppingrecord.model.GoodsDetail;
import com.xxf.android.shoppingrecord.model.PlanGoods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PlanDetailAdapter extends BaseAdapter {

    private int mCount;
    private long mDate;
    private String mShopCategory;
    private String mShopName;
    private Context mContext;
    private ListView mListView;
    private ArrayList<PlanGoods> mGoodsList = new ArrayList<PlanGoods>();

    public PlanDetailAdapter(Context context, ListView listview) {
        mContext = context;
        mListView = listview;
    }

    public void setShopInfo(String shopCategory, String shopName, long date) {
        mShopCategory = shopCategory;
        mShopName = shopName;
        mDate = date;
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
    public View getView(final int position, View conview, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.plan_detail_list_item, null);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(mGoodsList.get(position).goodsName);
        TextView price = (TextView) view.findViewById(R.id.price);
        String tmp;
        tmp = mContext.getString(R.string.sign_for_yuan) + getStringFromNumber(mGoodsList.get(position).price) + " X " + getStringFromNumber(mGoodsList.get(position).number) + " = " + mContext.getString(R.string.sign_for_yuan) + getStringFromNumber(mGoodsList.get(position).price * mGoodsList.get(position).number / 100);
        price.setText(tmp);

        ImageView status = (ImageView) view.findViewById(R.id.buy_status);
        PlanDB db = new PlanDB(mContext);
        int flag = db.getGoodsFlagById(mGoodsList.get(position).id);
        if (flag == 0) {
            status.setImageResource(R.drawable.checkbox_off);
        }
        else{
            status.setImageResource(R.drawable.checkbox_on);
        }
        status.setTag(mGoodsList.get(position).id);
        status.setOnClickListener(new CheckBoxImp(mGoodsList.get(position).detailId, mGoodsList.get(position).id, position, flag));

        return view;
    }

    public void refresh(ArrayList<PlanGoods> list) {
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

    private class CheckBoxImp implements OnClickListener {

        private int mStatus;
        private int mPosition;
        private long mShopId;
        private long mGoodsId;

        public CheckBoxImp(long shopId, long goodsId, int position, int status) {
            mShopId = shopId;
            mGoodsId = goodsId;
            mPosition = position;
            mStatus = status;
        }

        @Override
        public void onClick(View v) {
            mStatus = 1 - mStatus;
            PlanDB db = new PlanDB(mContext);
            db.setGoodsStatusById(mShopId, mGoodsId, mStatus);
            
            ImageView statusChecked = (ImageView) mListView.findViewWithTag(mGoodsList.get(mPosition).id);
            if (mStatus == 0) {
                statusChecked.setImageResource(R.drawable.checkbox_off);
            }
            else{
                statusChecked.setImageResource(R.drawable.checkbox_on);
            }
            
            InnerDB innerDb = new InnerDB(mContext);
            if (1 == mStatus) {
                long shopId = innerDb.getShopDetailIdByShopAndDate(mShopCategory, mShopName, mDate);
                if (-1 == shopId) {
                    innerDb.insertShopDetail(mShopCategory, mShopName, mDate);
                    shopId = innerDb.getShopDetailIdByShopAndDate(mShopCategory, mShopName, mDate);
                }

                GoodsDetail detail = new GoodsDetail();
                detail.goodsCategory = mGoodsList.get(mPosition).goodsCategory;
                detail.goodsName = mGoodsList.get(mPosition).goodsName;
                detail.price = mGoodsList.get(mPosition).price;
                detail.discount = 100;
                detail.number = mGoodsList.get(mPosition).number;
                detail.detailId = shopId;

                long goodsId = innerDb.getGoodsIdByDetail(detail);
                if (goodsId == -1) {
                    innerDb.insertNewGoodsDetail(detail, true);
                    goodsId = innerDb.getGoodsIdByDetail(detail);
                }

                Intent intent = new Intent(mContext, GoodsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong(ShoppingRecord.BUNDLE_SHOP_ID, shopId);
                bundle.putLong(ShoppingRecord.BUNDLE_GOODS_ID, goodsId);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        }

    }
}
