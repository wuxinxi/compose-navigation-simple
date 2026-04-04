package cn.xxstudy.navigation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import cn.xxstudy.navigation.core.Navigator
import cn.xxstudy.navigation.core.rememberNavigationState
import cn.xxstudy.navigation.core.toEntries
import cn.xxstudy.navigation.routes.HomeDetailNavKey
import cn.xxstudy.navigation.routes.HomeNavKey
import cn.xxstudy.navigation.routes.ModelNavKey
import cn.xxstudy.navigation.routes.SearchNavKey
import cn.xxstudy.navigation.routes.SettingNaveKey

@Composable
fun HomePage() {
    val navState = rememberNavigationState(
        startNavKey = HomeNavKey,
        topLevelKeys = listOf(HomeNavKey, SearchNavKey, ModelNavKey, SettingNaveKey)
    )
    val navigator = remember(navState) { Navigator(navState) }

    val entries = navState.toEntries { key ->
        when (key) {
            is HomeNavKey -> NavEntry(key) { HomeScreen(navigator) }
            is HomeDetailNavKey -> NavEntry(key) { HomeDetailScreen(navigator) }
            is SearchNavKey -> NavEntry(key) { SearchScreen() }
            is ModelNavKey -> NavEntry(key) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Model Content") }
            }
            is SettingNaveKey -> NavEntry(key) {
                SettingScreen(navigator)
            }
            else -> NavEntry(key) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Unknown Page") }
            }
        }
    }

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
                selected = navState.currentTopLevelKey == ModelNavKey,
                onClick = { navigator.navigate(ModelNavKey) },
                icon = { Text("M") },
                label = { Text("Model") }
            )
            NavigationRailItem(
                selected = navState.currentTopLevelKey == SettingNaveKey,
                onClick = { navigator.navigate(SettingNaveKey) },
                icon = { Text("S") },
                label = { Text("Settings") }
            )
        }
        
        NavDisplay(
            entries = entries,
            modifier = Modifier.weight(1f),
            onBack = { navigator.goBack() }
        )
    }
}
