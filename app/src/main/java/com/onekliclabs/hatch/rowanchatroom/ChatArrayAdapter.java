package com.onekliclabs.hatch.rowanchatroom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
    private Uri uri;

    public ChatArrayAdapter(Context context, int textViewResourceId, String username)
    {
        super(context, textViewResourceId);
        messageList = new ArrayList<>();
        this.username = username;

    }

    public ChatArrayAdapter(Context context, int textViewResourceId, String username, Uri uri)
    {
        super(context, textViewResourceId);
        messageList = new ArrayList<>();
        this.username = username;
        this.uri = uri;
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
     * Method to set to indiviual list items to created bubble layout
     * @param position
     * @param convertView
     * @param parent
     * @return view
     */
    @Override
    public View getView(int position, View convertView,ViewGroup parent)
    {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.bubble_layout, parent, false);
        }


        //initialize local variables
        ChatBox messageObj = getItem(position);

        messageObj.initWidgets(v);


        //set image to new image if profile image was updated
        Bitmap image = null;

        if (uri != null) {
            InputStream inputStream;
            try {
                // get a bitmap from the stream.
                inputStream = v.getContext().getContentResolver().openInputStream(uri);
                // show the image to the user
                image = BitmapFactory.decodeStream(inputStream);
                messageObj.setImageView(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            image = BitmapFactory.decodeResource(v.getResources(), android.R.drawable.ic_menu_add);
            messageObj.setImageView(image);
        }

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
