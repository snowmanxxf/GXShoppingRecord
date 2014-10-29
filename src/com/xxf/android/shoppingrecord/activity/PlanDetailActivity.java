package com.xxf.android.shoppingrecord.activity;

import java.util.ArrayList;

import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.adapter.PlanDetailAdapter;
import com.xxf.android.shoppingrecord.db.PlanDB;
import com.xxf.android.shoppingrecord.model.PlanGoods;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class PlanDetailActivity extends Activity {

    private boolean mIsInitLoad = true;
    private long mShopId;
    private long mRecordDate;
    private String mShopCategory;
    private String mShopName;
    private Button mBackButton;
    private Button mAddGoodButton;
    private ImageButton mPreDate;
    private ImageButton mNextDate;
    private TextView mDateShow;
    private TextView mTitle;
    private TextView mShowTip;
    private TextView mNotifyBar;
    private ListView mListView;
    private PlanDetailAdapter mDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.plan_detail_activity);
        
        init();
        setInitValue();
    }

    private void init() {
        mBackButton = (Button) findViewById(R.id.back);
        mDateShow = (TextView) findViewById(R.id.date);
        mPreDate = (ImageButton) findViewById(R.id.pre_date);
        mNextDate = (ImageButton) findViewById(R.id.next_date);
        mAddGoodButton = (Button) findViewById(R.id.add_goods);
        mTitle = (TextView) findViewById(R.id.title);
        mShowTip = (TextView) findViewById(R.id.show_no_tip);
        mListView = (ListView) findViewById(R.id.detail_list);
        mNotifyBar = (TextView) findViewById(R.id.topnotifybar);

        mDetailAdapter = new PlanDetailAdapter(this, mListView);
        mListView.setAdapter(mDetailAdapter);

        mBackButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ShoppingRecord.setSearchShopSP(PlanDetailActivity.this, "", "", -1);
                finish();
            }
        });

        mPreDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mRecordDate -= ShoppingRecord.A_DAY_TO_MSECOND;
                mDateShow.setText(ShoppingRecord.longToStringForTime(PlanDetailActivity.this, mRecordDate));
                PlanDB db = new PlanDB(PlanDetailActivity.this);
                db.updateShopDetailTime(mShopId, mRecordDate);
            }
        });

        mNextDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mRecordDate += ShoppingRecord.A_DAY_TO_MSECOND;
                mDateShow.setText(ShoppingRecord.longToStringForTime(PlanDetailActivity.this, mRecordDate));
                PlanDB db = new PlanDB(PlanDetailActivity.this);
                db.updateShopDetailTime(mShopId, mRecordDate);
            }
        });

        mAddGoodButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(PlanDetailActivity.this, PlanGoodsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong(ShoppingRecord.BUNDLE_SHOP_ID, mShopId);
                bundle.putLong(ShoppingRecord.BUNDLE_GOODS_ID, -1);
                intent.putExtras(bundle);
                PlanDetailActivity.this.startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Intent intent = new Intent(PlanDetailActivity.this, PlanGoodsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong(ShoppingRecord.BUNDLE_SHOP_ID, mShopId);
                bundle.putLong(ShoppingRecord.BUNDLE_GOODS_ID, mDetailAdapter.getItemId(position));
                intent.putExtras(bundle);
                PlanDetailActivity.this.startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(PlanDetailActivity.this);
                dialog.setTitle(R.string.tip);
                dialog.setMessage(R.string.info_confirm_delete_this_item);
                dialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PlanDB db = new PlanDB(PlanDetailActivity.this);
                        db.deleteGoodsDetailById(mDetailAdapter.getItemId(position));
                        db.setGoodsStatusById(((PlanGoods) mDetailAdapter.getItem(position)).detailId, -1, 1);
                        refreshList();
                    }
                });
                dialog.setNegativeButton(R.string.cancel, null);
                dialog.show();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (false == mIsInitLoad) {
            refreshList();
        }
        mIsInitLoad = false;
    }

    private void setInitValue() {
        Bundle bundle = getIntent().getExtras();
        mShopCategory = bundle.getString(ShoppingRecord.BUNDLE_SHOP_CATEGORY);
        mShopName = bundle.getString(ShoppingRecord.BUNDLE_SHOP_NAME);
        mRecordDate = bundle.getLong(ShoppingRecord.BUNDLE_RECORD_DATE);
        mDetailAdapter.setShopInfo(mShopCategory, mShopName, mRecordDate);
        if (null == mShopCategory) {
            mShopCategory = "";
        }
        if (null == mShopName) {
            mShopName = "";
        }
        mTitle.setText(mShopName);

        PlanDB db = new PlanDB(this);
        mShopId = db.getShopDetailIdByShopAndDate(mShopCategory, mShopName, mRecordDate);
        if (-1 == mShopId) {
            db.insertShopDetail(mShopCategory, mShopName, mRecordDate);
            mShopId = db.getShopDetailIdByShopAndDate(mShopCategory, mShopName, mRecordDate);
        }

        mDateShow.setText(ShoppingRecord.longToStringForTime(this, mRecordDate));

        refreshList();
    }

    private void refreshList() {
        if (-1 == mShopId) {
            return;
        }
        PlanDB db = new PlanDB(this);
        ArrayList<PlanGoods> list = db.getGoodsDetailByShopId(mShopId);
        mDetailAdapter.refresh(list);
        mDetailAdapter.notifyDataSetChanged();

        if (list.size() == 0) {
            mShowTip.setVisibility(View.VISIBLE);
            mNotifyBar.setVisibility(View.GONE);
        }
        else {
            mShowTip.setVisibility(View.GONE);
            mNotifyBar.setVisibility(View.VISIBLE);
            mNotifyBar.setText(getNotifyBarInfo());
        }

    }

    private String getNotifyBarInfo() {
        String tmp = null;
        PlanDB db = new PlanDB(this);
        ArrayList<PlanGoods> list = db.getGoodsDetailByShopId(mShopId);
        tmp = String.format(getString(R.string.info_plan_goods_detail_notify_bar), list.size() + "");
        return tmp;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
