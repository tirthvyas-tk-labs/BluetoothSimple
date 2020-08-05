package com.example.bluetoothsimple;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    // constants
    private static final int PERMISSION_CODE = 1000;
    private static final int REQUEST_ENABLE_BT = 2000;

    // variables
    private final ArrayList<String> mDeviceList = new ArrayList<>();

    // UI components
    private ListView listView;

    // objects declaration
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind UI components
        listView = (ListView) findViewById(R.id.listView);


        // check for permissions
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permission, PERMISSION_CODE);
        } else {
            checkBluetoothAdapter();
        }

    }

    // handle runtime permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    checkBluetoothAdapter();
                } else {
                    Toast.makeText(this, "Permission denied. Cannot run app.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // handle default BluetoothAdapter
    private void checkBluetoothAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                startBroadcastIntent();
            }
        }
    }

    // handle bluetooth enable request
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            startBroadcastIntent();
        } else {
            Toast.makeText(this, "Impossible to run without Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    // start discovery & register BroadcastReceiver
    public void startBroadcastIntent() {
        // start discovery
        mBluetoothAdapter.startDiscovery();
        // register broadcast receiver and listen to specific "ACTION_FOUND" request
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);
    }

    // BroadcastReceiver class
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // if remote bluetooth device found
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                //  receive related contents of new bluetooth device
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device != null) {
                    // device name and MAC address
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.d("BTDevice",deviceName + deviceHardwareAddress);

                    // add this info to list
                    mDeviceList.add(device.getName() + "\n" + device.getAddress());

                    // set contents into ListView
                    listView.setAdapter(new ArrayAdapter<>(context,
                            android.R.layout.simple_list_item_1, mDeviceList));
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregister broadcast receiver
        unregisterReceiver(receiver);
    }
}
