package com.onekliclabs.hatch.rowanchatroom;


import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Owner on 7/8/15.
 */
public class ChatBox
{
    private String message;
    private ImageView imageView;
    private RoundImageView roundImageView;
    private TextView username;
    private TextView messageView;
    private int position;
    private String type;

    public ChatBox(String message, String type)
    {
        super();
        this.type = type;
        this.message = message;
    }


    // -- To do -- change username
    public void setUserName(String userName)
    {
        this.username.setText("from server");
    }


    void setPosition(int position)
    {
        this.position = position;
    }


    void setMessageView()
    {
        messageView.setText(message);
    }

    public void setImageView(Bitmap image)
    {
        roundImageView = new RoundImageView(image);
        imageView.setImageDrawable(roundImageView);
    }

    public String getMessage()
    {
        return message;
    }


    public int getPositon()
    {
        return position;
    }


    public String getType()
    {
        return type;
    }


    public void initWidgets(View view)
    {

        if(type.equals("receive"))
        {
            imageView =  view.findViewById(R.id.imgView_RC);
            username =  view.findViewById(R.id.txtView_UsrnameRC);
            messageView =  view.findViewById(R.id.txtView_ChatRC);
            return;
        }

        imageView = view.findViewById(R.id.imageview);
        username = view.findViewById(R.id.textView_username);
        messageView = view.findViewById(R.id.chat_textview);
    }


}
