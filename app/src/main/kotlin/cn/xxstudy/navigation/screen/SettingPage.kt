package cn.xxstudy.navigation.screen

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.xxstudy.navigation.core.Navigator
import cn.xxstudy.navigation.routes.AboutNavKey
import cn.xxstudy.navigation.routes.LanguageNavKey
import cn.xxstudy.navigation.routes.SettingListNavKey
import cn.xxstudy.navigation.routes.SettingNaveKey
import cn.xxstudy.navigation.routes.WifiNavKey

/**
 * @data: 2026/4/4 23:19
 * @author: sensiwu
 * @remark:
 */
@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
    navigator: Navigator
) {
    val subStack = navigator.state.currentSubStack
    val activeRoute = remember(subStack) {
        derivedStateOf {
            // 从后往前找，找到第一个属于 Settings 三个选项的 key
            subStack.lastOrNull { it == WifiNavKey || it == LanguageNavKey || it == AboutNavKey }
        }
    }
    val currentKey = activeRoute.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        SettingCategory("WI-FI", currentKey == WifiNavKey) {
            navigator.navigate(WifiNavKey, replace = true)
        }
        SettingCategory("Language", currentKey == LanguageNavKey) {
            navigator.navigate(LanguageNavKey, replace = true)
        }
        SettingCategory("About", currentKey == AboutNavKey) {
            navigator.navigate(AboutNavKey, replace = true)
        }
    }
}


@Composable
fun SettingCategory(title: String, isSelected: Boolean, onClick: () -> Unit) {
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