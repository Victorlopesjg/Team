package com.example.clienttestebroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, Wifi.WiFiListener {

	private Wifi wiFi;
	private ListView list;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> itens;
	private TextView chamadaStatus;

	Button buttonSend;

	private static final String SERVERPORT = "6666";
	
	private String android_id;
	
	ClientAsyncTask clientAST;
	
	WifiManager wifiManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.botao).setOnClickListener(this);
		list = (ListView) findViewById(R.id.list);

		itens = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itens);
		list.setAdapter(adapter);

		buttonSend = (Button) findViewById(R.id.myButton);
		chamadaStatus = (TextView) findViewById(R.id.tvChamadaAtiva);
		
		android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
	}

	/**
	 * A��o do bot�o
	 *
	 * @param view
	 */
	public void onClick(View view) {
		Toast.makeText(this, "Buscando....", Toast.LENGTH_LONG).show();
		//Inicia a Busca...
		wiFi = Wifi.startScanWIFI(this, this);
	}

	/**
	 * Retorno da busca...
	 *
	 * @param arg0
	 * @param arg1
	 * @param results
	 */
	public void onResultScan(Context arg0, Intent arg1, List<ScanResult> results) {
		itens.clear();

		WifiConfiguration wifiConfiguration = new WifiConfiguration();
		for (ScanResult scanResult : results) {
			if ( scanResult.SSID.equals("Teste") ) {
				wifiConfiguration.BSSID = scanResult.BSSID;
				wifiConfiguration.SSID = "\"" + scanResult.SSID + "\"";
				wifiConfiguration.preSharedKey = "\"12345678\"";

				// Conecto na nova rede criada.
				wifiManager = wiFi.getWifiManager(this);
				int netId = wifiManager.addNetwork(wifiConfiguration);
//				wifiManager.saveConfiguration();
				wifiManager.enableNetwork(netId, true);
			}

			itens.add(scanResult.SSID + " - " + scanResult.BSSID);
		}
		adapter.notifyDataSetChanged();
		buttonSend.setVisibility(View.VISIBLE);
		chamadaStatus.setText(getResources().getString(R.string.desativada));
		chamadaStatus.setTextColor(getResources().getColor(R.color.desativada));
	}


	private InetAddress getBroadcastAddress() throws IOException {
		WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		// handle null somehow

		int broadcast = dhcp.serverAddress;

		return intToInetAddress(broadcast);
	}

	public static InetAddress intToInetAddress(int hostAddress) {
		byte[] addressBytes = { (byte)(0xff & hostAddress),
				(byte)(0xff & (hostAddress >> 8)),
				(byte)(0xff & (hostAddress >> 16)),
				(byte)(0xff & (hostAddress >> 24)) };

		try {
			return InetAddress.getByAddress(addressBytes);
		} catch (UnknownHostException e) {
			throw new AssertionError();
		}
	}

	public void send(View view) {
		
		//Pass the server ip, port and client message to the AsyncTask
		try {
			//Create an instance of AsyncTask
			clientAST = new ClientAsyncTask();
			Date d = Calendar.getInstance().getTime(); // Current time
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Set your date format
			String currentData = sdf.format(d);
			clientAST.execute(new String[] { getBroadcastAddress().toString().substring(1), SERVERPORT, "Presente" + android_id + " / " + currentData });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * AsyncTask which handles the communication with the server 
	 */
	class ClientAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try {
				String ip = params[0];
				
				//Create a client socket and define internet address and the port of the server
				Socket socket = new Socket(ip, Integer.parseInt(SERVERPORT));
				//Get the input stream of the client socket
				InputStream is = socket.getInputStream();
				//Get the output stream of the client socket
				PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
				//Write data to the output stream of the client socket
				out.println(params[2]);	
				//Buffer the data coming from the input stream
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				//Read data in the input buffer
				result = br.readLine();
				//Close the client socket
				socket.close();
				wifiManager.disconnect();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String s) {
			//Write server message to the text view
			chamadaStatus.setText(s);
		}
		
	}

}