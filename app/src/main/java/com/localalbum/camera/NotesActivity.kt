package com.qiuyinlan.suixinji

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val NoteInk = Color(0xFF4B3B30)
private val NotePrimary = Color(0xFF8B6A52)
private val NoteAccent = Color(0xFFC68E6B)
private val NotePaper = Color(0xFFF4E8DA)
private val NoteSoft = Color(0xFFEAD9C7)
private val NoteMuted = Color(0xFF8A786B)
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

    fun load(context: Context): NotesState = runCatching {
        val root = JSONObject(context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, "{}") ?: "{}")
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
                )
            },
            categories = root.optJSONArray("categories").toList { item -> NoteCategory(item.optString("id"), item.optString("name"), item.optNullableString("parentId"), item.optString("path"), item.optLong("color", 0L)) },
            diaries = root.optJSONArray("diaries").toList { item -> Diary(item.optString("id"), item.optString("title"), item.optString("markdown"), item.optString("date")) },
            letters = root.optJSONArray("letters").toList { item -> FutureLetter(item.optString("id"), item.optString("title"), item.optString("content"), item.optString("deliverDate"), item.optBoolean("shown")) },
        )
    }.getOrDefault(NotesState())

    fun save(context: Context, state: NotesState) {
        val root = JSONObject().apply {
            put("notes", JSONArray().apply {
                state.notes.forEach { note -> put(JSONObject().apply {
                    put("id", note.id); put("content", note.content); put("createdAt", note.createdAt); put("updatedAt", note.updatedAt)
                    put("categoryIds", JSONArray(note.categoryIds)); put("status", note.status); put("location", note.location); put("mood", note.mood)
                    put("comments", JSONArray().apply { note.comments.forEach { comment -> put(JSONObject().apply { put("position", comment.position); put("text", comment.text); put("expanded", comment.expanded) }) } })
                }) }
            })
            put("categories", JSONArray().apply { state.categories.forEach { put(JSONObject().apply { put("id", it.id); put("name", it.name); put("parentId", it.parentId); put("path", it.path); put("color", it.color) }) } })
            put("diaries", JSONArray().apply { state.diaries.forEach { put(JSONObject().apply { put("id", it.id); put("title", it.title); put("markdown", it.markdown); put("date", it.date) }) } })
            put("letters", JSONArray().apply { state.letters.forEach { put(JSONObject().apply { put("id", it.id); put("title", it.title); put("content", it.content); put("deliverDate", it.deliverDate); put("shown", it.shown) }) } })
        }
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY, root.toString()).apply()
    }
}

private inline fun <T> JSONArray?.toList(map: (JSONObject) -> T): List<T> = if (this == null) emptyList() else (0 until length()).mapNotNull { optJSONObject(it)?.let(map) }
private fun JSONArray?.toStringList(): List<String> = if (this == null) emptyList() else (0 until length()).map { optString(it) }
private fun JSONObject.optNullableString(key: String): String? = if (isNull(key)) null else optString(key).takeIf { it.isNotBlank() }
private fun prettyTime(value: Long): String = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA).format(Date(value))
private fun editTime(value: Long): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(Date(value))
private fun parseEditTime(value: String): Long? = runCatching { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).apply { isLenient = false }.parse(value)?.time }.getOrNull()
private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())

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

