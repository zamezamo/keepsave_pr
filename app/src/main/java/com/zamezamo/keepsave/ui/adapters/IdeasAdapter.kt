package com.zamezamo.keepsave.ui.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.card.MaterialCardView
import com.zamezamo.keepsave.R
import com.zamezamo.keepsave.domain.Idea
import com.zamezamo.keepsave.ui.views.IdeasEditActivity
import com.zamezamo.keepsave.ui.views.IdeasFragment
import com.zamezamo.keepsave.utils.DateTimeConverter

class IdeasAdapter : ListAdapter<Idea, IdeasAdapter.IdeasViewHolder>(DiffCallbackIdeas()) {

    companion object {
        private const val IDEA = "ideaSerialized"
    }

    var tracker: SelectionTracker<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): IdeasViewHolder =
        IdeasViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_ideas_item, parent, false)
        )

    override fun onBindViewHolder(holder: IdeasViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList())
    }

    override fun onBindViewHolder(viewHolder: IdeasViewHolder, position: Int, payload: List<Any>) {

        val item = getItem(position)

        if (payload.isEmpty() || payload[0] !is Bundle) {
            viewHolder.bind(item)
        } else {
            val bundle = payload[0] as Bundle
            viewHolder.update(bundle)
        }

    }

    override fun getItemCount(): Int {
        return currentList.size
    }



    inner class IdeasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private val materialCardView: MaterialCardView =
            itemView.findViewById(R.id.materialCardViewIdeasItem)

        private val constraintLayoutIdeasItemMain: ConstraintLayout =
            itemView.findViewById(R.id.constraintLayoutIdeasItemMain)
        private val textViewIdeasItemTitle: TextView =
            itemView.findViewById(R.id.textViewIdeasItemTitle)
        private val textViewIdeasItemLocation: TextView =
            itemView.findViewById(R.id.textViewIdeasItemLocation)
        private val textViewIdeasItemDateTime: TextView =
            itemView.findViewById(R.id.textViewIdeasItemDateTime)

        private val constraintLayoutIdeasItemDescription: ConstraintLayout =
            itemView.findViewById(R.id.constraintLayoutIdeasItemDescription)
        private val imageViewIdeasItemDescription: ImageView =
            itemView.findViewById(R.id.imageViewIdeasItemDescription)
        private val textViewIdeasItemDescription: TextView =
            itemView.findViewById(R.id.textViewIdeasItemDescription)

        fun bind(idea: Idea) {

            constraintLayoutIdeasItemMain.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    idea.colorPriority!!
                )
            )
            textViewIdeasItemTitle.text = idea.title
            setViewIfNotEmpty(textViewIdeasItemLocation, idea.location?.locationName)
            setViewIfNotEmpty(
                textViewIdeasItemDateTime,
                DateTimeConverter.convert(idea.dateAndTime, IdeasFragment.currentCalendar)
            )

            setViewIfNotEmpty(imageViewIdeasItemDescription, idea.imagesUri.firstOrNull())
            setViewIfNotEmpty(textViewIdeasItemDescription, idea.description)

            changeLayoutIdeasDescVisibilityIfNotEmpty()

            tracker?.let {

                materialCardView.isChecked = it.isSelected(idea.id)

            }

        }

        fun update(bundle: Bundle) {

            if (bundle.containsKey(DiffCallbackIdeas.ARG_COLOR_PRIORITY)) {
                constraintLayoutIdeasItemMain.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        bundle.getInt(DiffCallbackIdeas.ARG_COLOR_PRIORITY)
                    )
                )
            }

            if (bundle.containsKey(DiffCallbackIdeas.ARG_TITLE)) {
                textViewIdeasItemTitle.text = bundle.getString(DiffCallbackIdeas.ARG_TITLE)
            }

            if (bundle.containsKey(DiffCallbackIdeas.ARG_LOCATION)) {
                setViewIfNotEmpty(
                    textViewIdeasItemLocation,
                    bundle.getString(DiffCallbackIdeas.ARG_LOCATION)
                )
            }

            if (bundle.containsKey(DiffCallbackIdeas.ARG_DATE_AND_TIME)) {
                setViewIfNotEmpty(
                    textViewIdeasItemDateTime,
                    bundle.getString(DiffCallbackIdeas.ARG_DATE_AND_TIME)
                )
            }


            if (bundle.containsKey(DiffCallbackIdeas.ARG_IMG_URI)) {
                setViewIfNotEmpty(
                    imageViewIdeasItemDescription,
                    bundle.getString(DiffCallbackIdeas.ARG_IMG_URI)
                )
            }

            if (bundle.containsKey(DiffCallbackIdeas.ARG_DESCRIPTION)) {
                setViewIfNotEmpty(
                    textViewIdeasItemDescription,
                    bundle.getString(DiffCallbackIdeas.ARG_DESCRIPTION)
                )
            }

            changeLayoutIdeasDescVisibilityIfNotEmpty()

        }

        private fun setViewIfNotEmpty(view: View, content: String?) {
                when (view) {
                    is TextView -> {
                        if (!content.isNullOrEmpty()) {
                            view.text = content
                            view.visibility = View.VISIBLE
                        }
                        else view.visibility = View.GONE
                    }
                    is ImageView -> {
                        if (!content.isNullOrEmpty()) {
                            Glide.with(itemView.context)
                                .load(content)
                                .transform(MultiTransformation(CenterCrop(), RoundedCorners(12)))
                                .into(view)
                            view.visibility = View.VISIBLE
                        }
                        else
                            view.visibility = View.GONE
                    }
                }

        }

        private fun changeLayoutIdeasDescVisibilityIfNotEmpty() {
            if (
                imageViewIdeasItemDescription.visibility == View.VISIBLE ||
                textViewIdeasItemDescription.visibility == View.VISIBLE
            )
                constraintLayoutIdeasItemDescription.visibility = View.VISIBLE
            else
                constraintLayoutIdeasItemDescription.visibility = View.GONE
        }


        fun getItem(): ItemDetailsLookup.ItemDetails<String> =

            object : ItemDetailsLookup.ItemDetails<String>() {

                override fun getPosition(): Int = adapterPosition

                override fun getSelectionKey(): String = getItem(adapterPosition).id!!

            }

        override fun onClick(view: View) {
            val idea = getItem(adapterPosition)
            val intent = Intent(view.context, IdeasEditActivity::class.java)
            intent.putExtra(IDEA, idea)
            view.context.startActivity(intent)
        }

    }

}

