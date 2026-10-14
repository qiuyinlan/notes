package com.localalbum.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import java.io.File
import kotlin.math.roundToInt

private val Ink = Color(0xFF1C2421)
private val Moss = Color(0xFF315D4A)
private val Mint = Color(0xFFE4F0E8)
private val Paper = Color(0xFFF5F7F4)
private val Muted = Color(0xFF707A74)

private data class Category(val name: String, val folderUri: String? = null)
private data class ImportFolder(val name: String, val uri: Uri, val imageCount: Int)
private data class PhotoItem(val uri: Uri, val name: String, val sourceKey: String? = null, val dateMillis: Long = 0L)
private data class VideoItem(val uri: Uri, val name: String, val durationMillis: Long, val dateMillis: Long)

private fun PhotoItem.belongsTo(category: Category): Boolean =
    sourceKey == (category.folderUri ?: "DCIM/${category.name}")

private enum class AppScreen { HOME, CATEGORY, ORGANIZE, TRASH, IMPORT, VIDEOS, VIDEO_TRASH }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(245, 247, 244)
        window.navigationBarColor = android.graphics.Color.rgb(245, 247, 244)
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Moss,
                onPrimary = Color.White,
                background = Paper,
                surface = Color.White,
                onSurface = Ink,
                secondaryContainer = Mint,
            )) {
                AlbumCameraApp()
            }
        }
    }
}

