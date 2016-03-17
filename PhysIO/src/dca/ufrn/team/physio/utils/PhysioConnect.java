package dca.ufrn.team.physio.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import dca.ufrn.team.physio.constant.PhysioConstant;

public class PhysioConnect {

	private InputStream inStream;
	private OutputStream outStream;
	
	public PhysioConnect(){}
	
	public PhysioConnect(BluetoothSocket socket, boolean udp, Context context) {
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
		}
		inStream = tmpIn;
		outStream = tmpOut;
	}

	public PhysioConnect(BluetoothSocket socket) {
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
		}
		inStream = tmpIn;
		outStream = tmpOut;
	}
	
	public String readPhysio() throws IOException {
		byte[] buffer = new byte[256];
		int bytes;
		
		bytes = getInStream().read(buffer); 
		String readMessage = new String(buffer, 0, bytes);

		return readMessage;
	}
	
	public void writePhysio (String input) throws IOException{
		byte[] msgBuffer = input.getBytes(); 
		getOutStream().write(msgBuffer); 
	}

	public static BluetoothSocket createBluetoothSocket(String address) throws IOException {
		return createBluetoothSocket(address, PhysioConstant.BTMODULEUUID);
	}

	public static BluetoothSocket createBluetoothSocket(String address, UUID BTMODULEUUID) throws IOException {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
	}

	public InputStream getInStream() {
		return inStream;
	}

	public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}

	public OutputStream getOutStream() {
		return outStream;
	}

	public void setOutStream(OutputStream outStream) {
		this.outStream = outStream;
	}
	
}
