package com.zibi.mod.common.ui.logview

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.zibi.mod.common.ui.theme.AppTheme


@Composable
fun LogView(
    modifier: Modifier,
    logs: List<String>,
) {

    val scrollState = rememberScrollState()
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        listState.animateScrollToItem(index = if (logs.isNotEmpty()) logs.size - 1 else 0)
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .background(color = AppTheme.colors.black)
            .horizontalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
    ) {
        items(logs) { log ->
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = log,
//                fontStyle = AppTheme.typography.small2Regular.fontStyle,
                fontSize = 12.sp,
                color = AppTheme.colors.lightGreen,
                maxLines = 1,
            )
        }
    }
}