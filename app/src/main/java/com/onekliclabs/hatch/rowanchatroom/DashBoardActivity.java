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

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener
{
    static Client xmpp;

    static ImageButton imgbtn;
    static Button jHall;
    static Button rHall;
    static Button sHall;
    static Button bHall;

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
        rHall = (Button) findViewById(R.id.btn_rHall);
        sHall = (Button) findViewById(R.id.btn_sHall);
        bHall = (Button) findViewById(R.id.btn_bHall);

        imgbtn.setOnClickListener(this);
        jHall.setOnClickListener(this);
        rHall.setOnClickListener(this);
        sHall.setOnClickListener(this);
        bHall.setOnClickListener(this);
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

    @Override
    public void onClick(View view)
    {
        int num = imgbtn.getId();

        switch(view.getId())
        {
            case R.id.imgbtn_Shamrock:
                chat = new ChatRoomActivity("Shamrock Talk");
                xmpp.joinGroupChat(chat, "rowanchat@conference.ec2-54-198-216-41.compute-1.amazonaws.com");
                startActivity(new Intent(getBaseContext(), chat.getClass()));
            break;
            case R.id.btn_jHall:
                chat = new ChatRoomActivity("James Hall");
                xmpp.joinGroupChat(chat, "jameshall@conference.ec2-54-198-216-41.compute-1.amazonaws.com");
                startActivity(new Intent(getBaseContext(), chat.getClass()));
            break;
            case R.id.btn_rHall:
                chat = new ChatRoomActivity("Robinson Hall");
                xmpp.joinGroupChat(chat, "robinsonhall@conference.ec2-54-198-216-41.compute-1.amazonaws.com");
                startActivity(new Intent(getBaseContext(), chat.getClass()));
                break;
            case R.id.btn_sHall:
                chat = new ChatRoomActivity("Science Hall");
                xmpp.joinGroupChat(chat,"sciencehall@conference.ec2-54-198-216-41.compute-1.amazonaws.com");
                startActivity(new Intent(getBaseContext(), chat.getClass()));
                break;
            case R.id.btn_bHall:
                chat = new ChatRoomActivity("Business Hall");
                xmpp.joinGroupChat(chat, "businesshall@conference.ec2-54-198-216-41.compute-1.amazonaws.com");
                startActivity(new Intent(getBaseContext(), chat.getClass()));
                break;
        }


    }


}
