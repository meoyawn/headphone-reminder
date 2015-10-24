package adeln.boilerplate

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import domain.intent.pendingRecorder

class RecorderWidget : AppWidgetProvider() {
  companion object {
    fun recorder(context: Context, pi: PendingIntent): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_record)
            .apply { setOnClickPendingIntent(R.id.btn_record, pi) }
  }

  override fun onUpdate(context: Context,
                        appWidgetManager: AppWidgetManager,
                        appWidgetIds: IntArray) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    appWidgetIds.forEach {
      appWidgetManager.updateAppWidget(it, recorder(context, pendingRecorder(context)))
    }
  }
}