@Composable
private fun AlbumCameraApp() {
    val context = LocalContext.current
    val screenScope = rememberCoroutineScope()
    var categories by remember { mutableStateOf(CategoryStore.load(context)) }
    var selectedCategory by rememberSaveable { mutableStateOf(categories.firstOrNull()?.name ?: "日常") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var cameraOpen by rememberSaveable { mutableStateOf(false) }
    var screen by rememberSaveable { mutableStateOf(AppScreen.HOME) }
    var previewCategory by remember { mutableStateOf<Category?>(null) }
    var trashCategory by remember { mutableStateOf<Category?>(null) }
    var organizePhotos by remember { mutableStateOf<List<PhotoItem>>(emptyList()) }
    var trash by remember { mutableStateOf<List<PhotoItem>>(emptyList()) }
    var videos by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var videoTrash by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var pendingPreview by remember { mutableStateOf<Category?>(null) }
    var pendingOrganize by remember { mutableStateOf(false) }
    var pendingCategoryName by remember { mutableStateOf("") }
    var pendingDeleteCategory by remember { mutableStateOf<Category?>(null) }
    var importFolders by remember { mutableStateOf<List<ImportFolder>>(emptyList()) }
    var importSelected by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var pendingSystemDelete by remember { mutableStateOf<List<PhotoItem>>(emptyList()) }
    var pendingVideoDelete by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var pendingVideoReview by remember { mutableStateOf(false) }
    val deleteRequest = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            trash = trash.filterNot { item -> pendingSystemDelete.any { it.uri == item.uri } }
            Toast.makeText(context, "照片已删除", Toast.LENGTH_SHORT).show()
        }
        pendingSystemDelete = emptyList()
    }
    val videoDeleteRequest = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            videos = videos.filterNot { item -> pendingVideoDelete.any { it.uri == item.uri } }
            videoTrash = videoTrash.filterNot { item -> pendingVideoDelete.any { it.uri == item.uri } }
            Toast.makeText(context, "视频已删除", Toast.LENGTH_SHORT).show()
        }
        pendingVideoDelete = emptyList()
    }
    val photoPermission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
    val videoPermission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE
    val videoPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && pendingVideoReview) {
            screenScope.launch {
                videos = withContext(Dispatchers.IO) { queryAllVideos(context) }
                screen = AppScreen.VIDEOS
            }
        } else if (!granted) {
            Toast.makeText(context, "需要视频读取权限才能整理手机视频", Toast.LENGTH_LONG).show()
        }
        pendingVideoReview = false
    }
    val photoPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && pendingPreview != null) {
            previewCategory = pendingPreview
            screen = AppScreen.CATEGORY
        } else if (granted && pendingOrganize) {
            organizePhotos = queryAllPhotos(context, categories)
            screen = AppScreen.ORGANIZE
        }
        pendingPreview = null
        pendingOrganize = false
    }
    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        val name = safeFolderName(pendingCategoryName).ifBlank { uri?.let { folderDisplayName(context, it) }.orEmpty() }
        if (uri != null && name.isNotBlank() && categories.none { it.name == name }) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            categories = categories + Category(name, uri.toString())
            selectedCategory = name
            CategoryStore.save(context, categories)
        }
        showCreateDialog = false
    }
    val picturesPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION) }
            importFolders = queryPictureFolders(context, uri)
            importSelected = emptySet()
            screen = AppScreen.IMPORT
        }
    }

    LaunchedEffect(categories) {
        if (categories.isNotEmpty() && categories.none { it.name == selectedCategory }) selectedCategory = categories.first().name
    }

    if (cameraOpen) {
        CameraScreen(
            category = categories.firstOrNull { it.name == selectedCategory } ?: Category(selectedCategory),
            onBack = { cameraOpen = false },
            onCategoryChange = { selectedCategory = it },
            categories = categories,
        )
    } else when (screen) {
        AppScreen.HOME -> HomeScreen(
            categories = categories,
            selectedCategory = selectedCategory,
            trashCount = trash.size,
            videoTrashCount = videoTrash.size,
            onSelect = { category ->
                selectedCategory = category.name
                if (category.folderUri == null && ContextCompat.checkSelfPermission(context, photoPermission) != PackageManager.PERMISSION_GRANTED) {
                    pendingPreview = category
                    photoPermissionLauncher.launch(photoPermission)
                } else {
                    previewCategory = category
                    screen = AppScreen.CATEGORY
                }
            },
            onAdd = { showCreateDialog = true },
            onDelete = { name -> pendingDeleteCategory = categories.firstOrNull { it.name == name } },
            onShoot = { screen = AppScreen.HOME; cameraOpen = true },
            onShootCategory = { category -> selectedCategory = category.name; screen = AppScreen.HOME; cameraOpen = true },
            onOrganize = {
                if (ContextCompat.checkSelfPermission(context, photoPermission) != PackageManager.PERMISSION_GRANTED) {
                    pendingOrganize = true
                    photoPermissionLauncher.launch(photoPermission)
                } else {
                    organizePhotos = queryAllPhotos(context, categories)
                    screen = AppScreen.ORGANIZE
                }
            },
            onTrash = { trashCategory = null; screen = AppScreen.TRASH },
            onImport = { picturesPicker.launch(null) },
            onVideos = {
                if (ContextCompat.checkSelfPermission(context, videoPermission) == PackageManager.PERMISSION_GRANTED) {
                    screenScope.launch {
                        videos = withContext(Dispatchers.IO) { queryAllVideos(context) }
                        screen = AppScreen.VIDEOS
                    }
                } else {
                    pendingVideoReview = true
                    videoPermissionLauncher.launch(videoPermission)
                }
            },
            onVideoTrash = { screen = AppScreen.VIDEO_TRASH },
        )
        AppScreen.CATEGORY -> previewCategory?.let { category ->
            CategoryPhotosScreen(
                category = category,
                categories = categories,
                stagedPhotos = trash.filter { it.belongsTo(category) },
                onBack = { screen = AppScreen.HOME },
                onShoot = { selectedCategory = category.name; screen = AppScreen.HOME; cameraOpen = true },
                onStageDelete = { photos -> trash = (trash + photos).distinctBy { it.uri } },
                onTrash = { trashCategory = category; screen = AppScreen.TRASH },
            )
        }
        AppScreen.ORGANIZE -> OrganizePhotosScreen(
            photos = organizePhotos.filterNot { photo -> trash.any { it.uri == photo.uri } },
            categories = categories,
            onBack = { screen = AppScreen.HOME },
            onStageDelete = { photos -> trash = (trash + photos).distinctBy { it.uri } },
            onTrash = { trashCategory = null; screen = AppScreen.TRASH },
            trashCount = trash.size,
        )
        AppScreen.TRASH -> TrashScreen(
            photos = trashCategory?.let { category -> trash.filter { it.belongsTo(category) } } ?: trash,
            categoryName = trashCategory?.name,
            onBack = { screen = if (trashCategory != null) AppScreen.CATEGORY else AppScreen.HOME; trashCategory = null },
            onRestore = { restored -> trash = trash.filterNot { item -> restored.any { it.uri == item.uri } } },
            onDelete = { deleted ->
                val mediaPhotos = if (Build.VERSION.SDK_INT >= 30) deleted.filter { isMediaStorePhoto(context, it) } else emptyList()
                val documentPhotos = deleted.filterNot { it in mediaPhotos }
                val removedDocuments = documentPhotos.filter { deletePhoto(context, it) }
                if (removedDocuments.isNotEmpty()) {
                    trash = trash.filterNot { item -> removedDocuments.any { it.uri == item.uri } }
                }
                if (removedDocuments.size < documentPhotos.size) {
                    Toast.makeText(context, "部分文件无法删除，仍保留在暂存区", Toast.LENGTH_LONG).show()
                }
                if (mediaPhotos.isNotEmpty()) {
                    runCatching {
                        val request = MediaStore.createDeleteRequest(context.contentResolver, mediaPhotos.map { it.uri })
                        pendingSystemDelete = mediaPhotos
                        deleteRequest.launch(androidx.activity.result.IntentSenderRequest.Builder(request.intentSender).build())
                    }.onFailure {
                        pendingSystemDelete = emptyList()
                        Toast.makeText(context, "系统未能发起删除确认，照片仍保留在暂存区", Toast.LENGTH_LONG).show()
                    }
                }
            },
        )
        AppScreen.IMPORT -> ImportFoldersScreen(
            folders = importFolders,
            selected = importSelected,
            onBack = { screen = AppScreen.HOME },
            onToggle = { folder -> importSelected = if (folder.uri in importSelected) importSelected - folder.uri else importSelected + folder.uri },
            onSelectAll = { importSelected = if (importSelected.size == importFolders.size) emptySet() else importFolders.map { it.uri }.toSet() },
            onImport = {
                val existing = categories.map { it.name }.toSet()
                val additions = importFolders.filter { it.uri in importSelected && it.name !in existing }.map { Category(it.name, it.uri.toString()) }
                if (additions.isNotEmpty()) {
                    categories = (categories + additions).distinctBy { it.name }
                    CategoryStore.save(context, categories)
                }
                Toast.makeText(context, "已导入 ${additions.size} 个文件夹", Toast.LENGTH_SHORT).show()
                screen = AppScreen.HOME
            },
        )
        AppScreen.VIDEOS -> VideoReviewScreen(
            videos = videos.filterNot { item -> videoTrash.any { it.uri == item.uri } },
            stagedCount = videoTrash.size,
            onBack = { screen = AppScreen.HOME },
            onTrash = { screen = AppScreen.VIDEO_TRASH },
            onStageDelete = { video -> videoTrash = (videoTrash + video).distinctBy { it.uri } },
        )
        AppScreen.VIDEO_TRASH -> VideoTrashScreen(
            videos = videoTrash,
            onBack = { screen = AppScreen.VIDEOS },
            onRestore = { restored -> videoTrash = videoTrash.filterNot { item -> restored.any { it.uri == item.uri } } },
            onDelete = { deleted ->
                if (Build.VERSION.SDK_INT >= 30 && deleted.isNotEmpty()) {
                    runCatching {
                        val request = MediaStore.createDeleteRequest(context.contentResolver, deleted.map { it.uri })
                        pendingVideoDelete = deleted
                        videoDeleteRequest.launch(androidx.activity.result.IntentSenderRequest.Builder(request.intentSender).build())
                    }.onFailure {
                        pendingVideoDelete = emptyList()
                        Toast.makeText(context, "系统未能发起删除确认，视频仍保留在暂存区", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val removed = deleted.filter { runCatching { context.contentResolver.delete(it.uri, null, null) > 0 }.getOrDefault(false) }
                    videos = videos.filterNot { item -> removed.any { it.uri == item.uri } }
                    videoTrash = videoTrash.filterNot { item -> removed.any { it.uri == item.uri } }
                    if (removed.size < deleted.size) Toast.makeText(context, "部分视频删除失败，仍保留在暂存区", Toast.LENGTH_LONG).show()
                }
            },
        )
    }

    if (showCreateDialog) {
        CreateCategoryDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { rawName ->
                val name = safeFolderName(rawName)
                if (name.isNotBlank() && categories.none { it.name == name }) {
                    categories = categories + Category(name)
                    selectedCategory = name
                    CategoryStore.save(context, categories)
                }
                showCreateDialog = false
            },
            onPickFolder = { name -> pendingCategoryName = name; folderPicker.launch(null) },
        )
    }
    pendingDeleteCategory?.let { category ->
        AlertDialog(
            onDismissRequest = { pendingDeleteCategory = null },
            title = { Text("删除分类？") },
            text = { Text("请选择删除方式。只删除分类会保留照片；删除分类和照片会把该分类中的图片放入系统回收站/删除位置。") },
            confirmButton = {
                Row {
                    TextButton(onClick = {
                        queryPhotos(context, category).forEach { deletePhoto(context, it) }
                        categories = categories.filterNot { it.name == category.name }
                        CategoryStore.save(context, categories)
                        pendingDeleteCategory = null
                    }) { Text("分类和照片") }
                    TextButton(onClick = {
                        categories = categories.filterNot { it.name == category.name }
                        CategoryStore.save(context, categories)
                        pendingDeleteCategory = null
                    }) { Text("只删分类") }
                }
            },
            dismissButton = { TextButton(onClick = { pendingDeleteCategory = null }) { Text("取消") } },
        )
    }
}

