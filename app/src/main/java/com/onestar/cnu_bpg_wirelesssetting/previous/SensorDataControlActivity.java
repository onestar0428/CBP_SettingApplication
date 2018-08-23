//package com.onestar.cnu_bpg_wirelesssetting;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//public class SensorDataControlActivity extends AppCompatActivity {
//    private CommandManager commandManager;
//
//    private Button controlButton, stopButton;
//    private TextView resultTextView;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sensor_data_control);
//
//        commandManager = (CommandManager) getIntent().getSerializableExtra("OBJECT");
//
//        controlButton = (Button) findViewById(R.id.control_button);
//        stopButton = (Button) findViewById(R.id.stop_button);
//        resultTextView = (TextView) findViewById(R.id.data_textview);
//
//        controlButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Button btn = (Button) view;
//                if (btn.getText().toString().equals(R.string.start)) {
//                    if (commandManager != null) {
////                        commandManager.sendCommand(getResources().getString(R.string.start));
//                        btn.setText(getResources().getString(R.string.resume));
//                    }
//                } else if (btn.getText().toString().equals(R.string.resume)) {
//                    if (commandManager != null) {
////                        commandManager.sendCommand(getResources().getString(R.string.resume));
//                        btn.setText(getResources().getString(R.string.start));
//                    }
//                }
//            }
//        });
//        stopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (commandManager != null) {
////                    commandManager.sendCommand(getResources().getString(R.string.stop));
//                    controlButton.setText(getResources().getString(R.string.start));
//                }
//            }
//        });
//    }
//}
