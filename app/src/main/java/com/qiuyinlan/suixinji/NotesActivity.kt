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
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
private val NoteTagRegex = Regex("(?<![\\p{L}\\d_])#([\\p{L}\\d][\\p{L}\\d/_-]*)(?=\\s|$)")
private val TagPalette = listOf(
    0xFFE8D5C4.toInt(), 0xFFD8E2DC.toInt(), 0xFFF6E7CB.toInt(), 0xFFD9E4F5.toInt(),
    0xFFE6D5E8.toInt(), 0xFFF2D0A9.toInt(), 0xFFD5E8D4.toInt(), 0xFFF4CCCC.toInt(),
)
private fun tagColor(category: NoteCategory): Color {
    val stored = category.color.takeIf { it != 0L }?.toInt()
    return Color(stored ?: TagPalette[Math.floorMod(category.id.hashCode(), TagPalette.size)])
}

private data class NoteComment(val position: Int, val text: String, val expanded: Boolean = true)
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
                        NoteComment(comment.optInt("position"), comment.optString("text"), comment.optBoolean("expanded", true))
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
                    put("comments", JSONArray().apply { note.comments.forEach { comment -> put(JSONObject().apply { put("position", comment.position); put("text", comment.text); put("expanded", comment.expanded) }) } })
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
        context.contentResolver.query(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Downloads._ID),
            "${MediaStore.Downloads.DISPLAY_NAME}=?",
            arrayOf(BACKUP_NAME),
            "${MediaStore.Downloads.DATE_MODIFIED} DESC",
        )?.use { cursor -> if (cursor.moveToFirst()) ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cursor.getLong(0)) else null }
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
                put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/随心记")
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
            MaterialTheme(colorScheme = lightColorScheme(primary = NotePrimary, secondary = NoteAccent, background = NotePaper, surface = Color.White, onSurface = NoteInk)) {
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
        drawerContent = { ModalDrawerSheet { DrawerContent(state, page, selectedCategory) { target, category -> page = target; selectedCategory = category; scope.launch { drawerState.close() } } } },
    ) {
        Scaffold(
            containerColor = NotePaper,
            topBar = {
                TopAppBar(
                    title = { Text(when (page) { NotesPage.HOME -> "随心记"; NotesPage.ORGANIZER -> "整理"; NotesPage.TAGS -> "标签"; NotesPage.WALK -> "漫步"; NotesPage.MY -> "我的"; NotesPage.LETTERS -> "信件"; NotesPage.CATEGORY -> selectedCategory?.path ?: "分类" }, fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Filled.Menu, "打开导航") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NotePaper),
                )
            },
            bottomBar = { MainNavigation(page) { target -> page = target; selectedCategory = null; if (target != NotesPage.TAGS) tagCategory = null } },
        ) { padding ->
            when (page) {
                NotesPage.HOME -> HomePage(state, dueLetters, padding, onCapture = { editorNote = it }, onCategory = { selectedCategory = it; page = NotesPage.CATEGORY }, onDiary = { page = NotesPage.WALK }, onLetters = { page = NotesPage.MY }, onOrganize = { page = NotesPage.ORGANIZER }, onEdit = { editorNote = it }, onComment = { commentNote = it }, onUpdate = ::update)
                NotesPage.CATEGORY -> selectedCategory?.let { category -> CategoryPage(state, category, padding, onAdd = { editorNote = QuickNote(UUID.randomUUID().toString(), "", System.currentTimeMillis(), System.currentTimeMillis(), listOf(category.id)) }, onEdit = { editorNote = it }, onCategory = { selectedCategory = it }) }
                NotesPage.ORGANIZER -> OrganizerPage(state, padding, onUpdate = ::update, onEdit = { editorNote = it })
                NotesPage.TAGS -> TagsPage(state, tagCategory, padding, onSelect = { tagCategory = it }, onEdit = { editorNote = it }, onColorChange = { category, color -> update(state.copy(categories = state.categories.map { if (it.id == category.id) it.copy(color = color.toLong()) else it })) })
                NotesPage.WALK -> WalkPage(state, padding, onEdit = { editorNote = it })
                NotesPage.MY -> MyPage(state, padding, onLetters = { page = NotesPage.LETTERS }, onImport = { imported ->
                    var importState = state
                    val importedNotes = imported.map { note ->
                        val categories = importState.categories.toMutableList()
                        val ids = note.categoryIds.toMutableList()
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
                    update(importState.copy(notes = (state.notes + importedNotes).distinctBy { it.id }.sortedByDescending { it.createdAt }))
                })
                NotesPage.LETTERS -> LetterPage(state, dueLetters, padding, onBack = { page = NotesPage.MY }, onEdit = { letterEditor = it }, onCreate = { letterEditor = FutureLetter(UUID.randomUUID().toString(), "", "", today()) }, onUpdate = ::update)
            }
        }
    }
    editorNote?.let { note -> QuickNoteEditor(note, state.categories, onDismiss = { editorNote = null }, onSave = { saveNote(it); editorNote = null }, onDelete = { update(state.copy(notes = state.notes.filterNot { current -> current.id == note.id })); editorNote = null }) }
    commentNote?.let { note -> CommentSheet(note, onDismiss = { commentNote = null }, onSave = { comment -> update(state.copy(notes = state.notes.map { current -> if (current.id == note.id) current.copy(comments = current.comments + comment, updatedAt = System.currentTimeMillis()) else current })); commentNote = null }) }
    letterEditor?.let { letter -> LetterEditor(letter, onDismiss = { letterEditor = null }, onSave = { value -> update(state.copy(letters = listOf(value) + state.letters.filterNot { it.id == value.id })); letterEditor = null }) }
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
private fun DrawerContent(state: NotesState, page: NotesPage, selected: NoteCategory?, onNavigate: (NotesPage, NoteCategory?) -> Unit) {
    Column(Modifier.fillMaxSize().background(NotePaper).padding(vertical = 18.dp)) {
        Text("随心记", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp))
        DrawerItem("首页", Icons.Filled.Home, page == NotesPage.HOME) { onNavigate(NotesPage.HOME, null) }
        DrawerItem("随记箱 · ${state.notes.count { it.status == "inbox" }}", Icons.Filled.Inbox, page == NotesPage.ORGANIZER) { onNavigate(NotesPage.ORGANIZER, null) }
        DrawerItem("整理", Icons.Filled.Tune, page == NotesPage.ORGANIZER) { onNavigate(NotesPage.ORGANIZER, null) }
        DrawerItem("漫步", Icons.Filled.Book, page == NotesPage.WALK) { onNavigate(NotesPage.WALK, null) }
        DrawerItem("我的", Icons.Filled.Person, page == NotesPage.MY || page == NotesPage.LETTERS) { onNavigate(NotesPage.MY, null) }
        Divider(Modifier.padding(vertical = 12.dp))
        Text("分类", style = MaterialTheme.typography.labelLarge, color = NoteMuted, modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp))
        state.categories.filter { it.parentId == null }.forEach { category -> DrawerItem(category.path, Icons.Filled.Label, selected?.id == category.id) { onNavigate(NotesPage.CATEGORY, category) } }
        Spacer(Modifier.weight(1f))
        Text("本地保存 · ${state.notes.size} 条记录", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp))
    }
}

