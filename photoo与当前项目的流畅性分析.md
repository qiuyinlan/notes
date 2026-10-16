# photoo 与当前项目的流畅性分析

## 结论

photoo 加载系统照片更快、滑动更稳定，主要不是因为它使用了某个特殊的 AI 或动画库，而是因为它把照片应用中最容易卡顿的几条路径分开处理了：

1. 媒体目录只做一次后台查询，并复用查询结果。
2. 图片交给统一的图片加载和缓存层，而不是每个 Composable 自己解码 Bitmap。
3. 当前画面、下一张图片和视频首帧提前准备。
4. 手势帧只更新轻量的动画状态，不在拖动过程中做文件读取、EXIF、ML Kit 或数据库操作。
5. 播放器、图片请求和分析任务都有明确的生命周期。

当前项目已经具备 `HorizontalPager`、后台图片解码和稳定列表 key 等基础，但照片查询、图片缓存、视频首帧和状态生命周期还没有形成完整的资源管线。因此用户看到的结果会是：photoo 更快显示第一帧，切换时更少出现空白，连续操作时也更少触发内存压力。

## 一、系统照片为什么 photoo 加载得更快

### 1. photoo 先建立统一媒体目录

photoo 启动时在 `LaunchedEffect` 中使用 `Dispatchers.IO` 查询媒体目录，然后把结果放入 `allMedia`，后续批次和筛选都基于这份内存列表处理：

- [NativeVoiceLikeActivity.kt:1474](.photoo-check/android/app/src/main/com/voicelike/app/NativeVoiceLikeActivity.kt:1474)
- [NativeVoiceLikeActivity.kt:1582](.photoo-check/android/app/src/main/com/voicelike/app/NativeVoiceLikeActivity.kt:1582)
- [NativeVoiceLikeActivity.kt:5553](.photoo-check/android/app/src/main/com/voicelike/app/NativeVoiceLikeActivity.kt:5553)

当前项目有几处不同：

- 点击整理时，`queryAllPhotos(context, categories)` 直接在点击回调中执行：[MainActivity.kt:320](app/src/main/java/com/localalbum/camera/MainActivity.kt:320)
- 权限回调中也直接查询：[MainActivity.kt:256](app/src/main/java/com/localalbum/camera/MainActivity.kt:256)
- 分类页面初始化时，`queryPhotos` 可能在组合阶段执行：[MainActivity.kt:842](app/src/main/java/com/localalbum/camera/MainActivity.kt:842)
- `queryAllPhotos` 还会查询全局照片，再对每个分类重复查询并去重：[MainActivity.kt:1545](app/src/main/java/com/localalbum/camera/MainActivity.kt:1545)

`ContentResolver.query` 不应该被当作轻量的内存读取。照片很多、使用 SAF 文件夹或设备存储较慢时，主线程查询会直接表现为点击后卡顿、界面掉帧，严重时可能触发 ANR 或让后续资源操作堆积。

### 2. photoo 使用统一图片请求缓存

photoo 使用 Coil 的 `AsyncImage` 和 `SubcomposeAsyncImage`。这样同一张图片的请求可以复用内存缓存或磁盘缓存，并且请求在 Composable 离开屏幕时能够取消。加载期间还可以显示骨架图或视频缩略图。

当前项目的缩略图和查看器分别执行独立的 `ImageDecoder.decodeBitmap`：

- 缩略图：480 像素：[MainActivity.kt:903](app/src/main/java/com/localalbum/camera/MainActivity.kt:903)
- 查看器：2048 像素：[MainActivity.kt:1006](app/src/main/java/com/localalbum/camera/MainActivity.kt:1006)

虽然这些解码放在 `Dispatchers.IO` 是正确的，但当前没有统一的内存缓存和磁盘缓存。因此网格滚动回来、查看器切页或 Composable 被重新创建时，可能重复打开 URI、创建 Bitmap、复制像素并等待 GC。

这也是“没有阻塞主线程，但仍然不够丝滑”的典型情况：文件读取在线程池中进行，然而大量 Bitmap 创建仍然会带来 I/O 排队、内存峰值和 GC 抖动。

### 3. photoo 先显示可用画面，再切换到正式画面

photoo 播放视频时，在 ExoPlayer 第一帧准备好之前显示视频缩略图；第一帧渲染后再显示播放器：[NativeVoiceLikeActivity.kt:5392](.photoo-check/android/app/src/main/com/voicelike/app/NativeVoiceLikeActivity.kt:5392)

当前视频页面在切换 URI 后直接执行 `setMediaItem` 和 `prepare`：[MainActivity.kt:1223](app/src/main/java/com/localalbum/camera/MainActivity.kt:1223)。准备期间没有海报帧作为视觉占位，所以用户更容易看到黑屏或空白。即使实际准备时间相同，用户感受到的速度也会明显不同。

### 4. photoo 提前准备有限范围内的媒体

photoo 不会把所有原图一次性解码，而是对当前卡片附近的项目进行分析和预取。视频缩略图会在后台用 Coil 生成并缓存：[MediaAnalysisManager.kt:350](.photoo-check/android/app/src/main/com/voicelike/app/MediaAnalysisManager.kt:350)

它还使用批处理和有限并行度运行照片分析：[MediaAnalysisManager.kt:250](.photoo-check/android/app/src/main/com/voicelike/app/MediaAnalysisManager.kt:250)。这样分析任务不会和手势帧争用无限的线程和内存。

## 二、为什么 photoo 滑动时更稳定

### 1. 手势和动画状态是连续的

photoo 的顶层卡片保存 `offsetX`、`offsetY`、透明度和缩放等动画状态。拖动过程中使用 `snapTo` 跟手更新，松手后按距离和速度选择飞出或回弹：[NativeVoiceLikeActivity.kt:4699](.photoo-check/android/app/src/main/com/voicelike/app/NativeVoiceLikeActivity.kt:4699)

