package com.aeza.simpletaskchecker;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Seyyed Alireza Jamali on 1/24/2018.
 */

public class Provider extends AppWidgetProvider {

    static HashMap<Integer, ArrayList<BroadcastReceiver>> broadCastList = new HashMap<>();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for(int id : appWidgetIds) {

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
}
