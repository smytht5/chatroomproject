package com.onekliclabs.hatch.rowanchatroom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Owner on 7/17/15.
 */
public class EditProfileActivity extends AppCompatActivity
{

    public static final int IMAGE_GALLERY_REQUEST = 20;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private Uri pictureUri;
    private Uri fileUri;
    private FragmentManager fragmentManager;

    private ImageButton imageButton;
    private EditText userNameEditText;
    private Button confirmButton;

    static Client xmpp;         // client connected to server

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //imageButton =  (ImageButton) findViewById(R.id.imgbutn_editimage);
        userNameEditText = (EditText) findViewById(R.id.ep_editText_username);
        confirmButton = (Button) findViewById(R.id.ep_button);
        //if user has already chosen picture upload it to image button
        if (pictureUri != null)
        {
            InputStream inputStream ;
            try
            {
                inputStream = getContentResolver().openInputStream(pictureUri);

                // get a bitmap from the stream.
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                // show the image to the user
                imageButton.setImageBitmap(image);

            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

        }

        //set username first letter uppercase
        userNameEditText.setText(ChatRoomActivity.userName);

        //listen for user to press done to set new username
        userNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    //new username
                    String newUserName = userNameEditText.getText().toString();
                    //reset text to changed username
                    userNameEditText.setText(newUserName);
                    //set username above chat bubble to changed username
                    ChatRoomActivity.userName = newUserName;

                    handled = true;
                }
                return handled;
            }
        });


        //when button is pressed set username to new nickname
        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String tempName = userNameEditText.getText().toString();
                if(tempName.trim().length() == 0){
                    Log.e("NickName error", ": Nickname length 0");
                    userNameEditText.setError("Username is empty, Please input a valid name");
                }
                else {
                    DashBoardActivity.nickName = tempName;
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Confirm new nick name");
                    builder.setMessage("Is the name "+ tempName + " correct?");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
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
