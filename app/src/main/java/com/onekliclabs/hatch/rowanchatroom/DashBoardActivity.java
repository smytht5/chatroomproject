package com.onekliclabs.hatch.rowanchatroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Hatch on 3/6/18.
 */

public class DashBoardActivity extends Activity
{
    ImageButton imgbtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        imgbtn = this.findViewById(R.id.imgbtn_Shamrock);

        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                DashBoardActivity.this.finish();
            }
        });
    }
}
