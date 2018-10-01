package com.onestar.cnu_bpg_wirelesssetting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BaseObservable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import com.onestar.cnu_bpg_wirelesssetting.databinding.ActivitySettingsBinding;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DialogBuilder extends BaseObservable {
    private final static String TAG = DialogBuilder.class.getSimpleName();

    private static Context mContext;
    private static LayoutInflater mInflater;
    private static DialogBuilder.dialogBuilderListener mListener;
    private String mKey = "";
    private boolean isShowing = false;

    private static ActivitySettingsBinding mBinding;

    //TODO: static factory or singleton
    public DialogBuilder(Context context, DialogBuilder.dialogBuilderListener listener, ActivitySettingsBinding binding) {
        mContext = context;
        mListener = listener;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBinding = binding;

        Log.d(TAG, "DialogBuilder Constructor");
    }

    public void makeDialog(String key) {
        mKey = key;
        isShowing = true;

        Log.d(TAG, "makeDialog: " + key);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setNeutralButton(R.string.close_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dismiss(dialog);
            }
        });

        if (key.equals(mContext.getResources().getString(R.string.freq))) {
            buildFrequencyDialog(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.led))) {
            buildLedDialog(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.target))) {
            buildTargetDialog(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.report_to))) {
            buildReportDialog(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.protocol))) {
            buildProtocolDialog(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.set_time))) {
            buildTimeDialog(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.wifi))) {
            buildWifiDialog(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.reboot))) {
            buildRebootDialog(mBuilder);
        }

        mBuilder.create().show(); //TODO
        /*
        * 10-01 22:24:13.491 17658-17658/com.onestar.cnu_bpg_wirelesssetting E/AndroidRuntime: FATAL EXCEPTION: main
    Process: com.onestar.cnu_bpg_wirelesssetting, PID: 17658
    android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@61de3f8 is not valid; is your activity running?
        at android.view.ViewRootImpl.setView(ViewRootImpl.java:687)
        at android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:288)
        at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:69)
        at android.app.Dialog.show(Dialog.java:312)
        at com.onestar.cnu_bpg_wirelesssetting.DialogBuilder.makeDialog(DialogBuilder.java:76)
        at com.onestar.cnu_bpg_wirelesssetting.SettingsActivity.onButtonClick(SettingsActivity.java:271)
        at com.onestar.cnu_bpg_wirelesssetting.databinding.ActivitySettingsBinding$OnClickListenerImpl.onClick(ActivitySettingsBinding.java:661)
        at android.view.View.performClick(View.java:5194)
        at android.view.View$PerformClick.run(View.java:20903)
        at android.os.Handler.handleCallback(Handler.java:739)
        at android.os.Handler.dispatchMessage(Handler.java:95)
        at android.os.Looper.loop(Looper.java:145)
        at android.app.ActivityThread.main(ActivityThread.java:5942)
        at java.lang.reflect.Method.invoke(Native Method)
        at java.lang.reflect.Method.invoke(Method.java:372)
        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1399)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1194)
*/
    }

    private void buildTargetDialog(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.dialog_switch, null);

        //TODO: improve conditions
        final Switch pressure1Switch = (Switch) view.findViewById(R.id.switch_pressure1);
        if (mBinding.pressure1TextView.getText().toString().equals("Yes")) {
            pressure1Switch.setChecked(true);
        }
        final Switch pressure2Switch = (Switch) view.findViewById(R.id.switch_pressure2);
        if (mBinding.pressure2TextView.getText().toString().equals("Yes")) {
            pressure2Switch.setChecked(true);
        }
        final Switch rgbSwitch = (Switch) view.findViewById(R.id.switch_rgb);
        if (mBinding.rgbTextView.getText().toString().equals("Yes")) {
            rgbSwitch.setChecked(true);
        }
        final Switch irySwitch = (Switch) view.findViewById(R.id.switch_iry);
        if (mBinding.iryTextView.getText().toString().equals("Yes")) {
            irySwitch.setChecked(true);
        }
        final Switch accgyroSwitch = (Switch) view.findViewById(R.id.switch_accgyro);
        if (mBinding.accgyroTextView.getText().toString().equals("Yes")) {
            accgyroSwitch.setChecked(true);
        }
        final Switch timestampSwitch = (Switch) view.findViewById(R.id.switch_timestamp);
        if (mBinding.timestampTextView.getText().toString().equals("Yes")) {
            timestampSwitch.setChecked(true);
        }

        builder.setView(view)
                .setTitle("ON/OFF the elements")
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        passParameters(boolToInt(pressure1Switch.isChecked()) + ":"
                                + boolToInt(pressure2Switch.isChecked()) + ":" + boolToInt(rgbSwitch.isChecked())
                                + ":" + boolToInt(irySwitch.isChecked()) + ":" + boolToInt(accgyroSwitch.isChecked())
                                + ":" + boolToInt(timestampSwitch.isChecked()));

                        dismiss(dialog);
                    }

                    private int boolToInt(boolean bool) {
                        if (bool) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
    }

    private void buildLedDialog(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.dialog_led, null);

        final EditText redText = (EditText) view.findViewById(R.id.editText_red);
        redText.setText(mBinding.redTextView.getText().toString());

        final EditText greenText = (EditText) view.findViewById(R.id.editText_green);
        greenText.setText(mBinding.greenTextView.getText().toString());

        final EditText blueText = (EditText) view.findViewById(R.id.editText_blue);
        blueText.setText(mBinding.blueTextView.getText().toString());

        final EditText yellowText = (EditText) view.findViewById(R.id.editText_yellow);
        yellowText.setText(mBinding.yellowTextView.getText().toString());

        final EditText irText = (EditText) view.findViewById(R.id.editText_ir);
        irText.setText(mBinding.irTextView.getText().toString());

        builder.setView(view)
                .setTitle("Set (0 ~10)mA of each LED")
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        //TODO: check the range of LED
                        passParameters(redText.getText() + ":" + greenText.getText() + ":" +
                                blueText.getText() + ":" + yellowText.getText() + ":" +
                                irText.getText());

                        dismiss(dialog);
                    }
                });
    }

    private void buildRebootDialog(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.dialog_numpick, null);

        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);

        numberPicker.setMaxValue(60); // 1 min
        numberPicker.setMinValue(0);
        numberPicker.setValue(Integer.parseInt(mBinding.rebootTextView.getText().toString()));

        builder.setView(view)
                .setTitle("Input Reboot Delay (0 ~ )s")
                .setPositiveButton(mContext.getResources().getString(R.string.yes_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        passParameters(numberPicker.getValue() + "");
                        dismiss(dialog);
                    }
                });
    }

    private void buildFrequencyDialog(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.dialog_numpick, null);

        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);

        numberPicker.setMaxValue(500);
        numberPicker.setMinValue(80);
        numberPicker.setValue(Integer.parseInt(mBinding.frequencyTextView.getText().toString()));

        builder.setView(view)
                .setTitle("Input FREQUENCY ( ~ )Hz")
                .setPositiveButton(mContext.getResources().getString(R.string.yes_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        passParameters(numberPicker.getValue() + "");
                        dismiss(dialog);
                    }
                });
    }

    private void buildProtocolDialog(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.dialog_protocol, null);
        final Spinner protocolSpinner = (Spinner) view.findViewById(R.id.protocolSpinner);
        final EditText portEditText = (EditText) view.findViewById(R.id.portEditText);

        if (mBinding.protocolTextView.getText().toString().equals(mContext.getResources().getString(R.string.tcp))){
            protocolSpinner.setSelection(0);
        }else{
            protocolSpinner.setSelection(1);
        }
        portEditText.setText(mBinding.portTextView.getText().toString());

        builder.setView(view)
                .setTitle("Input Wi-Fi information")
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        passParameters(protocolSpinner.getSelectedItem().toString() + ":" + portEditText.getText().toString());

                        dismiss(dialog);
                    }
                });
    }

    private void buildWifiDialog(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.dialog_wifi, null);

        final EditText ssidEditText = (EditText) view.findViewById(R.id.ssidEditText);
        final EditText passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);

        ssidEditText.setText(mBinding.ssidTextView.getText().toString());
        passwordEditText.setText(mBinding.passwordTextView.getText().toString());

        builder.setView(view)
                .setTitle("Choose protocol and input port number (1 ~ 65535)")
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        passParameters(ssidEditText.getText().toString() + ":" + passwordEditText.getText().toString());

                        dismiss(dialog);
                    }
                });

    }

    // TODO: improve list of report
    private void buildReportDialog(AlertDialog.Builder builder) {
        final String[] reportList = {"console", "nw", "both", "none"}; //TODO: enum?
        int checkedIndex = 0;

        for (int i = 0; i < reportList.length; i++) {
            if (reportList[i].equals(mBinding.reportTextView.getText().toString())) {
                checkedIndex = i;
            }
        }

        builder.setSingleChoiceItems(R.array.report_to, checkedIndex, null)
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        passParameters(reportList[position] + "");
                        dismiss(dialog);
                    }
                });
    }

    private void buildTimeDialog(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.dialog_time, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        final NumberPicker hourPicker = (NumberPicker) view.findViewById(R.id.hourPicker);
        final NumberPicker minutePicker = (NumberPicker) view.findViewById(R.id.minutePicker);
        final NumberPicker secondPicker = (NumberPicker) view.findViewById(R.id.secondPicker);

        hourPicker.setMaxValue(60);
        hourPicker.setMinValue(0);

        minutePicker.setMaxValue(60);
        minutePicker.setMinValue(0);

        secondPicker.setMaxValue(60);
        secondPicker.setMinValue(0);

        String[] currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date()).split(":");
        hourPicker.setValue(Integer.parseInt(currentTime[0]));
        minutePicker.setValue(Integer.parseInt(currentTime[1]));
        secondPicker.setValue(Integer.parseInt(currentTime[2]));

        builder.setTitle("Pick the date and time")
                .setView(view)
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //yyyy:m:dd:hh:mm:ss
                        String params = datePicker.getYear() + ":" + datePicker.getMonth() + ":" + datePicker.getDayOfMonth() + ":" +
                                hourPicker.getValue() + ":" + minutePicker.getValue() + ":" + secondPicker.getValue();

                        passParameters(params);
                        dismiss(dialog);
                    }
                });
    }

    private void passParameters(String params) {
        mListener.onDialogValueChanged(mKey, params);
    }

    private void dismiss(DialogInterface dialog) {
        isShowing = false;
        dialog.dismiss();
    }

    public boolean isShowing() {
        return isShowing;
    }

    public interface dialogBuilderListener {
        void onDialogValueChanged(String key, String params);
    }
}
