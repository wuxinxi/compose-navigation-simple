package cn.xxstudy.navigation.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.xxstudy.navigation.core.Navigator
import cn.xxstudy.navigation.routes.HomeDetailNavKey
import kotlinx.coroutines.launch

enum class SettingRoute {
    WIFI, LANGUAGE, ABOUT
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SettingScreen(navigator: Navigator) {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<SettingRoute>()
    val coroutineScope = rememberCoroutineScope()

    val paneExpansionState = rememberPaneExpansionState()

    LaunchedEffect(Unit) {
        paneExpansionState.setFirstPaneProportion(0.2f)
        coroutineScope.launch {
            scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail, SettingRoute.WIFI)
        }
    }


    LaunchedEffect(Unit) {
        Log.d("SENSI", "SettingScreen: LaunchedEffect")
    }

    ListDetailPaneScaffold(
        directive = scaffoldNavigator.scaffoldDirective,
        value = scaffoldNavigator.scaffoldValue,
        paneExpansionState = paneExpansionState,
        listPane = {
            AnimatedPane {
                SettingListPane(
                    currentRoute = scaffoldNavigator.currentDestination?.contentKey,
                    onRouteSelected = { route ->
                        coroutineScope.launch {
                            scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail, route)
                        }
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val currentRoute = scaffoldNavigator.currentDestination?.contentKey
                when (currentRoute) {
                    SettingRoute.WIFI -> WifiDetailPane(navigator)
                    SettingRoute.LANGUAGE -> LanguageDetailPane()
                    SettingRoute.ABOUT -> AboutDetailPane()
                    null -> {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text(
                                text = "Please select an item from the left",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SettingListPane(
    currentRoute: SettingRoute?,
    onRouteSelected: (SettingRoute) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        SettingItem("Wi-Fi", currentRoute == SettingRoute.WIFI) {
            onRouteSelected(SettingRoute.WIFI)
        }
        Spacer(modifier = Modifier.height(8.dp))
        SettingItem("Language", currentRoute == SettingRoute.LANGUAGE) {
            onRouteSelected(SettingRoute.LANGUAGE)
        }
        Spacer(modifier = Modifier.height(8.dp))
        SettingItem("About", currentRoute == SettingRoute.ABOUT) {
            onRouteSelected(SettingRoute.ABOUT)
        }
    }
}

@Composable
fun SettingItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val textColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(title, color = textColor, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun WifiDetailPane(navigator: Navigator) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp)
    ) {
        Text("Wi-Fi Settings", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Configure your Wi-Fi networks here.", modifier = Modifier.clickable {
            navigator.navigate(HomeDetailNavKey)
        })
    }
}

@Composable
fun LanguageDetailPane() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp)
    ) {
        Text("Language Settings", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Select your preferred language.")
    }
}

@Composable
fun AboutDetailPane() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp)
    ) {
        Text("About", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("TangRen App v1.0.0")
    }
}
