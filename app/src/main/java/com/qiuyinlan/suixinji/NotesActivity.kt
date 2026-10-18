package com.qiuyinlan.suixinji

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.os.Environment
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.produceState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.window.Dialog
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
private val NotePrimary = Color(0xFF8B6A52)
private val NoteAccent = Color(0xFFC68E6B)
private val NotePaper = Color(0xFFF4E8DA)
private val NoteSoft = Color(0xFFEAD9C7)
private val NoteMuted = Color(0xFF8A786B)
private val NoteTagColor = Color(0xFF2878B5)
private val NoteTypography = Typography().copy(
    displayLarge = androidx.compose.ui.text.TextStyle(fontSize = 22.sp, lineHeight = 26.sp),
    displayMedium = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, lineHeight = 24.sp),
    displaySmall = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, lineHeight = 22.sp),
    headlineLarge = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, lineHeight = 24.sp),
    headlineMedium = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, lineHeight = 22.sp),
    headlineSmall = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, lineHeight = 20.sp),
    titleLarge = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, lineHeight = 19.sp),
    titleMedium = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, lineHeight = 17.sp),
    titleSmall = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    bodyLarge = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
    bodyMedium = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, lineHeight = 16.sp),
    bodySmall = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, lineHeight = 14.sp),
    labelLarge = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelMedium = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, lineHeight = 14.sp),
    labelSmall = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, lineHeight = 13.sp),
)
private val NoteTagRegex = Regex("(?<![\\p{L}\\d_])#([\\p{L}\\d][\\p{L}\\d/_-]*)(?=\\s|$)")
private val TagPalette = listOf(
    0xFFE8D5C4.toInt(), 0xFFD8E2DC.toInt(), 0xFFF6E7CB.toInt(), 0xFFD9E4F5.toInt(),
    0xFFE6D5E8.toInt(), 0xFFF2D0A9.toInt(), 0xFFD5E8D4.toInt(), 0xFFF4CCCC.toInt(),
)
private fun tagColor(category: NoteCategory): Color {
    val stored = category.color.takeIf { it != 0L }?.toInt()
    return Color(stored ?: TagPalette[Math.floorMod(category.id.hashCode(), TagPalette.size)])
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
    val mood: String = "NORMAL",
    val comments: List<NoteComment> = emptyList(),
    val imageUris: List<String> = emptyList(),
)
private data class NoteCategory(val id: String, val name: String, val parentId: String?, val path: String, val color: Long = 0L)
private data class ImportBundle(val notes: List<QuickNote> = emptyList(), val categories: List<NoteCategory> = emptyList()) {
    val size: Int get() = notes.size
    fun isEmpty(): Boolean = notes.isEmpty()
}
private data class WeReadBook(val id: String, val title: String, val noteCount: Int)
private data class Diary(val id: String, val title: String, val markdown: String, val date: String)
private data class FutureLetter(val id: String, val title: String, val content: String, val deliverDate: String, val shown: Boolean = false)
private data class NotesState(
    val notes: List<QuickNote> = emptyList(),
    val categories: List<NoteCategory> = emptyList(),
    val diaries: List<Diary> = emptyList(),
    val letters: List<FutureLetter> = emptyList(),
)

private object NotesStore {
    private const val PREF = "suixin_notes"
    private const val KEY = "state"
    private const val BACKUP_NAME = "suixinji-notes-backup.json"

    fun load(context: Context): NotesState {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val local = prefs.getString(KEY, null)
        if (!local.isNullOrBlank()) {
            // Migrate data created by older builds before a possible uninstall.
            if (backupUri(context) == null) writeBackup(context, local)
            return parse(local)
        }
        val backup = readBackup(context)
        if (!backup.isNullOrBlank()) {
            prefs.edit().putString(KEY, backup).apply()
            return parse(backup)
        }
        return NotesState()
    }

    private fun parse(raw: String): NotesState = runCatching {
        val root = JSONObject(raw)
        NotesState(
            notes = root.optJSONArray("notes").toList { item ->
                QuickNote(
                    id = item.optString("id"),
                    content = item.optString("content"),
                    createdAt = item.optLong("createdAt"),
                    updatedAt = item.optLong("updatedAt", item.optLong("createdAt")),
                    categoryIds = item.optJSONArray("categoryIds").toStringList(),
                    status = item.optString("status", "inbox"),
                    location = item.optNullableString("location"),
                    mood = item.optString("mood", "NORMAL"),
                    comments = item.optJSONArray("comments").toList { comment ->
                        NoteComment(comment.optInt("position"), comment.optString("text"), comment.optBoolean("expanded", true), comment.optLong("createdAt", System.currentTimeMillis()))
                    },
                    imageUris = item.optJSONArray("imageUris").toStringList(),
                )
            },
            categories = root.optJSONArray("categories").toList { item -> NoteCategory(item.optString("id"), item.optString("name"), item.optNullableString("parentId"), item.optString("path"), item.optLong("color", 0L)) },
            diaries = root.optJSONArray("diaries").toList { item -> Diary(item.optString("id"), item.optString("title"), item.optString("markdown"), item.optString("date")) },
            letters = root.optJSONArray("letters").toList { item -> FutureLetter(item.optString("id"), item.optString("title"), item.optString("content"), item.optString("deliverDate"), item.optBoolean("shown")) },
        )
    }.getOrDefault(NotesState())

    fun save(context: Context, state: NotesState) {
        val raw = JSONObject().apply {
            put("notes", JSONArray().apply {
                state.notes.forEach { note -> put(JSONObject().apply {
                    put("id", note.id); put("content", note.content); put("createdAt", note.createdAt); put("updatedAt", note.updatedAt)
                    put("categoryIds", JSONArray(note.categoryIds)); put("status", note.status); put("location", note.location); put("mood", note.mood)
                    put("comments", JSONArray().apply { note.comments.forEach { comment -> put(JSONObject().apply { put("position", comment.position); put("text", comment.text); put("expanded", comment.expanded); put("createdAt", comment.createdAt) }) } })
                    put("imageUris", JSONArray(note.imageUris))
                }) }
            })
            put("categories", JSONArray().apply { state.categories.forEach { put(JSONObject().apply { put("id", it.id); put("name", it.name); put("parentId", it.parentId); put("path", it.path); put("color", it.color) }) } })
            put("diaries", JSONArray().apply { state.diaries.forEach { put(JSONObject().apply { put("id", it.id); put("title", it.title); put("markdown", it.markdown); put("date", it.date) }) } })
            put("letters", JSONArray().apply { state.letters.forEach { put(JSONObject().apply { put("id", it.id); put("title", it.title); put("content", it.content); put("deliverDate", it.deliverDate); put("shown", it.shown) }) } })
        }.toString()
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY, raw).apply()
        writeBackup(context, raw)
    }

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(typography = NoteTypography, colorScheme = lightColorScheme(primary = NotePrimary, secondary = NoteAccent, background = NotePaper, surface = Color.White, onSurface = NoteInk)) {
                NotesApp()
            }
        }
    }
}

