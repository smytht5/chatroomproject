package com.onekliclabs.hatch.rowanchatroom;

/**
 * Created by Hatch on 3/6/18.
 */

import org.jivesoftware.smack.chat.Chat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;

public class MyService extends Service
{
    private static final String DOMAIN = "ec2-54-198-216-41.compute-1.amazonaws.com";
    private static final String USERNAME = "Harold";
    private static final String PASSWORD = "love";
    public static ConnectivityManager cm;
    public static Client xmpp;
    public static boolean ServerchatCreated = false;
    String text = "";
    protected ChatRoomActivity chat;

    @Override
    public IBinder onBind(final Intent intent) {
        return new LocalBinder<MyService>(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        chat = new ChatRoomActivity();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        xmpp = Client.getInstance(chat,MyService.this, DOMAIN, USERNAME, PASSWORD);
        xmpp.connect("onCreate");
        startActivity(new Intent(getBaseContext(), chat.getClass()));
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        xmpp.connection.disconnect();
    }

    public static boolean isNetworkConnected() {
        return cm.getActiveNetworkInfo() != null;
    }
}
