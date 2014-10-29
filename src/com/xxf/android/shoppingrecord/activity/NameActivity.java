package com.xxf.android.shoppingrecord.activity;

import java.util.ArrayList;

import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;
import com.xxf.android.shoppingrecord.adapter.SettingAdapter;
import com.xxf.android.shoppingrecord.db.OutDB;
import com.xxf.android.shoppingrecord.model.Category;
import com.xxf.android.shoppingrecord.model.Name;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class NameActivity extends Activity {
    
    private ListView mNameList;
    private SettingAdapter mNameAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.name_activity);
        
        init();
    }

    private void init(){
        Bundle bundle = getIntent().getExtras();
        int flag = bundle.getInt(ShoppingRecord.BUNDLE_FLAG);
        long id = bundle.getLong(ShoppingRecord.BUNDLE_ID);
        String name = bundle.getString(ShoppingRecord.BUNDLE_NAME);
        mNameList = (ListView) findViewById(R.id.name_list);
        mNameAdapter = new SettingAdapter(this, name, flag, id);
        ArrayList<Name> nameList = new ArrayList<Name>();
        ArrayList<Category> categoryList;
        OutDB db = new OutDB(this);
        if(3 == flag){
            nameList = db.getShopName(id);
        }
        if(4 == flag){
            nameList = db.getGoodsName(id);
        }
        categoryList = db.nameToCategory(nameList);
        mNameList.setAdapter(mNameAdapter);
        mNameAdapter.refresh(categoryList);
        mNameAdapter.notifyDataSetChanged();
        
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
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