private enum class NotesPage { HOME, ORGANIZER, TAGS, WALK, MY, LETTERS, CATEGORY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = androidx.compose.material3.rememberDrawerState(DrawerValue.Closed)
    var state by remember { mutableStateOf(NotesStore.load(context)) }
    var page by remember { mutableStateOf(NotesPage.HOME) }
    var selectedCategory by remember { mutableStateOf<NoteCategory?>(null) }
    var tagCategory by remember { mutableStateOf<NoteCategory?>(null) }
    var editorNote by remember { mutableStateOf<QuickNote?>(null) }
    var commentNote by remember { mutableStateOf<QuickNote?>(null) }
    var letterEditor by remember { mutableStateOf<FutureLetter?>(null) }
    var renameTarget by remember { mutableStateOf<NoteCategory?>(null) }
    var homeDateFilter by rememberSaveable { mutableStateOf<String?>(null) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showChildCategoryMenu by remember { mutableStateOf(false) }
    var showHomeSearch by rememberSaveable { mutableStateOf(false) }
    var showTagsSearch by rememberSaveable { mutableStateOf(false) }
    var showCategorySearch by rememberSaveable { mutableStateOf(false) }
    var newCategoryParent by remember { mutableStateOf<NoteCategory?>(null) }
    var showNewCategory by remember { mutableStateOf(false) }

    fun update(next: NotesState) { state = next; NotesStore.save(context, next) }
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { ModalDrawerSheet { DrawerContent(state, page, selectedCategory, onRename = { renameTarget = it }) { target, category -> page = target; selectedCategory = category; scope.launch { drawerState.close() } } } },
    ) {
        Scaffold(
            containerColor = NotePaper,
            topBar = {
                TopAppBar(
                    title = { Text(when (page) { NotesPage.HOME -> "随心记"; NotesPage.ORGANIZER -> "整理"; NotesPage.TAGS -> "标签"; NotesPage.WALK -> "漫步"; NotesPage.MY -> "我的"; NotesPage.LETTERS -> "信件"; NotesPage.CATEGORY -> selectedCategory?.path ?: "分类" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = {
                        val category = selectedCategory
                        if (page == NotesPage.CATEGORY && category != null) {
                            val parent = category.parentId?.let { parentId -> state.categories.firstOrNull { it.id == parentId } }
                            val children = state.categories.filter { it.parentId == category.id }
                            Box {
                                IconButton(onClick = { showCategoryMenu = true }) { Icon(Icons.Filled.Tune, "调整当前标签") }
                                DropdownMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false }) {
                                    DropdownMenuItem(text = { Text("编辑标签") }, onClick = { showCategoryMenu = false; renameTarget = category })
                                    DropdownMenuItem(text = { Text("查看上级标签") }, enabled = parent != null, onClick = { if (parent != null) { selectedCategory = parent; showCategoryMenu = false } })
                                    DropdownMenuItem(text = { Text(if (children.isEmpty()) "没有下级标签" else "查看下级标签") }, enabled = children.isNotEmpty(), onClick = { showCategoryMenu = false; showChildCategoryMenu = true })
                                }
                                DropdownMenu(expanded = showChildCategoryMenu, onDismissRequest = { showChildCategoryMenu = false }) {
                                    children.forEach { child ->
                                        DropdownMenuItem(text = { Text("#${child.name}", fontSize = 11.sp) }, onClick = { selectedCategory = child; showChildCategoryMenu = false })
                                    }
                                }
                            }
                        } else {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Filled.Menu, "打开导航") }
                        }
                    },
                    actions = {
                        when (page) {
                            NotesPage.HOME -> IconButton(onClick = { showHomeSearch = !showHomeSearch }) { Icon(Icons.Filled.Search, "搜索笔记") }
                            NotesPage.TAGS -> IconButton(onClick = { showTagsSearch = !showTagsSearch }) { Icon(Icons.Filled.Search, "搜索标签和笔记") }
                            NotesPage.CATEGORY -> IconButton(onClick = { showCategorySearch = !showCategorySearch }) { Icon(Icons.Filled.Search, "搜索当前标签") }
                            else -> Unit
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NotePaper),
                )
            },
            bottomBar = { MainNavigation(page) { target -> page = target; selectedCategory = null; homeDateFilter = null; showHomeSearch = false; showTagsSearch = false; showCategorySearch = false; if (target != NotesPage.TAGS) tagCategory = null } },
            floatingActionButton = {
                if (page == NotesPage.TAGS) {
                    FloatingActionButton(onClick = { newCategoryParent = tagCategory; showNewCategory = true }) { Icon(Icons.Filled.Add, "新建标签") }
                }
            },
        ) { padding ->
            when (page) {
                NotesPage.HOME -> HomePage(state, dueLetters, padding, homeDateFilter, showHomeSearch, onClearDateFilter = { homeDateFilter = null }, onCapture = { editorNote = it }, onCategory = { selectedCategory = it; page = NotesPage.CATEGORY }, onDiary = { page = NotesPage.WALK }, onLetters = { page = NotesPage.MY }, onOrganize = { page = NotesPage.ORGANIZER }, onEdit = { editorNote = it }, onComment = { commentNote = it }, onUpdate = ::update, onDelete = { note -> update(state.copy(notes = state.notes.filterNot { it.id == note.id })) })
                NotesPage.CATEGORY -> selectedCategory?.let { category -> CategoryPage(state, category, padding, showCategorySearch, onAdd = { editorNote = QuickNote(UUID.randomUUID().toString(), "", System.currentTimeMillis(), System.currentTimeMillis(), listOf(category.id)) }, onEdit = { editorNote = it }, onCategory = { selectedCategory = it }, onDelete = { note -> update(state.copy(notes = state.notes.filterNot { it.id == note.id })) }) }
                NotesPage.ORGANIZER -> OrganizerPage(state, padding, onUpdate = ::update, onEdit = { editorNote = it })
                NotesPage.TAGS -> TagsPage(state, tagCategory, padding, showTagsSearch, onSelect = { tagCategory = it }, onEdit = { editorNote = it }, onColorChange = { category, color -> update(state.copy(categories = state.categories.map { if (it.id == category.id) it.copy(color = color.toLong()) else it })) }, onRename = { renameTarget = it }, onDeleteNote = { note -> update(state.copy(notes = state.notes.filterNot { it.id == note.id })) }, onDeleteMany = { selectedCategories ->
                    val removedIds = state.categories.filter { item -> selectedCategories.any { category -> item.id == category.id || item.path.startsWith("${category.path}/") } }.map { it.id }.toSet()
                    update(state.copy(categories = state.categories.filterNot { it.id in removedIds }, notes = state.notes.map { note -> note.copy(categoryIds = note.categoryIds.filterNot { it in removedIds }) }))
                    tagCategory = null
                })
                NotesPage.WALK -> WalkPage(state, padding, onEdit = { editorNote = it }, onDelete = { note -> update(state.copy(notes = state.notes.filterNot { it.id == note.id })) })
                NotesPage.MY -> MyPage(state, padding, onLetters = { page = NotesPage.LETTERS }, onDayClick = { day -> homeDateFilter = day; page = NotesPage.HOME }, onImport = { bundle ->
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
                })
                NotesPage.LETTERS -> LetterPage(state, dueLetters, padding, onBack = { page = NotesPage.MY }, onEdit = { letterEditor = it }, onCreate = { letterEditor = FutureLetter(UUID.randomUUID().toString(), "", "", today()) }, onUpdate = ::update)
            }
        }
    }
    editorNote?.let { note -> QuickNoteEditor(note, state.categories, onDismiss = { editorNote = null }, onSave = { saveNote(it) }, onDelete = { update(state.copy(notes = state.notes.filterNot { current -> current.id == note.id })); editorNote = null }) }
    commentNote?.let { note -> CommentSheet(note, onDismiss = { commentNote = null }, onSave = { comment -> update(state.copy(notes = state.notes.map { current -> if (current.id == note.id) current.copy(comments = current.comments + comment, updatedAt = System.currentTimeMillis()) else current })); commentNote = null }) }
    letterEditor?.let { letter -> LetterEditor(letter, onDismiss = { letterEditor = null; page = NotesPage.MY }, onSave = { value -> update(state.copy(letters = listOf(value) + state.letters.filterNot { it.id == value.id })); letterEditor = null; page = NotesPage.MY }) }
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
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(page == NotesPage.HOME, { onSelect(NotesPage.HOME) }, icon = { Icon(Icons.Filled.Home, "首页") }, label = { Text("首页") }, colors = navColors)
        NavigationBarItem(page == NotesPage.ORGANIZER, { onSelect(NotesPage.ORGANIZER) }, icon = { Icon(Icons.Filled.Tune, "整理") }, label = { Text("整理") }, colors = navColors)
        NavigationBarItem(page == NotesPage.WALK, { onSelect(NotesPage.WALK) }, icon = { Icon(Icons.Filled.Book, "漫步") }, label = { Text("漫步") }, colors = navColors)
        NavigationBarItem(page == NotesPage.TAGS, { onSelect(NotesPage.TAGS) }, icon = { Icon(Icons.Filled.Label, "标签") }, label = { Text("标签") }, colors = navColors)
        NavigationBarItem(page == NotesPage.MY || page == NotesPage.LETTERS, { onSelect(NotesPage.MY) }, icon = { Icon(Icons.Filled.Person, "我的") }, label = { Text("我的") }, colors = navColors)
    }
}

