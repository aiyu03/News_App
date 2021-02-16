package com.aatech_aplha.news.util

import androidx.appcompat.widget.SearchView


inline fun SearchView.onQueryTextChanged(
    view: SearchView,
    crossinline listener: (String) -> Unit
) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            view.clearFocus()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }

    })
}