@Composable
private fun HomeScreen(
    categories: List<Category>,
    selectedCategory: String,
    trashCount: Int,
    videoTrashCount: Int,
    onSelect: (Category) -> Unit,
    onAdd: () -> Unit,
    onDelete: (String) -> Unit,
    onShoot: () -> Unit,
    onShootCategory: (Category) -> Unit,
    onOrganize: () -> Unit,
    onTrash: () -> Unit,
    onImport: () -> Unit,
    onVideos: () -> Unit,
    onVideoTrash: () -> Unit,
) {
    Scaffold(containerColor = Paper) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 18.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Row(Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("分类相机", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
                            Text("先选分类，再查看或拍摄", style = MaterialTheme.typography.bodyMedium, color = Muted, modifier = Modifier.padding(top = 5.dp))
                        }
                        Surface(shape = CircleShape, color = Mint, modifier = Modifier.size(48.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.PhotoLibrary, contentDescription = "本地相册", tint = Moss, modifier = Modifier.size(21.dp))
                            }
                        }
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onOrganize, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107), contentColor = Ink)) {
                            Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("  整理照片")
                        }
                        if (trashCount > 0) {
                            IconButton(onClick = onTrash, modifier = Modifier.size(48.dp)) {
                                Icon(Icons.Filled.DeleteOutline, contentDescription = "删除暂存区", tint = Moss)
                            }
                        }
                    }
                }
                item {
                    Button(onClick = onImport, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Moss)) {
                        Icon(Icons.Filled.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("  导入手机存储/Pictures 文件夹")
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onVideos, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Color.White)) {
                            Icon(Icons.Filled.VideoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("  整理视频")
                        }
                        if (videoTrashCount > 0) {
                            IconButton(onClick = onVideoTrash, modifier = Modifier.size(48.dp)) {
                                Icon(Icons.Filled.DeleteOutline, contentDescription = "视频删除暂存区", tint = Moss)
                            }
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("我的分类", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("${categories.size} 个", style = MaterialTheme.typography.labelMedium, color = Muted)
                        IconButton(onClick = onAdd) { Icon(Icons.Filled.Add, contentDescription = "新建分类", tint = Moss) }
                    }
                }
                if (categories.isEmpty()) {
                    item {
                        Surface(color = Color.White, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.FolderOpen, contentDescription = null, tint = Muted, modifier = Modifier.size(28.dp))
                                Text("还没有分类", color = Muted, modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                    }
                } else {
                    items(categories, key = { it.name }) { category ->
                        CategoryRow(
                            category = category,
                            selected = category.name == selectedCategory,
                            canDelete = categories.size > 1,
                            onClick = { onSelect(category) },
                            onDelete = { onDelete(category.name) },
                            onShoot = { onShootCategory(category) },
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = Muted, modifier = Modifier.size(15.dp))
                        Text("  照片只保存在手机本地", style = MaterialTheme.typography.labelSmall, color = Muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(category: Category, selected: Boolean, canDelete: Boolean, onClick: () -> Unit, onDelete: () -> Unit, onShoot: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (selected) Mint else Color.White,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(start = 14.dp, end = 4.dp, top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = if (selected) Color.White.copy(alpha = .78f) else Paper, shape = RoundedCornerShape(7.dp), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.FolderOpen, contentDescription = null, tint = Moss, modifier = Modifier.size(20.dp))
                }
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(category.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Ink)
                Text(category.displayLocation(), style = MaterialTheme.typography.labelSmall, color = Muted)
            }
            if (selected) {
                Text("已选", style = MaterialTheme.typography.labelSmall, color = Moss, modifier = Modifier.padding(horizontal = 8.dp))
            }
            IconButton(onClick = onShoot, modifier = Modifier.zIndex(1f)) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "拍摄到 ${category.name}", tint = Moss, modifier = Modifier.size(20.dp))
            }
            if (canDelete) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "删除分类 ${category.name}", tint = Muted, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun CreateCategoryDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit, onPickFolder: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建分类") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(30) },
                    singleLine = true,
                    label = { Text("分类名称") },
                    placeholder = { Text("例如：旅行、家人") },
                )
                Text("可以先输入名称，或直接选择已有文件夹并自动使用文件夹名称。", style = MaterialTheme.typography.bodySmall, color = Muted, modifier = Modifier.padding(top = 10.dp))
            }
        },
        confirmButton = {
            Row {
                TextButton(onClick = { onPickFolder(name) }) { Text("选择已有文件夹") }
                TextButton(onClick = { onCreate(name) }, enabled = name.isNotBlank()) { Text("默认位置") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}

@Composable
private fun CameraScreen(
    category: Category,
    categories: List<Category>,
    onCategoryChange: (String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { ContextCompat.getMainExecutor(context) }
    var hasPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    var capture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var canSwitchCamera by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission = granted
    }
    var showCategoryMenu by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    DisposableEffect(hasPermission, category.name, previewView, lensFacing) {
        val view = previewView
        if (hasPermission && view != null) {
            var disposed = false
            val providerFuture = ProcessCameraProvider.getInstance(context)
            val listener = Runnable {
                try {
                    val provider = providerFuture.get()
                    if (disposed) return@Runnable
                    canSwitchCamera = provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) && provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
                    val preview = Preview.Builder().build().also { it.surfaceProvider = view.surfaceProvider }
                    val imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
                    provider.unbindAll()
                    val selector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
                    provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
                    capture = imageCapture
                    cameraError = null
                } catch (error: Exception) {
                    cameraError = error.localizedMessage ?: "相机启动失败"
                }
            }
            providerFuture.addListener(listener, cameraExecutor)
            onDispose {
                disposed = true
                capture = null
                if (providerFuture.isDone) runCatching { providerFuture.get().unbindAll() }
            }
        } else {
            onDispose { }
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (hasPermission) {
            AndroidView(
                factory = { ctx -> PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewView = this
                } },
                modifier = Modifier.fillMaxSize(),
                update = {},
            )
        }
        Column(Modifier.fillMaxSize().padding(WindowInsets.safeDrawing.asPaddingValues())) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = Color.White)
                }
                Column(Modifier.weight(1f)) {
                    Text("正在拍摄", color = Color.White.copy(alpha = .78f), style = MaterialTheme.typography.labelMedium)
                    Text(category.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                if (categories.size > 1) {
                    Box {
                        IconButton(onClick = { showCategoryMenu = true }) {
                            Icon(Icons.Filled.SwapHoriz, contentDescription = "选择分类", tint = Color.White)
                        }
                        DropdownMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false }) {
                            categories.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.name) },
                                    onClick = { onCategoryChange(item.name); showCategoryMenu = false },
                                )
                            }
                        }
                    }
                }
                if (canSwitchCamera) {
                    IconButton(onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                        cameraError = null
                    }) {
                        Icon(Icons.Filled.FlipCameraAndroid, contentDescription = "切换前后摄像头", tint = Color.White)
                    }
                }
            }
            if (!hasPermission) {
                Column(Modifier.weight(1f).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("需要相机权限才能拍照", color = Color.White)
                    TextButton(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) { Text("授予权限", color = Color.White) }
                }
            } else {
                Spacer(Modifier.weight(1f))
                if (cameraError != null) {
                    Text(cameraError ?: "", color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 12.dp))
                }
                Text("保存到 ${category.displayLocation()}", color = Color.White.copy(alpha = .78f), style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 14.dp))
                FilledIconButton(
                    onClick = {
                        if (capture == null) Toast.makeText(context, "相机还在准备，请稍候", Toast.LENGTH_SHORT).show()
                        else takePhoto(context, capture, category, cameraExecutor)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 30.dp).size(76.dp).zIndex(2f),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White, contentColor = Moss),
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "拍照", modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

