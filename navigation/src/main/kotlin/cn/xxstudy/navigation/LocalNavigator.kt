package cn.xxstudy.navigation

import androidx.navigation3.runtime.NavKey

/**
 * @data: 2026/4/8 16:43
 * @author: sensiwu
 * @remark: reference nowinandroid
 */
class Navigator(val state: NavigationState) {

    fun navigate(key: NavKey, replace: Boolean = false) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> {
                if (replace) {
                    val stack = state.currentSubStack
                    // Do not replace when only Tab is in the stack
                    // Do not replace PaneNavKey to avoid breaking List-Detail
                    if (stack.size > 1 && stack.last() !is PaneNavKey) {
                        stack.removeAt(stack.size - 1)
                    }
                }
                goToSubNavKey(key)
            }
        }
    }

    fun goBack() {
        when (state.currentKey) {
            state.startNavKey -> { //Do nothing.
            }

            state.currentTopLevelKey -> state.topLevelStack.removeLastOrNull()
            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    fun <T> popUntil(routeKey: T? = null, predicate: (NavKey) -> Boolean) {
        state.currentSubStack.apply {
            val targetIndex = indexOfLast(predicate)
            if (targetIndex != -1) {
                val splitIndex = indexOfLast { it is PaneNavKey }
                val keepCount = if (splitIndex != -1 && splitIndex >= targetIndex) {
                    splitIndex + 2
                } else {
                    targetIndex + 1
                }

                if (size > keepCount) {
                    subList(keepCount, size).clear()
                }
            }
        }
    }

    private fun goToSubNavKey(key: NavKey) {
        state.currentSubStack.apply {
            if (contains(key)) {
                if (key is PaneNavKey) {
                    // If PaneNavKey already exists in the stack, do not simply remove it and add it to the top.
                    // Otherwise, it will be detached from the middle of the stack, causing the Detail page after it to lose its parent node and break the List-Detail!
                    // In this case, use the popUntil logic to navigate back to it.
                    popUntil<Unit>(predicate = { it == key })
                } else {
                    // For normal pages: follow the original logic. If it exists in the stack, remove it first, then add it to the top (bring-to-front).                    remove(key)
                    add(key)
                }
            } else {
                add(key)
            }
        }
    }

    /**
     * Switch tab
     * 1. If the target tab is the current startNavKey (Home), clear the TopLevel stack (back press will exit the app directly)
     *    Otherwise, remove it from the stack first
     * 2. Add the target tab to the TopLevel stack
     */
    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            if (key == state.startNavKey) {
                clear()
            } else {
                remove(key)
            }
            add(key)
        }

        if (key is PaneNavKey) {
            val subStack = state.subStacks[key]
            if (subStack != null && subStack.size == 1) {
                subStack.add(key.defaultDetailKey)
            }
        }
    }

    /**
     * Clear the current sub-stack (keep the top element, i.e., the Tab)
     * For PaneNavKey (List-Detail), keep the first two keys (Tab + default/current category) to avoid flickering
     */
    private fun clearSubStack() {
        state.currentSubStack.apply {
            val paneIndex = indexOfLast { it is PaneNavKey }
            val keepCount = if (paneIndex != -1) paneIndex + 2 else 1
            if (size > keepCount) {
                subList(keepCount, size).clear()
            }
        }
    }
}