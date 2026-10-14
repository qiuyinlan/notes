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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import java.io.File

private val Ink = Color(0xFF1C2421)
private val Moss = Color(0xFF315D4A)
private val Mint = Color(0xFFE4F0E8)
private val Paper = Color(0xFFF5F7F4)
private val Muted = Color(0xFF707A74)

private data class Category(val name: String, val folderUri: String? = null)
private data class PhotoItem(val uri: Uri, val name: String, val sourceKey: String? = null, val dateMillis: Long = 0L)

private enum class AppScreen { HOME, CATEGORY, ORGANIZE, TRASH }

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
    var categories by remember { mutableStateOf(CategoryStore.load(context)) }
    var selectedCategory by rememberSaveable { mutableStateOf(categories.firstOrNull()?.name ?: "日常") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var cameraOpen by rememberSaveable { mutableStateOf(false) }
    var screen by rememberSaveable { mutableStateOf(AppScreen.HOME) }
    var previewCategory by remember { mutableStateOf<Category?>(null) }
    var organizePhotos by remember { mutableStateOf<List<PhotoItem>>(emptyList()) }
    var trash by remember { mutableStateOf<List<PhotoItem>>(emptyList()) }
    var pendingPreview by remember { mutableStateOf<Category?>(null) }
    var pendingOrganize by remember { mutableStateOf(false) }
    var pendingCategoryName by remember { mutableStateOf("") }
    val photoPermission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
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
        val name = safeFolderName(pendingCategoryName)
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
            onDelete = { name ->
                categories = categories.filterNot { it.name == name }
                CategoryStore.save(context, categories)
            },
            onShoot = { cameraOpen = true },
            onShootCategory = { category -> selectedCategory = category.name; cameraOpen = true },
            onOrganize = {
                if (ContextCompat.checkSelfPermission(context, photoPermission) != PackageManager.PERMISSION_GRANTED) {
                    pendingOrganize = true
                    photoPermissionLauncher.launch(photoPermission)
                } else {
                    organizePhotos = queryAllPhotos(context, categories)
                    screen = AppScreen.ORGANIZE
                }
            },
            onTrash = { screen = AppScreen.TRASH },
        )
        AppScreen.CATEGORY -> previewCategory?.let { category ->
            CategoryPhotosScreen(
                category = category,
                categories = categories,
                onBack = { screen = AppScreen.HOME },
                onShoot = { selectedCategory = category.name; cameraOpen = true },
                onStageDelete = { photos -> trash = (trash + photos).distinctBy { it.uri } },
            )
        }
        AppScreen.ORGANIZE -> OrganizePhotosScreen(
            photos = organizePhotos,
            categories = categories,
            onBack = { screen = AppScreen.HOME },
            onStageDelete = { photos -> trash = (trash + photos).distinctBy { it.uri } },
            onTrash = { screen = AppScreen.TRASH },
            trashCount = trash.size,
        )
        AppScreen.TRASH -> TrashScreen(
            photos = trash,
            onBack = { screen = AppScreen.HOME },
            onRestore = { restored -> trash = trash.filterNot { item -> restored.any { it.uri == item.uri } } },
            onDelete = { deleted ->
                deleted.forEach { deletePhoto(context, it) }
                trash = trash.filterNot { item -> deleted.any { it.uri == item.uri } }
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
}

@Composable
private fun HomeScreen(
    categories: List<Category>,
    selectedCategory: String,
    trashCount: Int,
    onSelect: (Category) -> Unit,
    onAdd: () -> Unit,
    onDelete: (String) -> Unit,
    onShoot: () -> Unit,
    onShootCategory: (Category) -> Unit,
    onOrganize: () -> Unit,
    onTrash: () -> Unit,
) {
    Scaffold(containerColor = Paper) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 22.dp),
        ) {
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("分类相机", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
                    Text("先选相册，再开始拍摄", style = MaterialTheme.typography.bodyMedium, color = Muted, modifier = Modifier.padding(top = 5.dp))
                }
                Surface(shape = CircleShape, color = Mint, modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = "本地相册", tint = Moss, modifier = Modifier.size(21.dp))
                    }
                }
            }
            Spacer(Modifier.height(25.dp))
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Moss),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Album, contentDescription = null, tint = Color.White.copy(alpha = .85f))
                        Text("  当前分类", color = Color.White.copy(alpha = .8f), style = MaterialTheme.typography.labelLarge)
                    }
                    Text(selectedCategory, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 11.dp))
                    Text("保存到 ${categories.firstOrNull { it.name == selectedCategory }?.displayLocation() ?: "DCIM/$selectedCategory"}", color = Color.White.copy(alpha = .78f), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    Button(
                        onClick = onShoot,
                        modifier = Modifier.fillMaxWidth().padding(top = 18.dp).height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Moss),
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null)
                        Text("  拍摄", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 26.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("我的分类", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("${categories.size} 个", style = MaterialTheme.typography.labelMedium, color = Muted)
                IconButton(onClick = onAdd) { Icon(Icons.Filled.Add, contentDescription = "新建分类", tint = Moss) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onOrganize, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
                    Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("  整理照片")
                }
                if (trashCount > 0) {
                    IconButton(onClick = onTrash, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "删除暂存区", tint = Moss)
                    }
                }
            }
            if (categories.isEmpty()) {
                Surface(color = Color.White, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.FolderOpen, contentDescription = null, tint = Muted, modifier = Modifier.size(28.dp))
                        Text("还没有分类", color = Muted, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = Muted, modifier = Modifier.size(15.dp))
                Text("  照片只保存在手机本地", style = MaterialTheme.typography.labelSmall, color = Muted)
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
            IconButton(onClick = onShoot) {
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
                Text("默认保存到 DCIM/分类名，也可以选择手机里已有的文件夹。", style = MaterialTheme.typography.bodySmall, color = Muted, modifier = Modifier.padding(top = 10.dp))
            }
        },
        confirmButton = {
            Row {
                TextButton(onClick = { onPickFolder(name) }, enabled = name.isNotBlank()) { Text("选择已有文件夹") }
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
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission = granted
    }
    var showCategoryMenu by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    DisposableEffect(hasPermission, category.name, previewView) {
        val view = previewView
        if (hasPermission && view != null) {
            var disposed = false
            val providerFuture = ProcessCameraProvider.getInstance(context)
            val listener = Runnable {
                try {
                    val provider = providerFuture.get()
                    if (disposed) return@Runnable
                    val preview = Preview.Builder().build().also { it.surfaceProvider = view.surfaceProvider }
                    val imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
                    provider.unbindAll()
                    provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
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
                    onClick = { takePhoto(context, capture, category, cameraExecutor) },
                    enabled = capture != null,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 30.dp).size(76.dp),
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
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$timestamp.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (category.folderUri == null) put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/${category.name}")
    }
    val targetDoc = category.folderUri?.let { DocumentsContract.createDocument(context.contentResolver, Uri.parse(it), "image/jpeg", "$timestamp.jpg") }
    if (category.folderUri != null && targetDoc == null) {
        Toast.makeText(context, "无法在所选文件夹中创建照片", Toast.LENGTH_LONG).show()
        return
    }
    val temp = if (targetDoc != null) File(context.cacheDir, "$timestamp.jpg") else null
    val output = if (temp != null) ImageCapture.OutputFileOptions.Builder(temp).build()
    else ImageCapture.OutputFileOptions.Builder(context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values).build()
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
private fun CategoryPhotosScreen(category: Category, categories: List<Category>, onBack: () -> Unit, onShoot: () -> Unit, onStageDelete: (List<PhotoItem>) -> Unit) {
    val context = LocalContext.current
    var photos by remember(category) { mutableStateOf(queryPhotos(context, category)) }
    var selecting by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var viewerIndex by remember { mutableStateOf<Int?>(null) }
    BackHandler(onBack = onBack)
    Scaffold(containerColor = Paper, topBar = {
        Row(Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") }
            Column(Modifier.weight(1f)) {
                Text(category.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${photos.size} 张照片", style = MaterialTheme.typography.labelSmall, color = Muted)
            }
            if (selecting && selected.isNotEmpty()) {
                IconButton(onClick = {
                    val removed = photos.filter { it.uri in selected }
                    photos = photos.filterNot { it.uri in selected }
                    selected = emptySet()
                    selecting = false
                    onStageDelete(removed)
                }) { Icon(Icons.Filled.DeleteOutline, "暂存删除", tint = Moss) }
            }
            IconButton(onClick = { selecting = !selecting; selected = emptySet() }) {
                Icon(Icons.Filled.Check, if (selecting) "完成选择" else "选择", tint = Moss)
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
            onStageDelete = { photo ->
                photos = photos.filterNot { it.uri == photo.uri }
                onStageDelete(listOf(photo))
            },
            onMove = { photo, target ->
                if (movePhoto(context, photo, target)) {
                    photos = photos.filterNot { it.uri == photo.uri }
                    viewerIndex = null
                }
            },
        )
    }
}

@Composable
private fun PhotoThumb(photo: PhotoItem, selected: Boolean = false, onClick: () -> Unit) {
    val context = LocalContext.current
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = photo.uri) { value = withContext(Dispatchers.IO) { runCatching { ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, photo.uri)) }.getOrNull() } }
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
    onStageDelete: (PhotoItem) -> Unit,
    onMove: (PhotoItem, Category) -> Unit,
) {
    if (photos.isEmpty()) { onClose(); return }
    val pagerState = rememberPagerState(initialPage = initialIndex.coerceIn(0, photos.lastIndex), pageCount = { photos.size })
    val scope = rememberCoroutineScope()
    BackHandler(onBack = onClose)
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            PhotoPageImage(photos[page])
        }
        Row(Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) { Icon(Icons.Filled.Close, "关闭", tint = Color.White) }
            Text("${pagerState.currentPage + 1} / ${photos.size}", color = Color.White, modifier = Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 28.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            TextButton(onClick = {
                if (pagerState.currentPage < photos.lastIndex) scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } else onClose()
            }) { Text("保留", color = Color.White) }
            TextButton(onClick = {
                val photo = photos[pagerState.currentPage]
                onStageDelete(photo)
                if (photos.size == 1) onClose() else if (pagerState.currentPage >= photos.lastIndex) scope.launch { pagerState.animateScrollToPage(photos.lastIndex - 1) }
            }) { Icon(Icons.Filled.DeleteOutline, null, tint = Color.White); Text("  暂存删除", color = Color.White) }
        }
        LazyRow(Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 76.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), contentPadding = PaddingValues(horizontal = 18.dp)) {
            items(categories, key = { it.name }) { target ->
                Surface(onClick = { onMove(photos[pagerState.currentPage], target) }, color = Color.White.copy(alpha = .9f), shape = RoundedCornerShape(18.dp)) {
                    Text(target.name, color = Ink, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun PhotoPageImage(photo: PhotoItem) {
    val context = LocalContext.current
    val bitmapState = produceState<Bitmap?>(initialValue = null, key1 = photo.uri) { value = withContext(Dispatchers.IO) { runCatching { ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, photo.uri)) }.getOrNull() } }
    val bitmap: Bitmap? = bitmapState.value
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (bitmap != null) Image(bitmap = bitmap.asImageBitmap(), contentDescription = photo.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
    }
}

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
    var selected by remember { mutableStateOf<Set<Uri>>(emptySet()) }
    var selecting by remember { mutableStateOf(false) }
    var viewerIndex by remember { mutableStateOf<Int?>(null) }
    var mode by remember { mutableStateOf("随机整理") }
    var countText by remember { mutableStateOf("20") }
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    var visiblePhotos by remember(photos) { mutableStateOf(photos.shuffled().take(20)) }
    val count = countText.toIntOrNull()?.coerceIn(1, 500) ?: 20
    val monthOptions = remember(photos) {
        photos.mapNotNull { if (it.dateMillis > 0) SimpleDateFormat("yyyy年MM月", Locale.CHINA).format(Date(it.dateMillis)) else null }.distinct()
    }
    LaunchedEffect(mode, countText, selectedMonth, photos) {
        visiblePhotos = if (mode == "随机整理") photos.shuffled().take(count)
        else photos.filter { item -> selectedMonth == null || (item.dateMillis > 0 && SimpleDateFormat("yyyy年MM月", Locale.CHINA).format(Date(item.dateMillis)) == selectedMonth) }
    }
    BackHandler(onBack = onBack)
    Scaffold(containerColor = Paper, topBar = {
        Row(Modifier.fillMaxWidth().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") }
            Column(Modifier.weight(1f)) { Text("整理照片", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("本次 ${photos.size} 张", style = MaterialTheme.typography.labelSmall, color = Muted) }
            if (trashCount > 0) IconButton(onClick = onTrash) { Icon(Icons.Filled.DeleteOutline, "删除暂存区", tint = Moss) }
        }
    }) { p ->
        Column(Modifier.fillMaxSize().padding(p)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("随机整理", "按月份整理").forEach { item ->
                    TextButton(onClick = { mode = item }) { Text(item, color = if (mode == item) Moss else Muted, fontWeight = if (mode == item) FontWeight.Bold else FontWeight.Normal) }
                }
            }
            if (mode == "随机整理") {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("本次整理", style = MaterialTheme.typography.bodySmall, color = Muted)
                    OutlinedTextField(value = countText, onValueChange = { countText = it.filter(Char::isDigit).take(3) }, singleLine = true, modifier = Modifier.padding(start = 8.dp).width(90.dp), label = { Text("张数") })
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
                if (selecting && selected.isNotEmpty()) IconButton(onClick = { onStageDelete(visiblePhotos.filter { it.uri in selected }); selected = emptySet(); selecting = false }) { Icon(Icons.Filled.DeleteOutline, "暂存删除", tint = Moss) }
                IconButton(onClick = { selecting = !selecting; selected = emptySet() }) { Icon(Icons.Filled.Check, "选择", tint = Moss) }
            }
            if (visiblePhotos.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("没有找到可整理的照片", color = Muted) }
            else LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxSize(), contentPadding = PaddingValues(4.dp)) {
                items(visiblePhotos, key = { it.uri.toString() }) { photo ->
                    PhotoThumb(photo, photo.uri in selected) {
                        if (selecting) selected = if (photo.uri in selected) selected - photo.uri else selected + photo.uri
                        else viewerIndex = visiblePhotos.indexOfFirst { it.uri == photo.uri }
                    }
                }
            }
        }
    }
    viewerIndex?.let { index -> PhotoViewerScreen(visiblePhotos, index, categories, { viewerIndex = null }, { photo -> onStageDelete(listOf(photo)) }, { photo, target -> if (movePhoto(context, photo, target)) viewerIndex = null }) }
}

@Composable
private fun TrashScreen(
    photos: List<PhotoItem>,
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
            Text("删除暂存区", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("${photos.size} 张", style = MaterialTheme.typography.labelMedium, color = Muted)
        }
    }, bottomBar = {
        if (photos.isNotEmpty()) Row(Modifier.fillMaxWidth().background(Color.White).padding(WindowInsets.safeDrawing.asPaddingValues()).padding(horizontal = 22.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            TextButton(onClick = { onRestore(if (selected.isEmpty()) photos else photos.filter { it.uri in selected }); selected = emptySet() }) { Icon(Icons.Filled.Undo, null); Text("  恢复") }
            TextButton(onClick = { confirmDelete = true }) { Icon(Icons.Filled.Delete, null); Text("  删除") }
        }
    }) { p ->
        if (photos.isEmpty()) Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { Text("暂存区为空", color = Muted) }
        else LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(4.dp)) {
            items(photos, key = { it.uri.toString() }) { photo -> PhotoThumb(photo, photo.uri in selected) { selected = if (photo.uri in selected) selected - photo.uri else selected + photo.uri } }
        }
    }
    if (confirmDelete) AlertDialog(onDismissRequest = { confirmDelete = false }, title = { Text("确认永久删除？") }, text = { Text(if (selected.isEmpty()) "将删除暂存区内的全部照片。" else "将删除已选择的 ${selected.size} 张照片。") }, confirmButton = { TextButton(onClick = { onDelete(if (selected.isEmpty()) photos else photos.filter { it.uri in selected }); selected = emptySet(); confirmDelete = false }) { Text("确认删除") } }, dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("取消") } })
}