private fun takePhoto(context: Context, imageCapture: ImageCapture?, category: Category, executor: Executor) {
    val currentCapture = imageCapture ?: return
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(Date())
    val prepared = runCatching {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$timestamp.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (category.folderUri == null) put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/${category.name}")
        }
        val targetDoc = category.folderUri?.let { DocumentsContract.createDocument(context.contentResolver, Uri.parse(it), "image/jpeg", "$timestamp.jpg") }
        if (category.folderUri != null && targetDoc == null) error("无法在所选文件夹中创建照片")
        val temp = if (targetDoc != null) File(context.cacheDir, "$timestamp.jpg") else null
        val output = if (temp != null) ImageCapture.OutputFileOptions.Builder(temp).build()
        else ImageCapture.OutputFileOptions.Builder(context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values).build()
        Triple(output, targetDoc, temp)
    }.getOrElse {
        Toast.makeText(context, "无法准备保存位置，请重新选择文件夹", Toast.LENGTH_LONG).show()
        return
    }
    val output = prepared.first
    val targetDoc = prepared.second
    val temp = prepared.third
    currentCapture.takePicture(output, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val saved = runCatching {
                if (temp != null && targetDoc != null) {
                    context.contentResolver.openOutputStream(targetDoc, "w")!!.use { outputStream -> temp.inputStream().use { it.copyTo(outputStream) } }
                    temp.delete()
                }
                true
            }.getOrDefault(false)
            (context as? ComponentActivity)?.runOnUiThread { Toast.makeText(context, if (saved) "已保存到 ${category.name}" else "照片已拍摄，但写入分类失败", Toast.LENGTH_SHORT).show() }
        }
        override fun onError(exception: ImageCaptureException) {
            temp?.delete()
            if (targetDoc != null) runCatching { DocumentsContract.deleteDocument(context.contentResolver, targetDoc) }
            (context as? ComponentActivity)?.runOnUiThread { Toast.makeText(context, "保存失败：${exception.message ?: "请重试"}", Toast.LENGTH_LONG).show() }
        }
    })
}

private object CategoryStore {
    private const val PREFS = "categories"
    private const val KEY_NAMES = "names"

    fun load(context: Context): List<Category> = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getStringSet(KEY_NAMES, setOf("日常", "家人", "旅行" )).orEmpty().mapNotNull {
        val p = it.split("|", limit = 2); Category(p[0], p.getOrNull(1)).takeIf { c -> c.name.isNotBlank() }
    }.sortedBy { it.name }

    fun save(context: Context, categories: List<Category>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putStringSet(KEY_NAMES, categories.map { it.name + (it.folderUri?.let { u -> "|$u" } ?: "") }.toSet())
            .apply()
    }
}

private fun Category.displayLocation(): String = folderUri?.let { "已选择的手机文件夹" } ?: "DCIM/$name"

@Composable
private fun CategoryPhotosScreen(category: Category, categories: List<Category>, stagedPhotos: List<PhotoItem>, onBack: () -> Unit, onShoot: () -> Unit, onStageDelete: (List<PhotoItem>) -> Unit, onTrash: () -> Unit) {
    val context = LocalContext.current
    var photos by remember(category) { mutableStateOf(queryPhotos(context, category)) }
    var selecting by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var viewerIndex by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(stagedPhotos) { photos = photos.filterNot { photo -> stagedPhotos.any { it.uri == photo.uri } } }
    BackHandler(onBack = onBack)
    Scaffold(containerColor = Paper, topBar = {
        Row(Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") }
            Column(Modifier.weight(1f)) {
                Text(category.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${photos.size} 张照片", style = MaterialTheme.typography.labelSmall, color = Muted)
            }
            IconButton(onClick = {
                if (!selecting) {
                    onTrash()
                } else if (selected.isEmpty()) {
                    Toast.makeText(context, "请先选择照片", Toast.LENGTH_SHORT).show()
                } else {
                    val removed = photos.filter { it.uri in selected }
                    photos = photos.filterNot { it.uri in selected }
                    selected = emptySet()
                    selecting = false
                    onStageDelete(removed)
                    Toast.makeText(context, "已放入删除暂存区", Toast.LENGTH_SHORT).show()
                }
            }) {
                Box {
                    Icon(Icons.Filled.DeleteOutline, if (selecting) "暂存所选照片" else "${category.name} 删除暂存区", tint = Moss)
                    if (!selecting && stagedPhotos.isNotEmpty()) {
                        Surface(color = Color(0xFFFFC107), shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).size(16.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(stagedPhotos.size.coerceAtMost(99).toString(), style = MaterialTheme.typography.labelSmall, color = Ink)
                            }
                        }
                    }
                }
            }
            IconButton(onClick = { selecting = !selecting; selected = emptySet() }) {
                Icon(if (selecting) Icons.Filled.Close else Icons.Filled.CheckBox, if (selecting) "取消选择" else "选择照片", tint = Moss)
            }
            TextButton(onClick = onShoot) { Text("拍摄") }
        }
    }) { p ->
        if (photos.isEmpty()) Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { Text("这个分类还没有照片", color = Muted) }
        else LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(4.dp)) {
            items(photos, key = { it.uri.toString() }) { photo ->
                PhotoThumb(photo, selected = photo.uri in selected, onClick = {
                    if (selecting) selected = if (photo.uri in selected) selected - photo.uri else selected + photo.uri
                    else viewerIndex = photos.indexOfFirst { it.uri == photo.uri }
                })
            }
        }
    }
    viewerIndex?.let { index ->
        PhotoViewerScreen(
            photos = photos,
            initialIndex = index,
            categories = categories,
            onClose = { viewerIndex = null },
            onOpenTrash = { viewerIndex = null; onTrash() },
            onStageDelete = { photo ->
                photos = photos.filterNot { it.uri == photo.uri }
                onStageDelete(listOf(photo))
                Toast.makeText(context, "已放入删除暂存区", Toast.LENGTH_SHORT).show()
            },
            onMove = { photo, target ->
                if (movePhoto(context, photo, target)) {
                    photos = photos.filterNot { it.uri == photo.uri }
                    viewerIndex = null
                }
            },
            stagedCount = stagedPhotos.size,
        )
    }
}

@Composable
private fun PhotoThumb(photo: PhotoItem, selected: Boolean = false, onClick: () -> Unit) {
    val context = LocalContext.current
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = photo.uri) { value = withContext(Dispatchers.IO) { decodePhotoBitmap(context, photo, 480) } }
    Box(Modifier.padding(2.dp).size(120.dp).clickable(onClick = onClick)) {
        val bitmap: Bitmap? = bitmapState.value
        if (bitmap != null) Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        if (selected) {
            Surface(color = Moss, shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(24.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
            }
        }
    }
}

