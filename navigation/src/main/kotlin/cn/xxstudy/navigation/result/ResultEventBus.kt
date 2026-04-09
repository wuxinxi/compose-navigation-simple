package cn.xxstudy.navigation.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * @data: 2026/3/24 17:02
 * @author: sensiwu
 * @remark: One-time notifications
 * Toast\Dialog\Snackbar\Navigation
 */

object LocalResultEventBus {
    private val LocalResultEventBus: ProvidableCompositionLocal<ResultEventBus?> =
        compositionLocalOf { null }

    val current: ResultEventBus
        @Composable
        get() = LocalResultEventBus.current ?: ResultEventBus()

    /**
     * Provides a [ResultEventBus] to the composition
     */
    infix fun provides(bus: ResultEventBus): ProvidedValue<ResultEventBus?> = LocalResultEventBus.provides(bus)
}

/**
 * An EventBus for passing results between multiple sets of screens.
 * It provides a solution for event based results (One-time notifications).
 * Toast\Dialog\Snackbar\Navigation
 */
class ResultEventBus {
    val channelMap = mutableStateMapOf<String, Channel<Any?>>()

    /**
     * Provides a flow for the given resultKey.
     * @param resultKey The key default class name.
     */
    inline fun <reified T> getResultFlow(resultKey: String = T::class.java.toString()) = channelMap[resultKey]?.receiveAsFlow()

    fun <T> sendResult(resultKey: String, data: T) {
        if (!channelMap.contains(resultKey)) {
            channelMap[resultKey] =
                Channel(capacity = Channel.BUFFERED, onBufferOverflow = BufferOverflow.SUSPEND)
        }
        channelMap[resultKey]?.trySend(data)
    }

    /**
     * Removes all results associated with the given key from the store.
     * Usually there is no need to call it, because it will be finished after consumption.
     */
    inline fun <reified T> removeResult(resultKey: String = T::class.java.toString()) {
        channelMap.remove(resultKey)
    }
}

/**
 * Provides a [ResultEventBus] that will be remembered across configuration changes.
 */
@Composable
fun rememberResultEventBus(): ResultEventBus = remember { ResultEventBus() }
