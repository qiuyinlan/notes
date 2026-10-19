package com.qiuyinlan.suixinji

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object NoteWidgetContract {
    const val ACTION_NEW_NOTE = "com.qiuyinlan.suixinji.widget.NEW_NOTE"
    const val ACTION_OPEN_NOTE = "com.qiuyinlan.suixinji.widget.OPEN_NOTE"
    const val ACTION_SELECT_FAVORITE = "com.qiuyinlan.suixinji.widget.SELECT_FAVORITE"
    const val ACTION_MOVE_WALK = "com.qiuyinlan.suixinji.widget.MOVE_WALK"
    const val ACTION_RANDOM_WALK = "com.qiuyinlan.suixinji.widget.RANDOM_WALK"
    const val ACTION_REFRESH = "com.qiuyinlan.suixinji.widget.REFRESH"
    const val EXTRA_NOTE_ID = "note_id"
    const val EXTRA_WIDGET_ID = "widget_id"
    const val EXTRA_DIRECTION = "direction"
}

private data class WidgetNote(val id: String, val content: String, val createdAt: Long, val favorite: Boolean)

private object WidgetNotes {
    private const val PREFS = "suixin_notes"
    private const val STATE = "state"

    fun all(context: Context): List<WidgetNote> = runCatching {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(STATE, null) ?: return emptyList()
        val notes = JSONObject(raw).optJSONArray("notes") ?: JSONArray()
        (0 until notes.length()).mapNotNull { index ->
            val item = notes.optJSONObject(index) ?: return@mapNotNull null
            WidgetNote(item.optString("id"), item.optString("content"), item.optLong("createdAt"), item.optBoolean("favorite"))
        }.sortedByDescending { it.createdAt }
    }.getOrDefault(emptyList())

    fun favorites(context: Context): List<WidgetNote> = all(context).filter { it.favorite }
}

internal object NoteWidgets {
    private const val WALK_PREFS = "suixin_widget_walk"
    private const val FAVORITE_PREFS = "suixin_widget_favorite"

