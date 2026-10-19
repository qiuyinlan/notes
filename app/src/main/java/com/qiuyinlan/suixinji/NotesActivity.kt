package com.qiuyinlan.suixinji

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.WindowManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.os.Environment
import android.widget.Toast
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.StrikethroughS
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.produceState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.text.TextLayoutResult
import kotlin.math.roundToInt
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.BufferedOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlin.math.abs
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions

private val NoteInk = Color(0xFF4B3B30)
private const val MAX_PINNED_NOTES = 6
private val NotePrimary = Color(0xFF8B6A52)
private val NoteAccent = Color(0xFFC68E6B)
private val NotePaper = Color(0xFFF4E8DA)
private val NoteSoft = Color(0xFFEAD9C7)
private val NoteMuted = Color(0xFF8A786B)
private val NoteCommentColor = Color(0xFF808080)
private val NoteTagColor = Color(0xFF3A7FD5)
private val NoteTypography = Typography().copy(
    displayLarge = androidx.compose.ui.text.TextStyle(fontSize = 24.sp, lineHeight = 29.sp),
    displayMedium = androidx.compose.ui.text.TextStyle(fontSize = 22.sp, lineHeight = 27.sp),
    displaySmall = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, lineHeight = 25.sp),
    headlineLarge = androidx.compose.ui.text.TextStyle(fontSize = 22.sp, lineHeight = 27.sp),
    headlineMedium = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, lineHeight = 25.sp),
    headlineSmall = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, lineHeight = 23.sp),
    titleLarge = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, lineHeight = 24.sp),
    titleSmall = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, lineHeight = 22.sp),
    bodySmall = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, lineHeight = 21.sp),
    labelMedium = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
)
private val NoteTagRegex = Regex("(?<![\\p{L}\\d_])#([\\p{L}\\d](?:[\\p{L}\\d/._-]*[\\p{L}\\d])?)(?=$|[^\\p{L}\\d/._-])")
private val NoteReferenceRegex = Regex("\\[\\[note:([^|\\]]+)\\|([^\\]]*)]]")
private val TagPalette = listOf(
    0xFFE8D5C4.toInt(), 0xFFD8E2DC.toInt(), 0xFFF6E7CB.toInt(), 0xFFD9E4F5.toInt(),
    0xFFE6D5E8.toInt(), 0xFFF2D0A9.toInt(), 0xFFD5E8D4.toInt(), 0xFFF4CCCC.toInt(),
)
private fun tagColor(category: NoteCategory): Color {
    val stored = category.color.takeIf { it != 0L }?.toInt()
    return Color(stored ?: TagPalette[Math.floorMod(category.id.hashCode(), TagPalette.size)])
}
private fun pinnedTagColor(category: NoteCategory): Color {
    val color = tagColor(category)
    return Color(color.red * 0.82f, color.green * 0.82f, color.blue * 0.82f, color.alpha)
}

private data class NoteComment(val position: Int, val text: String, val expanded: Boolean = true, val createdAt: Long = System.currentTimeMillis())
private data class QuickNote(
    val id: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val categoryIds: List<String>,
    val status: String = "inbox",
    val location: String? = null,
    val mood: String = "",
    val comments: List<NoteComment> = emptyList(),
    val imageUris: List<String> = emptyList(),
    val favorite: Boolean = false,
    val pinned: Boolean = false,
)
private data class DeletedNote(val note: QuickNote, val deletedAt: Long, val originalIndex: Int)

internal data class WidgetLaunchRequest(val sequence: Int, val noteId: String?, val favoriteWidgetId: Int? = null)
private data class NoteCategory(val id: String, val name: String, val parentId: String?, val path: String, val color: Long = 0L)

private fun orderedCategories(categories: List<NoteCategory>, pinnedIds: List<String>): List<NoteCategory> {
    val order = pinnedIds.withIndex().associate { it.value to it.index }
    return categories.sortedWith(compareBy<NoteCategory> { if (it.id in order) 0 else 1 }.thenBy { order[it.id] ?: Int.MAX_VALUE })
}

private data class ImportBundle(val notes: List<QuickNote> = emptyList(), val categories: List<NoteCategory> = emptyList()) {
    val size: Int get() = notes.size
    fun isEmpty(): Boolean = notes.isEmpty()
}
private data class WeReadBook(val id: String, val title: String, val noteCount: Int)
private data class Diary(val id: String, val title: String, val markdown: String, val date: String)
private data class FutureLetter(val id: String, val title: String, val content: String, val deliverDate: String, val shown: Boolean = false)
private data class NotesState(
    val notes: List<QuickNote> = emptyList(),
    val trash: List<DeletedNote> = emptyList(),
    val categories: List<NoteCategory> = emptyList(),
    val tagPinnedIds: List<String> = emptyList(),
    val drawerPinnedIds: List<String> = emptyList(),
    val diaries: List<Diary> = emptyList(),
    val letters: List<FutureLetter> = emptyList(),
)

private fun noteTagPaths(note: QuickNote, categories: List<NoteCategory>): List<String> =
    (note.categoryIds.mapNotNull { id -> categories.firstOrNull { it.id == id }?.path } +
        NoteTagRegex.findAll(note.content).map { it.groupValues[1] }).distinct()

private fun noteLeafTags(note: QuickNote, categories: List<NoteCategory>): List<NoteCategory> {
    val paths = noteTagPaths(note, categories)
    return paths.filterNot { path -> paths.any { other -> other.startsWith("$path/") } }
        .map { path -> categories.firstOrNull { it.path == path } ?: NoteCategory("inline:$path", path.substringAfterLast('/'), null, path) }
}

private fun noteReferences(note: QuickNote, allNotes: List<QuickNote>): List<QuickNote> =
    NoteReferenceRegex.findAll(note.content)
        .mapNotNull { match -> allNotes.firstOrNull { it.id == match.groupValues[1] } }
        .distinctBy { it.id }
        .toList()

private fun indexExistingNoteTags(state: NotesState): NotesState {
    val categories = state.categories.toMutableList()
    val notes = state.notes.map { note ->
        val ids = note.categoryIds.toMutableList()
        NoteTagRegex.findAll(note.content).forEach { match ->
            var parentId: String? = null
            var path = ""
            match.groupValues[1].split('/').filter { it.isNotBlank() }.forEach { name ->
                path = if (path.isBlank()) name else "$path/$name"
                val category = categories.firstOrNull { it.path == path } ?: NoteCategory(UUID.randomUUID().toString(), name, parentId, path).also { categories += it }
                if (category.id !in ids) ids += category.id
                parentId = category.id
            }
        }
        note.copy(categoryIds = ids.distinct())
    }
    return state.copy(notes = notes, categories = categories)
}

private fun noteHasCategoryPath(note: QuickNote, path: String, categories: List<NoteCategory>): Boolean =
    noteTagPaths(note, categories).any { it == path || it.startsWith("$path/") }

private fun JSONObject.toQuickNote(): QuickNote = QuickNote(
    id = optString("id"),
    content = optString("content"),
    createdAt = optLong("createdAt"),
    updatedAt = optLong("updatedAt", optLong("createdAt")),
    categoryIds = optJSONArray("categoryIds").toStringList(),
    status = optString("status", "inbox"),
    location = optNullableString("location"),
    mood = optString("mood", "").let { if (it == "NORMAL") "" else it },
    comments = optJSONArray("comments").toList { comment ->
        NoteComment(comment.optInt("position"), comment.optString("text"), comment.optBoolean("expanded", true), comment.optLong("createdAt", System.currentTimeMillis()))
    },
    imageUris = optJSONArray("imageUris").toStringList(),
    favorite = optBoolean("favorite", false),
    pinned = optBoolean("pinned", false),
)

private fun QuickNote.toJson(): JSONObject = JSONObject().apply {
    put("id", id); put("content", content); put("createdAt", createdAt); put("updatedAt", updatedAt)
    put("categoryIds", JSONArray(categoryIds)); put("status", status); put("location", location); put("mood", mood)
    put("comments", JSONArray().apply { comments.forEach { comment -> put(JSONObject().apply { put("position", comment.position); put("text", comment.text); put("expanded", comment.expanded); put("createdAt", comment.createdAt) }) } })
    put("imageUris", JSONArray(imageUris)); put("favorite", favorite); put("pinned", pinned)
}

private object NotesStore {
    private const val PREF = "suixin_notes"
    private const val KEY = "state"
    private const val BACKUP_NAME = "suixinji-notes-backup.json"
    private const val TRASH_RETENTION_MS = 30L * 24 * 60 * 60 * 1000

    fun load(context: Context): NotesState {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val local = prefs.getString(KEY, null)
        if (!local.isNullOrBlank()) {
            // Migrate data created by older builds before a possible uninstall.
            if (backupUri(context) == null) writeBackup(context, local)
            return parseStored(context, local)
        }
        val backup = readBackup(context)
        if (!backup.isNullOrBlank()) {
            prefs.edit().putString(KEY, backup).apply()
            return parseStored(context, backup)
        }
        return NotesState()
    }

    private fun parseStored(context: Context, raw: String): NotesState {
        val state = parse(raw)
        val storedTrashCount = runCatching { JSONObject(raw).optJSONArray("trash")?.length() ?: 0 }.getOrDefault(0)
        if (state.trash.size < storedTrashCount) save(context, state)
        return state
    }

    private fun parse(raw: String): NotesState = runCatching {
        val root = JSONObject(raw)
        NotesState(
            notes = root.optJSONArray("notes").toList { item -> item.toQuickNote() },
            trash = root.optJSONArray("trash").toList { item ->
                DeletedNote(item.getJSONObject("note").toQuickNote(), item.optLong("deletedAt"), item.optInt("originalIndex"))
            }.filter { it.deletedAt >= System.currentTimeMillis() - TRASH_RETENTION_MS },
            categories = root.optJSONArray("categories").toList { item -> NoteCategory(item.optString("id"), item.optString("name"), item.optNullableString("parentId"), item.optString("path"), item.optLong("color", 0L)) },
            tagPinnedIds = root.optJSONArray("tagPinnedIds").toStringList(),
            drawerPinnedIds = root.optJSONArray("drawerPinnedIds").toStringList(),
            diaries = root.optJSONArray("diaries").toList { item -> Diary(item.optString("id"), item.optString("title"), item.optString("markdown"), item.optString("date")) },
            letters = root.optJSONArray("letters").toList { item -> FutureLetter(item.optString("id"), item.optString("title"), item.optString("content"), item.optString("deliverDate"), item.optBoolean("shown")) },
        )
    }.getOrDefault(NotesState())

    fun save(context: Context, state: NotesState) {
        val raw = serialize(state)
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY, raw).apply()
        writeBackup(context, raw)
    }

    fun serialize(state: NotesState): String = JSONObject().apply {
            put("notes", JSONArray().apply {
                state.notes.forEach { note -> put(note.toJson()) }
            })
            put("trash", JSONArray().apply { state.trash.forEach { item -> put(JSONObject().apply { put("note", item.note.toJson()); put("deletedAt", item.deletedAt); put("originalIndex", item.originalIndex) }) } })
            put("categories", JSONArray().apply { state.categories.forEach { put(JSONObject().apply { put("id", it.id); put("name", it.name); put("parentId", it.parentId); put("path", it.path); put("color", it.color) }) } })
            put("tagPinnedIds", JSONArray(state.tagPinnedIds))
            put("drawerPinnedIds", JSONArray(state.drawerPinnedIds))
            put("diaries", JSONArray().apply { state.diaries.forEach { put(JSONObject().apply { put("id", it.id); put("title", it.title); put("markdown", it.markdown); put("date", it.date) }) } })
            put("letters", JSONArray().apply { state.letters.forEach { put(JSONObject().apply { put("id", it.id); put("title", it.title); put("content", it.content); put("deliverDate", it.deliverDate); put("shown", it.shown) }) } })
        }.toString()

    fun restore(context: Context, uri: Uri): NotesState? = runCatching {
        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { parse(it.readText()) }
    }.getOrNull()

    private fun backupUri(context: Context): android.net.Uri? = runCatching {
        // Query the shared files collection so backups remain discoverable even if an older build
        // wrote the file under Documents instead of Downloads.
        val collection = MediaStore.Files.getContentUri("external")
        context.contentResolver.query(
            collection,
            arrayOf(MediaStore.MediaColumns._ID),
            "${MediaStore.MediaColumns.DISPLAY_NAME}=?",
            arrayOf(BACKUP_NAME),
            "${MediaStore.MediaColumns.DATE_MODIFIED} DESC",
        )?.use { cursor -> if (cursor.moveToFirst()) ContentUris.withAppendedId(collection, cursor.getLong(0)) else null }
    }.getOrNull()

    private fun readBackup(context: Context): String? = runCatching {
        val uri = backupUri(context) ?: return@runCatching null
        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
    }.getOrNull()

    private fun writeBackup(context: Context, raw: String) {
        runCatching {
            val resolver = context.contentResolver
            val uri = backupUri(context) ?: resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, BACKUP_NAME)
                put(MediaStore.Downloads.MIME_TYPE, "application/json")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }) ?: return
            resolver.openOutputStream(uri, "wt")?.bufferedWriter()?.use { it.write(raw) }
            if (ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }.let { resolver.update(uri, it, null, null) } == 0) Unit
        }
    }
}

private inline fun <T> JSONArray?.toList(map: (JSONObject) -> T): List<T> = if (this == null) emptyList() else (0 until length()).mapNotNull { optJSONObject(it)?.let(map) }
private fun JSONArray?.toStringList(): List<String> = if (this == null) emptyList() else (0 until length()).map { optString(it) }
private fun JSONObject.optNullableString(key: String): String? = if (isNull(key)) null else optString(key).takeIf { it.isNotBlank() }
private fun prettyTime(value: Long): String = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA).format(Date(value))
private fun editTime(value: Long): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(Date(value))
private fun parseEditTime(value: String): Long? = runCatching { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).apply { isLenient = false }.parse(value)?.time }.getOrNull()
private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())
private fun prettyMonth(value: Long): String = SimpleDateFormat("yyyy-MM", Locale.CHINA).format(Date(value))

private fun queryFolderImages(context: Context, treeUri: Uri): List<Uri> = runCatching {
    val childUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, DocumentsContract.getTreeDocumentId(treeUri))
    val result = mutableListOf<Uri>()
    context.contentResolver.query(childUri, arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_MIME_TYPE), null, null, null)?.use { cursor ->
        while (cursor.moveToNext()) {
            if (cursor.getString(1)?.startsWith("image/") == true) {
                result += DocumentsContract.buildDocumentUriUsingTree(treeUri, cursor.getString(0))
            }
        }
    }
    result
}.getOrDefault(emptyList())

class NotesActivity : ComponentActivity() {
    private var widgetLaunchSequence = 0
    private var widgetLaunchRequest by mutableStateOf<WidgetLaunchRequest?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        acceptWidgetIntent(intent)
        setContent {
            MaterialTheme(typography = NoteTypography, colorScheme = lightColorScheme(primary = NotePrimary, secondary = NoteAccent, background = NotePaper, surface = Color.White, onSurface = NoteInk)) {
                NotesApp(widgetLaunchRequest)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        acceptWidgetIntent(intent)
    }

    private fun acceptWidgetIntent(intent: android.content.Intent?) {
        when (intent?.action) {
            NoteWidgetContract.ACTION_NEW_NOTE -> widgetLaunchRequest = WidgetLaunchRequest(++widgetLaunchSequence, null)
            NoteWidgetContract.ACTION_OPEN_NOTE -> widgetLaunchRequest = WidgetLaunchRequest(++widgetLaunchSequence, intent.getStringExtra(NoteWidgetContract.EXTRA_NOTE_ID))
            NoteWidgetContract.ACTION_SELECT_FAVORITE -> widgetLaunchRequest = WidgetLaunchRequest(
                ++widgetLaunchSequence,
                null,
                intent.getIntExtra(NoteWidgetContract.EXTRA_WIDGET_ID, -1).takeIf { it >= 0 },
            )
        }
    }
}

private enum class NotesPage { HOME, ORGANIZER, TAGS, WALK, FAVORITES, MY, LETTERS, CATEGORY, TRASH }
private enum class WalkFilterMode { RANDOM, MONTH, TAG }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesApp(widgetLaunchRequest: WidgetLaunchRequest? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = androidx.compose.material3.rememberDrawerState(DrawerValue.Closed)
    var state by remember { mutableStateOf(NotesStore.load(context)) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarBounds by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    var page by remember { mutableStateOf(NotesPage.HOME) }
    var favoriteWidgetId by remember { mutableStateOf<Int?>(null) }
    var selectedCategory by remember { mutableStateOf<NoteCategory?>(null) }
    var categoryReturnPage by remember { mutableStateOf(NotesPage.TAGS) }
    var tagCategory by remember { mutableStateOf<NoteCategory?>(null) }
    var editorNote by remember { mutableStateOf<QuickNote?>(null) }
    var editorReturnPage by remember { mutableStateOf(NotesPage.HOME) }
    var lettersReturnPage by remember { mutableStateOf(NotesPage.HOME) }
    var commentNote by remember { mutableStateOf<QuickNote?>(null) }
    var letterEditor by remember { mutableStateOf<FutureLetter?>(null) }
    var renameTarget by remember { mutableStateOf<NoteCategory?>(null) }
    var homeDateFilter by rememberSaveable { mutableStateOf<String?>(null) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showChildCategoryMenu by remember { mutableStateOf(false) }
    var showHomeSearch by rememberSaveable { mutableStateOf(false) }
    var showTagsSearch by rememberSaveable { mutableStateOf(false) }
    var showCategorySearch by rememberSaveable { mutableStateOf(false) }
    var showGlobalSearch by remember { mutableStateOf(false) }
    var newCategoryParent by remember { mutableStateOf<NoteCategory?>(null) }
    var showNewCategory by remember { mutableStateOf(false) }
    var showTagActions by remember { mutableStateOf(false) }

    BackHandler(
        enabled = showGlobalSearch || (page != NotesPage.HOME && editorNote == null && commentNote == null && letterEditor == null && renameTarget == null && !showNewCategory),
    ) {
        if (showGlobalSearch) showGlobalSearch = false else when (page) {
            NotesPage.LETTERS -> { page = lettersReturnPage; selectedCategory = null }
            NotesPage.CATEGORY -> {
                if (categoryReturnPage == NotesPage.HOME) {
                    page = NotesPage.HOME
                    selectedCategory = null
                } else {
                    val parent = selectedCategory?.parentId?.let { id -> state.categories.firstOrNull { it.id == id } }
                    if (parent != null) selectedCategory = parent else { page = categoryReturnPage; selectedCategory = null }
                }
                showCategorySearch = false
            }
            NotesPage.TAGS -> {
                val parent = tagCategory?.parentId?.let { id -> state.categories.firstOrNull { it.id == id } }
                if (tagCategory != null) tagCategory = parent else page = NotesPage.HOME
            }
            else -> {
                if (page == NotesPage.FAVORITES) favoriteWidgetId = null
                page = NotesPage.HOME
                selectedCategory = null
                homeDateFilter = null
                showHomeSearch = false
                showTagsSearch = false
                showCategorySearch = false
            }
        }
    }

    fun update(next: NotesState) { state = next; NotesStore.save(context, next); NoteWidgets.refreshAll(context) }
    LaunchedEffect(Unit) {
        val indexed = indexExistingNoteTags(state)
        if (indexed != state) update(indexed)
    }
    fun restoreFromTrash(noteId: String): Boolean {
        val deleted = state.trash.firstOrNull { it.note.id == noteId } ?: return false
        val restored = if (deleted.note.pinned && state.notes.count { it.pinned } >= MAX_PINNED_NOTES) deleted.note.copy(pinned = false) else deleted.note
        val notes = if (state.notes.any { it.id == noteId }) state.notes else state.notes.toMutableList().apply { add(deleted.originalIndex.coerceIn(0, size), restored) }
        update(state.copy(notes = notes, trash = state.trash.filterNot { it.note.id == noteId }))
        return true
    }
    fun deleteNote(note: QuickNote, showUndoNotice: Boolean = true) {
        val index = state.notes.indexOfFirst { it.id == note.id }
        if (index < 0) return
        val now = System.currentTimeMillis()
        update(state.copy(
            notes = state.notes.filterNot { it.id == note.id },
            trash = (state.trash.filterNot { it.note.id == note.id } + DeletedNote(note, now, index))
                .filter { it.deletedAt >= now - 30L * 24 * 60 * 60 * 1000 },
        ))
        snackbarHostState.currentSnackbarData?.dismiss()
        if (showUndoNotice) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "已删除放入回收站",
                    actionLabel = "撤销",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite,
                )
                if (result == SnackbarResult.ActionPerformed) restoreFromTrash(note.id)
            }
        }
    }
    LaunchedEffect(state.trash) {
        state.trash.minOfOrNull { it.deletedAt + 30L * 24 * 60 * 60 * 1000 }?.let { expiresAt ->
            val wait = expiresAt - System.currentTimeMillis()
            if (wait > 0) delay(wait)
            val currentCutoff = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
            if (state.trash.any { it.deletedAt <= currentCutoff }) {
                update(state.copy(trash = state.trash.filter { it.deletedAt > currentCutoff }))
            }
        }
    }
    fun openEditor(note: QuickNote, returnPage: NotesPage = page) {
        editorReturnPage = returnPage
        editorNote = note
    }
    LaunchedEffect(widgetLaunchRequest?.sequence) {
        widgetLaunchRequest?.let { request ->
            if (request.favoriteWidgetId != null) {
                favoriteWidgetId = request.favoriteWidgetId
                page = NotesPage.FAVORITES
                editorNote = null
                return@let
            }
            val note = request.noteId?.let { id -> state.notes.firstOrNull { it.id == id } }
            val target = note ?: run {
                val now = System.currentTimeMillis()
                QuickNote(UUID.randomUUID().toString(), "", now, now, emptyList())
            }
            page = NotesPage.HOME
            openEditor(target, NotesPage.HOME)
        }
    }
    fun noteWithTags(note: QuickNote): QuickNote {
        val categories = state.categories.toMutableList()
        val ids = note.categoryIds.toMutableList()
        NoteTagRegex.findAll(note.content).forEach { match ->
            var parent: String? = null
            var built = ""
            match.groupValues[1].split('/').filter { it.isNotBlank() }.forEach { name ->
                built = if (built.isBlank()) name else "$built/$name"
                val existing = categories.firstOrNull { it.path == built }
                val found = existing ?: NoteCategory(UUID.randomUUID().toString(), name, parent, built).also { categories += it }
                if (found.id !in ids) ids += found.id
                parent = found.id
            }
        }
        state = state.copy(categories = categories)
        return note.copy(categoryIds = ids.distinct(), status = if (ids.isEmpty()) note.status else "organized")
    }
    fun saveNote(raw: QuickNote) {
        val note = noteWithTags(raw)
        val existing = state.notes.any { it.id == note.id }
        val notes = if (existing) state.notes.map { if (it.id == note.id) note else it } else listOf(note) + state.notes
        update(state.copy(notes = notes.sortedByDescending { it.createdAt }))
    }
    val dueLetters = state.letters.filter { it.deliverDate <= today() && !it.shown }
    fun navigateBackFromPage() {
        when (page) {
            NotesPage.CATEGORY -> {
                if (categoryReturnPage == NotesPage.HOME) {
                    selectedCategory = null
                    page = NotesPage.HOME
                } else {
                    val parent = selectedCategory?.parentId?.let { id -> state.categories.firstOrNull { it.id == id } }
                    if (parent != null) selectedCategory = parent else { selectedCategory = null; page = categoryReturnPage }
                }
            }
            NotesPage.TAGS -> {
                val parent = tagCategory?.parentId?.let { id -> state.categories.firstOrNull { it.id == id } }
                if (tagCategory != null) tagCategory = parent else page = NotesPage.HOME
            }
            NotesPage.LETTERS -> { page = lettersReturnPage; selectedCategory = null }
            NotesPage.MY, NotesPage.ORGANIZER, NotesPage.WALK, NotesPage.FAVORITES, NotesPage.TRASH -> page = NotesPage.HOME
            else -> Unit
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    state = state,
                    page = page,
                    selected = selectedCategory,
                    onRename = { renameTarget = it },
                    onTogglePinned = { category ->
                        val pinned = state.drawerPinnedIds
                        update(state.copy(drawerPinnedIds = if (category.id in pinned) pinned - category.id else if (pinned.size < 6) pinned + category.id else pinned))
                    },
                ) { target, category ->
                    if (target != NotesPage.FAVORITES) favoriteWidgetId = null
                    if (target == NotesPage.CATEGORY) categoryReturnPage = NotesPage.TAGS
                    page = target
                    selectedCategory = category
                    scope.launch { drawerState.close() }
                }
            }
        },
    ) {
        Scaffold(
            modifier = Modifier
                .pointerInput(snackbarHostState.currentSnackbarData) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val down = event.changes.firstOrNull { it.pressed && !it.previousPressed }
                            if (down != null && snackbarBounds?.contains(down.position) != true) {
                                snackbarHostState.currentSnackbarData?.dismiss()
                            }
                        }
                    }
                }
                .pointerInput(page, tagCategory?.id, selectedCategory?.id) {
                var startX = 0f
                var distance = 0f
                detectHorizontalDragGestures(
                    onDragStart = { start -> startX = start.x; distance = 0f },
                    onHorizontalDrag = { _, amount ->
                        if (startX <= 36.dp.toPx()) {
                            if (amount > 0f) distance += amount else distance = 0f
                        }
                    },
                    onDragEnd = { if (startX <= 36.dp.toPx() && distance > 90.dp.toPx()) navigateBackFromPage(); distance = 0f },
                    onDragCancel = { distance = 0f },
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState, modifier = Modifier.onGloballyPositioned { snackbarBounds = it.boundsInRoot() }) { data ->
                    Snackbar(data, containerColor = Color(0xFFFFE9E6), contentColor = Color(0xFF8F1D18), actionColor = NotePrimary)
                }
            },
            containerColor = NotePaper,
            topBar = {
                TopAppBar(
                    title = {
                        val category = selectedCategory
                        if (page == NotesPage.CATEGORY && category != null) {
                            Box {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { showCategoryMenu = true }.padding(horizontal = 4.dp, vertical = 6.dp)) {
                                    Text("#${category.path}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Icon(Icons.Filled.KeyboardArrowDown, "标签菜单", tint = NotePrimary, modifier = Modifier.size(18.dp))
                                }
                                val parent = category.parentId?.let { parentId -> state.categories.firstOrNull { it.id == parentId } }
                                val children = state.categories.filter { it.parentId == category.id }
                                NotePopupMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false }) {
                                    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
                                        Column(Modifier.padding(vertical = 6.dp)) {
                                            Text("标签操作", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp))
                                            DropdownMenuItem(text = { Text("编辑这个标签") }, onClick = { showCategoryMenu = false; renameTarget = category })
                                            DropdownMenuItem(text = { Text("查看上级标签") }, enabled = parent != null, onClick = { if (parent != null) { selectedCategory = parent; showCategoryMenu = false } })
                                            DropdownMenuItem(text = { Text(if (children.isEmpty()) "暂无下级标签" else "查看下级标签") }, enabled = children.isNotEmpty(), onClick = { showCategoryMenu = false; showChildCategoryMenu = true })
                                        }
                                    }
                                }
                                NotePopupMenu(expanded = showChildCategoryMenu, onDismissRequest = { showChildCategoryMenu = false }) {
                                    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(vertical = 6.dp)) { children.forEach { child -> DropdownMenuItem(text = { Text("#${child.name}") }, onClick = { selectedCategory = child; showChildCategoryMenu = false }) } } }
                                }
                            }
                        } else {
                            Text(when (page) { NotesPage.HOME -> "随心记"; NotesPage.ORGANIZER -> "整理"; NotesPage.TAGS -> "标签"; NotesPage.WALK -> "漫步"; NotesPage.FAVORITES -> "收藏列表"; NotesPage.MY -> "我的"; NotesPage.LETTERS -> "信件"; NotesPage.CATEGORY -> "分类"; NotesPage.TRASH -> "回收站" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Filled.Menu, "打开导航") }
                    },
                    actions = {
                        when (page) {
                            NotesPage.HOME, NotesPage.TAGS, NotesPage.CATEGORY -> IconButton(onClick = { showGlobalSearch = true }) { Icon(Icons.Filled.Search, "搜索标签和笔记") }
                            else -> Unit
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NotePaper),
                )
            },
            bottomBar = { MainNavigation(page) { target -> favoriteWidgetId = null; page = target; selectedCategory = null; homeDateFilter = null; showHomeSearch = false; showTagsSearch = false; showCategorySearch = false; if (target != NotesPage.TAGS) tagCategory = null } },
            floatingActionButton = {
                if (page == NotesPage.TAGS) {
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (showTagActions) {
                            Surface(shape = RoundedCornerShape(18.dp), color = Color.White, shadowElevation = 4.dp,
                                modifier = Modifier.clickable {
                                    showTagActions = false
                                    val category = tagCategory
                                    val now = System.currentTimeMillis()
                                    val prefix = category?.path?.takeIf { it.isNotBlank() }?.let { "#$it " } ?: ""
                                    openEditor(QuickNote(UUID.randomUUID().toString(), prefix, now, now, category?.let { listOf(it.id) } ?: emptyList()), NotesPage.TAGS)
                                }) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    Icon(Icons.Filled.Edit, "新建笔记", tint = NotePrimary, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp)); Text("新建笔记", color = NoteInk, style = MaterialTheme.typography.labelLarge)
                                }
                            }
                            Surface(shape = RoundedCornerShape(18.dp), color = Color.White, shadowElevation = 4.dp,
                                modifier = Modifier.clickable {
                                    showTagActions = false
                                    newCategoryParent = tagCategory
                                    showNewCategory = true
                                }) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    Icon(Icons.Filled.Label, "新建标签", tint = NotePrimary, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp)); Text("新建标签", color = NoteInk, style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                        FloatingActionButton(onClick = { showTagActions = !showTagActions }, containerColor = NotePrimary, contentColor = Color.White) {
                            Icon(if (showTagActions) Icons.Filled.Close else Icons.Filled.Add, "新建")
                        }
                    }
                }
            },
        ) { padding ->
            when (page) {
                NotesPage.HOME -> HomePage(state, if (homeDateFilter == null) dueLetters else state.letters.filter { it.deliverDate == homeDateFilter }, padding, homeDateFilter, showHomeSearch, onClearDateFilter = { homeDateFilter = null }, onCapture = { openEditor(it, NotesPage.HOME) }, onCategory = { selectedCategory = it; categoryReturnPage = NotesPage.HOME; page = NotesPage.CATEGORY }, onDiary = { page = NotesPage.WALK }, onLetters = { lettersReturnPage = NotesPage.HOME; page = NotesPage.LETTERS }, onOrganize = { page = NotesPage.ORGANIZER }, onEdit = { openEditor(it, NotesPage.HOME) }, onComment = { commentNote = it }, onUpdate = ::update, onToggleFavorite = { note -> update(state.copy(notes = state.notes.map { if (it.id == note.id) it.copy(favorite = !it.favorite) else it })) }, onDelete = ::deleteNote)
                NotesPage.CATEGORY -> selectedCategory?.let { category -> CategoryPage(state, category, padding, showCategorySearch, onAdd = { openEditor(QuickNote(UUID.randomUUID().toString(), "", System.currentTimeMillis(), System.currentTimeMillis(), listOf(category.id)), NotesPage.CATEGORY) }, onEdit = { openEditor(it, NotesPage.CATEGORY) }, onCategory = { selectedCategory = it }, onToggleFavorite = { note -> update(state.copy(notes = state.notes.map { if (it.id == note.id) it.copy(favorite = !it.favorite) else it })) }, onDelete = ::deleteNote) }
                NotesPage.ORGANIZER -> OrganizerPage(state, padding, onUpdate = ::update, onSaveNote = ::saveNote, onEdit = { openEditor(it, NotesPage.ORGANIZER) }, onDelete = { deleteNote(it, showUndoNotice = false) }, onDismissDeleteNotice = { snackbarHostState.currentSnackbarData?.dismiss() })
                NotesPage.TAGS -> TagsPage(state, tagCategory, padding, showTagsSearch, onSelect = { tagCategory = it }, onEdit = { openEditor(it, NotesPage.TAGS) }, onToggleFavorite = { note -> update(state.copy(notes = state.notes.map { if (it.id == note.id) it.copy(favorite = !it.favorite) else it })) }, onColorChange = { category, color -> update(state.copy(categories = state.categories.map { if (it.id == category.id) it.copy(color = color.toLong()) else it })) }, onTogglePinned = { category ->
                    val pinned = state.tagPinnedIds
                    update(state.copy(tagPinnedIds = if (category.id in pinned) pinned - category.id else if (pinned.size < 6) pinned + category.id else pinned))
                }, onRename = { renameTarget = it }, onDeleteNote = ::deleteNote, onDeleteMany = { selectedCategories ->
                    val removedIds = state.categories.filter { item -> selectedCategories.any { category -> item.id == category.id || item.path.startsWith("${category.path}/") } }.map { it.id }.toSet()
                    update(state.copy(
                        categories = state.categories.filterNot { it.id in removedIds },
                        notes = state.notes.map { note -> note.copy(categoryIds = note.categoryIds.filterNot { it in removedIds }) },
                        tagPinnedIds = state.tagPinnedIds.filterNot { it in removedIds },
                        drawerPinnedIds = state.drawerPinnedIds.filterNot { it in removedIds },
                    ))
                    tagCategory = null
                })
                NotesPage.WALK -> WalkPage(state, padding, onEdit = { openEditor(it, NotesPage.WALK) }, onDelete = ::deleteNote, onToggleFavorite = { note -> update(state.copy(notes = state.notes.map { if (it.id == note.id) it.copy(favorite = !it.favorite) else it })) }, onUpdate = ::update)
                NotesPage.FAVORITES -> FavoriteNotesPage(state.notes.filter { it.favorite }.sortedByDescending { it.createdAt }, padding) { note ->
                    val widgetId = favoriteWidgetId
                    if (widgetId != null) {
                        NoteWidgets.setFavorite(context, widgetId, note.id)
                        favoriteWidgetId = null
                        page = NotesPage.HOME
                        (context as? Activity)?.finish()
                    } else {
                        openEditor(note, NotesPage.FAVORITES)
                    }
                }
                NotesPage.TRASH -> TrashPage(state.trash, padding, onRestore = { restoreFromTrash(it) }, onDeleteForever = { noteId -> update(state.copy(trash = state.trash.filterNot { it.note.id == noteId })) })
            NotesPage.MY -> MyPage(state, padding, onLetters = { lettersReturnPage = NotesPage.MY; page = NotesPage.LETTERS }, onDayClick = { day -> homeDateFilter = day; page = NotesPage.HOME }, onImport = { bundle ->
                    var importState = state
                    val mergedCategories = importState.categories.toMutableList()
                    val idMap = mutableMapOf<String, String>()
                    bundle.categories.forEach { incoming ->
                        val existing = mergedCategories.firstOrNull { it.path == incoming.path }
                        val target = existing ?: incoming.copy(id = UUID.randomUUID().toString(), parentId = incoming.parentId?.let { idMap[it] }).also { mergedCategories += it }
                        idMap[incoming.id] = target.id
                    }
                    val importedNotes = bundle.notes.map { note ->
                        val categories = importState.categories.toMutableList()
                        val ids = note.categoryIds.mapNotNull { idMap[it] ?: it }.toMutableList()
                        NoteTagRegex.findAll(note.content).forEach { match ->
                            var parent: String? = null
                            var built = ""
                            match.groupValues[1].split('/').filter { it.isNotBlank() }.forEach { name ->
                                built = if (built.isBlank()) name else "$built/$name"
                                val found = categories.firstOrNull { it.path == built } ?: NoteCategory(UUID.randomUUID().toString(), name, parent, built).also { categories += it }
                                if (found.id !in ids) ids += found.id
                                parent = found.id
                            }
                        }
                        importState = importState.copy(categories = categories)
                        note.copy(categoryIds = ids.distinct(), status = if (ids.isEmpty()) note.status else "organized")
                    }
                    update(importState.copy(categories = mergedCategories, notes = (state.notes + importedNotes).distinctBy { it.id }.sortedByDescending { it.createdAt }))
            }, onRestore = { restored -> update(restored) })
                NotesPage.LETTERS -> LetterPage(state, dueLetters, padding, onBack = { page = lettersReturnPage }, onEdit = { letterEditor = it }, onCreate = { letterEditor = FutureLetter(UUID.randomUUID().toString(), "", "", today()) }, onUpdate = ::update)
            }
        }
    }
    if (showGlobalSearch) {
        SearchBrowserDialog(
            notes = state.notes,
            categories = state.categories,
            onDismiss = { showGlobalSearch = false },
            onSelectNote = null,
            onEditNote = { selected -> showGlobalSearch = false; openEditor(selected, page) },
        )
    }
    editorNote?.let { note -> key(note.id) { QuickNoteEditor(
        note,
        state.categories,
        state.notes,
        onDismiss = { editorNote = null; page = editorReturnPage },
        onSave = { saveNote(it) },
        onCreateLinkedNote = { saveNote(it) },
        onOpenLinkedNote = { editorNote = it },
        onNext = { val now = System.currentTimeMillis(); editorNote = QuickNote(UUID.randomUUID().toString(), "", now, now, emptyList()) },
        onDelete = { draft ->
            if (state.notes.none { it.id == draft.id }) saveNote(draft)
            deleteNote(draft)
            editorNote = null
            page = editorReturnPage
        },
        onToggleFavorite = { favorite -> update(state.copy(notes = state.notes.map { current -> if (current.id == note.id) current.copy(favorite = favorite) else current })) },
        onTogglePinned = { pinned ->
            if (pinned && !note.pinned && state.notes.count { it.pinned && it.id != note.id } >= MAX_PINNED_NOTES) {
                Toast.makeText(context, "最多置顶6篇哦", Toast.LENGTH_SHORT).show()
                false
            } else {
                update(state.copy(notes = state.notes.map { current -> if (current.id == note.id) current.copy(pinned = pinned) else current }))
                true
            }
        },
    ) } }
    commentNote?.let { note -> CommentSheet(note, onDismiss = { commentNote = null }, onSave = { comment -> update(state.copy(notes = state.notes.map { current -> if (current.id == note.id) current.copy(comments = current.comments + comment, updatedAt = System.currentTimeMillis()) else current })); commentNote = null }) }
    letterEditor?.let { letter -> LetterEditor(letter, onDismiss = { letterEditor = null; page = lettersReturnPage }, onSave = { value -> update(state.copy(letters = listOf(value) + state.letters.filterNot { it.id == value.id })); letterEditor = null; page = lettersReturnPage }) }
    renameTarget?.let { category ->
        RenameCategoryDialog(category, onDismiss = { renameTarget = null }, onSave = { newName ->
            val cleanName = newName.trim().trimStart('#')
            if (cleanName.isNotBlank() && !cleanName.contains('/')) {
                val oldPath = category.path
                val parentPath = oldPath.substringBeforeLast('/', "")
                val renamed = state.categories.map { item ->
                    if (item.id == category.id || item.path.startsWith("$oldPath/")) {
                        val suffix = item.path.removePrefix(oldPath)
                        val newPath = "$parentPath${if (parentPath.isBlank()) "" else "/"}$cleanName$suffix"
                        item.copy(name = if (item.id == category.id) cleanName else item.name, path = newPath)
                    } else item
                }
                update(state.copy(categories = renamed))
                selectedCategory = selectedCategory?.let { current -> renamed.firstOrNull { it.id == current.id } }
                tagCategory = tagCategory?.let { current -> renamed.firstOrNull { it.id == current.id } }
                renameTarget = null
            }
        })
    }
    if (showNewCategory) {
        NewCategoryDialog(newCategoryParent, onDismiss = { showNewCategory = false }, onCreate = { rawName ->
            val name = rawName.trim().trimStart('#')
            if (name.isNotBlank() && !name.contains('/')) {
                val parent = newCategoryParent
                val path = if (parent == null) name else "${parent.path}/$name"
                if (state.categories.none { it.path == path }) {
                    update(state.copy(categories = state.categories + NoteCategory(UUID.randomUUID().toString(), name, parent?.id, path)))
                    showNewCategory = false
                }
            }
        })
    }
}

