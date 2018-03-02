package com.aeza.dailytaskchecker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;


/**
 * Created by Seyyed Alireza Jamali on 1/24/2018.
 */

public class Configurations extends AppCompatActivity {
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    LinearLayout inputsWrapper;

    public Configurations context;

    boolean language;
    boolean isDoubleTap;
    boolean isStatic;
    boolean isStack;

    static final String INPUTS_DELIMITER = "D73t9d";
    static final String ORANGE_STATE = "D73t8d";
    static final String GREEN_STATE = "D73t7d";
    static final String TIME_STATE = "D73t6d";
    static final String NO_STATE = "D73t0d";
    static final String ORANGE_COLOR = "#FFFF6F00";
    static final String GREEN_COLOR = "#FF8BC34A";
    static final String RED_COLOR = "#FFE53935";
    static final String TIME_COLOR = "#FFE040FB";

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

        inputsWrapper = findViewById(R.id.inputs_wrapper);

        SeekBar seeker = findViewById(R.id.seeker_bar);
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
        Switch languageSw = findViewById(R.id.language_switch);
        languageSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    language = true;
                    ((Switch) findViewById(R.id.two_step_switch)).setText(R.string.en_two_step_confirm);
                    ((Switch) findViewById(R.id.stack_switch)).setText(R.string.en_stack);
                    ((Switch) findViewById(R.id.time_switch)).setText(R.string.en_time_based);
                    ((Button) findViewById(R.id.cancel)).setText(R.string.en_cancel);
                    ((Button) findViewById(R.id.confirm)).setText(R.string.en_confirm);
                    ((RadioButton) findViewById(R.id.radioButton1)).setText(R.string.en_dynamic);
                    ((RadioButton) findViewById(R.id.radioButton2)).setText(R.string.en_static);
                    ((EditText) findViewById(R.id.time_start_text)).setHint(R.string.en_time_start_text);
                    ((EditText) findViewById(R.id.time_deadline_text)).setHint(R.string.en_time_deadline_text);
                    prefs.edit().putBoolean(mAppWidgetId + "language", true).apply();
                } else {
                    language = false;
                    ((Switch) findViewById(R.id.two_step_switch)).setText(R.string.fa_two_step_confirm);
                    ((Switch) findViewById(R.id.stack_switch)).setText(R.string.fa_stack);
                    ((Switch) findViewById(R.id.time_switch)).setText(R.string.fa_time_based);
                    ((Button) findViewById(R.id.cancel)).setText(R.string.fa_cancel);
                    ((Button) findViewById(R.id.confirm)).setText(R.string.fa_confirm);
                    ((RadioButton) findViewById(R.id.radioButton1)).setText(R.string.fa_dynamic);
                    ((RadioButton) findViewById(R.id.radioButton2)).setText(R.string.fa_static);
                    ((EditText) findViewById(R.id.time_start_text)).setHint(R.string.fa_time_start_text);
                    ((EditText) findViewById(R.id.time_deadline_text)).setHint(R.string.fa_time_deadline_text);
                    prefs.edit().putBoolean(mAppWidgetId + "language", false).apply();
                }
            }
        });

        Switch timeSw = findViewById(R.id.time_switch);
        timeSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    findViewById(R.id.time_start_text).setVisibility(View.VISIBLE);
                    findViewById(R.id.time_deadline_text).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.time_start_text).setVisibility(View.GONE);
                    findViewById(R.id.time_deadline_text).setVisibility(View.GONE);
                }
                prefs.edit().putBoolean(mAppWidgetId + "isTime", isChecked).apply();
            }
        });
    }

    private String[] removeEmptyStringsFromArray(String[] inputs) {
        String result = "";
        for(String in : inputs) {
            result += in.substring(6).isEmpty() ? "" : in + INPUTS_DELIMITER;
        }
        inputs = result.split(INPUTS_DELIMITER);
        return inputs;
    }

    public void confirm(View view) {
        LinearLayout inputsWrapper = findViewById(R.id.inputs_wrapper);
        String[] inputs = new String[inputsWrapper.getChildCount()];

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, Provider.class));
        for (int i = 0; i < inputsWrapper.getChildCount(); i++) {
            EditText input = ((EditText) inputsWrapper.getChildAt(i));
            inputs[i] = NO_STATE + input.getText().toString();
            for (int id : widgetIds) {
                for (String name : prefs.getString(id + "", "").split(INPUTS_DELIMITER)) {
                    if (inputs[i].equals(name)) {
                        input.setError(getString(language ? R.string.en_name_already_in_use : R.string.fa_name_already_in_use));
                        return;
                    }
                    if (inputs[i].substring(6).isEmpty()) {
                        input.setError(getString(language ? R.string.en_cant_be_empty : R.string.fa_cant_be_empty));
                        return;
                    }
                }
            }

        }
        inputs = removeEmptyStringsFromArray(inputs);
        prefs.edit().putString(mAppWidgetId + "", TextUtils.join(INPUTS_DELIMITER, inputs)).apply();

        RadioGroup radioGroup = findViewById(R.id.config_radioGroup);
        int checkedRadioBtn = radioGroup.getCheckedRadioButtonId();
        isStatic = checkedRadioBtn == R.id.radioButton2;
        prefs.edit().putBoolean(mAppWidgetId + "isStatic", isStatic).apply();

        Switch doubleTapSw = findViewById(R.id.two_step_switch);
        isDoubleTap = doubleTapSw.isChecked();
        prefs.edit().putBoolean(mAppWidgetId + "isDoubleTap", isDoubleTap).apply();

        Switch stackSw = findViewById(R.id.stack_switch);
        isStack = stackSw.isChecked();
        prefs.edit().putBoolean(mAppWidgetId + "isStack", isStack).apply();

        boolean isTime = prefs.getBoolean(mAppWidgetId + "isTime", false);

        EditText timeStartText = findViewById(R.id.time_start_text);
        String startText = timeStartText.getText().toString();
        if (isTime && (startText.isEmpty() || Integer.valueOf(startText.substring(0, 2)) > 23)) {
            startText = "06:00";
            Toast.makeText(context, language ? getString(R.string.en_start_time_toast_dialog) + " " + startText :getString(R.string.fa_start_time_toast_dialog) + " " + startText, Toast.LENGTH_LONG).show();
        }
        prefs.edit().putString(mAppWidgetId + "timeStartText", startText).apply();

        EditText timeDeadlineText = findViewById(R.id.time_deadline_text);
        String deadlineText = timeDeadlineText.getText().toString();
        if (isTime && (deadlineText.isEmpty() || Integer.valueOf(deadlineText.substring(0, 2)) > 23)) {
            deadlineText = "18:00";
            Toast.makeText(context, language ? getString(R.string.en_deadline_time_toast_dialog) + " " + deadlineText :getString(R.string.fa_deadline_time_toast_dialog) + " " + deadlineText, Toast.LENGTH_LONG).show();
        }
        prefs.edit().putString(mAppWidgetId + "timeDeadlineText", deadlineText).apply();

        initAppWidgets(context, appWidgetManager, mAppWidgetId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    public void cancel(View view) {
        onBackPressed();
    }

    static int getColorFromState(String state) {
        switch (state) {
            case NO_STATE:
                return Color.BLACK;
            case GREEN_STATE:
                return Color.parseColor(GREEN_COLOR);
            case ORANGE_STATE:
                return Color.parseColor(ORANGE_COLOR);
            case TIME_STATE:
                return Color.parseColor(TIME_COLOR);
            default:
                return Color.BLACK;
        }
    }
    public static RemoteViews initAppWidgets(Context context, final AppWidgetManager appWidgetManager,
                                      final int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + ".prefs", Context.MODE_PRIVATE);
        String[] data = prefs.getString(appWidgetId + "", "").split(INPUTS_DELIMITER);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
        for (int currentBtnIndex = 0; currentBtnIndex < data.length; currentBtnIndex++) {
            final int btnId = R.id.btn_10 + currentBtnIndex;


            views.setTextViewText(btnId, data[currentBtnIndex].substring(6));
            views.setTextColor(btnId, getColorFromState(data[currentBtnIndex].substring(0, 6)));
            views.setViewVisibility(btnId, View.VISIBLE);

            boolean isTime = prefs.getBoolean(appWidgetId + "isTime", false);
            if (isTime) {
                boolean isAllGreen = true;
                for (String name : data) {
                    if (!name.startsWith(GREEN_STATE)) {
                        isAllGreen = false;
                        break;
                    }
                }
                try {
                    String timeStartText = prefs.getString(appWidgetId + "timeStartText", "06:00");
                    String timeDeadlineText = prefs.getString(appWidgetId + "timeDeadlineText", "18:00");
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Calendar calendar = Calendar.getInstance();
                    Date now = sdf.parse(calendar.get(HOUR_OF_DAY) + ":" + calendar.get(MINUTE));
                    if (!isAllGreen) {
                        if (now.after(sdf.parse(timeStartText))) {
                            boolean hasTimeState = false;
                            for (int k = 0; k < data.length; k++) {
                                if (data[k].startsWith(TIME_STATE)) {
                                    hasTimeState = true;
                                    if (now.after(sdf.parse(timeDeadlineText))) {
                                        views.setTextColor(R.id.btn_10 + k, Color.parseColor(RED_COLOR));
                                        data[k] = TIME_STATE + data[k].substring(6);
                                        boolean lan = prefs.getBoolean(appWidgetId + "language", false);

                                        NotificationCompat.Builder mBuilder =
                                                new NotificationCompat.Builder(context)
                                                        .setSmallIcon(R.drawable.ic_stat_name)
                                                        .setContentTitle(lan ? context.getString(R.string.en_missed_task) : context.getString(R.string.fa_missed_task))
                                                        .setContentText(lan ? data[k].substring(6) + " " + context.getString(R.string.en_isnt_checked) : data[k].substring(6));
                                        NotificationManager mNotificationManager =
                                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                        assert mNotificationManager != null;
                                        mNotificationManager.notify(appWidgetId, mBuilder.build());
                                    }
                                    break;
                                }
                            }
                            boolean isTimeTaskChecked = prefs.getBoolean(appWidgetId + "isTimeTaskChecked", false);
                            if (!hasTimeState && !isTimeTaskChecked) {
                                int j = 0;
                                boolean skipLastBtn = false;
                                while (data[j].startsWith(GREEN_STATE)) {
                                    if (j < data.length - 1) {
                                        j++;
                                    } else {
                                        skipLastBtn = true;
                                        break;
                                    }
                                }
                                if (!skipLastBtn) {
                                    views.setTextColor(R.id.btn_10 + j, Color.parseColor(TIME_COLOR));
                                    data[j] = TIME_STATE + data[j].substring(6);
                                }
                            }
                        } else {
                            prefs.edit().putBoolean(appWidgetId + "isTimeTaskChecked", false).apply();
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                prefs.edit().putString(appWidgetId + "", TextUtils.join(INPUTS_DELIMITER, data)).apply();
            }

            Intent btnIntent = new Intent(context, Provider.class);
            btnIntent.putExtra("widgetId", appWidgetId);
            btnIntent.putExtra("currentBtnIndex", currentBtnIndex);
            btnIntent.putExtra("btnId", btnId);
            int pendingIntentCode = Integer.valueOf(appWidgetId + "" + currentBtnIndex);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    pendingIntentCode , btnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(btnId, pendingIntent);

        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
        return views;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Seyyed.Alireza.Jamali@gmail.com", Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Telegram: @aeza90", Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }
}