private enum class NotesPage { HOME, ORGANIZER, TAGS, DIARIES, LETTERS, CATEGORY }

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
    var diaryEditor by remember { mutableStateOf<Diary?>(null) }
    var diaryWalk by remember { mutableStateOf<List<Diary>?>(null) }
    var letterEditor by remember { mutableStateOf<FutureLetter?>(null) }

    fun update(next: NotesState) { state = next; NotesStore.save(context, next) }
    fun noteWithTags(note: QuickNote): QuickNote {
        val categories = state.categories.toMutableList()
        val ids = note.categoryIds.toMutableList()
        Regex("(?<!\\w)#([\\p{L}\\d][\\p{L}\\d/_-]*)").findAll(note.content).forEach { match ->
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
                    title = { Text(when (page) { NotesPage.HOME -> "随心记"; NotesPage.ORGANIZER -> "整理"; NotesPage.TAGS -> "标签"; NotesPage.DIARIES -> "日记"; NotesPage.LETTERS -> "未来的信"; NotesPage.CATEGORY -> selectedCategory?.path ?: "分类" }, fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Filled.Menu, "打开导航") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NotePaper),
                )
            },
            bottomBar = { MainNavigation(page) { target -> page = target; selectedCategory = null; if (target != NotesPage.TAGS) tagCategory = null } },
        ) { padding ->
            when (page) {
                NotesPage.HOME -> HomePage(state, dueLetters, padding, onCapture = { editorNote = it }, onCategory = { selectedCategory = it; page = NotesPage.CATEGORY }, onDiary = { page = NotesPage.DIARIES }, onLetters = { page = NotesPage.LETTERS }, onOrganize = { page = NotesPage.ORGANIZER }, onEdit = { editorNote = it }, onComment = { commentNote = it }, onUpdate = ::update)
                NotesPage.CATEGORY -> selectedCategory?.let { category -> CategoryPage(state, category, padding, onAdd = { editorNote = QuickNote(UUID.randomUUID().toString(), "", System.currentTimeMillis(), System.currentTimeMillis(), listOf(category.id)) }, onEdit = { editorNote = it }, onCategory = { selectedCategory = it }) }
                NotesPage.ORGANIZER -> OrganizerPage(state, padding, onUpdate = ::update, onEdit = { editorNote = it })
                NotesPage.TAGS -> TagsPage(state, tagCategory, padding, onSelect = { tagCategory = it }, onColorChange = { category, color -> update(state.copy(categories = state.categories.map { if (it.id == category.id) it.copy(color = color.toLong()) else it })) })
                NotesPage.DIARIES -> DiaryPage(state, padding, onEdit = { diaryEditor = it }, onCreate = { diaryEditor = Diary(UUID.randomUUID().toString(), "", "", today()) }, onRandom = { diaryWalk = it })
                NotesPage.LETTERS -> LetterPage(state, dueLetters, padding, onEdit = { letterEditor = it }, onCreate = { letterEditor = FutureLetter(UUID.randomUUID().toString(), "", "", today()) }, onUpdate = ::update)
            }
        }
    }
    editorNote?.let { note -> QuickNoteEditor(note, state.categories, onDismiss = { editorNote = null }, onSave = { saveNote(it); editorNote = null }, onDelete = { update(state.copy(notes = state.notes.filterNot { current -> current.id == note.id })); editorNote = null }) }
    commentNote?.let { note -> CommentSheet(note, onDismiss = { commentNote = null }, onSave = { comment -> update(state.copy(notes = state.notes.map { current -> if (current.id == note.id) current.copy(comments = current.comments + comment, updatedAt = System.currentTimeMillis()) else current })); commentNote = null }) }
    diaryEditor?.let { diary -> DiaryEditor(diary, onDismiss = { diaryEditor = null }, onSave = { value -> update(state.copy(diaries = listOf(value) + state.diaries.filterNot { it.id == value.id })); diaryEditor = null }) }
    diaryWalk?.let { diaries -> DiaryWalkDialog(diaries, onDismiss = { diaryWalk = null }, onEdit = { diaryWalk = null; diaryEditor = it }) }
    letterEditor?.let { letter -> LetterEditor(letter, onDismiss = { letterEditor = null }, onSave = { value -> update(state.copy(letters = listOf(value) + state.letters.filterNot { it.id == value.id })); letterEditor = null }) }
}

@Composable
private fun MainNavigation(page: NotesPage, onSelect: (NotesPage) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(page == NotesPage.HOME, { onSelect(NotesPage.HOME) }, icon = { Icon(Icons.Filled.Home, "首页") }, label = { Text("首页") })
        NavigationBarItem(page == NotesPage.ORGANIZER, { onSelect(NotesPage.ORGANIZER) }, icon = { Icon(Icons.Filled.Tune, "整理") }, label = { Text("整理") })
        NavigationBarItem(page == NotesPage.TAGS, { onSelect(NotesPage.TAGS) }, icon = { Icon(Icons.Filled.Label, "标签") }, label = { Text("标签") })
        NavigationBarItem(page == NotesPage.LETTERS, { onSelect(NotesPage.LETTERS) }, icon = { Icon(Icons.Filled.MailOutline, "未来的信") }, label = { Text("未来的信") })
    }
}

