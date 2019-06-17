package com.example.gmahn.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class BTPairedListActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 385;

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> pairedDeviceInfoAdapter;
    private Set<BluetoothDevice> pairedDevices;

    private ListView lvPairedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btpaired_list);

        lvPairedDevice = findViewById(R.id.lvPairedDevice);
        pairedDeviceInfoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        findViewById(R.id.btnClosePairedList).setOnClickListener(v -> finish());
        getBlueToothAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!bluetoothAdapter.isEnabled()) {
            requestEnableBluetooth();
        }
        else {
            findPairedDevices();
        }
    }

    private void getBlueToothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void requestEnableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private void findPairedDevices() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        pairedDeviceInfoAdapter.clear();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceInfoAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            lvPairedDevice.setAdapter(pairedDeviceInfoAdapter);
        }
    }
}
