package com.localalbum.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.LruCache
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.Job
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.IntOffset
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.preload.DefaultPreloadManager
import androidx.media3.exoplayer.source.preload.TargetPreloadStatusControl
import androidx.media3.ui.PlayerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

private val Ink = Color(0xFF1C2421)
private val Moss = Color(0xFF315D4A)
private val Mint = Color(0xFFE4F0E8)
private val Paper = Color(0xFFF5F7F4)
private val Muted = Color(0xFF707A74)

private data class Category(val name: String, val folderUri: String? = null)
private data class ImportFolder(val name: String, val uri: Uri, val imageCount: Int)
private data class PhotoItem(val uri: Uri, val name: String, val sourceKey: String? = null, val dateMillis: Long = 0L, val sizeBytes: Long = 0L)
private data class VideoItem(val uri: Uri, val name: String, val durationMillis: Long, val dateMillis: Long, val sizeBytes: Long = 0L, val sourceKey: String? = null)
private data class PendingVideoMove(val video: VideoItem, val target: Category)
private enum class VideoMoveResult { MOVED, NEEDS_CONFIRMATION, FAILED }
private data class MediaStorage(
    val photoCount: Int = 0,
    val videoCount: Int = 0,
    val photoBytes: Long = 0L,
    val videoBytes: Long = 0L,
)

private sealed interface UndoableStage {
    val organizedAmount: Int
    data class Photos(val items: List<PhotoItem>, val reviewedItems: List<PhotoItem>) : UndoableStage {
        override val organizedAmount: Int = reviewedItems.size
    }
    data class Video(val item: VideoItem, override val organizedAmount: Int) : UndoableStage
}

private fun PhotoItem.belongsTo(category: Category): Boolean =
    sourceKey == (category.folderUri ?: "DCIM/${category.name}")

private enum class AppScreen { HOME, MORE, CATEGORY, ORGANIZE, TRASH, IMPORT, VIDEOS, VIDEO_TRASH }

