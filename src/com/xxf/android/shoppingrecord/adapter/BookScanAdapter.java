package com.xxf.android.shoppingrecord.adapter;

import java.util.ArrayList;

import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.model.PlanList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BookScanAdapter extends BaseAdapter {
    
    private boolean mIsEnd = true;
    private int mCount;
    private Context mContext;
    private ArrayList<PlanList> mList = new ArrayList<PlanList>();

    public BookScanAdapter(Context context){
        mContext = context;
    }
    
    @Override
    public int getCount() {
        if(!mIsEnd){
            return mCount + 1;
        }
        return mCount;
    }

    @Override
    public Object getItem(int position) {
        if(position < mCount){
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if(position < mCount){
            return mList.get(position).id;
        }
        return -1;
    }

    @Override
    public View getView(int position, View conView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.book_scan_row, null);
        
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.list_row);
        TextView loadMore = (TextView) view.findViewById(R.id.load_more_text);
        
        if(position < mCount){
            layout.setVisibility(View.VISIBLE);
            loadMore.setVisibility(View.GONE);
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(mList.get(position).name);
            TextView cate = (TextView) view.findViewById(R.id.cate);
            cate.setText("--" + mList.get(position).category);
            TextView time = (TextView) view.findViewById(R.id.time);
            time.setText(mContext.getString(R.string.plan_shop_time) + ShoppingRecord.longToStringForTime(mContext, (mList.get(position).planDate)));
            TextView status = (TextView) view.findViewById(R.id.shop_status);
            if(mList.get(position).flag == 0){
                status.setTextColor(Color.RED);
                status.setText("("+mContext.getString(R.string.not_finish)+")");
            }
            else{
                status.setTextColor(Color.GRAY);
                status.setText("("+mContext.getString(R.string.has_finish)+")");
            }
        }
        else{
            layout.setVisibility(View.GONE);
            loadMore.setVisibility(View.VISIBLE);
        }
        
        return view;
    }

    public void refreshItem(ArrayList<PlanList> list){
        mCount = list.size();
        mList.clear();
        for(int i = 0; i < mCount; i ++){
            mList.add(list.get(i));
        }
    }
    
    public void setEnd(boolean isEnd){
        mIsEnd = isEnd;
    }
}
