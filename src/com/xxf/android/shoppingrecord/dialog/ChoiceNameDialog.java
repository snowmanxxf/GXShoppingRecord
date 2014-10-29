package com.xxf.android.shoppingrecord.dialog;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.model.Name;

public class ChoiceNameDialog {

    private boolean mCanAdd;
    private long mOwn;
    private int mTitleList;
    private int mTitleEdit;
    private String mTextContent;
    private Context mContext;
    private ArrayList<Name> mNameList;
    private ChoiceNameListener mListener;

    public ChoiceNameDialog(int listTitle, int editTitle, Context context, String name, long own, ArrayList<Name> list, ChoiceNameListener listener, boolean canAdd) {
        mTitleList = listTitle;
        mTitleEdit = editTitle;
        mOwn = own;
        mTextContent = name;
        mContext = context;
        mNameList = list;
        mListener = listener;
        mCanAdd = canAdd;
    }

    public void show() {
        int length = mNameList.size();
        int checkedIndext = 0;
        int number;
        if (mCanAdd) {
            number = length + 1;
        }
        else {
            number = length;
        }
        final CharSequence[] categoryName = new CharSequence[number];
        if (mCanAdd) {
            categoryName[0] = mContext.getString(mTitleEdit);
        }
        String checkCategory = mTextContent;
        for (int i = 0; i < length; i++) {
            String tmp = mNameList.get(i).name;
            if (checkCategory.equals(tmp)) {
                if (mCanAdd) {
                    checkedIndext = i + 1;
                }
                else {
                    checkedIndext = i;
                }
            }
            if (mCanAdd) {
                categoryName[i + 1] = tmp.subSequence(0, tmp.length());
            }
            else {
                categoryName[i] = tmp.subSequence(0, tmp.length());
            }
        }

        AlertDialog.Builder listDialog = new AlertDialog.Builder(mContext);
        listDialog.setTitle(mTitleList);
        listDialog.setSingleChoiceItems(categoryName, checkedIndext, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int index) {
                dialog.dismiss();
                if (0 == index && mCanAdd) {
                    final EditText input = new EditText(mContext);
                    input.setSingleLine();
                    AlertDialog.Builder editDialog = new AlertDialog.Builder(mContext);
                    editDialog.setTitle(mTitleEdit);
                    editDialog.setView(input);
                    editDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int index) {
                            String tmp = input.getText().toString();
                            if (null != tmp && tmp.length() > 0) {
                                mListener.insertNameListener(input.getText().toString(), mOwn);
                            }

                        }
                    });
                    editDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int index) {
                            dialog.dismiss();
                        }
                    });
                    editDialog.show();
                }
                else {
                    mListener.choiceNameListener(categoryName[index].toString());
                }
            }
        });
        listDialog.show();
    }

    public interface ChoiceNameListener {
        public void choiceNameListener(String name);
        public void insertNameListener(String name, long own);
    }
}
