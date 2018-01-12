package switzersearch.com.switzertrainsearch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.concurrent.Callable;

import switzersearch.com.switzertrainsearch.R;
import switzersearch.com.switzertrainsearch.helper.Application;

public class MainActivity extends AppCompatActivity {
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private CallbackManager mFacebookCallbackManager;
    private LoginButton mFacebookSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);




        Button buttonSubmit = findViewById(R.id.button_submit);
        SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOptionSelectionActivity();
            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });


        mFacebookSignInButton = findViewById(R.id.facebook_sign_in_button);
        mFacebookSignInButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        handleSignInResult(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                LoginManager.getInstance().logOut();
                                return null;
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        handleSignInResult(null);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(MainActivity.class.getCanonicalName(), error.getMessage());
                        handleSignInResult(null);
                    }
                }
        );


    }

    private void goToOptionSelectionActivity() {
        Intent i=new Intent(MainActivity.this,optionSelectionActivity.class);
        startActivity(i);
    }

    private void signInWithGoogle() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(MainActivity.class.getName(),result.toString());
            if (result.isSuccess()) {
                final GoogleApiClient client = mGoogleApiClient;

                handleSignInResult(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if (client != null) {

                            Auth.GoogleSignInApi.signOut(client).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            Log.d(MainActivity.class.getCanonicalName(),
                                                    status.getStatusMessage());

                                        /* TODO: handle logout failures */
                                        }
                                    }
                            );

                        }

                        return null;
                    }
                });

            } else {
                handleSignInResult(null);
            }
        }
        else {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
            goToOptionSelectionActivity();
        }
    }


    private void handleSignInResult(Callable<Void> logout) {
        if(logout == null) {
            /* Login error */
            //Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
            goToOptionSelectionActivity();
        } else {
            /* Login success */
            Application.getInstance().setLogoutCallable(logout);
            startActivity(new Intent(this, LoggedInActivity.class));


        }
    }
}