@Composable
private fun DrawerContent(state: NotesState, page: NotesPage, selected: NoteCategory?, onNavigate: (NotesPage, NoteCategory?) -> Unit) {
    Column(Modifier.fillMaxSize().background(NotePaper).padding(vertical = 18.dp)) {
        Text("随心记", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp))
        DrawerItem("首页", Icons.Filled.Home, page == NotesPage.HOME) { onNavigate(NotesPage.HOME, null) }
        DrawerItem("随记箱 · ${state.notes.count { it.status == "inbox" }}", Icons.Filled.Inbox, page == NotesPage.ORGANIZER) { onNavigate(NotesPage.ORGANIZER, null) }
        DrawerItem("整理", Icons.Filled.Tune, page == NotesPage.ORGANIZER) { onNavigate(NotesPage.ORGANIZER, null) }
        DrawerItem("日记", Icons.Filled.Book, page == NotesPage.DIARIES) { onNavigate(NotesPage.DIARIES, null) }
        DrawerItem("未来的信", Icons.Filled.MailOutline, page == NotesPage.LETTERS) { onNavigate(NotesPage.LETTERS, null) }
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
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("随心记", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("想到什么，先留下来", color = NoteMuted, modifier = Modifier.padding(top = 3.dp, bottom = 10.dp))
            Box(Modifier.fillMaxWidth().height(112.dp).clickable { val now = System.currentTimeMillis(); onCapture(QuickNote(UUID.randomUUID().toString(), "", now, now, emptyList())) }) {
                OutlinedTextField(value = "", onValueChange = {}, enabled = false, modifier = Modifier.fillMaxSize(), placeholder = { Text("点击开始记录…") }, shape = RoundedCornerShape(16.dp), trailingIcon = { Icon(Icons.AutoMirrored.Filled.Send, "开始记录", tint = NotePrimary) })
            }
        }
        item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { SummaryCard("随记箱", state.notes.count { it.status == "inbox" }, Icons.Filled.Inbox, onOrganize); SummaryCard("今日的信", due.size, Icons.Filled.MailOutline, onLetters) } }
        item { Row(verticalAlignment = Alignment.CenterVertically) { Text("时间线", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); TextButton(onClick = onDiary) { Text("日记") } } }
        if (state.notes.isEmpty()) item { EmptyHint("还没有记录，点击上面的输入框开始") }
        items(state.notes.sortedByDescending { it.createdAt }, key = { it.id }) { note -> NoteTimelineCard(note, state.categories, onEdit, onComment = { onComment(note) }, onToggleComment = { commentIndex -> onUpdate(state.copy(notes = state.notes.map { current -> if (current.id == note.id) current.copy(comments = current.comments.mapIndexed { index, comment -> if (index == commentIndex) comment.copy(expanded = !comment.expanded) else comment }) else current })) }) }
        if (state.categories.isNotEmpty()) item { Text("分类空间", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        if (state.categories.isNotEmpty()) item { LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxWidth().height(((state.categories.count { it.parentId == null } + 1) / 2 * 88).coerceAtLeast(88).dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(state.categories.filter { it.parentId == null }) { category -> Card(Modifier.fillMaxWidth().height(78.dp).clickable { onCategory(category) }, colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(14.dp)) { Text(category.name, fontWeight = FontWeight.Bold); Text("${state.notes.count { category.id in it.categoryIds }} 条记录", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 7.dp)) } } } } }
    }
}

