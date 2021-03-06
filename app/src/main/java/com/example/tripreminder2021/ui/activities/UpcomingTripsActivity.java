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
import com.example.tripreminder2021.ui.navigation.history.HistoryFragment;
import com.example.tripreminder2021.ui.navigation.report.ReportFragment;
import com.example.tripreminder2021.ui.navigation.upComing.UpcomingFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.tripreminder2021.*;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
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
    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_upcoming_trips);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header=navigationView.getHeaderView(0);


        Toolbar toolbar = findViewById(R.id.toolbar);

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 80);
        }
        setSupportActionBar(toolbar);


        sharedPreferencesManager=new SharedPreferencesManager(this);



        internetConnection=new InternetConnection(this);

        final Snackbar snackBar = Snackbar.make(drawer,getString(R.string.no_internet),
                BaseTransientBottomBar.LENGTH_INDEFINITE);
        internetConnection.observe(this,aBoolean -> {

            if (!aBoolean) {
                snackBar.show();
                //fab.hide();
            }else {
                snackBar.dismiss();
                //fab.show();
            }
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.nav_report)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        TextView userMail = header.findViewById(R.id.user_email);
        userMail.setText(sharedPreferencesManager.getCurrentUserEmail());





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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.upcoming_trips, menu);
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.lang:

                final String[]  list ={"English","Arabic"};
                AlertDialog.Builder builder=new AlertDialog.Builder(UpcomingTripsActivity.this);
                builder.setTitle(getString(R.string.changeLanguage))
                        .setSingleChoiceItems(list, -1, (dialog, i) -> {
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
                        })
                        .setCancelable(true)
                        .create().show();

                break;
            case R.id.logout:

                Log.i("TAG", "onNavigationItemSelected: hhhhhhhhhhhhhhh");
                Log.i("TAG", "logoutt"+FirebaseAuth.getInstance().getCurrentUser());

                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(UpcomingTripsActivity.this);
                alertDialogBuilder.setMessage("Sure you want to  log out");
                alertDialogBuilder.setPositiveButton("yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    Log.i("TAG", "logouthhhhhhhhht"+FirebaseAuth.getInstance().getCurrentUser());
                    sharedPreferencesManager.setUserLogin(false);
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

                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }

}
