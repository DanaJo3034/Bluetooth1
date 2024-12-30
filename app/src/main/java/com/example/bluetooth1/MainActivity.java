package com.example.bluetooth1;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity{

    CheckBox enable_bt, visible_bt;
    TextView name_bt;
    ImageView search_bt;
    ListView listView;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> bluetoothDevices;

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private String[] bluetoothPermissions = {
            android.Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
    };


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enable_bt = findViewById(R.id.enable_bt);
        visible_bt = findViewById(R.id.visible_bt);
        search_bt = findViewById(R.id.search_bt);
        name_bt = findViewById(R.id.name_bt);
        listView = findViewById(R.id.listView);

        BA = BluetoothAdapter.getDefaultAdapter();

        name_bt.setText(getLocalBluetoothName());


        enable_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    BA.disable();
                    Toast.makeText(MainActivity.this, "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intentOn, 0 );
                    Toast.makeText(MainActivity.this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        visible_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, 0);
                    Toast.makeText(MainActivity.this, "Bluetooth Visible", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Bluetooth Invisible", Toast.LENGTH_SHORT).show();
                }
            }
        });

        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list();
            }
        });

        if (!hasBluetoothPermissions()) {
            requestBluetoothPermissions();
        } else {
            if(BA.isEnabled()){
                enable_bt.setChecked(true);
            }
        }
    }
    private boolean hasBluetoothPermissions() {
        for (String permission : bluetoothPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    private void requestBluetoothPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)) {
            new AlertDialog.Builder(this)
                    .setTitle("Bluetooth Permissions")
                    .setMessage("This app needs Bluetooth permissions to scan and connect to devices.")
                    .setPositiveButton("OK", (dialog, which) ->
                            ActivityCompat.requestPermissions(MainActivity.this, bluetoothPermissions, REQUEST_BLUETOOTH_PERMISSIONS))
                    .setNegativeButton("Cancel", null)
                    .show();
        }
        else {
            ActivityCompat.requestPermissions(this, bluetoothPermissions, REQUEST_BLUETOOTH_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (hasBluetoothPermissions()) {
                // Permissions granted, proceed with Bluetooth operations
                // ... your existing Bluetooth initialization code ...
            } else {

                Toast.makeText(this, "Bluetooth permissions are required", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void list() {
        bluetoothDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();
        for (BluetoothDevice device : bluetoothDevices) {
            list.add(device.getName());
        }
        Toast.makeText(this, "Showing Devices", Toast.LENGTH_SHORT).show();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }

    @SuppressLint("MissingPermission")
    public String getLocalBluetoothName() {
        if (BA == null) {
            BA = BluetoothAdapter.getDefaultAdapter();
        }
        return BA.getName() != null ? BA.getName() : BA.getAddress();
    }
}