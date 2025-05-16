package com.listofdrinks

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScrollStateViewModel: ViewModel() {
        val listState = LazyListState()
        val gridState = LazyGridState()

        fun syncScroll() {
                viewModelScope.launch {
                        delay(50)
                        listState.scrollToItem(gridState.firstVisibleItemIndex, 0)
                        gridState.scrollToItem(listState.firstVisibleItemIndex, 0)
                }
        }


}