private const val MODE_RANDOM = "\u968f\u673a\u6574\u7406"
private const val MODE_MONTH = "\u6309\u6708\u4efd\u6574\u7406"
private const val MODE_ALL = "\u5168\u90e8\u6574\u7406"
private const val MONTH_FORMAT = "yyyy\u5e74MM\u6708"

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
    var trashReturnScreen by rememberSaveable { mutableStateOf(AppScreen.HOME) }
    var restorePhotoGroupUris by remember { mutableStateOf<List<String>?>(null) }
    var restoreVideoGroupUris by remember { mutableStateOf<List<String>?>(null) }
    var organizePhotos by remember { mutableStateOf<List<PhotoItem>>(emptyList()) }
    var trash by remember { mutableStateOf<List<PhotoItem>>(emptyList()) }
    var videos by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var videoTrash by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var organizedCount by remember { mutableStateOf(StatsStore.organized(context)) }
    var deletedCount by remember { mutableStateOf(StatsStore.deleted(context)) }
    var clearedBytes by remember { mutableStateOf(StatsStore.deletedBytes(context)) }
    var reviewedPhotoKeys by remember { mutableStateOf(ReviewedStore.photos(context)) }
    var reviewedVideoKeys by remember { mutableStateOf(ReviewedStore.videos(context)) }
    var showRoundCompleteDialog by remember { mutableStateOf(false) }
    var loadingOrganize by remember { mutableStateOf(false) }
    var photoCatalog by remember { mutableStateOf<List<PhotoItem>?>(null) }
    var videoCatalog by remember { mutableStateOf<List<VideoItem>?>(null) }
    var mediaStorage by remember { mutableStateOf(MediaStorage()) }
    var mediaCatalogLoading by remember { mutableStateOf(false) }
    var mediaChangeToken by remember { mutableStateOf(0) }
    var openOrganizerWhenLoaded by remember { mutableStateOf(false) }
    var pendingPreview by remember { mutableStateOf<Category?>(null) }
    var pendingOrganize by remember { mutableStateOf(false) }
    var pendingCategoryName by remember { mutableStateOf("") }
    var pendingDeleteCategory by remember { mutableStateOf<Category?>(null) }
    var importFolders by remember { mutableStateOf<List<ImportFolder>>(emptyList()) }
    var importSelected by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var pendingSystemDelete by remember { mutableStateOf<List<PhotoItem>>(emptyList()) }
    var pendingVideoDelete by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var pendingVideoMove by remember { mutableStateOf<PendingVideoMove?>(null) }
    val undoableStages = remember { mutableStateListOf<UndoableStage>() }
    var pendingVideoReview by remember { mutableStateOf(false) }
    fun recordOrganized(amount: Int) {
        if (amount != 0) {
            StatsStore.addOrganized(context, amount)
            organizedCount = (organizedCount + amount).coerceAtLeast(0)
        }
    }
    fun recordDeleted(amount: Int, bytes: Long = 0L) {
        if (amount > 0) {
            StatsStore.addDeleted(context, amount)
            deletedCount += amount
        }
        if (bytes > 0L) {
            StatsStore.addDeletedBytes(context, bytes)
            clearedBytes += bytes
        }
    }
    fun markPhotosReviewed(items: Collection<PhotoItem>): List<PhotoItem> {
        val newItems = items.filter { it.uri.toString() !in reviewedPhotoKeys }
        if (newItems.isEmpty()) return emptyList()
        newItems.forEach { ReviewedStore.markPhoto(context, it.uri) }
        reviewedPhotoKeys = reviewedPhotoKeys + newItems.map { it.uri.toString() }
        recordOrganized(newItems.size)
        return newItems
    }
    fun markVideoReviewed(item: VideoItem): Boolean {
        val key = item.uri.toString()
        if (key in reviewedVideoKeys) return false
        ReviewedStore.markVideo(context, item.uri)
        reviewedVideoKeys = reviewedVideoKeys + key
        recordOrganized(1)
        return true
    }
    fun undoLastStage() {
        val action = undoableStages.removeLastOrNull() ?: return
        when (action) {
            is UndoableStage.Photos -> {
                trash = (trash + action.items).distinctBy { it.uri }
                if (action.reviewedItems.isNotEmpty()) {
                    action.reviewedItems.forEach { ReviewedStore.unmarkPhoto(context, it.uri) }
                    reviewedPhotoKeys = reviewedPhotoKeys - action.reviewedItems.map { it.uri.toString() }.toSet()
                }
                recordOrganized(-action.organizedAmount)
            }
            is UndoableStage.Video -> {
                videoTrash = videoTrash.filterNot { it.uri == action.item.uri }
                ReviewedStore.unmarkVideo(context, action.item.uri)
                reviewedVideoKeys = reviewedVideoKeys - action.item.uri.toString()
                recordOrganized(-action.organizedAmount)
            }
        }
        Toast.makeText(context, "已撤回上一次暂存删除", Toast.LENGTH_SHORT).show()
    }
    val deleteRequest = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            trash = trash.filterNot { item -> pendingSystemDelete.any { it.uri == item.uri } }
            recordDeleted(pendingSystemDelete.size, pendingSystemDelete.sumOf { it.sizeBytes })
            Toast.makeText(context, "照片已删除", Toast.LENGTH_SHORT).show()
        }
        pendingSystemDelete = emptyList()
    }
    val videoDeleteRequest = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            videos = videos.filterNot { item -> pendingVideoDelete.any { it.uri == item.uri } }
            videoTrash = videoTrash.filterNot { item -> pendingVideoDelete.any { it.uri == item.uri } }
            recordDeleted(pendingVideoDelete.size, pendingVideoDelete.sumOf { it.sizeBytes })
            Toast.makeText(context, "视频已删除", Toast.LENGTH_SHORT).show()
        }
        pendingVideoDelete = emptyList()
    }
    val videoMoveRequest = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        val move = pendingVideoMove
        if (move != null) {
            val moved = result.resultCode == android.app.Activity.RESULT_OK && runCatching {
                moveVideoToCategory(context, move.video, move.target)
            }.getOrDefault(false)
            if (moved) {
                videos = videos.filterNot { it.uri == move.video.uri }
                videoCatalog = videoCatalog?.filterNot { it.uri == move.video.uri }
                markVideoReviewed(move.video)
                Toast.makeText(context, "\u89c6\u9891\u5df2\u79fb\u52a8\u5230${move.target.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "\u89c6\u9891\u4fdd\u7559\u5728\u539f\u5206\u7c7b", Toast.LENGTH_SHORT).show()
            }
            pendingVideoMove = null
        }
    }
    val photoPermission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
    val videoPermission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE
    suspend fun refreshMediaCatalog() {
        val canReadPhotos = ContextCompat.checkSelfPermission(context, photoPermission) == PackageManager.PERMISSION_GRANTED
        val canReadVideos = ContextCompat.checkSelfPermission(context, videoPermission) == PackageManager.PERMISSION_GRANTED
        if (!canReadPhotos && !canReadVideos) {
            photoCatalog = null
            videoCatalog = null
            mediaStorage = MediaStorage()
            mediaCatalogLoading = false
            return
        }
        mediaCatalogLoading = true
        try {
            coroutineScope {
                val photos = async(Dispatchers.IO) {
                    if (canReadPhotos) queryAllPhotos(context, categories) else emptyList()
                }
                val loadedVideos = async(Dispatchers.IO) {
                    if (canReadVideos) queryAllVideos(context) else emptyList()
                }
                val loadedPhotos = photos.await()
                val loadedVideoList = loadedVideos.await()
                photoCatalog = if (canReadPhotos) loadedPhotos else null
                videoCatalog = if (canReadVideos) loadedVideoList else null
                mediaStorage = MediaStorage(
                    photoCount = loadedPhotos.size,
                    videoCount = loadedVideoList.size,
                    photoBytes = loadedPhotos.sumOf { it.sizeBytes },
                    videoBytes = loadedVideoList.sumOf { it.sizeBytes },
                )
            }
        } finally {
            mediaCatalogLoading = false
        }
    }

    DisposableEffect(context) {
        val handler = Handler(Looper.getMainLooper())
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                mediaChangeToken++
            }
        }
        context.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, observer)
        context.contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, observer)
        onDispose { context.contentResolver.unregisterContentObserver(observer) }
    }

    LaunchedEffect(categories, mediaChangeToken) {
        if (mediaChangeToken > 0) delay(300)
        refreshMediaCatalog()
        if (openOrganizerWhenLoaded && photoCatalog != null) {
            organizePhotos = photoCatalog.orEmpty().filterNot { it.uri.toString() in reviewedPhotoKeys }
            screen = AppScreen.ORGANIZE
            loadingOrganize = false
            openOrganizerWhenLoaded = false
        }
    }

    LaunchedEffect(photoCatalog, videoCatalog, reviewedPhotoKeys, reviewedVideoKeys) {
        val photos = photoCatalog ?: return@LaunchedEffect
        val loadedVideos = videoCatalog ?: return@LaunchedEffect
        val hasMedia = photos.isNotEmpty() || loadedVideos.isNotEmpty()
        val allReviewed = photos.all { it.uri.toString() in reviewedPhotoKeys } &&
            loadedVideos.all { it.uri.toString() in reviewedVideoKeys }
        if (hasMedia && allReviewed && !ReviewedStore.roundCompleteAnnounced(context)) {
            ReviewedStore.setRoundCompleteAnnounced(context, true)
            showRoundCompleteDialog = true
        }
    }

    val videoPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && pendingVideoReview) {
            screenScope.launch {
                refreshMediaCatalog()
                videos = videoCatalog.orEmpty().filterNot { it.uri.toString() in reviewedVideoKeys }
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
            loadingOrganize = true
            screenScope.launch {
                runCatching {
                    refreshMediaCatalog()
                    photoCatalog.orEmpty()
                }
                    .onSuccess {
                        organizePhotos = it.filterNot { photo -> photo.uri.toString() in reviewedPhotoKeys }
                        screen = AppScreen.ORGANIZE
                    }
                    .onFailure { Toast.makeText(context, "读取照片失败，请稍后重试", Toast.LENGTH_LONG).show() }
                loadingOrganize = false
            }
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
    } else {
        val trashedPhotoUris = remember(trash) { trash.mapTo(HashSet()) { it.uri } }
        val trashedVideoUris = remember(videoTrash) { videoTrash.mapTo(HashSet()) { it.uri } }
        when (screen) {
        AppScreen.HOME -> HomeScreen(
            categories = categories,
            selectedCategory = selectedCategory,
            trashCount = trash.size,
            videoTrashCount = videoTrash.size,
            organizeLoading = loadingOrganize,
            onMore = { screen = AppScreen.MORE },
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
                restorePhotoGroupUris = null
                if (loadingOrganize) {
                    Toast.makeText(context, "正在读取照片，请稍候", Toast.LENGTH_SHORT).show()
                } else if (ContextCompat.checkSelfPermission(context, photoPermission) != PackageManager.PERMISSION_GRANTED) {
                    pendingOrganize = true
                    photoPermissionLauncher.launch(photoPermission)
                } else {
                    val cachedPhotos = photoCatalog
                    if (cachedPhotos != null && !mediaCatalogLoading) {
                        organizePhotos = cachedPhotos.filterNot { it.uri.toString() in reviewedPhotoKeys }
                        screen = AppScreen.ORGANIZE
                    } else if (mediaCatalogLoading) {
                        openOrganizerWhenLoaded = true
                        loadingOrganize = true
                        Toast.makeText(context, "正在准备照片列表…", Toast.LENGTH_SHORT).show()
                    } else {
                        loadingOrganize = true
                        screenScope.launch {
                            runCatching {
                                refreshMediaCatalog()
                                photoCatalog.orEmpty()
                            }.onSuccess {
                                organizePhotos = it.filterNot { photo -> photo.uri.toString() in reviewedPhotoKeys }
                                screen = AppScreen.ORGANIZE
                            }.onFailure {
                                Toast.makeText(context, "读取照片失败，请稍后重试", Toast.LENGTH_LONG).show()
                            }
                            loadingOrganize = false
                        }
                    }
                }
            },
            onTrash = { trashCategory = null; trashReturnScreen = AppScreen.HOME; screen = AppScreen.TRASH },
            onImport = { picturesPicker.launch(null) },
            onVideos = {
                restoreVideoGroupUris = null
                if (ContextCompat.checkSelfPermission(context, videoPermission) == PackageManager.PERMISSION_GRANTED) {
                    val cachedVideos = videoCatalog
                    if (cachedVideos != null && !mediaCatalogLoading) {
                        videos = cachedVideos.filterNot { it.uri.toString() in reviewedVideoKeys }
                        screen = AppScreen.VIDEOS
                    } else if (mediaCatalogLoading) {
                        Toast.makeText(context, "正在读取视频，请稍后重试", Toast.LENGTH_SHORT).show()
                    } else {
                        screenScope.launch {
                            refreshMediaCatalog()
                            videos = videoCatalog.orEmpty().filterNot { it.uri.toString() in reviewedVideoKeys }
                            screen = AppScreen.VIDEOS
                        }
                    }
                } else {
                    pendingVideoReview = true
                    videoPermissionLauncher.launch(videoPermission)
                }
            },
            onVideoTrash = { trashCategory = null; trashReturnScreen = AppScreen.HOME; screen = AppScreen.VIDEO_TRASH },
        )
        AppScreen.MORE -> MoreScreen(
            organizedCount = organizedCount,
            deletedCount = deletedCount,
            clearedBytes = clearedBytes,
            storage = mediaStorage,
            onHome = { screen = AppScreen.HOME },
        )
        AppScreen.CATEGORY -> previewCategory?.let { category ->
            CategoryPhotosScreen(
                category = category,
                categories = categories,
                stagedPhotos = trash.filter { it.belongsTo(category) },
                canUndo = undoableStages.isNotEmpty(),
                onUndo = ::undoLastStage,
                onBack = { screen = AppScreen.HOME },
                onShoot = { selectedCategory = category.name; screen = AppScreen.HOME; cameraOpen = true },
                onStageDelete = { photos -> undoableStages.add(UndoableStage.Photos(photos, emptyList())); trash = (trash + photos).distinctBy { it.uri } },
                onTrash = { trashCategory = category; trashReturnScreen = AppScreen.CATEGORY; screen = AppScreen.TRASH },
            )
        }
        AppScreen.ORGANIZE -> OrganizePhotosScreen(
            photos = organizePhotos.filter { photo ->
                photo.uri !in trashedPhotoUris &&
                    (photo.uri.toString() !in reviewedPhotoKeys || restorePhotoGroupUris?.contains(photo.uri.toString()) == true)
            },
            categories = categories,
            initialGroupUris = restorePhotoGroupUris,
            onBack = { restorePhotoGroupUris = null; screen = AppScreen.HOME },
            onStageDelete = { photos -> undoableStages.add(UndoableStage.Photos(photos, markPhotosReviewed(photos))); trash = (trash + photos).distinctBy { it.uri } },
            onUndo = ::undoLastStage,
            canUndo = undoableStages.isNotEmpty(),
            onSeen = ::markPhotosReviewed,
            onTrash = { group ->
                restorePhotoGroupUris = group.map { it.uri.toString() }
                trashCategory = null
                trashReturnScreen = AppScreen.ORGANIZE
                screen = AppScreen.TRASH
            },
            trashCount = trash.size,
        )
        AppScreen.TRASH -> TrashScreen(
            photos = trashCategory?.let { category -> trash.filter { it.belongsTo(category) } } ?: trash,
            categoryName = trashCategory?.name,
            onBack = { screen = trashReturnScreen; trashCategory = null },
            onRestore = { restored -> trash = trash.filterNot { item -> restored.any { it.uri == item.uri } } },
            onDelete = { deleted ->
                val mediaPhotos = if (Build.VERSION.SDK_INT >= 30) deleted.filter { isMediaStorePhoto(context, it) } else emptyList()
                val documentPhotos = deleted.filterNot { it in mediaPhotos }
                val removedDocuments = documentPhotos.filter { deletePhoto(context, it) }
                if (removedDocuments.isNotEmpty()) {
                    trash = trash.filterNot { item -> removedDocuments.any { it.uri == item.uri } }
                    recordDeleted(removedDocuments.size, removedDocuments.sumOf { it.sizeBytes })
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
        AppScreen.VIDEOS -> VideoOrganizerScreen(
            videos = videos.filter { item ->
                item.uri !in trashedVideoUris &&
                    (item.uri.toString() !in reviewedVideoKeys || restoreVideoGroupUris?.contains(item.uri.toString()) == true)
            },
            categories = categories,
            initialGroupUris = restoreVideoGroupUris,
            stagedCount = videoTrash.size,
            canUndo = undoableStages.isNotEmpty(),
            onBack = { restoreVideoGroupUris = null; screen = AppScreen.HOME },
            onTrash = { group ->
                restoreVideoGroupUris = group.map { it.uri.toString() }
                trashCategory = null
                trashReturnScreen = AppScreen.VIDEOS
                screen = AppScreen.VIDEO_TRASH
            },
            onStageDelete = { video -> undoableStages.add(UndoableStage.Video(video, if (markVideoReviewed(video)) 1 else 0)); videoTrash = (videoTrash + video).distinctBy { it.uri } },
            onUndo = ::undoLastStage,
            onKeep = ::markVideoReviewed,
            onMove = { video, target ->
                val targetFolder = mediaStoreTarget(target)
                val sourceVolume = runCatching { MediaStore.getVolumeName(video.uri) }.getOrNull()
                val alreadyInTarget = targetFolder != null && sourceVolume.equals(targetFolder.volumeName, ignoreCase = true) && video.sourceKey?.trim('/') == targetFolder.relativePath.trim('/')
                if (targetFolder == null && target.folderUri == null) {
                    Toast.makeText(context, "\u5206\u7c7b\u4fdd\u5b58\u4f4d\u7f6e\u65e0\u6548\uff0c\u89c6\u9891\u4ecd\u4fdd\u7559\u5728\u539f\u4f4d\u7f6e", Toast.LENGTH_SHORT).show()
                    VideoMoveResult.FAILED
                } else if (alreadyInTarget) {
                    videos = videos.filterNot { it.uri == video.uri }
                    videoCatalog = videoCatalog?.filterNot { it.uri == video.uri }
                    markVideoReviewed(video)
                    VideoMoveResult.MOVED
                } else if (pendingVideoMove != null) {
                    VideoMoveResult.FAILED
                } else {
                    val pending = PendingVideoMove(video, target)
                    fun requestVideoWritePermission(): VideoMoveResult {
                        pendingVideoMove = pending
                        return runCatching {
                            val request = MediaStore.createWriteRequest(context.contentResolver, listOf(video.uri))
                            videoMoveRequest.launch(androidx.activity.result.IntentSenderRequest.Builder(request.intentSender).build())
                            VideoMoveResult.NEEDS_CONFIRMATION
                        }.getOrElse {
                            pendingVideoMove = null
                            VideoMoveResult.FAILED
                        }
                    }
                    if (Build.VERSION.SDK_INT >= 30) {
                        try {
                            if (moveVideoToCategory(context, video, target)) {
                                videos = videos.filterNot { it.uri == video.uri }
                                videoCatalog = videoCatalog?.filterNot { it.uri == video.uri }
                                markVideoReviewed(video)
                                VideoMoveResult.MOVED
                            } else requestVideoWritePermission()
                        } catch (_: SecurityException) {
                            requestVideoWritePermission()
                        }
                    } else {
                        try {
                            if (moveVideoToCategory(context, video, target)) {
                                videos = videos.filterNot { it.uri == video.uri }
                                videoCatalog = videoCatalog?.filterNot { it.uri == video.uri }
                                markVideoReviewed(video)
                                VideoMoveResult.MOVED
                            } else VideoMoveResult.FAILED
                        } catch (error: android.app.RecoverableSecurityException) {
                            pendingVideoMove = pending
                            videoMoveRequest.launch(androidx.activity.result.IntentSenderRequest.Builder(error.userAction.actionIntent.intentSender).build())
                            VideoMoveResult.NEEDS_CONFIRMATION
                        } catch (_: SecurityException) {
                            VideoMoveResult.FAILED
                        }
                    }
                }
            },
        )
        AppScreen.VIDEO_TRASH -> VideoTrashScreen(
            videos = videoTrash,
            onBack = { screen = trashReturnScreen },
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
                    recordDeleted(removed.size, removed.sumOf { it.sizeBytes })
                    if (removed.size < deleted.size) Toast.makeText(context, "部分视频删除失败，仍保留在暂存区", Toast.LENGTH_LONG).show()
                }
            },
        )
    }
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
    if (showRoundCompleteDialog) {
        AlertDialog(
            onDismissRequest = {
                ReviewedStore.setRoundCompleteAnnounced(context, true)
                showRoundCompleteDialog = false
            },
            title = { Text("这一轮整理完成") },
            text = { Text("手机里的照片和视频都已经整理过一遍了，是否要再次整理？") },
            confirmButton = {
                TextButton(onClick = {
                    ReviewedStore.clearReviewed(context)
                    reviewedPhotoKeys = emptySet()
                    reviewedVideoKeys = emptySet()
                    screen = AppScreen.HOME
                    showRoundCompleteDialog = false
                }) { Text("再次整理") }
            },
            dismissButton = {
                TextButton(onClick = { showRoundCompleteDialog = false }) { Text("稍后") }
            },
        )
    }
}

@Composable
private fun HomeScreen(
    categories: List<Category>,
    selectedCategory: String,
    trashCount: Int,
    videoTrashCount: Int,
    organizeLoading: Boolean,
    onMore: () -> Unit,
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
    Scaffold(containerColor = Paper, bottomBar = {
        NavigationBar(containerColor = Color.White) {
            NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Filled.Home, "首页") }, label = { Text("首页") })
            NavigationBarItem(selected = false, onClick = onMore, icon = { Icon(Icons.Filled.MoreHoriz, "更多") }, label = { Text("更多") })
        }
    }) { padding ->
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
                        Button(onClick = onOrganize, enabled = !organizeLoading, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107), contentColor = Ink)) {
                            Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(if (organizeLoading) "  正在读取照片..." else "  整理照片")
                        }
                        IconButton(onClick = onTrash, modifier = Modifier.size(48.dp)) {
                            Box {
                                Icon(Icons.Filled.DeleteOutline, contentDescription = "删除暂存区", tint = Moss)
                                Surface(color = Color(0xFFFFC107), shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).size(16.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text(trashCount.coerceAtMost(99).toString(), style = MaterialTheme.typography.labelSmall, color = Ink) }
                                }
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
                        IconButton(onClick = onVideoTrash, modifier = Modifier.size(48.dp)) {
                            Box {
                                Icon(Icons.Filled.DeleteOutline, contentDescription = "视频删除暂存区", tint = Moss)
                                Surface(color = Color(0xFFFFC107), shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).size(16.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text(videoTrashCount.coerceAtMost(99).toString(), style = MaterialTheme.typography.labelSmall, color = Ink) }
                                }
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
private fun MoreScreen(
    organizedCount: Int,
    deletedCount: Int,
    clearedBytes: Long,
    storage: MediaStorage,
    onHome: () -> Unit,
) {
    BackHandler(onBack = onHome)
    val totalBytes = storage.photoBytes + storage.videoBytes
    Scaffold(containerColor = Paper, bottomBar = {
        NavigationBar(containerColor = Color.White) {
            NavigationBarItem(selected = false, onClick = onHome, icon = { Icon(Icons.Filled.Home, "首页") }, label = { Text("首页") })
            NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Filled.MoreHoriz, "更多") }, label = { Text("更多") })
        }
    }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text("更多", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
                Text("查看本机相册的整理记录和空间占用", style = MaterialTheme.typography.bodyMedium, color = Muted, modifier = Modifier.padding(top = 5.dp))
            }
            item {
                Surface(color = Color.White, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text("整理数据", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Ink)
                        Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatValue(label = "已整理", value = organizedCount.toString(), modifier = Modifier.weight(1f))
                            StatValue(label = "已删除", value = deletedCount.toString(), modifier = Modifier.weight(1f))
                        }
                        Text("已清理空间", style = MaterialTheme.typography.labelMedium, color = Muted, modifier = Modifier.padding(top = 16.dp))
                        Text(formatBytes(clearedBytes), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Moss, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
            item {
                Surface(color = Color.White, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text("媒体总览", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Ink)
                        Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatValue(label = "照片数量", value = storage.photoCount.toString(), modifier = Modifier.weight(1f))
                            StatValue(label = "视频数量", value = storage.videoCount.toString(), modifier = Modifier.weight(1f))
                        }
                        Text("相册占用空间", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Ink, modifier = Modifier.padding(top = 18.dp))
                        Text(formatBytes(totalBytes), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Moss, modifier = Modifier.padding(top = 12.dp))
                        Text("照片和视频合计", style = MaterialTheme.typography.labelMedium, color = Muted, modifier = Modifier.padding(top = 3.dp))
                        Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StorageValue(label = "照片", value = formatBytes(storage.photoBytes), modifier = Modifier.weight(1f))
                            StorageValue(label = "视频", value = formatBytes(storage.videoBytes), modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            item {
                Text("空间数据来自手机本地媒体库，打开此页面时会自动刷新。", style = MaterialTheme.typography.labelSmall, color = Muted)
            }
        }
    }
}

@Composable
private fun StatValue(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(color = Mint, shape = RoundedCornerShape(8.dp), modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Moss)
            Text(label, style = MaterialTheme.typography.labelMedium, color = Muted, modifier = Modifier.padding(top = 3.dp))
        }
    }
}