@Composable
private fun MainNavigation(page: NotesPage, onSelect: (NotesPage) -> Unit) {
    val navColors = NavigationBarItemDefaults.colors(
        selectedIconColor = NotePrimary,
        selectedTextColor = NotePrimary,
        indicatorColor = NoteSoft,
        unselectedIconColor = NoteMuted,
        unselectedTextColor = NoteMuted,
    )
    NavigationBar(modifier = Modifier.height(62.dp), containerColor = Color.White, tonalElevation = 0.dp) {
        NavigationBarItem(page == NotesPage.HOME, { onSelect(NotesPage.HOME) }, icon = { Icon(Icons.Filled.Home, "首页", modifier = Modifier.size(20.dp)) }, label = { Text("首页", fontSize = 12.sp) }, colors = navColors)
        NavigationBarItem(page == NotesPage.ORGANIZER, { onSelect(NotesPage.ORGANIZER) }, icon = { Icon(Icons.Filled.Tune, "整理", modifier = Modifier.size(20.dp)) }, label = { Text("整理", fontSize = 12.sp) }, colors = navColors)
        NavigationBarItem(page == NotesPage.WALK, { onSelect(NotesPage.WALK) }, icon = { Icon(Icons.Filled.Book, "漫步", modifier = Modifier.size(20.dp)) }, label = { Text("漫步", fontSize = 12.sp) }, colors = navColors)
        NavigationBarItem(page == NotesPage.TAGS, { onSelect(NotesPage.TAGS) }, icon = { Icon(Icons.Filled.Label, "标签", modifier = Modifier.size(20.dp)) }, label = { Text("标签", fontSize = 12.sp) }, colors = navColors)
        NavigationBarItem(page == NotesPage.MY || page == NotesPage.LETTERS, { onSelect(NotesPage.MY) }, icon = { Icon(Icons.Filled.Person, "我的", modifier = Modifier.size(20.dp)) }, label = { Text("我的", fontSize = 12.sp) }, colors = navColors)
    }
}

@Composable
private fun DrawerContent(state: NotesState, page: NotesPage, selected: NoteCategory?, onRename: (NoteCategory) -> Unit, onTogglePinned: (NoteCategory) -> Unit, onNavigate: (NotesPage, NoteCategory?) -> Unit) {
    var expandedTags by remember { mutableStateOf(emptySet<String>()) }
    val rootTags = state.categories.filter { it.parentId == null }
    val pinnedTags = orderedCategories(state.categories.filter { it.id in state.drawerPinnedIds }, state.drawerPinnedIds)

    Column(Modifier.fillMaxSize().background(NotePaper).verticalScroll(rememberScrollState()).padding(vertical = 18.dp)) {
        Text("随心记", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp))
        DrawerItem("首页", Icons.Filled.Home, page == NotesPage.HOME) { onNavigate(NotesPage.HOME, null) }
        DrawerItem("随记箱 · ${state.notes.count { it.status == "inbox" }}", Icons.Filled.Inbox, page == NotesPage.ORGANIZER) { onNavigate(NotesPage.ORGANIZER, null) }
        DrawerItem("整理", Icons.Filled.Tune, page == NotesPage.ORGANIZER) { onNavigate(NotesPage.ORGANIZER, null) }
        DrawerItem("漫步", Icons.Filled.Book, page == NotesPage.WALK) { onNavigate(NotesPage.WALK, null) }
        DrawerItem("收藏列表", Icons.Filled.Favorite, page == NotesPage.FAVORITES) { onNavigate(NotesPage.FAVORITES, null) }
        DrawerItem("我的", Icons.Filled.Person, page == NotesPage.MY || page == NotesPage.LETTERS) { onNavigate(NotesPage.MY, null) }
        Divider(Modifier.padding(vertical = 12.dp))
        Text("分类", style = MaterialTheme.typography.labelLarge, color = NoteMuted, modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp))
            Text("已置顶的标签", style = MaterialTheme.typography.labelLarge, color = NoteMuted, modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp))
            pinnedTags.forEach { category ->
                DrawerTagRow(
                    category = category,
                    allCategories = state.categories,
                    notes = state.notes,
                    selected = selected,
                    expandedTags = expandedTags,
                    onToggle = { id -> expandedTags = if (id in expandedTags) expandedTags - id else expandedTags + id },
                    onRename = onRename,
                    pinnedIds = state.drawerPinnedIds,
                    onTogglePinned = onTogglePinned,
                    onNavigate = onNavigate,
                    flat = true,
                )
            }
            Divider(Modifier.padding(vertical = 8.dp))
            Text("标签", style = MaterialTheme.typography.labelLarge, color = NoteMuted, modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp))
            rootTags.forEach { category ->
                DrawerTagRow(
                    category = category,
                    allCategories = state.categories,
                    notes = state.notes,
                    selected = selected,
                    expandedTags = expandedTags,
                    onToggle = { id -> expandedTags = if (id in expandedTags) expandedTags - id else expandedTags + id },
                    onRename = onRename,
                    pinnedIds = state.drawerPinnedIds,
                    onTogglePinned = onTogglePinned,
                    onNavigate = onNavigate,
                )
            }
            if (rootTags.isEmpty()) {
                Text("还没有标签", color = NoteMuted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp))
            }
        DrawerItem("回收站 · ${state.trash.size}", Icons.Filled.DeleteOutline, page == NotesPage.TRASH) { onNavigate(NotesPage.TRASH, null) }
        Text("本地保存 · ${state.notes.size} 条记录", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp))
    }
}

@Composable
private fun TrashPage(
    trash: List<DeletedNote>,
    padding: PaddingValues,
    onRestore: (String) -> Boolean,
    onDeleteForever: (String) -> Unit,
) {
    var deleteTarget by remember { mutableStateOf<String?>(null) }
    deleteTarget?.let { noteId ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("永久删除笔记") },
            text = { Text("永久删除后无法恢复，确定继续吗？") },
            confirmButton = { TextButton(onClick = { onDeleteForever(noteId); deleteTarget = null }) { Text("永久删除", color = Color(0xFFB3261E)) } },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("取消") } },
        )
    }
    Column(Modifier.fillMaxSize().padding(padding)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
                .background(Color(0xFFFFE9E6), RoundedCornerShape(8.dp)).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.DeleteOutline, null, tint = Color(0xFFB3261E), modifier = Modifier.size(19.dp))
            Spacer(Modifier.width(8.dp))
            Text("回收站中的笔记将在 30 天后自动清除", color = Color(0xFFB3261E), style = MaterialTheme.typography.bodySmall)
        }
        if (trash.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { EmptyHint("回收站为空") }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(trash.sortedByDescending { it.deletedAt }, key = { it.note.id }) { deleted ->
                    val remainingDays = ((30L * 24 * 60 * 60 * 1000 - (System.currentTimeMillis() - deleted.deletedAt)) / (24L * 60 * 60 * 1000)).toInt().coerceAtLeast(0)
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(14.dp)) {
                            Text(deleted.note.content.ifBlank { "（空白笔记）" }, color = NoteInk, style = MaterialTheme.typography.bodyLarge, maxLines = 8, overflow = TextOverflow.Ellipsis)
                            Text("删除于 ${prettyTime(deleted.deletedAt)} · ${remainingDays} 天后清除", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 7.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { onRestore(deleted.note.id) }) { Text("恢复", color = NotePrimary) }
                                TextButton(onClick = { deleteTarget = deleted.note.id }) { Text("永久删除", color = Color(0xFFB3261E)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerTagRow(
    category: NoteCategory,
    allCategories: List<NoteCategory>,
    notes: List<QuickNote>,
    selected: NoteCategory?,
    expandedTags: Set<String>,
    onToggle: (String) -> Unit,
    onRename: (NoteCategory) -> Unit,
    pinnedIds: List<String>,
    onTogglePinned: (NoteCategory) -> Unit,
    onNavigate: (NotesPage, NoteCategory?) -> Unit,
    flat: Boolean = false,
) {
    val children = if (flat) emptyList() else allCategories.filter { it.parentId == category.id }
    val isExpanded = category.id in expandedTags
    val noteCount = notes.count { category.id in it.categoryIds }
    val selectedRow = selected?.id == category.id
    val isPinned = category.id in pinnedIds
    var menuOpen by remember(category.id) { mutableStateOf(false) }

    Row(
        Modifier.fillMaxWidth()
            .background(if (selectedRow) NoteSoft else if (isPinned) NoteSoft.copy(alpha = 0.45f) else Color.Transparent)
            .clickable { onNavigate(NotesPage.CATEGORY, category) }
            .padding(start = (if (flat) 12 else 12 + category.path.count { it == '/' } * 16).dp, end = 18.dp, top = 9.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (children.isNotEmpty()) {
            IconButton(onClick = { onToggle(category.id) }, modifier = Modifier.size(28.dp)) {
                Icon(if (isExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight, "展开或收起子标签", tint = NoteMuted)
            }
        } else {
            Spacer(Modifier.width(28.dp))
        }
        Icon(Icons.Filled.Label, null, tint = if (selectedRow) NotePrimary else if (isPinned) Color(0xFF34251B) else NoteMuted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("#${category.name}", color = if (selectedRow) NotePrimary else if (isPinned) Color(0xFF34251B) else NoteInk, fontSize = 14.sp, fontWeight = if (selectedRow || isPinned) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1f), maxLines = 1)
        if (isPinned) Icon(Icons.Filled.PushPin, "已置顶", tint = Color(0xFF34251B), modifier = Modifier.size(16.dp).padding(end = 2.dp))
        Text(noteCount.toString(), color = NoteMuted, style = MaterialTheme.typography.labelSmall)
        Box {
            IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(30.dp)) { Icon(Icons.Filled.MoreVert, "标签操作", tint = NoteMuted, modifier = Modifier.size(19.dp)) }
            NotePopupMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(text = { Text("修改名字") }, onClick = { menuOpen = false; onRename(category) })
                DropdownMenuItem(
                    text = { Text(if (category.id in pinnedIds) "取消置顶" else "置顶") },
                    enabled = category.id in pinnedIds || pinnedIds.size < 6,
                    onClick = { menuOpen = false; onTogglePinned(category) },
                )
            }
        }
    }

    if (isExpanded) {
        children.forEach { child ->
            DrawerTagRow(child, allCategories, notes, selected, expandedTags, onToggle, onRename, pinnedIds, onTogglePinned, onNavigate, flat = false)
        }
    }
}

@Composable
private fun RenameCategoryDialog(category: NoteCategory, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember(category.id) { mutableStateOf(category.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("修改标签名") },
        text = { OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("标签名") }) },
        confirmButton = { TextButton(onClick = { onSave(name) }, enabled = name.trim().isNotBlank()) { Text("保存") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}

@Composable
private fun NewCategoryDialog(parent: NoteCategory?, onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (parent == null) "新建标签" else "新建下级标签") },
        text = { OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("标签名称") }, placeholder = { Text("例如：阅读") }) },
        confirmButton = { TextButton(onClick = { onCreate(name) }, enabled = name.trim().isNotBlank()) { Text("创建") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}

@Composable
private fun DrawerItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).background(if (selected) NoteSoft else Color.Transparent).padding(horizontal = 22.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = if (selected) NotePrimary else NoteMuted, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(12.dp)); Text(label, color = NoteInk, fontSize = 14.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun HomePage(state: NotesState, due: List<FutureLetter>, padding: PaddingValues, dateFilter: String?, searchOpen: Boolean, onClearDateFilter: () -> Unit, onCapture: (QuickNote) -> Unit, onCategory: (NoteCategory) -> Unit, onOrganize: () -> Unit, onDiary: () -> Unit, onLetters: () -> Unit, onEdit: (QuickNote) -> Unit, onComment: (QuickNote) -> Unit, onUpdate: (NotesState) -> Unit, onToggleFavorite: (QuickNote) -> Unit, onDelete: (QuickNote) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    val dayFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }
    val visibleNotes = state.notes.sortedWith(compareByDescending<QuickNote> { it.pinned }.thenByDescending { it.createdAt }).filter { note ->
        (dateFilter == null || dayFormat.format(Date(note.createdAt)) == dateFilter) &&
            (query.isBlank() || note.content.contains(query, true) || note.categoryIds.mapNotNull { id -> state.categories.firstOrNull { it.id == id }?.path }.any { path -> path.contains(query, true) })
    }
    Column(Modifier.fillMaxSize().padding(padding)) {
        LazyColumn(Modifier.fillMaxWidth().weight(1f), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (dateFilter == null) Text("时间线", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    if (dateFilter != null) {
                        TextButton(onClick = onClearDateFilter) { Text("${dateFilter} ×", color = NotePrimary) }
                    }
                }
                if (dateFilter != null) Text("${dateFilter} 的记录", color = NoteMuted, modifier = Modifier.padding(top = 3.dp)) else Text("想到什么，先留下来", color = NoteMuted, modifier = Modifier.padding(top = 3.dp))
                if (searchOpen) OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true, label = { Text("搜索笔记") }, leadingIcon = { Icon(Icons.Filled.Search, null) }, trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Filled.Close, "清除搜索") } })
            }
            if (dateFilter == null) item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { SummaryCard("随记箱", state.notes.count { it.status == "inbox" }, Icons.Filled.Inbox, onOrganize); LetterSummaryIconCard(due.size, onLetters) } }
            item { Row(verticalAlignment = Alignment.CenterVertically) { Text("全部记录", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); if (dateFilter == null) { Spacer(Modifier.weight(1f)); TextButton(onClick = onDiary) { Text("漫步") } } } }
            if (state.notes.isEmpty()) item { EmptyHint(if (dateFilter == null) "还没有记录，点击底部输入框开始" else "这一天还没有笔记") }
            if (visibleNotes.isEmpty() && query.isNotBlank()) item { EmptyHint("没有找到匹配的笔记") }
            items(visibleNotes, key = { it.id }) { note -> NoteTimelineCard(note, state.categories, onEdit, onComment = { onComment(note) }, onToggleComment = { commentIndex -> onUpdate(state.copy(notes = state.notes.map { current -> if (current.id == note.id) current.copy(comments = current.comments.mapIndexed { index, comment -> if (index == commentIndex) comment.copy(expanded = !comment.expanded) else comment }) else current })) }, onToggleFavorite = { onToggleFavorite(note) }, onDelete = { onDelete(note) }, onTogglePinned = {
                if (!note.pinned && state.notes.count { it.pinned } >= MAX_PINNED_NOTES) {
                    Toast.makeText(context, "最多置顶6篇哦", Toast.LENGTH_SHORT).show()
                } else {
                    onUpdate(state.copy(notes = state.notes.map { current -> if (current.id == note.id) current.copy(pinned = !current.pinned) else current }))
                }
            }, allNotes = state.notes, onTagClick = onCategory) }
            if (state.categories.isNotEmpty()) item { Text("分类空间", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            if (state.categories.isNotEmpty()) item { LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxWidth().height(((state.categories.count { it.parentId == null } + 1) / 2 * 88).coerceAtLeast(88).dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(state.categories.filter { it.parentId == null }) { category -> Card(Modifier.fillMaxWidth().height(78.dp).clickable { onCategory(category) }, colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(14.dp)) { Text(category.name, fontWeight = FontWeight.Bold); Text("${state.notes.count { noteHasCategoryPath(it, category.path, state.categories) }} 条记录", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 7.dp)) } } } } }
        }
        Box(Modifier.fillMaxWidth().background(NotePaper).padding(horizontal = 16.dp, vertical = 6.dp).clickable {
            val now = System.currentTimeMillis()
            onCapture(QuickNote(UUID.randomUUID().toString(), "", now, now, emptyList()))
        }) {
            Surface(color = Color.White, shape = RoundedCornerShape(18.dp), shadowElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = "", onValueChange = {}, enabled = false, modifier = Modifier.fillMaxWidth().height(54.dp), singleLine = true, placeholder = { Text("点击开始记录…") }, shape = RoundedCornerShape(18.dp), trailingIcon = { Icon(Icons.AutoMirrored.Filled.Send, "开始记录", tint = NotePrimary) }, colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.Transparent, disabledBorderColor = Color.Transparent, disabledContainerColor = Color.Transparent))
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, count: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth(0.38f).height(62.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = NotePrimary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text(label, color = NoteMuted, fontSize = 13.sp)
                Text(count.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LetterSummaryIconCard(count: Int, onClick: () -> Unit) {
    Surface(color = Color.White, shape = RoundedCornerShape(12.dp), shadowElevation = 3.dp, modifier = Modifier.fillMaxWidth(0.34f).height(62.dp).clickable(onClick = onClick)) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.MailOutline, "信", tint = NotePrimary, modifier = Modifier.size(26.dp))
        Surface(color = NoteAccent, shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).size(18.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Text(count.toString(), color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
        }
      }
    }
}

