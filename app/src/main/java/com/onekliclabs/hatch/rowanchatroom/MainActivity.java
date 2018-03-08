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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Harold Hatch on 7/8/15.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    /**
     *
     */
    private static final String FILE_REGISTER = "register";

    //Protocal
    static final String ADD_LIKE = "/*";
    static final String REMOVE_LIKE = "/**";

    //widgets
    private ChatArrayAdapter adp;
    private ListView list;
    private EditText chatText;
    private Button send;
    private RadioButton rbutton;

    public static String userName;
    public static Uri pictureUri;

    int enters = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(getBaseContext(), MyService.class));

        //initialize widgets and back button
        initWidgets();

        //listener if user presses send
        send.setOnClickListener(this);

        //keep scrolled to the bottom
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setAdapter(adp);
        /*
        adp.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                list.setSelection(adp.getCount() - 1);
            }
        });*/

    }

    /**
     * When user presses the radio button this method is called
     * @param view
     **/

    public void onRadioButtonClicked(View view)
    {
        //Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        if (checked)
        {
            //mClient.sendMessage(ADD_LIKE + (adp.getCount()));
        }
        else
        {
            //mClient.sendMessage(REMOVE_LIKE +(adp.getCount()));
        }
    }


    /**
     * Initialize all widgets and classes
     */
    public void initWidgets()
    {
        chatText = (EditText) findViewById(R.id.chat_editText);
        rbutton =  (RadioButton) findViewById(R.id.radioButton_like);
        send =  (Button) findViewById(R.id.button_send);
        list = (ListView) findViewById(R.id.listView_1);
        list.setDivider(null);
        list.setDividerHeight(0);
        adp = new ChatArrayAdapter(this, R.layout.bubble_layout, userName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onClick(View v)
    {
        // Start background thread to send message to socket
        String message = chatText.getText().toString();

        // sends the message to the server
        //if (mClient != null)
        //{
        //    mClient.sendMessage(message);
        //}

        adp.add(new ChatBox(message));

        //refresh the list
        adp.notifyDataSetChanged();
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
}


