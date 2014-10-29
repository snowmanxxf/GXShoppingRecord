package com.xxf.android.shoppingrecord.activity;

import java.util.ArrayList;

import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.adapter.SettingAdapter;
import com.xxf.android.shoppingrecord.db.OutDB;
import com.xxf.android.shoppingrecord.model.Category;
import com.xxf.android.shoppingrecord.view.LinearLayoutForListView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingActivity extends Activity {
    
    private LinearLayoutForListView mMerchantList;
    private SettingAdapter mMerchantAdapter;
    private LinearLayoutForListView mGoodsList;
    private SettingAdapter mGoodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting_activity);

        init();
    }

    private void init() {
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mMerchantList = (LinearLayoutForListView) findViewById(R.id.merchant_list);
        mGoodsList = (LinearLayoutForListView) findViewById(R.id.goods_list);
        
        mMerchantAdapter = new SettingAdapter(this, getString(R.string.info_merchant_category), 1, mMerchantList);
        mGoodsAdapter = new SettingAdapter(this, getString(R.string.info_goods_category), 2, mGoodsList);

        OutDB db = new OutDB(this);
        ArrayList<Category> list = new ArrayList<Category>();
        list = db.getShopCategory();
        mMerchantAdapter.refresh(list);
        list = db.getGoodsCategory();
        mGoodsAdapter.refresh(list);
        mMerchantList.setAdapter(mMerchantAdapter);
        mGoodsList.setAdapter(mGoodsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); 
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); 
    }
}
