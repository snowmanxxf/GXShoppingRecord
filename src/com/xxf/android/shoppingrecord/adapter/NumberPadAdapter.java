package com.xxf.android.shoppingrecord.adapter;

import com.xxf.android.shoppingrecord.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

public class NumberPadAdapter extends BaseAdapter {
    
    private NumberPadListener mListener;
    private char[] mNumbers = {'7', '8', '9', '4', '5', '6', '1', '2', '3', '0', '.', 'C'};
    private Context mContext;
    
    public NumberPadAdapter(Context context, NumberPadListener listener){
        mListener = listener;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mNumbers.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.number_pad_number, null);
        Button button = (Button)(view.findViewById(R.id.number_button));
        button.setText(mNumbers[position]+"");
        button.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                mListener.numberPadListener(mNumbers[position]);
            }
        });
        return view;
    }
    
    public interface NumberPadListener{
        public void numberPadListener(char button);
    }
}
