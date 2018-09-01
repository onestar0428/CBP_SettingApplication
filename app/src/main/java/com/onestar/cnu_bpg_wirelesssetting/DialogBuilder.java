package com.onestar.cnu_bpg_wirelesssetting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class DialogBuilder {
    private static Context mContext;
    private static LayoutInflater mInflater;
    private static DialogBuilder.dialogBuilderListener mListener;
    private String mKey = "";

    //TODO: static factory or singleton
    public DialogBuilder(Context context, DialogBuilder.dialogBuilderListener listener) {
        mContext = context;
        mListener = listener;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void makeDialog(String key) {
        mKey = key;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(key);

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
        final View view = mInflater.inflate(R.layout.dialog_switch, null);

        //TODO: set default

        builder.setView(view)
                .setTitle("ON/OFF the elements")
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Switch pressure1Switch = (Switch) view.findViewById(R.id.switch_pressure1);
                        Switch pressure2Switch = (Switch) view.findViewById(R.id.switch_pressure2);
                        Switch rgbSwitch = (Switch) view.findViewById(R.id.switch_rgb);
                        Switch irySwitch = (Switch) view.findViewById(R.id.switch_iry);
                        Switch accgyroSwitch = (Switch) view.findViewById(R.id.switch_accgyro);
                        Switch timestampSwitch = (Switch) view.findViewById(R.id.switch_timestamp);

                        passParameters(boolToInt(pressure1Switch.isChecked()) + ":"
                                + boolToInt(pressure2Switch.isChecked()) + ":" + boolToInt(rgbSwitch.isChecked())
                                + ":" + boolToInt(irySwitch.isChecked()) + ":" + boolToInt(accgyroSwitch.isChecked())
                                + ":" + boolToInt(timestampSwitch.isChecked()));

                        dialog.dismiss();
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

    private void makeNumberPickers(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.dialog_numpicks, null);

        // TODO: set default & max value is strange
        final NumberPicker redPicker = (NumberPicker) view.findViewById(R.id.numberPicker_red);
        redPicker.setMinValue(0);
        redPicker.setMaxValue(100);
        final NumberPicker greenPicker = (NumberPicker) view.findViewById(R.id.numberPicker_green);
        greenPicker.setMinValue(0);
        greenPicker.setMaxValue(100);
        final NumberPicker bluePicker = (NumberPicker) view.findViewById(R.id.numberPicker_blue);
        bluePicker.setMinValue(0);
        bluePicker.setMaxValue(100);
        final NumberPicker yellowPicker = (NumberPicker) view.findViewById(R.id.numberPicker_yellow);
        yellowPicker.setMinValue(0);
        yellowPicker.setMaxValue(100);
        final NumberPicker irPicker = (NumberPicker) view.findViewById(R.id.numberPicker_ir);
        irPicker.setMinValue(0);
        irPicker.setMaxValue(100);

        builder.setView(view)
                .setTitle("Set mA of each LED")
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        passParameters(redPicker.getValue() + ":" + greenPicker.getValue() + ":" +
                                bluePicker.getValue() + ":" + yellowPicker.getValue() + ":" +
                                irPicker.getValue());

                        dialog.dismiss();
                    }
                });
    }

    private void makeNumberPicker(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.dialog_numpick, null);

        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);

        //TODO: set default
        builder.setView(view);
        if (mKey.equals(mContext.getResources().getString(R.string.reboot))) {
            numberPicker.setMaxValue(60); // 1 min
            numberPicker.setMinValue(0);
            builder.setTitle("Input Reboot Delay (0 ~ )s");
        } else if (mKey.equals(mContext.getResources().getString(R.string.freq))) {
            numberPicker.setMaxValue(500);
            numberPicker.setMinValue(80);
            builder.setTitle("Input FREQUENCY ( ~ )Hz");
        }

        builder.setPositiveButton(mContext.getResources().getString(R.string.yes_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                passParameters(numberPicker.getValue() + "");
                dialog.dismiss();
            }
        });
    }

    private void makeEditText(AlertDialog.Builder builder) {
        //TODO: set default

        if (mKey.equals(mContext.getResources().getString(R.string.wifi))) {
            final View view = mInflater.inflate(R.layout.dialog_text, null);
            builder.setView(view)
                    .setTitle("Input Wi-Fi information")
                    .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //TODO: TextView1
                            //TODD: TextView2
                            EditText editText1 = (EditText) view.findViewById(R.id.edit_text_1);
                            EditText editText2 = (EditText) view.findViewById(R.id.edit_text_2);

                            passParameters(editText1.getText().toString() + ":" + editText2.getText().toString());

                            dialog.dismiss();
                        }
                    });
        } else if (mKey.equals(mContext.getResources().getString(R.string.protocol))) {
            final View view = mInflater.inflate(R.layout.dialog_text, null);
            builder.setView(view)
                    .setTitle("Choose protocol and input port number (1 ~ 65535)")
                    .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // two check options
                            // one number picker

                            //passParameters(editText1.getText().toString(), editText2.getText().toString());

                            dialog.dismiss();
                        }
                    });
        }
    }

    private void makeChecker(AlertDialog.Builder builder) {
        final String[] reportList = {"console", "nw", "both", "none"}; //TODO: enum?

        //TODO: set default

        builder.setSingleChoiceItems(R.array.report_to, 0, null)
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        passParameters(reportList[position] + "");
                        dialog.dismiss();
                    }
                });
    }

    private void makeTimePicker(AlertDialog.Builder builder) {
        final View view = mInflater.inflate(R.layout.dialog_time, null);

        builder.setTitle("Pick the date and time")
                .setSingleChoiceItems(R.array.report_to, 0, null)
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
                        TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);

                        //yyyy:m:dd:hh:mm:ss
                        //cannot support 0 for front space..

                        //String params = datePicker.getYear() + ":" + datePicker.getMonth() + ":" + datePicker.getDayOfMonth() + ":" +
                        //        timePicker.getHour() + ":" + timePicker.getMinute() + ":" + 0;

                        //TODO: handling seconds (timepicker doesn't support I see) and others either
                        passParameters("");
                        dialog.dismiss();
                    }
                });
    }

    private void passParameters(String params) {
        mListener.onDialogValueChanged(mKey, params);
    }

    public interface dialogBuilderListener {
        void onDialogValueChanged(String key, String params);
    }
}
