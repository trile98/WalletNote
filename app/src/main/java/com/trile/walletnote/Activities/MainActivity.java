package com.trile.walletnote.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.trile.walletnote.ui.history.HistoryFragment;
import com.trile.walletnote.R;
import com.trile.walletnote.sharePreferencces.CurrentStatusPrefs;
import com.trile.walletnote.ui.addnew.AddNewFragment;
import com.trile.walletnote.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    int currentTabId;
    public int previousTabId;
    public BottomNavigationView navView;

    int countVar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countVar = 0;
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_incoming, R.id.navigation_outgoing,R.id.navigation_history)
//                .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
       // NavigationUI.setupWithNavController(navView, navController);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        previousTabId = R.id.navigation_home;

        navView.setOnNavigationItemSelectedListener(navListener);

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if(currentTabId == R.id.navigation_outgoing || currentTabId == R.id.navigation_incoming){
            navView.setSelectedItemId(previousTabId);
        }else{
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_button_for_exit_content), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.navigation_home:{
                            countVar = 0;
                            previousTabId = R.id.navigation_home;
                            currentTabId = R.id.navigation_home;
                            selectedFragment = new HomeFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                            break;
                        }
                        case R.id.navigation_incoming:{
                            countVar ++;
                            currentTabId = R.id.navigation_incoming;
                            selectedFragment = new AddNewFragment();
                            showPopupAddnew("Thêm mới thu nhập",selectedFragment,false);
                            break;
                        }
                        case R.id.navigation_outgoing:{
                            countVar ++;
                            currentTabId = R.id.navigation_outgoing;
                            selectedFragment = new AddNewFragment();
                            showPopupAddnew("Thêm mới chi tiêu",selectedFragment,true);
                            break;
                        }

                        case R.id.navigation_history:{
                            countVar = 0;
                            previousTabId = R.id.navigation_history;
                            currentTabId = R.id.navigation_history;
                            selectedFragment = new HistoryFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                            break;
                        }
                    }

                    return true;
                }
            };

    private void showPopupAddnew(String title,Fragment fragment, boolean addNewType){
        Dialog dialog = new Dialog(findViewById(R.id.fragment_container).getContext());
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.addnew_nav_dialog);
        TextView titleTextView = dialog.findViewById(R.id.addNewNavTitle);

        RadioGroup radioGroup = dialog.findViewById(R.id.addNewNavRadioGrp);
        Button confirmBtn = dialog.findViewById(R.id.addNewNavConfirmBtn);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Fragment()).commit();

        radioGroup.check(R.id.periodicAddNewRadioBtn);

        titleTextView.setText(title);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedBtnId = radioGroup.getCheckedRadioButtonId();
                RadioButton checkedBtn = dialog.findViewById(checkedBtnId);

                String DurationType = checkedBtn.getHint().toString();
                setUpAddNewInfoInSharedPreference(DurationType,addNewType);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                countVar = 0;
                dialog.dismiss();
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    navView.setSelectedItemId(R.id.navigation_home);
                    dialog.dismiss();
                }
                return true;
            }
        });

        if(countVar ==1 ) {
            dialog.show();
        }
    }

    private void setUpAddNewInfoInSharedPreference(String durationType, boolean addNewType){
        CurrentStatusPrefs currentStatusPrefs = new CurrentStatusPrefs(this.getApplicationContext());
        int result = currentStatusPrefs.setAddNewStatus(addNewType,durationType);
        if(result == 0)
            return;
    }

    public void replaceFragment(int chosenFragmentId){
        navView.setSelectedItemId(chosenFragmentId);
    }

}