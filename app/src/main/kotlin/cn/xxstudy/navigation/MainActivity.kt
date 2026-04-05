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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
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

    val entryProvider = entryProvider {
        entry<SplashNavKey> {
            SplashScreen(
                onNavigateToMain = { replaceWith(MainNavKey) },
                onNavigateToOnboarding = { replaceWith(LanguageNavKey) }
            )
        }

        entry<LanguageNavKey> {
            LanguageScreen(onNext = { replaceWith(WifiNavKey) })
        }

        entry<WifiNavKey> {
            WifiListScreen(onNext = { replaceWith(PrivacyPolicyNavKey) })
        }

        entry<PrivacyPolicyNavKey> {
            PrivacyPolicyScreen(onNext = { replaceWith(MainNavKey) })
        }

        entry<MainNavKey> {
            HomePage()
        }

    }

    val decorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
        rememberViewModelStoreNavEntryDecorator<NavKey>(),
    )

    val entries = rememberDecoratedNavEntries(rootBackStack, decorators, entryProvider)

    NavDisplay(
        entries = entries,
        modifier = modifier,
        onBack = { rootBackStack.removeLastOrNull() }
    )
}
