package cn.xxstudy.navigation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.xxstudy.navigation.viewmodel.FavoriteRecipe
import cn.xxstudy.navigation.viewmodel.FavoritesUiState
import cn.xxstudy.navigation.viewmodel.FavoritesViewModel
import cn.xxstudy.navigation.viewmodel.SortType
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sortType by viewModel.sortType.collectAsState()

    FavoritesScreenContent(
        uiState = uiState,
        sortType = sortType,
        onSortTypeChanged = { viewModel.changeSortType(it) },
        onRemoveFavorite = { viewModel.removeFromFavorites(it) }
    )
}

@Composable
private fun FavoritesScreenContent(
    uiState: FavoritesUiState,
    sortType: SortType,
    onSortTypeChanged: (SortType) -> Unit,
    onRemoveFavorite: (Long) -> Unit
) {
    val tabs = listOf("All Favourited Recipes", "My Favourited List")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // 同步 Pager 滚动和 Tab 选中状态
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 顶部标题栏 + TabRow
        FavoritesHeader(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            sortType = sortType,
            onSortTypeChanged = onSortTypeChanged,
            onTabSelected = { index ->
                selectedTabIndex = index
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )

        // HorizontalPager 内容 - 每个 Tab 都是独立的 Composable，自带状态保存
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            // 使用 key 确保每个页面不会被复用
            key(page) {
                FavoritesTabPage(
                    uiState = uiState,
                    onRemoveFavorite = onRemoveFavorite
                )
            }
        }
    }
}

@Composable
private fun FavoritesHeader(
    tabs: List<String>,
    selectedTabIndex: Int,
    sortType: SortType,
    onSortTypeChanged: (SortType) -> Unit,
    onTabSelected: (Int) -> Unit
) {
    Column {
        // 标题行（标题 + 排序按钮）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Favourites",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort",
                    tint = Color(0xFF0066CC),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SORT BY",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF0066CC),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        // 循环切换排序方式
                        val nextSortType = when (sortType) {
                            SortType.NEWEST -> SortType.RATING
                            SortType.RATING -> SortType.TIME
                            SortType.TIME -> SortType.NEWEST
                        }
                        onSortTypeChanged(nextSortType)
                    }
                )
            }
        }

        // TabRow
        PrimaryTabRow (
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = Color(0xFF0066CC),
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                    color = Color(0xFF0066CC),
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyFavoritesView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 空状态图标（刀叉和盘子）
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFFF5F5F5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🍴", style = MaterialTheme.typography.displaySmall)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No favourite recipe yet",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Explore Recipes 按钮
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0066CC),
            modifier = Modifier.clickable { /* Navigate to explore */ }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explore Recipes",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "→",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * 单个 Tab 的页面，内部维护自己的滚动状态
 * 使用 key(page) 确保每个页面都有独立的状态保存
 */
@Composable
private fun FavoritesTabPage(
    uiState: FavoritesUiState,
    onRemoveFavorite: (Long) -> Unit
) {
    // 每个页面独立保存滚动状态 - 这是关键！
    val gridState = rememberLazyGridState()

    when {
        // 加载或错误状态时不使用网格，避免滚动状态混乱
        uiState.isLoading || uiState.error != null || uiState.recipes.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> CircularProgressIndicator()
                    uiState.error != null -> Text(
                        text = uiState.error!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    else -> EmptyFavoritesView()
                }
            }
        }
        // 有数据时使用网格
        else -> {
            FavoritesGrid(
                recipes = uiState.recipes,
                onRemoveFavorite = onRemoveFavorite,
                gridState = gridState
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FavoritesGrid(
    recipes: List<FavoriteRecipe>,
    onRemoveFavorite: (Long) -> Unit,
    gridState: LazyGridState
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onRemoveFavorite = { onRemoveFavorite(recipe.id) }
            )
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: FavoriteRecipe,
    onRemoveFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // 图片区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = recipe.title,
                    tint = Color(0xFFCCCCCC),
                    modifier = Modifier.size(48.dp)
                )

                // 收藏按钮
                IconButton(
                    onClick = onRemoveFavorite,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFF0066CC), CircleShape)
                            .padding(4.dp)
                    )
                }

                // 作者信息
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(bottomStart = 12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 作者头像
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                if (recipe.isVerified) Color(0xFF0066CC) else Color.Gray,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = recipe.author.first().toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // 作者名
                    Text(
                        text = recipe.author,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // 认证标志
                    if (recipe.isVerified) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(Color(0xFF0066CC), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }

            // 内容区域
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // 标题
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 时间和难度
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.cookingTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = recipe.difficulty,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 评分和评论
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "★".repeat(recipe.rating.toInt()),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFFC107)
                        )
                        Text(
                            text = "(${recipe.reviewCount} reviews)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    IconButton(
                        onClick = { /* More options */ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
