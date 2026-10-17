package com.localalbum.camera

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SkipNext
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private val NoteInk = Color(0xFF21312C)
private val NoteGreen = Color(0xFF2F6B52)
private val NotePale = Color(0xFFEAF3EC)
private val NotePaper = Color(0xFFF7F9F6)
private val NoteMuted = Color(0xFF708077)

private data class QuickNote(val id: String, val content: String, val createdAt: Long, val categoryIds: List<String>, val status: String = "inbox")
private data class NoteCategory(val id: String, val name: String, val parentId: String?, val path: String)
private data class Diary(val id: String, val title: String, val markdown: String, val date: String)
private data class FutureLetter(val id: String, val title: String, val content: String, val deliverDate: String, val shown: Boolean = false)
private data class NotesState(val notes: List<QuickNote> = emptyList(), val categories: List<NoteCategory> = emptyList(), val diaries: List<Diary> = emptyList(), val letters: List<FutureLetter> = emptyList())

private object NotesStore {
    private const val PREF = "suixin_notes"
    private const val KEY = "state"
    fun load(context: Context): NotesState = runCatching {
        val root = JSONObject(context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, "{}") ?: "{}")
        NotesState(
            notes = root.optJSONArray("notes").toList { QuickNote(it.optString("id"), it.optString("content"), it.optLong("createdAt"), it.optJSONArray("categoryIds").toStringList(), it.optString("status", "inbox")) },
            categories = root.optJSONArray("categories").toList { NoteCategory(it.optString("id"), it.optString("name"), it.optNullableString("parentId"), it.optString("path")) },
            diaries = root.optJSONArray("diaries").toList { Diary(it.optString("id"), it.optString("title"), it.optString("markdown"), it.optString("date")) },
            letters = root.optJSONArray("letters").toList { FutureLetter(it.optString("id"), it.optString("title"), it.optString("content"), it.optString("deliverDate"), it.optBoolean("shown")) },
        )
    }.getOrDefault(NotesState())

    fun save(context: Context, state: NotesState) {
        val root = JSONObject().apply {
            put("notes", JSONArray().apply { state.notes.forEach { put(JSONObject().apply { put("id", it.id); put("content", it.content); put("createdAt", it.createdAt); put("categoryIds", JSONArray(it.categoryIds)); put("status", it.status) }) } })
            put("categories", JSONArray().apply { state.categories.forEach { put(JSONObject().apply { put("id", it.id); put("name", it.name); put("parentId", it.parentId); put("path", it.path) }) } })
            put("diaries", JSONArray().apply { state.diaries.forEach { put(JSONObject().apply { put("id", it.id); put("title", it.title); put("markdown", it.markdown); put("date", it.date) }) } })
            put("letters", JSONArray().apply { state.letters.forEach { put(JSONObject().apply { put("id", it.id); put("title", it.title); put("content", it.content); put("deliverDate", it.deliverDate); put("shown", it.shown) }) } })
        }
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY, root.toString()).apply()
    }
}

private inline fun <T> JSONArray?.toList(map: (JSONObject) -> T): List<T> = if (this == null) emptyList() else (0 until length()).mapNotNull { optJSONObject(it)?.let(map) }
private fun JSONArray?.toStringList(): List<String> = if (this == null) emptyList() else (0 until length()).map { optString(it) }
private fun JSONObject.optNullableString(key: String): String? = if (isNull(key)) null else optString(key).takeIf { it.isNotBlank() }
private fun prettyTime(value: Long): String = SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA).format(Date(value))
private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())

class NotesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = NoteGreen, background = NotePaper, surface = Color.White, onSurface = NoteInk)) {
                NotesApp()
            }
        }
    }
}

