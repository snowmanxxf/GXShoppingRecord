package com.xxf.android.shoppingrecord.activity;

import java.util.ArrayList;

import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.adapter.BookScanAdapter;
import com.xxf.android.shoppingrecord.db.PlanDB;
import com.xxf.android.shoppingrecord.model.PlanList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class BookScanActivity extends Activity {

    private boolean mIsInit = true;
    private int mOffset = 0;
    private ListView mListView;
    private BookScanAdapter mBookScanAdapter;
    private TextView mShopNoRecord;
    private ProgressBar mProgressBar;
    private ArrayList<PlanList> mDetailList = new ArrayList<PlanList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.book_scan_activity);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (false == mIsInit) {
            mOffset = 0;
            mDetailList.clear();
        }
        mIsInit = false;
        refresh();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }

    private void init() {
        Button backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mShopNoRecord = (TextView) findViewById(R.id.show_no_tip);

        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        mListView = (ListView) findViewById(R.id.book_history_list);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlanList detail = (PlanList) mBookScanAdapter.getItem(position);
                if (null != detail) {
                    Intent intent = new Intent(BookScanActivity.this, PlanDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(ShoppingRecord.BUNDLE_SHOP_CATEGORY, detail.category);
                    bundle.putString(ShoppingRecord.BUNDLE_SHOP_NAME, detail.name);
                    bundle.putLong(ShoppingRecord.BUNDLE_RECORD_DATE, detail.planDate);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {
                    refresh();
                }
            }
        });

        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(BookScanActivity.this);
                dialog.setTitle(R.string.tip);
                dialog.setMessage(R.string.info_confirm_delete_this_item);
                dialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PlanList detail = (PlanList) mBookScanAdapter.getItem(position);
                        if (null != detail) {
                            PlanDB db = new PlanDB(BookScanActivity.this);
                            db.deleteShopDetailById(detail.id);
                            mDetailList.clear();
                            mOffset = 0;
                            refresh();
                        }
                    }
                });
                dialog.setNegativeButton(R.string.cancel, null);
                dialog.show();
                return false;
            }
        });

        mBookScanAdapter = new BookScanAdapter(this);
        mListView.setAdapter(mBookScanAdapter);
    }

    private void refresh() {


        Thread thread = new Thread() {

            @Override
            public void run() {
                
                BookScanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                });
                
                PlanDB db = new PlanDB(BookScanActivity.this);
                final ArrayList<PlanList> planList = db.getShopDetailByAll("", "", -1, mOffset, 11);
                
                int sum = planList.size();
                if (planList.size() == 11) {
                    sum = planList.size() - 1;
                }
                for (int i = 0; i < sum; i++) {
                    mDetailList.add(planList.get(i));
                }

                BookScanActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mBookScanAdapter.refreshItem(mDetailList);
                        mOffset += (planList.size() - 1);
                        if (mOffset == -1) {
                            mShopNoRecord.setVisibility(View.VISIBLE);
                        }
                        else {
                            mShopNoRecord.setVisibility(View.GONE);
                        }
                        if (planList.size() == 11) {
                            mBookScanAdapter.setEnd(false);
                        }
                        else {
                            mBookScanAdapter.setEnd(true);
                        }
                        mProgressBar.setVisibility(View.GONE);
                        mBookScanAdapter.notifyDataSetChanged();
                    }
                });
            }

        };
        thread.start();
    }
}
