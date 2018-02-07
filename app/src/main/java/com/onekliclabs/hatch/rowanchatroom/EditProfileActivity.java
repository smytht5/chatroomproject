package com.onekliclabs.hatch.rowanchatroom;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.FragmentManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Owner on 7/17/15.
 */
public class EditProfileActivity extends Activity implements GetPictureFragment.OnFragmentInteractionListener
{

    public static final int IMAGE_GALLERY_REQUEST = 20;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private Uri pictureUri;
    private Uri fileUri;
    private FragmentManager fragmentManager;

    private ImageButton imageButton;
    private EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        setActionBar();
        initWidgets();

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
        userNameEditText.setText(MainActivity.userName);

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
                    MainActivity.userName = newUserName;

                    handled = true;
                }
                return handled;
            }
        });


        //when image button is clicked inflate GetPictureFragment
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GetPictureFragment fragment = new GetPictureFragment();

                fragmentManager = getFragmentManager();

                //inflate replacement fragment
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_choosepic, fragment)
                        .commit();
            }
        });
    }

    public void initWidgets()
    {
        imageButton = (ImageButton) findViewById(R.id.imgbutn_editimage);
        userNameEditText = (EditText) findViewById(R.id.ep_editText_username);
    }


    /**
     * Set Icon to back button
     */
    public void setActionBar()
    {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        InputStream inputStream;
        pictureUri = data.getData();

        if (requestCode == IMAGE_GALLERY_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                // we are getting an input stream, based on the URI of the image.
                try {
                    inputStream = getContentResolver().openInputStream(pictureUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    // show the image to the user
                    imageButton.setImageBitmap(image);

                    // set uri in Main
                    MainActivity.pictureUri = pictureUri;

                    //reset fragment space to nothing
                    BlankFragment fragment = new BlankFragment();
                    //inflate replacement fragment
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_choosepic, fragment)
                            .commit();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }

            }
        }else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                // we are getting an input stream, based on the URI of the image.
                try
                {
                    inputStream = getContentResolver().openInputStream(pictureUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    // show the image to the user
                    imageButton.setImageBitmap(image);

                    // set uri in Main
                    MainActivity.pictureUri = pictureUri;

                    //reset fragment space to nothing
                    BlankFragment fragment = new BlankFragment();
                    //inflate replacement fragment
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_choosepic, fragment)
                            .commit();

                } catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED)
            {
                // User cancelled the image capture
                Toast.makeText(this, "Image Capture Cancelled" , Toast.LENGTH_LONG).show();
            } else
            {
                // Image capture failed, advise user
                Toast.makeText(this, "Image Capture Cancelled" , Toast.LENGTH_LONG).show();
            }
        }
    }


    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type)
    {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video
     * @param type
     * */
    private  File getOutputMediaFile(int type)
    {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED)
        {
            Toast.makeText(this , "SD card not mounted", Toast.LENGTH_LONG);
            return null;
        }

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists())
        {
            if (! mediaStorageDir.mkdirs()){
                Log.d("Open Circle", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else
        {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onFragmentInteraction(int position)
    {
        switch(position)
        {
            case GetPictureFragment.CAMERA_BUTTON_POSITION:

                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                break;
            case GetPictureFragment.LIBRARY_BUTTON_POSITION:

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String picturesDirectoryPath = pictureDirectory.getPath();
                Uri data = Uri.parse(picturesDirectoryPath);

                photoPickerIntent.setDataAndType(data, "image/*");

                startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);

                break;
        }


    }

}
