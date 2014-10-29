package com.xxf.android.shoppingrecord.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LinearLayoutForListView extends LinearLayout {
    
    private final static boolean DEBUG = false;
    private final static String TAG = "LinearLayoutForListView";
    
    private BaseAdapter mAdapter;
    private Context mContext;

    public LinearLayoutForListView(Context context) {
        super(context);
        mContext = context;
    }

    public LinearLayoutForListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public BaseAdapter getAdpater() {
        return mAdapter;
    }

    public void setAdapter(BaseAdapter adpater) {
        if(mAdapter != null){
            removeAllViews();
        }
        mAdapter = adpater;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        fillLinearLayout();
    }

    public void fillLinearLayout() {
        int count = mAdapter.getCount();
        if(DEBUG){
            Log.d(TAG, "adapter count=" + count);
        }
        for (int i = 0; i < count; i++) {
            View v = mAdapter.getView(i, null, null);
            addView(v, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            ImageView image = new ImageView(mContext);
            image.setBackgroundColor(Color.GRAY);
            addView(image, new LayoutParams(LayoutParams.FILL_PARENT, 2));
        }
    }
}