@Composable
private fun SummaryCard(label: String, count: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth(0.48f).height(82.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Color.White)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = NotePrimary, modifier = Modifier.size(24.dp)); Spacer(Modifier.width(10.dp)); Column { Text(label, color = NoteMuted, style = MaterialTheme.typography.labelMedium); Text(count.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) } } }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteTimelineCard(note: QuickNote, categories: List<NoteCategory>, onEdit: (QuickNote) -> Unit, onToggleComment: (Int) -> Unit, onComment: () -> Unit = {}) {
    var expanded by rememberSaveable(note.id) { mutableStateOf(false) }
    val longText = note.content.length > 180 || note.content.count { it == '\n' } > 6
    Card(Modifier.fillMaxWidth().combinedClickable(onClick = { if (longText) expanded = !expanded }, onDoubleClick = { onEdit(note) }), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp)) {
            Text(note.content, maxLines = if (longText && !expanded) 7 else Int.MAX_VALUE, overflow = TextOverflow.Ellipsis)
            if (longText) Text(if (expanded) "点击收起 · 双击编辑" else "点击展开 · 双击编辑", color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 10.dp)) { Text(prettyTime(note.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall); note.location?.let { Text(" · $it", color = NoteMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }; Text(" · ${moodLabel(note.mood)}", color = NoteMuted, style = MaterialTheme.typography.labelSmall); Spacer(Modifier.weight(1f)); IconButton(onClick = onComment, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.ChatBubbleOutline, "添加评论", tint = NoteAccent, modifier = Modifier.size(19.dp)) } }
            val paths = note.categoryIds.mapNotNull { id -> categories.firstOrNull { it.id == id }?.path }
            if (paths.isNotEmpty()) Text(paths.joinToString(" · "), color = NotePrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
            if (note.comments.isNotEmpty()) CommentPreview(note.comments, onToggleComment, Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
private fun CommentPreview(comments: List<NoteComment>, onToggle: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) { comments.forEachIndexed { index, comment -> Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth().clickable { onToggle(index) }.padding(vertical = 3.dp)) { Icon(Icons.Filled.ChatBubbleOutline, "评论", tint = NoteAccent, modifier = Modifier.size(16.dp)); if (comment.expanded && comment.text.isNotBlank()) Text(comment.text, color = NoteMuted, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp, modifier = Modifier.padding(start = 7.dp)) } } }
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
    onColorChange: (NoteCategory, Int) -> Unit,
) {
    var colorTarget by remember { mutableStateOf<NoteCategory?>(null) }
    val categories = state.categories
    val visible = if (selected == null) categories.filter { it.parentId == null } else categories.filter { it.parentId == selected.id }
    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (selected != null) IconButton(onClick = { onSelect(categories.firstOrNull { it.id == selected.parentId }) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回上一级") }
            Column(Modifier.weight(1f)) {
                Text(selected?.path ?: "标签", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(if (selected == null) "按方块浏览你的分类标签" else "进入下一级标签空间", color = NoteMuted, style = MaterialTheme.typography.bodySmall)
            }
            if (selected != null) IconButton(onClick = { colorTarget = selected }) { Icon(Icons.Filled.Tune, "调整颜色", tint = NotePrimary) }
        }
        if (visible.isEmpty()) {
            EmptyHint(if (selected == null) "还没有标签，记录中添加 #标签 后会自动创建" else "这个标签下还没有二级标签")
        } else {
            LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxSize().padding(top = 12.dp), contentPadding = PaddingValues(bottom = 18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            photoUris = queryFolderImages(context, uri)
        }
    }
    val inbox = state.notes.filter { it.status == "inbox" }
    val current = inbox.getOrNull(index.coerceAtMost((inbox.size - 1).coerceAtLeast(0)))
    val noteScroll = rememberScrollState()
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
            else LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxWidth().weight(1f), contentPadding = PaddingValues(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(photoUris, key = { it.toString() }) { uri -> FolderPhotoThumb(context, uri) }
            }
        } else {
            Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(noteScroll), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (current == null) EmptyHint("随记箱为空，所有记录都整理好了") else {
                    Text("${index + 1} / ${inbox.size}", color = NoteMuted, style = MaterialTheme.typography.labelLarge)
                    NoteTimelineCard(current, state.categories, onEdit, {})
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { index++ }) { Icon(Icons.Filled.SkipNext, "跳过") }
                        IconButton(onClick = { selected = current }) { Icon(Icons.Filled.Label, "归类") }
                        IconButton(onClick = { onUpdate(state.copy(notes = state.notes.filterNot { it.id == current.id })); index = index.coerceAtMost(inbox.size - 2) }) { Icon(Icons.Filled.DeleteOutline, "删除") }
                    }
                }
                Text("跳过的记录仍会留在随记箱", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
    selected?.let { note -> CategoryPicker(state.categories, onDismiss = { selected = null }, onPick = { category -> onUpdate(state.copy(notes = state.notes.map { if (it.id == note.id) it.copy(categoryIds = listOf(category.id), status = "organized", updatedAt = System.currentTimeMillis()) else it })); selected = null; index++ }) }
}

@Composable
private fun CategoryPicker(categories: List<NoteCategory>, onDismiss: () -> Unit, onPick: (NoteCategory) -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("选择分类") }, text = { if (categories.isEmpty()) Text("还没有分类，请先记录一条带 #分类 的内容") else Column { categories.forEach { category -> TextButton(onClick = { onPick(category) }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Filled.Folder, null); Spacer(Modifier.width(8.dp)); Text(category.path) } } } }, confirmButton = { TextButton(onClick = onDismiss) { Text("取消") } })
}

