package cn.xxstudy.navigation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @data: 2026/4/5
 * @author: sensiwu
 * @remark: Favorites ViewModel - 管理收藏页面状态
 */

// 收藏列表的数据模型
data class FavoriteRecipe(
    val id: Long,
    val title: String,
    val author: String,
    val isVerified: Boolean,
    val imageUrl: String,
    val cookingTime: String,
    val difficulty: String,
    val rating: Float,
    val reviewCount: Int
)

// UI 状态
data class FavoritesUiState(
    val isLoading: Boolean = true,
    val recipes: List<FavoriteRecipe> = emptyList(),
    val error: String? = null
)

// 排序类型
enum class SortType {
    NEWEST, RATING, TIME
}

class FavoritesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState(isLoading = true))
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.NEWEST)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    init {
        loadFavorites()
    }

    /**
     * 加载收藏数据（Mock 数据）
     */
    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // 模拟网络请求延迟
                delay(500)

                val mockRecipes = listOf(
                    FavoriteRecipe(
                        id = 1,
                        title = "Minced beef with tomatoes and penne",
                        author = "Monsieur Cuisine Official",
                        isVerified = true,
                        imageUrl = "https://picsum.photos/400/300",
                        cookingTime = "1 hr 5 min",
                        difficulty = "Easy",
                        rating = 4.5f,
                        reviewCount = 125
                    ),
                    FavoriteRecipe(
                        id = 2,
                        title = "Gluten-free lentil bread",
                        author = "conniethechef",
                        isVerified = false,
                        imageUrl = "https://picsum.photos/400/300",
                        cookingTime = "25 min",
                        difficulty = "Easy",
                        rating = 4.0f,
                        reviewCount = 125
                    ),
                    FavoriteRecipe(
                        id = 3,
                        title = "Duck breast sous-vide with potato dumplings and red cabbage",
                        author = "Monsieur Cuisine Official",
                        isVerified = true,
                        imageUrl = "https://picsum.photos/400/300",
                        cookingTime = "1 hr 5 min",
                        difficulty = "Easy",
                        rating = 4.8f,
                        reviewCount = 125
                    ),
                    FavoriteRecipe(
                        id = 4,
                        title = "Spaghetti alla carbonara",
                        author = "carolrosehome",
                        isVerified = false,
                        imageUrl = "https://picsum.photos/400/300",
                        cookingTime = "25 min",
                        difficulty = "Easy",
                        rating = 4.6f,
                        reviewCount = 125
                    )
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipes = applySorting(mockRecipes, _sortType.value)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load favorites"
                    )
                }
            }
        }
    }

    /**
     * 更改排序方式
     */
    fun changeSortType(sortType: SortType) {
        _sortType.update { sortType }
        applyCurrentSorting()
    }

    /**
     * 移除收藏
     */
    fun removeFromFavorites(recipeId: Long) {
        _uiState.update { currentState ->
            currentState.copy(
                recipes = currentState.recipes.filter { it.id != recipeId }
            )
        }
    }

    /**
     * 应用当前排序
     */
    private fun applyCurrentSorting() {
        _uiState.update { currentState ->
            currentState.copy(
                recipes = applySorting(currentState.recipes, _sortType.value)
            )
        }
    }

    /**
     * 对食谱列表进行排序
     */
    private fun applySorting(
        recipes: List<FavoriteRecipe>,
        sortType: SortType
    ): List<FavoriteRecipe> {
        return when (sortType) {
            SortType.NEWEST -> recipes // Mock 数据默认就是最新顺序
            SortType.RATING -> recipes.sortedByDescending { it.rating }
            SortType.TIME -> recipes.sortedBy { it.cookingTime }
        }
    }
}