@Composable
private fun LetterSummaryCard(count: Int, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth(0.48f)) {
        SummaryCard("信", count, Icons.Filled.MailOutline, onClick)
        if (count > 0) Surface(color = NoteAccent, shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).size(22.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Text(count.toString(), color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun NotePopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    compact: Boolean = false,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    val menuColorScheme = MaterialTheme.colorScheme.copy(
        primary = Color.White,
        surface = Color.White,
        surfaceTint = Color.White,
        surfaceContainerLowest = Color.White,
        surfaceContainerLow = Color.White,
        surfaceContainer = Color.White,
        surfaceContainerHigh = Color.White,
        surfaceContainerHighest = Color.White,
    )
    MaterialTheme(colorScheme = menuColorScheme) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            shape = RoundedCornerShape(14.dp),
            containerColor = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                .widthIn(min = if (compact) 168.dp else 200.dp, max = if (compact) 232.dp else 280.dp),
        ) {
            Column(Modifier.padding(vertical = 5.dp), content = content)
        }
    }
}

@Composable
private fun NoteActionMenuItem(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    destructive: Boolean = false,
) {
    DropdownMenuItem(
        text = { Text(label, fontSize = 14.sp, color = if (destructive) Color(0xFFB3261E) else NoteInk, maxLines = 1) },
        leadingIcon = icon,
        modifier = Modifier.height(40.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        onClick = onClick,
    )
}

@Composable
private fun NoteOverflowMenu(note: QuickNote, onToggleFavorite: () -> Unit, onTogglePinned: (() -> Unit)?, onDelete: () -> Unit, showDeleteDialog: Boolean, onDeleteDialogChange: (Boolean) -> Unit, onFlip: () -> Unit = {}) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showActions by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onFlip, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.Flip, "翻转卡片", tint = NotePrimary, modifier = Modifier.size(19.dp)) }
        Box {
            IconButton(onClick = { showActions = true }, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.MoreVert, "笔记操作", tint = NoteMuted, modifier = Modifier.size(19.dp)) }
        NotePopupMenu(expanded = showActions, onDismissRequest = { showActions = false }, compact = true) {
            NoteActionMenuItem("复制全文", icon = { Icon(Icons.Filled.ContentCopy, null, tint = NoteMuted, modifier = Modifier.size(18.dp)) }, onClick = {
                showActions = false
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("随心记", note.content))
                Toast.makeText(context, "已复制全文", Toast.LENGTH_SHORT).show()
            })
            NoteActionMenuItem(if (note.favorite) "取消收藏" else "收藏", icon = { Icon(if (note.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, null, tint = NoteAccent, modifier = Modifier.size(18.dp)) }, onClick = { showActions = false; onToggleFavorite() })
            if (onTogglePinned != null) NoteActionMenuItem(if (note.pinned) "取消置顶" else "置顶", icon = { Icon(Icons.Filled.PushPin, null, tint = NoteMuted, modifier = Modifier.size(18.dp)) }, onClick = { showActions = false; onTogglePinned() })
            NoteActionMenuItem("删除", icon = { Icon(Icons.Filled.DeleteOutline, null, tint = Color(0xFFB3261E), modifier = Modifier.size(18.dp)) }, onClick = { showActions = false; onDeleteDialogChange(true) }, destructive = true)
        }
        }
    }
    if (showDeleteDialog) AlertDialog(onDismissRequest = { onDeleteDialogChange(false) }, title = { Text("删除笔记") }, text = { Text("确定删除这条笔记吗？") }, confirmButton = { TextButton(onClick = { onDeleteDialogChange(false); onDelete() }) { Text("删除") } }, dismissButton = { TextButton(onClick = { onDeleteDialogChange(false) }) { Text("取消") } })
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun NoteTimelineCard(note: QuickNote, categories: List<NoteCategory>, onEdit: (QuickNote) -> Unit, onToggleComment: (Int) -> Unit, onComment: () -> Unit = {}, onToggleFavorite: () -> Unit = {}, onDelete: () -> Unit = {}, onTogglePinned: (() -> Unit)? = null, allNotes: List<QuickNote> = emptyList(), modifier: Modifier = Modifier, onTagClick: (NoteCategory) -> Unit = {}) {
    var expanded by rememberSaveable(note.id) { mutableStateOf(false) }
    var flipped by rememberSaveable(note.id) { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val longText = note.content.length > 180 || note.content.count { it == '\n' } > 6
    Card(modifier.fillMaxWidth().combinedClickable(onClick = { if (longText && !flipped) expanded = !expanded }, onDoubleClick = { if (!flipped) onEdit(note) }, onLongClick = { flipped = !flipped }), colors = CardDefaults.cardColors(containerColor = if (note.pinned) Color(0xFFE5DED5) else Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(prettyTime(note.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                moodEmoji(note.mood).takeIf { it.isNotEmpty() }?.let { Text(it, modifier = Modifier.padding(start = 6.dp), fontSize = 15.sp) }
                note.location?.let { Text(" · $it", color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false)) }
                Spacer(Modifier.weight(1f))
                if (note.pinned) Icon(Icons.Filled.PushPin, "已置顶", tint = NoteInk, modifier = Modifier.size(16.dp))
                if (note.favorite) Icon(Icons.Filled.Favorite, "已收藏", tint = NoteAccent, modifier = Modifier.size(16.dp))
                NoteOverflowMenu(note, onToggleFavorite, onTogglePinned, onDelete, showDeleteDialog, { showDeleteDialog = it }, onFlip = { flipped = !flipped })
            }
            NotePreviewFlip(
                note, categories, allNotes, flipped,
                modifier = Modifier.fillMaxWidth(),
                onOpenNote = onEdit,
                onFlip = { flipped = !flipped },
                onPreviewClick = { if (longText && !expanded && !flipped) expanded = true },
                onPreviewDoubleClick = { if (!flipped) onEdit(note) },
                onPreviewLongClick = { flipped = !flipped },
            ) {
                CommentedNotePreview(note.content, note.comments, onToggleComment, maxLines = if (longText && !expanded) 7 else Int.MAX_VALUE, showEye = true, modifier = Modifier.fillMaxWidth())
                if (longText) Text(if (expanded) "收起" else "展开", color = NoteTagColor, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 10.dp)) { Spacer(Modifier.weight(1f)); if (note.comments.isEmpty()) IconButton(onClick = onComment, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.ChatBubbleOutline, "添加评论", tint = NoteAccent, modifier = Modifier.size(19.dp)) } }
            }
            val paths = noteTagPaths(note, categories)
            if (paths.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    paths.forEach { path ->
                        val category = categories.firstOrNull { it.path == path }
                            ?: NoteCategory("inline:$path", path.substringAfterLast('/'), null, path)
                        Surface(
                            color = Color(0xFFEAF2FB),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { onTagClick(category) },
                        ) {
                            Text(
                                "#$path",
                                color = NotePrimary,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CommentPreview(comments: List<NoteComment>, onToggle: (Int) -> Unit, showEye: Boolean = true, onDoubleClick: (Int) -> Unit = {}, modifier: Modifier = Modifier) {
    Column(modifier) {
        comments.forEachIndexed { index, comment ->
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)) {
                if (showEye) {
                    IconButton(onClick = { onToggle(index) }, modifier = Modifier.size(28.dp)) { Icon(if (comment.expanded) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, "展开或隐藏评论", tint = NoteAccent, modifier = Modifier.size(18.dp)) }
                }
                if ((!showEye || comment.expanded) && comment.text.isNotBlank()) {
                    Surface(color = NotePaper, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f).combinedClickable(onClick = {}, onDoubleClick = { onDoubleClick(index) })) {
                        Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 5.dp)) {
                            Text(comment.text, color = NoteCommentColor, style = MaterialTheme.typography.bodySmall, fontSize = 13.sp)
                            Text(prettyTime(comment.createdAt), color = NoteMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CommentedNotePreview(content: String, comments: List<NoteComment>, onToggle: (Int) -> Unit, maxLines: Int = Int.MAX_VALUE, showEye: Boolean = true, onDoubleClickComment: (Int) -> Unit = {}, modifier: Modifier = Modifier, onClickContent: (() -> Unit)? = null) {
    Column(modifier) {
        if (comments.isEmpty()) {
            ClickableMarkdownText(content, maxLines, onClickContent = onClickContent)
        } else if (content.isBlank()) {
            CommentPreview(comments, onToggle, showEye, onDoubleClickComment)
        } else {
            val sorted = comments.withIndex().sortedBy { it.value.position.coerceIn(0, content.length) }
            var cursor = 0
            sorted.forEach { indexed ->
                val position = indexed.value.position.coerceIn(cursor, content.length)
                if (position > cursor) ClickableMarkdownText(content.substring(cursor, position), maxLines, Modifier.fillMaxWidth(), onClickContent)
                CommentPreview(listOf(indexed.value), { onToggle(indexed.index) }, showEye = showEye, onDoubleClick = { onDoubleClickComment(indexed.index) }, modifier = Modifier.fillMaxWidth())
                cursor = position
            }
            if (cursor < content.length) ClickableMarkdownText(content.substring(cursor), maxLines, onClickContent = onClickContent)
        }
        LinkPreviewCards(content)
    }
}

@Composable
private fun ClickableMarkdownText(value: String, maxLines: Int = Int.MAX_VALUE, modifier: Modifier = Modifier, onClickContent: (() -> Unit)? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val annotated = remember(value) { markdownInline(value) }
    val singleLink = remember(value) { Regex("\\[([^\\]]+)]\\(([^)]+)\\)").find(value.trim())?.takeIf { it.value == value.trim() } }
    val linkText: @Composable (Modifier) -> Unit = { textModifier ->
        ClickableText(
            text = annotated,
            modifier = textModifier,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge.copy(color = NoteInk),
            onClick = { offset ->
                val url = annotated.getStringAnnotations("WEB_URL", offset, offset).firstOrNull()?.item
                if (url != null) openWebLink(context, url) else onClickContent?.invoke()
            },
        )
    }
    if (singleLink != null) {
        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            linkText(Modifier.weight(1f))
            IconButton(onClick = { openWebLink(context, singleLink.groupValues[2]) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.OpenInNew, "在浏览器打开链接", tint = NoteMuted, modifier = Modifier.size(20.dp))
            }
        }
    } else linkText(modifier)
}

@Composable
private fun LinkPreviewCards(content: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val links = remember(content) {
        Regex("\\[([^\\]]+)]\\(([^)]+)\\)").findAll(content)
            .map { Triple(it.groupValues[1], it.groupValues[2], it.value) }
            .distinctBy { it.second }
            .toList()
    }
    links.forEach { (title, rawUrl) ->
        val url = normalizeWebUrl(rawUrl)
        val host = Uri.parse(url).host.orEmpty()
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NoteSoft),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable { openWebLink(context, url) },
        ) {
            Row(Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp, top = 10.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Link, "链接", tint = NotePrimary, modifier = Modifier.size(22.dp))
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text(title, color = NoteInk, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(host, color = NoteMuted, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp))
                }
                IconButton(onClick = { openWebLink(context, url) }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Filled.OpenInNew, "在浏览器打开", tint = NoteMuted)
                }
            }
        }
    }
}

private fun normalizeWebUrl(value: String): String {
    val trimmed = value.trim()
    return if (trimmed.startsWith("http://", true) || trimmed.startsWith("https://", true)) trimmed else "https://$trimmed"
}

private fun openWebLink(context: Context, value: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(normalizeWebUrl(value))))
    }.onFailure {
        Toast.makeText(context, "无法打开这个链接", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotePreviewFlip(
    note: QuickNote,
    categories: List<NoteCategory>,
    allNotes: List<QuickNote>,
    flipped: Boolean,
    modifier: Modifier = Modifier,
    onOpenNote: (QuickNote) -> Unit,
    onFlip: () -> Unit,
    onPreviewClick: () -> Unit = {},
    onPreviewDoubleClick: () -> Unit = { onOpenNote(note) },
    onPreviewLongClick: () -> Unit = onFlip,
    frontContent: @Composable () -> Unit,
) {
    val rotation by animateFloatAsState(if (flipped) 180f else 0f, animationSpec = tween(420), label = "preview-card-flip")
    val density = androidx.compose.ui.platform.LocalDensity.current.density
    Box(modifier.heightIn(max = 420.dp).graphicsLayer { rotationY = rotation; cameraDistance = 12f * density }) {
        if (rotation < 90f) {
            Column(
                Modifier.fillMaxWidth()
                    .combinedClickable(onClick = onPreviewClick, onDoubleClick = onPreviewDoubleClick, onLongClick = onPreviewLongClick)
                    .verticalScroll(rememberScrollState()),
            ) { frontContent() }
        } else {
            Surface(
                Modifier.fillMaxWidth().heightIn(min = 220.dp, max = 420.dp).graphicsLayer { rotationY = 180f },
                color = Color(0xFFFAF9F7),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NoteSoft),
                shadowElevation = 2.dp,
            ) {
                NotePreviewConnections(note, categories, allNotes, onOpenNote, onFlip)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun NotePreviewConnections(
    note: QuickNote,
    categories: List<NoteCategory>,
    allNotes: List<QuickNote>,
    onOpenNote: (QuickNote) -> Unit,
    onFlip: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val referenceRequester = remember { BringIntoViewRequester() }
    val backlinkRequester = remember { BringIntoViewRequester() }
    val tags = noteLeafTags(note, categories)
    val linkedNotes = noteReferences(note, allNotes)
    val backlinks = remember(note.id, allNotes) {
        allNotes.filter { candidate -> candidate.id != note.id && noteReferences(candidate, listOf(note)).isNotEmpty() }
            .sortedByDescending { it.updatedAt }
    }
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFlip, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Flip, "翻回笔记正面", tint = NotePrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { scope.launch { referenceRequester.bringIntoView() } }, modifier = Modifier.size(36.dp)) {
                RelationDirectionIcon(forward = true, description = "跳到引用的笔记")
            }
            IconButton(onClick = { scope.launch { backlinkRequester.bringIntoView() } }, modifier = Modifier.size(36.dp)) {
                RelationDirectionIcon(forward = false, description = "跳到被引用的笔记")
            }
        }
        Column(Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(rememberScrollState())) {
            Text("关联主题", color = NoteInk, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            if (tags.isEmpty()) {
                Text("暂无关联标签", Modifier.padding(top = 6.dp), color = NoteMuted, style = MaterialTheme.typography.bodySmall)
            } else {
                tags.chunked(3).forEach { row ->
                    Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach { category ->
                            Surface(Modifier.weight(1f).aspectRatio(1f), color = tagColor(category), shape = RoundedCornerShape(7.dp)) {
                                Box(Modifier.fillMaxSize().padding(6.dp), contentAlignment = Alignment.Center) {
                                    Text(category.name, color = NoteInk, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f).aspectRatio(1f)) }
                    }
                }
            }
            RelationSectionTitle("引用的笔记", forward = true, modifier = Modifier.padding(top = 14.dp).bringIntoViewRequester(referenceRequester))
            if (linkedNotes.isEmpty()) {
                Text("暂无引用笔记", Modifier.padding(top = 6.dp), color = NoteMuted, style = MaterialTheme.typography.bodySmall)
            } else {
                linkedNotes.forEach { ConnectionNoteCard(it, onOpenNote) }
            }
            RelationSectionTitle("被引用的笔记", forward = false, modifier = Modifier.padding(top = 14.dp).bringIntoViewRequester(backlinkRequester))
            if (backlinks.isEmpty()) {
                Text("暂无笔记引用这篇记录", Modifier.padding(top = 6.dp), color = NoteMuted, style = MaterialTheme.typography.bodySmall)
            } else {
                backlinks.forEach { ConnectionNoteCard(it, onOpenNote) }
            }
        }
    }
}

@Composable
private fun RelationDirectionIcon(forward: Boolean, description: String) {
    Box(Modifier.size(24.dp)) {
        Icon(Icons.Filled.Link, description, tint = NotePrimary, modifier = Modifier.align(Alignment.Center).size(20.dp))
        Surface(color = Color(0xFFFAF9F7), shape = CircleShape, modifier = Modifier.align(Alignment.BottomEnd).size(12.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(if (forward) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack, description, tint = NotePrimary, modifier = Modifier.size(9.dp))
            }
        }
    }
}

@Composable
private fun RelationSectionTitle(text: String, forward: Boolean, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        RelationDirectionIcon(forward, text)
        Text(text, Modifier.padding(start = 6.dp), color = NotePrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ConnectionNoteCard(note: QuickNote, onOpenNote: (QuickNote) -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(top = 6.dp).clickable { onOpenNote(note) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(prettyTime(note.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                moodEmoji(note.mood).takeIf { it.isNotEmpty() }?.let { Text(it, Modifier.padding(start = 6.dp), fontSize = 14.sp) }
                note.location?.let { Text(" · $it", color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            }
            CommentedNotePreview(note.content, note.comments, {}, showEye = false, modifier = Modifier.fillMaxWidth().padding(top = 6.dp), onClickContent = { onOpenNote(note) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentSheet(note: QuickNote, onDismiss: () -> Unit, onSave: (NoteComment) -> Unit) {
    var draft by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = NotePaper) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(0.5f).padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text("添加评论", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("评论会标记在正文位置 ${note.content.length}", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
            OutlinedTextField(draft, { draft = it }, Modifier.fillMaxWidth().weight(1f), placeholder = { Text("写下你的评论…") }, maxLines = 8)
            Button(onClick = { if (draft.trim().isNotEmpty()) onSave(NoteComment(note.content.length, draft.trim(), true)) }, enabled = draft.trim().isNotEmpty(), modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 12.dp)) { Icon(Icons.AutoMirrored.Filled.Send, null); Spacer(Modifier.width(6.dp)); Text("保存评论") }
        }
    }
}

private fun moodLabel(mood: String): String = when (mood) { "HAPPY" -> "开心"; "SAD" -> "不开心"; "NORMAL_SELECTED" -> "正常"; else -> "选择心情" }

@Composable
private fun CategoryPage(state: NotesState, category: NoteCategory, padding: PaddingValues, searchOpen: Boolean, onAdd: () -> Unit, onEdit: (QuickNote) -> Unit, onCategory: (NoteCategory) -> Unit, onToggleFavorite: (QuickNote) -> Unit, onDelete: (QuickNote) -> Unit) {
    var query by rememberSaveable(category.id) { mutableStateOf("") }
    val children = state.categories.filter { it.parentId == category.id }
    val visibleChildren = children.filter { query.isBlank() || it.name.contains(query, true) || it.path.contains(query, true) }
    val notes = state.notes.filter { note -> noteHasCategoryPath(note, category.path, state.categories) && (query.isBlank() || note.content.contains(query, true) || noteTagPaths(note, state.categories).any { path -> path.contains(query, true) }) }
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (searchOpen) item { OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("搜索当前标签") }, leadingIcon = { Icon(Icons.Filled.Search, null) }, trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Filled.Close, "清除搜索") } }) }
        item { OutlinedButton(onClick = onAdd, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(6.dp)); Text("在这里写一条") } }
        if (visibleChildren.isNotEmpty()) { item { Text("子分类", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }; items(visibleChildren) { child -> Surface(Modifier.fillMaxWidth().clickable { onCategory(child) }, color = NoteSoft, shape = RoundedCornerShape(10.dp)) { Text(child.name, modifier = Modifier.padding(14.dp), color = NoteInk, fontWeight = FontWeight.Bold) } } }
        item { Text("本分类记录 · ${notes.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        items(notes, key = { it.id }) { note -> NoteTimelineCard(note, state.categories, onEdit, {}, onToggleFavorite = { onToggleFavorite(note) }, onDelete = { onDelete(note) }, allNotes = state.notes) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TagsPage(
    state: NotesState,
    selected: NoteCategory?,
    padding: PaddingValues,
    searchOpen: Boolean,
    onSelect: (NoteCategory?) -> Unit,
    onEdit: (QuickNote) -> Unit,
    onToggleFavorite: (QuickNote) -> Unit,
    onDeleteNote: (QuickNote) -> Unit,
    onColorChange: (NoteCategory, Int) -> Unit,
    onTogglePinned: (NoteCategory) -> Unit,
    onRename: (NoteCategory) -> Unit,
    onDeleteMany: (Set<NoteCategory>) -> Unit,
) {
    var colorTarget by remember { mutableStateOf<NoteCategory?>(null) }
    var menuTarget by remember { mutableStateOf<NoteCategory?>(null) }
    var showBulkDelete by remember { mutableStateOf(false) }
    var deleteSelection by remember { mutableStateOf<Set<String>>(emptySet()) }
    var tagsCollapsed by rememberSaveable(selected?.id) { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    val categories = state.categories
    val pinnedAtRoot = if (selected == null) orderedCategories(categories.filter { it.id in state.tagPinnedIds }, state.tagPinnedIds) else emptyList()
    val regularCategories = if (selected == null) {
        categories.filter { it.parentId == null && it.id !in state.tagPinnedIds }
    } else {
        categories.filter { it.parentId == selected.id }
    }
    val visible = (pinnedAtRoot + orderedCategories(regularCategories, if (selected == null) state.tagPinnedIds else emptyList())).filter { query.isBlank() || it.name.contains(query, true) || it.path.contains(query, true) }
    val notes = selected?.let { category -> state.notes.filter { note -> noteHasCategoryPath(note, category.path, categories) && (query.isBlank() || note.content.contains(query, true) || noteTagPaths(note, categories).any { path -> path.contains(query, true) }) }.sortedByDescending { it.createdAt } }.orEmpty()
    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 12.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (selected != null) IconButton(onClick = { onSelect(categories.firstOrNull { it.id == selected.parentId }) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回上一级") }
            Column(Modifier.weight(1f)) {
                Text(selected?.path ?: "标签", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(if (selected == null) "按方块浏览你的分类标签" else "进入下一级标签空间", color = NoteMuted, style = MaterialTheme.typography.bodySmall)
            }
            if (selected != null) IconButton(onClick = { colorTarget = selected }) { Icon(Icons.Filled.Tune, "调整颜色", tint = NotePrimary) }
        }
        if (searchOpen) OutlinedTextField(
            query,
            { query = it },
            Modifier.fillMaxWidth().padding(top = 10.dp),
            singleLine = true,
            label = { Text("搜索标签和笔记") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Filled.Close, "清除搜索") } },
        )
        if (visible.isNotEmpty() && !tagsCollapsed) {
            Column(Modifier.fillMaxWidth().padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                visible.chunked(2).forEach { rowCategories ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowCategories.forEach { category ->
                            val childCount = categories.count { it.parentId == category.id }
                            val noteCount = state.notes.count { noteHasCategoryPath(it, category.path, categories) }
                            val isPinned = category.id in state.tagPinnedIds
                            Surface(color = if (isPinned) pinnedTagColor(category) else tagColor(category), shape = RoundedCornerShape(14.dp), modifier = Modifier.weight(1f).aspectRatio(1.15f).combinedClickable(onClick = { onSelect(category) }, onLongClick = { deleteSelection = setOf(category.id); showBulkDelete = true })) {
                                Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                        Column(Modifier.weight(1f)) {
                                            Text(category.name, style = MaterialTheme.typography.titleMedium, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NoteInk)
                                            if (category.parentId != null) Text(category.path, style = MaterialTheme.typography.labelSmall, color = NoteMuted, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
                                        }
                                        if (isPinned) Icon(Icons.Filled.PushPin, "已置顶", tint = NoteInk, modifier = Modifier.size(17.dp))
                                    }
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Text("$noteCount 条记录${if (childCount > 0) "  ·  $childCount 个子标签" else ""}", style = MaterialTheme.typography.labelSmall, color = NoteMuted, modifier = Modifier.weight(1f))
                                        Box {
                                            IconButton(onClick = { menuTarget = category }, modifier = Modifier.size(30.dp)) { Icon(Icons.Filled.MoreVert, "标签操作", tint = NoteMuted, modifier = Modifier.size(19.dp)) }
                                            NotePopupMenu(expanded = menuTarget?.id == category.id, onDismissRequest = { menuTarget = null }) {
                                                DropdownMenuItem(text = { Text("修改名字") }, onClick = { menuTarget = null; onRename(category) })
                                                DropdownMenuItem(text = { Text("调整颜色") }, onClick = { menuTarget = null; colorTarget = category })
                                                DropdownMenuItem(
                                                    text = { Text(if (category.id in state.tagPinnedIds) "取消置顶" else "置顶") },
                                                    enabled = category.id in state.tagPinnedIds || state.tagPinnedIds.size < 6,
                                                    onClick = { menuTarget = null; onTogglePinned(category) },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (rowCategories.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        if (selected != null) {
            if (visible.isNotEmpty()) {
                TextButton(onClick = { tagsCollapsed = !tagsCollapsed }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(if (tagsCollapsed) "展开标签" else "收起", color = NoteTagColor)
                }
            }
            Text("${selected.name} 及子标签笔记 · ${notes.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp, bottom = 4.dp))
            if (notes.isEmpty()) EmptyHint("这个标签下还没有笔记") else Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 18.dp)) {
                notes.forEach { note -> NoteTimelineCard(note, state.categories, onEdit, {}, onToggleFavorite = { onToggleFavorite(note) }, onDelete = { onDeleteNote(note) }, allNotes = state.notes) }
            }
        } else if (visible.isEmpty()) {
            EmptyHint("还没有标签，记录中添加 #标签 后会自动创建")
        }
    }
    colorTarget?.let { category ->
        AlertDialog(
            onDismissRequest = { colorTarget = null },
            title = { Text("调整“${category.name}”颜色") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TagPalette.chunked(4).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { color ->
                                Surface(onClick = { onColorChange(category, color); colorTarget = null }, color = Color(color), shape = RoundedCornerShape(10.dp), modifier = Modifier.size(48.dp).weight(1f)) {}
                            }
                            repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { colorTarget = null }) { Text("取消") } },
        )
    }
    if (showBulkDelete) {
        AlertDialog(
            onDismissRequest = { showBulkDelete = false },
            title = { Text("选择要删除的标签") },
            text = {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { deleteSelection = categories.map { it.id }.toSet() }) { Text("全选") }
                        TextButton(onClick = { deleteSelection = emptySet() }) { Text("清空") }
                    }
                    Column(Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
                        categories.forEach { category ->
                            Row(Modifier.fillMaxWidth().clickable { deleteSelection = if (category.id in deleteSelection) deleteSelection - category.id else deleteSelection + category.id }, verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(category.id in deleteSelection, onCheckedChange = null)
                                Text("#${category.path}", color = NoteInk, modifier = Modifier.padding(vertical = 3.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(enabled = deleteSelection.isNotEmpty(), onClick = { val selectedCategories = categories.filter { it.id in deleteSelection }.toSet(); showBulkDelete = false; deleteSelection = emptySet(); onDeleteMany(selectedCategories) }) { Text("删除") } },
            dismissButton = { TextButton(onClick = { showBulkDelete = false; deleteSelection = emptySet() }) { Text("取消") } },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchBrowserDialog(
    notes: List<QuickNote>,
    categories: List<NoteCategory>,
    onDismiss: () -> Unit,
    onSelectNote: ((QuickNote) -> Unit)? = null,
    onEditNote: (QuickNote) -> Unit,
    onCreateNote: (() -> Unit)? = null,
) {
    var query by remember { mutableStateOf("") }
    var selectedPath by remember { mutableStateOf<String?>(null) }
    var selectedNoteId by remember { mutableStateOf<String?>(null) }
    var previewNote by remember { mutableStateOf<QuickNote?>(null) }
    var sortByUpdate by remember { mutableStateOf(true) }
    var splitRatio by remember { mutableStateOf(0.44f) }
    var collapsedPanel by remember { mutableStateOf<Int?>(null) }
    val currentCategory = selectedPath?.let { path -> categories.firstOrNull { it.path == path } }
    val childCategories = if (currentCategory == null) {
        categories.filter { it.parentId == null }
    } else categories.filter { it.parentId == currentCategory.id }
    val visibleCategories = (if (query.isBlank()) childCategories else categories.filter { category ->
        category.path.contains(query, ignoreCase = true) || category.name.contains(query, ignoreCase = true)
    }).distinctBy { it.id }.sortedBy { it.path }
    val visibleNotes = remember(notes, categories, query, selectedPath, sortByUpdate) {
        notes.asSequence()
            .filter { note -> selectedPath?.let { path -> noteHasCategoryPath(note, path, categories) } ?: true }
            .filter { note -> query.isBlank() || note.content.contains(query, ignoreCase = true) || noteTagPaths(note, categories).any { it.contains(query, ignoreCase = true) } }
            .let { results -> if (sortByUpdate) results.sortedByDescending { it.updatedAt } else results.sortedByDescending { it.createdAt } }
            .toList()
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            Modifier.fillMaxSize().pointerInput(selectedPath, currentCategory) {
                var startX = 0f
                var dragDistance = 0f
                detectHorizontalDragGestures(
                    onDragStart = { start -> startX = start.x; dragDistance = 0f },
                    onHorizontalDrag = { change, amount ->
                        if (startX <= 36.dp.toPx() && amount > 0f) {
                            change.consume()
                            dragDistance += amount
                        } else if (startX <= 36.dp.toPx()) {
                            dragDistance = 0f
                        }
                    },
                    onDragEnd = {
                        if (startX <= 36.dp.toPx() && dragDistance > 90.dp.toPx()) {
                            val parent = currentCategory?.parentId?.let { parentId -> categories.firstOrNull { it.id == parentId } }
                            if (parent != null) {
                                selectedPath = parent.path
                                selectedNoteId = null
                            } else if (selectedPath != null) {
                                selectedPath = null
                                selectedNoteId = null
                            } else {
                                onDismiss()
                            }
                        }
                        dragDistance = 0f
                    },
                    onDragCancel = { dragDistance = 0f },
                )
            },
            color = Color(0xFFF4F3F5),
        ) {
            Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { if (selectedPath != null) selectedPath = currentCategory?.parentId?.let { id -> categories.firstOrNull { it.id == id }?.path } else onDismiss() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, if (selectedPath == null) "关闭搜索" else "返回上级标签", tint = NoteInk)
                    }
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        placeholder = { Text("搜索标签和笔记") },
                        leadingIcon = { Icon(Icons.Filled.Search, null, tint = NoteMuted) },
                        trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Filled.Close, "清除搜索") } },
                    )
                    if (onCreateNote != null) IconButton(onClick = onCreateNote) { Icon(Icons.Filled.AddCircleOutline, "新建笔记", tint = NotePrimary) }
                }
                if (selectedPath != null) {
                    Text(currentCategory?.path ?: selectedPath.orEmpty(), color = NoteMuted, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(start = 52.dp, top = 4.dp))
                }
                BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)) {
                    val density = androidx.compose.ui.platform.LocalDensity.current
                    val availableHeightPx = with(density) { maxHeight.toPx() }
                    Column(Modifier.fillMaxSize()) {
                        if (collapsedPanel != 1) {
                            val tagWeight = if (collapsedPanel == 2) 1f else splitRatio
                            Column(Modifier.fillMaxWidth().weight(tagWeight).padding(top = 6.dp)) {
                                Row(Modifier.fillMaxWidth().padding(bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("标签", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = NoteInk)
                                    Spacer(Modifier.weight(1f))
                                    Text("${visibleCategories.size} 个", color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                                }
                                if (visibleCategories.isNotEmpty()) {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3),
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        items(visibleCategories, key = { it.id }) { category ->
                                            Surface(
                                                color = tagColor(category),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth().aspectRatio(1.15f).clickable { selectedPath = category.path },
                                            ) {
                                                Column(Modifier.fillMaxSize().padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                                    Text(category.name, color = NoteInk, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                                    Text("${notes.count { noteHasCategoryPath(it, category.path, categories) }} 篇", color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                                                }
                                            }
                                        }
                                    }
                                } else if (query.isNotBlank()) {
                                    Text("没有匹配的标签", color = NoteMuted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 8.dp))
                                }
                            }
                        }
                        Box(Modifier.fillMaxWidth().height(36.dp)) {
                            Box(
                                Modifier.align(Alignment.Center).width(68.dp).height(22.dp)
                                    .shadow(2.dp, RoundedCornerShape(12.dp))
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .pointerInput(availableHeightPx, collapsedPanel) {
                                        detectVerticalDragGestures { change, dragAmount ->
                                            change.consume()
                                            if (availableHeightPx > 0f && collapsedPanel == null) {
                                                splitRatio = (splitRatio + dragAmount / availableHeightPx).coerceIn(0.18f, 0.82f)
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.DragHandle, "拖动调整标签和笔记区域", tint = NoteMuted, modifier = Modifier.size(19.dp))
                            }
                            Row(Modifier.align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { collapsedPanel = if (collapsedPanel == 1) null else 1 }, modifier = Modifier.size(34.dp)) {
                                    Icon(Icons.Filled.KeyboardArrowUp, "收起标签，仅显示笔记", tint = Color(0xFF3478B8))
                                }
                                IconButton(onClick = { collapsedPanel = if (collapsedPanel == 2) null else 2 }, modifier = Modifier.size(34.dp)) {
                                    Icon(Icons.Filled.KeyboardArrowDown, "收起笔记，仅显示标签", tint = Color(0xFF3478B8))
                                }
                            }
                        }
                        if (collapsedPanel != 2) {
                            val noteWeight = if (collapsedPanel == 1) 1f else 1f - splitRatio
                            Column(Modifier.fillMaxWidth().weight(noteWeight).padding(top = 4.dp)) {
                                Row(Modifier.fillMaxWidth().padding(bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (currentCategory == null) "相关笔记" else "${currentCategory.name}及子标签笔记", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = NoteInk)
                                    Spacer(Modifier.weight(1f))
                                    TextButton(onClick = { sortByUpdate = !sortByUpdate }) {
                                        Text(if (sortByUpdate) "按更新时间" else "按创建时间", color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
                                    items(visibleNotes, key = { it.id }) { note ->
                                        val title = note.content.lineSequence().firstOrNull { it.isNotBlank() }?.take(120) ?: "（空白笔记）"
                                        Card(
                                            Modifier.fillMaxWidth().combinedClickable(
                                                onClick = { if (onSelectNote != null) onSelectNote(note) else selectedNoteId = note.id },
                                                onDoubleClick = { onEditNote(note) },
                                                onLongClick = { previewNote = note },
                                            ),
                                            colors = CardDefaults.cardColors(containerColor = if (selectedNoteId == note.id) NoteSoft else Color.White),
                                            shape = RoundedCornerShape(10.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        ) {
                                            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                                Text(title, color = NoteInk, style = MaterialTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                                val excerpt = note.content.lineSequence().drop(1).firstOrNull { it.isNotBlank() }
                                                if (!excerpt.isNullOrBlank()) Text(excerpt, Modifier.padding(top = 4.dp), color = NoteMuted, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                                val paths = noteTagPaths(note, categories)
                                                if (paths.isNotEmpty()) Text(paths.joinToString(" · "), Modifier.padding(top = 5.dp), color = NotePrimary, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                Text(prettyTime(if (sortByUpdate) note.updatedAt else note.createdAt), Modifier.padding(top = 5.dp), color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                    if (visibleNotes.isEmpty()) item { EmptyHint(if (query.isBlank()) "还没有笔记" else "没有找到相关笔记") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    previewNote?.let { note ->
        var previewExpanded by remember(note.id) { mutableStateOf(false) }
        BackHandler(enabled = previewExpanded) { previewExpanded = false }
        Dialog(onDismissRequest = { previewNote = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            val previewModifier = if (previewExpanded) {
                Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)
            } else {
                Modifier.fillMaxWidth().fillMaxHeight(0.82f).padding(horizontal = 18.dp)
            }
            Surface(
                previewModifier.pointerInput(previewExpanded) {
                    var dragStartX = 0f
                    var dragDistance = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { point -> dragStartX = point.x; dragDistance = 0f },
                        onHorizontalDrag = { change, amount ->
                            if (dragStartX <= 36.dp.toPx()) {
                                change.consume()
                                dragDistance += amount
                            }
                        },
                        onDragEnd = {
                            if (previewExpanded && dragStartX <= 36.dp.toPx() && dragDistance >= 80.dp.toPx()) previewExpanded = false
                        },
                        onDragCancel = { dragDistance = 0f },
                    )
                },
                color = Color.White,
                shape = if (previewExpanded) RoundedCornerShape(0.dp) else RoundedCornerShape(12.dp),
                shadowElevation = if (previewExpanded) 0.dp else 8.dp,
            ) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(prettyTime(note.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { previewExpanded = !previewExpanded }) {
                            Icon(if (previewExpanded) Icons.Filled.CloseFullscreen else Icons.Filled.OpenInFull, if (previewExpanded) "缩小预览" else "全屏查看", tint = NoteMuted)
                        }
                        IconButton(onClick = { previewNote = null }) { Icon(Icons.Filled.Close, "关闭预览", tint = NoteMuted) }
                    }
                    CommentedNotePreview(note.content, note.comments, {}, showEye = false, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                    val paths = noteTagPaths(note, categories)
                    if (paths.isNotEmpty()) Text(paths.joinToString(" · "), color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 10.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrganizerPage(state: NotesState, padding: PaddingValues, onUpdate: (NotesState) -> Unit, onSaveNote: (QuickNote) -> Unit, onEdit: (QuickNote) -> Unit, onDelete: (QuickNote) -> Unit, onDismissDeleteNotice: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var index by remember { mutableStateOf(0) }
    var selected by remember { mutableStateOf<QuickNote?>(null) }
    var photoMode by rememberSaveable { mutableStateOf(false) }
    var photoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var photoIndex by remember { mutableStateOf(0) }
    var photoDraft by remember { mutableStateOf("") }
    var deletePhotoAfterSave by remember { mutableStateOf(false) }
    var photoPreview by remember { mutableStateOf<Uri?>(null) }
    var mode by rememberSaveable { mutableStateOf("ALL") }
    var monthFilter by rememberSaveable { mutableStateOf("全部月份") }
    var batchInput by rememberSaveable { mutableStateOf("20") }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showNoteDeleteDialog by remember { mutableStateOf(false) }
    var undoState by remember { mutableStateOf<NotesState?>(null) }
    val deleteUndoStack = remember { mutableStateListOf<Pair<NotesState, String>>() }
    var pendingFocusId by remember { mutableStateOf<String?>(null) }
    var lastWasSkip by remember { mutableStateOf(false) }
    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            photoUris = queryFolderImages(context, uri)
            photoIndex = 0
            photoDraft = ""
        }
    }
    val inbox = state.notes.filter { it.status == "inbox" }
    val inboxIds = inbox.map { it.id }
    val batchCount = batchInput.toIntOrNull()?.coerceIn(1, 500) ?: 20
    val monthOptions = inbox.map { prettyMonth(it.createdAt) }.distinct().sortedDescending()
    val batch = remember(mode, monthFilter, inboxIds, batchCount, pendingFocusId) {
        val source = if (mode == "MONTH" && monthFilter != "全部月份") inbox.filter { prettyMonth(it.createdAt) == monthFilter } else inbox
        val selectedBatch = if (mode == "RANDOM") source.shuffled().take(batchCount) else if (mode == "MONTH") source.take(batchCount) else source
        val focusNote = source.firstOrNull { it.id == pendingFocusId }
        if (focusNote != null && selectedBatch.none { it.id == focusNote.id }) selectedBatch.dropLast(1) + focusNote else selectedBatch
    }
    val currentId = batch.getOrNull(index.coerceAtMost((batch.size - 1).coerceAtLeast(0)))?.id
    val current = currentId?.let { id -> state.notes.firstOrNull { it.id == id } }
    var organizerFlipped by remember(currentId) { mutableStateOf(false) }
    fun deleteForUndo(note: QuickNote) {
        deleteUndoStack.add(state to note.id)
        undoState = null
        lastWasSkip = false
        onDelete(note)
        index = index.coerceAtMost((batch.size - 2).coerceAtLeast(0))
    }
    val pageScroll = rememberScrollState()
    LaunchedEffect(mode, monthFilter, batchCount) {
        index = 0
        undoState = null
        deleteUndoStack.clear()
        lastWasSkip = false
    }
    LaunchedEffect(batch, pendingFocusId) {
        val noteId = pendingFocusId ?: return@LaunchedEffect
        val restoredIndex = batch.indexOfFirst { it.id == noteId }
        if (restoredIndex >= 0) {
            index = restoredIndex
        }
    }
    Box(Modifier.fillMaxSize().padding(padding).padding(16.dp).pointerInput(current?.id, photoMode) {
        if (!photoMode && current != null) {
            var dragDistance = 0f
            detectHorizontalDragGestures(
                onHorizontalDrag = { _, amount -> dragDistance += amount },
                onDragEnd = {
                    if (abs(dragDistance) > 80f) index = (index + if (dragDistance < 0f) 1 else -1).coerceIn(0, (batch.size - 1).coerceAtLeast(0))
                    dragDistance = 0f
                },
                onDragCancel = { dragDistance = 0f },
            )
        }
    }) {
        Column(Modifier.fillMaxSize().verticalScroll(pageScroll).padding(bottom = 92.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("整理台", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(if (photoMode) "选择手机文件夹查看照片" else "一次处理一条，评论会自动显示", color = NoteMuted)
            }
            TextButton(onClick = { photoMode = !photoMode }) {
                Icon(if (photoMode) Icons.Filled.Inbox else Icons.Filled.Folder, null)
                Spacer(Modifier.width(4.dp))
                Text(if (photoMode) "随记" else "照片")
            }
        }
        if (photoMode) {
            Button(onClick = { folderPicker.launch(null) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Folder, null); Spacer(Modifier.width(6.dp)); Text(if (photoUris.isEmpty()) "导入手机照片文件夹" else "重新选择文件夹")
            }
            if (photoUris.isEmpty()) EmptyHint("选择一个手机文件夹后，这里会显示其中的照片")
            else PhotoOrganizerPanel(context, photoUris, photoIndex, photoDraft, deletePhotoAfterSave, state.categories, state.notes, Modifier.weight(1f), onDraftChange = { photoDraft = it }, onDeleteChange = { deletePhotoAfterSave = it }, onPreview = { photoPreview = it }, onSave = {
                val uri = photoUris.getOrNull(photoIndex) ?: return@PhotoOrganizerPanel
                if (photoDraft.trim().isNotEmpty()) {
                    val now = System.currentTimeMillis()
                    val note = QuickNote(UUID.randomUUID().toString(), photoDraft.trim(), now, now, emptyList(), imageUris = listOf(uri.toString()))
                    onUpdate(state.copy(notes = listOf(note) + state.notes))
                    val shouldDelete = deletePhotoAfterSave
                    if (shouldDelete) {
                        runCatching { DocumentsContract.deleteDocument(context.contentResolver, uri) }
                        photoUris = photoUris.filterNot { it == uri }
                    }
                    photoDraft = ""
                    deletePhotoAfterSave = false
                    photoIndex = if (shouldDelete) photoIndex.coerceAtMost((photoUris.lastIndex).coerceAtLeast(0)) else (photoIndex + 1).coerceAtMost(photoUris.lastIndex)
                }
            }, onSaveEditedNote = { note ->
                onSaveNote(note)
            }, onAdvance = {
                val uri = photoUris.getOrNull(photoIndex)
                if (deletePhotoAfterSave && uri != null) {
                    runCatching { DocumentsContract.deleteDocument(context.contentResolver, uri) }
                    photoUris = photoUris.filterNot { it == uri }
                }
                photoDraft = ""
                deletePhotoAfterSave = false
                photoIndex = photoIndex.coerceAtMost((photoUris.lastIndex).coerceAtLeast(0))
            })
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("MONTH" to "按月份", "RANDOM" to "随机", "ALL" to "全部").forEach { (value, label) ->
                    TextButton(onClick = { mode = value; if (value == "MONTH" && monthOptions.isNotEmpty()) showMonthPicker = true }, modifier = Modifier.weight(1f)) { Text(label, color = if (mode == value) NotePrimary else NoteMuted, fontWeight = if (mode == value) FontWeight.Bold else FontWeight.Normal) }
                }
            }
            if (mode == "MONTH") OutlinedButton(onClick = { showMonthPicker = true }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Filled.CalendarMonth, null); Spacer(Modifier.width(6.dp)); Text(monthFilter) }
            if (mode == "MONTH" || mode == "RANDOM") OutlinedTextField(batchInput, { value -> batchInput = value.filter { it.isDigit() }.take(3) }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("本轮整理数量（1-500）") })
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (current == null) {
                    EmptyHint("随记箱为空，所有记录都整理好了")
                } else {
                    Text("${index + 1} / ${batch.size}", color = NoteMuted, style = MaterialTheme.typography.labelLarge)
                    Surface(color = Color.White, shape = RoundedCornerShape(12.dp), shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = {}, onDoubleClick = { if (!organizerFlipped) onEdit(current) }, onLongClick = { organizerFlipped = !organizerFlipped })) {
                        Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp)) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text(prettyTime(current.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                                    moodEmoji(current.mood).takeIf { it.isNotEmpty() }?.let { Text(it, modifier = Modifier.padding(start = 6.dp), fontSize = 15.sp) }
                                    current.location?.let { Text(" · $it", color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false)) }
                                    Spacer(Modifier.weight(1f))
                                    if (current.pinned) Icon(Icons.Filled.PushPin, "已置顶", tint = NoteInk, modifier = Modifier.size(16.dp))
                                    if (current.favorite) Icon(Icons.Filled.Favorite, "已收藏", tint = NoteAccent, modifier = Modifier.size(16.dp))
                                    NoteOverflowMenu(
                                        note = current,
                                        onToggleFavorite = {
                                            deleteUndoStack.clear()
                                            undoState = state
                                            lastWasSkip = false
                                            onUpdate(state.copy(notes = state.notes.map { if (it.id == current.id) it.copy(favorite = !it.favorite) else it }))
                                        },
                                        onTogglePinned = {
                                            if (!current.pinned && state.notes.count { it.pinned } >= MAX_PINNED_NOTES) Toast.makeText(context, "最多置顶6篇哦", Toast.LENGTH_SHORT).show()
                                            else {
                                                deleteUndoStack.clear()
                                                undoState = state
                                                lastWasSkip = false
                                                onUpdate(state.copy(notes = state.notes.map { if (it.id == current.id) it.copy(pinned = !it.pinned) else it }))
                                            }
                                        },
                                        onDelete = { deleteForUndo(current) },
                                        showDeleteDialog = showNoteDeleteDialog,
                                        onDeleteDialogChange = { showNoteDeleteDialog = it },
                                        onFlip = { organizerFlipped = !organizerFlipped },
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                NotePreviewFlip(current, state.categories, state.notes, organizerFlipped, onOpenNote = onEdit, onFlip = { organizerFlipped = !organizerFlipped }) {
                                    CommentedNotePreview(current.content, current.comments, {}, showEye = false)
                                }
                                val paths = noteTagPaths(current, state.categories)
                                if (paths.isNotEmpty()) Text(paths.joinToString(" · "), color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
                Text("左右滑动切换记录 · 跳过的记录仍会留在随记箱", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 8.dp))
            }
        }
        }
        if (!photoMode) Surface(color = Color.White, shadowElevation = 8.dp, shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB8B0A8)), modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(enabled = current != null, onClick = { current?.let { note -> deleteUndoStack.clear(); undoState = state; lastWasSkip = false; onUpdate(state.copy(notes = state.notes.map { if (it.id == note.id) it.copy(favorite = !it.favorite) else it })) } }) {
                        Icon(if (current?.favorite == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "收藏", tint = if (current?.favorite == true) NoteAccent else NotePrimary)
                    }
                    IconButton(enabled = current != null, onClick = { if (current != null) selected = current }) { Icon(Icons.Filled.Label, "归类") }
                    IconButton(enabled = current != null, onClick = { current?.let(::deleteForUndo) }) { Icon(Icons.Filled.DeleteOutline, "删除") }
                    IconButton(onClick = {
                        when {
                            lastWasSkip -> { index = (index - 1).coerceAtLeast(0); lastWasSkip = false }
                            deleteUndoStack.isNotEmpty() -> {
                                val (snapshot, restoreId) = deleteUndoStack.removeAt(deleteUndoStack.lastIndex)
                                onDismissDeleteNotice()
                                onUpdate(snapshot)
                                pendingFocusId = restoreId
                            }
                            undoState != null -> {
                                onDismissDeleteNotice()
                                onUpdate(undoState!!)
                                undoState = null
                            }
                        }
                    }, enabled = undoState != null || deleteUndoStack.isNotEmpty() || lastWasSkip) { Icon(Icons.Filled.Undo, "撤销") }
                }
            }
        }
    selected?.let { note -> CategoryPicker(state.categories, onDismiss = { selected = null }, onPick = { category -> deleteUndoStack.clear(); undoState = state; lastWasSkip = false; onUpdate(state.copy(notes = state.notes.map { if (it.id == note.id) it.copy(categoryIds = listOf(category.id), status = "organized", updatedAt = System.currentTimeMillis()) else it })); selected = null; index++ }, onCreate = { raw ->
        val clean = raw.trim().trimStart('#')
        if (clean.isNotBlank()) {
            val updatedCategories = state.categories.toMutableList()
            var parentId: String? = null
            var path = ""
            var category: NoteCategory? = null
            clean.split('/').filter { it.isNotBlank() }.forEach { part ->
                path = if (path.isBlank()) part else "$path/$part"
                val existing = updatedCategories.firstOrNull { it.path == path }
                val currentCategory = existing ?: NoteCategory(UUID.randomUUID().toString(), part, parentId, path).also { updatedCategories += it }
                parentId = currentCategory.id
                category = currentCategory
            }
            val selectedCategory = category ?: return@CategoryPicker
            deleteUndoStack.clear()
            undoState = state
            lastWasSkip = false
            onUpdate(state.copy(categories = updatedCategories, notes = state.notes.map { if (it.id == note.id) it.copy(categoryIds = listOf(selectedCategory.id), status = "organized", updatedAt = System.currentTimeMillis()) else it }))
            selected = null
            index++
        }
    }) }
    showMonthPicker.takeIf { it }?.let { AlertDialog(onDismissRequest = { showMonthPicker = false }, title = { Text("选择月份") }, text = { Column { monthOptions.forEach { month -> TextButton(onClick = { monthFilter = month; showMonthPicker = false }, modifier = Modifier.fillMaxWidth()) { Text(month) } } } }, confirmButton = { TextButton(onClick = { showMonthPicker = false }) { Text("取消") } }) }
    photoPreview?.let { uri -> ImagePreviewDialog(context, uri, onDismiss = { photoPreview = null }) }
}

@Composable
private fun CategoryPicker(categories: List<NoteCategory>, onDismiss: () -> Unit, onPick: (NoteCategory) -> Unit, onCreate: (String) -> Unit = {}) {
    var newCategory by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    val visibleCategories = categories.sortedBy { it.path }.filter { query.isBlank() || it.path.contains(query, ignoreCase = true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择标签") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("搜索已有标签") }, leadingIcon = { Icon(Icons.Filled.Search, null) })
                OutlinedTextField(newCategory, { newCategory = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("新建标签路径") }, placeholder = { Text("例如：生活/阅读") })
                if (newCategory.isNotBlank()) {
                    TextButton(onClick = { onCreate(newCategory) }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Add, null); Spacer(Modifier.width(6.dp)); Text("新建并归类到 #${newCategory.trim().trimStart('#')}")
                    }
                }
                Text("已有标签", color = NoteMuted, style = MaterialTheme.typography.labelMedium)
                if (visibleCategories.isEmpty()) {
                    EmptyHint(if (categories.isEmpty()) "还没有标签，可在上方新建" else "没有匹配的标签")
                } else {
                    LazyColumn(Modifier.fillMaxWidth().heightIn(max = 280.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(visibleCategories, key = { it.id }) { category ->
                            val depth = category.path.count { it == '/' }
                            Row(
                                Modifier.fillMaxWidth().padding(start = (depth * 16).dp).clickable { onPick(category) }.padding(horizontal = 8.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(color = tagColor(category), shape = RoundedCornerShape(6.dp), modifier = Modifier.size(30.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Label, null, tint = NoteInk, modifier = Modifier.size(16.dp)) }
                                }
                                Column(Modifier.weight(1f).padding(start = 10.dp)) {
                                    Text(category.name, color = NoteInk, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    if (depth > 0) Text(category.path, color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                val hasChildren = categories.any { it.parentId == category.id }
                                if (hasChildren) Icon(Icons.Filled.KeyboardArrowRight, null, tint = NoteMuted, modifier = Modifier.size(18.dp))
                            }
                            HorizontalDivider(color = NoteSoft)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun QuickNoteEditor(note: QuickNote, categories: List<NoteCategory>, allNotes: List<QuickNote>, onDismiss: () -> Unit, onSave: (QuickNote) -> Unit, onCreateLinkedNote: (QuickNote) -> Unit, onOpenLinkedNote: (QuickNote) -> Unit, onNext: () -> Unit, onDelete: (QuickNote) -> Unit, onToggleFavorite: (Boolean) -> Unit, onTogglePinned: (Boolean) -> Boolean) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf(TextFieldValue(note.content, TextRange(note.content.length))) }
    var emptyNumberedMarker by remember { mutableStateOf<IntRange?>(null) }
    val undoStack = remember { mutableStateListOf<TextFieldValue>() }
    val redoStack = remember { mutableStateListOf<TextFieldValue>() }
    var location by remember { mutableStateOf(note.location) }
    var mood by remember { mutableStateOf(note.mood) }
    var comments by remember { mutableStateOf(note.comments) }
    var editorTextLayout by remember { mutableStateOf<TextLayoutResult?>(null) }
    var editorTextLayoutSource by remember { mutableStateOf<String?>(null) }
    var editorPosition by remember { mutableStateOf<IntOffset?>(null) }
    val editorScroll = rememberScrollState()
    var editorViewportHeight by remember { mutableStateOf(0) }
    var nativeImeBottom by remember { mutableStateOf(0) }
    val editorVisualTransformation = remember(comments) { MarkdownVisualTransformation(comments) }
    var commentDraft by remember { mutableStateOf("") }
    var activeComment by remember { mutableStateOf<Int?>(null) }
    var bodyFocusRequest by remember { mutableStateOf(0) }
    var bodyFocusSegment by remember { mutableStateOf(0) }
    var createdAt by remember { mutableStateOf(note.createdAt) }
    var showTimeDialog by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf(note.imageUris) }
    var previewUri by remember { mutableStateOf<Uri?>(null) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    var showNoteDeleteDialog by remember { mutableStateOf(false) }
    var showEditorActions by remember { mutableStateOf(false) }
    var showMoreMarkdownTools by remember { mutableStateOf(false) }
    var showAddTools by remember { mutableStateOf(false) }
    var showReferencePicker by remember { mutableStateOf(false) }
    var showConnectionsBack by remember { mutableStateOf(false) }
    var referenceQuery by remember { mutableStateOf("") }
    var referenceSortByUpdate by remember { mutableStateOf(true) }
    var showLinkDialog by remember { mutableStateOf(false) }
    var linkUrl by remember { mutableStateOf("") }
    var linkTitle by remember { mutableStateOf("") }
    var linkDetails by remember { mutableStateOf("") }
    var savedNote by remember { mutableStateOf(note) }
    val recentTagCategories = remember(categories, allNotes) {
        val categoryById = categories.associateBy { it.id }
        val lastUsed = mutableMapOf<String, Long>()
        allNotes.sortedByDescending { it.createdAt }.forEach { existingNote ->
            existingNote.categoryIds.mapNotNull { categoryById[it]?.path }.forEach { path -> lastUsed.putIfAbsent(path, existingNote.createdAt) }
            NoteTagRegex.findAll(existingNote.content).map { it.groupValues[1] }.forEach { path -> lastUsed.putIfAbsent(path, existingNote.createdAt) }
        }
        categories.withIndex()
            .sortedWith(compareByDescending<IndexedValue<NoteCategory>> { lastUsed[it.value.path] ?: Long.MIN_VALUE }.thenBy { it.index })
            .map { it.value }
    }
    val recentTopLevelCategories = recentTagCategories.filter { it.parentId == null }.take(8)
    val linkedNotes = remember(text.text, allNotes) {
        NoteReferenceRegex.findAll(text.text).mapNotNull { match -> allNotes.firstOrNull { it.id == match.groupValues[1] } }.distinctBy { it.id }.toList()
    }
    val associatedCategories = remember(text.text, note.categoryIds, categories) {
        val paths = (note.categoryIds.mapNotNull { id -> categories.firstOrNull { it.id == id }?.path } + NoteTagRegex.findAll(text.text).map { it.groupValues[1] }).distinct()
        paths.filterNot { path -> paths.any { other -> other.startsWith("$path/") } }
            .map { path -> categories.firstOrNull { it.path == path } ?: NoteCategory("inline:$path", path.substringAfterLast('/'), null, path) }
    }
    val activeTagQuery = remember(text) {
        if (!text.selection.collapsed) null else {
            val cursor = text.selection.start.coerceIn(0, text.text.length)
            Regex("(?<![\\p{L}\\d_])#([\\p{L}\\d/._-]*)$")
                .find(text.text.substring(0, cursor))
                ?.let { match -> Triple(match.range.first, cursor, match.groupValues[1]) }
        }
    }
    val inlineTagSuggestions = activeTagQuery?.let { (_, _, query) ->
        val parentPath = when {
            query.endsWith('/') -> query.dropLast(1)
            '/' in query -> query.substringBeforeLast('/')
            else -> null
        }
        if (parentPath == null) {
            if (query.isBlank()) recentTopLevelCategories else categories
                .filter { it.path.contains(query, ignoreCase = true) }
                .sortedWith(
                    compareBy<NoteCategory> {
                        when {
                            it.name.startsWith(query, ignoreCase = true) -> 0
                            it.path.split('/').any { segment -> segment.startsWith(query, ignoreCase = true) } -> 1
                            else -> 2
                        }
                    }.thenBy { it.path },
                )
                .take(8)
        } else {
            val parent = categories.firstOrNull { it.path == parentPath }
            val childQuery = if (query.endsWith('/')) "" else query.substringAfterLast('/')
            categories
                .filter { parent != null && it.parentId == parent.id && it.name.contains(childQuery, ignoreCase = true) }
                .sortedWith(compareBy<NoteCategory> { if (it.name.startsWith(childQuery, ignoreCase = true)) 0 else 1 }.thenBy { it.name })
                .take(8)
        }
    }.orEmpty()
    fun currentNote(): QuickNote = savedNote.copy(
        content = text.text.trim(),
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis(),
        location = location,
        mood = mood,
        comments = comments.filter { it.text.isNotBlank() },
        imageUris = imageUris,
    )
    fun saveChanges() {
        val next = currentNote()
        onSave(next)
        comments = next.comments
        savedNote = next
    }
    fun hasChanges(): Boolean = text.text.trim() != savedNote.content.trim() || location != savedNote.location || mood != savedNote.mood || comments != savedNote.comments || createdAt != savedNote.createdAt || imageUris != savedNote.imageUris
    fun requestDismiss() { if (hasChanges()) showUnsavedDialog = true else onDismiss() }
    fun saveAndNext() {
        if (text.text.trim().isNotEmpty() || imageUris.isNotEmpty()) saveChanges()
        onNext()
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    fun replaceEditorText(value: TextFieldValue) {
        emptyNumberedMarker = null
        if (value != text) {
            undoStack.add(text)
            if (undoStack.size > 50) undoStack.removeAt(0)
            redoStack.clear()
            text = value
        }
    }
    fun shiftCommentAnchors(transform: (Int) -> Int) {
        comments = comments.map { comment -> comment.copy(position = transform(comment.position).coerceIn(0, text.text.length)) }
    }
    fun finishComment() {
        val index = activeComment ?: return
        val position = comments[index].position.coerceIn(0, text.text.length)
        bodyFocusSegment = comments.withIndex().sortedBy { it.value.position }.indexOfFirst { it.index == index } + 1
        comments = comments.mapIndexed { i, comment -> if (i == index) comment.copy(text = commentDraft.trim(), expanded = true) else comment }
        text = text.copy(selection = TextRange(position), composition = null)
        activeComment = null
        bodyFocusRequest++
    }
    fun handleEditorInput(value: TextFieldValue) {
        val oldText = text.text
        val newText = value.text
        val spaceIndex = value.selection.start - 1
        val insertedSpace = value.selection.collapsed && newText.length == oldText.length + 1 &&
            spaceIndex >= 0 && newText.getOrNull(spaceIndex) == ' ' &&
            newText.removeRange(spaceIndex, value.selection.start) == oldText
        if (insertedSpace) {
            val marker = emptyNumberedMarker
            if (marker != null && marker.last + 1 == spaceIndex) {
                val lineEnd = newText.indexOf('\n', value.selection.start).let { if (it < 0) newText.length else it }
                if (newText.substring(value.selection.start, lineEnd).isBlank()) {
                    val cleaned = newText.removeRange(marker.first, lineEnd)
                    replaceEditorText(TextFieldValue(cleaned, TextRange(marker.first)))
                    return
                }
            }
            val lineStart = newText.lastIndexOf('\n', (spaceIndex - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
            val typedMarker = Regex("^(\\s*\\d+[.)]\\s)$").matchEntire(newText.substring(lineStart, value.selection.start))
            replaceEditorText(value)
            if (typedMarker != null) emptyNumberedMarker = lineStart until value.selection.start
            return
        }
        val insertedNewline = newText.length == oldText.length + 1 && value.selection.start > 0 && newText.getOrNull(value.selection.start - 1) == '\n'
        if (!insertedNewline) {
            replaceEditorText(value)
            return
        }
        val newline = value.selection.start - 1
        val lineStart = newText.lastIndexOf('\n', (newline - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
        val previousLine = newText.substring(lineStart, newline)
        val emptyTask = Regex("^(\\s*)- \\[ \\]\\s*$").find(previousLine)
        val task = Regex("^(\\s*)- \\[[ xX]\\]\\s+.+$").find(previousLine)
        val emptyBullet = Regex("^(\\s*)([-*+])\\s*$").find(previousLine)
        val bullet = Regex("^(\\s*)([-*+])\\s+.+$").find(previousLine)
        val emptyNumbered = Regex("^(\\s*)\\d+[.)]\\s+$").find(previousLine)
        val numbered = Regex("^(\\s*)(\\d+)[.)]\\s+(.+)$").find(previousLine)
        when {
            emptyTask != null -> {
                val cleaned = newText.removeRange(lineStart, newline)
                replaceEditorText(TextFieldValue(cleaned, TextRange(value.selection.start - (newline - lineStart))))
            }
            emptyBullet != null -> {
                val cleaned = newText.removeRange(lineStart, newline)
                replaceEditorText(TextFieldValue(cleaned, TextRange(value.selection.start - (newline - lineStart))))
            }
            emptyNumbered != null -> {
                val cleaned = newText.removeRange(lineStart, newline)
                replaceEditorText(TextFieldValue(cleaned, TextRange(value.selection.start - (newline - lineStart))))
            }
            task != null -> {
                val prefix = task.groupValues[1] + "- [ ] "
                val next = newText.substring(0, value.selection.start) + prefix + newText.substring(value.selection.start)
                replaceEditorText(TextFieldValue(next, TextRange(value.selection.start + prefix.length)))
            }
            bullet != null -> {
                val prefix = bullet.groupValues[1] + bullet.groupValues[2] + " "
                val next = newText.substring(0, value.selection.start) + prefix + newText.substring(value.selection.start)
                replaceEditorText(TextFieldValue(next, TextRange(value.selection.start + prefix.length)))
            }
            numbered != null -> {
                val nextNumber = numbered.groupValues[2].toIntOrNull()?.plus(1) ?: 1
                val prefix = numbered.groupValues[1] + nextNumber + ". "
                val next = newText.substring(0, value.selection.start) + prefix + newText.substring(value.selection.start)
                replaceEditorText(TextFieldValue(next, TextRange(value.selection.start + prefix.length)))
                emptyNumberedMarker = value.selection.start until (value.selection.start + prefix.length)
            }
            else -> replaceEditorText(value)
        }
    }
    fun wrapSelection(prefix: String, suffix: String = prefix) {
        val start = text.selection.start.coerceIn(0, text.text.length)
        val end = text.selection.end.coerceIn(start, text.text.length)
        val selected = text.text.substring(start, end)
        val replacement = if (selected.isEmpty()) prefix + suffix else prefix + selected + suffix
        val next = text.text.substring(0, start) + replacement + text.text.substring(end)
        val cursor = if (selected.isEmpty()) start + prefix.length else start + replacement.length
        replaceEditorText(TextFieldValue(next, TextRange(cursor)))
        val delta = replacement.length - (end - start)
        shiftCommentAnchors { position ->
            when {
                position <= start -> position
                position >= end -> position + delta
                else -> position + prefix.length
            }
        }
    }
    fun prefixCurrentLine(prefix: String) {
        val start = text.selection.start.coerceIn(0, text.text.length)
        val lineStart = text.text.lastIndexOf('\n', (start - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
        replaceEditorText(TextFieldValue(text.text.substring(0, lineStart) + prefix + text.text.substring(lineStart), TextRange(start + prefix.length)))
        shiftCommentAnchors { if (it >= lineStart) it + prefix.length else it }
    }
    fun toggleTaskAt(position: Int) {
        val cursor = position.coerceIn(0, text.text.length)
        val lineStart = text.text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
        val lineEnd = text.text.indexOf('\n', cursor).let { if (it < 0) text.text.length else it }
        val line = text.text.substring(lineStart, lineEnd)
        val task = Regex("^(\\s*- \\[)([ xX])(\\])").find(line)
        if (task == null) {
            prefixCurrentLine("- [ ] ")
        } else {
            val mark = lineStart + task.range.first + task.groupValues[1].length
            val replacement = if (task.groupValues[2].equals(" ")) "x" else " "
            replaceEditorText(TextFieldValue(text.text.replaceRange(mark, mark + 1, replacement), text.selection))
        }
    }
    fun toggleTaskAtCursor() = toggleTaskAt(text.selection.start)
    fun insertMarkdownAtSelection(markdown: String) {
        val start = text.selection.min.coerceIn(0, text.text.length)
        val end = text.selection.max.coerceIn(start, text.text.length)
        val next = text.text.substring(0, start) + markdown + text.text.substring(end)
        replaceEditorText(TextFieldValue(next, TextRange(start + markdown.length)))
        val delta = markdown.length - (end - start)
        shiftCommentAnchors { position ->
            when {
                position <= start -> position
                position >= end -> position + delta
                else -> start + markdown.length
            }
        }
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    fun insertNoteReference(target: QuickNote) {
        val title = target.content.lineSequence().firstOrNull { it.isNotBlank() }?.trim()?.take(48) ?: "无标题笔记"
        insertMarkdownAtSelection("[[note:${target.id}|${title.replace("]", "") }]]")
        showReferencePicker = false
        referenceQuery = ""
    }
    fun prefixOrderedList() {
        val cursor = text.selection.start.coerceIn(0, text.text.length)
        val lineStart = text.text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
        val previousLine = text.text.substring(0, lineStart).trimEnd('\n').lines().lastOrNull { it.isNotBlank() }
        val previousNumber = previousLine?.let { Regex("^\\s*(\\d+)[.)]\\s+.*$").matchEntire(it)?.groupValues?.get(1)?.toIntOrNull() } ?: 0
        val prefix = "${previousNumber + 1}. "
        val next = text.text.substring(0, lineStart) + prefix + text.text.substring(lineStart)
        replaceEditorText(TextFieldValue(next, TextRange(cursor + prefix.length)))
        shiftCommentAnchors { if (it >= lineStart) it + prefix.length else it }
        emptyNumberedMarker = lineStart until (lineStart + prefix.length)
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            if (hasChanges()) saveChanges()
        }
    }
    val locationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true || result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) scope.launch(Dispatchers.IO) { val label = getLastLocationLabel(context) ?: "位置已授权"; withContext(Dispatchers.Main) { location = label } }
    }
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            imageUris = (imageUris + uri.toString()).distinct()
        }
    }
    Dialog(onDismissRequest = ::requestDismiss, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        val editorView = LocalView.current
        val editorKeyboardController = LocalSoftwareKeyboardController.current
        DisposableEffect(editorView) {
            val window = (editorView.parent as? DialogWindowProvider)?.window
            window?.let { androidx.core.view.WindowCompat.setDecorFitsSystemWindows(it, false) }
            val observer = android.view.ViewTreeObserver.OnGlobalLayoutListener {
                val insets = androidx.core.view.ViewCompat.getRootWindowInsets(editorView)
                val ime = insets?.getInsets(androidx.core.view.WindowInsetsCompat.Type.ime())?.bottom ?: 0
                val visible = android.graphics.Rect()
                editorView.getWindowVisibleDisplayFrame(visible)
                val rootLocation = IntArray(2)
                editorView.rootView.getLocationOnScreen(rootLocation)
                val obscured = (editorView.rootView.height + rootLocation[1] - visible.bottom).coerceAtLeast(0)
                val fallback = if (obscured > editorView.resources.displayMetrics.density * 120) obscured else 0
                val bottom = maxOf(ime, fallback)
                nativeImeBottom = bottom
            }
            editorView.viewTreeObserver.addOnGlobalLayoutListener(observer)
            androidx.core.view.ViewCompat.requestApplyInsets(editorView)
            onDispose { editorView.viewTreeObserver.removeOnGlobalLayoutListener(observer) }
        }
        LaunchedEffect(editorView, note.id) {
            (editorView.parent as? DialogWindowProvider)?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            delay(150)
            focusRequester.requestFocus()
            delay(100)
            editorKeyboardController?.show()
        }
        Surface(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars.union(WindowInsets.ime).union(WindowInsets(bottom = nativeImeBottom))), color = Color.White) {
            Box(Modifier.fillMaxSize().padding(horizontal = 2.dp, vertical = 8.dp)) {
                Column(Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (activeComment != null) finishComment() else { if (text.text.trim().isNotEmpty() || imageUris.isNotEmpty()) saveChanges(); onDismiss() } }) { Icon(Icons.Filled.Check, "保存并返回首页") }
                    Text(if (note.content.isBlank()) "记录此刻" else "编辑随记", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { showConnectionsBack = !showConnectionsBack }) {
                        Icon(Icons.Filled.Flip, if (showConnectionsBack) "返回编辑笔记" else "查看标签和关联笔记", tint = NotePrimary)
                    }
                    Box {
                        IconButton(onClick = { showEditorActions = true }) { Icon(Icons.Filled.MoreVert, "笔记操作", tint = NoteMuted) }
                        NotePopupMenu(expanded = showEditorActions, onDismissRequest = { showEditorActions = false }, compact = true) {
                            NoteActionMenuItem(if (savedNote.favorite) "取消收藏" else "收藏", icon = { Icon(if (savedNote.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, null, tint = NoteAccent, modifier = Modifier.size(18.dp)) }, onClick = {
                                showEditorActions = false
                                saveChanges()
                                val favorite = !savedNote.favorite
                                onToggleFavorite(favorite)
                                savedNote = savedNote.copy(favorite = favorite)
                            })
                            NoteActionMenuItem(if (savedNote.pinned) "取消置顶" else "置顶", icon = { Icon(Icons.Filled.PushPin, null, tint = NoteMuted, modifier = Modifier.size(18.dp)) }, onClick = {
                                showEditorActions = false
                                saveChanges()
                                val pinned = !savedNote.pinned
                                if (onTogglePinned(pinned)) savedNote = savedNote.copy(pinned = pinned)
                            })
                            NoteActionMenuItem("复制笔记全文", icon = { Icon(Icons.Filled.ContentCopy, null, tint = NoteMuted, modifier = Modifier.size(18.dp)) }, onClick = {
                                showEditorActions = false
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("随心记", text.text))
                                Toast.makeText(context, "全文已复制", Toast.LENGTH_SHORT).show()
                            })
                            NoteActionMenuItem("删除笔记", icon = { Icon(Icons.Filled.DeleteOutline, null, tint = Color(0xFFB3261E), modifier = Modifier.size(18.dp)) }, onClick = {
                                showEditorActions = false
                                showNoteDeleteDialog = true
                            }, destructive = true)
                        }
                    }
                }
                Column(Modifier.fillMaxWidth().heightIn(max = 156.dp).verticalScroll(rememberScrollState())) {
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ToolButton(Icons.Filled.LocationOn, "位置", location != null) { if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) scope.launch(Dispatchers.IO) { val label = getLastLocationLabel(context) ?: "位置已授权"; withContext(Dispatchers.Main) { location = label } } else locationLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) }
                    ToolButton(Icons.Filled.Label, "标签", false) { showTagDialog = true; tagInput = "" }
                    ToolButton(Icons.Filled.CalendarMonth, "日期", false) { showTimeDialog = true }
                    ToolButton(moodIcon(mood), moodLabel(mood), mood.isNotEmpty()) { mood = when (mood) { "" -> "HAPPY"; "HAPPY" -> "NORMAL_SELECTED"; "NORMAL_SELECTED" -> "SAD"; else -> "" } }
                    ToolButton(Icons.Filled.ChatBubbleOutline, "评论", activeComment != null) { comments = comments + NoteComment(text.selection.start, "", true); activeComment = comments.lastIndex; commentDraft = "" }
                    ToolButton(Icons.Filled.PhotoLibrary, "图片", false) { imageLauncher.launch(arrayOf("image/*")) }
                }
                val inlineTags = NoteTagRegex.findAll(text.text).map { it.groupValues[1] }.distinct().toList()
                if (inlineTags.isNotEmpty()) Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { inlineTags.forEach { Text("#$it", color = NoteTagColor, fontSize = 10.sp, fontWeight = FontWeight.Medium) } }
                location?.let { Text("位置：$it", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 6.dp)) }
                Text("创建时间：${prettyTime(createdAt)}", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 8.dp))
                if (imageUris.isNotEmpty()) {
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        imageUris.forEach { uriString ->
                            FolderPhotoThumb(context, Uri.parse(uriString), Modifier.size(76.dp).combinedClickable(onClick = { previewUri = Uri.parse(uriString) }, onLongClick = { previewUri = Uri.parse(uriString) }))
                        }
                    }
                }
                }
                val flipRotation by animateFloatAsState(if (showConnectionsBack) 180f else 0f, animationSpec = tween(420), label = "note-card-flip")
                val densityScale = androidx.compose.ui.platform.LocalDensity.current.density
                Box(Modifier.fillMaxWidth().weight(1f).graphicsLayer { rotationY = flipRotation; cameraDistance = 12f * densityScale }) {
                    if (flipRotation < 90f) {
                    val editorTextStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, color = Color.Black)
                    BoxWithConstraints(Modifier.fillMaxSize().onGloballyPositioned {
                        if (editorViewportHeight != it.size.height) editorViewportHeight = it.size.height
                    }) {
                        val density = androidx.compose.ui.platform.LocalDensity.current
                        val cursorMargin = with(density) { 32.dp.toPx() }
                        LaunchedEffect(editorTextLayout, text.selection, editorViewportHeight) {
                            val currentLayout = editorTextLayout
                            if (comments.isEmpty() && currentLayout != null && editorTextLayoutSource == text.text && editorViewportHeight > 0) {
                                val offset = editorVisualTransformation.filter(androidx.compose.ui.text.AnnotatedString(text.text))
                                    .offsetMapping.originalToTransformed(text.selection.end)
                                val cursor = currentLayout.getCursorRect(offset)
                                val top = cursor.top + with(density) { 16.dp.toPx() }
                                val bottom = cursor.bottom + with(density) { 16.dp.toPx() }
                                val visibleBottom = editorScroll.value + editorViewportHeight - cursorMargin
                                val target = when {
                                    bottom > visibleBottom -> bottom - editorViewportHeight + cursorMargin
                                    top < editorScroll.value -> top
                                    else -> editorScroll.value.toFloat()
                                }
                                editorScroll.scrollTo(target.roundToInt().coerceAtLeast(0))
                            }
                        }
                        val suggestions = inlineTagSuggestions.take(4)
                        val popupWidth = maxWidth * 0.70f
                        val popupHeight = (suggestions.size * 40 + 4).dp
                        val tagQuery = activeTagQuery
                        val layout = editorTextLayout?.takeIf { comments.isEmpty() && editorTextLayoutSource == text.text }
                        val cursorRect = if (tagQuery != null && layout != null) {
                            val transformedOffset = editorVisualTransformation
                                .filter(androidx.compose.ui.text.AnnotatedString(text.text))
                                .offsetMapping.originalToTransformed(tagQuery.first)
                            layout.getCursorRect(transformedOffset)
                        } else null
                        val fieldPosition = editorPosition
                        if (tagQuery != null && suggestions.isNotEmpty() && cursorRect != null && fieldPosition != null) {
                            val popupWidthPx = with(density) { popupWidth.toPx() }
                            val popupHeightPx = with(density) { popupHeight.toPx() }
                            val x = (fieldPosition.x + cursorRect.left)
                                .coerceIn(0f, (constraints.maxWidth - popupWidthPx).coerceAtLeast(0f))
                                .toInt()
                            val desiredBottom = fieldPosition.y + cursorRect.bottom - editorScroll.value - with(density) { 4.dp.toPx() }
                            val desiredY = desiredBottom - popupHeightPx
                            val maxY = (constraints.maxHeight - popupHeightPx).coerceAtLeast(0f)
                            val y = desiredY.coerceIn(0f, maxY).toInt()
                            Surface(
                                modifier = Modifier.offset { IntOffset(x, y) }.width(popupWidth).zIndex(1f),
                                color = Color(0xFFFFF8F2),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NoteSoft),
                                shadowElevation = 8.dp,
                            ) {
                                Column(Modifier.padding(vertical = 2.dp)) {
                                    suggestions.forEach { category ->
                                        TextButton(
                                            onClick = {
                                                val (start, cursor, _) = tagQuery
                                                val hasChildren = categories.any { it.parentId == category.id }
                                                val insertion = if (hasChildren) "#${category.path}/" else "#${category.path} "
                                                val suffix = text.text.substring(cursor).removePrefix(" ")
                                                val next = text.text.substring(0, start) + insertion + suffix
                                                replaceEditorText(TextFieldValue(next, TextRange(start + insertion.length)))
                                                focusRequester.requestFocus()
                                                keyboardController?.show()
                                            },
                                            modifier = Modifier.fillMaxWidth().height(40.dp),
                                        ) {
                                            Text("#${category.path}", maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 13.sp, color = NoteInk)
                                        }
                                    }
                                }
                            }
                        }
                        Box(Modifier.fillMaxSize().verticalScroll(editorScroll)) {
                            Column(Modifier.fillMaxWidth()) {
                            if (comments.isNotEmpty()) EmbeddedCommentEditor(
                                value = text,
                                comments = comments,
                                activeComment = activeComment,
                                focusRequest = bodyFocusRequest,
                                focusSegment = bodyFocusSegment,
                                initialFocus = focusRequester,
                                onBodyChange = { segment, start, end, value ->
                                    val next = text.text.substring(0, start) + value.text + text.text.substring(end)
                                    val ordered = comments.withIndex().sortedBy { it.value.position }
                                    val following = ordered.drop(segment).map { it.index }.toSet()
                                    val oldLength = text.text.length
                                    handleEditorInput(TextFieldValue(next, TextRange(start + value.selection.start, start + value.selection.end), value.composition?.let { TextRange(start + it.start, start + it.end) }))
                                    val delta = text.text.length - oldLength
                                    comments = comments.mapIndexed { index, comment -> if (index in following) comment.copy(position = (comment.position + delta).coerceIn(0, text.text.length)) else comment }
                                },
                                onCommentChange = { index, value ->
                                    commentDraft = value
                                    comments = comments.mapIndexed { i, comment -> if (i == index) comment.copy(text = value, expanded = true) else comment }
                                },
                                onDeleteComment = { index ->
                                    comments = comments.filterIndexed { i, _ -> i != index }
                                    if (activeComment == index) activeComment = null
                                },
                                onEditComment = { index -> activeComment = index; commentDraft = comments[index].text },
                                onToggleComment = { index -> comments = comments.mapIndexed { i, comment -> if (i == index) comment.copy(expanded = !comment.expanded) else comment } },
                                onDone = ::finishComment,
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                            ) else BasicTextField(
                            value = text,
                            onValueChange = ::handleEditorInput,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 48.dp).focusRequester(focusRequester).onGloballyPositioned {
                                val position = it.positionInParent()
                                val next = IntOffset(position.x.roundToInt(), position.y.roundToInt())
                                if (editorPosition != next) editorPosition = next
                            }.pointerInput(text.text, editorTextLayout) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                                    val layout = editorTextLayout
                                    val transformedOffset = layout?.getOffsetForPosition(down.position)
                                    val sourceOffset = transformedOffset?.let { offset ->
                                        editorVisualTransformation.filter(androidx.compose.ui.text.AnnotatedString(text.text))
                                            .offsetMapping.transformedToOriginal(offset)
                                    }
                                    val task = sourceOffset?.let { offset ->
                                        Regex("(?m)^[ \\t]*- \\[([ xX])\\]").findAll(text.text).firstOrNull { match ->
                                            match.range.first + match.value.lastIndexOf('[') == offset
                                        }
                                    }
                                    if (task != null) {
                                        down.consume()
                                        waitForUpOrCancellation(pass = PointerEventPass.Initial)?.let { up ->
                                            up.consume()
                                            toggleTaskAt(sourceOffset!!)
                                        }
                                    } else {
                                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                    }
                                }
                            },
                            textStyle = editorTextStyle,
                            visualTransformation = editorVisualTransformation,
                            maxLines = Int.MAX_VALUE,
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(NotePrimary),
                            onTextLayout = { layout ->
                                if (editorTextLayoutSource != text.text || editorTextLayout?.size != layout.size || editorTextLayout?.lineCount != layout.lineCount) {
                                    editorTextLayout = layout
                                    editorTextLayoutSource = text.text
                                }
                            },
                            decorationBox = { innerTextField ->
                                Box(Modifier.fillMaxWidth()) {
                                    if (text.text.isEmpty()) Text("写下此刻的想法…", style = editorTextStyle.copy(color = NoteMuted))
                                    innerTextField()
                                }
                            },
                            )
                            }
                        }
                    }
                    } else {
                        ConnectionBackFace(
                            categories = associatedCategories,
                            linkedNotes = linkedNotes,
                            modifier = Modifier.fillMaxSize().graphicsLayer { rotationY = 180f },
                            onOpenNote = { target -> saveChanges(); onOpenLinkedNote(target) },
                        )
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 4.dp,
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        if (showMoreMarkdownTools) {
                            Row(
                                Modifier.fillMaxWidth().height(48.dp).horizontalScroll(rememberScrollState()).padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                IconButton(onClick = { toggleTaskAtCursor() }, modifier = Modifier.size(42.dp)) { Icon(Icons.Filled.CheckBoxOutlineBlank, "插入或勾选任务", modifier = Modifier.size(21.dp)) }
                                IconButton(onClick = { prefixCurrentLine("- ") }, modifier = Modifier.size(42.dp)) { Icon(Icons.Filled.FormatListBulleted, "无序列表", modifier = Modifier.size(21.dp)) }
                                IconButton(onClick = { prefixOrderedList() }, modifier = Modifier.size(42.dp)) { Icon(Icons.Filled.FormatListNumbered, "有序列表", modifier = Modifier.size(21.dp)) }
                                IconButton(onClick = { wrapSelection("~~") }, modifier = Modifier.size(42.dp)) { Icon(Icons.Filled.StrikethroughS, "删除线", modifier = Modifier.size(21.dp)) }
                                IconButton(onClick = { prefixCurrentLine("> ") }, modifier = Modifier.size(42.dp)) { Icon(Icons.Filled.FormatQuote, "引用", modifier = Modifier.size(21.dp)) }
                                IconButton(onClick = { wrapSelection("*") }, modifier = Modifier.size(42.dp)) { Icon(Icons.Filled.FormatItalic, "斜体", modifier = Modifier.size(21.dp)) }
                                IconButton(enabled = undoStack.isNotEmpty(), onClick = {
                                    if (undoStack.isNotEmpty()) { val current = text; val previous = undoStack.removeAt(undoStack.lastIndex); redoStack.add(current); text = previous }
                                }, modifier = Modifier.size(42.dp)) { Icon(Icons.Filled.Undo, "撤回", modifier = Modifier.size(21.dp)) }
                                IconButton(enabled = redoStack.isNotEmpty(), onClick = {
                                    if (redoStack.isNotEmpty()) { val current = text; val next = redoStack.removeAt(redoStack.lastIndex); undoStack.add(current); text = next }
                                }, modifier = Modifier.size(42.dp)) { Icon(Icons.Filled.Redo, "重做", modifier = Modifier.size(21.dp)) }
                            }
                        }
                        if (showAddTools) {
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NoteSoft),
                                shadowElevation = 6.dp,
                            ) {
                                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = { showAddTools = false; showLinkDialog = true }) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Filled.Link, "链接", tint = NotePrimary, modifier = Modifier.size(22.dp))
                                            Text("链接", color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth().height(52.dp), verticalAlignment = Alignment.CenterVertically) {
                            Row(Modifier.weight(1f).horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    val cursor = text.selection.start.coerceIn(0, text.text.length)
                                    text = text.copy(text = text.text.substring(0, cursor) + "#" + text.text.substring(cursor), selection = TextRange(cursor + 1))
                                    focusRequester.requestFocus()
                                    keyboardController?.show()
                                }, modifier = Modifier.size(42.dp)) { Text("#", color = NotePrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                                IconButton(onClick = { showReferencePicker = true }, modifier = Modifier.size(42.dp)) { Text("@", color = NotePrimary, fontSize = 19.sp, fontWeight = FontWeight.Medium) }
                                IconButton(onClick = { wrapSelection("**") }, modifier = Modifier.size(42.dp)) { Icon(Icons.Filled.FormatBold, "加粗", modifier = Modifier.size(21.dp)) }
                                IconButton(onClick = { wrapSelection("==") }, modifier = Modifier.size(42.dp)) { Icon(Icons.Filled.FormatColorFill, "荧光笔", modifier = Modifier.size(21.dp)) }
                                IconButton(onClick = { wrapSelection("<u>", "</u>") }, modifier = Modifier.size(42.dp)) { Icon(Icons.Filled.FormatUnderlined, "下划线", modifier = Modifier.size(21.dp)) }
                                IconButton(onClick = { showMoreMarkdownTools = !showMoreMarkdownTools }, modifier = Modifier.size(42.dp)) {
                                    Icon(Icons.Filled.MoreVert, "更多格式工具", tint = if (showMoreMarkdownTools) NoteAccent else NotePrimary, modifier = Modifier.size(22.dp))
                                }
                            }
                            IconButton(onClick = { showAddTools = !showAddTools }, modifier = Modifier.size(42.dp)) {
                                    Icon(Icons.Filled.AddCircleOutline, "添加内容", tint = NotePrimary, modifier = Modifier.size(25.dp))
                            }
                            Surface(color = NotePrimary, shape = RoundedCornerShape(10.dp), modifier = Modifier.padding(end = 4.dp)) {
                                IconButton(onClick = { saveAndNext() }, modifier = Modifier.size(44.dp)) {
                                    Icon(Icons.Filled.Add, "保存并新建下一篇", tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }
    if (showUnsavedDialog) AlertDialog(
        onDismissRequest = { showUnsavedDialog = false },
        title = { Text("保存改动") },
        text = { Text("此处记录已有改动，是否保存编辑？") },
        confirmButton = { TextButton(onClick = { showUnsavedDialog = false; saveChanges(); onDismiss() }) { Text("保存") } },
        dismissButton = { Row { TextButton(onClick = { showUnsavedDialog = false; onDismiss() }) { Text("放弃") }; TextButton(onClick = { showUnsavedDialog = false }) { Text("继续编辑") } } },
    )
    if (showNoteDeleteDialog) AlertDialog(
        onDismissRequest = { showNoteDeleteDialog = false },
        title = { Text("删除笔记") },
        text = { Text("确定将这条笔记移入回收站吗？30 天内可恢复。") },
        confirmButton = { TextButton(onClick = { showNoteDeleteDialog = false; onDelete(currentNote()) }) { Text("删除") } },
        dismissButton = { TextButton(onClick = { showNoteDeleteDialog = false }) { Text("取消") } },
    )
    if (showTimeDialog) {
        LaunchedEffect(Unit) {
            val calendar = Calendar.getInstance().apply { timeInMillis = createdAt }
                val picker = DatePickerDialog(context, { _, year, month, day ->
                createdAt = Calendar.getInstance().apply {
                    set(year, month, day, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                showTimeDialog = false
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                picker.setOnCancelListener { showTimeDialog = false }
                picker.setOnDismissListener { showTimeDialog = false }
                picker.show()
        }
    }
    if (showTagDialog) AlertDialog(
        onDismissRequest = { showTagDialog = false },
        title = { Text("添加标签") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(tagInput, { tagInput = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("新建标签或路径") }, placeholder = { Text("例如：生活/阅读") })
                val query = tagInput.trim().trimStart('#')
                val suggestions = if (query.isBlank()) recentTagCategories else categories
                    .filter { it.path.contains(query, ignoreCase = true) }
                    .sortedWith(compareBy<NoteCategory> { if (it.path.startsWith(query, ignoreCase = true)) 0 else 1 }.thenBy { it.path })
                    .take(8)
                if (suggestions.isNotEmpty()) {
                    Text(if (query.isBlank()) "最近使用" else "匹配的标签", color = NoteMuted, style = MaterialTheme.typography.labelMedium)
                    Column(Modifier.heightIn(max = 280.dp).verticalScroll(rememberScrollState())) {
                        suggestions.forEach { category ->
                            TextButton(
                                onClick = { insertTagAtCursor(category.path, text) { text = it }; showTagDialog = false },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Icons.Filled.Label, null)
                                Spacer(Modifier.width(6.dp))
                                Text(category.path, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                } else if (query.isNotBlank()) {
                    Text("没有匹配的标签，可插入新标签", color = NoteMuted, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = { TextButton(onClick = { val value = tagInput.trim().trimStart('#'); if (value.isNotBlank() && value.split('/').all { it.isNotBlank() }) { insertTagAtCursor(value, text) { text = it }; showTagDialog = false } }) { Text("插入标签") } },
        dismissButton = { TextButton(onClick = { showTagDialog = false }) { Text("取消") } },
    )
    if (showReferencePicker) SearchBrowserDialog(
        notes = allNotes.filterNot { it.id == note.id },
        categories = categories,
        onDismiss = { showReferencePicker = false },
        onSelectNote = { insertNoteReference(it) },
        onEditNote = onOpenLinkedNote,
        onCreateNote = {
            val now = System.currentTimeMillis()
            val created = QuickNote(UUID.randomUUID().toString(), "", now, now, emptyList())
            onCreateLinkedNote(created)
            insertNoteReference(created.copy(content = "新笔记"))
        },
    )
    if (showLinkDialog) {
        Dialog(onDismissRequest = { showLinkDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
            Box(Modifier.fillMaxSize().imePadding(), contentAlignment = Alignment.BottomCenter) {
                Surface(
                    Modifier.fillMaxWidth().fillMaxHeight(0.76f),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                    shadowElevation = 12.dp,
                ) {
                    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        Row(Modifier.fillMaxWidth().height(58.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showLinkDialog = false }) { Icon(Icons.Filled.Close, "关闭") }
                            Text("插入链接", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                            Button(onClick = {
                                val rawUrl = linkUrl.trim()
                                if (rawUrl.isNotBlank()) {
                                    val url = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) rawUrl else "https://$rawUrl"
                                    val label = linkTitle.trim().ifBlank { rawUrl }
                                    val markdown = "[${label.replace("]", "")}]($url)" + linkDetails.trim().takeIf { it.isNotBlank() }?.let { " $it" }.orEmpty()
                                    insertMarkdownAtSelection(markdown)
                                    linkUrl = ""
                                    linkTitle = ""
                                    linkDetails = ""
                                    showLinkDialog = false
                                }
                            }, enabled = linkUrl.isNotBlank(), shape = RoundedCornerShape(8.dp)) { Text("完成") }
                        }
                        HorizontalDivider(color = NoteSoft)
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            OutlinedTextField(
                                linkUrl,
                                { linkUrl = it },
                                Modifier.fillMaxWidth(),
                                singleLine = true,
                                label = { Text("网址") },
                                placeholder = { Text("粘贴要插入的链接地址") },
                                trailingIcon = { if (linkUrl.isNotBlank()) IconButton(onClick = { linkUrl = "" }) { Icon(Icons.Filled.Close, "清除网址") } },
                            )
                            OutlinedTextField(linkTitle, { linkTitle = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("标题") }, placeholder = { Text("网页标题") })
                            OutlinedTextField(linkDetails, { linkDetails = it }, Modifier.fillMaxWidth().heightIn(min = 110.dp), label = { Text("附加信息") }, minLines = 3)
                        }
                    }
                }
            }
        }
    }
    previewUri?.let { uri -> ImagePreviewDialog(context, uri, onDismiss = { previewUri = null }) }
}

@Composable
private fun ConnectionBackFace(
    categories: List<NoteCategory>,
    linkedNotes: List<QuickNote>,
    modifier: Modifier = Modifier,
    onOpenNote: (QuickNote) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = Color(0xFFFAF9F7),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NoteSoft),
        shadowElevation = 3.dp,
    ) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("关联主题", color = NoteInk, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (categories.isEmpty()) {
                Text("暂无关联标签", Modifier.padding(top = 8.dp), color = NoteMuted, style = MaterialTheme.typography.bodySmall)
            } else {
                categories.chunked(3).forEach { row ->
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { category ->
                            Surface(
                                Modifier.weight(1f).aspectRatio(1f),
                                color = tagColor(category),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Box(Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.Center) {
                                    Text(category.name, color = NoteInk, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f).aspectRatio(1f)) }
                    }
                }
            }
            Text("关联笔记", Modifier.padding(top = 18.dp), color = NoteInk, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (linkedNotes.isEmpty()) {
                Text("暂无引用笔记", Modifier.padding(top = 8.dp), color = NoteMuted, style = MaterialTheme.typography.bodySmall)
            } else {
                linkedNotes.forEach { linked ->
                    Card(
                        Modifier.fillMaxWidth().padding(top = 8.dp).clickable { onOpenNote(linked) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    ) {
                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(prettyTime(linked.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                                moodEmoji(linked.mood).takeIf { it.isNotEmpty() }?.let { Text(it, Modifier.padding(start = 6.dp), fontSize = 14.sp) }
                                linked.location?.let { Text(" · $it", Modifier.weight(1f), color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            }
                            CommentedNotePreview(linked.content, linked.comments, {}, showEye = false, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EmbeddedCommentEditor(
    value: TextFieldValue,
    comments: List<NoteComment>,
    activeComment: Int?,
    focusRequest: Int,
    focusSegment: Int,
    initialFocus: FocusRequester,
    onBodyChange: (Int, Int, Int, TextFieldValue) -> Unit,
    onCommentChange: (Int, String) -> Unit,
    onDeleteComment: (Int) -> Unit,
    onEditComment: (Int) -> Unit,
    onToggleComment: (Int) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ordered = comments.withIndex().sortedBy { it.value.position }
    Column(modifier) {
        for (segment in 0..ordered.size) {
            val start = if (segment == 0) 0 else ordered[segment - 1].value.position.coerceIn(0, value.text.length)
            val end = if (segment == ordered.size) value.text.length else ordered[segment].value.position.coerceIn(start, value.text.length)
            key("body", segment) {
                val focus = remember { FocusRequester() }
                val bring = remember { BringIntoViewRequester() }
                val keyboard = LocalSoftwareKeyboardController.current
                val localValue = TextFieldValue(value.text.substring(start, end), TextRange((value.selection.start - start).coerceIn(0, end - start), (value.selection.end - start).coerceIn(0, end - start)), value.composition?.takeIf { it.start >= start && it.end <= end }?.let { TextRange(it.start - start, it.end - start) })
                LaunchedEffect(focusRequest) {
                    if (focusRequest > 0 && focusSegment == segment) {
                        focus.requestFocus()
                        keyboard?.show()
                        bring.bringIntoView()
                    }
                }
                BasicTextField(
                    value = localValue,
                    onValueChange = { onBodyChange(segment, start, end, it) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 28.dp).then(if (segment == 0) Modifier.focusRequester(initialFocus) else Modifier).focusRequester(focus).bringIntoViewRequester(bring),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, color = Color.Black),
                    visualTransformation = remember { MarkdownVisualTransformation() },
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(NotePrimary),
                )
            }
            ordered.getOrNull(segment)?.let { indexed ->
                val index = indexed.index
                val comment = indexed.value
                Row(Modifier.fillMaxWidth().padding(vertical = 1.dp), verticalAlignment = Alignment.Top) {
                    IconButton(onClick = { onToggleComment(index) }, modifier = Modifier.size(28.dp)) {
                        Icon(if (comment.expanded) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, "展开或隐藏评论", tint = NoteAccent, modifier = Modifier.size(18.dp))
                    }
                    if (comment.expanded || activeComment == index) {
                        Surface(color = NotePaper, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                            Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 5.dp)) {
                                if (activeComment == index) {
                                    val focus = remember { FocusRequester() }
                                    var hadFocus by remember(index) { mutableStateOf(false) }
                                    LaunchedEffect(index) { focus.requestFocus() }
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                        BasicTextField(
                                            value = comment.text,
                                            onValueChange = { onCommentChange(index, it) },
                                            modifier = Modifier.weight(1f).heightIn(min = 28.dp).focusRequester(focus).onFocusChanged { state ->
                                                if (state.isFocused) hadFocus = true
                                                else if (hadFocus) { hadFocus = false; onDone() }
                                            },
                                            textStyle = MaterialTheme.typography.bodySmall.copy(color = NoteCommentColor, fontSize = 13.sp),
                                            cursorBrush = androidx.compose.ui.graphics.SolidColor(NotePrimary),
                                            decorationBox = { inner -> Box { if (comment.text.isEmpty()) Text("写下评论…", color = NoteCommentColor, style = MaterialTheme.typography.bodySmall, fontSize = 13.sp); inner() } },
                                        )
                                        Icon(Icons.Filled.Check, "完成评论并继续正文", modifier = Modifier.size(28.dp).clickable(onClick = onDone).padding(4.dp))
                                    }
                                } else {
                                    var showCommentMenu by remember(index) { mutableStateOf(false) }
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                        Text(comment.text, color = NoteCommentColor, style = MaterialTheme.typography.bodySmall, fontSize = 13.sp, modifier = Modifier.weight(1f).combinedClickable(onClick = { onEditComment(index) }, onDoubleClick = { onEditComment(index) }))
                                        Box {
                                            IconButton(onClick = { showCommentMenu = true }, modifier = Modifier.size(26.dp)) { Icon(Icons.Filled.MoreVert, "评论操作", tint = NoteMuted, modifier = Modifier.size(17.dp)) }
                                            NotePopupMenu(expanded = showCommentMenu, onDismissRequest = { showCommentMenu = false }, compact = true) {
                                                NoteActionMenuItem("复制评论", icon = { Icon(Icons.Filled.ContentCopy, null, tint = NoteMuted, modifier = Modifier.size(18.dp)) }, onClick = {
                                                    showCommentMenu = false
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("评论", comment.text))
                                                    Toast.makeText(context, "评论已复制", Toast.LENGTH_SHORT).show()
                                                })
                                                NoteActionMenuItem("删除评论", icon = { Icon(Icons.Filled.DeleteOutline, null, tint = Color(0xFFB3261E), modifier = Modifier.size(18.dp)) }, onClick = { showCommentMenu = false; onDeleteComment(index) }, destructive = true)
                                            }
                                        }
                                    }
                                }
                                Text(prettyTime(comment.createdAt), color = NoteMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun insertTagAtCursor(tag: String, value: TextFieldValue, onChange: (TextFieldValue) -> Unit) {
    val insertion = "#${tag.trim().trimStart('#')} "
    val start = value.selection.min.coerceIn(0, value.text.length)
    val end = value.selection.max.coerceIn(start, value.text.length)
    val suffix = value.text.substring(end)
    val suffixStart = if (suffix.startsWith(" ")) 1 else 0
    val next = value.text.substring(0, start) + insertion + suffix.drop(suffixStart)
    onChange(TextFieldValue(next, TextRange(start + insertion.length)))
}

@Composable
private fun ToolButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, selected: Boolean, onClick: () -> Unit) { IconButton(onClick = onClick, modifier = Modifier.size(34.dp)) { Icon(icon, label, tint = if (selected) NoteAccent else NotePrimary, modifier = Modifier.size(19.dp)) } }
@Composable
private fun MarkdownToolButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Icon(icon, label, tint = NotePrimary, modifier = Modifier.size(21.dp))
    }
}
private fun moodIcon(mood: String) = when (mood) { "HAPPY" -> Icons.Filled.SentimentVerySatisfied; "SAD" -> Icons.Filled.SentimentDissatisfied; else -> Icons.Filled.SentimentNeutral }
private fun moodEmoji(mood: String): String = when (mood) { "HAPPY" -> "😊"; "SAD" -> "😞"; "NORMAL_SELECTED" -> "😐"; else -> "" }

@Composable
private fun FavoriteNotesPage(notes: List<QuickNote>, padding: PaddingValues, onSelect: (QuickNote) -> Unit) {
    if (notes.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("暂无收藏", color = NoteMuted, style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(notes, key = { it.id }) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(note) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            note.content.ifBlank { "（空白笔记）" },
                            color = NoteInk,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 6,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            prettyTime(note.createdAt),
                            color = NoteMuted,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun getLastLocationLabel(context: Context): String? {
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
    val location = manager.getProviders(true).asSequence().mapNotNull { provider -> runCatching { manager.getLastKnownLocation(provider) }.getOrNull() }.firstOrNull() ?: return null
    val address = runCatching { Geocoder(context, Locale.CHINA).getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull() }.getOrNull()
    val parts = listOf(address?.adminArea, address?.locality, address?.subLocality, address?.thoroughfare, address?.subThoroughfare).filter { !it.isNullOrBlank() }.map { it!!.trim() }.distinct()
    return parts.joinToString("").takeIf { it.isNotBlank() } ?: "位置已授权"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WalkPage(state: NotesState, padding: PaddingValues, onEdit: (QuickNote) -> Unit, onDelete: (QuickNote) -> Unit, onToggleFavorite: (QuickNote) -> Unit, onUpdate: (NotesState) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val notes = remember(state.notes) { state.notes.sortedByDescending { it.createdAt } }
    var filterMode by rememberSaveable { mutableStateOf(WalkFilterMode.RANDOM) }
    var selectedMonth by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedTag by rememberSaveable { mutableStateOf<String?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showTagPicker by remember { mutableStateOf(false) }
    val monthFormat = remember { SimpleDateFormat("yyyy-MM", Locale.CHINA) }
    val months = remember(notes) { notes.map { monthFormat.format(Date(it.createdAt)) }.distinct().sortedDescending() }
    val tags = remember(notes, state.categories) {
        notes.flatMap { note -> noteTagPaths(note, state.categories) }.distinct().sorted()
    }
    val filteredNotes = remember(notes, filterMode, selectedMonth, selectedTag, state.categories) {
        when (filterMode) {
            WalkFilterMode.RANDOM -> notes
            WalkFilterMode.MONTH -> notes.filter { selectedMonth != null && monthFormat.format(Date(it.createdAt)) == selectedMonth }
            WalkFilterMode.TAG -> notes.filter { note ->
                selectedTag != null && noteTagPaths(note, state.categories).any { it == selectedTag || it.startsWith("$selectedTag/") }
            }
        }
    }
    var index by remember(filteredNotes.map { it.id }) { mutableStateOf(filteredNotes.indices.randomOrNull() ?: 0) }
    val current = filteredNotes.getOrNull(index.coerceIn(0, (filteredNotes.size - 1).coerceAtLeast(0)))
    var showDeleteDialog by remember { mutableStateOf(false) }
    var walkFlipped by remember(current?.id) { mutableStateOf(false) }
    Box(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 10.dp).pointerInput(current?.id) {
        if (current != null) {
            var dragDistance = 0f
            detectHorizontalDragGestures(
                onHorizontalDrag = { _, amount -> dragDistance += amount },
                onDragEnd = {
                    if (abs(dragDistance) > 80f && filteredNotes.isNotEmpty()) index = (index + if (dragDistance < 0f) 1 else -1).coerceIn(0, filteredNotes.lastIndex)
                    dragDistance = 0f
                },
                onDragCancel = { dragDistance = 0f },
            )
        }
    }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 78.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("漫步", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("从已有笔记中随机开始，左右滑动按创建时间浏览", color = NoteMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { showFilterMenu = true },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NotePrimary.copy(alpha = 0.25f)),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                ) {
                    Icon(Icons.Filled.FilterList, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(when (filterMode) {
                        WalkFilterMode.RANDOM -> "随机 · 全部笔记"
                        WalkFilterMode.MONTH -> "${selectedMonth ?: "按月份"}"
                        WalkFilterMode.TAG -> "#${selectedTag ?: "按标签"}"
                    }, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelLarge)
                    Icon(Icons.Filled.KeyboardArrowDown, "选择漫步范围", modifier = Modifier.size(18.dp))
                }
                NotePopupMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                    DropdownMenuItem(text = { Text("随机") }, leadingIcon = { Icon(Icons.Filled.Shuffle, null, tint = NotePrimary) }, trailingIcon = { if (filterMode == WalkFilterMode.RANDOM) Icon(Icons.Filled.Check, null, tint = NotePrimary) }, onClick = {
                        filterMode = WalkFilterMode.RANDOM
                        selectedMonth = null
                        selectedTag = null
                        showFilterMenu = false
                        index = filteredNotes.indices.randomOrNull() ?: 0
                    })
                    DropdownMenuItem(text = { Text("按月份") }, leadingIcon = { Icon(Icons.Filled.CalendarMonth, null, tint = NotePrimary) }, trailingIcon = { if (filterMode == WalkFilterMode.MONTH) Icon(Icons.Filled.Check, null, tint = NotePrimary) }, onClick = { showFilterMenu = false; showMonthPicker = true })
                    DropdownMenuItem(text = { Text("按标签") }, leadingIcon = { Icon(Icons.Filled.Label, null, tint = NotePrimary) }, trailingIcon = { if (filterMode == WalkFilterMode.TAG) Icon(Icons.Filled.Check, null, tint = NotePrimary) }, onClick = { showFilterMenu = false; showTagPicker = true })
                }
            }
            Button(
                onClick = { index = filteredNotes.indices.randomOrNull() ?: 0 },
                enabled = filteredNotes.isNotEmpty(),
                modifier = Modifier.height(46.dp),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                Icon(Icons.Filled.Shuffle, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("重新随机", style = MaterialTheme.typography.labelLarge)
            }
        }
        if (current == null) {
            EmptyHint("还没有可漫步的笔记")
        } else {
            Text("当前范围第 ${index + 1} / ${filteredNotes.size} 篇", color = NoteMuted, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 10.dp))
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().heightIn(min = 300.dp, max = 620.dp).padding(vertical = 12.dp).combinedClickable(onClick = {}, onDoubleClick = { if (!walkFlipped) onEdit(current) }, onLongClick = { walkFlipped = !walkFlipped }),
            ) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(prettyTime(current.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                        moodEmoji(current.mood).takeIf { it.isNotEmpty() }?.let { Text(it, modifier = Modifier.padding(start = 6.dp), fontSize = 15.sp) }
                        current.location?.let { Text(" · $it", color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        Spacer(Modifier.weight(1f))
                        NoteOverflowMenu(
                            note = current,
                            onToggleFavorite = { onToggleFavorite(current) },
                            onTogglePinned = {
                                if (!current.pinned && state.notes.count { it.pinned } >= MAX_PINNED_NOTES) Toast.makeText(context, "最多置顶6篇哦", Toast.LENGTH_SHORT).show()
                                else onUpdate(state.copy(notes = state.notes.map { if (it.id == current.id) it.copy(pinned = !it.pinned) else it }))
                            },
                            onDelete = { onDelete(current) },
                            showDeleteDialog = showDeleteDialog,
                            onDeleteDialogChange = { showDeleteDialog = it },
                            onFlip = { walkFlipped = !walkFlipped },
                        )
                    }
                    NotePreviewFlip(current, state.categories, state.notes, walkFlipped, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), onOpenNote = onEdit, onFlip = { walkFlipped = !walkFlipped }) {
                        CommentedNotePreview(current.content, current.comments, {}, showEye = false)
                    }
                }
            }
            if (index == 0 || index == filteredNotes.lastIndex) Text(if (filteredNotes.size == 1) "当前只有这一篇" else if (index == 0) "已经到达最早的笔记" else "已经到达最新的笔记", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 6.dp))
        }
    }
    Surface(color = Color.White, shadowElevation = 8.dp, shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB8B0A8)), modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (index > 0) index-- }, enabled = index > 0) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "上一篇", tint = NotePrimary) }
            IconButton(onClick = { current?.let(onToggleFavorite) }, enabled = current != null) {
                Icon(if (current?.favorite == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "收藏", tint = if (current?.favorite == true) NoteAccent else NotePrimary)
            }
            IconButton(onClick = { if (index < filteredNotes.lastIndex) index++ }, enabled = index < filteredNotes.lastIndex) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "下一篇", tint = NotePrimary) }
        }
    }
    }
    WalkFilterPickers(
        showMonthPicker = showMonthPicker,
        showTagPicker = showTagPicker,
        months = months,
        tags = tags,
        onMonthSelected = { month -> selectedMonth = month; selectedTag = null; filterMode = WalkFilterMode.MONTH; showMonthPicker = false },
        onTagSelected = { tag -> selectedTag = tag; selectedMonth = null; filterMode = WalkFilterMode.TAG; showTagPicker = false },
        onDismissMonth = { showMonthPicker = false },
        onDismissTag = { showTagPicker = false },
    )
}

@Composable
private fun WalkFilterPickers(
    showMonthPicker: Boolean,
    showTagPicker: Boolean,
    months: List<String>,
    tags: List<String>,
    onMonthSelected: (String) -> Unit,
    onTagSelected: (String) -> Unit,
    onDismissMonth: () -> Unit,
    onDismissTag: () -> Unit,
) {
    if (showMonthPicker) {
        AlertDialog(
            onDismissRequest = onDismissMonth,
            title = { Text("按月份筛选") },
            text = {
                Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                    if (months.isEmpty()) EmptyHint("还没有可选择的月份")
                    months.forEach { month ->
                        TextButton(onClick = { onMonthSelected(month) }, modifier = Modifier.fillMaxWidth()) { Text(month) }
                    }
                }
            },
            confirmButton = { TextButton(onClick = onDismissMonth) { Text("取消") } },
        )
    }
    if (showTagPicker) {
        Dialog(onDismissRequest = onDismissTag, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(Modifier.fillMaxSize(), color = NotePaper) {
                Column(Modifier.fillMaxSize().padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismissTag) { Icon(Icons.Filled.Close, "关闭标签选择") }
                        Text("选择标签", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Text("选择后只在该标签的笔记中漫步", color = NoteMuted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 12.dp))
                    if (tags.isEmpty()) {
                        EmptyHint("还没有可选择的标签")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(tags) { tag ->
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().clickable { onTagSelected(tag) },
                                ) {
                                    Text("#$tag", color = NotePrimary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiaryPage(state: NotesState, padding: PaddingValues, onEdit: (Diary) -> Unit, onCreate: () -> Unit, onRandom: (List<Diary>) -> Unit) {
    var monthFilter by rememberSaveable { mutableStateOf("全部月份") }
    var categoryFilter by rememberSaveable { mutableStateOf("全部分类") }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showRandomHint by remember { mutableStateOf(false) }
    val months = state.diaries.map { it.date.take(7) }.filter { it.length == 7 }.distinct().sortedDescending()
    val categories = state.diaries.flatMap { diaryTags(it) }.distinct().sorted()
    val filtered = state.diaries.sortedByDescending { it.date }.filter { diary ->
        (monthFilter == "全部月份" || diary.date.startsWith(monthFilter)) && (categoryFilter == "全部分类" || categoryFilter in diaryTags(diary))
    }
    val randomPool = filtered.ifEmpty { state.diaries.sortedByDescending { it.date } }
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("日记列表", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("按日期浏览，进入后可以继续编辑", color = NoteMuted) }
                IconButton(onClick = { if (randomPool.isEmpty()) showRandomHint = true else onRandom(randomPool) }) { Icon(Icons.Filled.Shuffle, "随机漫步") }
                Button(onClick = onCreate) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(4.dp)); Text("新建") }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showMonthPicker = true }) { Icon(Icons.Filled.CalendarMonth, null); Spacer(Modifier.width(4.dp)); Text(monthFilter) }
                OutlinedButton(onClick = { showCategoryPicker = true }) { Icon(Icons.Filled.Label, null); Spacer(Modifier.width(4.dp)); Text(categoryFilter) }
            }
        }
        item { OutlinedButton(onClick = { if (randomPool.isEmpty()) showRandomHint = true else onRandom(randomPool) }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Filled.Shuffle, null); Spacer(Modifier.width(6.dp)); Text("开始随机漫步" + if (randomPool.isNotEmpty()) "（${randomPool.size} 篇）" else "") } }
        if (filtered.isEmpty()) item { EmptyHint("还没有符合筛选条件的日记") }
        items(filtered, key = { it.id }) { diary ->
            Card(Modifier.fillMaxWidth().clickable { onEdit(diary) }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text(diary.title.ifBlank { "无标题日记" }, fontWeight = FontWeight.Bold)
                    Text(diary.date, color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                    Text(diary.markdown.lineSequence().firstOrNull().orEmpty(), maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp))
                    val tags = diaryTags(diary)
                    if (tags.isNotEmpty()) Text(tags.joinToString("  ") { "#$it" }, color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
        item { Text("随记评论预览", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp)) }
        items(state.notes.filter { it.comments.isNotEmpty() }, key = { "comments-${it.id}" }) { note -> Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(14.dp)) { CommentedNotePreview(note.content, note.comments, {}, maxLines = 2, showEye = false, modifier = Modifier.padding(top = 6.dp)) } } }
    }
    if (showMonthPicker) AlertDialog(onDismissRequest = { showMonthPicker = false }, title = { Text("按月份筛选") }, text = { Column { TextButton(onClick = { monthFilter = "全部月份"; showMonthPicker = false }, modifier = Modifier.fillMaxWidth()) { Text("全部月份") }; months.forEach { month -> TextButton(onClick = { monthFilter = month; showMonthPicker = false }, modifier = Modifier.fillMaxWidth()) { Text(month) } } } }, confirmButton = { TextButton(onClick = { showMonthPicker = false }) { Text("取消") } })
    if (showCategoryPicker) AlertDialog(onDismissRequest = { showCategoryPicker = false }, title = { Text("按分类筛选") }, text = { Column { TextButton(onClick = { categoryFilter = "全部分类"; showCategoryPicker = false }, modifier = Modifier.fillMaxWidth()) { Text("全部分类") }; categories.forEach { category -> TextButton(onClick = { categoryFilter = category; showCategoryPicker = false }, modifier = Modifier.fillMaxWidth()) { Text("#$category") } } } }, confirmButton = { TextButton(onClick = { showCategoryPicker = false }) { Text("取消") } })
    if (showRandomHint) AlertDialog(onDismissRequest = { showRandomHint = false }, title = { Text("还没有可漫步的日记") }, text = { Text("请先新建一篇日记。随机漫步不要求标签，日期为空时也可以浏览。") }, confirmButton = { TextButton(onClick = { showRandomHint = false }) { Text("知道了") } })
}

private fun diaryTags(diary: Diary): List<String> = NoteTagRegex.findAll(diary.markdown).map { it.groupValues[1] }.distinct().toList()

@Composable
private fun DiaryWalkDialog(diaries: List<Diary>, onDismiss: () -> Unit, onEdit: (Diary) -> Unit) {
    var index by remember(diaries) { mutableStateOf(diaries.indices.randomOrNull() ?: 0) }
    val current = diaries.getOrNull(index)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize(), color = NotePaper) {
            Column(Modifier.fillMaxSize().padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, "返回列表") }
                    Text("随机漫步", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { index = diaries.indices.randomOrNull() ?: 0 }) { Text("重新随机") }
                }
                if (current != null) {
                    Text("当前范围第 ${index + 1} / ${diaries.size} 篇", color = NoteMuted, style = MaterialTheme.typography.labelMedium)
                    Text(current.title.ifBlank { "无标题日记" }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp))
                    Text(current.date, color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                    MarkdownPreview(current.markdown, Modifier.fillMaxWidth().weight(1f).padding(top = 20.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(onClick = { if (index > 0) index-- }, enabled = index > 0) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null); Spacer(Modifier.width(4.dp)); Text("上一篇") }
                        TextButton(onClick = { onEdit(current) }) { Icon(Icons.Filled.Edit, null); Spacer(Modifier.width(4.dp)); Text("继续编辑") }
                        OutlinedButton(onClick = { if (index < diaries.lastIndex) index++ }, enabled = index < diaries.lastIndex) { Text("下一篇"); Spacer(Modifier.width(4.dp)); Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
                    }
                    if (index == 0 || index == diaries.lastIndex) Text(if (index == 0 && index == diaries.lastIndex) "当前范围只有这一篇" else if (index == 0) "已经到达最早的日记" else "已经到达最新的日记", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp))
                } else EmptyHint("当前范围没有日记")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MyPage(state: NotesState, padding: PaddingValues, onLetters: () -> Unit, onDayClick: (String) -> Unit, onImport: (ImportBundle) -> Unit, onRestore: (NotesState) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showTransfer by remember { mutableStateOf(false) }
    var showExport by remember { mutableStateOf(false) }
    var pendingFormat by remember { mutableStateOf<String?>(null) }
    var result by remember { mutableStateOf<String?>(null) }
    var showWeRead by remember { mutableStateOf(false) }
    var weReadKey by remember { mutableStateOf("") }
    var importingWeRead by remember { mutableStateOf(false) }
    var weReadProgress by remember { mutableStateOf(0f) }
    var weReadProgressText by remember { mutableStateOf<String?>(null) }
    var weReadBooks by remember { mutableStateOf<List<WeReadBook>>(emptyList()) }
    var selectedWeReadIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var weReadTag by remember { mutableStateOf("微信读书") }
    val scope = rememberCoroutineScope()
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        val format = pendingFormat
        if (uri != null && format != null) result = if (exportNotesToZip(context, state, uri, format)) "导出完成" else "导出失败，请重试"
        pendingFormat = null
    }
    val backupExportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) result = runCatching { context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(NotesStore.serialize(state)) }; "备份已保存" }.getOrElse { "备份失败，请重试" }
    }
    val backupImportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val restored = NotesStore.restore(context, uri)
            if (restored != null) { onRestore(restored); result = "数据已恢复" } else result = "备份文件无效"
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val imported = parseFlomoZip(context, uri)
            onImport(imported)
            result = if (imported.notes.isEmpty()) "没有解析到可导入的 Flomo 笔记" else "已导入 ${imported.notes.size} 条笔记"
        }
    }
    val dayFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }
    val dayCounts = remember(state.notes) { state.notes.groupingBy { dayFormat.format(Date(it.createdAt)) }.eachCount() }
    var monthOffset by rememberSaveable { mutableStateOf(0) }
    val monthCalendar = remember(monthOffset) { Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1); add(Calendar.MONTH, monthOffset) } }
    val monthLabel = remember(monthOffset) { SimpleDateFormat("yyyy年M月", Locale.CHINA).format(monthCalendar.time) }
    val firstDayOffset = monthCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
    val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthCells = remember(monthOffset) {
        (0 until 42).map { index ->
            if (index < firstDayOffset || index >= firstDayOffset + daysInMonth) null
            else Calendar.getInstance().apply { set(monthCalendar.get(Calendar.YEAR), monthCalendar.get(Calendar.MONTH), index - firstDayOffset + 1, 0, 0, 0); set(Calendar.MILLISECOND, 0) }.time
        }
    }
    Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("我的", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("本地保存 · ${state.notes.size} 条记录", color = NoteMuted, modifier = Modifier.padding(top = 3.dp))
        Text("记录热力", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 22.dp))
        Text("左右滑动切换月份，点击日期查看当天全部笔记", color = NoteMuted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 3.dp, bottom = 12.dp))
        Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().combinedClickable(onClick = {}, onDoubleClick = { monthOffset = 0 })) {
            Column(Modifier.fillMaxWidth().pointerInput(monthOffset) {
                var dragDistance = 0f
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, amount -> dragDistance += amount },
                    onDragEnd = {
                        if (dragDistance < -80f) monthOffset += 1
                        else if (dragDistance > 80f) monthOffset -= 1
                        dragDistance = 0f
                    },
                    onDragCancel = { dragDistance = 0f },
                )
            }.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { monthOffset -= 1 }, modifier = Modifier.size(36.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "上个月", tint = NotePrimary) }
                    Text(monthLabel, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { monthOffset += 1 }, modifier = Modifier.size(36.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "下个月", tint = NotePrimary) }
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    listOf("日", "一", "二", "三", "四", "五", "六").forEach { label -> Text(label, Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = NoteMuted, style = MaterialTheme.typography.labelSmall) }
                }
                LazyVerticalGrid(GridCells.Fixed(7), Modifier.fillMaxWidth().height(228.dp), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalArrangement = Arrangement.spacedBy(3.dp), userScrollEnabled = false) {
                    items(monthCells) { day ->
                        if (day == null) {
                            Spacer(Modifier.size(34.dp))
                        } else {
                            val dayKey = dayFormat.format(day)
                            val count = dayCounts[dayKey] ?: 0
                            val color = when { count >= 5 -> NotePrimary; count >= 3 -> NoteAccent; count == 2 -> NoteSoft; count == 1 -> NotePaper; else -> Color(0xFFF7F4F0) }
                            val isToday = dayKey == dayFormat.format(Date())
                            Box(Modifier.fillMaxWidth().height(34.dp).clickable { onDayClick(dayKey) }, contentAlignment = Alignment.Center) {
                                Surface(color = color, shape = CircleShape, border = if (isToday) androidx.compose.foundation.BorderStroke(2.dp, NoteAccent) else null, modifier = Modifier.size(30.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text(dayKey.substring(8), color = if (count >= 3) Color.White else NoteMuted, style = MaterialTheme.typography.labelSmall) }
                                }
                            }
                        }
                    }
                }
            }
        }
        Button(onClick = onLetters, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) { Icon(Icons.Filled.MailOutline, null); Spacer(Modifier.width(7.dp)); Text("我的信件") }
        OutlinedButton(onClick = { showTransfer = true }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Icon(Icons.Filled.Save, null); Spacer(Modifier.width(7.dp)); Text("导入 / 导出") }
        result?.let { Text(it, color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 12.dp)) }
    }
    if (showTransfer) AlertDialog(onDismissRequest = { showTransfer = false }, title = { Text("导入 / 导出") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            TextButton(onClick = { showTransfer = false; importLauncher.launch(arrayOf("application/zip", "application/x-zip-compressed")) }, modifier = Modifier.fillMaxWidth()) { Text("导入 Flomo ZIP 压缩包") }
            TextButton(onClick = { showTransfer = false; showWeRead = true; weReadBooks = emptyList(); selectedWeReadIds = emptySet(); weReadTag = "微信读书"; weReadProgress = 0f; weReadProgressText = null }, modifier = Modifier.fillMaxWidth()) { Text("导入微信读书笔记") }
            TextButton(onClick = { showTransfer = false; backupExportLauncher.launch("suixinji-backup.json") }, modifier = Modifier.fillMaxWidth()) { Text("备份全部数据") }
            TextButton(onClick = { showTransfer = false; backupImportLauncher.launch(arrayOf("application/json", "text/plain")) }, modifier = Modifier.fillMaxWidth()) { Text("从备份恢复") }
            TextButton(onClick = { showTransfer = false; showExport = true }, modifier = Modifier.fillMaxWidth()) { Text("导出记录 ZIP") }
        }
    }, confirmButton = { TextButton(onClick = { showTransfer = false }) { Text("取消") } })
    if (showWeRead) AlertDialog(
        onDismissRequest = { if (!importingWeRead) showWeRead = false },
        title = { Text(if (weReadBooks.isEmpty()) "导入微信读书" else "选择要导入的书") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (weReadBooks.isEmpty()) {
                Text("输入 API Key 后先扫描书籍，再选择要导入的书。", color = NoteMuted, style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(weReadKey, { weReadKey = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("API Key") })
            } else {
                Text("已选择 ${selectedWeReadIds.size} / ${weReadBooks.size} 本", color = NoteMuted, style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(weReadTag, { weReadTag = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("导入到标签") }, supportingText = { Text("可选择已有标签，也可以直接输入新标签") })
                if (state.categories.any { it.parentId == null }) {
                    Text("选择已有标签", color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.categories.filter { it.parentId == null }.forEach { category ->
                            FilterChip(selected = weReadTag == category.path, onClick = { weReadTag = category.path }, label = { Text(category.path) })
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { selectedWeReadIds = weReadBooks.map { it.id }.toSet() }) { Text("全选") }
                    TextButton(onClick = { selectedWeReadIds = emptySet() }) { Text("清空") }
                }
                Column(Modifier.fillMaxWidth().heightIn(max = 430.dp).verticalScroll(rememberScrollState())) {
                    weReadBooks.forEach { book ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = book.id in selectedWeReadIds, onCheckedChange = { checked -> selectedWeReadIds = if (checked) selectedWeReadIds + book.id else selectedWeReadIds - book.id })
                            Column(Modifier.weight(1f).clickable { selectedWeReadIds = if (book.id in selectedWeReadIds) selectedWeReadIds - book.id else selectedWeReadIds + book.id }.padding(vertical = 5.dp)) { Text(book.title, maxLines = 2, overflow = TextOverflow.Ellipsis); Text("${book.noteCount} 条笔记", color = NoteMuted, style = MaterialTheme.typography.labelSmall) }
                        }
                    }
                }
            }
            if (importingWeRead) {
                weReadProgressText?.let { Text(it, color = NoteMuted, style = MaterialTheme.typography.labelSmall) }
                if (weReadProgress > 0f) LinearProgressIndicator(progress = weReadProgress, modifier = Modifier.fillMaxWidth()) else LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            result?.let { Text(it, color = NotePrimary, style = MaterialTheme.typography.labelSmall) }
        } },
        confirmButton = { TextButton(enabled = !importingWeRead && ((weReadBooks.isEmpty() && weReadKey.isNotBlank()) || (weReadBooks.isNotEmpty() && selectedWeReadIds.isNotEmpty() && weReadTag.isNotBlank())), onClick = {
            importingWeRead = true
            weReadProgress = 0f
            weReadProgressText = if (weReadBooks.isEmpty()) "正在扫描书籍…" else "正在准备导入…"
            scope.launch(Dispatchers.IO) {
                if (weReadBooks.isEmpty()) {
                    val books = fetchWeReadBooks(weReadKey.trim())
                    withContext(Dispatchers.Main) { importingWeRead = false; weReadBooks = books; selectedWeReadIds = emptySet(); weReadProgress = 0f; weReadProgressText = null; result = if (books.isEmpty()) "没有找到可导入的书" else "请选择要导入的书" }
                } else {
                    val imported = importWeReadNotes(weReadKey.trim(), selectedWeReadIds.toSet(), weReadTag.trim()) { current, total, title ->
                        withContext(Dispatchers.Main) {
                            weReadProgress = if (total == 0) 0f else current.toFloat() / total.toFloat()
                            weReadProgressText = "正在导入 $title（$current / $total）"
                        }
                    }
                    withContext(Dispatchers.Main) { importingWeRead = false; if (imported.notes.isNotEmpty()) { onImport(imported); result = "已导入 ${imported.notes.size} 条笔记"; showWeRead = false } else result = "所选书籍没有可导入笔记" }
                }
            }
        }) { Text(if (weReadBooks.isEmpty()) "扫描书籍" else "开始导入") } },
        dismissButton = { TextButton(enabled = !importingWeRead, onClick = { showWeRead = false }) { Text("取消") } },
    )
    if (showExport) AlertDialog(onDismissRequest = { showExport = false }, title = { Text("选择导出格式") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            TextButton(onClick = { pendingFormat = "md"; showExport = false; exportLauncher.launch("suixinji-${today()}.zip") }, modifier = Modifier.fillMaxWidth()) { Text("Markdown（.md）") }
            TextButton(onClick = { pendingFormat = "txt"; showExport = false; exportLauncher.launch("suixinji-${today()}.zip") }, modifier = Modifier.fillMaxWidth()) { Text("纯文本（.txt）") }
        }
    }, confirmButton = { TextButton(onClick = { showExport = false }) { Text("取消") } })
}

private fun parseFlomoZip(context: Context, uri: Uri): ImportBundle = runCatching {
    val html = StringBuilder()
    context.contentResolver.openInputStream(uri)?.use { input -> ZipInputStream(BufferedInputStream(input)).use { zip ->
        while (true) {
            val entry = zip.nextEntry ?: break
            if (!entry.isDirectory && entry.name.lowercase(Locale.ROOT).endsWith(".html")) {
                val bytes = ByteArrayOutputStream(); zip.copyTo(bytes); html.append(bytes.toString(Charsets.UTF_8.name()))
            }
            zip.closeEntry()
        }
    } }
    val memoRegex = Regex("(?s)<div\\s+class=\\\"memo\\\".*?<div\\s+class=\\\"files\\\">(.*?)</div>")
    val timeRegex = Regex("(?s)<div\\s+class=\\\"time\\\">(.*?)</div>")
    val contentRegex = Regex("(?s)<div\\s+class=\\\"content\\\">(.*?)</div>\\s*<div\\s+class=\\\"files\\\">")
    val dateParser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).apply { isLenient = false }
    val categories = mutableListOf<NoteCategory>()
    fun categoryForPath(path: String): NoteCategory {
        categories.firstOrNull { it.path == path }?.let { return it }
        val parentPath = path.substringBeforeLast('/', "")
        val parent = parentPath.takeIf { it.isNotBlank() }?.let { categoryForPath(it) }
        return NoteCategory(UUID.randomUUID().toString(), path.substringAfterLast('/'), parent?.id, path).also { categories += it }
    }
    val notes = memoRegex.findAll(html).mapNotNull { memo ->
        val block = memo.value
        val timeText = timeRegex.find(block)?.groupValues?.getOrNull(1)?.trim() ?: return@mapNotNull null
        val contentHtml = contentRegex.find(block)?.groupValues?.getOrNull(1) ?: return@mapNotNull null
        val content = Html.fromHtml(contentHtml, Html.FROM_HTML_MODE_LEGACY).toString().replace("\u00a0", " ").trim()
        if (content.isBlank()) return@mapNotNull null
        val created = runCatching { dateParser.parse(timeText)?.time }.getOrNull() ?: return@mapNotNull null
        val ids = mutableListOf<String>()
        val headingStack = mutableListOf<String>()
        content.lineSequence().forEach { line ->
            val heading = Regex("^\\s*(#{1,6})\\s+(.+?)\\s*$").find(line) ?: return@forEach
            val level = heading.groupValues[1].length
            while (headingStack.size >= level) headingStack.removeAt(headingStack.lastIndex)
            headingStack += heading.groupValues[2].trim()
            ids += categoryForPath(headingStack.joinToString("/")).id
        }
        NoteTagRegex.findAll(content).forEach { match -> ids += categoryForPath(match.groupValues[1]).id }
        QuickNote(UUID.randomUUID().toString(), content, created, created, ids.distinct())
    }.toList()
    ImportBundle(notes, categories)
}.getOrDefault(ImportBundle())

private fun fetchWeReadBooks(apiKey: String): List<WeReadBook> = runCatching {
    fun request(params: JSONObject): JSONObject {
        val connection = (URL("https://i.weread.qq.com/api/agent/gateway").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"; connectTimeout = 15000; readTimeout = 30000; doOutput = true
            setRequestProperty("Authorization", "Bearer $apiKey"); setRequestProperty("Content-Type", "application/json")
        }
        val body = JSONObject(params.toString()).put("api_name", "/user/notebooks")
        connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        return JSONObject(response)
    }
    val result = mutableListOf<WeReadBook>()
    var lastSort = 0L
    do {
        val params = JSONObject().put("count", 100)
        if (lastSort > 0) params.put("lastSort", lastSort)
        val page = request(params)
        val books = page.optJSONArray("books") ?: JSONArray()
        for (index in 0 until books.length()) {
            val entry = books.optJSONObject(index) ?: continue
            val book = entry.optJSONObject("book") ?: entry
            val id = entry.optString("bookId", book.optString("bookId"))
            val title = book.optString("title", "未命名书籍")
            if (id.isNotBlank()) result += WeReadBook(id, title, entry.optInt("noteCount", 0))
        }
        lastSort = books.optJSONObject(books.length() - 1)?.optLong("sort", 0L) ?: 0L
        if (books.length() == 0 || page.optInt("hasMore", 0) == 0) lastSort = 0L
    } while (lastSort > 0)
    result.distinctBy { it.id }
}.getOrDefault(emptyList())

private suspend fun importWeReadNotes(apiKey: String, selectedBookIds: Set<String>, rootTag: String, onProgress: suspend (Int, Int, String) -> Unit = { _, _, _ -> }): ImportBundle = try {
    fun request(path: String, params: JSONObject = JSONObject()): JSONObject {
        val connection = (URL("https://i.weread.qq.com/api/agent/gateway").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"; connectTimeout = 15000; readTimeout = 30000; doOutput = true
            setRequestProperty("Authorization", "Bearer $apiKey"); setRequestProperty("Content-Type", "application/json")
        }
        val body = JSONObject(params.toString()).put("api_name", path)
        connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
        val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
        val response = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
        if (connection.responseCode !in 200..299) error("HTTP ${connection.responseCode}")
        return JSONObject(response)
    }
    val categories = mutableListOf<NoteCategory>()
    val notes = mutableListOf<QuickNote>()
    onProgress(0, selectedBookIds.size, "正在读取书籍列表")
    val notebookBooks = mutableListOf<JSONObject>()
    var lastSort = 0L
    do {
        val notebookParams = JSONObject().put("count", 100)
        if (lastSort > 0) notebookParams.put("lastSort", lastSort)
        val response = request("/user/notebooks", notebookParams)
        val page = response.optJSONArray("books") ?: JSONArray()
        for (i in 0 until page.length()) page.optJSONObject(i)?.let { notebookBooks += it }
        lastSort = page.optJSONObject(page.length() - 1)?.optLong("sort", 0L) ?: 0L
        if (page.length() == 0 || response.optInt("hasMore", 0) == 0) lastSort = 0L
    } while (lastSort > 0)
    val notebooks = JSONArray().apply { notebookBooks.forEach { put(it) } }
    val rootParts = rootTag.split('/').filter { it.isNotBlank() }
    var rootParent: NoteCategory? = null
    var rootPath = ""
    rootParts.forEach { part ->
        rootPath = if (rootPath.isBlank()) part else "$rootPath/$part"
        val rootCategory = categories.firstOrNull { it.path == rootPath } ?: NoteCategory(UUID.randomUUID().toString(), part, rootParent?.id, rootPath).also { categories += it }
        rootParent = rootCategory
    }
    val importRoot = rootParent ?: NoteCategory(UUID.randomUUID().toString(), "微信读书", null, "微信读书").also { categories += it }
    val selectedTotal = selectedBookIds.size
    var importedBookCount = 0
    for (i in 0 until notebooks.length()) {
        val book = notebooks.optJSONObject(i) ?: continue
        val bookInfo = book.optJSONObject("book") ?: book
        val bookName = bookInfo.optString("title", book.optString("bookName", "WeRead"))
        val bookId = book.optString("bookId", bookInfo.optString("bookId"))
        if (bookId !in selectedBookIds) continue
        val bookPath = "${importRoot.path}/$bookName"
        val bookCategory = categories.firstOrNull { it.path == bookPath } ?: NoteCategory(UUID.randomUUID().toString(), bookName, importRoot.id, bookPath).also { categories += it }
        importedBookCount += 1
        onProgress(importedBookCount - 1, selectedTotal, bookName)
        val params = JSONObject().put("bookId", bookId)
        val bookmarkResponse = request("/book/bookmarklist", params)
        val chapterNames = mutableMapOf<Int, String>()
        bookmarkResponse.optJSONArray("chapters")?.let { chapters ->
            for (k in 0 until chapters.length()) chapters.optJSONObject(k)?.let { chapterNames[it.optInt("chapterIdx")] = it.optString("title") }
        }
        val marks = bookmarkResponse.optJSONArray("updated") ?: JSONArray()
        for (j in 0 until marks.length()) {
            val mark = marks.optJSONObject(j) ?: continue
            val content = mark.optString("markText", mark.optString("text", "")).trim()
            if (content.isBlank()) continue
            val chapter = mark.optString("chapterName", chapterNames[mark.optInt("chapterIdx")] ?: "")
            val path = if (chapter.isBlank()) bookPath else "$bookPath/$chapter"
            val category = categories.firstOrNull { it.path == path } ?: NoteCategory(UUID.randomUUID().toString(), chapter, bookCategory.id, path).also { categories += it }
            val created = mark.optLong("createTime", System.currentTimeMillis()).let { if (it < 100000000000L) it * 1000 else it }
            notes += QuickNote(UUID.randomUUID().toString(), content, created, created, listOf(bookCategory.id, category.id), status = "organized")
        }
        val reviews = request("/review/list/mine", JSONObject().put("bookid", bookId).put("count", 100)).optJSONArray("reviews") ?: JSONArray()
        for (j in 0 until reviews.length()) {
            val review = reviews.optJSONObject(j) ?: continue
            val content = review.optString("review", review.optString("content", "")).trim()
            if (content.isBlank()) continue
            val created = review.optLong("createTime", System.currentTimeMillis()).let { if (it < 100000000000L) it * 1000 else it }
            notes += QuickNote(UUID.randomUUID().toString(), content, created, created, listOf(bookCategory.id), status = "organized")
        }
        onProgress(importedBookCount, selectedTotal, bookName)
    }
    ImportBundle(notes, categories)
} catch (_: Exception) {
    ImportBundle()
}

private fun exportNotesToZip(context: Context, state: NotesState, uri: Uri, format: String): Boolean = runCatching {
    val output = context.contentResolver.openOutputStream(uri) ?: return@runCatching false
    output.use { stream ->
        ZipOutputStream(BufferedOutputStream(stream)).use { zip ->
            fun addEntry(path: String, body: String) {
                zip.putNextEntry(ZipEntry(path))
                zip.write(body.toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
            var index = 1
            state.notes.sortedBy { it.createdAt }.forEach { note ->
                val title = note.content.lineSequence().firstOrNull().orEmpty().ifBlank { "随记" }
                val name = safeExportName(title)
                addEntry("随记/${index++}-$name.$format", exportDocument(title, editTime(note.createdAt), note.content, format))
            }
            state.diaries.sortedBy { it.date }.forEach { diary ->
                val title = diary.title.ifBlank { "日记" }
                addEntry("日记/${safeExportName(title)}-${diary.date}.$format", exportDocument(title, diary.date, diary.markdown, format))
            }
            state.letters.sortedBy { it.deliverDate }.forEach { letter ->
                val title = letter.title.ifBlank { "未来的信" }
                addEntry("信件/${safeExportName(title)}-${letter.deliverDate}.$format", exportDocument(title, letter.deliverDate, letter.content, format))
            }
        }
    }
    true
}.getOrDefault(false)

private fun exportDocument(title: String, date: String, content: String, format: String): String {
    return if (format == "md") {
        "# $title\n\n日期：$date\n\n$content\n"
    } else {
        "标题：$title\n日期：$date\n\n${plainExportText(content)}\n"
    }
}

private fun plainExportText(value: String): String = value
    .replace(Regex("^\\s{0,3}#{1,6}\\s+", RegexOption.MULTILINE), "")
    .replace(Regex("[*_`~]"), "")
    .replace(Regex("^\\s*[-*+]\\s+", RegexOption.MULTILINE), "• ")

private fun safeExportName(value: String): String = value.replace(Regex("[\\\\/:*?\"<>|\\r\\n]+"), "_").trim().take(48).ifBlank { "未命名" }

@Composable
private fun LetterPage(state: NotesState, due: List<FutureLetter>, padding: PaddingValues, onBack: () -> Unit, onEdit: (FutureLetter) -> Unit, onCreate: () -> Unit, onUpdate: (NotesState) -> Unit) {
    var dragDistance by remember { mutableStateOf(0f) }
    var startedFromEdge by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragStart = { start -> startedFromEdge = start.x <= 32.dp.toPx() },
            onHorizontalDrag = { _, amount -> if (startedFromEdge) dragDistance += amount },
            onDragEnd = { if (startedFromEdge && dragDistance > 80f) onBack(); dragDistance = 0f; startedFromEdge = false },
            onDragCancel = { dragDistance = 0f; startedFromEdge = false },
        )
    }) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Column { Text("未来的信", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("到日期后打开 App 查看", color = NoteMuted) }; Spacer(Modifier.weight(1f)); Button(onClick = onCreate) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(4.dp)); Text("写信") } }; if (due.isNotEmpty()) { Text("今天给你的信", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp)); due.forEach { letter -> Card(Modifier.fillMaxWidth().padding(top = 10.dp).clickable { onUpdate(state.copy(letters = state.letters.map { if (it.id == letter.id) it.copy(shown = true) else it })) }, colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(16.dp)) { Text(letter.title.ifBlank { "给未来的自己" }, fontWeight = FontWeight.Bold); Text(letter.content, Modifier.padding(top = 8.dp), maxLines = 4, overflow = TextOverflow.Ellipsis) } } } }; Text("全部信件", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp)); LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 12.dp)) { items(state.letters, key = { it.id }) { letter -> Card(Modifier.fillMaxWidth().clickable { onEdit(letter) }, colors = CardDefaults.cardColors(containerColor = Color.White)) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.MailOutline, null, tint = NotePrimary); Spacer(Modifier.width(10.dp)); Column { Text(letter.title.ifBlank { "给未来的自己" }, fontWeight = FontWeight.Bold); Text("${letter.deliverDate}${if (letter.shown) " · 已展示" else ""}", color = NoteMuted, style = MaterialTheme.typography.labelSmall) } } } } } }
}

@Composable
private fun QuickNoteEditorPreview() = Unit

@Composable
private fun DiaryEditor(diary: Diary, onDismiss: () -> Unit, onSave: (Diary) -> Unit) {
    var title by remember { mutableStateOf(diary.title) }
    var text by remember { mutableStateOf(diary.markdown) }
    var preview by rememberSaveable(diary.id) { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize(), color = NotePaper) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("编辑日记", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { preview = !preview }) { Text(if (preview) "编辑" else "预览") }
                    TextButton(onClick = { onSave(diary.copy(title = title, markdown = text)) }) { Text("保存") }
                }
                OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("标题") })
                Spacer(Modifier.height(10.dp))
                if (preview) {
                    MarkdownPreview(text, Modifier.fillMaxWidth().weight(1f))
                } else {
                    OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().weight(1f), label = { Text("Markdown") }, maxLines = 30)
                }
            }
        }
    }
}

