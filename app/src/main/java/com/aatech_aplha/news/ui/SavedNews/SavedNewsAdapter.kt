package com.aatech_aplha.news.ui.SavedNews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aatech_aplha.news.Database.NewsSaved
import com.aatech_aplha.news.R
import com.aatech_aplha.news.databinding.RowSavedNewsBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.text.SimpleDateFormat

class SavedNewsAdapter(private val listener: OnClickListener) :
    ListAdapter<NewsSaved, SavedNewsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowSavedNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item, binding.root)
                    }
                }
            }
        }

        fun bind(news: NewsSaved) {
            binding.apply {
                Glide.with(itemView)
                    .load(news.urlToImage)
                    .centerCrop()
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_no_photo)
                    .into(ivIcon)
                tvTitle.text = news.title
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val outputFormat = SimpleDateFormat("MMM dd, YYYY hh:mm aaa")
                val parsedDate = inputFormat.parse(news.publishedAt)
                val formattedDate = outputFormat.format(parsedDate)
                tvDate.text = formattedDate
                ivSaved.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        if (item != null) {
                            listener.onShareClick(item)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            RowSavedNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback() : DiffUtil.ItemCallback<NewsSaved>() {
        override fun areItemsTheSame(oldItem: NewsSaved, newItem: NewsSaved): Boolean =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: NewsSaved, newItem: NewsSaved): Boolean =
            oldItem == newItem
    }

    interface OnClickListener {
        fun onShareClick(news: NewsSaved)
        fun onItemClick(news: NewsSaved, view: View)
    }
}