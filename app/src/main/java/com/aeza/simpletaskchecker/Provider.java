package com.aeza.simpletaskchecker;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

import static com.aeza.simpletaskchecker.Configurations.initAppWidgets;

/**
 * Created by Seyyed Alireza Jamali on 1/24/2018.
 */

public class Provider extends AppWidgetProvider {

    static HashMap<Integer, ArrayList<BroadcastReceiver>> broadcastList = new HashMap<>();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + ".prefs", Context.MODE_PRIVATE);
        for (int id : appWidgetIds) {
            if (prefs.contains(id + "")) {
                ArrayList<BroadcastReceiver> list = broadcastList.get(id);
                if (list != null) {
                    for (BroadcastReceiver b : list) {
                        context.unregisterReceiver(b);
                    }
                }
                initAppWidgets(context, appWidgetManager, id);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int id : appWidgetIds) {
            ArrayList<BroadcastReceiver> list = broadcastList.get(id);
            if (list != null) {
                for (BroadcastReceiver b : list) {
                    context.unregisterReceiver(b);
                }
            }
            context.getSharedPreferences(context.getPackageName() + ".prefs", Context.MODE_PRIVATE)
                    .edit().remove(id + "").apply();
        }
    }


}