@Composable
private fun DrawerContent(state: NotesState, page: NotesPage, selected: NoteCategory?, onRename: (NoteCategory) -> Unit, onNavigate: (NotesPage, NoteCategory?) -> Unit) {
    var expandedTags by remember { mutableStateOf(emptySet<String>()) }
    val rootTags = state.categories.filter { it.parentId == null }

    Column(Modifier.fillMaxSize().background(NotePaper).padding(vertical = 18.dp)) {
        Text("随心记", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp))
        DrawerItem("首页", Icons.Filled.Home, page == NotesPage.HOME) { onNavigate(NotesPage.HOME, null) }
        DrawerItem("随记箱 · ${state.notes.count { it.status == "inbox" }}", Icons.Filled.Inbox, page == NotesPage.ORGANIZER) { onNavigate(NotesPage.ORGANIZER, null) }
        DrawerItem("整理", Icons.Filled.Tune, page == NotesPage.ORGANIZER) { onNavigate(NotesPage.ORGANIZER, null) }
        DrawerItem("漫步", Icons.Filled.Book, page == NotesPage.WALK) { onNavigate(NotesPage.WALK, null) }
        DrawerItem("我的", Icons.Filled.Person, page == NotesPage.MY || page == NotesPage.LETTERS) { onNavigate(NotesPage.MY, null) }
        Divider(Modifier.padding(vertical = 12.dp))
        Text("分类", style = MaterialTheme.typography.labelLarge, color = NoteMuted, modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp))
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            rootTags.forEach { category ->
                DrawerTagRow(
                    category = category,
                    allCategories = state.categories,
                    notes = state.notes,
                    selected = selected,
                    expandedTags = expandedTags,
                    onToggle = { id -> expandedTags = if (id in expandedTags) expandedTags - id else expandedTags + id },
                    onRename = onRename,
                    onNavigate = onNavigate,
                )
            }
            if (rootTags.isEmpty()) {
                Text("还没有标签", color = NoteMuted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp))
            }
        }
        Text("本地保存 · ${state.notes.size} 条记录", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp))
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
    onNavigate: (NotesPage, NoteCategory?) -> Unit,
) {
    val children = allCategories.filter { it.parentId == category.id }
    val isExpanded = category.id in expandedTags
    val noteCount = notes.count { category.id in it.categoryIds }
    val selectedRow = selected?.id == category.id

    Row(
        Modifier.fillMaxWidth()
            .background(if (selectedRow) NoteSoft else Color.Transparent)
            .clickable { onNavigate(NotesPage.CATEGORY, category) }
            .padding(start = (12 + category.path.count { it == '/' } * 16).dp, end = 18.dp, top = 9.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (children.isNotEmpty()) {
            IconButton(onClick = { onToggle(category.id) }, modifier = Modifier.size(28.dp)) {
                Icon(if (isExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight, "展开或收起子标签", tint = NoteMuted)
            }
        } else {
            Spacer(Modifier.width(28.dp))
        }
        Icon(Icons.Filled.Label, null, tint = if (selectedRow) NotePrimary else NoteMuted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("#${category.name}", color = if (selectedRow) NotePrimary else NoteInk, fontSize = 11.sp, fontWeight = if (selectedRow) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1f), maxLines = 1)
        Text(noteCount.toString(), color = NoteMuted, style = MaterialTheme.typography.labelSmall)
        IconButton(onClick = { onRename(category) }, modifier = Modifier.size(30.dp)) { Icon(Icons.Filled.MoreVert, "修改标签", tint = NoteMuted, modifier = Modifier.size(19.dp)) }
    }

    if (isExpanded) {
        children.forEach { child ->
            DrawerTagRow(child, allCategories, notes, selected, expandedTags, onToggle, onRename, onNavigate)
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
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).background(if (selected) NoteSoft else Color.Transparent).padding(horizontal = 22.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = if (selected) NotePrimary else NoteMuted, modifier = Modifier.size(21.dp)); Spacer(Modifier.width(14.dp)); Text(label, color = NoteInk, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun HomePage(state: NotesState, due: List<FutureLetter>, padding: PaddingValues, dateFilter: String?, searchOpen: Boolean, onClearDateFilter: () -> Unit, onCapture: (QuickNote) -> Unit, onCategory: (NoteCategory) -> Unit, onOrganize: () -> Unit, onDiary: () -> Unit, onLetters: () -> Unit, onEdit: (QuickNote) -> Unit, onComment: (QuickNote) -> Unit, onUpdate: (NotesState) -> Unit, onDelete: (QuickNote) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val dayFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }
    val visibleNotes = state.notes.sortedByDescending { it.createdAt }.filter { note ->
        (dateFilter == null || dayFormat.format(Date(note.createdAt)) == dateFilter) &&
            (query.isBlank() || note.content.contains(query, true) || note.categoryIds.mapNotNull { id -> state.categories.firstOrNull { it.id == id }?.path }.any { path -> path.contains(query, true) })
    }
    Column(Modifier.fillMaxSize().padding(padding)) {
        LazyColumn(Modifier.fillMaxWidth().weight(1f), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("时间线", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    if (dateFilter != null) {
                        TextButton(onClick = onClearDateFilter) { Text("${dateFilter} ×", color = NotePrimary) }
                    }
                }
                if (dateFilter != null) Text("当天记录", color = NoteMuted, modifier = Modifier.padding(top = 3.dp)) else Text("想到什么，先留下来", color = NoteMuted, modifier = Modifier.padding(top = 3.dp))
                if (searchOpen) OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true, label = { Text("搜索笔记") }, leadingIcon = { Icon(Icons.Filled.Search, null) }, trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Filled.Close, "清除搜索") } })
            }
            item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { SummaryCard("随记箱", state.notes.count { it.status == "inbox" }, Icons.Filled.Inbox, onOrganize); SummaryCard("今日的信", due.size, Icons.Filled.MailOutline, onLetters) } }
            item { Row(verticalAlignment = Alignment.CenterVertically) { Text("全部记录", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); TextButton(onClick = onDiary) { Text("漫步") } } }
            if (state.notes.isEmpty()) item { EmptyHint("还没有记录，点击底部输入框开始") }
            if (visibleNotes.isEmpty() && query.isNotBlank()) item { EmptyHint("没有找到匹配的笔记") }
            items(visibleNotes, key = { it.id }) { note -> NoteTimelineCard(note, state.categories, onEdit, onComment = { onComment(note) }, onToggleComment = { commentIndex -> onUpdate(state.copy(notes = state.notes.map { current -> if (current.id == note.id) current.copy(comments = current.comments.mapIndexed { index, comment -> if (index == commentIndex) comment.copy(expanded = !comment.expanded) else comment }) else current })) }, onDelete = { onDelete(note) }) }
            if (state.categories.isNotEmpty()) item { Text("分类空间", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            if (state.categories.isNotEmpty()) item { LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxWidth().height(((state.categories.count { it.parentId == null } + 1) / 2 * 88).coerceAtLeast(88).dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(state.categories.filter { it.parentId == null }) { category -> Card(Modifier.fillMaxWidth().height(78.dp).clickable { onCategory(category) }, colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(14.dp)) { Text(category.name, fontWeight = FontWeight.Bold); Text("${state.notes.count { category.id in it.categoryIds }} 条记录", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 7.dp)) } } } } }
        }
        Box(Modifier.fillMaxWidth().background(NotePaper).padding(horizontal = 16.dp, vertical = 8.dp).clickable {
            val now = System.currentTimeMillis()
            onCapture(QuickNote(UUID.randomUUID().toString(), "", now, now, emptyList()))
        }) {
            OutlinedTextField(value = "", onValueChange = {}, enabled = false, modifier = Modifier.fillMaxWidth().height(58.dp), singleLine = true, placeholder = { Text("点击开始记录…") }, shape = RoundedCornerShape(12.dp), trailingIcon = { Icon(Icons.AutoMirrored.Filled.Send, "开始记录", tint = NotePrimary) })
        }
    }
}

