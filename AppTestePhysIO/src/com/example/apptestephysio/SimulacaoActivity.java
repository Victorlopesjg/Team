package com.example.apptestephysio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import dca.ufrn.team.physio.model.PhysioData;
import dca.ufrn.team.physio.utils.PhysioConnect;

public class SimulacaoActivity extends Activity {
	
	Handler bluetoothIn;
	final int handlerState = 0;
	private BluetoothSocket btSocket = null;
	public static final String EXTRA_DEVICE_ADDRESS = "ADDRESS";
	public static final int SERVERPORT = 6666;
	private ServerSocket socServer;
	String readMessage = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simulacao);

		try {
			Intent intent = getIntent();
			String address = intent.getStringExtra(EXTRA_DEVICE_ADDRESS);
			btSocket = PhysioConnect.createBluetoothSocket(address);
			btSocket.connect();

			TaskBlu(btSocket);
		} catch (IOException e) {
			Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
			try {
				btSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void startarMeuSocket(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					socServer = new ServerSocket(SERVERPORT);
					Socket socClient = null;
					while (true) {
						socClient = socServer.accept();
						InputStream is = socClient.getInputStream();
						PrintWriter out = new PrintWriter(socClient.getOutputStream(), true);
						
						PhysioData physioData = new PhysioData();
						String array[] = new String[3];
						array = readMessage.split(" ");
						String json = "{\"thumb\":"+array[1]+
								",\"indexFinger\":"+array[1]+
								",\"middleFinger\":"+array[1]+
								",\"ringFinger\":"+array[1]+
								",\"littleFinger\":"+array[1]+"}";
						out.println(json);
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						String result = br.readLine();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void TaskBlu(BluetoothSocket socket) {
		new AsyncTask<Void, Void, Void>() {
			PhysioConnect physioConnect;
			protected void onPreExecute() {
				physioConnect = new PhysioConnect(btSocket);
				try {
					physioConnect.writePhysio("#sd");
				} catch (IOException e) {
					e.printStackTrace();
				}
				startarMeuSocket();
				Toast.makeText(getBaseContext(), "Passou amigo", Toast.LENGTH_LONG).show();
			};

			@Override
			protected Void doInBackground(Void... params) {
				startarMeuSocket();
				while (true) {
					try {
						physioConnect.writePhysio("#sd");
						readMessage = physioConnect.readPhysio();
						Log.d("MSG BLUETOOTH", readMessage);
					} catch (Exception e) {
						break;
					}
				}
				return null;
			}
		}.execute(null, null, null);
	}

}
