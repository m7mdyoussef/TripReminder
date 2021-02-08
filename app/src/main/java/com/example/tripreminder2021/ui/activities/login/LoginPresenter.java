package com.example.tripreminder2021.ui.activities.login;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.tripreminder2021.config.*;
import com.example.tripreminder2021.config.SharedPreferencesManager;
import com.example.tripreminder2021.pojo.User;
import com.example.tripreminder2021.requests.ConnectionAvailability;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginPresenter implements ILoginContract.Presenter{

    private ILoginContract.View toLoginView;
    private Context context ;
    private ConnectionAvailability checkInternetConnection;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    SharedPreferencesManager sharedPreferencesManager;


    public  LoginPresenter(ILoginContract.View toLoginView, Context context){
        this.toLoginView =toLoginView;
        this.context=context;
        checkInternetConnection=new ConnectionAvailability(context);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressBar=new ProgressBar(context);
        sharedPreferencesManager=new SharedPreferencesManager(context);
    }

    @Override
    public void login(String email, String password) {

        if (!checkInternetConnection.isConnectingToInternet()){
            toLoginView.onInternetDisconnected();
            return;
        }
        else {
            toLoginView.onShowProgressBar(true);
            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            doAfterLoginSuccess(firebaseAuth.getCurrentUser().getUid(),
                                    firebaseAuth.getCurrentUser().getEmail());
                        }
                        else {
                            toLoginView.onLoginError(task.getException().getMessage());
                        }
                        toLoginView.onShowProgressBar(false);
                    });
        }
    }

    @Override
    public void loginWithGoogle(GoogleSignInAccount signInAccount) {
        toLoginView.onShowProgressBar(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        databaseReference.child(Constants.USER_CHILD_NAME).child(user.getUid()).child("email").setValue(user.getEmail());
                        databaseReference.child(Constants.USER_CHILD_NAME).child(user.getUid()).child("name").setValue(user.getDisplayName());
                        databaseReference.child(Constants.USER_CHILD_NAME).child(user.getUid()).child("password").setValue("Login with google");

                        doAfterLoginSuccess(user.getUid(),user.getEmail());
                    } else {
                        toLoginView.onLoginError(task.getException().getMessage());
                    }
                    toLoginView.onShowProgressBar(false);
                });
    }

    @Override
    public void loginWithFacebook(AuthCredential credential) {
        toLoginView.onShowProgressBar(true);
        firebaseAuth.signInWithCredential(credential).
                addOnCompleteListener((Activity) context, task -> {

                    if (task.isSuccessful())
                    {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        User facebookUser=new User(user.getDisplayName(),user.getEmail(),"Login using facebook");
                        databaseReference.child(Constants.USER_CHILD_NAME).child(user.getUid()).
                                setValue(facebookUser);

                        Log.i("TAG", "loginWithFacebook: "+user.getDisplayName());


                        doAfterLoginSuccess(user.getUid(),user.getDisplayName());
                    }
                    else {
                        toLoginView.onLoginError(task.getException().getMessage());
                    }
                    toLoginView.onShowProgressBar(false);

                });
    }

    @Override
    public void loginWithTwitter(AuthCredential credential) {
       toLoginView.onShowProgressBar(true);
       firebaseAuth.signInWithCredential(credential).
               addOnCompleteListener((Activity) context, task -> {
                   if (task.isSuccessful())
                   {
                       FirebaseUser user = firebaseAuth.getCurrentUser();
                       User twitterUser=new User(user.getDisplayName(),user.getEmail(),"Login using twitter");
                       databaseReference.child(Constants.USER_CHILD_NAME).child(user.getUid()).
                               setValue(twitterUser);
                       doAfterLoginSuccess(user.getUid(),user.getDisplayName());
                   }
                   else {
                       toLoginView.onLoginError(task.getException().getMessage());
                   }
                   toLoginView.onShowProgressBar(false);
               });
    }
    @Override
    public void restPassword(String email) {
        toLoginView.onShowProgressBar(true);
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful())
                        toLoginView.onEmailSentSuccess();
                    else
                        toLoginView.onEmailSentError(task.getException().getMessage());

                    toLoginView.onShowProgressBar(false);
                });
    }

    private void doAfterLoginSuccess(String uId,String email)
    {
        sharedPreferencesManager.setUserLogin(true);
        sharedPreferencesManager.setCurrentUserID(firebaseAuth.getCurrentUser().getUid());
        sharedPreferencesManager.setCurrentUserEmail(email);
        Constants.CURRENT_USER_ID=sharedPreferencesManager.getCurrentUserId();

        toLoginView.onLoginSuccess();
    }
}