@Composable
private fun QuickNoteEditor(note: QuickNote, categories: List<NoteCategory>, onDismiss: () -> Unit, onSave: (QuickNote) -> Unit, onDelete: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf(TextFieldValue(note.content)) }
    var location by remember { mutableStateOf(note.location) }
    var mood by remember { mutableStateOf(note.mood) }
    var comments by remember { mutableStateOf(note.comments) }
    var commentDraft by remember { mutableStateOf("") }
    var activeComment by remember { mutableStateOf<Int?>(null) }
    var createdAt by remember { mutableStateOf(note.createdAt) }
    var showTimeDialog by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    val locationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true || result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) scope.launch(Dispatchers.IO) { val label = getLastLocationLabel(context) ?: "位置已授权"; withContext(Dispatchers.Main) { location = label } }
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize(), color = NotePaper) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, "关闭") }
                    Text(if (note.content.isBlank()) "记录此刻" else "编辑随记", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { showTimeDialog = true }) { Icon(Icons.Filled.AccessTime, "修改创建时间") }
                    TextButton(onClick = { onSave(note.copy(content = text.text.trim(), createdAt = createdAt, updatedAt = System.currentTimeMillis(), location = location, mood = mood, comments = comments.filter { it.text.isNotBlank() })) }, enabled = text.text.trim().isNotEmpty()) { Icon(Icons.Filled.Save, null); Text("保存") }
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ToolButton(Icons.Filled.LocationOn, "位置", location != null) { if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) scope.launch(Dispatchers.IO) { val label = getLastLocationLabel(context) ?: "位置已授权"; withContext(Dispatchers.Main) { location = label } } else locationLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) }
                    ToolButton(Icons.Filled.Label, "标签", false) { showTagDialog = true; tagInput = "" }
                    ToolButton(Icons.Filled.CalendarMonth, "日期", false) { showTimeDialog = true }
                    ToolButton(moodIcon(mood), moodLabel(mood), mood == "HAPPY") { mood = when (mood) { "HAPPY" -> "NORMAL"; "NORMAL" -> "SAD"; else -> "HAPPY" } }
                    ToolButton(Icons.Filled.ChatBubbleOutline, "评论", activeComment != null) { comments = comments + NoteComment(text.selection.start, "", true); activeComment = comments.lastIndex; commentDraft = "" }
                }
                location?.let { Text("位置：$it", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 6.dp)) }
                Text("创建时间：${prettyTime(createdAt)}", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().weight(1f).focusRequester(focusRequester), placeholder = { Text("写下此刻的想法…") }, maxLines = 30, shape = RoundedCornerShape(14.dp))
                activeComment?.let { index ->
                    OutlinedTextField(commentDraft, { commentDraft = it; comments = comments.mapIndexed { i, comment -> if (i == index) comment.copy(text = it, expanded = true) else comment } }, Modifier.fillMaxWidth().padding(top = 10.dp), label = { Text("评论 · 文字位置 ${comments.getOrNull(index)?.position ?: text.text.length}") }, trailingIcon = { IconButton(onClick = { activeComment = null }) { Icon(Icons.Filled.Close, "隐藏评论编辑") } })
                }
            }
        }
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
}

