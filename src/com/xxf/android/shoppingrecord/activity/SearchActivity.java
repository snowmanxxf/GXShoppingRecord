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
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity implements ChoiceCategoryListener, ChoiceNameListener, choiceDateListenerInterface{

    private boolean mHasChoiceCategory = false;
    private boolean mHasChoiceName = false;
    private long mOwn = -1;
    private long mTime = -1;
    private LinearLayout mCategoryView;
    private LinearLayout mNameView;
    private LinearLayout mDateView;
    private LinearLayout mResetAll;
    private TextView mCategoryTextView;
    private TextView mNameTextView;
    private TextView mDateTextView;
    private Button mConfirmButton;
    private Button mCancelButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_activity);
        
        init();
    }
    
    private void init(){
        mCategoryTextView = (TextView) findViewById(R.id.choice_category);
        mNameTextView = (TextView) findViewById(R.id.choice_name);
        mDateTextView = (TextView) findViewById(R.id.choice_date);
        mConfirmButton = (Button) findViewById(R.id.corfirm);
        mCancelButton = (Button) findViewById(R.id.cancel);
        mCategoryView = (LinearLayout) findViewById(R.id.choice_category_view);
        mNameView = (LinearLayout) findViewById(R.id.choice_name_view);
        mDateView = (LinearLayout) findViewById(R.id.choice_date_view);
        mResetAll = (LinearLayout) findViewById(R.id.reset_all_condition);
        
        mCategoryView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                OutDB db = new OutDB(SearchActivity.this);
                ArrayList<Category> categoryList = db.getShopCategory();
                
                if(categoryList.size() > 0){
                    ChoiceCategoryDialog categoryDialog = new ChoiceCategoryDialog(SearchActivity.this, R.string.add_category, mCategoryTextView.getText().toString(), categoryList, SearchActivity.this, false);
                    categoryDialog.show();
                }
                else{
                    Toast.makeText(SearchActivity.this, SearchActivity.this.getString(R.string.info_no_category_for_choice), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        mNameView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                if(-1 != mOwn){
                    OutDB db = new OutDB(SearchActivity.this);
                    ArrayList<Name> nameList = db.getShopName(mOwn);
                    
                    ChoiceNameDialog nameDialog = new ChoiceNameDialog(
                            R.string.shop_choice, R.string.add_shop, 
                            SearchActivity.this, mNameTextView.getText().toString(), mOwn, nameList, SearchActivity.this, false);
                    nameDialog.show();
                }
                else{
                    Toast.makeText(SearchActivity.this, SearchActivity.this.getString(R.string.info_choice_category_first), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        mDateView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                ChoiceDateDialog dialog = new ChoiceDateDialog(SearchActivity.this, System.currentTimeMillis(), SearchActivity.this);
                dialog.show();
                
            }
        });
        
        mResetAll.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                mTime = -1;
                mHasChoiceCategory = false;
                mHasChoiceName = false;
                mCategoryTextView.setText(SearchActivity.this.getString(R.string.unlimit));
                mNameTextView.setText(SearchActivity.this.getString(R.string.unlimit));
                mDateTextView.setText(SearchActivity.this.getString(R.string.unlimit));
            }
        });
        
        mConfirmButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                String tmpCategory = mCategoryTextView.getText().toString();
                if(false == mHasChoiceCategory){
                    tmpCategory = "";
                }
                String tmpName = mNameTextView.getText().toString();
                if(false == mHasChoiceName){
                    tmpName = "";
                }
                ShoppingRecord.setSearchShopSP(SearchActivity.this, tmpCategory, tmpName, mTime);
                finish();
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
        
    }

    @Override
    public void choiceNameListener(String name) {
        mNameTextView.setText(name);
        mHasChoiceName = true;
    }

    @Override
    public void insertNameListener(String name, long own) {
       
    }

    @Override
    public void choiceDateListener(long time) {
        mTime = time;
        mDateTextView.setText(ShoppingRecord.longToStringForTime(this, time));
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