private enum class NotesPage { HOME, ORGANIZER, DIARIES, LETTERS, CATEGORY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(NotesStore.load(context)) }
    var page by remember { mutableStateOf(NotesPage.HOME) }
    var selectedCategory by remember { mutableStateOf<NoteCategory?>(null) }
    var drawer by remember { mutableStateOf(DrawerValue.Closed) }
    var editor by remember { mutableStateOf<QuickNote?>(null) }
    var diaryEditor by remember { mutableStateOf<Diary?>(null) }
    var letterEditor by remember { mutableStateOf<FutureLetter?>(null) }
    fun update(next: NotesState) { state = next; NotesStore.save(context, next) }
    fun addNote(text: String, category: NoteCategory? = selectedCategory) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val paths = Regex("(?<!\\w)#([\\p{L}\\d][\\p{L}\\d/_-]*)").findAll(clean).map { it.groupValues[1] }.toList()
        var categories = state.categories.toMutableList()
        val ids = mutableListOf<String>()
        paths.forEach { path ->
            var parent: String? = null
            var built = ""
            path.split('/').filter { it.isNotBlank() }.forEach { name ->
                built = if (built.isBlank()) name else "$built/$name"
                val existing = categories.firstOrNull { it.path == built }
                val found = existing ?: NoteCategory(UUID.randomUUID().toString(), name, parent, built).also { categories += it }
                ids += found.id
                parent = found.id
            }
        }
        category?.let { if (it.id !in ids) ids += it.id }
        update(state.copy(categories = categories, notes = listOf(QuickNote(UUID.randomUUID().toString(), clean, System.currentTimeMillis(), ids, if (ids.isEmpty()) "inbox" else "organized")) + state.notes))
    }
    val dueLetters = state.letters.filter { it.deliverDate <= today() && !it.shown }
    ModalNavigationDrawer(drawerState = androidx.compose.material3.rememberDrawerState(drawer), drawerContent = {
        ModalDrawerSheet { DrawerContent(state, page, selectedCategory, onClose = { drawer = DrawerValue.Closed }, onNavigate = { target, category -> page = target; selectedCategory = category; drawer = DrawerValue.Closed }) }
    }) {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text(when (page) { NotesPage.HOME -> "随心记"; NotesPage.ORGANIZER -> "整理台"; NotesPage.DIARIES -> "日记"; NotesPage.LETTERS -> "未来的信"; NotesPage.CATEGORY -> selectedCategory?.path ?: "分类" }, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { if (page == NotesPage.HOME) drawer = DrawerValue.Open else page = NotesPage.HOME }) { Icon(if (page == NotesPage.HOME) Icons.Filled.Menu else Icons.AutoMirrored.Filled.ArrowBack, "返回") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NotePaper),
            )
        }) { padding ->
            when (page) {
                NotesPage.HOME -> HomePage(state, dueLetters, padding, onAdd = ::addNote, onCategory = { selectedCategory = it; page = NotesPage.CATEGORY }, onOrganize = { page = NotesPage.ORGANIZER }, onDiary = { page = NotesPage.DIARIES }, onLetters = { page = NotesPage.LETTERS }, onEdit = { editor = it })
                NotesPage.CATEGORY -> CategoryPage(state, selectedCategory!!, padding, onAdd = ::addNote, onEdit = { editor = it }, onCategory = { selectedCategory = it })
                NotesPage.ORGANIZER -> OrganizerPage(state, padding, onUpdate = ::update)
                NotesPage.DIARIES -> DiaryPage(state, padding, onEdit = { diaryEditor = it }, onCreate = { diaryEditor = Diary(UUID.randomUUID().toString(), "", "", today()) })
                NotesPage.LETTERS -> LetterPage(state, dueLetters, padding, onEdit = { letterEditor = it }, onCreate = { letterEditor = FutureLetter(UUID.randomUUID().toString(), "", "", today()) }, onUpdate = ::update)
            }
        }
    }
    editor?.let { NoteEditorDialog(it, onDismiss = { editor = null }, onSave = { text -> update(state.copy(notes = state.notes.map { n -> if (n.id == it.id) n.copy(content = text) else n })); editor = null }, onDelete = { update(state.copy(notes = state.notes.filterNot { n -> n.id == it.id })); editor = null }) }
    diaryEditor?.let { DiaryEditorDialog(it, onDismiss = { diaryEditor = null }, onSave = { d -> update(state.copy(diaries = listOf(d) + state.diaries.filterNot { x -> x.id == d.id })); diaryEditor = null }) }
    letterEditor?.let { LetterEditorDialog(it, onDismiss = { letterEditor = null }, onSave = { l -> update(state.copy(letters = listOf(l) + state.letters.filterNot { x -> x.id == l.id })); letterEditor = null }) }
}

