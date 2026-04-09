package cn.xxstudy.navigation.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf

/**
 * @data: 2026/3/24 17:21
 * @author: sensiwu
 * @remark: It provides a solution for state based results.
 * UI state
 */
object LocalResultStore {
    private val LocalResultStore: ProvidableCompositionLocal<ResultStore?> =
        compositionLocalOf { null }

    val current: ResultStore
        @Composable
        get() = LocalResultStore.current ?: ResultStore()

    @Composable
    infix fun provides(store: ResultStore): ProvidedValue<ResultStore?> = LocalResultStore provides store
}

class ResultStore {
    val resultStateMap = mutableStateMapOf<String, MutableState<Any?>>()

    /**
     * Retrieves the current result of the given resultKey.
     */
    fun getResultState(resultKey: String): Any? = resultStateMap[resultKey]?.value

    /**
     * Sets the result for the given resultKey.
     */
    fun <T> setResult(resultKey: String, result: T) {
        resultStateMap[resultKey] = mutableStateOf(result)
    }

    /**
     * Removes all results associated with the given key from the store.
     * Let this result no longer continue
     */
    fun removeResult(resultKey: String) {
        resultStateMap.remove(resultKey)
    }
}
