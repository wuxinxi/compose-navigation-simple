package cn.xxstudy.navigation.core

import androidx.navigation3.runtime.NavKey
import cn.xxstudy.navigation.routes.SplitNavKey

/**
 * @data: 2026/3/26 15:39
 * @author: sensiwu
 * @remark:
 */
class Navigator(val state: NavigationState) {

    fun navigate(key: NavKey, replace: Boolean = false) {
        when (key) {
            // 点击当前正在显示的 tab，清空子栈
            state.currentTopLevelKey -> clearSubStack()
            // 点击其他 tab,切换顶层 tab
            in state.topLevelKeys -> goToTopLevel(key)
            // 子栈内跳转
            else -> {
                if (replace) {
                    val stack = state.currentSubStack
                    // "在 Tab 上面的就不允许 replace"：如果栈里只有 Tab (size == 1)，不执行 replace
                    // "SplitNavKey 也不允许直接 replace"：防止破坏双窗 ListDetail 结构
                    if (stack.size > 1 && stack.last() !is SplitNavKey) {
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
                val splitIndex = indexOfLast { it is SplitNavKey }
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
                if (key is SplitNavKey) {
                    // 如果栈里已经有 SplitNavKey，不能简单地 remove 后加到栈顶。
                    // 否则会把它从栈中间抽离，导致原本跟在它后面的 Detail 失去父节点，破坏双窗布局！
                    // 所以此时应该使用 popUntil 逻辑回退到它
                    popUntil<Unit>(predicate = { it == key })
                } else {
                    // 普通页面按原逻辑：如果在栈里面先删除，再添加到栈顶（bring-to-front）
                    remove(key)
                    add(key)
                }
            } else {
                add(key)
            }
        }
    }

    /**
     * 切换tab
     * 1. 如果切换的tab是当前startNevKey(Home) 清空 TopLevel 栈（back 直接退出 app）
     * 否则先先把它从栈里移除
     * 2. 添加到 TopLevel 栈
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

        if (key is SplitNavKey) {
            val subStack = state.subStacks[key]
            if (subStack != null && subStack.size == 1) {
                // 只有 list key，没有 detail，添加默认 detail
                subStack.add(key.defaultDetailKey)
            }
        }
    }

    /**
     * 清空当前子栈（留下栈顶即Tab）
     * SplitNavKey (Master-Detail)，保留前两个 Key（Tab + 默认或当前分类），避免闪烁
     */
    private fun clearSubStack() {
        state.currentSubStack.apply {
            val splitIndex = indexOfLast { it is SplitNavKey }
            val keepCount = if (splitIndex != -1) splitIndex + 2 else 1
            if (size > keepCount) {
                subList(keepCount, size).clear()
            }
        }
    }
}