@Composable
private fun DrawerContent(state: NotesState, page: NotesPage, selected: NoteCategory?, onClose: () -> Unit, onNavigate: (NotesPage, NoteCategory?) -> Unit) {
    Column(Modifier.fillMaxSize().padding(vertical = 18.dp)) {
        Text("随心记", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp))
        DrawerItem("首页", Icons.Filled.Home, page == NotesPage.HOME) { onNavigate(NotesPage.HOME, null) }
        DrawerItem("随记箱 · ${state.notes.count { it.status == "inbox" }}", Icons.Filled.Inbox, false) { onNavigate(NotesPage.ORGANIZER, null) }
        DrawerItem("整理台", Icons.Filled.Tune, page == NotesPage.ORGANIZER) { onNavigate(NotesPage.ORGANIZER, null) }
        DrawerItem("日记", Icons.Filled.Book, page == NotesPage.DIARIES) { onNavigate(NotesPage.DIARIES, null) }
        DrawerItem("未来的信", Icons.Filled.MailOutline, page == NotesPage.LETTERS) { onNavigate(NotesPage.LETTERS, null) }
        Divider(Modifier.padding(vertical = 12.dp))
        Text("分类", style = MaterialTheme.typography.labelLarge, color = NoteMuted, modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp))
        state.categories.filter { it.parentId == null }.forEach { category -> DrawerItem(category.path, Icons.Filled.Label, selected?.id == category.id) { onNavigate(NotesPage.CATEGORY, category) } }
        Spacer(Modifier.weight(1f)); Text("本地保存 · ${state.notes.size} 条记录", color = NoteMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp))
    }
}

@Composable private fun DrawerItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) { Row(Modifier.fillMaxWidth().clickable(onClick = onClick).background(if (selected) NotePale else Color.Transparent).padding(horizontal = 22.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = if (selected) NoteGreen else NoteMuted, modifier = Modifier.size(21.dp)); Spacer(Modifier.width(14.dp)); Text(label, color = if (selected) NoteGreen else NoteInk, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) } }

