package com.zamezamo.keepsave.ui.adapters

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.zamezamo.keepsave.domain.Idea
import com.zamezamo.keepsave.ui.views.IdeasFragment
import com.zamezamo.keepsave.utils.DateTimeConverter

class DiffCallbackIdeas : DiffUtil.ItemCallback<Idea>() {

    companion object {

        const val ARG_COLOR_PRIORITY = "ideaColorPriority"
        const val ARG_TITLE = "ideaTitle"
        const val ARG_LOCATION = "ideaLocation"
        const val ARG_DATE_AND_TIME = "ideaDateAndTime"

        const val ARG_IMG_URI = "ideaImgUri"
        const val ARG_DESCRIPTION = "ideaDescription"

    }



    override fun areItemsTheSame(oldItem: Idea, newItem: Idea): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Idea, newItem: Idea): Boolean =
        oldItem == newItem

    override fun getChangePayload(oldItem: Idea, newItem: Idea): Any? {

        if (oldItem.id == newItem.id) {
            return if (oldItem == newItem) {
                super.getChangePayload(oldItem, newItem)
            } else {

                val diff = Bundle()

                if (oldItem.colorPriority != newItem.colorPriority)
                    diff.putInt(ARG_COLOR_PRIORITY, newItem.colorPriority!!)

                if (oldItem.title != newItem.title)
                    diff.putString(ARG_TITLE, newItem.title)

                if (oldItem.location?.locationName != newItem.location?.locationName)
                    diff.putString(ARG_LOCATION, newItem.location?.locationName)

                if (oldItem.dateAndTime != newItem.dateAndTime)
                    diff.putString(ARG_DATE_AND_TIME, DateTimeConverter.convert(
                            newItem.dateAndTime,
                            IdeasFragment.currentCalendar
                        )
                    )

                if (oldItem.imagesUri.first() != newItem.imagesUri.first())
                    diff.putString(ARG_IMG_URI, newItem.imagesUri.first())

                if (oldItem.description != newItem.description) {
                    diff.putString(ARG_DESCRIPTION, newItem.description)
                }

                diff

            }
        }

        return super.getChangePayload(oldItem, newItem)

    }

}