@Composable
private fun MarkdownPreview(markdown: String, modifier: Modifier = Modifier) {
    val lines = remember(markdown) { markdown.replace("\r\n", "\n").split('\n') }
    var inCodeBlock = false
    Column(modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        if (markdown.isBlank()) {
            Text("暂无内容", color = NoteMuted)
        }
        lines.forEach { rawLine ->
            val line = rawLine.trimEnd()
            if (line.trimStart().startsWith("```")) {
                inCodeBlock = !inCodeBlock
                if (inCodeBlock) Spacer(Modifier.height(4.dp))
            } else if (inCodeBlock) {
                Surface(color = NoteSoft, shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(line, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontFamily = FontFamily.Monospace, color = NoteInk)
                }
            } else {
                val heading = Regex("^(#{1,6})\\s+(.+)$").find(line)
                val bullet = Regex("^\\s*[-*+]\\s+(.+)$").find(line)
                val numbered = Regex("^\\s*(\\d+)[.)]\\s+(.+)$").find(line)
                when {
                    line.isBlank() -> Spacer(Modifier.height(2.dp))
                    line.matches(Regex("^\\s*([-*_])(?:\\s*\\1){2,}\\s*$")) -> Divider(color = NoteSoft)
                    heading != null -> {
                        val level = heading.groupValues[1].length
                        val style = when (level) {
                            1, 2 -> MaterialTheme.typography.bodyLarge
                            3 -> MaterialTheme.typography.titleLarge
                            else -> MaterialTheme.typography.titleMedium
                        }
                        Text(markdownInline(heading.groupValues[2]), style = style, fontWeight = FontWeight.Bold, color = NoteInk)
                    }
                    bullet != null -> Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Text("•", modifier = Modifier.padding(end = 8.dp), fontWeight = FontWeight.Bold, color = NoteAccent)
                        Text(markdownInline(bullet.groupValues[1]), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = NoteInk)
                    }
                    numbered != null -> Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Text("${numbered.groupValues[1]}. ", modifier = Modifier.padding(end = 4.dp), fontWeight = FontWeight.Bold, color = NoteAccent)
                        Text(markdownInline(numbered.groupValues[2]), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = NoteInk)
                    }
                    line.trimStart().startsWith("> ") -> Text(markdownInline(line.trimStart().removePrefix("> ")), modifier = Modifier.fillMaxWidth().padding(start = 12.dp), style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic), color = NoteMuted)
                    else -> Text(markdownInline(line), modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodyLarge, color = NoteInk)
                }
            }
        }
    }
}

