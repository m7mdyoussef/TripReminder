package com.example.tripreminder2021.ui.activities;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripreminder2021.config.SharedPreferencesManager;
import com.example.tripreminder2021.pojo.LocalHelper;
import com.example.tripreminder2021.requests.InternetConnection;
import com.example.tripreminder2021.ui.activities.login.Activity_Login;
import com.example.tripreminder2021.ui.navigation.upComing.UpcomingFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.tripreminder2021.*;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class UpcomingTripsActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    Fragment fragment;
    FloatingActionButton fab;
    private SharedPreferencesManager sharedPreferencesManager;
    AlertDialog dialog;

    private InternetConnection internetConnection;
    DrawerLayout coordinatorLayout;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_upcoming_trips);
        coordinatorLayout =findViewById(R.id.drawer_layout);


        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 80);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sharedPreferencesManager=new SharedPreferencesManager(this);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UpcomingTripsActivity.this, AddBtnActivity.class);
                startActivityForResult(i, 55);
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        internetConnection=new InternetConnection(this);

        final Snackbar snackBar = Snackbar.make(coordinatorLayout,getString(R.string.no_internet),
                BaseTransientBottomBar.LENGTH_INDEFINITE);
        internetConnection.observe(this,aBoolean -> {

            if (!aBoolean)
                snackBar.show();
            else
                snackBar.dismiss();
        });

        View header=navigationView.getHeaderView(0);
        TextView userMail = header.findViewById(R.id.user_email);
        userMail.setText(sharedPreferencesManager.getCurrentUserEmail());


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.nav_report)
                .setDrawerLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(toolbar, navController, mAppBarConfiguration);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_language) {

                    final String[]  list ={"English","Arabic"};
                    AlertDialog.Builder builder=new AlertDialog.Builder(UpcomingTripsActivity.this);
                    dialog = builder.setTitle(getString(R.string.changeLanguage))
                            .setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    if (i==0){
                                        //english
                                        setLocale("en");
                                        recreate();
                                    }
                                    else if (i==1){
                                        //arabic
                                        setLocale("ar");
                                        recreate();
                                    }
                                }
                            })
                            .setCancelable(true)
                            .create();
                    dialog.show();




                    return true;

                } else if (menuItem.getItemId() == R.id.nav_logout) {
                    Log.i("TAG", "onNavigationItemSelected: hhhhhhhhhhhhhhh");
                    Log.i("TAG", "logoutt"+FirebaseAuth.getInstance().getCurrentUser());

                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(UpcomingTripsActivity.this);
                    alertDialogBuilder.setMessage("Sure you want to  log out");
                    alertDialogBuilder.setPositiveButton("yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Log.i("TAG", "logouthhhhhhhhht"+FirebaseAuth.getInstance().getCurrentUser());
                        sharedPreferencesManager.setUserLogin(false);
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        Intent mainIntent = new Intent(UpcomingTripsActivity.this, Activity_Login.class);
                        startActivity(mainIntent);
                        finish();
                    });

                    alertDialogBuilder.setNegativeButton("Cancel",
                            (arg0, arg1) -> {
                            });

                    //Showing the alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();



                    return true;
                } else if (menuItem.getItemId() == R.id.nav_home) {
                    //Navigation here

                    navController.navigate(R.id.nav_home);
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    return true;
                } else if (menuItem.getItemId() == R.id.nav_history) {
                    //Navigation here
                    //fab.hide();
                    navController.navigate(R.id.nav_history);
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    return true;
                }
                else if(menuItem.getItemId()==R.id.nav_report)
                {
                    navController.navigate(R.id.nav_report);
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    return true;

                }

                return true;
            }
        });


    }

    private void setLocale(String langu) {

        Locale locale  = new Locale(langu);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //save data to shared preferences
        SharedPreferences.Editor editor =getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("my langu", langu);
        editor.apply();
    }

    //load language saved in shared preferences
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String my_lang = prefs.getString("my langu", "en");
        setLocale(my_lang);
        LocalHelper.LANGUAGE=my_lang;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // finishAffinity();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.upcoming_trips, menu);

        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }

}
