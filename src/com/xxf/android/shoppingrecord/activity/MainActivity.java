package com.xxf.android.shoppingrecord.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.feedback.NotificationType;
import com.feedback.UMFeedbackService;
import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity);
        
        MobclickAgent.update(this);
        MobclickAgent.setUpdateOnlyWifi(false);
        UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);
        
        init();
    }

    private void init() {
        GridView gridview = (GridView) findViewById(R.id.gridview);
        ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
        String[] menusString = getResources().getStringArray(R.array.main_activity_rows);
        int[] meunIcon = new int[] { R.drawable.add_plan, R.drawable.plan_icon, R.drawable.shopping_history, R.drawable.add_shopping, R.drawable.jisuan, R.drawable.icon, R.drawable.leibie, R.drawable.check_update, R.drawable.exit_icon };

        for (int i = 0; i < menusString.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", meunIcon[i]);
            map.put("ItemText", menusString[i]);
            meumList.add(map);
        }
        SimpleAdapter saMenuItem = new SimpleAdapter(this, meumList, // 数据源
                R.layout.main_row, // xml实现
                new String[] { "ItemImage", "ItemText" }, // 对应map的Key
                new int[] { R.id.menu_image, R.id.menu_text }); // 对应R的Id
        gridview.setAdapter(saMenuItem);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                case 0:
                    startActivity(new Intent(MainActivity.this, PlanShopActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(MainActivity.this, BookScanActivity.class));
                    break;
                case 2:
                    ShoppingRecord.setSearchShopSP(MainActivity.this, "", "", -1);
                    startActivity(new Intent(MainActivity.this, ShopActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(MainActivity.this, NewShopActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
                    break;
                case 5:
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    break;
                case 6:
                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
                    break;
                case 7:
                    UMFeedbackService.openUmengFeedbackSDK(MainActivity.this);
                    break;
                case 8:
                    exitShoppingRecord();
                    break;
                default:
                    break;
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exitShoppingRecord();
        }
        return false;
    }

    private void exitShoppingRecord() {
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        builder.setMessage(R.string.info_confirm_to_exit);
        builder.setTitle(R.string.tip);
        builder.setPositiveButton(R.string.confirm, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }

        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
