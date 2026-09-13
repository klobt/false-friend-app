package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.agh.falsefriendapp.ui.state.ReviewItem

class SessionReviewViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<ReviewItem>>(emptyList())
    val items = _items.asStateFlow()

    fun setItems(items: List<ReviewItem>) {
        _items.value = items
    }

    fun clear() {
        _items.value = emptyList()
    }
}
