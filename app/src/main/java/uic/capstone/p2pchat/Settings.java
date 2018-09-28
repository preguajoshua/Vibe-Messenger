package uic.capstone.p2pchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import javax.crypto.Mac;
public class Settings extends AppCompatActivity {

    WifiManager wifiManager;
    WifiInfo connection;
    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Information");

        if(getSupportActionBar()!=null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connection = wifiManager.getConnectionInfo();
        TextView SSID, RSSI, MacAddress;

        SSID = (TextView) findViewById(R.id.WiFiName);
        RSSI = (TextView) findViewById(R.id.RSSI);
        MacAddress = (TextView) findViewById(R.id.MacAddress);

        int numberOfLevels = 5;
        int rssi = connection.getRssi();
//        int rssi = Integer.parseInt(RSSI.getText().toString());
        String condition="";
        if(rssi <= 0 && rssi >= -50){
            condition="Best Signal";
        }else if(rssi < -50 && rssi  >= -70){
            condition="Good Signal";
        }else if(rssi < -70 && rssi >= -80){
            condition="Low Signal";
        }else if(rssi < -80 && rssi >= -100){
            condition="Very Weak Signal";
        }else{
            condition="No signal";
        }


        SSID.setText(connection.getSSID());
        RSSI.setText(condition);
        MacAddress.setText(connection.getMacAddress());
    }


    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