private class MarkdownVisualTransformation(private val comments: List<NoteComment> = emptyList()) : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        val source = text.text
        val hidden = BooleanArray(source.length)
        val taskMatches = Regex("(?m)^[ \\t]*- \\[([ xX])\\]").findAll(source).toList()
        val taskCheckboxes = taskMatches.associate { match ->
            val boxIndex = match.range.first + match.value.lastIndexOf('[')
            boxIndex to if (match.groupValues[1].equals(" ")) "☐" else "☑"
        }
        taskMatches.forEach { match ->
            val boxIndex = match.range.first + match.value.lastIndexOf('[')
            val markerStart = match.range.first + match.value.indexOf("- [")
            for (markerIndex in markerStart until boxIndex) hidden[markerIndex] = true
            hidden[boxIndex + 1] = true
            hidden[boxIndex + 2] = true
        }
        fun hide(start: Int, end: Int) {
            for (index in start.coerceAtLeast(0) until end.coerceAtMost(source.length)) hidden[index] = true
        }
        val inlinePattern = Regex("(\\*\\*|__)(.*?)\\1|(?<!\\*)\\*([^*]*?)\\*|~~(.*?)~~|`([^`]*?)`|<u>(.*?)</u>|==([^=]*?)==")
        inlinePattern.findAll(source).forEach { match ->
            when {
                match.groupValues[1].isNotEmpty() -> { val size = match.groupValues[1].length; hide(match.range.first, match.range.first + size); hide(match.range.last + 1 - size, match.range.last + 1) }
                match.groupValues[3].isNotEmpty() -> { hide(match.range.first, match.range.first + 1); hide(match.range.last, match.range.last + 1) }
                match.groupValues[4].isNotEmpty() -> { hide(match.range.first, match.range.first + 2); hide(match.range.last + 1 - 2, match.range.last + 1) }
                match.groupValues[5].isNotEmpty() -> { hide(match.range.first, match.range.first + 1); hide(match.range.last, match.range.last + 1) }
                match.groupValues[6].isNotEmpty() -> { hide(match.range.first, match.range.first + 3); hide(match.range.last + 1 - 4, match.range.last + 1) }
                match.groupValues[7].isNotEmpty() -> { hide(match.range.first, match.range.first + 2); hide(match.range.last + 1 - 2, match.range.last + 1) }
            }
        }
        val noteLinkPattern = Regex("\\[\\[note:([^|\\]]+)\\|([^\\]]*)]]")
        noteLinkPattern.findAll(source).forEach { match ->
            val title = match.groups[2]!!.range
            for (index in match.range.first until title.first) hidden[index] = true
            for (index in title.last + 1..match.range.last) hidden[index] = true
        }
        val webLinkPattern = Regex("\\[([^\\]]+)]\\(([^)]+)\\)")
        webLinkPattern.findAll(source).forEach { match ->
            hidden[match.range.first] = true
            for (index in match.groups[1]!!.range.last + 1..match.range.last) hidden[index] = true
        }
        val sourceToDisplay = IntArray(source.length + 1)
        val displayToSourceList = mutableListOf<Int>()
        val markerDisplayRanges = mutableListOf<IntRange>()
        val taskCheckboxDisplayRanges = mutableListOf<IntRange>()
        val commentsByPosition = comments.groupBy { it.position.coerceIn(0, source.length) }
        val numberedLineStarts = Regex("(?m)^[ \\t]*\\d+[.)][ \\t]+")
            .findAll(source)
            .map { it.range.first }
            .toSet()
        val display = StringBuilder()
        for (index in 0..source.length) {
            sourceToDisplay[index] = display.length
            commentsByPosition[index].orEmpty().forEach {
                val markerStart = display.length
                display.append("💬")
                repeat(display.length - markerStart) { displayToSourceList += index }
                markerDisplayRanges += markerStart until display.length
            }
            if (index in numberedLineStarts) {
                display.append(0x2009.toChar())
                displayToSourceList += index
            }
            sourceToDisplay[index] = display.length
            if (index == source.length) break
            val character = source[index]
            if (!hidden[index]) {
                val atLineStart = index == 0 || source[index - 1] == '\n'
                val taskCheckbox = taskCheckboxes[index]
                if (taskCheckbox != null) {
                    val boxStart = display.length
                    display.append(taskCheckbox)
                    taskCheckboxDisplayRanges += boxStart until display.length
                } else {
                    display.append(if (character == '-' && atLineStart && source.getOrNull(index + 1) == ' ') '•' else character)
                }
                displayToSourceList += index
            }
        }
        val styled = buildAnnotatedString {
            append(display.toString())
            markerDisplayRanges.forEach { range -> addStyle(SpanStyle(color = NoteAccent, fontWeight = FontWeight.Bold), range.first, range.last + 1) }
            taskCheckboxDisplayRanges.forEach { range -> addStyle(SpanStyle(color = NotePrimary, fontWeight = FontWeight.Bold), range.first, range.last + 1) }
            fun styleRange(range: IntRange, style: SpanStyle) {
                val start = sourceToDisplay[range.first.coerceIn(0, source.length)]
                val end = sourceToDisplay[(range.last + 1).coerceIn(0, source.length)]
                if (start < end) addStyle(style, start, end)
            }
            Regex("(?m)^(#{1,6})\\s+.+$").findAll(source).forEach { match ->
                val level = match.groupValues[1].length
                val headingStyle = if (level <= 2) SpanStyle(fontWeight = FontWeight.Bold, color = NoteInk)
                else SpanStyle(fontWeight = FontWeight.Bold, fontSize = if (level == 3) 18.sp else 16.sp, color = NoteInk)
                styleRange(match.range, headingStyle)
            }
            inlinePattern.findAll(source).forEach { match ->
                val contentRange = when {
                    match.groupValues[2].isNotEmpty() -> match.groups[2]?.range
                    match.groupValues[3].isNotEmpty() -> match.groups[3]?.range
                    match.groupValues[4].isNotEmpty() -> match.groups[4]?.range
                    match.groupValues[5].isNotEmpty() -> match.groups[5]?.range
                    match.groupValues[6].isNotEmpty() -> match.groups[6]?.range
                    match.groupValues[7].isNotEmpty() -> match.groups[7]?.range
                    else -> null
                }
                if (contentRange != null) {
                    val style = when {
                        match.groupValues[2].isNotEmpty() -> SpanStyle(fontWeight = FontWeight.Bold)
                        match.groupValues[3].isNotEmpty() -> SpanStyle(fontStyle = FontStyle.Italic)
                        match.groupValues[4].isNotEmpty() -> SpanStyle(textDecoration = TextDecoration.LineThrough)
                        match.groupValues[5].isNotEmpty() -> SpanStyle(fontFamily = FontFamily.Monospace, background = NoteSoft)
                        match.groupValues[6].isNotEmpty() -> SpanStyle(textDecoration = TextDecoration.Underline)
                        else -> SpanStyle(background = Color(0xFFFFF1A8))
                    }
                    styleRange(contentRange, style)
                }
            }
            noteLinkPattern.findAll(source).forEach { match ->
                styleRange(match.groups[2]!!.range, SpanStyle(color = NotePrimary, textDecoration = TextDecoration.Underline))
            }
            webLinkPattern.findAll(source).forEach { match ->
                styleRange(match.groups[1]!!.range, SpanStyle(color = NotePrimary, textDecoration = TextDecoration.Underline))
            }
            taskMatches.filter { it.groupValues[1].equals("x", ignoreCase = true) }.forEach { match ->
                val contentStart = match.range.last + 1
                val contentEnd = source.indexOf('\n', contentStart).let { if (it < 0) source.length else it }
                if (contentStart < contentEnd) {
                    styleRange(contentStart until contentEnd, SpanStyle(textDecoration = TextDecoration.LineThrough))
                }
            }
            NoteTagRegex.findAll(source).forEach { match -> styleRange(match.range, SpanStyle(color = NoteTagColor, fontWeight = FontWeight.Medium, fontSize = 12.sp)) }
        }
        val displayToSource = IntArray(displayToSourceList.size + 1)
        displayToSourceList.forEachIndexed { index, sourceIndex -> displayToSource[index] = sourceIndex }
        displayToSource[displayToSource.lastIndex] = source.length
        return TransformedText(styled, object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = sourceToDisplay[offset.coerceIn(0, source.length)]
            override fun transformedToOriginal(offset: Int): Int = displayToSource[offset.coerceIn(0, displayToSource.lastIndex)]
        })
    }
}

