package com.xxf.android.shoppingrecord.dialog;

import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.adapter.NumberPadAdapter;
import com.xxf.android.shoppingrecord.adapter.NumberPadAdapter.NumberPadListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

public class SetNumberDialog implements NumberPadListener {

    private boolean mIsInit = true;
    private boolean mIsDecimal = false;
    private int mTitleId;
    private int mEndMsg;
    private int mDigit;
    private int mGetNumberInterger = 0;
    private int mGetNumberFloat = 0;
    private int mMaxValue;
    private long mDecimalNumber = 10;
    private TextView mInputText;
    private TextView mShowText;
    private NumberPadAdapter mGridAdapter;
    private GridView mGridView;
    private Context mContext;
    private SetNumberListenerInterface mListener;

    public SetNumberDialog(TextView textView, Context context, int title, int endMsg, int defaultNumer, int digit, int maxValue, SetNumberListenerInterface listener) {
        mContext = context;
        mTitleId = title;
        mGetNumberInterger = defaultNumer/100;
        mGetNumberFloat = defaultNumer%100;
        mDigit = digit;
        mEndMsg = endMsg;
        mShowText = textView;
        mMaxValue = maxValue;
        mListener = listener;
    }

    public void show() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.set_number_dialog, null);

        mGridAdapter = new NumberPadAdapter(mContext, this);
        mGridView = (GridView) view.findViewById(R.id.number_grid);
        mGridView.setAdapter(mGridAdapter);

        mInputText = (TextView) view.findViewById(R.id.input_content);
        refreshInputText();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mTitleId);
        builder.setView(view);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String decimal = getNumberString();
                mShowText.setText(decimal);
                mListener.setNumberListenerInterface();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void refreshInputText() {

        String decimal = getNumberString();
        
        StringBuilder sb = new StringBuilder();
        if (-1 != mEndMsg) {
            if (mDigit > 0) {
                sb.append(decimal + mContext.getString(mEndMsg));
            }
            else {
                sb.append(decimal + mContext.getString(mEndMsg));
            }
        }
        else {
            if (mDigit > 0) {
                sb.append(decimal);
            }
            else {
                sb.append(decimal);
            }
        }
        mInputText.setText(sb.toString());
    }

    @Override
    public void numberPadListener(char button) {
        if(mIsInit){
            mIsDecimal = false;
            mDecimalNumber = 10;
            mGetNumberInterger = 0;
            mGetNumberFloat = 0;
            mIsInit = false;
        }
        if (button >= '0' && button <= '9') {
            if (!mIsDecimal) {
                if(mGetNumberInterger * 10 + button - '0' <= mMaxValue){
                    mGetNumberInterger = mGetNumberInterger * 10  + button - '0';
                }
            }
            else if (mDecimalNumber <= Math.pow(10, mDigit)) {
                mDecimalNumber *= 10;
                mGetNumberFloat = mGetNumberFloat * 10 + button - '0';
            }
        }
        else if (button == '.' && mDigit > 0) {
            mIsDecimal = true;
        }
        else if (button == 'C') {
            mIsDecimal = false;
            mDecimalNumber = 10;
            mGetNumberInterger = 0;
            mGetNumberFloat = 0;
        }

        refreshInputText();
    }
    
    private String getNumberString(){
        String decimal = "";
        if(mGetNumberFloat == 0){
            String tmp = "";
            if(0 != mDigit){
                StringBuilder sbTmp = new StringBuilder();
                sbTmp.append(".");
                for(int i = 0; i < mDigit; i ++){
                    sbTmp.append("0");
                }
                tmp = sbTmp.toString();
            }
            decimal = mGetNumberInterger + tmp;
        }
        else if(mGetNumberFloat < 10){
            if(0 != mDigit){
                if(mIsInit){
                    decimal = mGetNumberInterger + ".0" + mGetNumberFloat;
                }
                else{
                    decimal = mGetNumberInterger + "." + mGetNumberFloat+"0";
                }
            }
            else{
                decimal = mGetNumberInterger + "";
            }
        }
        else{
            if(0 != mDigit){
                decimal = mGetNumberInterger + "." + mGetNumberFloat;
            }
            else{
                decimal = mGetNumberInterger + "";
            }
        }
        return decimal;
    }

    public interface SetNumberListenerInterface{
        public void setNumberListenerInterface();
    }
}
