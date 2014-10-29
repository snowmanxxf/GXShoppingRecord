package com.xxf.android.shoppingrecord.activity;

import com.admogo.AdMogoManager;
import com.mobclick.android.MobclickAgent;
import com.xxf.android.shoppingrecord.R;
import com.xxf.android.shoppingrecord.ShoppingRecord;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

    private boolean mActive = true;
    private int mSplashTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        MobclickAgent.onError(this);
        
        ShoppingRecord.setSearchShopSP(this, "", "", -1);
        goMainActivity();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && event.getRepeatCount() == 0) {
            mSplashTime = 0;
            return true;
        }
        return false;
    }

    private void goMainActivity() {
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (mActive && (waited < mSplashTime)) {
                        sleep(100);
                        if (mActive) {
                            waited += 100;
                        }
                    }
                }
                catch (Exception e) {

                }
                finally {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        splashTread.start();
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
        AdMogoManager.clear();
        super.onDestroy();
    }
}
