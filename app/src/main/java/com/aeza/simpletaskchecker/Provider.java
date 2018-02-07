package com.aeza.simpletaskchecker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.HashMap;
import static com.aeza.simpletaskchecker.Configurations.*;

/**
 * Created by Seyyed Alireza Jamali on 1/24/2018.
 */

public class Provider extends AppWidgetProvider {

    static HashMap<Integer, ArrayList<BroadcastReceiver>> broadCastList = new HashMap<>();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + ".prefs", MODE_PRIVATE);
        for(int id : appWidgetIds) {
            if (prefs.contains(id + "")) {
                String[] inputs = prefs.getString(id + "", "").split(INPUTS_DELIMITER);
                updateAppWidget(context, appWidgetManager, id, inputs);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int id : appWidgetIds) {
            ArrayList<BroadcastReceiver> list = broadCastList.get(id);
            if(list != null) {
                for (BroadcastReceiver b : list) {
                    context.unregisterReceiver(b);
                }
            }
            context.getSharedPreferences(context.getPackageName() + ".prefs", Context.MODE_PRIVATE)
                    .edit().remove(id + "").apply();
        }
    }

    private void updateAppWidget(Context context, final AppWidgetManager appWidgetManager,
                         final int appWidgetId, String[] inputs) {

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
        ArrayList<BroadcastReceiver> localBrList = new ArrayList<>();
        for (int i = 0; i < inputs.length; i++) {
            int btnId = R.id.btn1 + i;
            views.setTextViewText(btnId, inputs[i].substring(6));
            views.setTextColor(btnId,
                    inputs[i].startsWith(NO_STATE) ? Color.BLACK :
                            inputs[i].startsWith(GREEN_STATE) ?
                                    Color.parseColor("#FF8BC34A") : Color.parseColor("#FFFF6F00")
            );
            views.setViewVisibility(btnId, View.VISIBLE);

            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + ".prefs", MODE_PRIVATE);
                    boolean isDoubleTap = prefs.getBoolean(appWidgetId + "isDoubleTap", false);
                    boolean isStatic = prefs.getBoolean(appWidgetId + "isStatic", false);
                    boolean isStack = prefs.getBoolean(appWidgetId + "isStack", false);

                    String[] data = prefs.getString(appWidgetId + "", "").split(INPUTS_DELIMITER);
                    int currentBtnIndex = intent.getIntExtra("btn_index", 0);
                    int btnId = intent.getIntExtra("btn_id", 0);
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
                                    int tempBtnId = R.id.btn1 + i;
                                    views.setTextViewText(tempBtnId, data[i].substring(6));
                                    views.setTextColor(tempBtnId,
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
                                    int tempBtnId = R.id.btn1 + i;
                                    views.setTextViewText(tempBtnId, data[i].substring(6));
                                    views.setTextColor(tempBtnId,
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
                                        int tempBtnId = R.id.btn1 + i;
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
                }

            };

            localBrList.add(broadcastReceiver);

            Intent btnIntent = new Intent(inputs[i]);

            btnIntent.putExtra("btn_index", i);
            btnIntent.putExtra("btn_id", btnId);

            context.getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter(inputs[i]));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                    appWidgetId, btnIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(btnId, pendingIntent);

        }
        Provider.broadCastList.put(appWidgetId, localBrList);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }
}