@Composable
private fun PhotoViewerScreen(
    photos: List<PhotoItem>,
    initialIndex: Int,
    categories: List<Category>,
    onClose: () -> Unit,
    onOpenTrash: () -> Unit,
    onStageDelete: (PhotoItem) -> Unit,
    onMove: (PhotoItem, Category) -> Unit,
    onKeep: (PhotoItem) -> Unit = {},
    stagedCount: Int,
    autoPlayEnabled: Boolean = false,
) {
    if (photos.isEmpty()) { onClose(); return }
    val pagerState = rememberPagerState(initialPage = initialIndex.coerceIn(0, photos.lastIndex), pageCount = { photos.size })
    val scope = rememberCoroutineScope()
    var autoPlaying by remember { mutableStateOf(false) }
    val currentPhoto = photos.getOrNull(pagerState.currentPage)
    LaunchedEffect(autoPlayEnabled, autoPlaying, currentPhoto?.uri) {
        if (autoPlayEnabled && autoPlaying) {
            delay(1000)
            currentPhoto?.let(onStageDelete)
        }
    }
    BackHandler(onBack = onClose)
    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 1) { page ->
                PhotoPageImage(photos[page])
            }
            Row(Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) { Icon(Icons.Filled.Close, "关闭", tint = Color.White) }
                Text("${pagerState.currentPage + 1} / ${photos.size}", color = Color.White, modifier = Modifier.weight(1f))
                IconButton(onClick = onOpenTrash) {
                    Box(Modifier.size(40.dp)) {
                        Icon(Icons.Filled.DeleteOutline, "打开删除暂存区", tint = Color.White, modifier = Modifier.align(Alignment.Center))
                        Surface(
                            color = Color(0xFFFFC107),
                            shape = CircleShape,
                            modifier = Modifier.align(Alignment.TopEnd).width(if (stagedCount > 99) 24.dp else 18.dp).height(18.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(if (stagedCount > 99) "99+" else stagedCount.toString(), style = MaterialTheme.typography.labelSmall, color = Ink, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
        Surface(color = Color(0xFF111111), modifier = Modifier.fillMaxWidth().zIndex(10f)) {
            Column(Modifier.padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 18.dp, vertical = 10.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), contentPadding = PaddingValues(horizontal = 2.dp)) {
                    items(categories, key = { it.name }) { target ->
                        Surface(onClick = { onMove(photos[pagerState.currentPage], target) }, color = Color(0xFF303030), shape = RoundedCornerShape(18.dp)) {
                            Text(target.name, color = Color.White, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (autoPlayEnabled) {
                        IconButton(onClick = { autoPlaying = !autoPlaying }, colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)) {
                            Icon(if (autoPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, if (autoPlaying) "暂停自动播放" else "开始自动播放")
                        }
                    }
                    Button(onClick = {
                        photos.getOrNull(pagerState.currentPage)?.let(onKeep)
                        if (autoPlayEnabled && autoPlaying) {
                            autoPlaying = false
                        } else {
                            autoPlaying = false
                            if (pagerState.currentPage < photos.lastIndex) scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } else onClose()
                        }
                    }, modifier = Modifier.width(72.dp), contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Ink)) { Text("保留", maxLines = 1) }
                    Button(onClick = {
                        autoPlaying = false
                        val photo = photos[pagerState.currentPage]
                        onStageDelete(photo)
                        if (photos.size == 1) onClose() else if (pagerState.currentPage >= photos.lastIndex) scope.launch { pagerState.animateScrollToPage(photos.lastIndex - 1) }
                    }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3A3A), contentColor = Color.White)) {
                        Icon(Icons.Filled.DeleteOutline, null)
                        Text("暂存删除", maxLines = 1, softWrap = false, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoPageImage(photo: PhotoItem) {
    val context = LocalContext.current
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = photo.uri) { value = withContext(Dispatchers.IO) { decodePhotoBitmap(context, photo, 2048) } }
    val bitmap: Bitmap? = bitmapState.value
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (bitmap != null) Image(bitmap = bitmap.asImageBitmap(), contentDescription = photo.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
    }
}

private fun decodePhotoBitmap(context: Context, photo: PhotoItem, maxDimension: Int): Bitmap? = runCatching {
    val source = ImageDecoder.createSource(context.contentResolver, photo.uri)
    ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
        val sourceMax = maxOf(info.size.width, info.size.height)
        if (sourceMax > maxDimension) {
            val scale = maxDimension.toFloat() / sourceMax
            decoder.setTargetSize(
                (info.size.width * scale).toInt().coerceAtLeast(1),
                (info.size.height * scale).toInt().coerceAtLeast(1),
            )
        }
    }
}.getOrNull()

@Composable
private fun OrganizePhotosScreen(
    photos: List<PhotoItem>,
    categories: List<Category>,
    onBack: () -> Unit,
    onStageDelete: (List<PhotoItem>) -> Unit,
    onTrash: () -> Unit,
    trashCount: Int,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val sourcePhotos = remember { photos }
    var selected by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var selecting by remember { mutableStateOf(false) }
    var viewerIndex by remember { mutableStateOf<Int?>(null) }
    var mode by remember { mutableStateOf("随机整理") }
    var countText by remember { mutableStateOf("20") }
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    val count = countText.toIntOrNull()?.coerceIn(1, 500) ?: 20
    var batchPhotos by remember { mutableStateOf(sourcePhotos.shuffled().take(count)) }
    var visiblePhotos by remember { mutableStateOf(batchPhotos) }
    var processedUris by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    val monthOptions = remember(sourcePhotos) {
        sourcePhotos.mapNotNull { if (it.dateMillis > 0) SimpleDateFormat("yyyy年MM月", Locale.CHINA).format(Date(it.dateMillis)) else null }.distinct()
    }
    LaunchedEffect(mode, countText, selectedMonth, sourcePhotos) {
        processedUris = emptySet()
        val nextBatch = if (mode == "随机整理") sourcePhotos.shuffled().take(count)
        else sourcePhotos.filter { item -> selectedMonth == null || (item.dateMillis > 0 && SimpleDateFormat("yyyy年MM月", Locale.CHINA).format(Date(item.dateMillis)) == selectedMonth) }
        batchPhotos = nextBatch
        visiblePhotos = nextBatch
    }
    val remainingCount = sourcePhotos.count { it.uri !in processedUris }
    val nextBatchCount = minOf(count, remainingCount)
    val batchComplete = batchPhotos.isNotEmpty() && batchPhotos.all { it.uri in processedUris }
    BackHandler(onBack = onBack)
    Scaffold(containerColor = Paper, topBar = {
        Row(Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") }
            Column(Modifier.weight(1f)) { Text("整理照片", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("本次 ${photos.size} 张", style = MaterialTheme.typography.labelSmall, color = Muted) }
            if (trashCount > 0 || selecting) IconButton(onClick = {
                if (!selecting) {
                    onTrash()
                } else if (selected.isEmpty()) {
                    Toast.makeText(context, "请先选择照片", Toast.LENGTH_SHORT).show()
                } else {
                    val staged = visiblePhotos.filter { it.uri in selected }
                    processedUris = processedUris + staged.map { it.uri }
                    visiblePhotos = visiblePhotos.filterNot { it.uri in selected }
                    onStageDelete(staged)
                    selected = emptySet()
                    selecting = false
                    Toast.makeText(context, "已放入删除暂存区", Toast.LENGTH_SHORT).show()
                }
            }) { Icon(Icons.Filled.DeleteOutline, if (selecting) "暂存所选照片" else "删除暂存区", tint = Moss) }
        }
    }) { p ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(p)
                .pointerInput(focusManager) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial, requireUnconsumed = false)
                        focusManager.clearFocus()
                    }
                }
        ) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("随机整理", "按月份整理").forEach { item ->
                    TextButton(onClick = { mode = item }) { Text(item, color = if (mode == item) Moss else Muted, fontWeight = if (mode == item) FontWeight.Bold else FontWeight.Normal) }
                }
            }
            if (mode == "随机整理") {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("本次整理", style = MaterialTheme.typography.bodySmall, color = Muted)
                    OutlinedTextField(value = countText, onValueChange = { countText = it.filter(Char::isDigit).take(3) }, singleLine = true, modifier = Modifier.padding(start = 8.dp).width(72.dp).height(50.dp))
                    Text("张", style = MaterialTheme.typography.bodySmall, color = Muted, modifier = Modifier.padding(start = 6.dp))
                }
            } else if (monthOptions.isNotEmpty()) {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(monthOptions) { month ->
                        TextButton(onClick = { selectedMonth = month }) { Text(month, color = if (selectedMonth == month) Moss else Muted, fontWeight = if (selectedMonth == month) FontWeight.Bold else FontWeight.Normal) }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (selecting) "已选 ${selected.size} 张" else "共 ${visiblePhotos.size} 张，点击照片查看", style = MaterialTheme.typography.bodySmall, color = Muted, modifier = Modifier.weight(1f))
                IconButton(onClick = { selecting = !selecting; selected = emptySet() }) { Icon(if (selecting) Icons.Filled.Close else Icons.Filled.CheckBox, if (selecting) "取消选择" else "选择照片", tint = Moss) }
            }
            if (mode == "随机整理" && batchComplete && remainingCount > 0) {
                Button(
                    onClick = {
                        val nextBatch = sourcePhotos.filterNot { it.uri in processedUris }.shuffled().take(count)
                        batchPhotos = nextBatch
                        visiblePhotos = nextBatch
                        selected = emptySet()
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).height(40.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("看下一组（展示 $nextBatchCount 张照片）", maxLines = 1)
                }
            }
            if (visiblePhotos.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("没有找到可整理的照片", color = Muted) }
            else LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxSize(), contentPadding = PaddingValues(4.dp)) {
                items(visiblePhotos, key = { it.uri.toString() }) { photo ->
                    PhotoThumb(photo, photo.uri in selected) {
                        if (selecting) selected = if (photo.uri in selected) selected - photo.uri else selected + photo.uri
                        else {
                            viewerIndex = visiblePhotos.indexOfFirst { it.uri == photo.uri }
                        }
                    }
                }
            }
        }
    }
    viewerIndex?.let { index ->
        PhotoViewerScreen(
            photos = visiblePhotos,
            initialIndex = index,
            categories = categories,
            onClose = { viewerIndex = null },
            onOpenTrash = { viewerIndex = null; onTrash() },
            onStageDelete = { photo ->
                processedUris = processedUris + photo.uri
                visiblePhotos = visiblePhotos.filterNot { it.uri == photo.uri }
                onStageDelete(listOf(photo))
            },
            onKeep = { photo -> processedUris = processedUris + photo.uri },
            onMove = { photo, target -> if (movePhoto(context, photo, target)) viewerIndex = null },
            stagedCount = trashCount,
            autoPlayEnabled = true,
        )
    }
}

