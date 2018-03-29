package com.onekliclabs.hatch.rowanchatroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.jivesoftware.smack.packet.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Harold Hatch on 7/8/15.
 */
public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener
{
    protected static ChatArrayAdapter adp;
    protected static Client mClient;

    private ListView list;
    private EditText chatText;
    private Button send;

    public static String userName;
    public static Uri pictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initialize widgets and back button
        initWidgets();

        //listener if user presses send
        send.setOnClickListener(this);


        //keep scrolled to the bottom
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setAdapter(adp);

        adp.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                list.setSelection(adp.getCount() - 1);
            }
        });
    }

    /**
     * Initialize all widgets and classes
     */
    public void initWidgets()
    {

        chatText = (EditText) findViewById(R.id.chat_editText);
        send =  (Button) findViewById(R.id.button_send);
        list = (ListView) findViewById(R.id.listView_1);
        list.setDivider(null);
        list.setDividerHeight(0);
        adp = new ChatArrayAdapter(this, R.layout.post_bubble, userName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void setClient(Client client)
    {
        mClient = client;
    }

    @Override
    public void onClick(View v)
    {
        // Start background thread to send message to chat room
        String message = chatText.getText().toString();
        mClient.sendMessage(message);
        chatText.setText("");
        hideSoftKeyboard();
    }

    /**
     * Hides software keyboard from view
     */
    public void hideSoftKeyboard()
    {
        View view = getCurrentFocus();

        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
        }
    }

    static void postReceivedMessage(String message, String type, String userName)
    {
        adp.add(new ChatBox(message, type, userName));
        adp.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void finish()
    {
        super.finish();
        mClient.disconnectFromCroupChat();
    }
}


