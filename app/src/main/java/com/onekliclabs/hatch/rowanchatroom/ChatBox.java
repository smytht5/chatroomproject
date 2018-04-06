package com.onekliclabs.hatch.rowanchatroom;


import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Harold.
 *
 * This is the chat box. The chat stores all information needed to post a
 * message to the current window. This information includes: message, user name,
 * user image. Also the chat box initializes all views needed to post a message
 * to the current window.
 */
public class ChatBox
{
    private ImageView imageView;
    private RoundImageView roundImageView;
    private TextView usernameView;
    private TextView messageView;

    private String message;
    private String type;                    // the type of message received from server (message sent by user or another)
    private String username;

    public ChatBox(String message, String type, String username)
    {
        super();
        this.type = type;
        this.message = message;
        this.username = username;
    }

    public String getMessage()
    {
        return message;
    }


    public String getType()
    {
        return type;
    }

    void initInfo(Bitmap image)
    {
        usernameView.setText(username);
        messageView.setText(message);
        roundImageView = new RoundImageView(image);
        imageView.setImageDrawable(roundImageView);
    }

    void initWidgets(View view)
    {

        if(type.equals("user"))
        {
            imageView = view.findViewById(R.id.imageview);
            usernameView = view.findViewById(R.id.textView_username);
            messageView = view.findViewById(R.id.chat_textview);
            return;
        }

        imageView =  view.findViewById(R.id.imgView_RC);
        usernameView =  view.findViewById(R.id.txtView_UsrnameRC);
        messageView =  view.findViewById(R.id.txtView_ChatRC);
    }


}
