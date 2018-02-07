package com.onekliclabs.hatch.rowanchatroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Owner on 8/28/2015.
 */
public class SplashPageActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);

        getActionBar().hide();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run()
            {
                /* Create an Intent that will start the Menu-Activity. */
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                SplashPageActivity.this.finish();
            }
        }, 3000);
    }
}
