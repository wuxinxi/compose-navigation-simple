package cn.xxstudy.navigation.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay
import cn.xxstudy.navigation.core.AppNavDisplay
import cn.xxstudy.navigation.core.Navigator
import cn.xxstudy.navigation.core.animation.NavigationAnimation
import cn.xxstudy.navigation.core.rememberNavigationState
import cn.xxstudy.navigation.core.toEntries
import cn.xxstudy.navigation.routes.AboutNavKey
import cn.xxstudy.navigation.routes.FavoritesNavKey
import cn.xxstudy.navigation.routes.HomeDetailNavKey
import cn.xxstudy.navigation.routes.HomeNavKey
import cn.xxstudy.navigation.routes.LanguageNavKey
import cn.xxstudy.navigation.routes.ModelNavKey
import cn.xxstudy.navigation.routes.SearchNavKey
import cn.xxstudy.navigation.routes.SettingListNavKey
import cn.xxstudy.navigation.routes.SettingNaveKey
import cn.xxstudy.navigation.routes.WifiNavKey

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided! Make sure your component is wrapped inside NavDisplay.")
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomePage() {
    val navState = rememberNavigationState(
        startNavKey = HomeNavKey,
        topLevelKeys = listOf(
            HomeNavKey,
            SearchNavKey,
            FavoritesNavKey,
            ModelNavKey,
            SettingListNavKey
        )
    )
    val navigator = remember(navState) { Navigator(navState) }

    val paneExpansionState = rememberPaneExpansionState()
    val listDetailStrategy =
        rememberListDetailSceneStrategy<NavKey>(paneExpansionState = paneExpansionState)

    LaunchedEffect(Unit) {
        paneExpansionState.setFirstPaneProportion(0.2f)
    }


    val entryProvider = entryProvider {
        entry<HomeNavKey> { HomeScreen() }

        entry<HomeDetailNavKey>(metadata = metadata {
            ListDetailSceneStrategy.detailPane()
            put(NavDisplay.TransitionKey) { NavigationAnimation.VerticalBottomUp.getTransition() }
            put(NavDisplay.PopTransitionKey) { NavigationAnimation.VerticalBottomUp.getPopTransition() }
        }) {
            HomeDetailScreen(navigator)
        }
        entry<SearchNavKey> { SearchScreen() }
        entry<FavoritesNavKey> { FavoritesScreen() }
        entry<ModelNavKey> {
            Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Model Content") }
        }
        entry<SettingListNavKey>(
            metadata = ListDetailSceneStrategy.listPane()
        ) {
            SettingPage(navigator = navigator)
        }

        entry<WifiNavKey>(
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            WifiDetailPane(navigator)
        }

        entry<LanguageNavKey>(
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            LanguageDetailPane()
        }

        entry<AboutNavKey>(
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            AboutDetailPane()
        }
    }

    val entries = navState.toEntries(entryProvider)


    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail {
            NavigationRailItem(
                selected = navState.currentTopLevelKey == HomeNavKey,
                onClick = { navigator.navigate(HomeNavKey) },
                icon = { Text("H") },
                label = { Text("Home") }
            )
            NavigationRailItem(
                selected = navState.currentTopLevelKey == SearchNavKey,
                onClick = { navigator.navigate(SearchNavKey) },
                icon = { Text("S") },
                label = { Text("Search") }
            )
            NavigationRailItem(
                selected = navState.currentTopLevelKey == FavoritesNavKey,
                onClick = { navigator.navigate(FavoritesNavKey) },
                icon = { Text("F") },
                label = { Text("Favorites") }
            )
            NavigationRailItem(
                selected = navState.currentTopLevelKey == ModelNavKey,
                onClick = { navigator.navigate(ModelNavKey) },
                icon = { Text("M") },
                label = { Text("Model") }
            )
            NavigationRailItem(
                selected = navState.currentTopLevelKey == SettingListNavKey,
                onClick = { navigator.navigate(SettingListNavKey) },
                icon = { Text("S") },
                label = { Text("Settings") }
            )
        }

        AppNavDisplay(navigator = navigator, entries = entries)
    }
}