@Composable
private fun TrashScreen(
    photos: List<PhotoItem>,
    categoryName: String?,
    onBack: () -> Unit,
    onRestore: (List<PhotoItem>) -> Unit,
    onDelete: (List<PhotoItem>) -> Unit,
) {
    var selected by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var confirmDelete by remember { mutableStateOf(false) }
    BackHandler(onBack = onBack)
    Scaffold(containerColor = Paper, topBar = {
        Row(Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") }
            Text(categoryName?.let { "$it 删除暂存区" } ?: "删除暂存区", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("${photos.size} 张", style = MaterialTheme.typography.labelMedium, color = Muted)
        }
    }, bottomBar = {
        if (photos.isNotEmpty()) Row(Modifier.fillMaxWidth().background(Color.White).padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 22.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            TextButton(onClick = { onRestore(if (selected.isEmpty()) photos else photos.filter { it.uri in selected }); selected = emptySet() }) { Icon(Icons.Filled.Undo, null); Text("  恢复") }
            TextButton(onClick = { confirmDelete = true }) { Icon(Icons.Filled.Delete, null); Text("  删除") }
        }
    }) { p ->
        if (photos.isEmpty()) Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { Text(if (categoryName == null) "暂存区为空" else "这个分类的暂存区为空", color = Muted) }
        else LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(4.dp)) {
            items(photos, key = { it.uri.toString() }) { photo -> PhotoThumb(photo, photo.uri in selected) { selected = if (photo.uri in selected) selected - photo.uri else selected + photo.uri } }
        }
    }
    if (confirmDelete) AlertDialog(onDismissRequest = { confirmDelete = false }, title = { Text("确认永久删除？") }, text = { Text(if (selected.isEmpty()) "将删除暂存区内的全部照片。" else "将删除已选择的 ${selected.size} 张照片。") }, confirmButton = { TextButton(onClick = { onDelete(if (selected.isEmpty()) photos else photos.filter { it.uri in selected }); selected = emptySet(); confirmDelete = false }) { Text("确认删除") } }, dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("取消") } })
}

