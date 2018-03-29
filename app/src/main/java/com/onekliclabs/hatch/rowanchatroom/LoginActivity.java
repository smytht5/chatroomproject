package com.onekliclabs.hatch.rowanchatroom;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity
{

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mEmailLoginFormView;

    private static final String DOMAIN = "ec2-54-198-216-41.compute-1.amazonaws.com";
    private static String USERNAME;
    private static String PASSWORD;
    public static Client xmpp;

    private View mSignOutButtons;
    private View mLoginFormView;

    private Button mSigninButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mEmailLoginFormView = findViewById(R.id.email_login_form);
        mSigninButton = findViewById(R.id.email_sign_in_button);

        mSigninButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                USERNAME = mEmailView.getText().toString().substring(0,mEmailView.getText().toString().indexOf("@"));
                PASSWORD = mPasswordView.getText().toString();
                Log.d("Login Info", USERNAME + " , " + PASSWORD);
                xmpp = Client.getInstance(LoginActivity.this, DOMAIN, USERNAME, PASSWORD);
                xmpp.connect("onCreate");

                // continually check for connection while not connected
                do{
                    new Thread(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(100);
                            } catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }

                        }
                    }).start();

                    // check to see if account has connect
                    if(xmpp.isConnected())
                    {
                        // if connected start next activity and destroy this one
                        DashBoardActivity activity = new DashBoardActivity(xmpp);
                        startActivity(new Intent(getBaseContext(), activity.getClass()));
                        LoginActivity.this.finish();
                    }


                }while (!xmpp.isConnected());

            }
        });

    }

}