private fun markdownInline(value: String) = buildAnnotatedString {
    val pattern = Regex("""(\*\*|__)(.*?)\1|(?<!\*)\*([^*]*?)\*|~~(.*?)~~|`([^`]*?)`|<u>(.*?)</u>|\[([^]]+)]\([^)]+\)|==([^=]*?)==|(?<![\p{L}\d_])#[\p{L}\d][\p{L}\d/._-]*(?=\s|$)|\[\[note:([^|\]]+)\|([^\]]*)]]""")
    var cursor = 0
    pattern.findAll(value).forEach { match ->
        append(value.substring(cursor, match.range.first))
        when {
            match.groupValues[1].isNotEmpty() -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(match.groupValues[2]) }
            match.groupValues[3].isNotEmpty() -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(match.groupValues[3]) }
            match.groupValues[4].isNotEmpty() -> withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append(match.groupValues[4]) }
            match.groupValues[5].isNotEmpty() -> withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = NoteSoft)) { append(match.groupValues[5]) }
            match.groupValues[6].isNotEmpty() -> withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append(match.groupValues[6]) }
            match.groupValues[7].isNotEmpty() -> {
                val webLink = Regex("\\[([^\\]]+)]\\(([^)]+)\\)").matchEntire(match.value)
                val url = webLink?.groupValues?.get(2)
                if (url != null) pushStringAnnotation("WEB_URL", url)
                withStyle(SpanStyle(color = NotePrimary, textDecoration = TextDecoration.Underline)) { append(match.groupValues[7]) }
                if (url != null) pop()
            }
            match.groupValues[8].isNotEmpty() -> withStyle(SpanStyle(background = Color(0xFFFFF1A8))) { append(match.groupValues[8]) }
            match.groupValues[10].isNotEmpty() -> withStyle(SpanStyle(color = NotePrimary, textDecoration = TextDecoration.Underline)) { append(match.groupValues[10]) }
            match.value.startsWith("#") -> withStyle(SpanStyle(color = NoteTagColor, fontWeight = FontWeight.Medium, fontSize = 12.sp)) { append(match.value) }
        }
        cursor = match.range.last + 1
    }
    append(value.substring(cursor))
}
@Composable
private fun LetterEditor(letter: FutureLetter, onDismiss: () -> Unit, onSave: (FutureLetter) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var title by remember { mutableStateOf(letter.title) }
    var text by remember { mutableStateOf(letter.content) }
    var date by remember { mutableStateOf(letter.deliverDate) }
    var dragDistance by remember { mutableStateOf(0f) }
    var startedFromEdge by remember { mutableStateOf(false) }
    fun chooseDate() {
        val parts = date.split('-').mapNotNull { it.toIntOrNull() }
        val calendar = Calendar.getInstance().apply { if (parts.size == 3) set(parts[0], parts[1] - 1, parts[2]) }
        DatePickerDialog(context, { _, year, month, day -> date = "%04d-%02d-%02d".format(Locale.CHINA, year, month + 1, day) }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            Modifier.fillMaxSize().pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { start -> startedFromEdge = start.x <= 32.dp.toPx() },
                    onHorizontalDrag = { _, amount -> if (startedFromEdge) dragDistance += amount },
                    onDragEnd = { if (startedFromEdge && dragDistance > 80f) onDismiss(); dragDistance = 0f; startedFromEdge = false },
                    onDragCancel = { dragDistance = 0f; startedFromEdge = false },
                )
            },
            color = NotePaper,
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, "返回我的") }
                    Text("写给未来的自己", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { if (date.isNotBlank()) onSave(letter.copy(title = title, content = text, deliverDate = date)) }) { Text("保存") }
                }
                OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("标题（可选）") })
                OutlinedTextField(date, { date = it }, Modifier.fillMaxWidth().padding(top = 10.dp), label = { Text("拆信时间") }, trailingIcon = { IconButton(onClick = ::chooseDate) { Icon(Icons.Filled.CalendarMonth, "选择拆信日期", tint = NotePrimary) } })
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().weight(1f), label = { Text("正文") }, maxLines = 30)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PhotoOrganizerPanel(
    context: Context,
    photos: List<Uri>,
    index: Int,
    draft: String,
    deleteAfterSave: Boolean,
    categories: List<NoteCategory>,
    allNotes: List<QuickNote>,
    modifier: Modifier,
    onDraftChange: (String) -> Unit,
    onDeleteChange: (Boolean) -> Unit,
    onPreview: (Uri) -> Unit,
    onSave: () -> Unit,
    onSaveEditedNote: (QuickNote) -> Unit,
    onAdvance: () -> Unit,
) {
    var expandedEditor by remember { mutableStateOf(false) }
    var editorSaved by remember { mutableStateOf(false) }
    var expandedDraft by remember(photos.getOrNull(index)) { mutableStateOf<QuickNote?>(null) }
    val uri = photos.getOrNull(index)
    if (uri == null) {
        EmptyHint("文件夹中没有可整理的照片")
        return
    }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("照片 ${index + 1} / ${photos.size}", color = NoteMuted, style = MaterialTheme.typography.labelMedium)
        FolderPhotoThumb(context, uri, Modifier.fillMaxWidth().weight(0.9f).combinedClickable(onClick = {}, onLongClick = { onPreview(uri) }))
        OutlinedTextField(
            draft,
            onDraftChange,
            Modifier.fillMaxWidth().weight(1.1f),
            placeholder = { Text("记录这张图片的关键信息…") },
            maxLines = 12,
            trailingIcon = {
                IconButton(onClick = {
                    val now = System.currentTimeMillis()
                    editorSaved = false
                    expandedDraft = QuickNote(UUID.randomUUID().toString(), draft, now, now, emptyList(), imageUris = listOf(uri.toString()))
                    expandedEditor = true
                }) { Icon(Icons.Filled.OpenInFull, "放大编辑", tint = NotePrimary) }
            },
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(deleteAfterSave, onDeleteChange)
            Text("保存笔记后删除原图", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onSave,
                enabled = draft.trim().isNotEmpty(),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 11.dp),
            ) {
                Icon(Icons.Filled.Save, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(7.dp))
                Text("保存笔记并下一张")
            }
        }
    }
    if (expandedEditor) {
        val note = expandedDraft ?: QuickNote(UUID.randomUUID().toString(), draft, System.currentTimeMillis(), System.currentTimeMillis(), emptyList(), imageUris = listOf(uri.toString()))
        QuickNoteEditor(
            note = note,
            categories = categories,
            allNotes = allNotes,
            onDismiss = {
                expandedEditor = false
                expandedDraft = null
                if (editorSaved) onAdvance()
            },
            onSave = { saved -> onSaveEditedNote(saved); editorSaved = true },
            onCreateLinkedNote = onSaveEditedNote,
            onOpenLinkedNote = {},
            onNext = {},
            onDelete = {},
            onToggleFavorite = {},
            onTogglePinned = { true },
        )
    }
}