    fun refreshAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val walkIds = manager.getAppWidgetIds(ComponentName(context, WalkWidgetProvider::class.java))
        val inputIds = manager.getAppWidgetIds(ComponentName(context, QuickInputWidgetProvider::class.java))
        val favoriteIds = manager.getAppWidgetIds(ComponentName(context, FavoriteNoteWidgetProvider::class.java))
        walkIds.forEach { updateWalk(context, manager, it) }
        inputIds.forEach { updateInput(context, manager, it) }
        favoriteIds.forEach { updateFavorite(context, manager, it) }
    }

    fun updateWalk(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val notes = WidgetNotes.all(context)
        val prefs = context.getSharedPreferences(WALK_PREFS, Context.MODE_PRIVATE)
        val key = "current_$widgetId"
        val savedId = prefs.getString(key, null)
        val note = notes.firstOrNull { it.id == savedId } ?: notes.randomOrNull().also { prefs.edit().putString(key, it?.id).apply() }
        val views = RemoteViews(context.packageName, R.layout.widget_walk)
        views.setTextViewText(R.id.widget_walk_created_at, note?.let { formatCreatedAt(it.createdAt) }.orEmpty())
        views.setViewVisibility(R.id.widget_walk_created_at, if (note == null) View.GONE else View.VISIBLE)
        views.setOnClickPendingIntent(R.id.widget_walk_random, randomWalkPendingIntent(context, widgetId))
        views.setTextViewText(R.id.widget_walk_content, note?.content?.ifBlank { "點擊開始記錄此刻" } ?: "還沒有筆記")
        views.setTextViewText(R.id.widget_walk_position, note?.let { "漫步 · ${notes.indexOfFirst { item -> item.id == it.id } + 1}/${notes.size}" } ?: "漫步")
        views.setOnClickPendingIntent(R.id.widget_walk_content, openNotePendingIntent(context, widgetId, note?.id))
        views.setOnClickPendingIntent(R.id.widget_walk_previous, moveWalkPendingIntent(context, widgetId, -1))
        views.setOnClickPendingIntent(R.id.widget_walk_next, moveWalkPendingIntent(context, widgetId, 1))
        manager.updateAppWidget(widgetId, views)
    }

    fun moveWalk(context: Context, widgetId: Int, direction: Int) {
        val notes = WidgetNotes.all(context)
        if (notes.isEmpty()) return
        val prefs = context.getSharedPreferences(WALK_PREFS, Context.MODE_PRIVATE)
        val key = "current_$widgetId"
        val currentId = prefs.getString(key, null)
        val currentIndex = notes.indexOfFirst { it.id == currentId }.let { if (it < 0) 0 else it }
        val nextIndex = (currentIndex + direction).coerceIn(0, notes.lastIndex)
        prefs.edit().putString(key, notes[nextIndex].id).apply()
    }

    fun updateInput(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_quick_input)
        val intent = Intent(context, NotesActivity::class.java).apply {
            action = NoteWidgetContract.ACTION_NEW_NOTE
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        views.setOnClickPendingIntent(R.id.widget_quick_input, PendingIntent.getActivity(context, 300000 + widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        manager.updateAppWidget(widgetId, views)
    }

    fun updateFavorite(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val selectedId = context.getSharedPreferences(FAVORITE_PREFS, Context.MODE_PRIVATE).getString("note_$widgetId", null)
        val note = WidgetNotes.favorites(context).firstOrNull { it.id == selectedId }
        val views = RemoteViews(context.packageName, R.layout.widget_favorite_note)
        views.setTextViewText(R.id.widget_favorite_created_at, note?.let { formatCreatedAt(it.createdAt) }.orEmpty())
        views.setViewVisibility(R.id.widget_favorite_created_at, if (note == null) View.GONE else View.VISIBLE)
        views.setTextViewText(R.id.widget_favorite_title, "收藏笔记")
        views.setTextViewText(R.id.widget_favorite_content, note?.content ?: "点击选择收藏文章")
        views.setOnClickPendingIntent(R.id.widget_favorite_content, favoritePickerPendingIntent(context, widgetId))
        manager.updateAppWidget(widgetId, views)
    }

    fun setFavorite(context: Context, widgetId: Int, noteId: String) {
        context.getSharedPreferences(FAVORITE_PREFS, Context.MODE_PRIVATE).edit().putString("note_$widgetId", noteId).apply()
        updateFavorite(context, AppWidgetManager.getInstance(context), widgetId)
    }

    fun randomWalk(context: Context, widgetId: Int) {
        val notes = WidgetNotes.all(context)
        if (notes.isEmpty()) return
        val prefs = context.getSharedPreferences(WALK_PREFS, Context.MODE_PRIVATE)
        val key = "current_$widgetId"
        val currentId = prefs.getString(key, null)
        val candidates = notes.filterNot { it.id == currentId }.ifEmpty { notes }
        prefs.edit().putString(key, candidates.random().id).apply()
    }

    private fun moveWalkPendingIntent(context: Context, widgetId: Int, direction: Int): PendingIntent {
        val intent = Intent(context, WalkWidgetProvider::class.java).apply {
            action = NoteWidgetContract.ACTION_MOVE_WALK
            data = Uri.parse("suixinji://walk/$widgetId/$direction")
            putExtra(NoteWidgetContract.EXTRA_WIDGET_ID, widgetId)
            putExtra(NoteWidgetContract.EXTRA_DIRECTION, direction)
        }
        return PendingIntent.getBroadcast(context, 100000 + widgetId * 2 + if (direction < 0) 0 else 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun randomWalkPendingIntent(context: Context, widgetId: Int): PendingIntent {
        val intent = Intent(context, WalkWidgetProvider::class.java).apply {
            action = NoteWidgetContract.ACTION_RANDOM_WALK
            data = Uri.parse("suixinji://walk/$widgetId/random")
            putExtra(NoteWidgetContract.EXTRA_WIDGET_ID, widgetId)
        }
        return PendingIntent.getBroadcast(context, 200000 + widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun formatCreatedAt(createdAt: Long): String = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(createdAt))

    private fun openNotePendingIntent(context: Context, requestCode: Int, noteId: String?): PendingIntent {
        val intent = Intent(context, NotesActivity::class.java).apply {
            action = if (noteId == null) NoteWidgetContract.ACTION_NEW_NOTE else NoteWidgetContract.ACTION_OPEN_NOTE
            noteId?.let { putExtra(NoteWidgetContract.EXTRA_NOTE_ID, it) }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun favoritePickerPendingIntent(context: Context, widgetId: Int): PendingIntent {
        val intent = Intent(context, NotesActivity::class.java).apply {
            action = NoteWidgetContract.ACTION_SELECT_FAVORITE
            putExtra(NoteWidgetContract.EXTRA_WIDGET_ID, widgetId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(context, 600000 + widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}

class WalkWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { NoteWidgets.updateWalk(context, manager, it) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == NoteWidgetContract.ACTION_MOVE_WALK) {
            val widgetId = intent.getIntExtra(NoteWidgetContract.EXTRA_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                NoteWidgets.moveWalk(context, widgetId, intent.getIntExtra(NoteWidgetContract.EXTRA_DIRECTION, 0))
                NoteWidgets.updateWalk(context, AppWidgetManager.getInstance(context), widgetId)
            }
        } else if (intent.action == NoteWidgetContract.ACTION_RANDOM_WALK) {
            val widgetId = intent.getIntExtra(NoteWidgetContract.EXTRA_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                NoteWidgets.randomWalk(context, widgetId)
                NoteWidgets.updateWalk(context, AppWidgetManager.getInstance(context), widgetId)
            }
        } else if (intent.action == NoteWidgetContract.ACTION_REFRESH) {
                onUpdate(context, AppWidgetManager.getInstance(context), intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS) ?: intArrayOf())
        }
    }
}

class QuickInputWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { NoteWidgets.updateInput(context, manager, it) }
    }
}

class FavoriteNoteWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { NoteWidgets.updateFavorite(context, manager, it) }
    }
}
