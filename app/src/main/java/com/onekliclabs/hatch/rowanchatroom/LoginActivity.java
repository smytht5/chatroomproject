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

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
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
                Log.d("Login Info", mEmailView.getText().toString()+" password: "+ mPasswordView.getText().toString());
                xmpp = Client.getInstance(LoginActivity.this, DOMAIN, USERNAME, PASSWORD);
                xmpp.connect("onCreate");
                DashBoardActivity activity = new DashBoardActivity(xmpp);
                startActivity(new Intent(getBaseContext(), activity.getClass()));

                while (!xmpp.isConnected()) {
                    LoginActivity.this.finish();
                    new Thread(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(500);
                            } catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            }
        });

    }

}

