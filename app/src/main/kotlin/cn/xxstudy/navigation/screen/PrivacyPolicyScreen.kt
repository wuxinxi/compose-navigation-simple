package cn.xxstudy.navigation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cn.xxstudy.navigation.core.AppPreferences
import kotlinx.coroutines.launch

@Composable
fun PrivacyPolicyScreen(onNext: () -> Unit) {
    val context = LocalContext.current
    val appPreferences = remember { AppPreferences(context) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Privacy Policy",
            style = MaterialTheme.typography.displayMedium
        )
        Button(onClick = {
            scope.launch {
                appPreferences.setOnboardingCompleted(true)
                onNext()
            }
        }) {
            Text("Accept & Start")
        }
    }
}