private fun queryPhotos(context: Context, category: Category): List<PhotoItem> {
    val uris = mutableListOf<PhotoItem>()
    if (category.folderUri != null) runCatching { context.contentResolver.query(DocumentsContract.buildChildDocumentsUriUsingTree(Uri.parse(category.folderUri), DocumentsContract.getTreeDocumentId(Uri.parse(category.folderUri))), arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_MIME_TYPE, DocumentsContract.Document.COLUMN_LAST_MODIFIED), null, null, null)?.use { c -> while (c.moveToNext()) if (c.getString(2)?.startsWith("image/") == true) { val id = c.getString(0); uris += PhotoItem(DocumentsContract.buildDocumentUriUsingTree(Uri.parse(category.folderUri), id), c.getString(1), category.folderUri, c.getLong(3)) } } } else context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED), "${MediaStore.Images.Media.RELATIVE_PATH}=?", arrayOf("DCIM/${category.name}/"), "${MediaStore.Images.Media.DATE_ADDED} DESC")?.use { c -> while (c.moveToNext()) uris += PhotoItem(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c.getString(0)), c.getString(1), "DCIM/${category.name}", c.getLong(2) * 1000L) }
    return uris
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

private fun deletePhoto(context: Context, photo: PhotoItem) {
    runCatching {
        if (DocumentsContract.isDocumentUri(context, photo.uri)) DocumentsContract.deleteDocument(context.contentResolver, photo.uri)
        else context.contentResolver.delete(photo.uri, null, null)
    }
}

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