@Composable
private fun DrawerItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).background(if (selected) NoteSoft else Color.Transparent).padding(horizontal = 22.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = if (selected) NotePrimary else NoteMuted, modifier = Modifier.size(21.dp)); Spacer(Modifier.width(14.dp)); Text(label, color = NoteInk, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun HomePage(state: NotesState, due: List<FutureLetter>, padding: PaddingValues, onCapture: (QuickNote) -> Unit, onCategory: (NoteCategory) -> Unit, onOrganize: () -> Unit, onDiary: () -> Unit, onLetters: () -> Unit, onEdit: (QuickNote) -> Unit, onComment: (QuickNote) -> Unit, onUpdate: (NotesState) -> Unit) {
    Column(Modifier.fillMaxSize().padding(padding)) {
        LazyColumn(Modifier.fillMaxWidth().weight(1f), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text("时间线", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("想到什么，先留下来", color = NoteMuted, modifier = Modifier.padding(top = 3.dp))
            }
            item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { SummaryCard("随记箱", state.notes.count { it.status == "inbox" }, Icons.Filled.Inbox, onOrganize); SummaryCard("今日的信", due.size, Icons.Filled.MailOutline, onLetters) } }
            item { Row(verticalAlignment = Alignment.CenterVertically) { Text("全部记录", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); TextButton(onClick = onDiary) { Text("漫步") } } }
            if (state.notes.isEmpty()) item { EmptyHint("还没有记录，点击底部输入框开始") }
            items(state.notes.sortedByDescending { it.createdAt }, key = { it.id }) { note -> NoteTimelineCard(note, state.categories, onEdit, onComment = { onComment(note) }, onToggleComment = { commentIndex -> onUpdate(state.copy(notes = state.notes.map { current -> if (current.id == note.id) current.copy(comments = current.comments.mapIndexed { index, comment -> if (index == commentIndex) comment.copy(expanded = !comment.expanded) else comment }) else current })) }) }
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
private fun NoteTimelineCard(note: QuickNote, categories: List<NoteCategory>, onEdit: (QuickNote) -> Unit, onToggleComment: (Int) -> Unit, onComment: () -> Unit = {}, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable(note.id) { mutableStateOf(false) }
    val longText = note.content.length > 180 || note.content.count { it == '\n' } > 6
    Card(modifier.fillMaxWidth().combinedClickable(onClick = { if (longText) expanded = !expanded }, onDoubleClick = { onEdit(note) }), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp)) {
            CommentedNotePreview(note.content, note.comments, onToggleComment, maxLines = if (longText && !expanded) 7 else Int.MAX_VALUE, showEye = false)
            if (longText) Text(if (expanded) "点击收起 · 双击编辑" else "点击展开 · 双击编辑", color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 10.dp)) { Text(prettyTime(note.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall); note.location?.let { Text(" · $it", color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }; Text(" · ${moodLabel(note.mood)}", color = NoteMuted, style = MaterialTheme.typography.labelSmall); Spacer(Modifier.weight(1f)); IconButton(onClick = onComment, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.ChatBubbleOutline, "添加评论", tint = NoteAccent, modifier = Modifier.size(19.dp)) } }
            val paths = note.categoryIds.mapNotNull { id -> categories.firstOrNull { it.id == id }?.path }
            if (paths.isNotEmpty()) Text(paths.joinToString(" · "), color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun CommentPreview(comments: List<NoteComment>, onToggle: (Int) -> Unit, showEye: Boolean = true, modifier: Modifier = Modifier) {
    Column(modifier) { comments.forEachIndexed { index, comment -> Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth().clickable(enabled = showEye) { onToggle(index) }.padding(vertical = 3.dp)) { if (showEye) Icon(if (comment.expanded) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, "展开或隐藏评论", tint = NoteAccent, modifier = Modifier.size(17.dp)); Icon(Icons.Filled.ChatBubbleOutline, "评论", tint = NoteAccent, modifier = Modifier.size(16.dp).padding(start = 2.dp)); if ((!showEye || comment.expanded) && comment.text.isNotBlank()) Surface(color = NotePaper, shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(start = 7.dp)) { Text(comment.text, color = NoteMuted, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) } } } }
}

@Composable
private fun CommentedNotePreview(content: String, comments: List<NoteComment>, onToggle: (Int) -> Unit, maxLines: Int = Int.MAX_VALUE, showEye: Boolean = true, modifier: Modifier = Modifier) {
    if (comments.isEmpty()) Text(markdownInline(content), modifier = modifier, maxLines = maxLines, overflow = TextOverflow.Ellipsis)
    else {
        val sorted = comments.withIndex().sortedBy { it.value.position.coerceIn(0, content.length) }
        Column(modifier) {
            var cursor = 0
            sorted.forEach { indexed ->
                val position = indexed.value.position.coerceIn(cursor, content.length)
                if (position > cursor) Text(markdownInline(content.substring(cursor, position)), maxLines = maxLines, overflow = TextOverflow.Ellipsis)
                CommentPreview(listOf(indexed.value), { onToggle(indexed.index) }, showEye = showEye, modifier = Modifier.padding(vertical = 2.dp))
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
private fun CategoryPage(state: NotesState, category: NoteCategory, padding: PaddingValues, onAdd: () -> Unit, onEdit: (QuickNote) -> Unit, onCategory: (NoteCategory) -> Unit) {
    val children = state.categories.filter { it.parentId == category.id }
    val notes = state.notes.filter { category.id in it.categoryIds }
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("在 ${category.path} 中记录", color = NoteMuted); OutlinedButton(onClick = onAdd, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(6.dp)); Text("在这里写一条") } }
        if (children.isNotEmpty()) { item { Text("子分类", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }; items(children) { child -> Surface(Modifier.fillMaxWidth().clickable { onCategory(child) }, color = NoteSoft, shape = RoundedCornerShape(10.dp)) { Text(child.name, modifier = Modifier.padding(14.dp), color = NoteInk, fontWeight = FontWeight.Bold) } } }
        item { Text("本分类记录 · ${notes.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        items(notes, key = { it.id }) { note -> NoteTimelineCard(note, state.categories, onEdit, {}) }
    }
}

@Composable
private fun TagsPage(
    state: NotesState,
    selected: NoteCategory?,
    padding: PaddingValues,
    onSelect: (NoteCategory?) -> Unit,
    onEdit: (QuickNote) -> Unit,
    onColorChange: (NoteCategory, Int) -> Unit,
) {
    var colorTarget by remember { mutableStateOf<NoteCategory?>(null) }
    val categories = state.categories
    val visible = if (selected == null) categories.filter { it.parentId == null } else categories.filter { it.parentId == selected.id }
    val notes = selected?.let { category -> state.notes.filter { category.id in it.categoryIds }.sortedByDescending { it.createdAt } }.orEmpty()
    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (selected != null) IconButton(onClick = { onSelect(categories.firstOrNull { it.id == selected.parentId }) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回上一级") }
            Column(Modifier.weight(1f)) {
                Text(selected?.path ?: "标签", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(if (selected == null) "按方块浏览你的分类标签" else "进入下一级标签空间", color = NoteMuted, style = MaterialTheme.typography.bodySmall)
            }
            if (selected != null) IconButton(onClick = { colorTarget = selected }) { Icon(Icons.Filled.Tune, "调整颜色", tint = NotePrimary) }
        }
        if (visible.isNotEmpty()) {
            LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxWidth().height((((visible.size + 1) / 2) * 126).coerceAtLeast(126).dp).padding(top = 12.dp), contentPadding = PaddingValues(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(visible, key = { it.id }) { category ->
                    val childCount = categories.count { it.parentId == category.id }
                    val noteCount = state.notes.count { category.id in it.categoryIds }
                    Surface(onClick = { onSelect(category) }, color = tagColor(category), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().aspectRatio(1.15f)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(category.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NoteInk)
                                Text(category.path, style = MaterialTheme.typography.labelSmall, color = NoteMuted, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
                            }
                            Text("$noteCount 条记录${if (childCount > 0) "  ·  $childCount 个子标签" else ""}", style = MaterialTheme.typography.labelSmall, color = NoteMuted)
                        }
                    }
                }
            }
        }
        if (selected != null) {
            Text("${selected.name} 下的笔记 · ${notes.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp, bottom = 4.dp))
            if (notes.isEmpty()) EmptyHint("这个标签下还没有笔记") else LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
                items(notes, key = { "tag-${it.id}" }) { note -> NoteTimelineCard(note, state.categories, onEdit, {}) }
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
    val noteScroll = rememberScrollState()
    LaunchedEffect(mode, monthFilter, batchCount, inboxIds.joinToString("|")) {
        index = 0
        undoState = null
        lastWasSkip = false
    }
    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                Modifier.fillMaxWidth().weight(1f).verticalScroll(noteScroll).pointerInput(current?.id) {
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
    var showPreview by rememberSaveable { mutableStateOf(true) }
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
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize(), color = NotePaper) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, "关闭") }
                    Text(if (note.content.isBlank()) "记录此刻" else "编辑随记", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { showTimeDialog = true }) { Icon(Icons.Filled.AccessTime, "修改创建时间") }
                    TextButton(onClick = { onSave(note.copy(content = text.text.trim(), createdAt = createdAt, updatedAt = System.currentTimeMillis(), location = location, mood = mood, comments = comments.filter { it.text.isNotBlank() }, imageUris = imageUris)) }, enabled = text.text.trim().isNotEmpty() || imageUris.isNotEmpty()) { Icon(Icons.Filled.Save, null); Text("保存") }
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
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    MarkdownToolButton(Icons.Filled.FormatBold, "粗体") { wrapSelection("**") }
                    MarkdownToolButton(Icons.Filled.FormatItalic, "斜体") { wrapSelection("*") }
                    MarkdownToolButton(Icons.Filled.FormatUnderlined, "下划线") { wrapSelection("<u>", "</u>") }
                    MarkdownToolButton(Icons.Filled.StrikethroughS, "删除线") { wrapSelection("~~") }
                    MarkdownToolButton(Icons.Filled.FormatQuote, "引用") { prefixCurrentLine("> ") }
                    MarkdownToolButton(Icons.Filled.Remove, "分隔线") {
                        val insertion = if (text.text.isBlank()) "---\n" else "\n\n---\n"
                        replaceEditorText(TextFieldValue(text.text + insertion, TextRange(text.text.length + insertion.length)))
                    }
                    IconButton(onClick = {
                        if (undoStack.isNotEmpty()) {
                            val current = text
                            val previous = undoStack.removeAt(undoStack.lastIndex)
                            redoStack.add(current)
                            text = previous
                        }
                    }, enabled = undoStack.isNotEmpty()) { Icon(Icons.Filled.Undo, "撤销") }
                    IconButton(onClick = {
                        if (redoStack.isNotEmpty()) {
                            val current = text
                            val next = redoStack.removeAt(redoStack.lastIndex)
                            undoStack.add(current)
                            text = next
                        }
                    }, enabled = redoStack.isNotEmpty()) { Icon(Icons.Filled.Redo, "重做") }
                    Box {
                        IconButton(onClick = { showMoreFormatting = true }) { Icon(Icons.Filled.MoreVert, "更多格式") }
                        DropdownMenu(expanded = showMoreFormatting, onDismissRequest = { showMoreFormatting = false }) {
                            DropdownMenuItem(text = { Text("行内代码") }, onClick = { wrapSelection("`"); showMoreFormatting = false })
                            DropdownMenuItem(text = { Text("代码块") }, onClick = {
                                prefixCurrentLine("```\n")
                                replaceEditorText(TextFieldValue(text.text + "\n```", TextRange(text.text.length + 4)))
                                showMoreFormatting = false
                            })
                            DropdownMenuItem(text = { Text("无序列表") }, onClick = { prefixCurrentLine("- "); showMoreFormatting = false })
                            DropdownMenuItem(text = { Text("有序列表") }, onClick = { prefixCurrentLine("1. "); showMoreFormatting = false })
                            DropdownMenuItem(text = { Text(if (showPreview) "隐藏实时预览" else "显示实时预览") }, onClick = { showPreview = !showPreview; showMoreFormatting = false })
                        }
                    }
                }
                val inlineTags = NoteTagRegex.findAll(text.text).map { it.groupValues[1] }.distinct().toList()
                if (inlineTags.isNotEmpty()) Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { inlineTags.forEach { Text("#$it", color = NoteTagColor, fontSize = 12.sp, fontWeight = FontWeight.Medium) } }
                location?.let { Text("位置：$it", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 6.dp)) }
                Text("创建时间：${prettyTime(createdAt)}", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 8.dp))
                if (imageUris.isNotEmpty()) {
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        imageUris.forEach { uriString ->
                            FolderPhotoThumb(context, Uri.parse(uriString), Modifier.size(76.dp).combinedClickable(onClick = { previewUri = Uri.parse(uriString) }, onLongClick = { previewUri = Uri.parse(uriString) }))
                        }
                    }
                }
                Column(Modifier.fillMaxWidth().weight(1f)) {
                    OutlinedTextField(text, { replaceEditorText(it) }, Modifier.fillMaxWidth().weight(if (showPreview) 0.58f else 1f).focusRequester(focusRequester), placeholder = { Text("写下此刻的想法…") }, maxLines = 30, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White))
                    if (showPreview) {
                        Text("实时预览", color = NoteMuted, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp, bottom = 3.dp))
                        Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().weight(0.42f)) {
                            MarkdownPreview(text.text, Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp))
                        }
                    }
                }
                if (comments.isNotEmpty()) CommentedNotePreview(text.text, comments, { index -> comments = comments.mapIndexed { i, comment -> if (i == index) comment.copy(expanded = !comment.expanded) else comment } }, showEye = true, modifier = Modifier.fillMaxWidth().heightIn(max = 128.dp).padding(top = 8.dp).verticalScroll(rememberScrollState()))
            }
        }
    }
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