@Composable
private fun VideoReviewScreen(
    videos: List<VideoItem>,
    stagedCount: Int,
    onBack: () -> Unit,
    onTrash: () -> Unit,
    onStageDelete: (VideoItem) -> Unit,
) {
    val context = LocalContext.current
    var currentUri by remember { mutableStateOf(videos.firstOrNull()?.uri) }
    var heldSpeed by remember { mutableStateOf<Int?>(null) }
    var playbackPosition by remember { mutableStateOf(0L) }
    var playbackDuration by remember { mutableStateOf(0L) }
    var sliderPosition by remember { mutableStateOf(0f) }
    var isScrubbing by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    LaunchedEffect(videos) {
        if (videos.none { it.uri == currentUri }) currentUri = videos.firstOrNull()?.uri
    }
    val currentIndex = videos.indexOfFirst { it.uri == currentUri }
    val currentVideo = videos.getOrNull(currentIndex)
    val player = remember { ExoPlayer.Builder(context).build() }
    LaunchedEffect(currentVideo?.uri) {
        player.setPlaybackParameters(PlaybackParameters(1f))
        heldSpeed = null
        playbackPosition = 0L
        playbackDuration = 0L
        sliderPosition = 0f
        if (currentVideo != null) {
            player.setMediaItem(ExoMediaItem.fromUri(currentVideo.uri))
            player.prepare()
            player.playWhenReady = true
        } else {
            player.stop()
            player.clearMediaItems()
        }
    }
    LaunchedEffect(currentVideo?.uri) {
        while (currentVideo != null) {
            playbackDuration = player.duration.coerceAtLeast(0L)
            isPlaying = player.isPlaying
            if (!isScrubbing) {
                playbackPosition = player.currentPosition.coerceAtLeast(0L)
                sliderPosition = if (playbackDuration > 0L) playbackPosition.toFloat() / playbackDuration else 0f
            }
            delay(200)
        }
        isPlaying = false
    }
    DisposableEffect(player) { onDispose { player.release() } }
    BackHandler(onBack = onBack)

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (currentVideo != null) {
            AndroidView(
                factory = { ctx -> PlayerView(ctx).apply { useController = false; resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT; this.player = player } },
                update = { it.player = player },
                modifier = Modifier.fillMaxSize()
                    .pointerInput(currentVideo.uri) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var totalVertical = 0f
                            var totalHorizontal = 0f
                            var fastForwarding = false
                            var released = false
                            val speedStep = 48.dp.toPx()
                            while (!released) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                totalVertical += change.position.y - change.previousPosition.y
                                totalHorizontal += change.position.x - change.previousPosition.x
                                val moved = kotlin.math.hypot(totalVertical.toDouble(), totalHorizontal.toDouble()).toFloat()
                                if (!fastForwarding && change.pressed && moved < viewConfiguration.touchSlop && change.uptimeMillis - down.uptimeMillis >= viewConfiguration.longPressTimeoutMillis) {
                                    fastForwarding = true
                                    heldSpeed = 2
                                }
                                if (fastForwarding) {
                                    val speed = (2f - totalVertical / speedStep).roundToInt().coerceIn(1, 5)
                                    player.setPlaybackParameters(PlaybackParameters(speed.toFloat()))
                                    heldSpeed = speed
                                    change.consume()
                                } else if (kotlin.math.abs(totalVertical) >= viewConfiguration.touchSlop) {
                                    change.consume()
                                }
                                released = !change.pressed
                            }
                            if (fastForwarding) {
                                player.setPlaybackParameters(PlaybackParameters(1f))
                                heldSpeed = null
                            } else if (kotlin.math.abs(totalVertical) > 80f) {
                                val nextIndex = if (totalVertical < 0f) currentIndex + 1 else currentIndex - 1
                                currentUri = videos.getOrNull(nextIndex)?.uri ?: currentUri
                            }
                        }
                    },
            )
            Column(Modifier.fillMaxSize().padding(WindowInsets.safeDrawing.asPaddingValues())) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Color.White) }
                    Column(Modifier.weight(1f)) {
                        Text("整理视频", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${currentIndex + 1} / ${videos.size}", color = Color.White.copy(alpha = .75f), style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = onTrash) {
                        Box {
                            Icon(Icons.Filled.DeleteOutline, "视频删除暂存区", tint = Color.White)
                            if (stagedCount > 0) Surface(color = Color(0xFFFFC107), shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).size(16.dp)) {
                                Box(contentAlignment = Alignment.Center) { Text(stagedCount.coerceAtMost(99).toString(), style = MaterialTheme.typography.labelSmall, color = Ink) }
                            }
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                heldSpeed?.let { speed ->
                    Text("x$speed", color = Color.White, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally).background(Color.Black.copy(alpha = .65f), CircleShape).padding(horizontal = 22.dp, vertical = 10.dp))
                }
                Column(Modifier.fillMaxWidth().background(Color(0xFF111111)).padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Text(currentVideo.name, color = Color.White, maxLines = 1, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(formatDuration(if (isScrubbing) (sliderPosition * playbackDuration).toLong() else playbackPosition), color = Color.White, style = MaterialTheme.typography.labelSmall)
                        Slider(
                            value = sliderPosition.coerceIn(0f, 1f),
                            onValueChange = { value -> isScrubbing = true; sliderPosition = value },
                            onValueChangeFinished = {
                                if (playbackDuration > 0L) player.seekTo((sliderPosition * playbackDuration).toLong())
                                playbackPosition = (sliderPosition * playbackDuration).toLong()
                                isScrubbing = false
                            },
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(thumbColor = Color(0xFFFFC107), activeTrackColor = Color(0xFFFFC107), inactiveTrackColor = Color.White.copy(alpha = .35f)),
                        )
                        Text(formatDuration(playbackDuration), color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (player.isPlaying) player.pause() else player.play(); isPlaying = player.isPlaying }) {
                            Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, if (isPlaying) "暂停视频" else "播放视频", tint = Color.White)
                        }
                        TextButton(onClick = { currentUri = videos.getOrNull(currentIndex + 1)?.uri }, modifier = Modifier.weight(1f)) { Text("保留并下一个", color = Color.White) }
                        Button(onClick = {
                            val nextVideo = videos.getOrNull(currentIndex + 1) ?: videos.getOrNull(currentIndex - 1)
                            currentUri = nextVideo?.uri
                            onStageDelete(currentVideo)
                        }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF343434), contentColor = Color.White)) {
                            Icon(Icons.Filled.DeleteOutline, null)
                            Text("  暂存删除")
                        }
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(WindowInsets.safeDrawing.asPaddingValues()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Filled.VideoLibrary, null, tint = Color.White.copy(alpha = .65f), modifier = Modifier.size(48.dp))
                Text(if (videos.isEmpty()) "手机里没有可整理的视频" else "视频已整理完", color = Color.White, modifier = Modifier.padding(top = 12.dp))
                Row(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onBack) { Text("返回", color = Color.White) }
                    if (stagedCount > 0) TextButton(onClick = onTrash) { Text("查看暂存区 ($stagedCount)", color = Color(0xFFFFC107)) }
                }
            }
        }
    }
}

@Composable
private fun VideoTrashScreen(
    videos: List<VideoItem>,
    onBack: () -> Unit,
    onRestore: (List<VideoItem>) -> Unit,
    onDelete: (List<VideoItem>) -> Unit,
) {
    var selected by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var confirmDelete by remember { mutableStateOf(false) }
    BackHandler(onBack = onBack)
    Scaffold(containerColor = Paper, topBar = {
        Row(Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") }
            Text("视频删除暂存区", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("${videos.size} 个", style = MaterialTheme.typography.labelMedium, color = Muted)
        }
    }, bottomBar = {
        if (videos.isNotEmpty()) Row(Modifier.fillMaxWidth().background(Color.White).padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 22.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            TextButton(onClick = { onRestore(if (selected.isEmpty()) videos else videos.filter { it.uri in selected }); selected = emptySet() }) { Icon(Icons.Filled.Undo, null); Text("  恢复") }
            TextButton(onClick = { confirmDelete = true }) { Icon(Icons.Filled.Delete, null); Text("  删除") }
        }
    }) { padding ->
        if (videos.isEmpty()) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("视频暂存区为空", color = Muted) }
        else LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(videos, key = { it.uri.toString() }) { video ->
                Surface(onClick = { selected = if (video.uri in selected) selected - video.uri else selected + video.uri }, color = if (video.uri in selected) Mint else Color.White, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (video.uri in selected) Icons.Filled.Check else Icons.Filled.VideoLibrary, if (video.uri in selected) "已选" else "视频", tint = Moss)
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(video.name, maxLines = 1, fontWeight = FontWeight.Medium)
                            Text("${formatDuration(video.durationMillis)}  ·  ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(video.dateMillis))}", style = MaterialTheme.typography.labelSmall, color = Muted)
                        }
                    }
                }
            }
        }
    }
    if (confirmDelete) AlertDialog(onDismissRequest = { confirmDelete = false }, title = { Text("确认永久删除？") }, text = { Text(if (selected.isEmpty()) "将删除暂存区内的全部视频。" else "将删除已选择的 ${selected.size} 个视频。") }, confirmButton = { TextButton(onClick = { onDelete(if (selected.isEmpty()) videos else videos.filter { it.uri in selected }); selected = emptySet(); confirmDelete = false }) { Text("确认删除") } }, dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("取消") } })
}

private fun formatDuration(durationMillis: Long): String {
    val seconds = durationMillis / 1000
    return "%d:%02d".format(seconds / 60, seconds % 60)
}