@Composable
private fun StorageValue(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(color = Paper, shape = RoundedCornerShape(8.dp), modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Muted)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Ink, modifier = Modifier.padding(top = 5.dp))
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

private object StatsStore {
    private const val PREFS = "organize_stats"
    private const val ORGANIZED = "organized_count"
    private const val DELETED = "deleted_count"
    private const val DELETED_BYTES = "deleted_bytes"

    fun organized(context: Context): Int = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(ORGANIZED, 0)

    fun deleted(context: Context): Int = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(DELETED, 0)

    fun deletedBytes(context: Context): Long = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(DELETED_BYTES, 0L)

    fun addOrganized(context: Context, amount: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putInt(ORGANIZED, (prefs.getInt(ORGANIZED, 0) + amount).coerceAtLeast(0)).apply()
    }

    fun addDeleted(context: Context, amount: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putInt(DELETED, prefs.getInt(DELETED, 0) + amount).apply()
    }

    fun addDeletedBytes(context: Context, amount: Long) {
        if (amount <= 0L) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putLong(DELETED_BYTES, prefs.getLong(DELETED_BYTES, 0L) + amount).apply()
    }
}

private fun Category.displayLocation(): String = folderUri?.let { "已选择的手机文件夹" } ?: "DCIM/$name"

private object ReviewedStore {
    private const val PREFS = "reviewed_media"
    private const val PHOTOS = "photos"
    private const val VIDEOS = "videos"
    private const val ROUND_COMPLETE_ANNOUNCED = "round_complete_announced"

    fun photos(context: Context): Set<String> = load(context, PHOTOS)

    fun videos(context: Context): Set<String> = load(context, VIDEOS)

    fun markPhoto(context: Context, uri: Uri) = mark(context, PHOTOS, uri)

    fun markVideo(context: Context, uri: Uri) = mark(context, VIDEOS, uri)

    fun unmarkPhoto(context: Context, uri: Uri) = unmark(context, PHOTOS, uri)

    fun unmarkVideo(context: Context, uri: Uri) = unmark(context, VIDEOS, uri)

    fun roundCompleteAnnounced(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(ROUND_COMPLETE_ANNOUNCED, false)

    fun setRoundCompleteAnnounced(context: Context, announced: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(ROUND_COMPLETE_ANNOUNCED, announced)
            .apply()
    }

    fun clearReviewed(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(PHOTOS)
            .remove(VIDEOS)
            .putBoolean(ROUND_COMPLETE_ANNOUNCED, false)
            .apply()
    }

    private fun load(context: Context, key: String): Set<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getStringSet(key, emptySet()).orEmpty().toSet()

    private fun mark(context: Context, key: String, uri: Uri) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val updated = prefs.getStringSet(key, emptySet()).orEmpty().toMutableSet().apply { add(uri.toString()) }
        prefs.edit().putStringSet(key, updated).apply()
    }

    private fun unmark(context: Context, key: String, uri: Uri) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val updated = prefs.getStringSet(key, emptySet()).orEmpty().toMutableSet().apply { remove(uri.toString()) }
        prefs.edit().putStringSet(key, updated).apply()
    }
}

@Composable
private fun CategoryPhotosScreen(category: Category, categories: List<Category>, stagedPhotos: List<PhotoItem>, canUndo: Boolean, onUndo: () -> Unit, onBack: () -> Unit, onShoot: () -> Unit, onStageDelete: (List<PhotoItem>) -> Unit, onTrash: () -> Unit) {
    val context = LocalContext.current
    var photos by remember(category) { mutableStateOf(emptyList<PhotoItem>()) }
    var loadingPhotos by remember(category) { mutableStateOf(true) }
    var selecting by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var viewerIndex by remember { mutableStateOf<Int?>(null) }
    var previewIndex by remember { mutableStateOf<Int?>(null) }
    val selectionBounds = remember { mutableStateMapOf<Uri, Rect>() }
    var selectionGridOrigin by remember { mutableStateOf(Offset.Zero) }
    val stagedPhotoUris = remember(stagedPhotos) { stagedPhotos.mapTo(HashSet()) { it.uri } }
    LaunchedEffect(category, stagedPhotoUris) {
        loadingPhotos = true
        val loaded = withContext(Dispatchers.IO) { queryPhotos(context, category) }
        photos = loaded.filterNot { it.uri in stagedPhotoUris }
        loadingPhotos = false
    }
    BackHandler(onBack = onBack)
    Scaffold(containerColor = Paper, topBar = {
        Row(Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") }
            Column(Modifier.weight(1f)) {
                Text(category.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${photos.size} 张照片", style = MaterialTheme.typography.labelSmall, color = Muted)
            }
            IconButton(onClick = onUndo, enabled = canUndo) { Icon(Icons.Filled.Undo, "撤回上一次暂存删除", tint = Moss) }
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
                    if (!selecting) {
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
        if (loadingPhotos) Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Moss) }
        else if (photos.isEmpty()) Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { Text("这个分类还没有照片", color = Muted) }
        else Box(
            Modifier.fillMaxSize().padding(p)
                .onGloballyPositioned { selectionGridOrigin = it.positionInRoot() }
                .photoGridDragSelection(selecting, photos, selected, selectionBounds, selectionGridOrigin) { uri, shouldSelect ->
                    selected = if (shouldSelect) selected + uri else selected - uri
                },
        ) {
            LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxSize(), contentPadding = PaddingValues(4.dp)) {
                items(photos, key = { it.uri.toString() }) { photo ->
                    PhotoThumb(photo, selected = photo.uri in selected, onBoundsChanged = { rect -> if (rect == null) selectionBounds.remove(photo.uri) else selectionBounds[photo.uri] = rect }, onClick = {
                        if (selecting) selected = if (photo.uri in selected) selected - photo.uri else selected + photo.uri
                        else viewerIndex = photos.indexOfFirst { it.uri == photo.uri }
                    }, onLongClick = { if (!selecting) viewerIndex = photos.indexOfFirst { it.uri == photo.uri } })
                }
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
                }
            },
            stagedCount = stagedPhotos.size,
            canUndo = canUndo,
            onUndo = onUndo,
        )
    }
    previewIndex?.let { index ->
        PhotoPreviewScreen(photos = photos, initialIndex = index, onClose = { previewIndex = null })
    }
}