@Composable
private fun FolderPhotoThumb(context: Context, uri: Uri, modifier: Modifier = Modifier) {
    val bitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) {
            runCatching { context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it)?.asImageBitmap() } }.getOrNull()
        }
    }
    Card(modifier, colors = CardDefaults.cardColors(containerColor = NoteSoft)) {
        if (bitmap != null) Image(bitmap!!, contentDescription = "照片", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("读取中", style = MaterialTheme.typography.labelSmall, color = NoteMuted) }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ImagePreviewDialog(context: Context, uri: Uri, onDismiss: () -> Unit) {
    val bitmap by produceState<Bitmap?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) { runCatching { context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) } }.getOrNull() }
    }
    var ocrText by remember(uri) { mutableStateOf("") }
    var ocrLoading by remember(uri) { mutableStateOf(false) }
    fun recognizeText() {
        val source = bitmap ?: return
        ocrLoading = true
        val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        recognizer.process(InputImage.fromBitmap(source, 0))
            .addOnSuccessListener { result -> ocrText = result.text; ocrLoading = false; recognizer.close() }
            .addOnFailureListener { ocrText = "识别失败，请换一张清晰图片"; ocrLoading = false; recognizer.close() }
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize(), color = Color.Black) {
            Column(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    if (bitmap != null) Image(bitmap!!.asImageBitmap(), contentDescription = "放大图片", modifier = Modifier.fillMaxSize().combinedClickable(onClick = {}, onLongClick = { recognizeText() }), contentScale = ContentScale.Fit)
                    else Text("读取中", color = Color.White)
                    IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)) { Icon(Icons.Filled.Close, "关闭", tint = Color.White) }
                }
                if (ocrLoading) Text("正在本地识别文字…", color = Color.White, modifier = Modifier.padding(12.dp))
                if (ocrText.isNotBlank()) Surface(color = Color.White, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.28f)) { SelectionContainer { Text(ocrText, color = NoteInk, modifier = Modifier.verticalScroll(rememberScrollState()).padding(14.dp)) } }
            }
        }
    }
}

@Composable private fun EmptyHint(text: String) { Surface(color = NoteSoft, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) { Text(text, color = NoteInk, modifier = Modifier.padding(16.dp)) } }
