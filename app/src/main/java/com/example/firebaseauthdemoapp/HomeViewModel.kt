package com.example.firebaseauthdemoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // Dummy data for illustration; in a real setup, these would be retrieved from a repository or API
    private val _recentItems = MutableStateFlow<List<Item>>(emptyList())
    val recentItems: StateFlow<List<Item>> get() = _recentItems

    var foundItemsCount = 0  // Replace with live data or state if it changes dynamically
    var returnedItemsCount = 0  // Same here

    init {
        // Load initial data for recent items, found, and returned counts
        viewModelScope.launch {
            // Fetch recent items and set them
            _recentItems.value = fetchRecentItems()

            // Assume some logic or data fetching for counts
            foundItemsCount = fetchFoundItemsCount()
            returnedItemsCount = fetchReturnedItemsCount()
        }
    }

    private suspend fun fetchRecentItems(): List<Item> {
        // Fetch or generate recent items
        return listOf(
            Item("Lost Wallet", Category("Accessories"), "2024-11-01", 0.75),
            Item("Lost Keys", Category("Household"), "2024-10-29", 0.65)
        )
    }

    private fun fetchFoundItemsCount(): Int {
        return 42  // Example count; replace with actual logic
    }

    private fun fetchReturnedItemsCount(): Int {
        return 17  // Example count; replace with actual logic
    }
}

data class Item(
    val name: String,
    val category: Category,
    val dateLost: String,
    val matchPercentage: Double? = null
)

data class Category(val name: String)
