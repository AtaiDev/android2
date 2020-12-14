package com.example.taskapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.taskapp.utils.Prefs;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavigation();
        boolean isShown = new Prefs(this).isShown();
        if (!isShown) navController.navigate(R.id.boardFragment);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                clearAll();
                break;
            case R.id.action_exit:
                exitApp();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);

    }

    private void exitApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Confirm exit ?")
                .setMessage("exiting will not store your last action!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Prefs(getBaseContext()).clearAll();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "Your are back to business", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.create().show();

    }

    private void clearAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Are sure you want to clear all data ?")
                .setView(R.layout.alert_dialog_view)
                .setMessage("clearing all data may not be retrieved")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Prefs(getBaseContext()).clearAll();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(),"Your are back to bussiness",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();


    }

    private void initNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.navigation_home || destination.getId() == R.id.navigation_dashboard ||
                        destination.getId() == R.id.navigation_notifications || destination.getId() == R.id.navigation_profile) {
                    navView.setVisibility(View.VISIBLE);
                } else {
                    navView.setVisibility(View.GONE);
                }
                /*hiding the tool bar by checking it for null to avoid */
                if (getSupportActionBar() != null) {
                    if (destination.getId() == R.id.boardFragment)
                        getSupportActionBar().hide();
                    else getSupportActionBar().show();
                }

            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    public void closeFragment() {
        navController.navigateUp ();
    }
}