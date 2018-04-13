package com.onekliclabs.hatch.rowanchatroom;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Hatch on 3/6/18.
 */

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener
{
    public static Client xmpp;         // client connected to server
    public static ConnectivityManager cm;
    public static GoogleApiClient google;
    private static ChatRoomActivity chat;

    public static String newName;

    public DashBoardActivity()
    {
        // default constructor
    }

    public DashBoardActivity(Client xmppClient, GoogleApiClient googleClient )
    {
        xmpp = xmppClient;
        google = googleClient;
        newName = xmpp.getOriginalUserName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //manage network connectivity
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // set event listeners
        findViewById(R.id.imgbtn_Shamrock).setOnClickListener(this);
        findViewById(R.id.btn_jHall).setOnClickListener(this);
        findViewById(R.id.btn_rHall).setOnClickListener(this);
        findViewById(R.id.btn_sHall).setOnClickListener(this);
        findViewById(R.id.btn_bHall).setOnClickListener(this);


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
        // if item in action bar is selected start edit profile activity
        startActivity(new Intent(this, EditProfileActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Auth.GoogleSignInApi.signOut(google);
    }

    @Override
    public void onClick(View view)
    {
        view.setClickable(false);
        /*
         * switch/case to connect to a group chat room based on the button
         * that was pressed. when button is pressed XMPP client is connected
         * and activity is started
         */
        switch(view.getId())
        {
            case R.id.imgbtn_Shamrock:

                chat = new ChatRoomActivity("Shamrock Talk");
                Log.i("newUserName", newName);
                xmpp.joinMultiChat(chat, "rowanchat@conference.ec2-54-198-216-41.compute-1.amazonaws.com", newName);

                // check if joining was successful else throw error
                if(xmpp.isJoined())
                    startActivity(new Intent(getBaseContext(), chat.getClass()));
                else
                    Log.e("Error", "Can't join room");
            break;
            case R.id.btn_jHall:
                chat = new ChatRoomActivity("James Hall");
                xmpp.joinMultiChat(chat, "jameshall@conference.ec2-54-198-216-41.compute-1.amazonaws.com",newName);

                // check if joining was successful else throw error
                if(xmpp.isJoined())
                    startActivity(new Intent(getBaseContext(), chat.getClass()));
                else
                    Log.e("Error", "Can't join room");
            break;
            case R.id.btn_rHall:
                chat = new ChatRoomActivity("Robinson Hall");
                xmpp.joinMultiChat(chat, "robinsonhall@conference.ec2-54-198-216-41.compute-1.amazonaws.com",newName);

                // check if joining was successful else throw error
                if(xmpp.isJoined())
                    startActivity(new Intent(getBaseContext(), chat.getClass()));
                else
                    Log.e("Error", "Can't join room");
                break;
            case R.id.btn_sHall:
                chat = new ChatRoomActivity("Science Hall");
                xmpp.joinMultiChat(chat,"sciencehall@conference.ec2-54-198-216-41.compute-1.amazonaws.com",newName);

                // check if joining was successful else throw error
                if(xmpp.isJoined())
                    startActivity(new Intent(getBaseContext(), chat.getClass()));
                else
                    Log.e("Error", "Can't join room");
                break;
            case R.id.btn_bHall:
                chat = new ChatRoomActivity("Business Hall");
                xmpp.joinMultiChat(chat, "businesshall@conference.ec2-54-198-216-41.compute-1.amazonaws.com",newName);

                // check if joining was successful else throw error
                if(xmpp.isJoined())
                    startActivity(new Intent(getBaseContext(), chat.getClass()));
                else
                    Log.e("Error", "Can't join room");
                break;
        }

        view.setClickable(true);
    }

    @Override
    public void finish()
    {
        super.finish();
        xmpp.disconnectFromServer();
        Auth.GoogleSignInApi.signOut(google);
    }
}
