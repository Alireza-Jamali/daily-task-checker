package com.aeza.simpletaskchecker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Switch;

import java.util.ArrayList;


/**
 * Created by Seyyed Alireza Jamali on 1/24/2018.
 */

public class Configurations extends AppCompatActivity {
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    LinearLayout inputsWrapper;

    public Configurations context;
    public static boolean language;
    final public static String INPUTS_DELIMITER = "D73t9d";
    final public static String ORANGE_STATE = "D73t8d";
    final public static String GREEN_STATE = "D73t7d";
    final public static String NO_STATE = "D73t0d";
    boolean isDoubleTap;
    boolean isStatic;
    boolean isStack;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.configs);
        context = Configurations.this;

        prefs = getSharedPreferences(getPackageName() + ".prefs", MODE_PRIVATE);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        inputsWrapper = (LinearLayout) findViewById(R.id.inputs_wrapper);

        SeekBar seeker = (SeekBar) findViewById(R.id.seeker_bar);
        seeker.setMax(10);
        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int childCount = inputsWrapper.getChildCount();
                progress -= childCount;
                if (progress != -1) {
                    for (int i = 0; i < progress; i++) {
                        EditText editText = new EditText(inputsWrapper.getContext());
                        editText.setHint(language ? R.string.en_name : R.string.fa_name);
                        editText.setBackgroundColor(Color.TRANSPARENT);
                        inputsWrapper.addView(editText);
                    }
                } else {
                    inputsWrapper.removeViewAt(childCount - 1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() != inputsWrapper.getChildCount()) {
                    int progress = seekBar.getProgress();
                    while (progress != inputsWrapper.getChildCount()) {
                        inputsWrapper.removeViewAt(inputsWrapper.getChildCount() - 1);
                    }
                }
            }
        });
        Switch languageSw = (Switch) findViewById(R.id.language_switch);
        languageSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    language = true;
                    ((Switch) findViewById(R.id.two_step_switch)).setText(R.string.en_two_step_confirm);
                    ((Switch) findViewById(R.id.stack_switch)).setText(R.string.en_stack);
                    ((Button) findViewById(R.id.cancel)).setText(R.string.en_cancel);
                    ((Button) findViewById(R.id.confirm)).setText(R.string.en_confirm);
                    ((RadioButton) findViewById(R.id.radioButton1)).setText(R.string.en_dynamic);
                    ((RadioButton) findViewById(R.id.radioButton2)).setText(R.string.en_static);
                } else {
                    language = false;
                    ((Switch) findViewById(R.id.two_step_switch)).setText(R.string.fa_two_step_confirm);
                    ((Switch) findViewById(R.id.stack_switch)).setText(R.string.fa_stack);
                    ((Button) findViewById(R.id.cancel)).setText(R.string.fa_cancel);
                    ((Button) findViewById(R.id.confirm)).setText(R.string.fa_confirm);
                    ((RadioButton) findViewById(R.id.radioButton1)).setText(R.string.fa_dynamic);
                    ((RadioButton) findViewById(R.id.radioButton2)).setText(R.string.fa_static);
                }
            }
        });
    }

    public void confirm(View view) {
        LinearLayout inputsWrapper = (LinearLayout) findViewById(R.id.inputs_wrapper);
        String[] inputs = new String[inputsWrapper.getChildCount()];

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, Provider.class));
        for (int i = 0; i < inputsWrapper.getChildCount(); i++) {
            EditText input = ((EditText) inputsWrapper.getChildAt(i));
            inputs[i] = NO_STATE + input.getText().toString();
            for (int id : widgetIds) {
                for (String name : prefs.getString(id + "", "").split(INPUTS_DELIMITER)) {
                    if (inputs[i].equals(name)) {
                        input.setError("این نام قبلا استفاده شده است");
                        return;
                    }
                }
            }
        }

        prefs.edit().putString(mAppWidgetId + "", TextUtils.join(INPUTS_DELIMITER, inputs)).apply();

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.config_radioGroup);
        int checkedRadioBtn = radioGroup.getCheckedRadioButtonId();
        isStatic = checkedRadioBtn == R.id.radioButton2;
        prefs.edit().putBoolean(mAppWidgetId + "isStatic", isStatic).apply();

        Switch doubleTapSw = (Switch) findViewById(R.id.two_step_switch);
        isDoubleTap = doubleTapSw.isChecked();
        prefs.edit().putBoolean(mAppWidgetId + "isDoubleTap", isDoubleTap).apply();

        Switch stackSw = (Switch) findViewById(R.id.stack_switch);
        isStack = stackSw.isChecked();
        prefs.edit().putBoolean(mAppWidgetId + "isStack", isStack).apply();

        createAppWidget(context, appWidgetManager, mAppWidgetId, inputs);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    public void cancel(View view) {
        onBackPressed();
    }


    void createAppWidget(Context context, final AppWidgetManager appWidgetManager,
                         final int appWidgetId, final String[] inputs) {

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
        ArrayList<BroadcastReceiver> localBrList = new ArrayList<>();
        for (int i = 0; i < inputs.length; i++) {
            final int currentBtnIndex = i;
            prefs.edit().putInt(inputs[i] + "currentBtnIndex", currentBtnIndex).apply();
            final int btnId = R.id.btn1 + i;
            prefs.edit().putInt(inputs[i] + "btnId", btnId).apply();
            views.setTextViewText(btnId, inputs[i].substring(6));
            views.setViewVisibility(btnId, View.VISIBLE);

            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String[] data = prefs.getString(appWidgetId + "", "").split(INPUTS_DELIMITER);
                    String btnName = data[currentBtnIndex];
                    switch (btnName.substring(0, 6)) {
                        case NO_STATE: {
                            if (!isStack) {
                                views.setTextColor(btnId, Color.parseColor(isDoubleTap ? "#FFFF6F00" : "#FF8BC34A"));
                                String newBtnName = (isDoubleTap ? ORANGE_STATE : GREEN_STATE) + btnName.substring(6);
                                views.setTextViewText(btnId, newBtnName.substring(6));
                                data[currentBtnIndex] = newBtnName;
                            } else {
                                if (isDoubleTap) {
                                    String temp = data[currentBtnIndex];
                                    for (int k = currentBtnIndex; k > 0; k--) {
                                        data[k] = data[k - 1];
                                    }
                                    data[0] = ORANGE_STATE + temp.substring(6);
                                } else {
                                    int moveUpTurns = (data.length - 1) - currentBtnIndex;
                                    String temp = data[currentBtnIndex];
                                    for (int k = 0; k < moveUpTurns; k++) {
                                        data[currentBtnIndex + k] = data[currentBtnIndex + k + 1];
                                    }
                                    data[data.length - 1] = GREEN_STATE + temp.substring(6);
                                }
                                for (int i = 0; i < data.length; i++) {
                                    int btnId = R.id.btn1 + i;
                                    views.setTextViewText(btnId, data[i].substring(6));
                                    views.setTextColor(btnId,
                                            data[i].startsWith(NO_STATE) ? Color.BLACK :
                                                    data[i].startsWith(GREEN_STATE) ?
                                                            Color.parseColor("#FF8BC34A") : Color.parseColor("#FFFF6F00")
                                    );
                                }
                            }
                            prefs.edit().putString(appWidgetId + "", TextUtils.join(INPUTS_DELIMITER, data)).apply();
                        }
                        break;
                        case ORANGE_STATE: {
                            views.setTextColor(btnId, Color.parseColor("#FF8BC34A"));
                            String newBtnName = GREEN_STATE + btnName.substring(6);
                            views.setTextViewText(btnId, newBtnName.substring(6));
                            data[currentBtnIndex] = newBtnName;

                            if (isStack) {
                                int moveUpTurns = (data.length - 1) - currentBtnIndex;
                                String temp = data[currentBtnIndex];
                                for (int k = 0; k < moveUpTurns; k++) {
                                    data[currentBtnIndex + k] = data[currentBtnIndex + k + 1];
                                }
                                data[data.length - 1] = GREEN_STATE + temp.substring(6);
                                for (int i = 0; i < data.length; i++) {
                                    int btnId = R.id.btn1 + i;
                                    views.setTextViewText(btnId, data[i].substring(6));
                                    views.setTextColor(btnId,
                                            data[i].startsWith(NO_STATE) ? Color.BLACK :
                                                    data[i].startsWith(GREEN_STATE) ?
                                                            Color.parseColor("#FF8BC34A") : Color.parseColor("#FFFF6F00")
                                    );
                                }
                            }

                            prefs.edit().putString(appWidgetId + "", TextUtils.join(INPUTS_DELIMITER, data)).apply();
                        }
                        break;
                        case GREEN_STATE: {
                            views.setTextColor(btnId, isStatic ? Color.parseColor("#FF8BC34A") : Color.BLACK);
                            String newBtnName = (isStatic ? GREEN_STATE : NO_STATE) + btnName.substring(6);
                            views.setTextViewText(btnId, newBtnName.substring(6));
                            data[currentBtnIndex] = newBtnName;
                            prefs.edit().putString(appWidgetId + "", TextUtils.join(INPUTS_DELIMITER, data)).apply();

                            if (isStatic) {
                                boolean isAllGreen = true;
                                for (String name : data) {
                                    if (!name.startsWith(GREEN_STATE)) {
                                        isAllGreen = false;
                                    }
                                }
                                if (isAllGreen && data.length > 0) {
                                    for (int i = 0; i < data.length; i++) {
                                        int btnId = R.id.btn1 + i;
                                        String btn = NO_STATE + data[i].substring(6);
                                        views.setTextViewText(btnId, btn.substring(6));
                                        views.setTextColor(btnId, Color.BLACK);
                                        data[i] = btn;
                                    }
                                    prefs.edit().putString(appWidgetId + "", TextUtils.join(INPUTS_DELIMITER, data)).apply();
                                }
                            }
                        }
                        break;
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }

            };

            localBrList.add(broadcastReceiver);

            Intent btnIntent = new Intent(inputs[i]);
            context.getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter(inputs[i]));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                    appWidgetId, btnIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(btnId, pendingIntent);

        }
        Provider.broadCastList.put(appWidgetId, localBrList);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }


}
