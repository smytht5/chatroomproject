package com.onekliclabs.hatch.rowanchatroom;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Hatch on 3/6/18.
 */

public class DashBoardActivity extends AppCompatActivity
{
    public static Client xmpp;

    ImageButton imgbtn;
    Button jHall;

    private static ChatRoomActivity chat;

    public DashBoardActivity()
    {

    }

    public DashBoardActivity(Client client)
    {
        xmpp = client;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        imgbtn = (ImageButton) findViewById(R.id.imgbtn_Shamrock);
        jHall = (Button) findViewById(R.id.btn_jHall);


        imgbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                chat = new ChatRoomActivity();
                xmpp.joinGroupChat(chat);
                startActivity(new Intent(getBaseContext(), chat.getClass()));
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        startActivity(new Intent(this, EditProfileActivity.class));

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        xmpp.connection.disconnect();
    }

}
