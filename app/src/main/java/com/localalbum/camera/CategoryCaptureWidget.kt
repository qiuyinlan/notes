package com.localalbum.camera

import com.qiuyinlan.suixinji.R
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

internal object CategoryCaptureWidget {
    private const val PREFS = "category_capture_widgets"

    fun saveCategory(context: Context, widgetId: Int, categoryName: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("category_$widgetId", categoryName).apply()
    }

    fun update(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val categories = CategoryStore.load(context)
        val views = RemoteViews(context.packageName, R.layout.category_capture_widget)
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val categoryName = prefs.getString("category_$widgetId", null)
            ?.takeIf { name -> categories.any { it.name == name } }
            ?: categories.firstOrNull()?.name
            ?: context.getString(R.string.widget_default_category)
        views.setTextViewText(R.id.widget_category_action, context.getString(R.string.widget_category_action, categoryName))
        views.setOnClickPendingIntent(R.id.widget_category_action, pendingIntent(context, widgetId, WIDGET_ACTION_CAMERA, categoryName))
        manager.updateAppWidget(widgetId, views)
    }

    fun updateAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, CategoryCaptureWidgetProvider::class.java))
        ids.forEach { update(context, manager, it) }
    }

    fun deleteCategories(context: Context, widgetIds: IntArray) {
        val editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        widgetIds.forEach { editor.remove("category_$it") }
        editor.apply()
    }

    private fun pendingIntent(context: Context, widgetId: Int, action: String, categoryName: String? = null): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(WIDGET_ACTION_EXTRA, action)
            if (categoryName != null) putExtra(WIDGET_CATEGORY_EXTRA, categoryName)
            data = Uri.parse("takephoto-widget://$widgetId/$action")
        }
        return PendingIntent.getActivity(context, widgetId * 10 + action.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}

class CategoryCaptureWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { CategoryCaptureWidget.update(context, appWidgetManager, it) }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        CategoryCaptureWidget.deleteCategories(context, appWidgetIds)
    }
}

class MediaOrganizerWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { widgetId ->
            val views = RemoteViews(context.packageName, R.layout.media_organizer_widget)
            val photos = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(WIDGET_ACTION_EXTRA, WIDGET_ACTION_PHOTOS)
                data = Uri.parse("takephoto-organizer://$widgetId/photos")
            }
            val videos = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(WIDGET_ACTION_EXTRA, WIDGET_ACTION_VIDEOS)
                data = Uri.parse("takephoto-organizer://$widgetId/videos")
            }
            views.setOnClickPendingIntent(R.id.widget_photos_action, PendingIntent.getActivity(context, widgetId * 10 + 1, photos, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            views.setOnClickPendingIntent(R.id.widget_videos_action, PendingIntent.getActivity(context, widgetId * 10 + 2, videos, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            manager.updateAppWidget(widgetId, views)
        }
    }
}
