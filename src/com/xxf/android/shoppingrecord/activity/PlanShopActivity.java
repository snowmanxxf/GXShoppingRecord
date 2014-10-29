package com.xxf.android.shoppingrecord.activity;

import java.util.ArrayList;

import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.db.OutDB;
import com.xxf.android.shoppingrecord.model.Category;
import com.xxf.android.shoppingrecord.model.Name;
import com.xxf.android.shoppingrecord.dialog.ChoiceCategoryDialog;
import com.xxf.android.shoppingrecord.dialog.ChoiceDateDialog;
import com.xxf.android.shoppingrecord.dialog.ChoiceNameDialog;
import com.xxf.android.shoppingrecord.dialog.ChoiceCategoryDialog.ChoiceCategoryListener;
import com.xxf.android.shoppingrecord.dialog.ChoiceDateDialog.choiceDateListenerInterface;
import com.xxf.android.shoppingrecord.dialog.ChoiceNameDialog.ChoiceNameListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PlanShopActivity extends Activity implements ChoiceCategoryListener, ChoiceNameListener, choiceDateListenerInterface {

    private boolean mHasChoiceCategory = false;
    private boolean mHasChoiceName = false;
    private long mOwn = -1;
    private long mTime = System.currentTimeMillis();
    private LinearLayout mCategoryView;
    private LinearLayout mNameView;
    private LinearLayout mDateView;
    private TextView mCategoryTextView;
    private TextView mNameTextView;
    private TextView mDateTextView;
    private Button mConfirmButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.plan_shop_activity);

        init();
    }

    private void init() {
        mCategoryTextView = (TextView) findViewById(R.id.choice_category);
        mNameTextView = (TextView) findViewById(R.id.choice_name);
        mDateTextView = (TextView) findViewById(R.id.choice_date);
        mConfirmButton = (Button) findViewById(R.id.corfirm);
        mCancelButton = (Button) findViewById(R.id.cancel);
        mCategoryView = (LinearLayout) findViewById(R.id.choice_category_view);
        mNameView = (LinearLayout) findViewById(R.id.choice_name_view);
        mDateView = (LinearLayout) findViewById(R.id.choice_date_view);

        mTime = System.currentTimeMillis();
        mDateTextView.setText(ShoppingRecord.longToStringForTime(this, mTime));

        mCategoryView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                OutDB db = new OutDB(PlanShopActivity.this);
                ArrayList<Category> categoryList = db.getShopCategory();

                ChoiceCategoryDialog categoryDialog = new ChoiceCategoryDialog(PlanShopActivity.this, R.string.add_category, mCategoryTextView.getText().toString(), categoryList, PlanShopActivity.this, true);
                categoryDialog.show();
            }
        });

        mNameView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (-1 != mOwn) {
                    OutDB db = new OutDB(PlanShopActivity.this);
                    ArrayList<Name> nameList = db.getShopName(mOwn);

                    ChoiceNameDialog nameDialog = new ChoiceNameDialog(R.string.shop_choice, R.string.add_shop, PlanShopActivity.this, mNameTextView.getText().toString(), mOwn, nameList, PlanShopActivity.this, true);
                    nameDialog.show();
                }
                else {
                    Toast.makeText(PlanShopActivity.this, PlanShopActivity.this.getString(R.string.info_choice_category_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDateView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ChoiceDateDialog dialog = new ChoiceDateDialog(PlanShopActivity.this, mTime, PlanShopActivity.this);
                dialog.show();
            }
        });

        mConfirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String category = mCategoryTextView.getText().toString();
                String name = mNameTextView.getText().toString();
                if (null == category || category.length() == 0 || false == mHasChoiceCategory) {
                    Toast.makeText(PlanShopActivity.this, PlanShopActivity.this.getString(R.string.info_can_not_empty_category), Toast.LENGTH_SHORT).show();
                }
                else if (null == name || name.length() == 0 || false == mHasChoiceName) {
                    Toast.makeText(PlanShopActivity.this, PlanShopActivity.this.getString(R.string.info_can_not_empty_name), Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(PlanShopActivity.this, PlanDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(ShoppingRecord.BUNDLE_SHOP_CATEGORY, category);
                    bundle.putString(ShoppingRecord.BUNDLE_SHOP_NAME, name);
                    bundle.putLong(ShoppingRecord.BUNDLE_RECORD_DATE, mTime);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                OutDB db = new OutDB(PlanShopActivity.this);
                db.delShopByCategoryId(mOwn);
                finish();
            }
        });
    }

    @Override
    public void choiceCategoryListener(String name, long id) {
        mCategoryTextView.setText(name);
        mHasChoiceCategory = true;
        mHasChoiceName = false;
        mNameTextView.setText(getString(R.string.click_choice));
        mOwn = id;
    }

    @Override
    public void insertCategoryListener(String name) {
        mHasChoiceCategory = true;
        mHasChoiceName = false;
        OutDB db = new OutDB(PlanShopActivity.this);
        db.insertShopCategory(name);
        mOwn = db.getCategoryShopIdByName(name);
        mCategoryTextView.setText(name);
        mNameTextView.setText(getString(R.string.click_choice));
    }

    @Override
    public void choiceNameListener(String name) {
        mHasChoiceName = true;
        mNameTextView.setText(name);
    }

    @Override
    public void insertNameListener(String name, long own) {
        mHasChoiceName = true;
        OutDB db = new OutDB(PlanShopActivity.this);
        db.insertShopName(name, own);
        mNameTextView.setText(name);
    }

    @Override
    public void choiceDateListener(long time) {
        mTime = time;
        mDateTextView.setText(ShoppingRecord.longToStringForTime(this, mTime));
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
