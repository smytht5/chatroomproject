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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.auth.api.signin.GoogleSignIn.getClient;

public class LoginActivity extends Activity implements View.OnClickListener
{
    private final String DOMAIN = "ec2-54-198-216-41.compute-1.amazonaws.com";
    private static String USERNAME;
    private static String PASSWORD;
    private final int RC_SIGN_IN = 9001;
    public static Client xmpp;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // set listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if( account != null && account.getEmail().contains(getString(R.string.rowan_email_tag)))
        {
            startClient(account);
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("ActivityResult", "result code: " + resultCode);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            if(account.getEmail().contains("@students.rowan.edu"))
                startClient(account);
            else
                findViewById(R.id.txtview_loginerror).setVisibility(View.VISIBLE);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    public void startClient(GoogleSignInAccount account)
    {
        // Signed in successfully, show authenticated UI.
        String name = account.getEmail();


        if(name.contains("@")) {
            USERNAME = name.substring(0,name.indexOf('@'));
            Log.e("Account Name", "given name: " + USERNAME );
        }
        else
            USERNAME = name;

        PASSWORD = account.getId();

        xmpp = new Client(LoginActivity.this, DOMAIN, USERNAME, PASSWORD);
        xmpp.connect("startClient");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e)
        {

        }

        // start next activity and destroy this one
        DashBoardActivity activity = new DashBoardActivity(xmpp, mGoogleSignInClient);
        startActivity(new Intent(getBaseContext(), activity.getClass()));
        LoginActivity.this.finish();
    }
}