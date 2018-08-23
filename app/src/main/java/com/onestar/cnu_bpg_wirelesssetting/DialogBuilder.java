package com.onestar.cnu_bpg_wirelesssetting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class DialogBuilder implements DialogInterface.OnClickListener, DialogInterface.OnMultiChoiceClickListener, NumberPicker.OnValueChangeListener {
    private static Context mContext;
    private final static LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    private static DialogBuilder.dialogBuilderListener mListener;
    private String mKey = "";

    public DialogBuilder(Context context, DialogBuilder.dialogBuilderListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void makeDialog(String key) {
        mKey = key;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(key);
        mBuilder.setNeutralButton(mContext.getResources().getString(R.string.close_dialog), this);

        if (key.equals(mContext.getResources().getString(R.string.freq))) {
            makeNumberPicker(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.led))) {
        } else if (key.equals(mContext.getResources().getString(R.string.target))) {
        } else if (key.equals(mContext.getResources().getString(R.string.report_to))) {
        } else if (key.equals(mContext.getResources().getString(R.string.wifi))) {
        } else if (key.equals(mContext.getResources().getString(R.string.protocol))) {
        } else if (key.equals(mContext.getResources().getString(R.string.set_time))) {
        } else if (key.equals(mContext.getResources().getString(R.string.wifi))) {
            makeEditText(mBuilder);
        } else if (key.equals(mContext.getResources().getString(R.string.reboot))) {
            makeNumberPicker(mBuilder);
        }

        mBuilder.create().show();
    }

    private void makeMultipleOptions(AlertDialog.Builder builder, String key) {
        if (key.equals(mContext.getResources().getString(R.string.target))) {
            builder.setMultiChoiceItems(R.array.target, null, this);
        } else if (key.equals(mContext.getResources().getString(R.string.report_to))) {
            builder.setMultiChoiceItems(R.array.report_to, null, this);
        }
        builder.setTitle("ON/OFF by checking options")
                .setPositiveButton(mContext.getResources().getString(R.string.yes_dialog), this)
                .setNegativeButton(mContext.getResources().getString(R.string.no_dialog), this);
    }

    private void makeNumberPicker(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.layout_dialog_num, null);

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
                ;
                passParameters(np.getValue() + "");

                dialog.dismiss();
            }
        });
    }

    private void makeEditText(AlertDialog.Builder builder) {
        // ssid, pw
        final View view = mInflater.inflate(R.layout.layout_dialog_text, null);

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

        // protocol, port
    }

    private void makeTimePicker(AlertDialog.Builder builder) {
        // set time
    }

    private void passParameters(String... args) {
        mListener.onDialogValueChanged(mKey, args);
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        // if positive button click
        //     sendCommand()
        // else negative button click
        //     just finish dialog
    }

    @Override
    public void onClick(DialogInterface dialog, int which,
                        boolean isChecked) {
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    public interface dialogBuilderListener {
        void onDialogValueChanged(String key, String... params);
    }
}
