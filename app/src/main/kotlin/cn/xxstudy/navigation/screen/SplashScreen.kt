package cn.xxstudy.navigation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cn.xxstudy.navigation.core.AppPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(onNavigateToMain: () -> Unit, onNavigateToOnboarding: () -> Unit) {
    val context = LocalContext.current
    val appPreferences = remember { AppPreferences(context) }

    LaunchedEffect(Unit) {
        delay(3000)
        val isOnboardingCompleted = appPreferences.isOnboardingCompleted.first()
        if (isOnboardingCompleted) {
            onNavigateToMain()
        } else {
            onNavigateToOnboarding()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "TangRen Splash",
            style = MaterialTheme.typography.displayLarge
        )
    }
}
