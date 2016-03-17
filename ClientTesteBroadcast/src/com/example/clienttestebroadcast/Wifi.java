package com.example.clienttestebroadcast;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class Wifi extends BroadcastReceiver{


	/**
     * Listener respons�vel para notificar a consulta
     *
     */
    public static interface WiFiListener {

        public void onResultScan(Context arg0, Intent arg1, List<ScanResult> results);
    }

    public static Wifi startScanWIFI(Context context, WiFiListener wiFiListener) {
        Wifi wifi = new Wifi();
        wifi.wiFiListener = wiFiListener;

        //Registra o broadcast para scaniar as redes wi-fi
        context.registerReceiver(wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifi.wifiManager = wifi.getWifiManager(context);

        //Se o wi-fi estiver desabilitado, envia um comando para habilitar e retorna null para tratar.
        if (wifi.wifiManager.isWifiEnabled() == false) {
            Toast.makeText(context, "Habilitando Wi-Fi...", Toast.LENGTH_LONG).show();
            wifi.wifiManager.setWifiEnabled(true);
            return null;
        }

        //Inicia o rastramento
        wifi.wifiManager.startScan();

        return wifi;
    }
    private WifiManager wifiManager;
    private WiFiListener wiFiListener;

    private Wifi() {
    }

    /**
     * Pega o adaptador Wirelles
     *
     * @param context
     * @return
     */
    public WifiManager getWifiManager(Context context) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
        return wifiManager;
    }

    /**
     * M�todo responsavel em receber o broadcast do scan wi-fi
     *
     * @param arg0
     * @param arg1
     */
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        List<ScanResult> results = wifiManager.getScanResults();

        // Se tiver listener, envia os dados.
        if (wiFiListener != null) {
            wiFiListener.onResultScan(arg0, arg1, results);
        }

        //Cancela o Broadcast e desregistra.
        clearAbortBroadcast();
        arg0.unregisterReceiver(this);
    }

}
