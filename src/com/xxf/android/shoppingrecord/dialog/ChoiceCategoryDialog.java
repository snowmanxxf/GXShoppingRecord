package com.xxf.android.shoppingrecord.dialog;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.model.Category;

public class ChoiceCategoryDialog {

    private boolean mCanAdd;
    private int mFirstString;
    private String mTextContent;
    private Context mContext;
    private ArrayList<Category> mCategoryList;
    private ChoiceCategoryListener mListener;

    public ChoiceCategoryDialog(Context context, int firstString, String name,
            ArrayList<Category> list, ChoiceCategoryListener listener, boolean canAdd) {
        mTextContent = name;
        mContext = context;
        mCategoryList = list;
        mListener = listener;
        mCanAdd = canAdd;
        mFirstString = firstString;
    }

    public void show() {
        int length = mCategoryList.size();
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
            categoryName[0] = mContext.getString(mFirstString);
        }
        String checkCategory = mTextContent;
        for (int i = 0; i < length; i++) {
            String tmp = mCategoryList.get(i).name;
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
        listDialog.setTitle(R.string.category_choice);
        listDialog.setSingleChoiceItems(categoryName, checkedIndext,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        dialog.dismiss();
                        if (0 == index && mCanAdd) {
                            final EditText input = new EditText(mContext);
                            input.setSingleLine();
                            AlertDialog.Builder editDialog = new AlertDialog.Builder(mContext);
                            editDialog.setTitle(R.string.add_category);
                            editDialog.setView(input);
                            editDialog.setPositiveButton(R.string.confirm,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int index) {
                                            String tmp = input.getText().toString();
                                            if (null != tmp && tmp.length() > 0) {
                                                mListener.insertCategoryListener(tmp);
                                            }
                                        }
                                    });
                            editDialog.setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int index) {
                                            dialog.dismiss();
                                        }
                                    });
                            editDialog.show();
                        }
                        else {
                            if (mCanAdd) {
                                mListener.choiceCategoryListener(categoryName[index].toString(),
                                        mCategoryList.get(index - 1).id);
                            }
                            else {
                                mListener.choiceCategoryListener(categoryName[index].toString(),
                                        mCategoryList.get(index).id);
                            }
                        }
                    }
                });
        listDialog.show();
    }

    public interface ChoiceCategoryListener {
        public void choiceCategoryListener(String name, long id);
        public void insertCategoryListener(String name);
    }
}
