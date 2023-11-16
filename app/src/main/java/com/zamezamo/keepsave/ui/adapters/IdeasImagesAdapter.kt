package com.zamezamo.keepsave.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.zamezamo.keepsave.R

class IdeasImagesAdapter(private val imagesUri: List<String>) : RecyclerView.Adapter<IdeasImagesAdapter.IdeasImagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): IdeasImagesViewHolder =
        IdeasImagesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.activity_idea_edit_images_item, parent, false)
        )

    override fun onBindViewHolder(holder: IdeasImagesViewHolder, position: Int) {
        val item = imagesUri[position]

        holder.bind(item)
    }


    override fun getItemCount(): Int = imagesUri.size

    class IdeasImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnLongClickListener {

        init {
            itemView.setOnLongClickListener(this)
        }

        private val imageViewEditIdea: ImageView =
            itemView.findViewById(R.id.imageViewEditIdea)
        private val imageButtonEditIdeaRemove: ImageView =
            itemView.findViewById(R.id.imageButtonEditIdeaRemove)

        fun bind(imgUri: String) {

            Glide.with(itemView.context)
                .load(imgUri)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(12)))
                .into(imageViewEditIdea)

        }

        override fun onLongClick(view: View): Boolean {
            TODO("Not yet implemented")
        }

    }


}