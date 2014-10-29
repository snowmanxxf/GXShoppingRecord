package com.xxf.android.shoppingrecord.activity;

import java.util.ArrayList;

import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.db.OutDB;
import com.xxf.android.shoppingrecord.db.PlanDB;
import com.xxf.android.shoppingrecord.dialog.ChoiceCategoryDialog;
import com.xxf.android.shoppingrecord.dialog.ChoiceNameDialog;
import com.xxf.android.shoppingrecord.dialog.SetNumberDialog;
import com.xxf.android.shoppingrecord.dialog.SetNumberDialog.SetNumberListenerInterface;
import com.xxf.android.shoppingrecord.dialog.ChoiceCategoryDialog.ChoiceCategoryListener;
import com.xxf.android.shoppingrecord.dialog.ChoiceNameDialog.ChoiceNameListener;
import com.xxf.android.shoppingrecord.model.Category;
import com.xxf.android.shoppingrecord.model.GoodsDetail;
import com.xxf.android.shoppingrecord.model.Name;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PlanGoodsActivity extends Activity implements ChoiceCategoryListener, ChoiceNameListener, SetNumberListenerInterface {

    private boolean mHasInit = false;
    private boolean mChoseCategory = false;
    private boolean mChoseName = false;
    private int mDanweiWhich = 0;
    private long mShopId = -1;
    private long mGoodsId = -1;
    private long mOwn = -1;
    private LinearLayout mCategoryView;
    private LinearLayout mNameView;
    private LinearLayout mPriceView;
    private LinearLayout mNumberView;
    private TextView mCategoryTextView;
    private TextView mNameTextView;
    private TextView mPriceTextView;
    private TextView mNumberTextView;
    private Button mConfirmButton;
    private Button mCancelButton;
    private String[] mDanWei;
    private Button mDanweiButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.plan_goods_activity);
        init();
        initValue();
    }

    private void init() {
        mDanWei = getResources().getStringArray(R.array.dan_wei_value);
        mDanweiButton = (Button) findViewById(R.id.dan_wei);
        mDanweiButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                showChooseDanweiList();
            }
        });
        
        mCategoryTextView = (TextView) findViewById(R.id.choice_category);
        mNameTextView = (TextView) findViewById(R.id.choice_name);
        mPriceTextView = (TextView) findViewById(R.id.set_price);
        mNumberTextView = (TextView) findViewById(R.id.set_number);
        mConfirmButton = (Button) findViewById(R.id.corfirm);
        mCancelButton = (Button) findViewById(R.id.cancel);
        mCategoryView = (LinearLayout) findViewById(R.id.choice_category_view);
        mNameView = (LinearLayout) findViewById(R.id.choice_name_view);
        mPriceView = (LinearLayout) findViewById(R.id.set_price_view);
        mNumberView = (LinearLayout) findViewById(R.id.set_number_view);

        mCategoryView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                OutDB db = new OutDB(PlanGoodsActivity.this);
                ArrayList<Category> categoryList = db.getGoodsCategory();

                ChoiceCategoryDialog categoryDialog = new ChoiceCategoryDialog(PlanGoodsActivity.this, R.string.add_category, mCategoryTextView.getText().toString(), categoryList, PlanGoodsActivity.this, true);
                categoryDialog.show();
            }
        });

        mNameView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (-1 != mOwn) {
                    OutDB db = new OutDB(PlanGoodsActivity.this);
                    ArrayList<Name> nameList = db.getGoodsName(mOwn);

                    ChoiceNameDialog nameDialog = new ChoiceNameDialog(R.string.goods_choice, R.string.add_goods, PlanGoodsActivity.this, mNameTextView.getText().toString(), mOwn, nameList, PlanGoodsActivity.this, true);
                    nameDialog.show();
                }
                else {
                    Toast.makeText(PlanGoodsActivity.this, PlanGoodsActivity.this.getString(R.string.info_choice_category_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mPriceView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                float price = Float.parseFloat(mPriceTextView.getText().toString());
                price += 0.005;
                SetNumberDialog dialog = new SetNumberDialog(mPriceTextView, PlanGoodsActivity.this, R.string.input_price, R.string.price_for_yuan, (int) (price * 100), 2, 99999999, PlanGoodsActivity.this);
                dialog.show();
            }
        });

        mNumberView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                float number = Float.parseFloat(mNumberTextView.getText().toString());
                number += 0.005;
                SetNumberDialog dialog = new SetNumberDialog(mNumberTextView, PlanGoodsActivity.this, R.string.input_number, -1, (int) (number * 100), 2, 99999999, PlanGoodsActivity.this);
                dialog.show();
            }
        });

        mConfirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String category = mCategoryTextView.getText().toString();
                String name = mNameTextView.getText().toString();
                if (null == category || category.length() == 0 || false == mChoseCategory) {
                    Toast.makeText(PlanGoodsActivity.this, PlanGoodsActivity.this.getString(R.string.info_can_not_empty_category), Toast.LENGTH_SHORT).show();
                }
                else if (null == name || name.length() == 0 || false == mChoseName) {
                    Toast.makeText(PlanGoodsActivity.this, PlanGoodsActivity.this.getString(R.string.info_can_not_empty_name), Toast.LENGTH_SHORT).show();
                }
                else {
                    PlanDB db = new PlanDB(PlanGoodsActivity.this);
                    db.insertNewGoodsDetail(getGoodsDetail(), !mHasInit);
                    db.setGoodsStatusById(mShopId, mGoodsId, 1);
                    finish();
                }
            }
        });

        mCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }
    
    private void showChooseDanweiList() {
        final String[] danweiArray = getResources().getStringArray(R.array.dan_wei_value);

        AlertDialog.Builder cityList = new AlertDialog.Builder(this);
        cityList.setTitle(R.string.choose_dan_wei);
        cityList.setSingleChoiceItems(danweiArray, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mDanweiButton.setText(mDanWei[which]);
                mDanweiWhich = which;
            }
        });
        cityList.show();
    }

    @Override
    public void choiceCategoryListener(String name, long id) {
        mCategoryTextView.setText(name);
        mNameTextView.setText(getString(R.string.click_choice));
        mOwn = id;
        mChoseCategory = true;
        mChoseName = false;
    }

    @Override
    public void insertCategoryListener(String name) {
        OutDB db = new OutDB(PlanGoodsActivity.this);
        db.insertGoodsCategory(name);
        mCategoryTextView.setText(name);
        mOwn = db.getCategoryGoodsIdByName(name);
        mNameTextView.setText(getString(R.string.click_choice));
        mChoseCategory = true;
        mChoseName = false;
    }

    @Override
    public void choiceNameListener(String name) {
        mNameTextView.setText(name);
        mChoseName = true;
    }

    @Override
    public void insertNameListener(String name, long own) {
        OutDB db = new OutDB(PlanGoodsActivity.this);
        db.insertGoodsName(name, own);
        mNameTextView.setText(name);
        mChoseName = true;
    }

    private void initValue() {
        Bundle bundle = getIntent().getExtras();
        mShopId = bundle.getLong(ShoppingRecord.BUNDLE_SHOP_ID);
        mGoodsId = bundle.getLong(ShoppingRecord.BUNDLE_GOODS_ID);
        PlanDB db = new PlanDB(this);
        GoodsDetail list = db.getGoodsDetailById(mGoodsId);
        if (list != null) {
            mHasInit = true;
            mChoseCategory = true;
            mChoseName = true;
            mCategoryTextView.setText(list.goodsCategory);
            mNameTextView.setText(list.goodsName);

            OutDB outdb = new OutDB(this);
            mOwn = outdb.getCategoryGoodsIdByName(list.goodsCategory);

            String tmp;
            if (list.price < 100) {
                tmp = "0." + list.price;
            }
            else {
                if (list.price % 100 < 10) {
                    tmp = list.price / 100 + ".0" + list.price % 100;
                }
                else {
                    tmp = list.price / 100 + "." + list.price % 100;
                }
            }
            mPriceTextView.setText(tmp);
            if (list.number < 100) {
                tmp = "0." + list.number;
            }
            else {
                if (list.number % 100 < 10) {
                    tmp = list.number / 100 + ".0" + list.number % 100;
                }
                else {
                    tmp = list.number / 100 + "." + list.number % 100;
                }
            }
            mNumberTextView.setText(String.valueOf(tmp));

            TextView title = (TextView) findViewById(R.id.title);
            title.setText(R.string.fix_good);
            mConfirmButton.setText(R.string.fix);
            
            mDanweiButton.setText(mDanWei[list.danWei]);
            mDanweiWhich = list.danWei;
        }
        else {
            mPriceTextView.setText("0.00");
            mNumberTextView.setText("0.00");
        }

        setNumberListenerInterface();
    }

    private GoodsDetail getGoodsDetail() {
        GoodsDetail detail = new GoodsDetail();
        detail.detailId = mShopId;
        detail.goodsCategory = mCategoryTextView.getText().toString();
        detail.goodsName = mNameTextView.getText().toString();
        float price = Float.parseFloat(mPriceTextView.getText().toString());
        price += 0.005;
        detail.price = (int) (price * 100);
        float number = Float.parseFloat(mNumberTextView.getText().toString());
        number += 0.005;
        detail.number = (int) (number * 100);
        detail.danWei = mDanweiWhich;
        return detail;
    }

    @Override
    public void setNumberListenerInterface() {
        
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
