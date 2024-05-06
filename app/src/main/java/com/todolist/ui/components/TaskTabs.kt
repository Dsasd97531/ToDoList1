package com.todolist.ui.components
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TaskTabs(
    selectedTab: TaskTag,
    onTabSelected: (TaskTag) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = TaskTag.values()
    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        modifier = modifier
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                text = { Text(tab.displayName) }
            )
        }
    }
}