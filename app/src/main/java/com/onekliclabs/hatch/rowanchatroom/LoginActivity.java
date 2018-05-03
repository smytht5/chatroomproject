package com.onekliclabs.hatch.rowanchatroom;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener
{
    private final String DOMAIN = "ec2-54-198-216-41.compute-1.amazonaws.com";
    private final int RC_SIGN_IN = 9001;
    public static Client xmpp;
    GoogleApiClient mGoogleSignInClient;

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
        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        // set listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        //GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(signInIntent);

        //if( result != null )
        //{
            //GoogleSignInAccount account = result.getSignInAccount();

            //if (account.getEmail().contains(getString(R.string.rowan_email_tag)))
                startClient("default48");//account);
        //}
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
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
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
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        /*
        if(result.isSuccess())
        {
            GoogleSignInAccount account = result.getSignInAccount();

            // Signed in successfully, show authenticated UI.
            if(account.getEmail().contains("@students.rowan.edu"))
                startClient(account);
            else {
                findViewById(R.id.txtview_loginerror).setVisibility(View.VISIBLE);
                Auth.GoogleSignInApi.signOut(mGoogleSignInClient);
            }

        } else {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + result.getStatus().getStatusCode());
            //updateUI(null);
        }*/
    }

    public void startClient(String name)//GoogleSignInAccount account)
    {
        String username = name;//account.getEmail().substring(0,account.getEmail().indexOf('@'));
        // Signed in successfully, show authenticated UI.
        xmpp = new Client(LoginActivity.this, DOMAIN, username,"password"); //account.getId());
        xmpp.connect("startClient");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // start next activity and destroy this one
        DashBoardActivity activity = new DashBoardActivity(xmpp, mGoogleSignInClient);
        startActivity(new Intent(getBaseContext(), activity.getClass()));
        LoginActivity.this.finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}