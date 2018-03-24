package com.onekliclabs.hatch.rowanchatroom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owner on 7/8/15.
 */
public class ChatArrayAdapter extends ArrayAdapter<ChatBox>
{
    private List<ChatBox> messageList;
    private String username;

    public ChatArrayAdapter(Context context, int textViewResourceId, String username)
    {
        super(context, textViewResourceId);
        messageList = new ArrayList<>();
        this.username = username;

    }

    @Override
    public void add(ChatBox chatBox)
    {
        super.add(chatBox);
        messageList.add(chatBox);
    }

    @Override
    public int getCount()
    {
        return messageList.size();
    }

    /**
     *
     * @param index
     * @return item a specific index
     */
    @Override
    public ChatBox getItem(int index)
    {
        return messageList.get(index);
    }

    /**
     * Method to set to individual list items to created bubble layout
     * @param position
     * @param convertView
     * @param parent
     * @return view
     */
    @Override
    public View getView(int position, View convertView,ViewGroup parent)
    {
        View v = convertView;
        ChatBox messageObj = getItem(position);


        if (messageObj.getType().equals("receive"))
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.receive_bubble, parent, false);
        }else
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.post_bubble, parent, false);
        }


        if(getItem(position) == null)
            Log.d("Null Reference", "Position: "+ position);
        //initialize local variables


        messageObj.initWidgets(v);


        //set image to new image if profile image was updated
        Bitmap image = BitmapFactory.decodeResource(v.getResources(), android.R.drawable.ic_menu_add);
        messageObj.setImageView(image);


        messageObj.setMessageView();

        messageObj.setUserName(username);

        messageObj.setPosition(messageList.size());


        return v;
    }


    public Bitmap decodeToBitmap(byte [] decodedByte)
    {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
