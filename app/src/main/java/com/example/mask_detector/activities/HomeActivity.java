package com.example.mask_detector.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mask_detector.R;
import com.example.mask_detector.auth.Login;
import com.example.mask_detector.fragments.DocumentsFragment;
import com.example.mask_detector.fragments.HomeFragment;
import com.example.mask_detector.fragments.MediaFragment;
import com.example.mask_detector.fragments.ProfileFragment;
import com.example.mask_detector.fragments.ScanQRFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.nav_doc:
                        selectedFragment = new DocumentsFragment();
                        break;
                    case R.id.nav_favorites:
                        new AlertDialog.Builder(this)
                                .setTitle("Scanning QR...")
                                .setMessage("Scan QR code that refer to an image medical document")
                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.cancel, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        selectedFragment = new ScanQRFragment();
                        break;
                    case R.id.nav_media:
                        selectedFragment = new MediaFragment();
                        break;
                    case R.id.nav_search:
                        selectedFragment = new ProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                return true;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        String id = pref.getString("user", null);

       if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, Login.class));
            finish();
        }

        /*if(id!=null && id.equals("admin")){
            startActivity(new Intent(this,SurveillanceActivity.class));
        }*/
        else {
            Toast.makeText(this, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), Toast.LENGTH_SHORT).show();
        }
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }
}