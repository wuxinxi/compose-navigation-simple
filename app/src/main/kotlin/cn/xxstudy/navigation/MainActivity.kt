package cn.xxstudy.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import cn.xxstudy.navigation.routes.LanguageNavKey
import cn.xxstudy.navigation.routes.MainNavKey
import cn.xxstudy.navigation.routes.PrivacyPolicyNavKey
import cn.xxstudy.navigation.routes.SplashNavKey
import cn.xxstudy.navigation.routes.WifiNavKey
import cn.xxstudy.navigation.screen.HomePage
import cn.xxstudy.navigation.screen.LanguageScreen
import cn.xxstudy.navigation.screen.PrivacyPolicyScreen
import cn.xxstudy.navigation.screen.SplashScreen
import cn.xxstudy.navigation.screen.WifiListScreen
import cn.xxstudy.navigation.ui.theme.NavigationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppRoot(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppRoot(modifier: Modifier = Modifier) {
    // 外层线性引导流：Splash -> (Language -> Wifi -> PrivacyPolicy) -> Main App(HomePage)
    val rootBackStack = rememberNavBackStack(SplashNavKey as NavKey)

    fun replaceWith(key: NavKey) {
        rootBackStack.removeLastOrNull()
        rootBackStack.add(key)
    }

    val entries = rememberDecoratedNavEntries(rootBackStack, entryProvider = { key ->
        when (key) {
            is SplashNavKey -> NavEntry(key) {
                SplashScreen(
                    onNavigateToMain = { replaceWith(MainNavKey) },
                    onNavigateToOnboarding = { replaceWith(LanguageNavKey) }
                )
            }
            is LanguageNavKey -> NavEntry(key) {
                LanguageScreen(onNext = { replaceWith(WifiNavKey) })
            }
            is WifiNavKey -> NavEntry(key) {
                WifiListScreen(onNext = { replaceWith(PrivacyPolicyNavKey) })
            }
            is PrivacyPolicyNavKey -> NavEntry(key) {
                PrivacyPolicyScreen(onNext = { replaceWith(MainNavKey) })
            }
            is MainNavKey -> NavEntry(key) {
                HomePage()
            }
            else -> NavEntry(key) { }
        }
    })

    NavDisplay(
        entries = entries,
        modifier = modifier,
        onBack = { rootBackStack.removeLastOrNull() }
    )
}
