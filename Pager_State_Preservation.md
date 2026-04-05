# PrimaryTabRow + HorizontalPager 状态保持最佳实践

## 问题场景

在使用 `TabRow` + `HorizontalPager` 时，当用户在一个 Tab 页面滚动到底部，切换到另一个 Tab 后再切回来，滚动位置会丢失，页面重置到顶部。

## 核心原理

### 为什么状态会丢失？

1. **HorizontalPager 默认行为**：只保留当前可见页面，不可见页面会被销毁
2. **状态作用域问题**：如果在 `HorizontalPager` 外部创建 `LazyGridState`，所有页面共享同一个状态对象
3. **页面复用机制**：Pager 会复用页面，导致状态被覆盖

## 解决方案

### 方案一：key 隔离 + 内部状态（推荐）

```kotlin
@Composable
private fun FavoritesScreenContent(
    uiState: FavoritesUiState,
    onRemoveFavorite: (Long) -> Unit
) {
    val tabs = listOf("Tab 1", "Tab 2")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // 同步 Pager 和 Tab 状态
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // TabRow
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(selectedTabIndex)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        // HorizontalPager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1  // 保持相邻页面在内存中
        ) { page ->
            // 🔑 关键：使用 key 隔离每个页面的状态
            key(page) {
                FavoritesTabPage(
                    uiState = uiState,
                    onRemoveFavorite = onRemoveFavorite
                )
            }
        }
    }
}

/**
 * 单个 Tab 页面，内部维护独立的滚动状态
 */
@Composable
private fun FavoritesTabPage(
    uiState: FavoritesUiState,
    onRemoveFavorite: (Long) -> Unit
) {
    // 🔑 关键：在每个页面内部创建独立的滚动状态
    val gridState = rememberLazyGridState()

    when {
        uiState.isLoading -> LoadingView()
        uiState.error != null -> ErrorView(uiState.error!!)
        uiState.recipes.isEmpty() -> EmptyView()
        else -> {
            LazyVerticalGrid(
                state = gridState,  // 使用当前页面的状态
                columns = GridCells.Fixed(2)
            ) {
                items(uiState.recipes, key = { it.id }) { item ->
                    ItemCard(item, onRemoveFavorite)
                }
            }
        }
    }
}
```

### 关键点解析

| 要点 | 作用 | 说明 |
|------|------|------|
| `key(page)` | 状态隔离 | 告诉 Compose 每个页面是独立实例，不会共享状态 |
| `rememberLazyGridState()` 在内部 | 独立状态 | 每个页面创建自己的状态对象，不会被其他页面影响 |
| `beyondViewportPageCount = 1` | 保留页面 | 保持相邻页面在内存中，减少重建频率 |

## 为什么这样有效？

### 1. Compose 的状态作用域

```kotlin
// ❌ 错误：所有页面共享一个状态
val gridState = rememberLazyGridState()  // 在 Pager 外部创建
HorizontalPager(...) { page ->
    LazyVerticalGrid(state = gridState)  // 所有页面用同一个
}

// ✅ 正确：每个页面有自己的状态
HorizontalPager(...) { page ->
    key(page) {
        FavoritesTabPage()  // 内部创建独立状态
    }
}

@Composable
private fun FavoritesTabPage() {
    val gridState = rememberLazyGridState()  // 每个页面独立
    LazyVerticalGrid(state = gridState)
}
```

### 2. key 的工作原理

```kotlin
key(page) {
    // 当 page 值改变时，Compose 会：
    // 1. 丢弃旧组合树
    // 2. 创建新的组合树
    // 3. 新的 rememberLazyGridState() 会被调用
    // 4. 状态完全独立，不会互相影响
}
```

## 完整示例：TabRow vs PrimaryTabRow

### PrimaryTabRow（Material 3 推荐）

```kotlin
PrimaryTabRow(
    selectedTabIndex = selectedTabIndex,
    indicator = {
        TabRowDefaults.PrimaryIndicator(
            modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
            color = Color.Blue,
            height = 3.dp
        )
    }
) {
    tabs.forEachIndexed { index, title ->
        Tab(
            selected = selectedTabIndex == index,
            onClick = { /* 切换逻辑 */ },
            text = { Text(title) }
        )
    }
}
```

### TabRow（自定义指示器）

```kotlin
TabRow(
    selectedTabIndex = selectedTabIndex,
    indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
        )
    }
) {
    // ... 同上
}
```

## 状态同步：Pager ↔ Tab

```kotlin
var selectedTabIndex by remember { mutableIntStateOf(0) }
val pagerState = rememberPagerState(pageCount = { tabs.size })

// Pager 滑动 → 更新 Tab
LaunchedEffect(pagerState.currentPage) {
    selectedTabIndex = pagerState.currentPage
}

// Tab 点击 → 滑动 Pager
Tab(
    onClick = {
        selectedTabIndex = index
        coroutineScope.launch {
            pagerState.animateScrollToPage(index)
        }
    }
)
```

## 注意事项

### 1. beyondViewportPageCount 的使用

```kotlin
HorizontalPager(
    beyondViewportPageCount = 1  // 保留相邻的 1 个页面
)
```

- 对于 2 个 Tab：设置 `1` 即可
- 对于更多 Tab：根据内存情况调整
- 过大可能导致内存问题

### 2. 避免的状态共享陷阱

```kotlin
// ❌ 错误做法
val states = remember { List(2) { LazyGridState() } }
HorizontalPager(...) { page ->
    LazyVerticalGrid(state = states[page])  // 外部状态可能被复用
}

// ✅ 正确做法
HorizontalPager(...) { page ->
    key(page) {
        TabPage()  // 内部 rememberLazyGridState()
    }
}
```

### 3. ViewModel 状态 vs UI 状态

```kotlin
// ViewModel 保存业务数据（列表、加载状态等）
class FavoritesViewModel : ViewModel() {
    val uiState: StateFlow<FavoritesUiState> = ...
}

// Composable 保存 UI 状态（滚动位置、动画等）
@Composable
fun TabPage() {
    val gridState = rememberLazyGridState()  // UI 状态
    val viewModel: FavoritesViewModel = viewModel()  // 业务数据
}
```

## 总结

| 方案 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| `key` + 内部状态 | 完全隔离，状态不互相影响 | 页面销毁时状态丢失 | 大多数场景（推荐） |
| 外部状态列表 | 状态持久化 | 复杂，容易出错 | 需要跨页面共享状态 |
| `beyondViewportPageCount` | 减少重建 | 内存占用增加 | 配合其他方案使用 |

**最佳实践**：`key(page)` + `rememberLazyGridState()` 在页面内部 + `beyondViewportPageCount = 1`
