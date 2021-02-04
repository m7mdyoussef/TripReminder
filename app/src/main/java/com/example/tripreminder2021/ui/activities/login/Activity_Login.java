package com.example.tripreminder2021.ui.activities.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripreminder2021.R;
import com.example.tripreminder2021.config.Constants;
import com.example.tripreminder2021.config.SharedPreferencesManager;
import com.example.tripreminder2021.dataValidation.DataValidator;
import com.example.tripreminder2021.dataValidation.ValidationServices;
import com.example.tripreminder2021.ui.activities.UpcomingTripsActivity;
import com.example.tripreminder2021.ui.activities.register.Activity_Register;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
//import com.twitter.sdk.android.core.Callback;
//import com.twitter.sdk.android.core.Result;
//import com.twitter.sdk.android.core.Twitter;
//import com.twitter.sdk.android.core.TwitterAuthConfig;
//import com.twitter.sdk.android.core.TwitterConfig;
//import com.twitter.sdk.android.core.TwitterException;
//import com.twitter.sdk.android.core.TwitterSession;
//import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Activity_Login extends AppCompatActivity
        implements ILoginContract.View,DataValidator.View {

    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button btn_login;
    private TextView tv_register_link;
    private TextView restPassword;
    private Switch aSwitch;
    private ProgressBar progressBar;
    // facebook google twitter

    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private LoginButton loginWithFacebook;
    private CallbackManager callbackManager;
    private SignInButton loginWithGoogle;
    private GoogleSignInClient mGoogleSignInClient;

   // private TwitterLoginButton loginWithTwitter;
    // shared preference
    private SharedPreferencesManager sharedPreferencesManager ;
    // interface
    private ILoginContract.Presenter getPresenter;
    private DataValidator.Presenter getValidator;
    // onBackButton
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    // password rest dialog
    private  AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesManager=new SharedPreferencesManager(this);
        //sharedPreferencesManager.setUserLogin(false);
        if (sharedPreferencesManager.isUserLogin()) {
            Intent intent = new Intent(Activity_Login.this, UpcomingTripsActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity__login);

        initViews();
        getPresenter = new LoginPresenter(this, this);
        getValidator = new ValidationServices(this, this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginWithGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 100);
        });

        // configure facebook


        FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
        loginWithFacebook.registerCallback(callbackManager,new FacebookCallBack());


        authStateListener= firebaseAuth -> {

            FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
            if(firebaseUser!=null)
            {
                sharedPreferencesManager.setUserLogin(true);
                sharedPreferencesManager.setCurrentUserID(firebaseUser.getUid());
                sharedPreferencesManager.setCurrentUserEmail(firebaseUser.getEmail());
            }
            else
            {
                sharedPreferencesManager.setUserLogin(false);
            }

        };
        accessTokenTracker=new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken==null)
                {
                    FirebaseAuth.getInstance().signOut();
                    sharedPreferencesManager.setUserLogin(false);
                }
            }
        };

