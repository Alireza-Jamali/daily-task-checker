package com.aeza.dailytaskchecker;

import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.aeza.dailytaskchecker.Configurations.GREEN_COLOR;
import static com.aeza.dailytaskchecker.Configurations.GREEN_STATE;
import static com.aeza.dailytaskchecker.Configurations.INPUTS_DELIMITER;
import static com.aeza.dailytaskchecker.Configurations.NO_STATE;
import static com.aeza.dailytaskchecker.Configurations.ORANGE_COLOR;
import static com.aeza.dailytaskchecker.Configurations.ORANGE_STATE;
import static com.aeza.dailytaskchecker.Configurations.TIME_STATE;
import static com.aeza.dailytaskchecker.Configurations.getColorFromState;
import static com.aeza.dailytaskchecker.Configurations.initAppWidgets;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;


/**
 * Created by Seyyed Alireza Jamali on 1/24/2018.
 */

public class Provider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + ".prefs", Context.MODE_PRIVATE);
        for (int id : appWidgetIds) {
            if (prefs.contains(id + "")) {
                initAppWidgets(context, appWidgetManager, id);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int id : appWidgetIds) {
            context.getSharedPreferences(context.getPackageName() + ".prefs", Context.MODE_PRIVATE)
                    .edit().remove(id + "").apply();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + ".prefs", Context.MODE_PRIVATE);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetId = intent.getIntExtra("widgetId", 0);

        if (!prefs.contains(appWidgetId + "")) {
            return;
        }
        if (intent.getAction() == null) {

            int currentBtnIndex = intent.getIntExtra("currentBtnIndex", 0);
            int btnId = intent.getIntExtra("btnId", 0);



            final RemoteViews views = initAppWidgets(context, appWidgetManager, appWidgetId);


            boolean isDoubleTap = prefs.getBoolean(appWidgetId + "isDoubleTap", false);
            boolean isStatic = prefs.getBoolean(appWidgetId + "isStatic", false);
            boolean isStack = prefs.getBoolean(appWidgetId + "isStack", false);

            String[] data = prefs.getString(appWidgetId + "", "").split(INPUTS_DELIMITER);
            String btnName = data[currentBtnIndex];
            switch (btnName.substring(0, 6)) {
                case TIME_STATE: {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Calendar calendar = Calendar.getInstance();
                    String timeStartText = prefs.getString(appWidgetId + "timeStartText", "06:00");
                    try {
                        Date now = sdf.parse(calendar.get(HOUR_OF_DAY) + ":" + calendar.get(MINUTE));
                        if (!now.before(sdf.parse(timeStartText))) {
                            data[currentBtnIndex] = NO_STATE + data[currentBtnIndex].substring(6);
                            prefs.edit().putBoolean(appWidgetId + "isTimeTaskChecked", true).apply();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    NotificationManager mNotificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    assert mNotificationManager != null;
                    mNotificationManager.cancel(appWidgetId);
                }
                case NO_STATE: {
                    if (isStack) {
                        if (isDoubleTap) {
                            String temp = data[currentBtnIndex];
                            System.arraycopy(data, 0, data, 1, currentBtnIndex);
                            data[0] = ORANGE_STATE + temp.substring(6);
                        } else {
                            int moveUpTurns = (data.length - 1) - currentBtnIndex;
                            String temp = data[currentBtnIndex];
                            System.arraycopy(data, currentBtnIndex + 1, data, currentBtnIndex, moveUpTurns);
                            data[data.length - 1] = GREEN_STATE + temp.substring(6);
                        }
                        for (int i = 0; i < data.length; i++) {
                            int tempBtnId = R.id.btn_10 + i;
                            views.setTextViewText(tempBtnId, data[i].substring(6));
                            views.setTextColor(tempBtnId, getColorFromState(data[i].substring(0, 6)));
                        }
                    } else {
                        views.setTextColor(btnId, Color.parseColor(isDoubleTap ? ORANGE_COLOR : GREEN_COLOR));
                        String newBtnName = (isDoubleTap ? ORANGE_STATE : GREEN_STATE) + btnName.substring(6);
                        views.setTextViewText(btnId, newBtnName.substring(6));
                        data[currentBtnIndex] = newBtnName;
                    }
                    prefs.edit().putString(appWidgetId + "", TextUtils.join(INPUTS_DELIMITER, data)).apply();
                }
                break;
                case ORANGE_STATE: {
                    views.setTextColor(btnId, Color.parseColor(GREEN_COLOR));
                    String newBtnName = GREEN_STATE + btnName.substring(6);
                    views.setTextViewText(btnId, newBtnName.substring(6));
                    data[currentBtnIndex] = newBtnName;
                    if (isStack) {
                        int moveUpTurns = (data.length - 1) - currentBtnIndex;
                        String temp = data[currentBtnIndex];
                        System.arraycopy(data, currentBtnIndex + 1, data, currentBtnIndex, moveUpTurns);
                        data[data.length - 1] = GREEN_STATE + temp.substring(6);
                        for (int i = 0; i < data.length; i++) {
                            int tempBtnId = R.id.btn_10 + i;
                            views.setTextViewText(tempBtnId, data[i].substring(6));
                            views.setTextColor(tempBtnId, getColorFromState(data[i].substring(0, 6)));
                        }
                    }
                    prefs.edit().putString(appWidgetId + "", TextUtils.join(INPUTS_DELIMITER, data)).apply();
                }
                break;
                case GREEN_STATE: {
                    views.setTextColor(btnId, isStatic ? Color.parseColor(GREEN_COLOR) : Color.BLACK);
                    String newBtnName = (isStatic ? GREEN_STATE : NO_STATE) + btnName.substring(6);
                    views.setTextViewText(btnId, newBtnName.substring(6));
                    data[currentBtnIndex] = newBtnName;
                    prefs.edit().putString(appWidgetId + "", TextUtils.join(INPUTS_DELIMITER, data)).apply();

                    if (isStatic) {
                        boolean isAllGreen = true;
                        for (String name : data) {
                            if (!name.startsWith(GREEN_STATE)) {
                                isAllGreen = false;
                                break;
                            }
                        }
                        if (isAllGreen && data.length > 0) {
                            for (int i = 0; i < data.length; i++) {
                                int tempBtnId = R.id.btn_10 + i;
                                String btn = NO_STATE + data[i].substring(6);
                                views.setTextViewText(tempBtnId, btn.substring(6));
                                views.setTextColor(tempBtnId, Color.BLACK);
                                data[i] = btn;
                            }
                            prefs.edit().putString(appWidgetId + "", TextUtils.join(INPUTS_DELIMITER, data)).apply();
                        }
                    }
                }
                break;
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } else {
            initAppWidgets(context, appWidgetManager, appWidgetId);
        }
    }
}