private fun insertTagAtCursor(tag: String, value: TextFieldValue, onChange: (TextFieldValue) -> Unit) {
    val insertion = "#${tag.trimStart('#')}"
    val start = value.selection.start.coerceIn(0, value.text.length)
    val next = value.text.substring(0, start) + insertion + value.text.substring(start)
    onChange(TextFieldValue(next, TextRange(start + insertion.length)))
}

@Composable
private fun ToolButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, selected: Boolean, onClick: () -> Unit) { Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 5.dp)) { Icon(icon, label, tint = if (selected) NoteAccent else NotePrimary, modifier = Modifier.size(23.dp)); Text(label, color = NoteMuted, style = MaterialTheme.typography.labelSmall) } }
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
private fun DiaryPage(state: NotesState, padding: PaddingValues, onEdit: (Diary) -> Unit, onCreate: () -> Unit, onRandom: (List<Diary>) -> Unit) {
    var monthFilter by rememberSaveable { mutableStateOf("全部月份") }
    var categoryFilter by rememberSaveable { mutableStateOf("全部分类") }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    val months = state.diaries.map { it.date.take(7) }.filter { it.length == 7 }.distinct().sortedDescending()
    val categories = state.diaries.flatMap { diaryTags(it) }.distinct().sorted()
    val filtered = state.diaries.sortedByDescending { it.date }.filter { diary ->
        (monthFilter == "全部月份" || diary.date.startsWith(monthFilter)) && (categoryFilter == "全部分类" || categoryFilter in diaryTags(diary))
    }
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("日记列表", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("按日期浏览，进入后可以继续编辑", color = NoteMuted) }
                IconButton(onClick = { onRandom(filtered) }, enabled = filtered.isNotEmpty()) { Icon(Icons.Filled.Shuffle, "随机漫步") }
                Button(onClick = onCreate) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(4.dp)); Text("新建") }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showMonthPicker = true }) { Icon(Icons.Filled.CalendarMonth, null); Spacer(Modifier.width(4.dp)); Text(monthFilter) }
                OutlinedButton(onClick = { showCategoryPicker = true }) { Icon(Icons.Filled.Label, null); Spacer(Modifier.width(4.dp)); Text(categoryFilter) }
            }
        }
        item { OutlinedButton(onClick = { onRandom(filtered) }, enabled = filtered.isNotEmpty(), modifier = Modifier.fillMaxWidth()) { Icon(Icons.Filled.Shuffle, null); Spacer(Modifier.width(6.dp)); Text("开始随机漫步" + if (filtered.isNotEmpty()) "（${filtered.size} 篇）" else "") } }
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
        items(state.notes.filter { it.comments.isNotEmpty() }, key = { "comments-${it.id}" }) { note -> Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(14.dp)) { Text(note.content, maxLines = 2, overflow = TextOverflow.Ellipsis); CommentPreview(note.comments, {}, Modifier.padding(top = 6.dp)) } } }
    }
    if (showMonthPicker) AlertDialog(onDismissRequest = { showMonthPicker = false }, title = { Text("按月份筛选") }, text = { Column { TextButton(onClick = { monthFilter = "全部月份"; showMonthPicker = false }, modifier = Modifier.fillMaxWidth()) { Text("全部月份") }; months.forEach { month -> TextButton(onClick = { monthFilter = month; showMonthPicker = false }, modifier = Modifier.fillMaxWidth()) { Text(month) } } } }, confirmButton = { TextButton(onClick = { showMonthPicker = false }) { Text("取消") } })
    if (showCategoryPicker) AlertDialog(onDismissRequest = { showCategoryPicker = false }, title = { Text("按分类筛选") }, text = { Column { TextButton(onClick = { categoryFilter = "全部分类"; showCategoryPicker = false }, modifier = Modifier.fillMaxWidth()) { Text("全部分类") }; categories.forEach { category -> TextButton(onClick = { categoryFilter = category; showCategoryPicker = false }, modifier = Modifier.fillMaxWidth()) { Text("#$category") } } } }, confirmButton = { TextButton(onClick = { showCategoryPicker = false }) { Text("取消") } })
}

