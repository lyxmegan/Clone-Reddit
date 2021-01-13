package edu.cs371m.reddit.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.cs371m.reddit.R
import edu.cs371m.reddit.api.RedditPost
import edu.cs371m.reddit.glide.Glide

/**
 * Created by witchel on 8/25/2019
 */

// This adapter inherits from ListAdapter, which should mean that all we need
// to do is give it a new list and an old list and as clients we will never
// have to call notifyDatasetChanged().  Well, unfortunately, I can't implement
// equal for SpannableStrings correctly.  So clients of this adapter are, under
// certain circumstances, going to have to call notifyDatasetChanged()
class PostRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<RedditPost, PostRowAdapter.VH>(RedditDiff()) {
    companion object {
        val postTitle = "title"
        val postSelfText = "selfText"
        val postImageUrl = "imageURL"
        val postThumURL = "thumURL"
    }

    class RedditDiff : DiffUtil.ItemCallback<RedditPost>() {

        override fun areItemsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.selfText == newItem.selfText
                    && oldItem.publicDescription == newItem.publicDescription
                    && oldItem.displayName == oldItem.displayName
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        var title = view.findViewById<TextView>(R.id.title)
        var image = view.findViewById<ImageView>(R.id.image)
        var selfText = view.findViewById<TextView>(R.id.selfText)
        var score = view.findViewById<TextView>(R.id.score)
        var comments = view.findViewById<TextView>(R.id.comments)
        var rowFav = view.findViewById<ImageView>(R.id.rowFav)


        init {
            title.setOnClickListener {
            }
            rowFav.setOnClickListener {
                val position = adapterPosition
                if (viewModel.isFavarite(getItem(position))) {
                    rowFav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    viewModel.removeFavoritePost(getItem(position))
                } else {
                    rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
                    viewModel.addFavoritePost(getItem(position))
                }
            }
        }

        fun bind(post: RedditPost) {
            title.text = post.title
            viewModel.netFetchImage(post.imageURL, post.thumbnailURL, image)
            selfText.text = post.selfText
            score.text = post.score.toString()
            comments.text = post.commentCount.toString()
            if (viewModel.isFavarite(post)) {
                rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
            } else {
                rowFav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_post, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
        holder.title.setOnClickListener {
            val selectedTitle = getItem(position).title
            val selectedSelfText = getItem(position).selfText
            val selectedImageUrl = getItem(position).imageURL
            val selectedThumURL = getItem(position).thumbnailURL
            val intent = Intent(holder.itemView.context, OnePost::class.java)
            intent.apply {
                putExtra(postTitle, selectedTitle.toString())
                putExtra(postSelfText, selectedSelfText.toString())
                putExtra(postImageUrl, selectedImageUrl)
                putExtra(postThumURL, selectedThumURL)
            }
            holder.itemView.context.startActivity(intent)
        }
    }
}

