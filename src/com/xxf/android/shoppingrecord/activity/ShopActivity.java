package com.xxf.android.shoppingrecord.activity;

import java.util.ArrayList;

import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.adapter.ShopAdapter;
import com.xxf.android.shoppingrecord.db.InnerDB;
import com.xxf.android.shoppingrecord.model.ShopDetail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ShopActivity extends Activity {

    private boolean mIsInit = true;
    private int mOffset = 0;
    private long mRecordDate;
    private String mShopCategory;
    private String mShopName;
    private Button mSearchButton;
    private Button mBack;
    private ListView mList;
    private ShopAdapter mShopAdapter;
    private TextView mShopNoRecord;
    private TextView mNotifyBar;
    private ProgressBar mProgressBar;
    private ArrayList<ShopDetail> mDetailList = new ArrayList<ShopDetail>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shop_activity);
        
        init();
        initValue();
    }

    private void init() {
        mBack = (Button) findViewById(R.id.back);
        mSearchButton = (Button) findViewById(R.id.search);
        mList = (ListView) findViewById(R.id.recent_list);
        mShopAdapter = new ShopAdapter(this);
        mShopNoRecord = (TextView) findViewById(R.id.show_no_tip);
        mNotifyBar = (TextView) findViewById(R.id.topnotifybar);
        
        mBack.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopActivity.this, SearchActivity.class));
                // finish();
            }
        });
        
        mProgressBar = (ProgressBar) findViewById(R.id.recent_progress);

        mList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopDetail detail = (ShopDetail) mShopAdapter.getItem(position);
                if (null != detail) {
                    Intent intent = new Intent(ShopActivity.this, DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(ShoppingRecord.BUNDLE_SHOP_CATEGORY, detail.category);
                    bundle.putString(ShoppingRecord.BUNDLE_SHOP_NAME, detail.name);
                    bundle.putLong(ShoppingRecord.BUNDLE_RECORD_DATE, detail.date);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {
                    refreshList();
                }
            }
        });

        mList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ShopActivity.this);
                dialog.setTitle(R.string.tip);
                dialog.setMessage(R.string.info_confirm_delete_this_item);
                dialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShopDetail detail = (ShopDetail) mShopAdapter.getItem(position);
                        if (null != detail) {
                            InnerDB db = new InnerDB(ShopActivity.this);
                            db.deleteShopDetailById(detail.id);
                            mDetailList.clear();
                            mOffset = 0;
                            refreshList();
                        }
                    }
                });
                dialog.setNegativeButton(R.string.cancel, null);
                dialog.show();
                return false;
            }
        });

        mList.setAdapter(mShopAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (false == mIsInit) {
            mOffset = 0;
            mDetailList.clear();
            initValue();
        }

        mIsInit = false;
    }

    private void initValue() {
        mShopCategory = ShoppingRecord.getSearchShopCategory(this);
        mShopName = ShoppingRecord.getSearchShopName(this);
        mRecordDate = ShoppingRecord.getSearchShopTime(this);
        refreshList();
    }

    private void refreshList() {
        
        Thread thread = new Thread(){

            @Override
            public void run() {
                
                ShopActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                });

                InnerDB db = new InnerDB(ShopActivity.this);
                final ArrayList<ShopDetail> detail = db.getShopDetailByAll(mShopCategory, mShopName, mRecordDate, mOffset, 11);
                mOffset += (detail.size() - 1);
                
                int sum = detail.size();
                if (detail.size() == 11) {
                    sum = detail.size() - 1;
                }
                for (int i = 0; i < sum; i++) {
                    mDetailList.add(detail.get(i));
                }

                ShopActivity.this.runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        mShopAdapter.refreshList(mDetailList);
                        boolean isUnCondition = true;
                        StringBuilder sb = new StringBuilder();
                        if (null != mShopCategory && mShopCategory.length() > 0) {
                            isUnCondition = false;
                            sb.append(mShopCategory + "  ");
                        }
                        if (null != mShopName && mShopName.length() > 0) {
                            isUnCondition = false;
                            sb.append(mShopName + "  ");
                        }
                        if (-1 != mRecordDate) {
                            isUnCondition = false;
                            sb.append(ShoppingRecord.longToStringForTime(ShopActivity.this, mRecordDate));
                        }
                        if (isUnCondition) {
                            mNotifyBar.setText(getString(R.string.no_search_condition));
                        }
                        else {
                            mNotifyBar.setText(getString(R.string.info_fiter_condition) + sb.toString());
                        }

                        if (mOffset == -1) {
                            mShopNoRecord.setVisibility(View.VISIBLE);
                            mNotifyBar.setVisibility(View.GONE);
                        }
                        else {
                            mShopNoRecord.setVisibility(View.GONE);
                            mNotifyBar.setVisibility(View.VISIBLE);
                        }

                        if (detail.size() == 11) {
                            mShopAdapter.setEnd(false);
                        }
                        else {
                            mShopAdapter.setEnd(true);
                        }
                        mProgressBar.setVisibility(View.GONE);
                        mShopAdapter.notifyDataSetChanged();
                    }
                });
            }
            
        };
        thread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}