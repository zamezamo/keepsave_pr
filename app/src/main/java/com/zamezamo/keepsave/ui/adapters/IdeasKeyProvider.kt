package com.zamezamo.keepsave.ui.adapters

import androidx.recyclerview.selection.ItemKeyProvider

class IdeasKeyProvider(private val adapter: IdeasAdapter) : ItemKeyProvider<String>(SCOPE_CACHED){

    override fun getKey(position: Int): String? =
        adapter.currentList[position].id

    override fun getPosition(key: String): Int =
        adapter.currentList.indexOfFirst { it.id == key }

}