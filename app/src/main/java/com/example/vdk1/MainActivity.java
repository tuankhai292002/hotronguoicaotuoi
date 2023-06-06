package com.example.vdk1;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import org.osmdroid.config.Configuration;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.vdk1.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference rootRef;
    myReciever br;
    public static ActivityMainBinding binding;
    Marker marker;
    GeoPoint currentLocation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("test", "onCreate: ");
        super.onCreate(savedInstanceState);
        Bin.isActivityActive = true;
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, ChayNgamService.class));

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        IntentFilter filter = new IntentFilter("test.Broadcast");
        br = new myReciever();
        registerReceiver(br, filter);
        binding.mapview.setTileProvider(new MapTileProviderBasic(MainActivity.this));

        try {
            PackageManager pm = MainActivity.this.getPackageManager();
            String packageName = MainActivity.this.getPackageName();
            PackageInfo pInfo = pm.getPackageInfo(packageName, 0);
            String version = pInfo.versionName;
            Configuration.getInstance().setUserAgentValue(MainActivity.this.getPackageName() + "/" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final ITileSource tileSource = TileSourceFactory.DEFAULT_TILE_SOURCE;
        binding.mapview.setTileSource(tileSource);
        binding.mapview.setMultiTouchControls(true);
        binding.mapview.getController().setZoom(19);  // Thiết lập mức độ zoom mặc định

        binding.mapview.getController().setCenter(new GeoPoint(16.0667, 108.2167));  // Thiết lập vị trí trung tâm bản đồ (latitude, longitude là tọa độ vị trí hiện tại)
        marker = new Marker(binding.mapview);

        marker.setTitle("Vị trí đánh dấu");
        marker.setSnippet("Vị trí hiện tại");
        Drawable markerDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.old1);
        int desiredWidth = markerDrawable.getIntrinsicWidth() / 4;// Kích thước mong muốn (một nửa kích thước ban đầu)
        int desiredHeight = markerDrawable.getIntrinsicHeight() / 4; // Kích thước mong muốn (một nửa kích thước ban đầu)
        Drawable scaledDrawable = new ScaleDrawable(markerDrawable, 0, desiredWidth, desiredHeight).getDrawable();
        marker.setIcon(scaledDrawable);
        //marker.setIcon(getResources().getDrawable(R.drawable.old1));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(null);
        binding.mapview.getOverlays().add(marker);

        binding.mapview.invalidate();
        setContentView(binding.getRoot());
    }

    class myReciever extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("test", "đã recieveeeee");
            MainActivity activity = (MainActivity) context;
            Log.d("test", "đã recieve: " + activity.toString());
            GeoPoint location = intent.getParcelableExtra("CurrentLocation");


            // Kiểm tra nếu marker đã tồn tại
            if (marker != null) {
                // Cập nhật vị trí của marker
                binding.mapview.getController().setCenter(location);// Thiết lập vị trí trung tâm bản đồ (latitude, longitude là tọa độ vị trí hiện tại)
                marker.setPosition(location);

                // Refresh lại bản đồ để hiển thị vị trí mới của marker
                binding.mapview.invalidate();
            }
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