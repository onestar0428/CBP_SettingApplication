package com.onestar.cnu_bpg_wirelesssetting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

import java.util.ArrayList;

public class DialogBuilder implements DialogInterface.OnClickListener, DialogInterface.OnMultiChoiceClickListener, NumberPicker.OnValueChangeListener {
    private static Context mContext;
    private static LayoutInflater mInflater;
    private static DialogBuilder.dialogBuilderListener mListener;
    private String mKey = "";

    public DialogBuilder(Context context, DialogBuilder.dialogBuilderListener listener) {
        mContext = context;
        mListener = listener;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void makeDialog(String key) {
        mKey = key;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(key);
        mBuilder.setNeutralButton(mContext.getResources().getString(R.string.close_dialog), this);

        if (key.equals(mContext.getResources().getString(R.string.freq))) {
            makeNumberPicker(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.led))) {
            makeNumberPickers(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.target))) {
            makeSwitcher(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.report_to))) {
            makeChecker(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.protocol))) {
            makeEditText(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.set_time))) {
            makeTimePicker(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.wifi))) {
            makeEditText(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.reboot))) {
            makeNumberPicker(mBuilder);
        }

        mBuilder.create().show();
    }

    private void makeSwitcher(AlertDialog.Builder builder) {
        final ArrayList<String> mSelectedItems = new ArrayList();
        final View view = mInflater.inflate(R.layout.dialog_switch, null);
        final Switch pressure1Switch = (Switch) view.findViewById(R.id.switch_pressure1);
        final Switch pressure2Switch = (Switch) view.findViewById(R.id.switch_pressure2);
        final Switch rgbSwitch = (Switch) view.findViewById(R.id.switch_rgb);
        final Switch irySwitch = (Switch) view.findViewById(R.id.switch_iry);
        final Switch accgyroSwitch = (Switch) view.findViewById(R.id.switch_accgyro);
        final Switch timestampSwitch = (Switch) view.findViewById(R.id.switch_timestamp);

        // set switch on/off using valueManager.getter

        builder.setView(view)
                .setTitle("ON/OFF the elements")
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        passParameters(pressure1Switch.isChecked() + "", pressure2Switch.isChecked() + "",
                                rgbSwitch.isChecked() + "", irySwitch.isChecked() + "",
                                accgyroSwitch.isChecked() + "", timestampSwitch.isChecked() + "");

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
    }

    private void makeNumberPickers(AlertDialog.Builder builder) {
        // set led
    }

    private void makeNumberPicker(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.dialog_num, null);

        final NumberPicker np = (NumberPicker) view.findViewById(R.id.numberPicker1);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        if (mKey.equals(mContext.getResources().getString(R.string.reboot))) {
            np.setMaxValue(60); // 1min
            np.setMinValue(0);
            builder.setTitle("Input Reboot Delay (0 ~ )s");
        } else if (mKey.equals(mContext.getResources().getString(R.string.freq))) {
            np.setMaxValue(500);
            np.setMinValue(80);
            builder.setTitle("Input FREQUENCY ( ~ )Hz");
        }

        builder.setNegativeButton(mContext.getResources().getString(R.string.no_dialog), this)
                .setPositiveButton(mContext.getResources().getString(R.string.yes_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        passParameters(np.getValue() + "");
                        dialog.dismiss();
                    }
                });
    }

    private void makeEditText(AlertDialog.Builder builder) {
        if (mKey.equals(mContext.getResources().getString(R.string.wifi))) {
            final View view = mInflater.inflate(R.layout.dialog_text, null);
            builder.setView(view)
                    .setMessage("Input Wi-Fi information")
                    .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText editText1 = (EditText) view.findViewById(R.id.edit_text_1);
                            EditText editText2 = (EditText) view.findViewById(R.id.edit_text_2);

                            passParameters(editText1.getText().toString(), editText2.getText().toString());

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.no_dialog, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
        } else if (mKey.equals(mContext.getResources().getString(R.string.protocol))) {
            final View view = mInflater.inflate(R.layout.dialog_text, null);
            builder.setView(view)
                    .setMessage("Choose protocol and input port number (1 ~ 65535)")
                    .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // two check options
                            // one number picker

                            //passParameters(editText1.getText().toString(), editText2.getText().toString());

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.no_dialog, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
        }
    }

    private void makeChecker(AlertDialog.Builder builder) {
        // report
        // four check options
    }

    private void makeTimePicker(AlertDialog.Builder builder) {
        // set time
    }

    private void passParameters(String... args) {
        mListener.onDialogValueChanged(mKey, args);
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    public interface dialogBuilderListener {
        void onDialogValueChanged(String key, String... params);
    }
}