@Composable
private fun SummaryCard(label: String, count: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth(0.48f).height(82.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Color.White)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = NotePrimary, modifier = Modifier.size(24.dp)); Spacer(Modifier.width(10.dp)); Column { Text(label, color = NoteMuted, style = MaterialTheme.typography.labelMedium); Text(count.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) } } }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteTimelineCard(note: QuickNote, categories: List<NoteCategory>, onEdit: (QuickNote) -> Unit, onToggleComment: (Int) -> Unit, onComment: () -> Unit = {}, onDelete: () -> Unit = {}, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable(note.id) { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val longText = note.content.length > 180 || note.content.count { it == '\n' } > 6
    Card(modifier.fillMaxWidth().combinedClickable(onClick = { if (longText) expanded = !expanded }, onDoubleClick = { onEdit(note) }, onLongClick = { showDeleteDialog = true }), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp)) {
            CommentedNotePreview(note.content, note.comments, onToggleComment, maxLines = if (longText && !expanded) 7 else Int.MAX_VALUE, showEye = false)
            if (longText) Text(if (expanded) "点击收起 · 双击编辑" else "点击展开 · 双击编辑", color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 10.dp)) { Text(prettyTime(note.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall); note.location?.let { Text(" · $it", color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }; Text(" · ${moodLabel(note.mood)}", color = NoteMuted, style = MaterialTheme.typography.labelSmall); Spacer(Modifier.weight(1f)); IconButton(onClick = onComment, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.ChatBubbleOutline, "添加评论", tint = NoteAccent, modifier = Modifier.size(19.dp)) } }
            val paths = note.categoryIds.mapNotNull { id -> categories.firstOrNull { it.id == id }?.path }
            if (paths.isNotEmpty()) Text(paths.joinToString(" · "), color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
        }
    }
    if (showDeleteDialog) AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("删除笔记") }, text = { Text("确定删除这条笔记吗？") }, confirmButton = { TextButton(onClick = { showDeleteDialog = false; onDelete() }) { Text("删除") } }, dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("取消") } })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CommentPreview(comments: List<NoteComment>, onToggle: (Int) -> Unit, showEye: Boolean = true, onDoubleClick: (Int) -> Unit = {}, modifier: Modifier = Modifier) {
    Column(modifier) {
        comments.forEachIndexed { index, comment ->
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                if (showEye) {
                    IconButton(onClick = { onToggle(index) }, modifier = Modifier.size(22.dp)) { Icon(if (comment.expanded) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, "展开或隐藏评论", tint = NoteAccent, modifier = Modifier.size(17.dp)) }
                }
                Icon(Icons.Filled.ChatBubbleOutline, "评论", tint = NoteAccent, modifier = Modifier.size(16.dp).padding(start = 2.dp, top = 3.dp))
                if ((!showEye || comment.expanded) && comment.text.isNotBlank()) {
                    Surface(color = NotePaper, shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(start = 7.dp).combinedClickable(onClick = {}, onDoubleClick = { onDoubleClick(index) })) {
                        Column(Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
                            Text(comment.text, color = NoteMuted, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp)
                            Text(prettyTime(comment.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CommentedNotePreview(content: String, comments: List<NoteComment>, onToggle: (Int) -> Unit, maxLines: Int = Int.MAX_VALUE, showEye: Boolean = true, onDoubleClickComment: (Int) -> Unit = {}, modifier: Modifier = Modifier) {
    if (comments.isEmpty()) Text(markdownInline(content), modifier = modifier, maxLines = maxLines, overflow = TextOverflow.Ellipsis)
    else if (content.isBlank()) CommentPreview(comments, onToggle, showEye, onDoubleClickComment, modifier)
    else {
        val sorted = comments.withIndex().sortedBy { it.value.position.coerceIn(0, content.length) }
        Column(modifier) {
            var cursor = 0
            sorted.forEach { indexed ->
                val position = indexed.value.position.coerceIn(cursor, content.length)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    if (position > cursor) Text(markdownInline(content.substring(cursor, position)), modifier = Modifier.weight(1f), maxLines = maxLines, overflow = TextOverflow.Ellipsis)
                    else Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.ChatBubbleOutline, "评论位置", tint = NoteAccent, modifier = Modifier.size(17.dp).combinedClickable(onClick = {}, onDoubleClick = { onDoubleClickComment(indexed.index) }))
                }
                CommentPreview(listOf(indexed.value), { onToggle(indexed.index) }, showEye = showEye, onDoubleClick = { onDoubleClickComment(indexed.index) }, modifier = Modifier.padding(vertical = 2.dp))
                cursor = position
            }
            if (cursor < content.length) Text(markdownInline(content.substring(cursor)), maxLines = maxLines, overflow = TextOverflow.Ellipsis)
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

private fun moodLabel(mood: String): String = when (mood) { "HAPPY" -> "开心"; "SAD" -> "不开心"; else -> "正常" }

@Composable
private fun CategoryPage(state: NotesState, category: NoteCategory, padding: PaddingValues, searchOpen: Boolean, onAdd: () -> Unit, onEdit: (QuickNote) -> Unit, onCategory: (NoteCategory) -> Unit, onDelete: (QuickNote) -> Unit) {
    var query by rememberSaveable(category.id) { mutableStateOf("") }
    val children = state.categories.filter { it.parentId == category.id }
    val visibleChildren = children.filter { query.isBlank() || it.name.contains(query, true) || it.path.contains(query, true) }
    val notes = state.notes.filter { note -> category.id in note.categoryIds && (query.isBlank() || note.content.contains(query, true) || note.categoryIds.mapNotNull { id -> state.categories.firstOrNull { it.id == id }?.path }.any { path -> path.contains(query, true) }) }
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (searchOpen) item { OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("搜索当前标签") }, leadingIcon = { Icon(Icons.Filled.Search, null) }, trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Filled.Close, "清除搜索") } }) }
        item { OutlinedButton(onClick = onAdd, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(6.dp)); Text("在这里写一条") } }
        if (visibleChildren.isNotEmpty()) { item { Text("子分类", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }; items(visibleChildren) { child -> Surface(Modifier.fillMaxWidth().clickable { onCategory(child) }, color = NoteSoft, shape = RoundedCornerShape(10.dp)) { Text(child.name, modifier = Modifier.padding(14.dp), color = NoteInk, fontWeight = FontWeight.Bold) } } }
        item { Text("本分类记录 · ${notes.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        items(notes, key = { it.id }) { note -> NoteTimelineCard(note, state.categories, onEdit, {}, onDelete = { onDelete(note) }) }
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
    onDeleteNote: (QuickNote) -> Unit,
    onColorChange: (NoteCategory, Int) -> Unit,
    onRename: (NoteCategory) -> Unit,
    onDeleteMany: (Set<NoteCategory>) -> Unit,
) {
    var colorTarget by remember { mutableStateOf<NoteCategory?>(null) }
    var menuTarget by remember { mutableStateOf<NoteCategory?>(null) }
    var showBulkDelete by remember { mutableStateOf(false) }
    var deleteSelection by remember { mutableStateOf<Set<String>>(emptySet()) }
    var dragDistance by remember { mutableStateOf(0f) }
    var startedFromEdge by remember { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    val categories = state.categories
    val visible = (if (selected == null) categories.filter { it.parentId == null } else categories.filter { it.parentId == selected.id }).filter { query.isBlank() || it.name.contains(query, true) || it.path.contains(query, true) }
    val notes = selected?.let { category -> state.notes.filter { note -> category.id in note.categoryIds && (query.isBlank() || note.content.contains(query, true) || note.categoryIds.mapNotNull { id -> categories.firstOrNull { it.id == id }?.path }.any { path -> path.contains(query, true) }) }.sortedByDescending { it.createdAt } }.orEmpty()
    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 12.dp).verticalScroll(rememberScrollState()).pointerInput(selected?.id) {
        detectHorizontalDragGestures(
            onDragStart = { start -> startedFromEdge = start.x <= 32.dp.toPx() },
            onHorizontalDrag = { _, amount -> if (startedFromEdge) dragDistance += amount },
            onDragEnd = {
                if (selected != null && startedFromEdge && dragDistance > 80f) onSelect(categories.firstOrNull { it.id == selected.parentId })
                dragDistance = 0f
                startedFromEdge = false
            },
            onDragCancel = { dragDistance = 0f; startedFromEdge = false },
        )
    }) {
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
        if (visible.isNotEmpty()) {
            LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxWidth().height((((visible.size + 1) / 2) * 126).coerceAtLeast(126).dp).padding(top = 12.dp), contentPadding = PaddingValues(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(visible, key = { it.id }) { category ->
                    val childCount = categories.count { it.parentId == category.id }
                    val noteCount = state.notes.count { category.id in it.categoryIds }
                    Surface(color = tagColor(category), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().aspectRatio(1.15f).combinedClickable(onClick = { onSelect(category) }, onLongClick = { deleteSelection = setOf(category.id); showBulkDelete = true })) {
                        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(category.name, style = MaterialTheme.typography.titleMedium, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NoteInk)
                                Text(category.path, style = MaterialTheme.typography.labelSmall, color = NoteMuted, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
                            }
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("$noteCount 条记录${if (childCount > 0) "  ·  $childCount 个子标签" else ""}", style = MaterialTheme.typography.labelSmall, color = NoteMuted, modifier = Modifier.weight(1f))
                                Box {
                                    IconButton(onClick = { menuTarget = category }, modifier = Modifier.size(30.dp)) { Icon(Icons.Filled.MoreVert, "标签操作", tint = NoteMuted, modifier = Modifier.size(19.dp)) }
                                    DropdownMenu(expanded = menuTarget?.id == category.id, onDismissRequest = { menuTarget = null }) {
                                        DropdownMenuItem(text = { Text("修改名字") }, onClick = { menuTarget = null; onRename(category) })
                                        DropdownMenuItem(text = { Text("调整颜色") }, onClick = { menuTarget = null; colorTarget = category })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (selected != null) {
            Text("${selected.name} 下的笔记 · ${notes.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp, bottom = 4.dp))
            if (notes.isEmpty()) EmptyHint("这个标签下还没有笔记") else Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 18.dp)) {
                notes.forEach { note -> NoteTimelineCard(note, state.categories, onEdit, {}, onDelete = { onDeleteNote(note) }) }
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

@Composable
private fun OrganizerPage(state: NotesState, padding: PaddingValues, onUpdate: (NotesState) -> Unit, onEdit: (QuickNote) -> Unit) {
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
    var undoState by remember { mutableStateOf<NotesState?>(null) }
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
    val batch = remember(mode, monthFilter, inboxIds, batchCount) {
        val source = if (mode == "MONTH" && monthFilter != "全部月份") inbox.filter { prettyMonth(it.createdAt) == monthFilter } else inbox
        if (mode == "RANDOM") source.shuffled().take(batchCount) else if (mode == "MONTH") source.take(batchCount) else source
    }
    val current = batch.getOrNull(index.coerceAtMost((batch.size - 1).coerceAtLeast(0)))
    val pageScroll = rememberScrollState()
    LaunchedEffect(mode, monthFilter, batchCount, inboxIds.joinToString("|")) {
        index = 0
        undoState = null
        lastWasSkip = false
    }
    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f).verticalScroll(pageScroll), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
            else PhotoOrganizerPanel(context, photoUris, photoIndex, photoDraft, deletePhotoAfterSave, Modifier.weight(1f), onDraftChange = { photoDraft = it }, onDeleteChange = { deletePhotoAfterSave = it }, onPreview = { photoPreview = it }, onSave = {
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
            }, onNext = { photoDraft = ""; deletePhotoAfterSave = false; photoIndex = (photoIndex + 1).coerceAtMost(photoUris.lastIndex) })
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("MONTH" to "按月份", "RANDOM" to "随机", "ALL" to "全部").forEach { (value, label) ->
                    TextButton(onClick = { mode = value; if (value == "MONTH" && monthOptions.isNotEmpty()) showMonthPicker = true }, modifier = Modifier.weight(1f)) { Text(label, color = if (mode == value) NotePrimary else NoteMuted, fontWeight = if (mode == value) FontWeight.Bold else FontWeight.Normal) }
                }
            }
            if (mode == "MONTH") OutlinedButton(onClick = { showMonthPicker = true }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Filled.CalendarMonth, null); Spacer(Modifier.width(6.dp)); Text(monthFilter) }
            if (mode == "MONTH" || mode == "RANDOM") OutlinedTextField(batchInput, { value -> batchInput = value.filter { it.isDigit() }.take(3) }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("本轮整理数量（1-500）") })
            Column(
                Modifier.fillMaxWidth().pointerInput(current?.id) {
                    if (current != null) {
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
                },
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (current == null) {
                    EmptyHint("随记箱为空，所有记录都整理好了")
                } else {
                    Text("${index + 1} / ${batch.size}", color = NoteMuted, style = MaterialTheme.typography.labelLarge)
                    Text(prettyTime(current.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelMedium)
                    SelectionContainer {
                        Column(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp)) {
                            Text(current.content, style = MaterialTheme.typography.bodyLarge, color = NoteInk)
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 18.dp)) {
                                Text(prettyTime(current.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                                current.location?.let { Text(" · $it", color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                Text(" · ${moodLabel(current.mood)}", color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                            }
                            val paths = current.categoryIds.mapNotNull { id -> state.categories.firstOrNull { it.id == id }?.path }
                            if (paths.isNotEmpty()) Text(paths.joinToString(" · "), color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 6.dp))
                            if (current.comments.isNotEmpty()) CommentedNotePreview(current.content, current.comments, {}, showEye = false, modifier = Modifier.padding(top = 12.dp))
                        }
                    }
                }
                Text("左右滑动切换记录 · 跳过的记录仍会留在随记箱", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 8.dp))
            }
            }
            Surface(color = Color.White, shadowElevation = 6.dp, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(enabled = current != null, onClick = { if (current != null) { lastWasSkip = true; undoState = null; index++ } }) { Icon(Icons.Filled.SkipNext, "跳过") }
                    IconButton(enabled = current != null, onClick = { if (current != null) selected = current }) { Icon(Icons.Filled.Label, "归类") }
                    IconButton(enabled = current != null, onClick = { if (current != null) { undoState = state; lastWasSkip = false; onUpdate(state.copy(notes = state.notes.filterNot { it.id == current.id })); index = index.coerceAtMost((batch.size - 2).coerceAtLeast(0)) } }) { Icon(Icons.Filled.DeleteOutline, "删除") }
                    IconButton(onClick = { when { undoState != null -> { onUpdate(undoState!!); index = (index - 1).coerceAtLeast(0); undoState = null }; lastWasSkip -> { index = (index - 1).coerceAtLeast(0); lastWasSkip = false } } }, enabled = undoState != null || lastWasSkip) { Icon(Icons.Filled.Undo, "撤销") }
                }
            }
        }
    }
    selected?.let { note -> CategoryPicker(state.categories, onDismiss = { selected = null }, onPick = { category -> undoState = state; lastWasSkip = false; onUpdate(state.copy(notes = state.notes.map { if (it.id == note.id) it.copy(categoryIds = listOf(category.id), status = "organized", updatedAt = System.currentTimeMillis()) else it })); selected = null; index++ }, onCreate = { raw ->
        val clean = raw.trim().trimStart('#')
        if (clean.isNotBlank()) {
            val category = state.categories.firstOrNull { it.path == clean } ?: NoteCategory(UUID.randomUUID().toString(), clean.substringAfterLast('/'), null, clean)
            undoState = state
            lastWasSkip = false
            onUpdate(state.copy(categories = if (state.categories.any { it.path == category.path }) state.categories else state.categories + category, notes = state.notes.map { if (it.id == note.id) it.copy(categoryIds = listOf(category.id), status = "organized", updatedAt = System.currentTimeMillis()) else it }))
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
    AlertDialog(onDismissRequest = onDismiss, title = { Text("选择分类") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(newCategory, { newCategory = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("新建分类") }, placeholder = { Text("例如：生活/阅读") })
            if (newCategory.isNotBlank()) TextButton(onClick = { onCreate(newCategory) }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(6.dp)); Text("新建并归类") }
            categories.forEach { category -> TextButton(onClick = { onPick(category) }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Filled.Folder, null); Spacer(Modifier.width(8.dp)); Text(category.path) } }
        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("取消") } })
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun QuickNoteEditor(note: QuickNote, categories: List<NoteCategory>, onDismiss: () -> Unit, onSave: (QuickNote) -> Unit, onDelete: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf(TextFieldValue(note.content)) }
    val undoStack = remember { mutableStateListOf<TextFieldValue>() }
    val redoStack = remember { mutableStateListOf<TextFieldValue>() }
    var showMoreFormatting by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf(note.location) }
    var mood by remember { mutableStateOf(note.mood) }
    var comments by remember { mutableStateOf(note.comments) }
    var commentDraft by remember { mutableStateOf("") }
    var activeComment by remember { mutableStateOf<Int?>(null) }
    var createdAt by remember { mutableStateOf(note.createdAt) }
    var showTimeDialog by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf(note.imageUris) }
    var previewUri by remember { mutableStateOf<Uri?>(null) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    var showNoteDeleteDialog by remember { mutableStateOf(false) }
    var savedNote by remember { mutableStateOf(note) }
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
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    fun replaceEditorText(value: TextFieldValue) {
        if (value != text) {
            undoStack.add(text)
            if (undoStack.size > 50) undoStack.removeAt(0)
            redoStack.clear()
            text = value
        }
    }
    fun handleEditorInput(value: TextFieldValue) {
        val oldText = text.text
        val newText = value.text
        val insertedNewline = newText.length == oldText.length + 1 && value.selection.start > 0 && newText.getOrNull(value.selection.start - 1) == '\n'
        if (!insertedNewline) {
            replaceEditorText(value)
            return
        }
        val newline = value.selection.start - 1
        val lineStart = newText.lastIndexOf('\n', (newline - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
        val previousLine = newText.substring(lineStart, newline)
        val emptyBullet = Regex("^(\\s*)([-*+])\\s*$").find(previousLine)
        val bullet = Regex("^(\\s*)([-*+])\\s+.+$").find(previousLine)
        val numbered = Regex("^(\\s*)(\\d+)[.)]\\s+(.+)$").find(previousLine)
        when {
            emptyBullet != null -> {
                val cleaned = newText.removeRange(lineStart, newline)
                replaceEditorText(TextFieldValue(cleaned, TextRange(value.selection.start - (newline - lineStart))))
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
    }
    fun prefixCurrentLine(prefix: String) {
        val start = text.selection.start.coerceIn(0, text.text.length)
        val lineStart = text.text.lastIndexOf('\n', (start - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
        replaceEditorText(TextFieldValue(text.text.substring(0, lineStart) + prefix + text.text.substring(lineStart), TextRange(start + prefix.length)))
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
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
    Dialog(onDismissRequest = ::requestDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize(), color = NotePaper) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = ::requestDismiss) { Icon(Icons.Filled.Close, "关闭") }
                    Text(if (note.content.isBlank()) "记录此刻" else "编辑随记", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { saveChanges() }, enabled = text.text.trim().isNotEmpty() || imageUris.isNotEmpty()) { Icon(Icons.Filled.Save, "保存") }
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ToolButton(Icons.Filled.LocationOn, "位置", location != null) { if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) scope.launch(Dispatchers.IO) { val label = getLastLocationLabel(context) ?: "位置已授权"; withContext(Dispatchers.Main) { location = label } } else locationLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) }
                    ToolButton(Icons.Filled.Label, "标签", false) { showTagDialog = true; tagInput = "" }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { val cursor = text.selection.start; text = text.copy(text = text.text.substring(0, cursor) + "#" + text.text.substring(cursor), selection = TextRange(cursor + 1)) }.padding(horizontal = 5.dp)) { Text("#", color = NotePrimary, fontSize = 23.sp, fontWeight = FontWeight.Bold); Text("快捷标签", color = NoteMuted, style = MaterialTheme.typography.labelSmall) }
                    ToolButton(Icons.Filled.CalendarMonth, "日期", false) { showTimeDialog = true }
                    ToolButton(moodIcon(mood), moodLabel(mood), mood == "HAPPY") { mood = when (mood) { "HAPPY" -> "NORMAL"; "NORMAL" -> "SAD"; else -> "HAPPY" } }
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
                Row(Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { wrapSelection("**") }) { Icon(Icons.Filled.FormatBold, "加粗") }
                    IconButton(onClick = { wrapSelection("==") }) { Icon(Icons.Filled.FormatColorFill, "荧光笔") }
                    IconButton(onClick = { wrapSelection("~~") }) { Icon(Icons.Filled.StrikethroughS, "删除线") }
                    IconButton(onClick = { wrapSelection("<u>", "</u>") }) { Icon(Icons.Filled.FormatUnderlined, "下划线") }
                    Spacer(Modifier.weight(1f))
                    Box {
                        IconButton(onClick = { showMoreFormatting = true }) { Icon(Icons.Filled.MoreVert, "更多格式") }
                        DropdownMenu(expanded = showMoreFormatting, onDismissRequest = { showMoreFormatting = false }) {
                            Row(Modifier.padding(horizontal = 6.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                IconButton(onClick = { prefixCurrentLine("> "); showMoreFormatting = false }) { Icon(Icons.Filled.FormatQuote, "引用") }
                                IconButton(enabled = undoStack.isNotEmpty(), onClick = {
                                    if (undoStack.isNotEmpty()) { val current = text; val previous = undoStack.removeAt(undoStack.lastIndex); redoStack.add(current); text = previous }
                                    showMoreFormatting = false
                                }) { Icon(Icons.Filled.Undo, "撤销") }
                                IconButton(enabled = redoStack.isNotEmpty(), onClick = {
                                    if (redoStack.isNotEmpty()) { val current = text; val next = redoStack.removeAt(redoStack.lastIndex); undoStack.add(current); text = next }
                                    showMoreFormatting = false
                                }) { Icon(Icons.Filled.Redo, "重做") }
                            }
                        }
                    }
                }
                Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Column(Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp)) {
                        OutlinedTextField(
                            text,
                            { handleEditorInput(it) },
                            Modifier.fillMaxWidth().weight(1f).focusRequester(focusRequester),
                            placeholder = { Text("写下此刻的想法…") },
                            maxLines = 30,
                            visualTransformation = MarkdownVisualTransformation(comments),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White),
                        )
                        if (comments.isNotEmpty()) {
                            CommentedNotePreview(
                                "",
                                comments,
                                { index -> comments = comments.mapIndexed { i, comment -> if (i == index) comment.copy(expanded = !comment.expanded) else comment } },
                                showEye = true,
                                onDoubleClickComment = { index ->
                                    val position = comments.getOrNull(index)?.position?.coerceIn(0, text.text.length) ?: text.text.length
                                    text = text.copy(selection = TextRange(position))
                                    focusRequester.requestFocus()
                                    keyboardController?.show()
                                },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                            )
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
        text = { Text("确定删除这条笔记吗？删除后无法恢复。") },
        confirmButton = { TextButton(onClick = { showNoteDeleteDialog = false; onDelete() }) { Text("删除") } },
        dismissButton = { TextButton(onClick = { showNoteDeleteDialog = false }) { Text("取消") } },
    )
    activeComment?.let { index ->
        CommentComposer(
            draft = commentDraft,
            position = comments.getOrNull(index)?.position ?: text.text.length,
            onDraftChange = { value -> commentDraft = value; comments = comments.mapIndexed { i, comment -> if (i == index) comment.copy(text = value, expanded = true) else comment } },
            onDismiss = { activeComment = null },
            onSave = { comments = comments.mapIndexed { i, comment -> if (i == index) comment.copy(text = commentDraft.trim(), expanded = true) else comment }; activeComment = null },
        )
    }
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
                if (categories.isNotEmpty()) {
                    Text("已有标签", color = NoteMuted, style = MaterialTheme.typography.labelMedium)
                    categories.take(8).forEach { category -> TextButton(onClick = { insertTagAtCursor(category.path, text) { text = it }; showTagDialog = false }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Filled.Label, null); Spacer(Modifier.width(6.dp)); Text(category.path) } }
                }
            }
        },
        confirmButton = { TextButton(onClick = { val value = tagInput.trim().trimStart('#'); if (value.isNotBlank() && value.split('/').all { it.isNotBlank() }) { insertTagAtCursor(value, text) { text = it }; showTagDialog = false } }) { Text("新建并插入") } },
        dismissButton = { TextButton(onClick = { showTagDialog = false }) { Text("取消") } },
    )
    previewUri?.let { uri -> ImagePreviewDialog(context, uri, onDismiss = { previewUri = null }) }
}

@Composable
private fun CommentComposer(draft: String, position: Int, onDraftChange: (String) -> Unit, onDismiss: () -> Unit, onSave: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) { focusRequester.requestFocus(); keyboardController?.show() }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.28f)).clickable(onClick = onDismiss), contentAlignment = Alignment.BottomCenter) {
            Surface(color = NotePaper, shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp), modifier = Modifier.fillMaxWidth().fillMaxHeight(0.52f).clickable(onClick = {})) {
                Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("添加评论", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, "关闭评论") }
                    }
                    Text("插入位置：正文第 $position 个字符之后", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 10.dp))
                    OutlinedTextField(draft, onDraftChange, Modifier.fillMaxWidth().weight(1f).focusRequester(focusRequester), placeholder = { Text("写下你的想法…") }, maxLines = 8, colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White))
                    Button(onClick = onSave, enabled = draft.isNotBlank(), modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Icon(Icons.AutoMirrored.Filled.Send, null); Spacer(Modifier.width(6.dp)); Text("保存评论") }
                }
            }
        }
    }
}

