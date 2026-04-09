package cn.xxstudy.navigation.core.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * @data: 2026/3/24 17:01
 * @author: sensiwu
 * @remark:
 */
@Composable
inline fun <reified T> ResultEffect(
    resultEventBus: ResultEventBus = LocalResultEventBus.current,
    resultKey: String = T::class.java.toString(),
    crossinline onResult: suspend (T) -> Unit,
) {
    LaunchedEffect(resultKey, resultEventBus.channelMap[resultKey]) {
        resultEventBus.getResultFlow<T>(resultKey)?.collect { result ->
            onResult.invoke(result as T)
        }
    }
}
