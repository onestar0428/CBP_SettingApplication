package com.onestar.cnu_bpg_wirelesssetting.previous;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onestar.cnu_bpg_wirelesssetting.R;

public class DataCommunicationActivity extends AppCompatActivity implements android.view.View.OnClickListener{
    private TextView connectionStatus;
    private Button bluetoothButton;
    private Button wifiButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectionStatus = (TextView)findViewById(R.id.connectionText);
        bluetoothButton = (Button)findViewById(R.id.bluetoothButton);
        wifiButton = (Button)findViewById(R.id.wifiButton);

        bluetoothButton.setOnClickListener(this);
        wifiButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){

        switch(v.getId()){
            case R.id.bluetoothButton:
                // connecting with bluetooth
                //startActivity(new Intent(this, BluetoothConnectionActivity.class));
                break;
            case R.id.wifiButton:
                // connecting with wi-fi
                //startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        //if (connected){
        //startActivity(new Intent(this, SettingsActivity.class));
        //connectionStatus.setText(R.string.connection_status_success);
        //}else{
        //    connectionStatus.setText(R.string.connection_status_fail);
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
