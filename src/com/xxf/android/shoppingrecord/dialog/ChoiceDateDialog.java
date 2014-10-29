package com.xxf.android.shoppingrecord.dialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xxf.android.shoppingrecord.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChoiceDateDialog {

    private int mYear;
    private int mMonth;
    private int mDay;
    private long mRetTime;
    private long mTime;
    private Context mContext;
    private EditText mYearEdit;
    private EditText mMonthEdit;
    private EditText mDayEdit;
    private choiceDateListenerInterface mListener;

    public ChoiceDateDialog(Context context, long time, choiceDateListenerInterface listener) {
        mTime = time;
        mRetTime = mTime;
        mContext = context;
        mListener = listener;
    }

    public void show() {
        Date date = new Date(mTime);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String[] array = (simpleDate.format(date)).split("-");
        mYear = Integer.parseInt(array[0]);
        mMonth = Integer.parseInt(array[1]);
        mDay = Integer.parseInt(array[2]);

        View view = LayoutInflater.from(mContext).inflate(R.layout.choice_date_dialog, null);
        mYearEdit = (EditText) view.findViewById(R.id.year_edit);
        mMonthEdit = (EditText) view.findViewById(R.id.month_edit);
        mDayEdit = (EditText) view.findViewById(R.id.day_edit);

        addDateButtons(view);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(R.string.date_choice);
        dialog.setView(view);
        dialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                setYearMonthDay();
                if (isSafeDate(mYear, mMonth, mDay)) {
                    mRetTime = getFormatDate(mYear, mMonth, mDay);
                    mListener.choiceDateListener(mRetTime);
                }
                else {
                    Toast.makeText(mContext, mContext.getString(R.string.info_not_safe_date), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public long getSetTime() {
        return mRetTime;
    }

    private void addDateButtons(final View dateView) {
        Button[] dateButtons = new Button[6];
        dateButtons[0] = (Button) dateView.findViewById(R.id.year_date_add);
        dateButtons[1] = (Button) dateView.findViewById(R.id.year_date_reduce);
        dateButtons[2] = (Button) dateView.findViewById(R.id.month_date_add);
        dateButtons[3] = (Button) dateView.findViewById(R.id.month_date_reduce);
        dateButtons[4] = (Button) dateView.findViewById(R.id.day_date_add);
        dateButtons[5] = (Button) dateView.findViewById(R.id.day_date_reduce);

        refreshEdit(dateView);

        dateButtons[0].setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                setYearMonthDay();
                if (mYear >= 9999) {
                    return;
                }
                mYear++;
                if (mDay > getMaxDay(mYear, mMonth)) {
                    mDay = getMaxDay(mYear, mMonth);
                }
                refreshEdit(dateView);
            }
        });

        dateButtons[1].setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                setYearMonthDay();
                if (mYear <= 1970) {
                    return;
                }
                mYear--;
                if (mDay > getMaxDay(mYear, mMonth)) {
                    mDay = getMaxDay(mYear, mMonth);
                }
                refreshEdit(dateView);
            }
        });

        dateButtons[2].setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                setYearMonthDay();
                if (mMonth >= 12) {
                    return;
                }
                mMonth++;
                if (mDay > getMaxDay(mYear, mMonth)) {
                    mDay = getMaxDay(mYear, mMonth);
                }
                refreshEdit(dateView);
            }
        });

        dateButtons[3].setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                setYearMonthDay();
                if (mMonth <= 1) {
                    return;
                }
                mMonth--;
                if (mDay > getMaxDay(mYear, mMonth)) {
                    mDay = getMaxDay(mYear, mMonth);
                }
                refreshEdit(dateView);
            }
        });

        dateButtons[4].setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                setYearMonthDay();
                if (mDay >= getMaxDay(mYear, mMonth)) {
                    return;
                }
                mDay++;
                refreshEdit(dateView);
            }
        });

        dateButtons[5].setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                setYearMonthDay();
                if (mDay <= 1) {
                    return;
                }
                mDay--;
                refreshEdit(dateView);
            }
        });
    }

    private void refreshEdit(final View view) {
        mYearEdit.setText(mYear + "");
        mMonthEdit.setText(mMonth + "");
        mDayEdit.setText(mDay + "");
    }

    private long getFormatDate(int year, int month, int day) {
        StringBuilder sb = new StringBuilder();
        sb.append(year + "-");
        if (month < 10) {
            sb.append("0" + month + "-");
        }
        else {
            sb.append(month + "-");
        }
        if (day < 10) {
            sb.append("0" + day);
        }
        else {
            sb.append(day + "");
        }

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(sb.toString());
        }
        catch (ParseException e) {
        }
        if (null == date) {
            return 0;
        }
        return date.getTime();
    }

    private int getMaxDay(int year, int month) {
        int[] days = { 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        if ((year % 4 == 0) && ((year % 100 != 0) | (year % 400 == 0))) {
            days[1] = 29;
        }
        else {
            days[1] = 28;
        }
        return days[month - 1];
    }

    public interface choiceDateListenerInterface {
        public void choiceDateListener(long time);
    }

    private boolean isSafeDate(int year, int month, int day) {
        if (year < 1970 || year > 9999) {
            return false;
        }
        if (month < 1 || month > 12) {
            return false;
        }
        if (day < 1 || day > getMaxDay(year, month)) {
            return false;
        }
        return true;
    }

    private void setYearMonthDay() {
        try {
            mYear = Integer.parseInt(mYearEdit.getText().toString());
            mMonth = Integer.parseInt(mMonthEdit.getText().toString());
            mDay = Integer.parseInt(mDayEdit.getText().toString());
        }
        catch (Exception e) {
            mDay = -1;
        }
    }
}