@Composable
private fun Modifier.photoGridDragSelection(
    enabled: Boolean,
    photos: List<PhotoItem>,
    selected: Set<Uri>,
    bounds: Map<Uri, Rect>,
    gridOrigin: Offset,
    onSelectionChange: (Uri, Boolean) -> Unit,
): Modifier {
    val latestEnabled = rememberUpdatedState(enabled)
    val latestPhotos = rememberUpdatedState(photos)
    val latestSelected = rememberUpdatedState(selected)
    val latestBounds = rememberUpdatedState(bounds)
    val latestOrigin = rememberUpdatedState(gridOrigin)
    val latestOnSelectionChange = rememberUpdatedState(onSelectionChange)
    return pointerInput(Unit) {
        awaitEachGesture {
            val down = awaitFirstDown(pass = PointerEventPass.Initial, requireUnconsumed = false)
            if (!latestEnabled.value) return@awaitEachGesture
            val downRoot = down.position + latestOrigin.value
            val startPhoto = latestPhotos.value.firstOrNull { latestBounds.value[it.uri]?.contains(downRoot) == true }
                ?: return@awaitEachGesture
            val shouldSelect = startPhoto.uri !in latestSelected.value
            val visited = mutableSetOf<Uri>()
            var previous = down.position
            var directionLocked = false
            var selectingDrag = false
            var finished = false
            while (!finished) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                val point = change.position
                if (change.pressed) {
                    val deltaFromDown = point - down.position
                    if (!directionLocked && deltaFromDown.getDistance() > viewConfiguration.touchSlop) {
                        directionLocked = true
                        // A mainly vertical first movement belongs to LazyVerticalGrid scrolling.
                        // Once the gesture starts horizontally, it stays in selection mode even
                        // when the user turns upward or downward later.
                        selectingDrag = kotlin.math.abs(deltaFromDown.x) > kotlin.math.abs(deltaFromDown.y) * 1.5f
                        if (!selectingDrag) break
                        change.consume()
                        visited.add(startPhoto.uri)
                        latestOnSelectionChange.value(startPhoto.uri, shouldSelect)
                    }
                    if (selectingDrag) {
                        change.consume()
                        val segmentStart = previous + latestOrigin.value
                        val segmentEnd = point + latestOrigin.value
                        latestPhotos.value.forEach { photo ->
                            val rect = latestBounds.value[photo.uri] ?: return@forEach
                            if (photo.uri !in visited && segmentIntersectsRect(segmentStart, segmentEnd, rect)) {
                                visited.add(photo.uri)
                                latestOnSelectionChange.value(photo.uri, shouldSelect)
                            }
                        }
                    }
                } else finished = true
                previous = point
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PhotoThumb(
    photo: PhotoItem,
    selected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onBoundsChanged: ((Rect?) -> Unit)? = null,
) {
    val context = LocalContext.current
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = photo.uri) { value = loadPhotoBitmap(context, photo, 480) }
    DisposableEffect(photo.uri) {
        onDispose { onBoundsChanged?.invoke(null) }
    }
    Box(Modifier.padding(2.dp).size(120.dp).onGloballyPositioned { coordinates -> onBoundsChanged?.invoke(coordinates.boundsInRoot()) }.combinedClickable(onClick = onClick, onLongClick = onLongClick)) {
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
private fun PhotoPreviewScreen(
    photos: List<PhotoItem>,
    initialIndex: Int,
    onClose: () -> Unit,
    onSeen: (PhotoItem) -> Unit = {},
) {
    if (photos.isEmpty()) {
        onClose()
        return
    }
    val pagerState = rememberPagerState(initialPage = initialIndex.coerceIn(0, photos.lastIndex), pageCount = { photos.size })
    val currentPhoto = photos.getOrNull(pagerState.currentPage)
    var expandedImage by remember { mutableStateOf(false) }
    LaunchedEffect(currentPhoto?.uri) { currentPhoto?.let(onSeen) }
    BackHandler(onBack = onClose)
    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Surface(color = Color.Black, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Color.White) }
            Column(Modifier.weight(1f).padding(start = 4.dp)) {
                Text(currentPhoto?.name.orEmpty(), color = Color.White, maxLines = 1, style = MaterialTheme.typography.bodySmall)
                Text(formatMediaDate(currentPhoto?.dateMillis ?: 0L), color = Color.White.copy(alpha = .72f), style = MaterialTheme.typography.labelSmall)
            }
            Text("${pagerState.currentPage + 1} / ${photos.size}", color = Color.White, style = MaterialTheme.typography.labelLarge)
        }
        }
        Box(Modifier.fillMaxWidth().weight(1f).padding(if (expandedImage) 0.dp else 12.dp), contentAlignment = Alignment.Center) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 1) { page ->
                ZoomablePhotoPageImage(photos[page], onLongPress = { expandedImage = true })
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
    onSeen: (PhotoItem) -> Unit = {},
    stagedCount: Int,
    canUndo: Boolean,
    onUndo: () -> Unit,
    autoPlayEnabled: Boolean = false,
) {
    if (photos.isEmpty()) { onClose(); return }
    val pagerState = rememberPagerState(initialPage = initialIndex.coerceIn(0, photos.lastIndex), pageCount = { photos.size })
    val scope = rememberCoroutineScope()
    var autoPlaying by remember { mutableStateOf(false) }
    var expandedImage by remember { mutableStateOf(false) }
    val currentPhoto = photos.getOrNull(pagerState.currentPage)
    LaunchedEffect(currentPhoto?.uri) {
        expandedImage = false
        currentPhoto?.let(onSeen)
    }
    LaunchedEffect(autoPlayEnabled, autoPlaying, currentPhoto?.uri) {
        if (autoPlayEnabled && autoPlaying) {
            delay(1000)
            currentPhoto?.let(onStageDelete)
        }
    }
    BackHandler(onBack = onClose)
    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 72.dp, bottom = 170.dp)
                    .padding(if (expandedImage) 0.dp else 12.dp),
                beyondViewportPageCount = 1,
            ) { page ->
                ZoomablePhotoPageImage(photos[page], onLongPress = { expandedImage = true })
            }
            Column(Modifier.fillMaxSize().padding(WindowInsets.safeDrawing.asPaddingValues()).zIndex(3f)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClose) { Icon(Icons.Filled.Close, "返回", tint = Color.White) }
                    Column(Modifier.weight(1f)) {
                        Text("整理照片", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${pagerState.currentPage + 1} / ${photos.size}", color = Color.White.copy(alpha = .75f), style = MaterialTheme.typography.labelSmall)
                        Text("左右滑动切换照片", color = Color.White.copy(alpha = .62f), style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = onUndo, enabled = canUndo) {
                        Icon(Icons.Filled.Undo, "撤回上一次暂存删除", tint = Color.White)
                    }
                    IconButton(onClick = onOpenTrash) {
                        Box {
                            Icon(Icons.Filled.DeleteOutline, "照片删除暂存区", tint = Color.White)
                            Surface(color = Color(0xFFFFC107), shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).size(16.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(stagedCount.coerceAtMost(99).toString(), style = MaterialTheme.typography.labelSmall, color = Ink, maxLines = 1)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                Surface(color = Color(0xFF111111).copy(alpha = .92f), modifier = Modifier.fillMaxWidth()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(categories, key = { it.name }) { target ->
                            TextButton(onClick = { currentPhoto?.let { onMove(it, target) } }) {
                                Icon(Icons.Filled.FolderOpen, contentDescription = null, tint = Color(0xFFB9E7C8), modifier = Modifier.size(16.dp))
                                Text(" ${target.name}", color = Color.White, maxLines = 1)
                            }
                        }
                    }
                }
                Column(Modifier.fillMaxWidth().background(Color(0xFF111111)).padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Text(formatMediaDate(currentPhoto?.dateMillis ?: 0L), color = Color.White.copy(alpha = .72f), maxLines = 1, style = MaterialTheme.typography.labelSmall, modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp))
                    Text(currentPhoto?.name.orEmpty(), color = Color.White, maxLines = 1, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (autoPlayEnabled) {
                            IconButton(onClick = { autoPlaying = !autoPlaying }) {
                                Icon(if (autoPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, if (autoPlaying) "暂停自动播放" else "开始自动播放", tint = Color.White)
                            }
                        }
                        Button(onClick = {
                            photos.getOrNull(pagerState.currentPage)?.let(onKeep)
                            if (autoPlayEnabled && autoPlaying) {
                                autoPlaying = false
                            } else if (pagerState.currentPage < photos.lastIndex) {
                                autoPlaying = false
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                            } else {
                                autoPlaying = false
                                onClose()
                            }
                        }, modifier = Modifier.width(80.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Ink)) {
                            Text("保留", maxLines = 1)
                        }
                        Button(onClick = {
                            autoPlaying = false
                            photos.getOrNull(pagerState.currentPage)?.let(onStageDelete)
                        }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF343434), contentColor = Color.White)) {
                            Icon(Icons.Filled.DeleteOutline, null)
                            Text("  暂存删除", maxLines = 1, softWrap = false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoPageImage(photo: PhotoItem) {
    val context = LocalContext.current
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = photo.uri) { value = loadPhotoBitmap(context, photo, 2048) }
    val bitmap: Bitmap? = bitmapState.value
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (bitmap != null) Image(bitmap = bitmap.asImageBitmap(), contentDescription = photo.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
    }
}

@Composable
private fun ZoomablePhotoPageImage(photo: PhotoItem, onLongPress: (() -> Unit)? = null) {
    val context = LocalContext.current
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = photo.uri) {
        value = loadPhotoBitmap(context, photo, 4096)
    }
    var scale by remember(photo.uri) { mutableStateOf(1f) }
    var translation by remember(photo.uri) { mutableStateOf(Offset.Zero) }
    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(photo.uri) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        if (event.changes.none { it.pressed }) break
                        if (event.changes.count { it.pressed } >= 2) {
                            scale = (scale * event.calculateZoom()).coerceIn(1f, 6f)
                            translation += event.calculatePan()
                            event.changes.forEach { it.consume() }
                        }
                    }
                }
            }
            .pointerInput(photo.uri) {
                detectTapGestures(onLongPress = onLongPress?.let { callback -> { _: Offset -> callback() } }, onDoubleTap = {
                    scale = if (scale > 1.05f) 1f else 2f
                    if (scale == 1f) translation = Offset.Zero
                })
            },
        contentAlignment = Alignment.Center,
    ) {
        bitmapState.value?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = photo.name,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = translation.x
                        translationY = translation.y
                    },
                contentScale = ContentScale.Fit,
            )
        }
    }
}

private object PhotoBitmapCache {
    private const val MAX_BYTES = 48 * 1024 * 1024
    private val cache = object : LruCache<String, Bitmap>(MAX_BYTES) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.allocationByteCount
    }

    fun get(key: String): Bitmap? = synchronized(cache) { cache.get(key) }

    fun put(key: String, bitmap: Bitmap) {
        synchronized(cache) { cache.put(key, bitmap) }
    }
}

