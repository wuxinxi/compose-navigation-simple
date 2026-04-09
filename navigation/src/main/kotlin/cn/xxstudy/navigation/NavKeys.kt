package cn.xxstudy.navigation

import androidx.navigation3.runtime.NavKey

/**
 * @data: 2026/4/8 16:45
 * @author: sensiwu
 * @remark:
 */
/**
 * Marker interface for **root-level navigation destinations**.
 *
 * Use for:
 * - Splash screen
 * - Onboarding / PrivacyPolicy screen
 * - Main tab pages (Home, Setting, etc.)
 * - App-level full-screen pages
 */
sealed interface RootNavKey : NavKey

/**
 * Marker interface for **child / nested navigation destinations**.
 *
 * Use for:
 * - DetailScreen etc.
 * - Pages navigated from a root page
 * - Normal sub-screens in the navigation flow
 */
sealed interface SubNavKey : NavKey

/**
 * Marker interface for **master-detail (split-pane) navigation destinations**.
 * Typically used for tablet / foldable / large screen layouts.
 *
 * Use for:
 * - List + Detail screen layouts
 * - Left menu + right content patterns
 * - Settings screen
 */
sealed interface PaneNavKey : NavKey {
    val defaultDetailKey: NavKey
}