private fun diaryTags(diary: Diary): List<String> = Regex("(?<!\\w)#([\\p{L}\\d/_-]+)").findAll(diary.markdown).map { it.groupValues[1] }.distinct().toList()

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
                    Text(current.markdown, modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 20.dp), style = MaterialTheme.typography.bodyLarge)
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
private fun LetterPage(state: NotesState, due: List<FutureLetter>, padding: PaddingValues, onEdit: (FutureLetter) -> Unit, onCreate: () -> Unit, onUpdate: (NotesState) -> Unit) {
    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Column { Text("未来的信", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("到日期后打开 App 查看", color = NoteMuted) }; Spacer(Modifier.weight(1f)); Button(onClick = onCreate) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(4.dp)); Text("写信") } }; if (due.isNotEmpty()) { Text("今天给你的信", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp)); due.forEach { letter -> Card(Modifier.fillMaxWidth().padding(top = 10.dp).clickable { onUpdate(state.copy(letters = state.letters.map { if (it.id == letter.id) it.copy(shown = true) else it })) }, colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(16.dp)) { Text(letter.title.ifBlank { "给未来的自己" }, fontWeight = FontWeight.Bold); Text(letter.content, Modifier.padding(top = 8.dp), maxLines = 4, overflow = TextOverflow.Ellipsis) } } } }; Text("全部信件", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp)); LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 12.dp)) { items(state.letters, key = { it.id }) { letter -> Card(Modifier.fillMaxWidth().clickable { onEdit(letter) }, colors = CardDefaults.cardColors(containerColor = Color.White)) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.MailOutline, null, tint = NotePrimary); Spacer(Modifier.width(10.dp)); Column { Text(letter.title.ifBlank { "给未来的自己" }, fontWeight = FontWeight.Bold); Text("${letter.deliverDate}${if (letter.shown) " · 已展示" else ""}", color = NoteMuted, style = MaterialTheme.typography.labelSmall) } } } } } }
}

@Composable
private fun QuickNoteEditorPreview() = Unit

@Composable
private fun DiaryEditor(diary: Diary, onDismiss: () -> Unit, onSave: (Diary) -> Unit) { var title by remember { mutableStateOf(diary.title) }; var text by remember { mutableStateOf(diary.markdown) }; Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) { Surface(Modifier.fillMaxSize(), color = NotePaper) { Column(Modifier.padding(18.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text("编辑日记", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); TextButton(onClick = { onSave(diary.copy(title = title, markdown = text)) }) { Text("保存") } }; OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("标题") }); Spacer(Modifier.height(10.dp)); OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().weight(1f), label = { Text("Markdown") }, maxLines = 30) } } } }
@Composable
private fun LetterEditor(letter: FutureLetter, onDismiss: () -> Unit, onSave: (FutureLetter) -> Unit) { var title by remember { mutableStateOf(letter.title) }; var text by remember { mutableStateOf(letter.content) }; var date by remember { mutableStateOf(letter.deliverDate) }; Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) { Surface(Modifier.fillMaxSize(), color = NotePaper) { Column(Modifier.padding(18.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text("写给未来的自己", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); TextButton(onClick = { if (date.isNotBlank()) onSave(letter.copy(title = title, content = text, deliverDate = date)) }) { Text("保存") } }; OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("标题（可选）") }); OutlinedTextField(date, { date = it }, Modifier.fillMaxWidth().padding(top = 10.dp), label = { Text("展示日期 YYYY-MM-DD") }); Spacer(Modifier.height(10.dp)); OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().weight(1f), label = { Text("正文") }, maxLines = 30) } } } }

@Composable
private fun FolderPhotoThumb(context: Context, uri: Uri) {
    val bitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) {
            runCatching { context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it)?.asImageBitmap() } }.getOrNull()
        }
    }
    Card(Modifier.fillMaxWidth().aspectRatio(1f), colors = CardDefaults.cardColors(containerColor = NoteSoft)) {
        if (bitmap != null) Image(bitmap!!, contentDescription = "照片", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("读取中", style = MaterialTheme.typography.labelSmall, color = NoteMuted) }
    }
}

@Composable private fun EmptyHint(text: String) { Surface(color = NoteSoft, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) { Text(text, color = NoteInk, modifier = Modifier.padding(16.dp)) } }