@Composable
private fun HomePage(state: NotesState, due: List<FutureLetter>, padding: PaddingValues, onAdd: (String) -> Unit, onCategory: (NoteCategory) -> Unit, onOrganize: () -> Unit, onDiary: () -> Unit, onLetters: () -> Unit, onEdit: (QuickNote) -> Unit) {
    var input by remember { mutableStateOf("") }
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("先记下来，之后再整理", color = NoteMuted); OutlinedTextField(input, { input = it }, Modifier.fillMaxWidth().height(120.dp), placeholder = { Text("想到什么就写什么… 也可以用 #生活/阅读") }, maxLines = 5, shape = RoundedCornerShape(14.dp), trailingIcon = { IconButton(onClick = { onAdd(input); input = "" }) { Icon(Icons.Filled.Send, "保存", tint = NoteGreen) } }) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { SummaryCard("随记箱", state.notes.count { it.status == "inbox" }, Icons.Filled.Inbox, onOrganize); SummaryCard("今日的信", due.size, Icons.Filled.MailOutline, onLetters) } }
        item { Row(verticalAlignment = Alignment.CenterVertically) { Text("分类空间", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); TextButton(onClick = onOrganize) { Text("去整理") } } }
        if (state.categories.isEmpty()) item { EmptyHint("还没有分类，使用 #生活/阅读 会自动创建") }
        else item { LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxWidth().height(((state.categories.count { it.parentId == null } + 1) / 2 * 92).coerceAtLeast(92).dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(state.categories.filter { it.parentId == null }) { category -> Card(Modifier.fillMaxWidth().height(82.dp).clickable { onCategory(category) }, colors = CardDefaults.cardColors(containerColor = NotePale)) { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) { Text(category.name, fontWeight = FontWeight.Bold, color = NoteInk, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${state.notes.count { category.id in it.categoryIds }} 条记录", color = NoteMuted, style = MaterialTheme.typography.labelSmall) } } } } }
        item { Row(verticalAlignment = Alignment.CenterVertically) { Text("最近记录", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); TextButton(onClick = onDiary) { Icon(Icons.Filled.Book, null, Modifier.size(17.dp)); Spacer(Modifier.width(4.dp)); Text("日记") } } }
        if (state.notes.isEmpty()) item { EmptyHint("还没有记录，下面的输入框等你开始") }
        items(state.notes.take(20), key = { it.id }) { note -> NoteRow(note, state.categories, onClick = { onEdit(note) }) }
    }
}

@Composable private fun SummaryCard(label: String, count: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) { Card(Modifier.fillMaxWidth(0.48f).height(88.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Color.White)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = NoteGreen, modifier = Modifier.size(25.dp)); Spacer(Modifier.width(10.dp)); Column { Text(label, color = NoteMuted, style = MaterialTheme.typography.labelMedium); Text(count.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) } } } }
@Composable private fun EmptyHint(text: String) { Surface(color = NotePale, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) { Text(text, color = NoteGreen, modifier = Modifier.padding(16.dp)) } }
@Composable private fun NoteRow(note: QuickNote, categories: List<NoteCategory>, onClick: () -> Unit) { Card(Modifier.fillMaxWidth().clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(15.dp)) { Text(note.content, maxLines = 4, overflow = TextOverflow.Ellipsis); Spacer(Modifier.height(7.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Text(prettyTime(note.createdAt), color = NoteMuted, style = MaterialTheme.typography.labelSmall); if (note.categoryIds.isNotEmpty()) { Spacer(Modifier.width(10.dp)); Text(note.categoryIds.mapNotNull { id -> categories.firstOrNull { it.id == id }?.path }.joinToString(" · "), color = NoteGreen, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis) } } } } }

@Composable private fun CategoryPage(state: NotesState, category: NoteCategory, padding: PaddingValues, onAdd: (String, NoteCategory?) -> Unit, onEdit: (QuickNote) -> Unit, onCategory: (NoteCategory) -> Unit) { var input by remember { mutableStateOf("") }; val children = state.categories.filter { it.parentId == category.id }; val notes = state.notes.filter { category.id in it.categoryIds }; LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { Text("在 ${category.path} 中记录", color = NoteMuted); OutlinedTextField(input, { input = it }, Modifier.fillMaxWidth(), placeholder = { Text("写下这件事…") }, trailingIcon = { IconButton(onClick = { onAdd(input, category); input = "" }) { Icon(Icons.Filled.Send, "保存") } }) }; if (children.isNotEmpty()) { item { Text("子分类", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }; items(children) { child -> Surface(Modifier.fillMaxWidth().clickable { onCategory(child) }, color = NotePale, shape = RoundedCornerShape(10.dp)) { Text(child.name, modifier = Modifier.padding(14.dp), color = NoteGreen, fontWeight = FontWeight.Bold) } } }; item { Text("本分类记录 · ${notes.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }; items(notes, key = { it.id }) { NoteRow(it, state.categories, onClick = { onEdit(it) }) } } }

@Composable private fun OrganizerPage(state: NotesState, padding: PaddingValues, onUpdate: (NotesState) -> Unit) { var index by remember { mutableStateOf(0) }; var selected by remember { mutableStateOf<QuickNote?>(null) }; val inbox = state.notes.filter { it.status == "inbox" }; val current = inbox.getOrNull(index.coerceAtMost((inbox.size - 1).coerceAtLeast(0))); Column(Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { Text("一次处理一条未分类记录", color = NoteMuted); if (current == null) { EmptyHint("随记箱为空，所有记录都整理好了") } else { Text("${index + 1} / ${inbox.size}", color = NoteMuted, style = MaterialTheme.typography.labelLarge); Card(colors = CardDefaults.cardColors(containerColor = Color.White)) { Text(current.content, Modifier.padding(20.dp), style = MaterialTheme.typography.titleMedium) }; Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { OutlinedButton(onClick = { index++ }) { Icon(Icons.Filled.SkipNext, null); Spacer(Modifier.width(5.dp)); Text("跳过") }; Button(onClick = { selected = current }) { Icon(Icons.Filled.Label, null); Spacer(Modifier.width(5.dp)); Text("归类") }; IconButton(onClick = { onUpdate(state.copy(notes = state.notes.filterNot { it.id == current.id })); index = index.coerceAtMost(inbox.size - 2) }) { Icon(Icons.Filled.DeleteOutline, "删除") } } }; Spacer(Modifier.weight(1f)); Text("跳过的记录仍会留在随记箱，重新进入整理台时可以继续处理", color = NoteMuted, style = MaterialTheme.typography.labelSmall) }; selected?.let { note -> CategoryPicker(state.categories, onDismiss = { selected = null }, onPick = { category -> onUpdate(state.copy(notes = state.notes.map { if (it.id == note.id) it.copy(categoryIds = listOf(category.id), status = "organized") else it })); selected = null; index++ }) } }

@Composable private fun CategoryPicker(categories: List<NoteCategory>, onDismiss: () -> Unit, onPick: (NoteCategory) -> Unit) { AlertDialog(onDismissRequest = onDismiss, title = { Text("选择分类") }, text = { if (categories.isEmpty()) Text("还没有分类，请先在首页使用 #分类") else Column { categories.forEach { category -> TextButton(onClick = { onPick(category) }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Filled.Folder, null); Spacer(Modifier.width(8.dp)); Text(category.path) } } } }, confirmButton = { TextButton(onClick = onDismiss) { Text("取消") } }) }

@Composable private fun DiaryPage(state: NotesState, padding: PaddingValues, onEdit: (Diary) -> Unit, onCreate: () -> Unit) { Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp)) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Text("Markdown 原文保留", color = NoteMuted); Spacer(Modifier.weight(1f)); Button(onClick = onCreate) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(4.dp)); Text("新建") } }; if (state.diaries.isEmpty()) EmptyHint("还没有日记，写下第一篇吧") else LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 14.dp)) { items(state.diaries, key = { it.id }) { diary -> Card(Modifier.fillMaxWidth().clickable { onEdit(diary) }) { Column(Modifier.padding(16.dp)) { Text(diary.title.ifBlank { "无标题日记" }, fontWeight = FontWeight.Bold); Text(diary.date, color = NoteMuted, style = MaterialTheme.typography.labelSmall); Spacer(Modifier.height(7.dp)); Text(diary.markdown.lineSequence().firstOrNull().orEmpty(), maxLines = 2, overflow = TextOverflow.Ellipsis) } } } } } }

