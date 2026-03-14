package io.github.romanvht.byedpi.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.romanvht.byedpi.R
import io.github.romanvht.byedpi.activities.ToggleActivity

abstract class BaseToggleWidget : AppWidgetProvider() {
    abstract val layoutRes: Int

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, layoutRes)

            val toggleIntent = Intent(context, ToggleActivity::class.java)
            val togglePending = PendingIntent.getActivity(
                context,
                appWidgetId,
                toggleIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widgetToggle, togglePending)

            if (layoutRes == R.layout.widget_toggle_large) {
                val startIntent = Intent(context, ToggleActivity::class.java).putExtra("only_start", true)
                val stopIntent = Intent(context, ToggleActivity::class.java).putExtra("only_stop", true)
                val startPending = PendingIntent.getActivity(
                    context,
                    appWidgetId + 1000,
                    startIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                val stopPending = PendingIntent.getActivity(
                    context,
                    appWidgetId + 2000,
                    stopIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                views.setOnClickPendingIntent(R.id.widgetStart, startPending)
                views.setOnClickPendingIntent(R.id.widgetStop, stopPending)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
