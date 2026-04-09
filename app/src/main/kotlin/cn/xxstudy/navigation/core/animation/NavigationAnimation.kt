package cn.xxstudy.navigation.core.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

/**
 * @data: 2026/3/18 10:18
 * @author: Sensi
 * @remark:
 */
interface NavigationAnimation {

    // Combined enter/exit animations for forward stack push
    fun getTransition(): ContentTransform

    // Reverse enter/exit animations for backward stack pop
    fun getPopTransition(): ContentTransform

    data object None : NavigationAnimation {
        override fun getTransition(): ContentTransform = EnterTransition.None togetherWith ExitTransition.None

        override fun getPopTransition(): ContentTransform = EnterTransition.None togetherWith ExitTransition.None
    }

    /**
     * Classic horizontal slide transition (Right-to-Left on push, Left-to-Right on pop).
     */
    data object HorizontalSlide : NavigationAnimation {
        override fun getTransition(): ContentTransform = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(400),
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { -it / 3 },
            animationSpec = tween(400),
        )

        override fun getPopTransition(): ContentTransform = slideInHorizontally(
            initialOffsetX = { -it / 3 },
            animationSpec = tween(400),
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(400),
        )
    }

    /**
     * Vertical popup transition (Bottom-to-Top on push, Top-to-Bottom on pop).
     * Great for comment sections, modals, or detail overrides.
     */
    data object VerticalBottomUp : NavigationAnimation {
        override fun getTransition(): ContentTransform = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(350),
        ) togetherWith slideOutVertically(
            targetOffsetY = { -it / 5 },
            animationSpec = tween(350),
        )

        override fun getPopTransition(): ContentTransform = slideInVertically(
            initialOffsetY = { -it / 5 },
            animationSpec = tween(350),
        ) togetherWith slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(350),
        )
    }

    /**
     * Fast Crossfade.
     */
    data object Fade : NavigationAnimation {
        override fun getTransition(): ContentTransform = fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))

        override fun getPopTransition(): ContentTransform = fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
    }
}