private suspend fun loadPhotoBitmap(context: Context, photo: PhotoItem, maxDimension: Int): Bitmap? = withContext(Dispatchers.IO) {
    val key = "${photo.uri}|$maxDimension"
    PhotoBitmapCache.get(key) ?: decodePhotoBitmap(context, photo, maxDimension)?.also { PhotoBitmapCache.put(key, it) }
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
    initialGroupUris: List<String>?,
    onBack: () -> Unit,
    onStageDelete: (List<PhotoItem>) -> Unit,
    canUndo: Boolean,
    onUndo: () -> Unit,
    onSeen: (Collection<PhotoItem>) -> Unit,
    onTrash: (List<PhotoItem>) -> Unit,
    trashCount: Int,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val sourcePhotos = remember { photos }
    var selected by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var selecting by remember { mutableStateOf(false) }
    val selectionBounds = remember { mutableStateMapOf<Uri, Rect>() }
    var selectionGridOrigin by remember { mutableStateOf(Offset.Zero) }
    var viewerIndex by remember { mutableStateOf<Int?>(null) }
    var previewIndex by remember { mutableStateOf<Int?>(null) }
    var gameMode by rememberSaveable { mutableStateOf(false) }
    var modeMenuExpanded by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf(MODE_RANDOM) }
    var countText by remember { mutableStateOf("20") }
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    val count = countText.toIntOrNull()?.coerceIn(1, 500) ?: 20
    val restoredPhotos = initialGroupUris?.let { ids -> sourcePhotos.filter { it.uri.toString() in ids } }.orEmpty()
    var batchPhotos by remember { mutableStateOf(if (initialGroupUris != null) restoredPhotos else sourcePhotos.shuffled().take(count)) }
    var visiblePhotos by remember { mutableStateOf(batchPhotos) }
    var processedUris by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var restoredGroupPending by remember { mutableStateOf(initialGroupUris != null) }
    val monthOptions = remember(sourcePhotos) {
        sourcePhotos.mapNotNull { if (it.dateMillis > 0) SimpleDateFormat(MONTH_FORMAT, Locale.CHINA).format(Date(it.dateMillis)) else null }.distinct()
    }
    LaunchedEffect(mode, countText, selectedMonth) {
        if (restoredGroupPending) {
            restoredGroupPending = false
            return@LaunchedEffect
        }
        val available = sourcePhotos.filterNot { it.uri in processedUris }
        val nextBatch = when (mode) {
            MODE_RANDOM -> available.shuffled().take(count)
            MODE_ALL -> available
            else -> available.filter { item -> selectedMonth == null || (item.dateMillis > 0 && SimpleDateFormat(MONTH_FORMAT, Locale.CHINA).format(Date(item.dateMillis)) == selectedMonth) }
        }
        batchPhotos = nextBatch
        visiblePhotos = nextBatch
    }
    val remainingCount = sourcePhotos.count { it.uri !in processedUris }
    val batchComplete = batchPhotos.isNotEmpty() && batchPhotos.all { it.uri in processedUris }
    BackHandler(onBack = onBack)
    // ORGANIZE_MODE_SWITCH
    if (gameMode) {
        PhotoDeleteGameScreen(
            photos = sourcePhotos,
            categories = categories,
            stagedCount = trashCount,
            canUndo = canUndo,
            onUndo = onUndo,
            onBack = onBack,
            onStageDelete = onStageDelete,
            onSeen = onSeen,
            onTrash = onTrash,
            onExitGame = { gameMode = false },
        )
        return
    }
    Scaffold(containerColor = Paper, topBar = {
        Row(Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") }
            Column(Modifier.weight(1f)) { Text("整理照片", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("本次 ${photos.size} 张", style = MaterialTheme.typography.labelSmall, color = Muted) }
            Box {
                IconButton(onClick = { modeMenuExpanded = true }) { Icon(Icons.Filled.SwapHoriz, "鍒囨崲鏁寸悊妯″紡", tint = Moss) }
                DropdownMenu(expanded = modeMenuExpanded, onDismissRequest = { modeMenuExpanded = false }) {
                    DropdownMenuItem(text = { Text("普通整理") }, onClick = { modeMenuExpanded = false })
                    DropdownMenuItem(text = { Text("游戏删照片") }, onClick = { gameMode = true; modeMenuExpanded = false })
                }
            }
            if (canUndo) IconButton(onClick = onUndo) { Icon(Icons.Filled.Undo, "撤回上一次暂存删除", tint = Moss) }
            IconButton(onClick = { onTrash(visiblePhotos) }) {
                Box {
                    Icon(Icons.Filled.DeleteOutline, "删除暂存区", tint = Moss)
                    Surface(color = Color(0xFFFFC107), shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).size(16.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text(trashCount.coerceAtMost(99).toString(), style = MaterialTheme.typography.labelSmall, color = Ink) }
                    }
                }
            }
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
                listOf(MODE_RANDOM, MODE_MONTH, MODE_ALL).forEach { item ->
                    TextButton(onClick = { mode = item }) { Text(item, color = if (mode == item) Moss else Muted, fontWeight = if (mode == item) FontWeight.Bold else FontWeight.Normal) }
                }
            }
            if (mode == MODE_RANDOM) {
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
                if (selecting) {
                    IconButton(onClick = {
                        if (selected.isEmpty()) {
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
                    }) { Icon(Icons.Filled.DeleteOutline, "暂存所选照片", tint = Moss) }
                }
                IconButton(onClick = { selecting = !selecting; selected = emptySet() }) { Icon(if (selecting) Icons.Filled.Close else Icons.Filled.CheckBox, if (selecting) "取消选择" else "选择照片", tint = Moss) }
            }
            if (mode == MODE_RANDOM) {
                Button(
                    onClick = {
                        val nextBatch = sourcePhotos.filterNot { it.uri in processedUris }.shuffled().take(count)
                        batchPhotos = nextBatch
                        visiblePhotos = nextBatch
                        selected = emptySet()
                    },
                    enabled = remainingCount > 0,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).height(40.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("\u4e0b\u4e00\u7ec4", maxLines = 1)
                }
            }
            if (visiblePhotos.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("没有找到可整理的照片", color = Muted) }
            else Box(
                Modifier.fillMaxSize()
                    .onGloballyPositioned { selectionGridOrigin = it.positionInRoot() }
                    .photoGridDragSelection(selecting, visiblePhotos, selected, selectionBounds, selectionGridOrigin) { uri, shouldSelect ->
                        selected = if (shouldSelect) selected + uri else selected - uri
                    },
            ) {
                LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxSize(), contentPadding = PaddingValues(4.dp)) {
                    items(visiblePhotos, key = { it.uri.toString() }) { photo ->
                        PhotoThumb(photo, photo.uri in selected, onBoundsChanged = { rect -> if (rect == null) selectionBounds.remove(photo.uri) else selectionBounds[photo.uri] = rect }, onClick = {
                            if (selecting) selected = if (photo.uri in selected) selected - photo.uri else selected + photo.uri
                            else viewerIndex = visiblePhotos.indexOfFirst { it.uri == photo.uri }
                        }, onLongClick = { if (!selecting) viewerIndex = visiblePhotos.indexOfFirst { it.uri == photo.uri } })
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
            onOpenTrash = { viewerIndex = null; onTrash(visiblePhotos) },
            onStageDelete = { photo ->
                val group = visiblePhotos
                val wasLast = visiblePhotos.size <= 1
                processedUris = processedUris + photo.uri
                visiblePhotos = visiblePhotos.filterNot { it.uri == photo.uri }
                onStageDelete(listOf(photo))
                if (wasLast) {
                    viewerIndex = null
                    onTrash(group)
                }
            },
            onKeep = { photo ->
                val group = visiblePhotos
                val wasLast = visiblePhotos.size <= 1
                processedUris = processedUris + photo.uri
                visiblePhotos = visiblePhotos.filterNot { it.uri == photo.uri }
                onSeen(listOf(photo))
                if (wasLast) {
                    viewerIndex = null
                    onTrash(group)
                }
            },
            onSeen = { photo ->
                processedUris = processedUris + photo.uri
                onSeen(listOf(photo))
            },
            onMove = { photo, target ->
                val group = visiblePhotos
                val wasLast = visiblePhotos.size <= 1
                if (movePhoto(context, photo, target)) {
                    processedUris = processedUris + photo.uri
                    visiblePhotos = visiblePhotos.filterNot { it.uri == photo.uri }
                    onSeen(listOf(photo))
                    viewerIndex = null
                    if (wasLast && trashCount > 0) onTrash(group)
                }
            },
            stagedCount = trashCount,
            canUndo = canUndo,
            onUndo = onUndo,
            autoPlayEnabled = true,
        )
    }
    previewIndex?.let { index ->
        PhotoPreviewScreen(
            photos = visiblePhotos,
            initialIndex = index,
            onClose = { previewIndex = null },
            onSeen = { photo -> processedUris = processedUris + photo.uri; onSeen(listOf(photo)) },
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PhotoDeleteGameScreen(
    photos: List<PhotoItem>,
    categories: List<Category>,
    stagedCount: Int,
    canUndo: Boolean,
    onUndo: () -> Unit,
    onBack: () -> Unit,
    onStageDelete: (List<PhotoItem>) -> Unit,
    onSeen: (Collection<PhotoItem>) -> Unit,
    onTrash: (List<PhotoItem>) -> Unit,
    onExitGame: () -> Unit,
) {
    val context = LocalContext.current
    val gameScope = rememberCoroutineScope()
    var modeMenuExpanded by remember { mutableStateOf(false) }
    var previewIndex by remember { mutableStateOf<Int?>(null) }
    var roundSize by rememberSaveable { mutableStateOf(6) }
    var remaining by remember(photos) { mutableStateOf(photos) }
    var batch by remember(photos, roundSize) { mutableStateOf(remaining.shuffled().take(roundSize)) }
    val cardBounds = remember { mutableStateMapOf<Uri, Rect>() }
    val swipeTrail = remember { mutableStateListOf<Offset>() }
    val cutThisStroke = remember { mutableSetOf<Uri>() }
    var gameRootOrigin by remember { mutableStateOf(Offset.Zero) }
    var comboCount by remember { mutableStateOf(0) }
    var comboToken by remember { mutableStateOf(0) }
    val flyingPhotos = remember { mutableStateMapOf<Uri, Pair<PhotoItem, Rect>>() }

    fun stage(photosToStage: Collection<PhotoItem>) {
        val staged = photosToStage.distinctBy { it.uri }.filter { photo -> batch.any { it.uri == photo.uri } }
        if (staged.isEmpty()) return
        staged.forEach { photo -> cardBounds[photo.uri]?.let { bounds -> flyingPhotos[photo.uri] = photo to bounds } }
        gameScope.launch {
            delay(340)
            staged.forEach { flyingPhotos.remove(it.uri) }
        }
        val stagedUris = staged.mapTo(mutableSetOf()) { it.uri }
        remaining = remaining.filterNot { it.uri in stagedUris }
        batch = batch.filterNot { it.uri in stagedUris }
        onStageDelete(staged)
    }
    val latestBatch = rememberUpdatedState(batch)
    val latestStage = rememberUpdatedState<(Collection<PhotoItem>) -> Unit>({ photosToStage -> stage(photosToStage) })
    val latestRootOrigin = rememberUpdatedState(gameRootOrigin)

    fun keep(photo: PhotoItem) {
        if (batch.none { it.uri == photo.uri }) return
        remaining = remaining.filterNot { it.uri == photo.uri }
        batch = batch.filterNot { it.uri == photo.uri }
        onSeen(listOf(photo))
    }

    fun move(photo: PhotoItem, target: Category) {
        if (movePhoto(context, photo, target)) {
            remaining = remaining.filterNot { it.uri == photo.uri }
            batch = batch.filterNot { it.uri == photo.uri }
            onSeen(listOf(photo))
        }
    }

    LaunchedEffect(comboToken) {
        if (comboCount > 0) {
            delay(900)
            comboCount = 0
        }
    }
BackHandler(onBack = onBack)
    Scaffold(containerColor = Color(0xFF10251D), topBar = {
        Row(
            Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Color.White) }
            Box {
                IconButton(onClick = { modeMenuExpanded = true }) {
                    Icon(Icons.Filled.SwapHoriz, "切换整理模式", tint = Color(0xFFB9E7C8))
                }
                DropdownMenu(expanded = modeMenuExpanded, onDismissRequest = { modeMenuExpanded = false }) {
                    DropdownMenuItem(text = { Text("普通整理") }, onClick = { onExitGame(); modeMenuExpanded = false })
                    DropdownMenuItem(text = { Text("游戏删照片") }, onClick = { modeMenuExpanded = false })
                }
            }
            Column(Modifier.weight(1f).padding(start = 4.dp)) {
                Text("游戏删照片", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("像切水果一样快速清理", color = Color.White.copy(alpha = .72f), style = MaterialTheme.typography.labelSmall)
            }
            if (canUndo) IconButton(onClick = onUndo) { Icon(Icons.Filled.Undo, "撤回上一次暂存删除", tint = Color.White) }
            IconButton(onClick = { onTrash(batch) }) {
                Box {
                    Icon(Icons.Filled.DeleteOutline, "打开删除暂存区", tint = Color.White)
                    if (stagedCount > 0) Surface(color = Color(0xFFFFC107), shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).size(17.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text(stagedCount.coerceAtMost(99).toString(), style = MaterialTheme.typography.labelSmall, color = Ink) }
                    }
                }
            }
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("本轮 ${batch.size} 张", color = Color.White, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                listOf(4, 6, 8).forEach { size ->
                    TextButton(onClick = { roundSize = size }) {
                        Text("${size}张", color = if (roundSize == size) Color(0xFFFFC107) else Color.White.copy(alpha = .7f), fontWeight = if (roundSize == size) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
            if (batch.isEmpty()) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    if (remaining.isNotEmpty()) {
                        Button(
                            onClick = { batch = remaining.shuffled().take(roundSize) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107), contentColor = Ink),
                            modifier = Modifier.padding(top = 12.dp),
                        ) { Text("\u4e0b\u4e00\u7ec4") }
                    }
                    if (stagedCount > 0) TextButton(onClick = { onTrash(batch) }) { Text("\u67e5\u770b\u5220\u9664\u6682\u5b58\u533a", color = Color(0xFFFFC107)) }
                }
            } else {
                Text("单击暂存一张，按住划过多张连切，长按预览", color = Color.White.copy(alpha = .72f), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp))
                Box(
                    Modifier
                        .fillMaxSize()
                        .onGloballyPositioned { gameRootOrigin = it.positionInRoot() }
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val strokeBatch = latestBatch.value
                                val photosHitThisStroke = linkedMapOf<Uri, PhotoItem>()
                                cutThisStroke.clear()
                                swipeTrail.clear()
                                swipeTrail.add(down.position)
                                var previous = down.position
                                var moved = false
                                var released = false
                                while (!released) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                    val point = change.position
                                    if (change.pressed) {
                                        if ((point - down.position).getDistance() > viewConfiguration.touchSlop) {
                                            moved = true
                                            change.consume()
                                        }
                                        if (moved) {
                                            if ((point - (swipeTrail.lastOrNull() ?: down.position)).getDistance() >= 10.dp.toPx()) swipeTrail.add(point)
                                            val previousRoot = previous + latestRootOrigin.value
                                            val pointRoot = point + latestRootOrigin.value
                                            strokeBatch.forEach { photo ->
                                                if (photo.uri !in cutThisStroke) {
                                                    val bounds = cardBounds[photo.uri] ?: return@forEach
                                                    if (segmentIntersectsRect(previousRoot, pointRoot, bounds)) {
                                                        cutThisStroke.add(photo.uri)
                                                        comboCount += 1
                                                        comboToken++
                                                        photosHitThisStroke[photo.uri] = photo
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        released = true
                                    }
                                    previous = point
                                }
                                if (moved && photosHitThisStroke.isNotEmpty()) {
                                    latestStage.value(photosHitThisStroke.values.toList())
                                } else if (!moved && comboCount > 0) {
                                    comboCount = 0
                                }
                                gameScope.launch {
                                    delay(260)
                                    swipeTrail.clear()
                                }
                            }
                        },
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(batch, key = { it.uri.toString() }) { photo ->
                            val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = photo.uri) {
                                value = loadPhotoBitmap(context, photo, 720)
                            }
                            Surface(
                                color = Color(0xFF234A39),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.86f)
                                    .onGloballyPositioned { cardBounds[photo.uri] = it.boundsInRoot() }
                                    .combinedClickable(
                                        onClick = { comboCount = 0; stage(listOf(photo)) },
                                        onLongClick = { previewIndex = batch.indexOfFirst { it.uri == photo.uri } },
                                    ),
                            ) {
                                Box(Modifier.fillMaxSize()) {
                                    bitmapState.value?.let { bitmap ->
                                        Image(bitmap.asImageBitmap(), contentDescription = photo.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    }
                                    Surface(color = Color.Black.copy(alpha = .58f), modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                                        Text(photo.name, color = Color.White, maxLines = 1, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                    Canvas(Modifier.fillMaxSize().zIndex(2f)) {
                        if (swipeTrail.size > 1) {
                            val path = Path().apply {
                                moveTo(swipeTrail.first().x, swipeTrail.first().y)
                                swipeTrail.drop(1).forEach { lineTo(it.x, it.y) }
                            }
                            drawPath(path, Color(0xFFFFE082), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round))
                            drawPath(path, Color.White.copy(alpha = .8f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                        }
                    }
                    if (comboCount > 1) {
                        Surface(
                            color = Color(0xFFFFC107),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp).zIndex(3f),
                        ) {
                            Text("${comboCount} 连击", color = Ink, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                        }
                    }
                    flyingPhotos.values.toList().forEach { (photo, bounds) ->
                        val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = photo.uri) {
                            value = loadPhotoBitmap(context, photo, 720)
                        }
                        FlyingPhotoEffect(
                            photo = photo,
                            bitmap = bitmapState.value,
                            bounds = bounds,
                            rootOrigin = gameRootOrigin,
                            onFinished = { flyingPhotos.remove(photo.uri) },
                        )
                    }
                }
            }
        }
    }
    previewIndex?.let { index ->
        PhotoViewerScreen(
            photos = batch,
            initialIndex = index,
            categories = categories,
            onClose = { previewIndex = null },
            onOpenTrash = { previewIndex = null; onTrash(batch) },
            onStageDelete = {
                val group = batch
                val wasLast = batch.size <= 1
                stage(listOf(it))
                if (wasLast) {
                    previewIndex = null
                    onTrash(group)
                }
            },
            onKeep = {
                val group = batch
                val wasLast = batch.size <= 1
                keep(it)
                if (wasLast) {
                    previewIndex = null
                    onTrash(group)
                }
            },
            onSeen = { photo -> onSeen(listOf(photo)) },
            onMove = { photo, target ->
                val group = batch
                val wasLast = batch.size <= 1
                move(photo, target)
                if (wasLast && stagedCount > 0) onTrash(group)
            },
            stagedCount = stagedCount,
            canUndo = canUndo,
            onUndo = onUndo,
        )
    }
}

private fun segmentIntersectsRect(start: Offset, end: Offset, bounds: Rect): Boolean {
    var minimum = 0f
    var maximum = 1f
    val dx = end.x - start.x
    val dy = end.y - start.y

    fun clip(p: Float, q: Float): Boolean {
        if (p == 0f) return q >= 0f
        val ratio = q / p
        if (p < 0f) {
            if (ratio > maximum) return false
            if (ratio > minimum) minimum = ratio
        } else {
            if (ratio < minimum) return false
            if (ratio < maximum) maximum = ratio
        }
        return true
    }

    return clip(-dx, start.x - bounds.left) &&
        clip(dx, bounds.right - start.x) &&
        clip(-dy, start.y - bounds.top) &&
        clip(dy, bounds.bottom - start.y)
}

@Composable
private fun FlyingPhotoEffect(photo: PhotoItem, bitmap: Bitmap?, bounds: Rect, rootOrigin: Offset, onFinished: () -> Unit) {
    val progress = remember(photo.uri) { Animatable(0f) }
    LaunchedEffect(photo.uri) { progress.animateTo(1f, tween(320)); onFinished() }
    val density = LocalDensity.current
    val width = with(density) { bounds.width.toDp() }
    val height = with(density) { bounds.height.toDp() }
    Box(
        Modifier
            .offset {
                IntOffset(
                    x = (bounds.left - rootOrigin.x + progress.value * 48.dp.toPx()).roundToInt(),
                    y = (bounds.top - rootOrigin.y - progress.value * 100.dp.toPx()).roundToInt(),
                )
            }
            .size(width, height)
            .graphicsLayer {
                alpha = 1f - progress.value
                scaleX = 1f - progress.value * .25f
                scaleY = 1f - progress.value * .25f
                rotationZ = progress.value * 18f
            }
            .zIndex(4f),
    ) {
        if (bitmap != null) Image(bitmap.asImageBitmap(), contentDescription = photo.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else Surface(Modifier.fillMaxSize(), color = Moss, shape = RoundedCornerShape(14.dp)) {}
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
    var previewIndex by remember { mutableStateOf<Int?>(null) }
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
            items(photos, key = { it.uri.toString() }) { photo ->
                PhotoThumb(
                    photo,
                    photo.uri in selected,
                    onClick = { selected = if (photo.uri in selected) selected - photo.uri else selected + photo.uri },
                    onLongClick = { previewIndex = photos.indexOfFirst { it.uri == photo.uri } },
                )
            }
        }
    }
    previewIndex?.let { index ->
        PhotoPreviewScreen(photos = photos, initialIndex = index, onClose = { previewIndex = null })
    }
    if (confirmDelete) AlertDialog(onDismissRequest = { confirmDelete = false }, title = { Text("确认永久删除？") }, text = { Text(if (selected.isEmpty()) "将删除暂存区内的全部照片。" else "将删除已选择的 ${selected.size} 张照片。") }, confirmButton = { TextButton(onClick = { onDelete(if (selected.isEmpty()) photos else photos.filter { it.uri in selected }); selected = emptySet(); confirmDelete = false }) { Text("确认删除") } }, dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("取消") } })
}

@Composable
private fun VideoPoster(video: VideoItem) {
    val context = LocalContext.current
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = video.uri) {
        value = withContext(Dispatchers.IO) { loadVideoThumbnail(context, video) }
    }
    Box(Modifier.fillMaxSize().background(Color(0xFF17211D)), contentAlignment = Alignment.Center) {
        bitmapState.value?.let { bitmap ->
            Image(bitmap.asImageBitmap(), contentDescription = video.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        }
    }
}

private object VideoThumbnailCache {
    private val cache = object : LruCache<String, Bitmap>(6) {}

    fun get(key: String): Bitmap? = synchronized(cache) { cache.get(key) }

    fun put(key: String, bitmap: Bitmap) {
        synchronized(cache) { cache.put(key, bitmap) }
    }
}

private fun loadVideoThumbnail(context: Context, video: VideoItem): Bitmap? {
    val key = video.uri.toString()
    VideoThumbnailCache.get(key)?.let { return it }
    return runCatching {
        MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(context, video.uri)
            retriever.getFrameAtTime(0L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }
    }.getOrNull()?.also { VideoThumbnailCache.put(key, it) }
}

@Composable
private fun VideoThumb(video: VideoItem, onClick: () -> Unit) {
    val context = LocalContext.current
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = video.uri) {
        value = withContext(Dispatchers.IO) { loadVideoThumbnail(context, video) }
    }
    Surface(
        onClick = onClick,
        color = Color(0xFF17211D),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().aspectRatio(0.82f),
    ) {
        Box(Modifier.fillMaxSize()) {
            bitmapState.value?.let { bitmap ->
                Image(bitmap.asImageBitmap(), contentDescription = video.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Surface(color = Color.Black.copy(alpha = .58f), modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                Row(Modifier.padding(horizontal = 8.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text(video.name, color = Color.White, maxLines = 1, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f).padding(start = 4.dp))
                    Text(formatDuration(video.durationMillis), color = Color.White.copy(alpha = .8f), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun VideoOrganizerScreen(
    videos: List<VideoItem>,
    categories: List<Category>,
    initialGroupUris: List<String>?,
    stagedCount: Int,
    canUndo: Boolean,
    onBack: () -> Unit,
    onTrash: (List<VideoItem>) -> Unit,
    onUndo: () -> Unit,
    onStageDelete: (VideoItem) -> Unit,
    onKeep: (VideoItem) -> Unit,
    onMove: (VideoItem, Category) -> VideoMoveResult,
) {
    val context = LocalContext.current
    val sourceVideos = remember(videos) { videos }
    var mode by rememberSaveable { mutableStateOf(MODE_RANDOM) }
    var countText by rememberSaveable { mutableStateOf("20") }
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    val restoredVideos = initialGroupUris?.let { ids -> sourceVideos.filter { it.uri.toString() in ids } }.orEmpty()
    var visibleVideos by remember { mutableStateOf(if (initialGroupUris != null) restoredVideos else sourceVideos.shuffled().take(20)) }
    var processedUris by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var restoredGroupPending by remember { mutableStateOf(initialGroupUris != null) }
    var viewerUri by remember { mutableStateOf<Uri?>(null) }
    val count = countText.toIntOrNull()?.coerceIn(1, 500) ?: 20
    val monthOptions = remember(sourceVideos) {
        sourceVideos.mapNotNull { if (it.dateMillis > 0) SimpleDateFormat(MONTH_FORMAT, Locale.CHINA).format(Date(it.dateMillis)) else null }.distinct()
    }
    LaunchedEffect(mode, countText, selectedMonth, sourceVideos) {
        if (restoredGroupPending) {
            restoredGroupPending = false
            return@LaunchedEffect
        }
        val available = sourceVideos.filterNot { it.uri in processedUris }
        visibleVideos = when (mode) {
            MODE_RANDOM -> available.shuffled().take(count)
            MODE_ALL -> available
            else -> available.filter { video -> selectedMonth == null || (video.dateMillis > 0 && SimpleDateFormat(MONTH_FORMAT, Locale.CHINA).format(Date(video.dateMillis)) == selectedMonth) }
        }
    }
    val remainingCount = sourceVideos.count { it.uri !in processedUris }
    BackHandler(onBack = onBack)
    val selectedViewerUri = viewerUri
    if (selectedViewerUri != null) {
        VideoReviewScreen(
            videos = visibleVideos,
            initialUri = selectedViewerUri,
            categories = categories,
            stagedCount = stagedCount,
            canUndo = canUndo,
            onBack = { viewerUri = null },
            onTrash = { viewerUri = null; onTrash(visibleVideos) },
            onUndo = onUndo,
            onStageDelete = { video ->
                val group = visibleVideos
                val wasLast = visibleVideos.size <= 1
                processedUris = processedUris + video.uri
                visibleVideos = visibleVideos.filterNot { it.uri == video.uri }
                onStageDelete(video)
                if (wasLast) {
                    viewerUri = null
                    onTrash(group)
                }
            },
            onKeep = { video ->
                val group = visibleVideos
                val wasLast = visibleVideos.size <= 1
                processedUris = processedUris + video.uri
                visibleVideos = visibleVideos.filterNot { it.uri == video.uri }
                onKeep(video)
                if (wasLast) {
                    viewerUri = null
                    onTrash(group)
                }
            },
            onMove = { video, target ->
                val group = visibleVideos
                val wasLast = visibleVideos.size <= 1
                val result = onMove(video, target)
                if (result == VideoMoveResult.MOVED) {
                    processedUris = processedUris + video.uri
                    visibleVideos = visibleVideos.filterNot { it.uri == video.uri }
                    if (wasLast) {
                        viewerUri = null
                        if (stagedCount > 0) onTrash(group)
                    }
                }
                result
            },
        )
        return
    }
    Scaffold(
        containerColor = Paper,
        topBar = {
            Row(Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") }
                Column(Modifier.weight(1f)) {
                    Text("整理视频", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("本次 ${sourceVideos.size} 个", style = MaterialTheme.typography.labelSmall, color = Muted)
                }
                if (canUndo) IconButton(onClick = onUndo) { Icon(Icons.Filled.Undo, "撤回上一次暂存删除", tint = Moss) }
                IconButton(onClick = { onTrash(visibleVideos) }) {
                    Box {
                        Icon(Icons.Filled.DeleteOutline, "打开删除暂存区", tint = Moss)
                        if (stagedCount > 0) Surface(color = Color(0xFFFFC107), shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).size(16.dp)) {
                            Box(contentAlignment = Alignment.Center) { Text(stagedCount.coerceAtMost(99).toString(), style = MaterialTheme.typography.labelSmall, color = Ink) }
                        }
                    }
                }
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(MODE_RANDOM, MODE_MONTH, MODE_ALL).forEach { item ->
                    TextButton(onClick = { mode = item; selectedMonth = if (item == MODE_MONTH) selectedMonth else null }) {
                        Text(item, color = if (mode == item) Moss else Muted, fontWeight = if (mode == item) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
            if (mode == MODE_RANDOM) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("本次整理", style = MaterialTheme.typography.bodySmall, color = Muted)
                    OutlinedTextField(value = countText, onValueChange = { countText = it.filter(Char::isDigit).take(3) }, singleLine = true, modifier = Modifier.padding(start = 8.dp).width(72.dp).height(50.dp))
                    Text("个", style = MaterialTheme.typography.bodySmall, color = Muted, modifier = Modifier.padding(start = 6.dp))
                }
            } else if (mode == MODE_MONTH && monthOptions.isNotEmpty()) {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(monthOptions) { month ->
                        TextButton(onClick = { selectedMonth = month }) { Text(month, color = if (selectedMonth == month) Moss else Muted, fontWeight = if (selectedMonth == month) FontWeight.Bold else FontWeight.Normal) }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("共 ${visibleVideos.size} 个，点击视频预览", style = MaterialTheme.typography.bodySmall, color = Muted, modifier = Modifier.weight(1f))
                if (mode == MODE_RANDOM) {
                    Button(
                        onClick = { visibleVideos = sourceVideos.filterNot { it.uri in processedUris }.shuffled().take(count) },
                        enabled = remainingCount > 0,
                        modifier = Modifier.height(38.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    ) { Text(if (remainingCount > 0) "下一组" else "本轮已完成") }
                }
            }
            if (visibleVideos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("没有找到可整理的视频", color = Muted) }
            } else {
                LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(visibleVideos, key = { it.uri.toString() }) { video ->
                        VideoThumb(video) { viewerUri = video.uri }
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoReviewScreen(
    videos: List<VideoItem>,
    initialUri: Uri?,
    categories: List<Category>,
    stagedCount: Int,
    canUndo: Boolean,
    onBack: () -> Unit,
    onTrash: () -> Unit,
    onUndo: () -> Unit,
    onStageDelete: (VideoItem) -> Unit,
    onKeep: (VideoItem) -> Unit,
    onMove: (VideoItem, Category) -> VideoMoveResult,
) {
    val context = LocalContext.current
    var currentUri by remember(initialUri) { mutableStateOf(initialUri ?: videos.firstOrNull()?.uri) }
    var heldSpeed by remember { mutableStateOf<Int?>(null) }
    var playbackPosition by remember { mutableStateOf(0L) }
    var playbackDuration by remember { mutableStateOf(0L) }
    var sliderPosition by remember { mutableStateOf(0f) }
    var isScrubbing by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    val swipeOffset = remember { Animatable(0f) }
    val swipeScope = rememberCoroutineScope()
    LaunchedEffect(currentUri, videos) {
        withContext(Dispatchers.IO) {
            val index = videos.indexOfFirst { it.uri == currentUri }
            listOfNotNull(videos.getOrNull(index - 1), videos.getOrNull(index + 1))
                .forEach { loadVideoThumbnail(context, it) }
        }
    }
    LaunchedEffect(videos) {
        if (videos.none { it.uri == currentUri }) currentUri = videos.firstOrNull()?.uri
    }
    val currentIndex = videos.indexOfFirst { it.uri == currentUri }
    val currentVideo = videos.getOrNull(currentIndex)
    val playingIndexState = rememberUpdatedState(currentIndex)
    val targetPreloadStatusControl = remember(context) {
        object : TargetPreloadStatusControl<Int> {
            override fun getTargetPreloadStatus(index: Int): TargetPreloadStatusControl.PreloadStatus? {
                return when (kotlin.math.abs(index - playingIndexState.value)) {
                    1 -> DefaultPreloadManager.Status(DefaultPreloadManager.Status.STAGE_LOADED_FOR_DURATION_MS, 3_000L)
                    2 -> DefaultPreloadManager.Status(DefaultPreloadManager.Status.STAGE_TRACKS_SELECTED)
                    in 3..4 -> DefaultPreloadManager.Status(DefaultPreloadManager.Status.STAGE_SOURCE_PREPARED)
                    else -> DefaultPreloadManager.Status(DefaultPreloadManager.Status.STAGE_SOURCE_PREPARED)
                }
            }
        }
    }
    val preloadManagerBuilder = remember(context) { DefaultPreloadManager.Builder(context, targetPreloadStatusControl) }
    val preloadManager = remember(preloadManagerBuilder) { preloadManagerBuilder.build() }
    val player = remember(preloadManagerBuilder) { preloadManagerBuilder.buildExoPlayer() }
    var renderedVideoUri by remember(player) { mutableStateOf<Uri?>(null) }
    val mediaItems = remember(videos) { videos.map { ExoMediaItem.fromUri(it.uri) } }
    val managedMediaItems = remember(preloadManager) { mutableMapOf<Uri, Pair<Int, ExoMediaItem>>() }
    LaunchedEffect(videos, currentIndex, mediaItems) {
        if (currentIndex !in videos.indices) return@LaunchedEffect
        val desiredIndices = (maxOf(0, currentIndex - 4)..minOf(videos.lastIndex, currentIndex + 4)).toSet()
        val staleEntries = managedMediaItems.filter { (uri, entry) ->
            val newIndex = videos.indexOfFirst { it.uri == uri }
            newIndex !in desiredIndices || newIndex != entry.first
        }
        staleEntries.forEach { (uri, entry) ->
            preloadManager.remove(entry.second)
            managedMediaItems.remove(uri)
        }
        desiredIndices.forEach { index ->
            val video = videos[index]
            if (video.uri !in managedMediaItems) {
                val mediaItem = mediaItems[index]
                preloadManager.add(mediaItem, index)
                managedMediaItems[video.uri] = index to mediaItem
            }
        }
        preloadManager.setCurrentPlayingIndex(currentIndex)
        preloadManager.invalidate()
    }
    fun togglePlayback() {
        if (player.playbackState == androidx.media3.common.Player.STATE_ENDED) {
            player.seekTo(0L)
            player.play()
        } else if (player.isPlaying || player.playWhenReady) {
            player.pause()
        } else {
            player.play()
        }
        isPlaying = player.playWhenReady && player.playbackState != androidx.media3.common.Player.STATE_ENDED
    }
    DisposableEffect(player) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onRenderedFirstFrame() {
                renderedVideoUri = player.currentMediaItem?.localConfiguration?.uri
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }
    LaunchedEffect(currentVideo?.uri) {
        renderedVideoUri = null
        player.setPlaybackParameters(PlaybackParameters(1f))
        heldSpeed = null
        playbackPosition = 0L
        playbackDuration = 0L
        sliderPosition = 0f
        if (currentVideo != null && currentIndex in mediaItems.indices) {
            val mediaItem = mediaItems[currentIndex]
            val registeredItem = managedMediaItems[currentVideo.uri]
            if (registeredItem?.first != currentIndex || registeredItem.second != mediaItem) {
                registeredItem?.let { preloadManager.remove(it.second) }
                preloadManager.add(mediaItem, currentIndex)
                managedMediaItems[currentVideo.uri] = currentIndex to mediaItem
            }
            preloadManager.setCurrentPlayingIndex(currentIndex)
            preloadManager.invalidate()
            val preloadedSource = preloadManager.getMediaSource(mediaItem)
            if (preloadedSource != null) player.setMediaSource(preloadedSource) else player.setMediaItem(mediaItem)
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
    DisposableEffect(player, preloadManager) { onDispose { player.release(); preloadManager.release() } }
    BackHandler(onBack = onBack)

    BoxWithConstraints(Modifier.fillMaxSize().background(Color.Black)) {
        val viewportHeight = constraints.maxHeight.toFloat().coerceAtLeast(1f)
        val contentHeight = (viewportHeight - with(LocalDensity.current) { 242.dp.toPx() }).coerceAtLeast(1f)
        val swipeTargetIndex = when {
            swipeOffset.value < 0f -> currentIndex + 1
            swipeOffset.value > 0f -> currentIndex - 1
            else -> -1
        }
        if (swipeOffset.value != 0f && swipeTargetIndex in videos.indices) {
            val targetVideo = videos[swipeTargetIndex]
            val targetPlacement = swipeOffset.value + if (swipeOffset.value < 0f) contentHeight else -contentHeight
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 72.dp, bottom = 170.dp)
                    .offset { IntOffset(0, targetPlacement.roundToInt()) }
                    .zIndex(0f),
            ) {
                VideoPoster(targetVideo)
            }
        }
        if (currentVideo != null) {
            AndroidView(
                    factory = { ctx -> PlayerView(ctx).apply { useController = false; setKeepContentOnPlayerReset(false); resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT; this.player = player } },
                    update = { it.player = player },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 72.dp, bottom = 170.dp)
                        .offset { IntOffset(0, swipeOffset.value.roundToInt()) }
                        .zIndex(1f)
                        .pointerInput(currentVideo.uri) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var totalVertical = 0f
                            var totalHorizontal = 0f
                            var releasedChange: androidx.compose.ui.input.pointer.PointerInputChange? = null
                            val longPressWait = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    val change = event.changes.firstOrNull { it.id == down.id } ?: return@withTimeoutOrNull false
                                    totalVertical += change.position.y - change.previousPosition.y
                                    totalHorizontal += change.position.x - change.previousPosition.x
                                    if (!change.pressed) {
                                        releasedChange = change
                                        return@withTimeoutOrNull false
                                    }
                                    val moved = kotlin.math.hypot(totalVertical.toDouble(), totalHorizontal.toDouble()).toFloat()
                                    if (moved >= viewConfiguration.touchSlop) return@withTimeoutOrNull false
                                }
                                @Suppress("UNREACHABLE_CODE")
                                true
                            }
                            val fastForwarding = longPressWait == null
                            val speedStep = 48.dp.toPx()
                            if (fastForwarding) {
                                heldSpeed = 2
                                player.setPlaybackParameters(PlaybackParameters(2f))
                                var released = false
                                while (!released) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                    totalVertical += change.position.y - change.previousPosition.y
                                    totalHorizontal += change.position.x - change.previousPosition.x
                                    val speed = (2f - totalVertical / speedStep).roundToInt().coerceIn(1, 5)
                                    player.setPlaybackParameters(PlaybackParameters(speed.toFloat()))
                                    heldSpeed = speed
                                    change.consume()
                                    released = !change.pressed
                                }
                                player.setPlaybackParameters(PlaybackParameters(1f))
                                heldSpeed = null
                            } else {
                                var released = releasedChange != null
                                var swipeJob: Job? = null
                                while (!released) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                    totalVertical += change.position.y - change.previousPosition.y
                                    totalHorizontal += change.position.x - change.previousPosition.x
                                    if (kotlin.math.abs(totalVertical) > kotlin.math.abs(totalHorizontal) && kotlin.math.abs(totalVertical) > viewConfiguration.touchSlop) {
                                        val dragOffset = totalVertical.coerceIn(-contentHeight, contentHeight)
                                        swipeJob?.cancel()
                                        swipeJob = swipeScope.launch { swipeOffset.snapTo(dragOffset) }
                                        change.consume()
                                    }
                                    released = !change.pressed
                                }
                                val swipeThreshold = contentHeight * 0.22f
                                val nextIndex = when {
                                    totalVertical < -swipeThreshold -> currentIndex + 1
                                    totalVertical > swipeThreshold -> currentIndex - 1
                                    else -> -1
                                }
                                if (nextIndex in videos.indices) {
                                    val outgoingVideo = currentVideo
                                    val targetVideo = videos[nextIndex]
                                    val finalOffset = if (totalVertical < 0f) -contentHeight else contentHeight
                                    swipeJob?.cancel()
                                    swipeScope.launch {
                                        swipeOffset.animateTo(finalOffset, tween(180))
                                        currentUri = targetVideo.uri
                                        swipeOffset.snapTo(0f)
                                        outgoingVideo?.let(onKeep)
                                    }
                                } else if (kotlin.math.abs(totalVertical) > viewConfiguration.touchSlop) {
                                    swipeJob?.cancel()
                                    swipeScope.launch { swipeOffset.animateTo(0f, tween(180)) }
                                } else if (releasedChange != null) {
                                    togglePlayback()
                                }
                            }
                        }
                        },
                )
            if (renderedVideoUri != currentVideo.uri) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 72.dp, bottom = 170.dp)
                        .zIndex(2f),
                ) {
                    VideoPoster(currentVideo)
                }
            }
            Column(Modifier.fillMaxSize().padding(WindowInsets.safeDrawing.asPaddingValues()).zIndex(3f)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Color.White) }
                    Column(Modifier.weight(1f)) {
                        Text("整理视频", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${currentIndex + 1} / ${videos.size}", color = Color.White.copy(alpha = .75f), style = MaterialTheme.typography.labelSmall)
                        Text("上下滑动切换视频", color = Color.White.copy(alpha = .62f), style = MaterialTheme.typography.labelSmall)
                    }
                    if (canUndo) IconButton(onClick = onUndo) { Icon(Icons.Filled.Undo, "撤回上一次暂存删除", tint = Color.White) }
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
                Surface(color = Color(0xFF111111).copy(alpha = .92f), modifier = Modifier.fillMaxWidth()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(categories, key = { it.name }) { target ->
                            TextButton(
                                onClick = {
                                    currentVideo?.let { video ->
                                        when (onMove(video, target)) {
                                            VideoMoveResult.MOVED -> currentUri = videos.getOrNull(currentIndex + 1)?.uri
                                                ?: videos.getOrNull(currentIndex - 1)?.uri
                                            VideoMoveResult.NEEDS_CONFIRMATION -> Unit
                                            VideoMoveResult.FAILED -> Toast.makeText(context, "视频移动失败，原视频已保留", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                            ) {
                                Icon(Icons.Filled.FolderOpen, contentDescription = null, tint = Color(0xFFB9E7C8), modifier = Modifier.size(16.dp))
                                Text(" ${target.name}", color = Color.White, maxLines = 1)
                            }
                        }
                    }
                }
                Column(Modifier.fillMaxWidth().background(Color(0xFF111111)).padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Text(formatMediaDate(currentVideo.dateMillis), color = Color.White.copy(alpha = .72f), maxLines = 1, style = MaterialTheme.typography.labelSmall, modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp))
                    Text(currentVideo.name, color = Color.White, maxLines = 1, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp))
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
                        IconButton(onClick = { togglePlayback() }) {
                            Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, if (isPlaying) "暂停视频" else "播放视频", tint = Color.White)
                        }
                        Button(onClick = {
                            val nextVideo = videos.getOrNull(currentIndex + 1) ?: videos.getOrNull(currentIndex - 1)
                            currentUri = nextVideo?.uri
                            currentVideo?.let(onKeep)
                        }, modifier = Modifier.width(80.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Ink)) { Text("保留", maxLines = 1) }
                        Button(onClick = {
                            val nextVideo = videos.getOrNull(currentIndex + 1) ?: videos.getOrNull(currentIndex - 1)
                            currentUri = nextVideo?.uri
                            onStageDelete(currentVideo)
                        }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF343434), contentColor = Color.White)) {
                            Icon(Icons.Filled.DeleteOutline, null)
                            Text("  暂存删除")
                        }
                    }
                }
            }
            heldSpeed?.let { speed ->
                Box(Modifier.fillMaxSize().zIndex(5f)) {
                    Row(
                        Modifier.align(Alignment.TopCenter).padding(top = 104.dp),
                        horizontalArrangement = Arrangement.spacedBy(9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.Bottom) {
                            (1..5).forEach { level ->
                                val active = level == speed
                                Box(
                                    Modifier
                                        .width(if (active) 5.dp else 3.dp)
                                        .height((7 + level * 3 + if (active) 4 else 0).dp)
                                        .background(if (level <= speed) Color(0xFFFFC107) else Color.White.copy(alpha = .45f), RoundedCornerShape(2.dp))
                                )
                            }
                        }
                        Column {
                            Text("x$speed", color = Color(0xFFFFC107), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text("上下滑动改变播放速度", color = Color.White, style = MaterialTheme.typography.labelSmall, maxLines = 1)
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
            TextButton(onClick = { onDelete(if (selected.isEmpty()) videos else videos.filter { it.uri in selected }); selected = emptySet() }) { Icon(Icons.Filled.Delete, null); Text("  删除") }
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
}

private fun formatDuration(durationMillis: Long): String {
    val seconds = durationMillis / 1000
    return "%d:%02d".format(seconds / 60, seconds % 60)
}

private fun formatMediaDate(dateMillis: Long): String =
    if (dateMillis > 0L) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(dateMillis)) else "日期未知"

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
        context.contentResolver.query(children, arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_MIME_TYPE, DocumentsContract.Document.COLUMN_LAST_MODIFIED, DocumentsContract.Document.COLUMN_SIZE), null, null, null)?.use { c ->
            while (c.moveToNext()) if (c.getString(2)?.startsWith("image/") == true) {
                val id = c.getString(0)
                val size = if (c.isNull(4)) 0L else c.getLong(4)
                uris += PhotoItem(DocumentsContract.buildDocumentUriUsingTree(folderUri, id), c.getString(1), category.folderUri, c.getLong(3), size)
            }
        }
    } else context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED, MediaStore.MediaColumns.SIZE), "${MediaStore.Images.Media.RELATIVE_PATH}=?", arrayOf("DCIM/${category.name}/"), "${MediaStore.Images.Media.DATE_ADDED} DESC")?.use { c -> while (c.moveToNext()) uris += PhotoItem(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c.getString(0)), c.getString(1), "DCIM/${category.name}", c.getLong(2) * 1000L, if (c.isNull(3)) 0L else c.getLong(3)) }
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
    val byUri = LinkedHashMap<String, PhotoItem>()
    val volumes = runCatching { MediaStore.getExternalVolumeNames(context) }.getOrDefault(emptySet())
    (volumes.ifEmpty { setOf("external_primary") }).forEach { volumeName ->
        val collection = MediaStore.Images.Media.getContentUri(volumeName)
        runCatching {
            context.contentResolver.query(
                collection,
                arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.RELATIVE_PATH, MediaStore.MediaColumns.SIZE),
                null, null, "${MediaStore.Images.Media.DATE_ADDED} DESC",
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val item = PhotoItem(
                        Uri.withAppendedPath(collection, cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getLong(2) * 1000L,
                        if (cursor.isNull(4)) 0L else cursor.getLong(4),
                    )
                    byUri.putIfAbsent(item.uri.toString(), item)
                }
            }
        }
    }
    categories.forEach { category ->
        queryPhotos(context, category).forEach { item -> byUri.putIfAbsent(item.uri.toString(), item) }
    }
    return byUri.values.sortedByDescending { it.dateMillis }
}

private fun queryAllVideos(context: Context): List<VideoItem> {
    val videos = mutableListOf<VideoItem>()
    val volumes = runCatching { MediaStore.getExternalVolumeNames(context) }.getOrDefault(emptySet())
    (volumes.ifEmpty { setOf("external_primary") }).forEach { volumeName ->
        val collection = MediaStore.Video.Media.getContentUri(volumeName)
        runCatching {
            context.contentResolver.query(
                collection,
                arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATE_ADDED, MediaStore.MediaColumns.SIZE, MediaStore.Video.Media.RELATIVE_PATH),
                null, null, "${MediaStore.Video.Media.DATE_ADDED} DESC",
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    videos += VideoItem(
                        uri = Uri.withAppendedPath(collection, cursor.getString(0)),
                        name = cursor.getString(1) ?: "video",
                        durationMillis = cursor.getLong(2),
                        dateMillis = cursor.getLong(3) * 1000L,
                        sizeBytes = if (cursor.isNull(4)) 0L else cursor.getLong(4),
                        sourceKey = cursor.getString(5),
                    )
                }
            }
        }
    }
    return videos.sortedByDescending { it.dateMillis }
}
private fun queryMediaStorage(context: Context): MediaStorage = MediaStorage(
    photoBytes = queryMediaBytes(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
    videoBytes = queryMediaBytes(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI),
)

private fun queryMediaBytes(context: Context, collection: Uri): Long {
    var total = 0L
    runCatching {
        context.contentResolver.query(collection, arrayOf(MediaStore.MediaColumns.SIZE), null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
            if (sizeIndex >= 0) {
                while (cursor.moveToNext()) {
                    if (!cursor.isNull(sizeIndex)) total += cursor.getLong(sizeIndex).coerceAtLeast(0L)
                }
            }
        }
    }
    return total
}

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024L) return "$bytes B"
    val units = arrayOf("KB", "MB", "GB", "TB")
    var value = bytes.toDouble()
    var unitIndex = -1
    while (value >= 1024.0 && unitIndex < units.lastIndex) {
        value /= 1024.0
        unitIndex++
    }
    return if (value >= 100.0 || unitIndex == 0) "%.0f %s".format(Locale.getDefault(), value, units[unitIndex])
    else "%.1f %s".format(Locale.getDefault(), value, units[unitIndex])
}

private fun isMediaStorePhoto(context: Context, photo: PhotoItem): Boolean =
    photo.uri.authority == "media" && !DocumentsContract.isDocumentUri(context, photo.uri)

private fun deletePhoto(context: Context, photo: PhotoItem): Boolean = runCatching {
    if (DocumentsContract.isDocumentUri(context, photo.uri)) DocumentsContract.deleteDocument(context.contentResolver, photo.uri)
    else context.contentResolver.delete(photo.uri, null, null) > 0
}.getOrDefault(false)

private data class MediaStoreTarget(val volumeName: String, val relativePath: String)

private fun mediaStoreTarget(target: Category): MediaStoreTarget? {
    if (target.folderUri == null) return MediaStoreTarget("external_primary", "DCIM/${target.name}/")
    val targetUri = Uri.parse(target.folderUri)
    val documentId = runCatching { DocumentsContract.getDocumentId(targetUri) }
        .getOrElse { runCatching { DocumentsContract.getTreeDocumentId(targetUri) }.getOrNull() }
        ?: return null
    val volumeAndPath = documentId.split(':', limit = 2)
    if (volumeAndPath.size != 2) return null
    val volumeName = when (volumeAndPath[0].lowercase(Locale.ROOT)) {
        "primary", "external_primary" -> "external_primary"
        else -> volumeAndPath[0]
    }
    val path = volumeAndPath[1].trim('/')
    return MediaStoreTarget(volumeName, if (path.isEmpty()) "" else "$path/")
}

private fun moveMediaStoreItem(context: Context, uri: Uri, target: Category): Boolean? {
    if (uri.authority != "media") return null
    val destination = mediaStoreTarget(target) ?: return null
    val sourceVolume = runCatching { MediaStore.getVolumeName(uri) }.getOrNull() ?: return null
    if (!sourceVolume.equals(destination.volumeName, ignoreCase = true)) return null
    val values = ContentValues().apply { put(MediaStore.MediaColumns.RELATIVE_PATH, destination.relativePath) }
    return context.contentResolver.update(uri, values, null, null) > 0
}

private fun movePhoto(context: Context, photo: PhotoItem, target: Category): Boolean {
    val targetKey = target.folderUri ?: "DCIM/${target.name}"
    if (photo.sourceKey == targetKey) return true
    val resolver = context.contentResolver
    if (target.folderUri != null && DocumentsContract.isDocumentUri(context, photo.uri) && photo.sourceKey != null) {
        val sourceParent = Uri.parse(photo.sourceKey)
        val targetParent = Uri.parse(target.folderUri)
        if (sourceParent.authority == targetParent.authority) {
            val movedUri = runCatching {
                DocumentsContract.moveDocument(resolver, photo.uri, sourceParent, targetParent)
            }.getOrNull()
            if (movedUri != null) return true
        }
    }
    val directMove = moveMediaStoreItem(context, photo.uri, target)
    if (directMove != null) return directMove
    val targetUri = runCatching {
        if (target.folderUri != null) {
            DocumentsContract.createDocument(resolver, Uri.parse(target.folderUri), resolver.getType(photo.uri) ?: "image/jpeg", photo.name)
        } else {
            val destination = mediaStoreTarget(target) ?: return false
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, photo.name)
                put(MediaStore.Images.Media.MIME_TYPE, resolver.getType(photo.uri) ?: "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, destination.relativePath)
            }
            resolver.insert(MediaStore.Images.Media.getContentUri(destination.volumeName), values)
        }
    }.getOrNull() ?: return false
    val copied = runCatching {
        resolver.openInputStream(photo.uri)?.use { input ->
            resolver.openOutputStream(targetUri, "w")?.use { output -> input.copyTo(output) }
                ?: error("Unable to open destination photo")
        } ?: error("Unable to open source photo")
        true
    }.getOrDefault(false)
    if (!copied || !deletePhoto(context, photo)) {
        runCatching { deletePhoto(context, PhotoItem(targetUri, photo.name)) }
        return false
    }
    return true
}
private fun moveVideoToCategory(context: Context, video: VideoItem, target: Category): Boolean {
    val directMove = moveMediaStoreItem(context, video.uri, target)
    if (directMove != null) return directMove
    val resolver = context.contentResolver
    val destination = mediaStoreTarget(target)
    val targetUri = runCatching {
        if (target.folderUri != null) {
            DocumentsContract.createDocument(
                resolver,
                Uri.parse(target.folderUri),
                resolver.getType(video.uri) ?: "video/mp4",
                video.name,
            )
        } else {
            val folder = destination ?: return false
            val values = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, video.name)
                put(MediaStore.Video.Media.MIME_TYPE, resolver.getType(video.uri) ?: "video/mp4")
                put(MediaStore.MediaColumns.RELATIVE_PATH, folder.relativePath)
            }
            resolver.insert(MediaStore.Video.Media.getContentUri(folder.volumeName), values)
        }
    }.getOrNull() ?: return false
    val copied = runCatching {
        resolver.openInputStream(video.uri)?.use { source ->
            resolver.openOutputStream(targetUri, "w")?.use { destinationStream ->
                source.copyTo(destinationStream)
            } ?: error("Unable to open destination video")
        } ?: error("Unable to open source video")
        true
    }.getOrDefault(false)
    if (!copied || !deleteVideoUri(context, video.uri)) {
        runCatching { deleteVideoUri(context, targetUri) }
        return false
    }
    return true
}
private fun deleteVideoUri(context: Context, uri: Uri): Boolean = runCatching {
    if (DocumentsContract.isDocumentUri(context, uri)) DocumentsContract.deleteDocument(context.contentResolver, uri)
    else context.contentResolver.delete(uri, null, null) > 0
}.getOrDefault(false)

private fun safeFolderName(value: String): String = value
    .trim()
    .replace(Regex("[/\\\\:*?\"<>|]"), "_")
    .trim('.')
