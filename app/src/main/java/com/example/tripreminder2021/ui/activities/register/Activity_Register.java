package com.example.tripreminder2021.ui.activities.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripreminder2021.R;
import com.example.tripreminder2021.dataValidation.DataValidator;
import com.example.tripreminder2021.dataValidation.ValidationServices;
import com.example.tripreminder2021.requests.InternetConnection;
import com.example.tripreminder2021.ui.activities.login.Activity_Login;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class Activity_Register extends AppCompatActivity implements IRegisterContract.View , DataValidator.View{


    private IRegisterContract.Presenter getPresenter;
    private ProgressBar progressBar;

    private DataValidator.Presenter getValidator;

    private Button btn_register;
    private TextView txt_login_link;
    private EditText Username,Email,Password;
    private TextInputLayout inputLayoutName,inputLayoutEmail, inputLayoutPassword;

    private InternetConnection internetConnection;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__register);
        coordinatorLayout =findViewById(R.id.register_coordinator_layout);

        initViews();

        getPresenter=new RegisterPresenter(this,this);
        getValidator=new ValidationServices(this,this);

        btn_register.setOnClickListener(v -> submitForm());
        txt_login_link.setOnClickListener(v -> finish());

        snackBar = Snackbar.make(coordinatorLayout,getString(R.string.no_internet),
                BaseTransientBottomBar.LENGTH_INDEFINITE);
        snackBar.show();
        internetConnection.observe(this,aBoolean -> {

            if (!aBoolean)
                snackBar.show();
            else
                snackBar.dismiss();
        });
    }

    private void initViews(){
        Username=findViewById(R.id.register_username);
        Email=findViewById(R.id.register_email);
        Password=findViewById(R.id.register_password);
        btn_register=findViewById(R.id.btn_register);
        txt_login_link=findViewById(R.id.tv_log_link);

        inputLayoutName =  findViewById(R.id.input_layout_register_username);
        inputLayoutEmail =  findViewById(R.id.input_layout_register_email);
        inputLayoutPassword =  findViewById(R.id.input_layout_register_password);


        progressBar = findViewById(R.id.login_progress);
        progressBar.setVisibility(View.GONE);
        internetConnection=new InternetConnection(this);
    }

    private void try_to_register(){

        btn_register.setEnabled(false);

        String username=Username.getText().toString().trim();
        String email=Email.getText().toString().trim();
        String password=Password.getText().toString().trim();
        getPresenter.register(username,email,password);
    }

    private void submitForm(){

        String name=Username.getText().toString().trim();
        String email=Email.getText().toString().trim();
        String password=Password.getText().toString().trim();

        if (!getValidator.validateName(name)) {
           return;
        }
        if (!getValidator.validateEmail(email)) {
            return;
        }
        if (!getValidator.validatePassword(password)){
            return;
        }
        try_to_register();

    }

    @Override
    public void onRegisterSuccess() {
        btn_register.setEnabled(true);
        Toast.makeText(this, "Sign up Successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, Activity_Login.class));
    }

    @Override
    public void onRegisterError(String errorMessage) {
        btn_register.setEnabled(true);
        Toast.makeText(this, "Please Try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInternetDisconnected() {
        btn_register.setEnabled(true);
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
    public void onNameIsValidated(boolean value,String message) {

        if (!value){
            inputLayoutName.setError(message);
            requestFocus(Username);
        }
        else
            inputLayoutName.setErrorEnabled(false);
    }

    @Override
    public void onEmailIsValidated(boolean value,String message) {
        if (!value){
            inputLayoutEmail.setError(message);
            requestFocus(Email);
        }
        else
            inputLayoutEmail.setErrorEnabled(false);
    }

    @Override
    public void onPasswordIsValidated(boolean value,String message) {
        if (!value){
            inputLayoutPassword.setError(message);
            requestFocus(Password);
        }
        else
            inputLayoutPassword.setErrorEnabled(false);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