@Composable
private fun WalkPage(state: NotesState, padding: PaddingValues, onEdit: (QuickNote) -> Unit) {
    val notes = remember(state.notes) { state.notes.sortedByDescending { it.createdAt } }
    var index by remember(notes) { mutableStateOf(notes.indices.randomOrNull() ?: 0) }
    val current = notes.getOrNull(index.coerceIn(0, (notes.size - 1).coerceAtLeast(0)))
    var dragDistance by remember { mutableStateOf(0f) }
    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 10.dp)) {
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
                modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 12.dp).pointerInput(current.id) {
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
private fun MyPage(state: NotesState, padding: PaddingValues, onLetters: () -> Unit, onImport: (List<QuickNote>) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showTransfer by remember { mutableStateOf(false) }
    var showExport by remember { mutableStateOf(false) }
    var pendingFormat by remember { mutableStateOf<String?>(null) }
    var result by remember { mutableStateOf<String?>(null) }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        val format = pendingFormat
        if (uri != null && format != null) result = if (exportNotesToZip(context, state, uri, format)) "导出完成" else "导出失败，请重试"
        pendingFormat = null
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val imported = parseFlomoZip(context, uri)
            onImport(imported)
            result = if (imported.isEmpty()) "没有解析到可导入的 Flomo 笔记" else "已导入 ${imported.size} 条笔记"
        }
    }
    val days = remember { (0 until 90).map { offset -> Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -(89 - offset)) }.time } }
    val dayFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }
    val dayCounts = remember(state.notes) { state.notes.groupingBy { dayFormat.format(Date(it.createdAt)) }.eachCount() }
    Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("我的", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("本地保存 · ${state.notes.size} 条记录", color = NoteMuted, modifier = Modifier.padding(top = 3.dp))
        Text("近三个月记录热力", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 22.dp))
        Text("每天一个圆圈，颜色越深表示当天记录越多", color = NoteMuted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 3.dp, bottom = 12.dp))
        LazyVerticalGrid(GridCells.Fixed(15), Modifier.fillMaxWidth().height(212.dp), horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            items(days, key = { dayFormat.format(it) }) { day ->
                val count = dayCounts[dayFormat.format(day)] ?: 0
                val color = when { count >= 5 -> NotePrimary; count >= 3 -> NoteAccent; count == 2 -> NoteSoft; count == 1 -> NotePaper; else -> Color(0xFFF7F4F0) }
                Surface(color = color, shape = CircleShape, modifier = Modifier.size(15.dp)) {}
            }
        }
        Button(onClick = onLetters, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) { Icon(Icons.Filled.MailOutline, null); Spacer(Modifier.width(7.dp)); Text("我的信件") }
        OutlinedButton(onClick = { showTransfer = true }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Icon(Icons.Filled.Save, null); Spacer(Modifier.width(7.dp)); Text("导入 / 导出") }
        result?.let { Text(it, color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 12.dp)) }
    }
    if (showTransfer) AlertDialog(onDismissRequest = { showTransfer = false }, title = { Text("导入 / 导出") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            TextButton(onClick = { showTransfer = false; importLauncher.launch(arrayOf("application/zip", "application/x-zip-compressed")) }, modifier = Modifier.fillMaxWidth()) { Text("导入 Flomo ZIP 压缩包") }
            TextButton(onClick = { showTransfer = false; showExport = true }, modifier = Modifier.fillMaxWidth()) { Text("导出记录 ZIP") }
        }
    }, confirmButton = { TextButton(onClick = { showTransfer = false }) { Text("取消") } })
    if (showExport) AlertDialog(onDismissRequest = { showExport = false }, title = { Text("选择导出格式") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            TextButton(onClick = { pendingFormat = "md"; showExport = false; exportLauncher.launch("suixinji-${today()}.zip") }, modifier = Modifier.fillMaxWidth()) { Text("Markdown（.md）") }
            TextButton(onClick = { pendingFormat = "txt"; showExport = false; exportLauncher.launch("suixinji-${today()}.zip") }, modifier = Modifier.fillMaxWidth()) { Text("纯文本（.txt）") }
        }
    }, confirmButton = { TextButton(onClick = { showExport = false }) { Text("取消") } })
}

