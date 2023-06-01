package com.example.vdk1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.vdk1.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database ;
    DatabaseReference rootRef;
    myReciever br;
    public static ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("test", "onCreate: ");
        super.onCreate(savedInstanceState);
        Bin.isActivityActive=true;
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, ChayNgamService.class));

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        IntentFilter filter = new IntentFilter("test.Broadcast");
        br = new myReciever();
        registerReceiver( br, filter);
        setContentView(binding.getRoot());
    }
    class myReciever extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity activity = (MainActivity) context;
            Log.d("test", "Activity address: " + activity.toString());
            String ketqua =  intent.getStringExtra("ketqua");


        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Bin.isActivityActive = true;
    }
    @Override
    public void onPause() {
        Log.d("test", "onPauseeeee: ");
        Bin.isActivityActive=false;
        super.onPause();
    }
    public void onDestroy()
    {

        Log.d("test", "onDestroyyyyyy: ");
        Bin.isActivityActive=false;
        super.onDestroy();
    }


}