它还使用：

- `VelocityTracker` 判断快速滑动
- 固定阈值避免误触
- `tween` 完成快速飞出
- `spring` 完成自然回弹
- `graphicsLayer`、缩放、透明度和阴影完成视觉反馈
- `key(item.id)` 保持卡片身份和状态稳定

当前项目的整理流程是网格多选，点击图片后进入 `HorizontalPager`：[MainActivity.kt:1027](app/src/main/java/com/localalbum/camera/MainActivity.kt:1027)。这个流程本身没有错误，但它的反馈主要发生在按钮点击和列表更新之后，不是连续的卡片拖动反馈。因此它不能直接产生 photoo 那种“手指移动多少，卡片就跟着移动多少”的感觉。

### 2. photoo 把重任务移出手势路径

卡片拖动时，photoo 不会同步执行原图解码、EXIF 读取、反向地理编码或整批 ML Kit 分析。当前卡片和下一张卡片的分析通过后台任务完成，结果缓存后再更新界面。

当前项目的图片解码虽然放在 I/O 线程，但图片对象仍由每个 `PhotoThumb` 和 `PhotoPageImage` 独立管理。手势和页面状态也集中在较大的 `AlbumCameraApp` 中：[MainActivity.kt:184](app/src/main/java/com/localalbum/camera/MainActivity.kt:184)。这不意味着单文件一定会卡顿，但它让资源生命周期和重组边界更难控制。

## 三、当前项目中最需要优先处理的问题

按对用户体验的影响排序：

### P0：把媒体目录查询移到后台，并缓存结果

目标是让 `MediaStore` 和 `DocumentsContract` 查询只在 `Dispatchers.IO` 中执行一次。页面只消费已经加载好的媒体列表，筛选和分页在内存中完成。

这项改动不新增功能，只是把现有查询放到正确的生命周期和线程中。它同时降低启动卡顿、点击卡顿和查询过程中状态不一致的风险。

### P0：建立统一的图片缓存

优先使用 Coil 的图片请求层，或者实现一个有大小上限的 `LruCache`。缓存 key 应至少区分：

```text
媒体 Uri + 请求类型（缩略图/查看器）+ 目标尺寸
```

需要保留加载占位图，并限制请求尺寸。不要因为使用缓存就把所有照片解码成 2048 像素 Bitmap 常驻内存。

### P1：给查看器增加前后相邻图片预取

保留当前已经存在的 `beyondViewportPageCount = 1`，同时让前后页面使用同一图片缓存。这样相邻页面在真正滑到屏幕上之前就可以开始读取，切页时不会从空白状态开始。

### P1：给视频切换增加首帧占位

在 ExoPlayer 报告 `onRenderedFirstFrame` 之前显示缓存的视频帧；首帧出现后再淡入播放器。当前播放器复用策略可以继续使用，不需要为了体验优化强行增加新的功能。

### P2：把重组和资源状态从根页面中隔离

不需要立刻重写整个项目。可以先把媒体目录、图片请求、播放器和分析结果分别放到独立的状态对象或 `ViewModel` 中，让页面只接收稳定的状态和事件。

重点不是减少文件行数，而是确保以下状态不会因为无关页面变化而反复创建：

- 媒体列表
- 图片请求
- Bitmap 或视频帧
- ExoPlayer
- ML Kit 分析结果

### P3：只有在需要 photoo 交互时才增加卡片动画

如果目标只是让现有网格和查看器更顺滑，不需要复制 photoo 的卡片交互。只有当产品确实需要拖动卡片完成操作时，才单独实现一个自己的 `SwipeCard` 组件。

## 四、稳定性风险判断

目前不能仅凭静态代码断定某一次闪退的唯一原因，但以下路径是高风险点：

1. 主线程执行媒体查询会导致界面长时间无响应。
2. 缩略图和大图重复创建 Bitmap，照片较多时会抬高内存峰值和 GC 压力。
3. 查看器删除当前照片后，列表长度和 `PagerState.currentPage` 会同时变化，所有使用 `photos[pagerState.currentPage]` 的位置都需要保证索引仍然有效。
4. 视频切换时重新准备播放器，如果页面状态快速变化，播放器回调和当前 URI 可能短暂不同步。
5. 缺少统一缓存时，页面反复进入和退出会重复创建 I/O 请求，异常更难复现和定位。

这些问题可以通过后台查询、稳定 key、统一缓存、索引保护和明确的播放器生命周期逐步降低，不需要新增用户功能。

## 五、什么才算“丝滑”

优化完成后，应该以这些结果判断，而不是只看动画数量：

- 进入照片列表时，主线程不执行大批量 `ContentResolver.query`。
- 网格滚动回来时，已见过的缩略图基本直接从缓存显示。
- 查看器切换到相邻照片时不出现长时间纯黑或空白。
- 视频切换时先看到首帧或缩略图，再显示播放器。
- 连续快速删除或返回时，不出现索引越界、播放器已释放或重复请求异常。
- 照片数量增加后，内存占用有上限，不随访问过的图片数量无限增长。

真正的验证应使用 Android Studio Profiler、Frame Timeline 和 Macrobenchmark，观察主线程帧耗时、Bitmap 分配、GC 次数、I/O 请求和播放器准备时间。当前结论来自静态代码对比，尚未在真机上做这些基准测试。

## 六、实现边界

后续优化可以参考 photoo 的公开行为和 Android/Compose/Coil/Media3 的通用做法，但应由当前项目独立实现，不复制 photoo 的源码、类名、注释、资源或布局。photoo 使用的是自定义许可证，明确限制修改、分发和衍生使用；本项目只借鉴工程思路，不直接引入其代码。