private fun parseFlomoZip(context: Context, uri: Uri): List<QuickNote> = runCatching {
    val html = StringBuilder()
    context.contentResolver.openInputStream(uri)?.use { input ->
        ZipInputStream(BufferedInputStream(input)).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                if (!entry.isDirectory && entry.name.lowercase(Locale.ROOT).endsWith(".html")) {
                    val bytes = ByteArrayOutputStream()
                    zip.copyTo(bytes)
                    html.append(bytes.toString(Charsets.UTF_8.name()))
                }
                zip.closeEntry()
            }
        }
    }
    val memoRegex = Regex("(?s)<div\\s+class=\\\"memo\\\".*?<div\\s+class=\\\"files\\\">(.*?)</div>")
    val timeRegex = Regex("(?s)<div\\s+class=\\\"time\\\">(.*?)</div>")
    val contentRegex = Regex("(?s)<div\\s+class=\\\"content\\\">(.*?)</div>\\s*<div\\s+class=\\\"files\\\">")
    val dateParser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).apply { isLenient = false }
    memoRegex.findAll(html).mapNotNull { memo ->
        val block = memo.value
        val timeText = timeRegex.find(block)?.groupValues?.getOrNull(1)?.trim() ?: return@mapNotNull null
        val contentHtml = contentRegex.find(block)?.groupValues?.getOrNull(1) ?: return@mapNotNull null
        val content = Html.fromHtml(contentHtml, Html.FROM_HTML_MODE_LEGACY).toString().replace("\u00a0", " ").trim()
        if (content.isBlank()) return@mapNotNull null
        val created = runCatching { dateParser.parse(timeText)?.time }.getOrNull() ?: return@mapNotNull null
        QuickNote(UUID.randomUUID().toString(), content, created, created, emptyList())
    }.toList()
}.getOrDefault(emptyList())

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
    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).pointerInput(Unit) {
        detectHorizontalDragGestures(onHorizontalDrag = { _, amount -> dragDistance += amount }, onDragEnd = { if (dragDistance > 80f) onBack(); dragDistance = 0f }, onDragCancel = { dragDistance = 0f })
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

private fun markdownInline(value: String) = buildAnnotatedString {
    val pattern = Regex("""(\*\*|__)(.+?)\1|(?<!\*)\*([^*]+)\*|~~(.+?)~~|`([^`]+)`|<u>(.+?)</u>|\[([^]]+)]\([^)]+\)|(?<![\p{L}\d_])#[\p{L}\d][\p{L}\d/_-]*(?=\s|$)""")
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
            match.value.startsWith("#") -> withStyle(SpanStyle(color = NoteTagColor, fontWeight = FontWeight.Medium, fontSize = 12.sp)) { append(match.value) }
        }
        cursor = match.range.last + 1
    }
    append(value.substring(cursor))
}
@Composable
private fun LetterEditor(letter: FutureLetter, onDismiss: () -> Unit, onSave: (FutureLetter) -> Unit) { var title by remember { mutableStateOf(letter.title) }; var text by remember { mutableStateOf(letter.content) }; var date by remember { mutableStateOf(letter.deliverDate) }; Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) { Surface(Modifier.fillMaxSize(), color = NotePaper) { Column(Modifier.padding(18.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text("写给未来的自己", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); TextButton(onClick = { if (date.isNotBlank()) onSave(letter.copy(title = title, content = text, deliverDate = date)) }) { Text("保存") } }; OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("标题（可选）") }); OutlinedTextField(date, { date = it }, Modifier.fillMaxWidth().padding(top = 10.dp), label = { Text("展示日期 YYYY-MM-DD") }); Spacer(Modifier.height(10.dp)); OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().weight(1f), label = { Text("正文") }, maxLines = 30) } } } }

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