@Composable
private fun ImportFoldersScreen(
    folders: List<ImportFolder>,
    selected: Set<Uri>,
    onBack: () -> Unit,
    onToggle: (ImportFolder) -> Unit,
    onSelectAll: () -> Unit,
    onImport: () -> Unit,
) {
    BackHandler(onBack = onBack)
    Scaffold(containerColor = Paper, topBar = {
        Row(Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") }
            Column(Modifier.weight(1f)) {
                Text("导入 Pictures 文件夹", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("选择后会按原文件夹名称加入分类，不复制照片", style = MaterialTheme.typography.labelSmall, color = Muted)
            }
            IconButton(onClick = onSelectAll) { Icon(Icons.Filled.Check, "全选", tint = Moss) }
        }
    }, bottomBar = {
        Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onImport, enabled = selected.isNotEmpty(), modifier = Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 18.dp, vertical = 10.dp), shape = RoundedCornerShape(8.dp)) {
                Text("导入已选 ${selected.size} 个文件夹")
            }
        }
    }) { p ->
        if (folders.isEmpty()) Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { Text("这个文件夹下没有图片子文件夹", color = Muted) }
        else LazyColumn(Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(folders, key = { it.uri.toString() }) { folder ->
                val checked = folder.uri in selected
                Surface(onClick = { onToggle(folder) }, color = if (checked) Mint else Color.White, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (checked) Icons.Filled.Check else Icons.Filled.RadioButtonUnchecked, if (checked) "已选" else "未选", tint = Moss)
                        Column(Modifier.padding(start = 12.dp).weight(1f)) {
                            Text(folder.name, fontWeight = FontWeight.Medium)
                            Text("${folder.imageCount} 张图片", style = MaterialTheme.typography.labelSmall, color = Muted)
                        }
                    }
                }
            }
        }
    }
}

private fun queryPhotos(context: Context, category: Category): List<PhotoItem> {
    val uris = mutableListOf<PhotoItem>()
    if (category.folderUri != null) runCatching {
        val folderUri = Uri.parse(category.folderUri)
        val folderDocumentId = DocumentsContract.getDocumentId(folderUri)
        val children = DocumentsContract.buildChildDocumentsUriUsingTree(folderUri, folderDocumentId)
        context.contentResolver.query(children, arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_MIME_TYPE, DocumentsContract.Document.COLUMN_LAST_MODIFIED), null, null, null)?.use { c ->
            while (c.moveToNext()) if (c.getString(2)?.startsWith("image/") == true) {
                val id = c.getString(0)
                uris += PhotoItem(DocumentsContract.buildDocumentUriUsingTree(folderUri, id), c.getString(1), category.folderUri, c.getLong(3))
            }
        }
    } else context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED), "${MediaStore.Images.Media.RELATIVE_PATH}=?", arrayOf("DCIM/${category.name}/"), "${MediaStore.Images.Media.DATE_ADDED} DESC")?.use { c -> while (c.moveToNext()) uris += PhotoItem(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c.getString(0)), c.getString(1), "DCIM/${category.name}", c.getLong(2) * 1000L) }
    return uris
}

private fun folderDisplayName(context: Context, treeUri: Uri): String? = runCatching {
    val documentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, DocumentsContract.getTreeDocumentId(treeUri))
    context.contentResolver.query(documentUri, arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME), null, null, null)?.use { c -> if (c.moveToFirst()) c.getString(0) else null }
}.getOrNull()

private fun queryPictureFolders(context: Context, picturesTree: Uri): List<ImportFolder> {
    val result = mutableListOf<ImportFolder>()
    runCatching {
        val childUri = DocumentsContract.buildChildDocumentsUriUsingTree(picturesTree, DocumentsContract.getTreeDocumentId(picturesTree))
        context.contentResolver.query(childUri, arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_MIME_TYPE), null, null, DocumentsContract.Document.COLUMN_DISPLAY_NAME)?.use { c ->
            while (c.moveToNext()) {
                if (c.getString(2) != DocumentsContract.Document.MIME_TYPE_DIR) continue
                val id = c.getString(0)
                val folderUri = DocumentsContract.buildDocumentUriUsingTree(picturesTree, id)
                val imageCount = countImagesInTree(context, picturesTree, id)
                if (imageCount > 0) result += ImportFolder(c.getString(1), folderUri, imageCount)
            }
        }
    }
    return result
}

private fun countImagesInTree(context: Context, treeUri: Uri, documentId: String): Int {
    val childUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, documentId)
    var count = 0
    runCatching {
        context.contentResolver.query(childUri, arrayOf(DocumentsContract.Document.COLUMN_MIME_TYPE), null, null, null)?.use { c ->
            while (c.moveToNext()) if (c.getString(0)?.startsWith("image/") == true) count++
        }
    }
    return count
}

private fun queryAllPhotos(context: Context, categories: List<Category>): List<PhotoItem> {
    val all = mutableListOf<PhotoItem>()
    runCatching {
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.RELATIVE_PATH),
            null, null, "${MediaStore.Images.Media.DATE_ADDED} DESC",
        )?.use { c ->
            while (c.moveToNext()) all += PhotoItem(
                Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c.getString(0)),
                c.getString(1),
                c.getString(3),
                c.getLong(2) * 1000L,
            )
        }
    }
    return (all + categories.flatMap { queryPhotos(context, it) }).distinctBy { it.uri }
}

private fun queryAllVideos(context: Context): List<VideoItem> {
    val videos = mutableListOf<VideoItem>()
    runCatching {
        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATE_ADDED),
            null, null, "${MediaStore.Video.Media.DATE_ADDED} DESC",
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                videos += VideoItem(
                    uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cursor.getString(0)),
                    name = cursor.getString(1) ?: "video",
                    durationMillis = cursor.getLong(2),
                    dateMillis = cursor.getLong(3) * 1000L,
                )
            }
        }
    }
    return videos
}

private fun isMediaStorePhoto(context: Context, photo: PhotoItem): Boolean =
    photo.uri.authority == "media" && !DocumentsContract.isDocumentUri(context, photo.uri)

private fun deletePhoto(context: Context, photo: PhotoItem): Boolean = runCatching {
    if (DocumentsContract.isDocumentUri(context, photo.uri)) DocumentsContract.deleteDocument(context.contentResolver, photo.uri)
    else context.contentResolver.delete(photo.uri, null, null) > 0
}.getOrDefault(false)

private fun movePhoto(context: Context, photo: PhotoItem, target: Category): Boolean {
    val targetKey = target.folderUri ?: "DCIM/${target.name}"
    if (photo.sourceKey == targetKey) return true
    val targetUri = runCatching {
        if (target.folderUri != null) {
            DocumentsContract.createDocument(context.contentResolver, Uri.parse(target.folderUri), "image/jpeg", photo.name)
        } else {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, photo.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/${target.name}")
            }
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }
    }.getOrNull() ?: return false
    val copied = runCatching {
        context.contentResolver.openInputStream(photo.uri)!!.use { input ->
            context.contentResolver.openOutputStream(targetUri)!!.use { output -> input.copyTo(output) }
        }
        true
    }.getOrDefault(false)
    if (!copied) {
        runCatching { deletePhoto(context, PhotoItem(targetUri, photo.name)) }
        return false
    }
    deletePhoto(context, photo)
    return true
}

private fun safeFolderName(value: String): String = value
    .trim()
    .replace(Regex("[/\\\\:*?\"<>|]"), "_")
    .trim('.')
