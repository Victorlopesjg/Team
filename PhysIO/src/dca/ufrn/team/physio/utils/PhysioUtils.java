package dca.ufrn.team.physio.utils;

import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.Toast;
import dca.ufrn.team.physio.model.PhysioDevice;

public class PhysioUtils {

	public static void checkBTState(Activity myActivity) {
		BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			Toast.makeText(myActivity.getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
			myActivity.finish();
		} else {
			if (!mBtAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				myActivity.startActivityForResult(enableBtIntent, 1);
			}
		}
	}

	public static String getInfoBluetDevice(Activity myActivity) {
		String out = null;
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		
		if(adapter != null)
			out = "\nAdapter: " + adapter.toString() + "\n\nName: " + adapter.getName() + "\nAddress: " + adapter.getAddress();
		
		return out;
	}
	
	@SuppressWarnings("null")
	public static List<PhysioDevice> getDevices(){
		List<PhysioDevice> listPhysioDevices = null;
		// Get the local Bluetooth adapter
		BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		
		if (pairedDevices.size() > 0) {
	          for (BluetoothDevice device : pairedDevices) {
	        	  PhysioDevice d = new PhysioDevice(device.getName().toString(), device.getAddress().toString());
	        	  listPhysioDevices.add(d);
	          }
	      } 
		
		return listPhysioDevices;
	}

}
