package com.onekliclabs.hatch.rowanchatroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Owner on 7/19/15.
 */
public class RegisterActivity extends Activity
{
    private static final String FILE_REGISTER = "register";
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = (Button) findViewById(R.id.button_register);

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText usernameEditText = (EditText) findViewById(R.id.editText_username);
                //EditText emailEditText = (EditText) findViewById(R.id.editText_email);
                //EditText passwordEditText = (EditText) findViewById(R.id.editText_password);
                //EditText confirmPasswordEditText = (EditText) findViewById(R.id.editText_confpassword);

                String usernameText = usernameEditText.getText().toString();
                //String emailText = emailEditText.getText().toString();
                //String passwordText = passwordEditText.getText().toString();
                //String confPasswordText = confirmPasswordEditText.getText().toString();


                try
                {
                    FileOutputStream foStreamProfile = openFileOutput(FILE_REGISTER, Context.MODE_APPEND);

                    foStreamProfile.write(usernameText.getBytes());
                    foStreamProfile.write("\n".getBytes());

                }
                catch(FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch(IOException e)
                {
                    e.printStackTrace();

                }

                startActivity(new Intent(getBaseContext(),MainActivity.class));

            }
        });
    }
}

