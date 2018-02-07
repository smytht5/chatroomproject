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
    private RadioButton radioButton;
    private ImageView imageView;
    private RoundImageView roundImageView;
    private TextView username;
    private TextView messageView;
    private int position;

    public ChatBox(String message)
    {
        super();

        this.message = message;
    }

    // -- To do -- change username
    public void setUserName(String userName)
    {
        this.username.setText("razorSandwich");
    }

    void setPosition(int position)
    {
        this.position = position;
    }

    void setMessageView(){messageView.setText(message);}

    public void setImageView(Bitmap image)
    {
        roundImageView = new RoundImageView(image);
        imageView.setImageDrawable(roundImageView);
    }

    public String getMessage()
    {
        return message;
    }

    public int getPositon(){return position;}

    public void initWidgets(View view)
    {
        radioButton = (RadioButton) view.findViewById(R.id.radioButton_like);
        imageView = (ImageView) view.findViewById(R.id.imageview);
        username = (TextView) view.findViewById(R.id.textView_username);
        messageView = (TextView) view.findViewById(R.id.chat_textview);
    }

    public void addLike()
    {
        String text = (String) radioButton.getText();
        int num = Integer.parseInt(text);

        if (text == null)
        {
            radioButton.setText("1");
        }else
            radioButton.setText(num++);
    }

    public void removeLike()
    {
        String text = (String) radioButton.getText();
        int num = Integer.parseInt(text);

        if (text == null)
        {
            radioButton.setText(num--);
        }

    }


}
