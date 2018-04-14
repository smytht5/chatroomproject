package com.onekliclabs.hatch.rowanchatroom;

import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Created by Harold Hatch on 7/8/15.
 */
public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener, OnKeyListener
{
    protected static ChatArrayAdapter adp;
    protected static Client mClient;

    private ListView list;
    private EditText chatText;
    private Button send;

    public static String userName;
    public static Uri pictureUri;

    public static String title;

    public ChatRoomActivity()
    {
        // Default constructor
    }

    public ChatRoomActivity(String title)
    {
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(title);

        //initialize widgets and back button
        initWidgets();

        //listener if user presses send
        send.setOnClickListener(this);
        chatText.setOnKeyListener(this);


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
        if(!message.equals(""))
        {
            mClient.sendMultiChatMessage(message);
            chatText.setText("");
            hideSoftKeyboard();
        }
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
        String nmessage = message.trim();

        if(nmessage.contains("\\u0027"))
            nmessage = nmessage.replace("\\u0027", "'");

        nmessage = nmessage.replaceAll("^\"|\"$","");

        adp.add(new ChatBox(nmessage, type, userName));
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
        mClient.disconnectFromMultiChat();
        super.finish();
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_ENTER)
        {
            onClick(v);
            return true;
        }
        return false;
    }
}


