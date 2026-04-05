package cn.xxstudy.navigation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * @data: 2026/4/3 16:26
 * @author: sensiwu
 * @remark:
 */

/**
 * 根路由
 */
sealed interface RootNavKey : NavKey

/**
 * 子路由
 */
sealed interface SubNavKey : NavKey

/**
 * 左右拆分的路由(Master-Detail)
 */
sealed interface SplitNavKey : NavKey {
    val defaultDetailKey: NavKey
}

// Onboarding & Splash & MainApp
@Serializable
object SplashNavKey : RootNavKey

@Serializable
object PrivacyPolicyNavKey : RootNavKey

@Serializable
object MainNavKey : RootNavKey

// default root
@Serializable
object HomeNavKey : RootNavKey

@Serializable
object SearchNavKey : RootNavKey

@Serializable
object ModelNavKey : RootNavKey

@Serializable
object SettingNaveKey : RootNavKey


// dynamic root
@Serializable
object DynamicManualModelNavKey : RootNavKey

@Serializable
object DynamicAutoModelNavKey : RootNavKey

@Serializable
object DynamicScaleNavKey : RootNavKey


// sub nav key
@Serializable
object ManualModelNavKey : SubNavKey

@Serializable
data class RecipeDetailNavKey(val id: Long) : SubNavKey

@Serializable
object SettingListNavKey : SplitNavKey {
    override val defaultDetailKey: NavKey = WifiNavKey
}


@Serializable
object HomeDetailNavKey : SubNavKey

@Serializable
object WifiNavKey : SubNavKey

@Serializable
object LanguageNavKey : SubNavKey

@Serializable
object AboutNavKey : SubNavKey

@Serializable
object FeedbackNavKey : SubNavKey
