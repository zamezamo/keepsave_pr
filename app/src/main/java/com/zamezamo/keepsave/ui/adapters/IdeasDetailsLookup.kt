package com.zamezamo.keepsave.ui.adapters

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class IdeasDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {

    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {

        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as IdeasAdapter.IdeasViewHolder).getItem()
        }

        return null

    }

}