private fun insertTagAtCursor(tag: String, value: TextFieldValue, onChange: (TextFieldValue) -> Unit) {
    val insertion = "#${tag.trimStart('#')}"
    val start = value.selection.start.coerceIn(0, value.text.length)
    val next = value.text.substring(0, start) + insertion + value.text.substring(start)
    onChange(TextFieldValue(next, TextRange(start + insertion.length)))
}

@Composable
private fun ToolButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, selected: Boolean, onClick: () -> Unit) { Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 5.dp)) { Icon(icon, label, tint = if (selected) NoteAccent else NotePrimary, modifier = Modifier.size(23.dp)); Text(label, color = NoteMuted, style = MaterialTheme.typography.labelSmall) } }
@Composable
private fun MarkdownToolButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Icon(icon, label, tint = NotePrimary, modifier = Modifier.size(21.dp))
    }
}
private fun moodIcon(mood: String) = when (mood) { "HAPPY" -> Icons.Filled.SentimentVerySatisfied; "SAD" -> Icons.Filled.SentimentDissatisfied; else -> Icons.Filled.SentimentNeutral }
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
private fun WalkPage(state: NotesState, padding: PaddingValues, onEdit: (QuickNote) -> Unit, onDelete: (QuickNote) -> Unit) {
    val notes = remember(state.notes) { state.notes.sortedByDescending { it.createdAt } }
    var index by remember(notes) { mutableStateOf(notes.indices.randomOrNull() ?: 0) }
    val current = notes.getOrNull(index.coerceIn(0, (notes.size - 1).coerceAtLeast(0)))
    var dragDistance by remember { mutableStateOf(0f) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 10.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("漫步", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("从已有笔记中随机开始，左右滑动按创建时间浏览", color = NoteMuted, style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = { index = notes.indices.randomOrNull() ?: 0 }, enabled = notes.isNotEmpty()) { Icon(Icons.Filled.Shuffle, null); Spacer(Modifier.width(4.dp)); Text("重新随机") }
        }
        if (current == null) {
            EmptyHint("还没有可漫步的笔记")
        } else {
            Text("当前范围第 ${index + 1} / ${notes.size} 篇", color = NoteMuted, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 10.dp))
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().heightIn(min = 300.dp, max = 620.dp).padding(vertical = 12.dp).combinedClickable(onClick = {}, onLongClick = { showDeleteDialog = true }).pointerInput(current.id) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, amount -> dragDistance += amount },
                        onDragEnd = { if (abs(dragDistance) > 80f) index = (index + if (dragDistance < 0f) 1 else -1).coerceIn(0, notes.lastIndex); dragDistance = 0f },
                        onDragCancel = { dragDistance = 0f },
                    )
                },
            ) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
                    Text(prettyTime(current.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall)
                    CommentedNotePreview(current.content, current.comments, {}, showEye = false, modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { if (index > 0) index-- }, enabled = index > 0) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null); Spacer(Modifier.width(4.dp)); Text("上一篇") }
                TextButton(onClick = { onEdit(current) }) { Icon(Icons.Filled.Edit, null); Spacer(Modifier.width(4.dp)); Text("编辑") }
                OutlinedButton(onClick = { if (index < notes.lastIndex) index++ }, enabled = index < notes.lastIndex) { Text("下一篇"); Spacer(Modifier.width(4.dp)); Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
            }
            if (index == 0 || index == notes.lastIndex) Text(if (notes.size == 1) "当前只有这一篇" else if (index == 0) "已经到达最早的笔记" else "已经到达最新的笔记", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 6.dp))
        }
    }
    if (showDeleteDialog && current != null) AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("删除笔记") }, text = { Text("确定删除这条笔记吗？") }, confirmButton = { TextButton(onClick = { showDeleteDialog = false; onDelete(current) }) { Text("删除") } }, dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("取消") } })
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