@Composable private fun LetterPage(state: NotesState, due: List<FutureLetter>, padding: PaddingValues, onEdit: (FutureLetter) -> Unit, onCreate: () -> Unit, onUpdate: (NotesState) -> Unit) { Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp)) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Text("到日期后打开 App 查看", color = NoteMuted); Spacer(Modifier.weight(1f)); Button(onClick = onCreate) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(4.dp)); Text("写信") } }; if (due.isNotEmpty()) { Text("今天给你的信", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)); due.forEach { letter -> Card(Modifier.fillMaxWidth().padding(top = 10.dp).clickable { onUpdate(state.copy(letters = state.letters.map { if (it.id == letter.id) it.copy(shown = true) else it })) }) { Column(Modifier.padding(16.dp)) { Text(letter.title.ifBlank { "给未来的自己" }, fontWeight = FontWeight.Bold); Text(letter.content, Modifier.padding(top = 8.dp), maxLines = 4, overflow = TextOverflow.Ellipsis) } } } }; Text("全部信件", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 22.dp)); LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 12.dp)) { items(state.letters, key = { it.id }) { letter -> Card(Modifier.fillMaxWidth().clickable { onEdit(letter) }) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.MailOutline, null, tint = NoteGreen); Spacer(Modifier.width(10.dp)); Column { Text(letter.title.ifBlank { "给未来的自己" }, fontWeight = FontWeight.Bold); Text("${letter.deliverDate}${if (letter.shown) " · 已展示" else ""}", color = NoteMuted, style = MaterialTheme.typography.labelSmall) } } } } } } }

@Composable private fun NoteEditorDialog(note: QuickNote, onDismiss: () -> Unit, onSave: (String) -> Unit, onDelete: () -> Unit) { var text by remember { mutableStateOf(note.content) }; AlertDialog(onDismissRequest = onDismiss, title = { Text("编辑随记") }, text = { OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().height(180.dp), maxLines = 8) }, confirmButton = { TextButton(onClick = { onSave(text) }) { Icon(Icons.Filled.Save, null); Text("保存") } }, dismissButton = { Row { TextButton(onClick = onDelete) { Icon(Icons.Filled.DeleteOutline, null); Text("删除") }; TextButton(onClick = onDismiss) { Text("取消") } } }) }
@Composable private fun DiaryEditorDialog(diary: Diary, onDismiss: () -> Unit, onSave: (Diary) -> Unit) { var title by remember { mutableStateOf(diary.title) }; var text by remember { mutableStateOf(diary.markdown) }; Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) { Surface(Modifier.fillMaxSize(), color = NotePaper) { Column(Modifier.padding(18.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text("编辑日记", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); TextButton(onClick = { onSave(diary.copy(title = title, markdown = text)) }) { Text("保存") } }; OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("标题") }); Spacer(Modifier.height(10.dp)); OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().weight(1f), label = { Text("Markdown") }, maxLines = 30) } } } }
@Composable private fun LetterEditorDialog(letter: FutureLetter, onDismiss: () -> Unit, onSave: (FutureLetter) -> Unit) { var title by remember { mutableStateOf(letter.title) }; var text by remember { mutableStateOf(letter.content) }; var date by remember { mutableStateOf(letter.deliverDate) }; Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) { Surface(Modifier.fillMaxSize(), color = NotePaper) { Column(Modifier.padding(18.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text("写给未来的自己", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); TextButton(onClick = { if (date.isNotBlank()) onSave(letter.copy(title = title, content = text, deliverDate = date)) }) { Text("保存") } }; OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("标题（可选）") }); OutlinedTextField(date, { date = it }, Modifier.fillMaxWidth().padding(top = 10.dp), label = { Text("展示日期 YYYY-MM-DD") }); Spacer(Modifier.height(10.dp)); OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().weight(1f), label = { Text("正文") }, maxLines = 30) } } } }
