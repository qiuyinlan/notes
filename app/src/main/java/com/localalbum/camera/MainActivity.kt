package com.localalbum.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

private val Ink = Color(0xFF1C2421)
private val Moss = Color(0xFF315D4A)
private val Mint = Color(0xFFE4F0E8)
private val Paper = Color(0xFFF5F7F4)
private val Muted = Color(0xFF707A74)

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
    var selectedCategory by rememberSaveable { mutableStateOf(categories.firstOrNull() ?: "日常") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var cameraOpen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(categories) {
        if (categories.isNotEmpty() && selectedCategory !in categories) selectedCategory = categories.first()
    }

    if (cameraOpen) {
        CameraScreen(
            category = selectedCategory,
            onBack = { cameraOpen = false },
            onCategoryChange = { selectedCategory = it },
            categories = categories,
        )
    } else {
        HomeScreen(
            categories = categories,
            selectedCategory = selectedCategory,
            onSelect = { selectedCategory = it },
            onAdd = { showCreateDialog = true },
            onDelete = { name ->
                categories = categories - name
                CategoryStore.save(context, categories)
            },
            onShoot = { cameraOpen = true },
        )
    }

    if (showCreateDialog) {
        CreateCategoryDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { rawName ->
                val name = safeFolderName(rawName)
                if (name.isNotBlank() && name !in categories) {
                    categories = categories + name
                    selectedCategory = name
                    CategoryStore.save(context, categories)
                }
                showCreateDialog = false
            },
        )
    }
}

@Composable
private fun HomeScreen(
    categories: List<String>,
    selectedCategory: String,
    onSelect: (String) -> Unit,
    onAdd: () -> Unit,
    onDelete: (String) -> Unit,
    onShoot: () -> Unit,
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
                        Icon(Icons.Filled.Lock, contentDescription = "仅保存在本机", tint = Moss, modifier = Modifier.size(21.dp))
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
                    Text("照片将保存到 DCIM/$selectedCategory", color = Color.White.copy(alpha = .78f), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
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
            if (categories.isEmpty()) {
                Surface(color = Color.White, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.FolderOpen, contentDescription = null, tint = Muted, modifier = Modifier.size(28.dp))
                        Text("还没有分类", color = Muted, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories, key = { it }) { name ->
                        CategoryRow(
                            name = name,
                            selected = name == selectedCategory,
                            canDelete = categories.size > 1,
                            onClick = { onSelect(name) },
                            onDelete = { onDelete(name) },
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
private fun CategoryRow(name: String, selected: Boolean, canDelete: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
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
                Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Ink)
                Text(if (selected) "拍摄将存入此分类" else "本地相册文件夹", style = MaterialTheme.typography.labelSmall, color = Muted)
            }
            if (selected) {
                Text("已选", style = MaterialTheme.typography.labelSmall, color = Moss, modifier = Modifier.padding(horizontal = 8.dp))
            }
            if (canDelete) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "删除分类 $name", tint = Muted, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun CreateCategoryDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建分类") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.take(30) },
                singleLine = true,
                label = { Text("分类名称") },
                placeholder = { Text("例如：旅行、家人") },
            )
        },
        confirmButton = { TextButton(onClick = { onCreate(name) }, enabled = name.isNotBlank()) { Text("创建") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}

@Composable
private fun CameraScreen(
    category: String,
    categories: List<String>,
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

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    DisposableEffect(hasPermission, category, previewView) {
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
                    Text(category, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                if (categories.size > 1) {
                    IconButton(onClick = {
                        val current = categories.indexOf(category)
                        onCategoryChange(categories[(current + 1).mod(categories.size)])
                    }) {
                        Icon(Icons.Filled.SwapHoriz, contentDescription = "切换分类", tint = Color.White)
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
                Text("保存到 DCIM/$category", color = Color.White.copy(alpha = .78f), style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 14.dp))
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

private fun takePhoto(context: Context, imageCapture: ImageCapture?, category: String, executor: Executor) {
    val currentCapture = imageCapture ?: return
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(Date())
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "${timestamp}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/$category")
    }
    val output = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values,
    ).build()
    currentCapture.takePicture(output, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            (context as? ComponentActivity)?.runOnUiThread {
                Toast.makeText(context, "已保存到 $category", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onError(exception: ImageCaptureException) {
            (context as? ComponentActivity)?.runOnUiThread {
                Toast.makeText(context, "保存失败：${exception.message ?: "请重试"}", Toast.LENGTH_LONG).show()
            }
        }
    })
}

private object CategoryStore {
    private const val PREFS = "categories"
    private const val KEY_NAMES = "names"

    fun load(context: Context): List<String> = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getStringSet(KEY_NAMES, setOf("日常", "家人", "旅行"))
        .orEmpty()
        .sorted()

    fun save(context: Context, categories: List<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putStringSet(KEY_NAMES, categories.toSet())
            .apply()
    }
}

private fun safeFolderName(value: String): String = value
    .trim()
    .replace(Regex("[/\\\\:*?\"<>|]"), "_")
    .trim('.')
