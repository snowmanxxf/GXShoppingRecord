package com.xxf.android.shoppingrecord.activity;

import java.util.ArrayList;

import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.adapter.ResultAdapter;
import com.xxf.android.shoppingrecord.db.InnerDB;
import com.xxf.android.shoppingrecord.model.ShopDetail;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ResultActivity extends Activity {
    
    private String mCategory;
    private String mName;
    private long mStartDate;
    private long mEndDate;
    private int mSum = 0;
    private int mNumber = 0;
    private ProgressBar mProgressBar;
    private ArrayList<ShopDetail> mDetail = new ArrayList<ShopDetail>();
    private ArrayList<Integer> mSumList = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.result_activity);
        
        init();
    }

    private void init(){
        mCategory = ShoppingRecord.getSearchShopCategory(this);
        mName = ShoppingRecord.getSearchShopName(this);
        mStartDate = ShoppingRecord.getSearchStartTime(this);
        mEndDate = ShoppingRecord.getSearchEndTime(this);

        if(mStartDate == -1){
            mStartDate = Long.MIN_VALUE;
        }
        if(mEndDate == -1){
            mEndDate = Long.MAX_VALUE;
        }
        
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        final TextView notify = (TextView) findViewById(R.id.topnotifybar);
        final TextView noResult = (TextView) findViewById(R.id.show_no_tip);
        
        notify.setVisibility(View.GONE);
        noResult.setVisibility(View.VISIBLE);
        
        Thread thread = new Thread(){

            @Override
            public void run() {
                InnerDB db = new InnerDB(ResultActivity.this);
                ArrayList<ShopDetail> detailList = db.getShopDetailByAll(mCategory, mName, -1, 0, -1);
                for(int i = 0; i < detailList.size(); i ++){
                    ShopDetail detail = detailList.get(i);
                    long date = detail.date;
                    if(date >= mStartDate && date <= mEndDate){
                        mNumber ++;
                        mDetail.add(detail);
                        int tmp = db.getSumGoodsByOwnId(detail.id);
                        mSumList.add(tmp);
                        mSum += tmp;
                    }
                }
                
                ResultActivity.this.runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        if(mNumber > 0){
                            notify.setVisibility(View.VISIBLE);
                            noResult.setVisibility(View.GONE);
                            String tmp;
                            if(mSum % 100 < 10){
                                tmp = mSum/100 + ".0" + mSum%100;
                            }
                            else{
                                tmp = mSum/100 + "." + mSum%100;
                            }
                            notify.setText(String.format(getString(R.string.info_result_statistics), mNumber, tmp));
                            
                            ListView list = (ListView) findViewById(R.id.recent_list);
                            ResultAdapter adapter = new ResultAdapter(ResultActivity.this);
                            adapter.refreshList(mDetail, mSumList);
                            list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        
                        mProgressBar = (ProgressBar) findViewById(R.id.recent_progress);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
            
        };
        thread.start();
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