//
//        TwitterAuthConfig config =new TwitterAuthConfig(getString(R.string.twitter_api_key),
//                                                    getString(R.string.twitter_api_secret));
//        TwitterConfig twitterConfig =new TwitterConfig.Builder(this)
//                .twitterAuthConfig(config).build();
//        Twitter.initialize(twitterConfig);
//        loginWithTwitter.setCallback(new TwitterCallBack());

        btn_login.setOnClickListener(v -> submitForm());
        tv_register_link.setOnClickListener(v -> startActivity(new Intent(this, Activity_Register.class)));
        restPassword.setOnClickListener(v -> initializeDialog());
        aSwitch.setOnClickListener(view -> saveUserData());
    }

    private void initViews() {
        mEmailView = findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);
        inputLayoutEmail = findViewById(R.id.input_layout_login_email);
        inputLayoutPassword = findViewById(R.id.input_layout_login_password);
        btn_login = findViewById(R.id.btn_login);
        tv_register_link = findViewById(R.id.tv_reg_link);
        aSwitch = (findViewById(R.id.remember_switch));
        progressBar = findViewById(R.id.login_progress);
        progressBar.setVisibility(View.GONE);
        restPassword = findViewById(R.id.restPassword);
        loginWithFacebook = findViewById(R.id.login_with_facebook);
      //  loginWithTwitter = findViewById(R.id.login_with_twitter);

        // Set the dimensions of the sign-in button.
        loginWithGoogle = findViewById(R.id.login_with_google);
        loginWithGoogle.setSize(SignInButton.SIZE_STANDARD);
    }

    private void saveUserData() {
        if (aSwitch.isChecked()) {
            sharedPreferencesManager.setUserData(mEmailView.getText().toString(), mPasswordView.getText().toString());
            sharedPreferencesManager.setUserDataSaved(true);
        }
        else if(!aSwitch.isChecked())
            sharedPreferencesManager.setUserDataSaved(false);
    }

    private void getUserData() {
        String[] array = sharedPreferencesManager.getUSerData();
        mEmailView.setText(array[0]);
        mPasswordView.setText(array[1]);
        aSwitch.setChecked(false);
    }

    private void submitForm() {

        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        if (!getValidator.validateEmail(email)) {
            return;
        }
        if (!getValidator.validatePassword(password)) {
            return;
        }
        try_to_login();
    }

    private void try_to_login() {
        btn_login.setEnabled(false);
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        getPresenter.login(email, password);
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onStart() {

        if (sharedPreferencesManager.isUserDataSaved()) {
            getUserData();
        }
        if (sharedPreferencesManager.isUserLogin())
        {
            Log.i("TAG", "onStart: "+ FirebaseAuth.getInstance().getCurrentUser().getUid());
            Log.i("TAG", "onStart: "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

            Constants.CURRENT_USER_ID=FirebaseAuth.getInstance().getCurrentUser().getUid();
            Constants.CURRENT_USER_EMAIL=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

            Log.i("TAG", "onStart Constants: "+ Constants.CURRENT_USER_ID);
            Log.i("TAG", "onStart Constants: "+ Constants.CURRENT_USER_EMAIL);

        }
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
        super.onStart();
    }
    @Override
    protected void onStop() {
        if(authStateListener!=null)
        {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
        super.onStop();
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
        } else {

            Toast.makeText(getApplicationContext(), "Click Again To Exit", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                getPresenter.loginWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    @Override
    public void onLoginSuccess() {
        btn_login.setEnabled(true);
        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, UpcomingTripsActivity.class));
    }

    @Override
    public void onLoginError(String errorMessage) {
        btn_login.setEnabled(true);
        Toast.makeText(this, "Please Try again\n" + errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInternetDisconnected() {
        btn_login.setEnabled(true);
        Toast.makeText(this, "Sorry!! No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShowProgressBar(boolean val) {
        if (val)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onNameIsValidated(boolean val, String message) {
    }

    @Override
    public void onEmailIsValidated(boolean val, String message) {
        if (!val) {
            inputLayoutEmail.setError(message);
            requestFocus(mEmailView);
        } else
            inputLayoutEmail.setErrorEnabled(false);
    }

    @Override
    public void onPasswordIsValidated(boolean val, String message) {
        if (!val) {
            inputLayoutPassword.setError(message);
            requestFocus(mPasswordView);
        } else
            inputLayoutPassword.setErrorEnabled(false);
    }

    @Override
    public void onEmailSentSuccess() {
        alert.dismiss();
        Toast.makeText(Activity_Login.this, getResources().getString(R.string.rest_link_sent_to_you), Toast.LENGTH_LONG).show();
    }
    @Override
    public void onEmailSentError(String message) {
        Toast.makeText(Activity_Login.this, message, Toast.LENGTH_SHORT).show();
    }

    private void initializeDialog() {

        final LayoutInflater inflater = LayoutInflater.from(this);
        final View inflate_view = inflater.inflate(R.layout.rest_password_fragment, null);

        final AutoCompleteTextView EmailView=inflate_view.findViewById(R.id.rest_emailPassword);
        final  Button btn_submit=inflate_view.findViewById(R.id.send_restEmail);


        btn_submit.setOnClickListener(v -> {
            String email = EmailView.getText().toString().trim();
            sendPasswordResetEmail(email);
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.restPasswordTitle));
        builder.setView(inflate_view);
        builder.setCancelable(false);

        builder.setPositiveButton(getResources().getString(R.string.cancel), (dialog, id) -> builder.create().dismiss());
        alert = builder.create();
        alert.show();

    }
    private void sendPasswordResetEmail(String email){

        if (!getValidator.validateEmail(email)) {
            Toast.makeText(this, getResources().getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            getPresenter.restPassword(email);
        }
    }

    private class FacebookCallBack implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {
            AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
            getPresenter.loginWithFacebook(credential);
        }
        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    }

//    private class TwitterCallBack extends Callback<TwitterSession> {
//
//        @Override
//        public void success(Result<TwitterSession> result) {
//            AuthCredential credential= TwitterAuthProvider.
//                    getCredential(result.data.getAuthToken().token,
//                                    result.data.getAuthToken().secret);
//
//            getPresenter.loginWithTwitter(credential);
//        }
//
//        @Override
//        public void failure(TwitterException exception) {
//
//        }
//    }


}
