package com.example.aco430wifiscanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.net.wifi.ScanResult;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    WifiScanReceiver wifiScanReceiver;
    TextView textView;
    Button button;
    ListView listView;
    List<ScanResult> results;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter adapter;

    //define EXTRA_MESSAGE

    //define LOG_TAG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /* scanStart method is called when the scan button is pressed */
    public void scanStart(View view) {
        arrayList.clear();

        textView = findViewById(R.id.textView4);
        button = findViewById(R.id.button3);
        listView = findViewById(R.id.listView);

        //declare a new WifiScanReceiver object then register the object to receive the broadcast intent SCAN_RESULTS_AVAILABLE_ACTION
        wifiScanReceiver = new WifiScanReceiver();
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //get an instance of a WifiManager object, check whether isWifiEnabled, start the scan if wifi is enabled, or else
        // show “enable wifi” in the welcome text view and wait until user hit scan button again.
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            textView.setText("Enable Wifi");
            Toast.makeText(this, "Enable Wifi", Toast.LENGTH_LONG).show();
            unregisterReceiver(wifiScanReceiver);
        }

        else {
            textView.setText("Scan in progress.");
            button.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.INVISIBLE);
            wifiManager.startScan();
        }


    }

    /* define WifiScanReceiver class and implement its onReceive method. onReceive method of the BroadcastReceiver is called when the registered intent is broadcasted */

    public class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent){
            arrayList.clear();

            results = wifiManager.getScanResults();

            //get an instance of the scan button and set it visible
            button.setVisibility(View.VISIBLE);

            //get an instance of the ap list view and set it visible
            //listView = findViewById(R.id.listView);
            listView.setVisibility(View.VISIBLE);

            //build a list of Strings from the scan results, set up an ArrayAdapter for the ap list view
            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(adapter);

            for(ScanResult scanResult : results) {
                    arrayList.add(scanResult.SSID + " - " + scanResult.BSSID + " - " + scanResult.capabilities + " - " + scanResult.frequency + " " + scanResult.level);
                    adapter.notifyDataSetChanged();
            }
            textView.setText(new StringBuilder().append("Number Of Wifi connections : ").append(arrayList.size()).toString());
            unregisterReceiver(wifiScanReceiver);
        }
    }
}
