package cn.xxstudy.navigation.core

import android.util.Log
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import cn.xxstudy.navigation.core.animation.NavigationAnimation
import cn.xxstudy.navigation.core.result.LocalResultEventBus
import cn.xxstudy.navigation.core.result.LocalResultStore
import cn.xxstudy.navigation.core.result.ResultEventBus
import cn.xxstudy.navigation.core.result.ResultStore
import cn.xxstudy.navigation.screen.LocalNavigator

/**
 * @data: 2026/4/9 11:10
 * @author: sensiwu
 * @remark:
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun <T : NavKey> AppNavDisplay(
    navigator: Navigator,
    entries: List<NavEntry<T>>,
    transitionStyle: NavigationAnimation = NavigationAnimation.None,
    resultStore: ResultStore? = null,
    resultEventBus: ResultEventBus? = null,
) {

    val providers = mutableListOf<ProvidedValue<*>>(LocalNavigator provides navigator)
    resultStore?.let { providers.add(LocalResultStore provides it) }
    resultEventBus?.let { providers.add(LocalResultEventBus provides it) }

    val paneExpansionState = rememberPaneExpansionState()
    val listDetailStrategy =
        rememberListDetailSceneStrategy<T>(paneExpansionState = paneExpansionState)

    LaunchedEffect(Unit) {
        paneExpansionState.setFirstPaneProportion(0.3f)
    }

    CompositionLocalProvider(*providers.toTypedArray()) {
        NavDisplay(
            entries = entries,
            onBack = { navigator.goBack() },
            sceneStrategies = listOf(listDetailStrategy),
            transitionSpec = { transitionStyle.getTransition() },
            popTransitionSpec = { transitionStyle.getPopTransition() },
            predictivePopTransitionSpec = { transitionStyle.getPopTransition() },
        )
    }
}
