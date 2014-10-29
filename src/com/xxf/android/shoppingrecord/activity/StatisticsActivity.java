package com.xxf.android.shoppingrecord.activity;

import java.util.ArrayList;

import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.db.OutDB;
import com.xxf.android.shoppingrecord.dialog.ChoiceCategoryDialog;
import com.xxf.android.shoppingrecord.dialog.ChoiceDateDialog;
import com.xxf.android.shoppingrecord.dialog.ChoiceNameDialog;
import com.xxf.android.shoppingrecord.dialog.ChoiceCategoryDialog.ChoiceCategoryListener;
import com.xxf.android.shoppingrecord.dialog.ChoiceDateDialog.choiceDateListenerInterface;
import com.xxf.android.shoppingrecord.dialog.ChoiceNameDialog.ChoiceNameListener;
import com.xxf.android.shoppingrecord.model.Category;
import com.xxf.android.shoppingrecord.model.Name;

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

public class StatisticsActivity extends Activity implements ChoiceCategoryListener, ChoiceNameListener{

    private boolean mHasChoiceCategory = false;
    private boolean mHasChoiceName = false;
    private long mOwn = -1;
    private long mStartTime = -1;
    private long mEndTime = -1;
    private LinearLayout mCategoryView;
    private LinearLayout mNameView;
    private LinearLayout mStartDateView;
    private LinearLayout mEndDateView;
    private LinearLayout mResetAll;
    private TextView mCategoryTextView;
    private TextView mNameTextView;
    private TextView mStartDateTextView;
    private TextView mEndDateTextView;
    private Button mConfirmButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.statistics_activity);

        init();
    }

    private void init() {
        mCategoryTextView = (TextView) findViewById(R.id.choice_category);
        mNameTextView = (TextView) findViewById(R.id.choice_name);
        mStartDateTextView = (TextView) findViewById(R.id.start_date);
        mEndDateTextView = (TextView) findViewById(R.id.end_date);
        mConfirmButton = (Button) findViewById(R.id.corfirm);
        mCancelButton = (Button) findViewById(R.id.cancel);
        mCategoryView = (LinearLayout) findViewById(R.id.choice_category_view);
        mNameView = (LinearLayout) findViewById(R.id.choice_name_view);
        mStartDateView = (LinearLayout) findViewById(R.id.start_date_view);
        mEndDateView = (LinearLayout) findViewById(R.id.end_date_view);
        mResetAll = (LinearLayout) findViewById(R.id.reset_all_condition);

        mCategoryView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                OutDB db = new OutDB(StatisticsActivity.this);
                ArrayList<Category> categoryList = db.getShopCategory();

                ChoiceCategoryDialog categoryDialog = new ChoiceCategoryDialog(StatisticsActivity.this, R.string.input_category, mCategoryTextView.getText().toString(), categoryList, StatisticsActivity.this, true);
                categoryDialog.show();
            }
        });

        mNameView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (-1 != mOwn) {
                    OutDB db = new OutDB(StatisticsActivity.this);
                    ArrayList<Name> nameList = db.getShopName(mOwn);

                    ChoiceNameDialog nameDialog = new ChoiceNameDialog(R.string.shop_choice, R.string.input_name, StatisticsActivity.this, mNameTextView.getText().toString(), mOwn, nameList,
                            StatisticsActivity.this, true);
                    nameDialog.show();
                }
                else {
                    Toast.makeText(StatisticsActivity.this, StatisticsActivity.this.getString(R.string.info_choice_category_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mStartDateView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ChoiceDateDialog dialog = new ChoiceDateDialog(StatisticsActivity.this, System.currentTimeMillis(), new StartDateListener());
                dialog.show();

            }
        });
        
        mEndDateView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ChoiceDateDialog dialog = new ChoiceDateDialog(StatisticsActivity.this, System.currentTimeMillis(), new EndDateListener());
                dialog.show();

            }
        });

        mResetAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mStartTime = -1;
                mEndTime = -1;
                mHasChoiceCategory = false;
                mHasChoiceName = false;
                mCategoryTextView.setText(StatisticsActivity.this.getString(R.string.unlimit));
                mNameTextView.setText(StatisticsActivity.this.getString(R.string.unlimit));
                mStartDateTextView.setText(StatisticsActivity.this.getString(R.string.unlimit));
                mEndDateTextView.setText(StatisticsActivity.this.getString(R.string.unlimit));
            }
        });

        mConfirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String tmpCategory = mCategoryTextView.getText().toString();
                if (false == mHasChoiceCategory) {
                    tmpCategory = "";
                }
                String tmpName = mNameTextView.getText().toString();
                if (false == mHasChoiceName) {
                    tmpName = "";
                }
                ShoppingRecord.setSearchStatisticsSP(StatisticsActivity.this, tmpCategory, tmpName, mStartTime, mEndTime);
                startActivity(new Intent(StatisticsActivity.this, ResultActivity.class));
            }
        });

        mCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    public void choiceCategoryListener(String name, long id) {
        mCategoryTextView.setText(name);
        mHasChoiceCategory = true;
        mHasChoiceName = false;
        mNameTextView.setText(getString(R.string.unlimit));
        mOwn = id;
    }

    @Override
    public void insertCategoryListener(String name) {
        mHasChoiceCategory = true;
        mHasChoiceName = false;
        OutDB db = new OutDB(StatisticsActivity.this);
        db.insertShopCategory(name);
        mCategoryTextView.setText(name);
        mOwn = db.getCategoryShopIdByName(name);
        mNameTextView.setText(getString(R.string.unlimit));
    }

    @Override
    public void choiceNameListener(String name) {
        mNameTextView.setText(name);
        mHasChoiceName = true;
    }

    @Override
    public void insertNameListener(String name, long own) {
        mHasChoiceName = true;
        OutDB db = new OutDB(StatisticsActivity.this);
        db.insertShopName(name, own);
        mNameTextView.setText(name);
    }
    
    private class StartDateListener implements choiceDateListenerInterface{

        @Override
        public void choiceDateListener(long time) {
            mStartTime = time;
            mStartDateTextView.setText(ShoppingRecord.longToStringForTime(StatisticsActivity.this, time));
        }
        
    }
    
    private class EndDateListener implements choiceDateListenerInterface{

        @Override
        public void choiceDateListener(long time) {
            mEndTime = time;
            mEndDateTextView.setText(ShoppingRecord.longToStringForTime(StatisticsActivity.this, time));
        }
        
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