@Composable
private fun MyPage(state: NotesState, padding: PaddingValues, onLetters: () -> Unit, onDayClick: (String) -> Unit, onImport: (ImportBundle) -> Unit) {
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
        Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
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
                            Box(Modifier.fillMaxWidth().height(34.dp).clickable { onDayClick(dayKey) }, contentAlignment = Alignment.Center) {
                                Surface(color = color, shape = CircleShape, modifier = Modifier.size(30.dp)) {
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
                            1 -> MaterialTheme.typography.headlineMedium
                            2 -> MaterialTheme.typography.headlineSmall
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
        val sourceToDisplay = IntArray(source.length + 1)
        val displayToSourceList = mutableListOf<Int>()
        val markerDisplayRanges = mutableListOf<IntRange>()
        val display = StringBuilder()
        for (index in 0..source.length) {
            sourceToDisplay[index] = display.length
            comments.filter { it.position.coerceIn(0, source.length) == index }.forEach {
                val markerStart = display.length
                display.append("💬")
                repeat(display.length - markerStart) { displayToSourceList += index }
                markerDisplayRanges += markerStart until display.length
            }
            sourceToDisplay[index] = display.length
            if (index == source.length) break
            val character = source[index]
            if (!hidden[index]) {
                val atLineStart = index == 0 || source[index - 1] == '\n'
                display.append(if (character == '-' && atLineStart && source.getOrNull(index + 1) == ' ') '•' else character)
                displayToSourceList += index
            }
        }
        val styled = buildAnnotatedString {
            append(display.toString())
            markerDisplayRanges.forEach { range -> addStyle(SpanStyle(color = NoteAccent, fontWeight = FontWeight.Bold), range.first, range.last + 1) }
            fun styleRange(range: IntRange, style: SpanStyle) {
                val start = sourceToDisplay[range.first.coerceIn(0, source.length)]
                val end = sourceToDisplay[(range.last + 1).coerceIn(0, source.length)]
                if (start < end) addStyle(style, start, end)
            }
            Regex("(?m)^(#{1,6})\\s+.+$").findAll(source).forEach { match ->
                val level = match.groupValues[1].length
                styleRange(match.range, SpanStyle(fontWeight = FontWeight.Bold, fontSize = when (level) { 1 -> 24.sp; 2 -> 21.sp; 3 -> 18.sp; else -> 16.sp }, color = NoteInk))
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
            Regex("(?<![\\p{L}\\d_])#[\\p{L}\\d][\\p{L}\\d/_-]*(?=\\s|$)").findAll(source).forEach { match -> styleRange(match.range, SpanStyle(color = NoteTagColor, fontWeight = FontWeight.Medium)) }
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
    val pattern = Regex("""(\*\*|__)(.*?)\1|(?<!\*)\*([^*]*?)\*|~~(.*?)~~|`([^`]*?)`|<u>(.*?)</u>|\[([^]]+)]\([^)]+\)|==([^=]*?)==|(?<![\p{L}\d_])#[\p{L}\d][\p{L}\d/_-]*(?=\s|$)""")
    var cursor = 0
    pattern.findAll(value).forEach { match ->
        append(value.substring(cursor, match.range.first))
        when {
            match.groupValues[1].isNotEmpty() -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(match.groupValues[2]) }
            match.groupValues[3].isNotEmpty() -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(match.groupValues[3]) }
            match.groupValues[4].isNotEmpty() -> withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append(match.groupValues[4]) }
            match.groupValues[5].isNotEmpty() -> withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = NoteSoft)) { append(match.groupValues[5]) }
            match.groupValues[6].isNotEmpty() -> withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append(match.groupValues[6]) }
            match.groupValues[7].isNotEmpty() -> withStyle(SpanStyle(color = NotePrimary, textDecoration = TextDecoration.Underline)) { append(match.groupValues[7]) }
            match.groupValues[8].isNotEmpty() -> withStyle(SpanStyle(background = Color(0xFFFFF1A8))) { append(match.groupValues[8]) }
            match.value.startsWith("#") -> withStyle(SpanStyle(color = NoteTagColor, fontWeight = FontWeight.Medium, fontSize = 12.sp)) { append(match.value) }
        }
        cursor = match.range.last + 1
    }
    append(value.substring(cursor))
}
@Composable
private fun LetterEditor(letter: FutureLetter, onDismiss: () -> Unit, onSave: (FutureLetter) -> Unit) {
    var title by remember { mutableStateOf(letter.title) }
    var text by remember { mutableStateOf(letter.content) }
    var date by remember { mutableStateOf(letter.deliverDate) }
    var dragDistance by remember { mutableStateOf(0f) }
    var startedFromEdge by remember { mutableStateOf(false) }
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
                OutlinedTextField(date, { date = it }, Modifier.fillMaxWidth().padding(top = 10.dp), label = { Text("展示日期 YYYY-MM-DD") })
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
    modifier: Modifier,
    onDraftChange: (String) -> Unit,
    onDeleteChange: (Boolean) -> Unit,
    onPreview: (Uri) -> Unit,
    onSave: () -> Unit,
    onNext: () -> Unit,
) {
    val uri = photos.getOrNull(index)
    if (uri == null) {
        EmptyHint("文件夹中没有可整理的照片")
        return
    }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("照片 ${index + 1} / ${photos.size}", color = NoteMuted, style = MaterialTheme.typography.labelMedium)
        FolderPhotoThumb(context, uri, Modifier.fillMaxWidth().weight(0.9f).combinedClickable(onClick = {}, onLongClick = { onPreview(uri) }))
        OutlinedTextField(draft, onDraftChange, Modifier.fillMaxWidth().weight(1.1f), placeholder = { Text("记录这张图片的关键信息…") }, maxLines = 12)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(deleteAfterSave, onDeleteChange)
            Text("保存笔记后删除原图", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onNext, enabled = index < photos.lastIndex) { Text("下一张") }
            Button(onClick = onSave, enabled = draft.trim().isNotEmpty()) { Icon(Icons.Filled.Save, null); Spacer(Modifier.width(4.dp)); Text("保存并下一张") }
        }
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
