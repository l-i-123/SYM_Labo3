package ch.heigvd.iict.sym.a3dcompassapp;

/**
 * Created by Maxime Vulliens and Elie N'djoli
 * on 20.11.18
 * Description : Activity 2
 */

import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Activity2 extends AppCompatActivity implements BeaconConsumer{


    protected static final String TAG = "RangingActivity";
    BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    ArrayList<Region> newRangedRegions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

        // Add parser for iBeacons;
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        // wake up the app when a beacon is seen
        Region region = new Region("backgroundRegion",
                null, null, null);

        beaconManager.bind(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }


    @Override
    public void onBeaconServiceConnect() {
        newRangedRegions = new ArrayList<>(beaconManager.getRangedRegions());
        //newRangedRegions.
    }



}
