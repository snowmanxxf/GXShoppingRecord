package com.xxf.android.shoppingrecord.activity;

import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_activity);
        
        init();
    }

    private void init(){
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        TextView about = (TextView) findViewById(R.id.about_text);
        try {
            about.setText(getString(R.string.app_name)+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        }
        catch (Exception e) {
            
        }
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
