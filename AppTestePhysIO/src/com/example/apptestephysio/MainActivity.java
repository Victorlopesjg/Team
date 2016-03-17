package com.example.apptestephysio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {

	private BluetoothAdapter mBluetoothAdapter;
	
	Handler bluetoothIn;
	final int handlerState = 0;
	
	boolean sendUdp;
	String udpOutputData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();
		
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		final List<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
		list.addAll(pairedDevices);
		
		final ListView listDevices  = (ListView) findViewById(R.id.listDevices);
		DeviceAdapter adapter = new DeviceAdapter(getApplicationContext(), list);
		listDevices.setAdapter(adapter);
		
		listDevices.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,View view, int position, long id) {

				BluetoothDevice device = (BluetoothDevice) listDevices.getItemAtPosition(position);
				Intent i = new Intent(getApplicationContext(), SimulacaoActivity.class);
				i.putExtra(SimulacaoActivity.EXTRA_DEVICE_ADDRESS, device.getAddress());
				startActivity(i);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
