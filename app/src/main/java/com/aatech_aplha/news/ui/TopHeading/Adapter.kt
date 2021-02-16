package com.aatech_aplha.news.ui.TopHeading

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aatech_aplha.news.R
import com.aatech_aplha.news.data.News
import com.aatech_aplha.news.databinding.RowNewsBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.text.SimpleDateFormat

class Adapter(
    private val listener: ClickListener
) : PagingDataAdapter<News, Adapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: RowNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onClickListener(item, binding.imageNews)
                    }
                }
            }
        }

        fun bind(news: News) {
            binding.apply {
                val transformation = MultiTransformation(CenterCrop(), RoundedCorners(30))
                Glide.with(itemView)
                    .load(news.urlToImage)
                    .transform(transformation)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressBar.visibility = View.INVISIBLE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressBar.visibility = View.INVISIBLE
                            return false
                        }

                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_no_photo)
                    .into(imageNews)

                tvTitle.text = news.title
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val outputFormat = SimpleDateFormat("MMM dd, YYYY hh:mm aaa")
                val parsedDate = inputFormat.parse(news.publishedAt)
                val formattedDate = outputFormat.format(parsedDate)
                tvDate.text = formattedDate
                tvAuthor.text = news.source.name
                ivShare.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        if (item != null) {
                            listener.onShareClick(item)
                        }
                    }
                }
                ivSaved.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        if (item != null) {
                            listener.onSaveNewsClick(item)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            RowNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean =
            oldItem.url == oldItem.url

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean =
            oldItem == newItem
    }

    interface ClickListener {
        fun onClickListener(news: News, view: View)
        fun onShareClick(news: News)
        fun onSaveNewsClick(news: News)
    }
}