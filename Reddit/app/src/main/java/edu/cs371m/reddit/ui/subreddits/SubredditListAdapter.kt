package edu.cs371m.reddit.ui.subreddits

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.toSpannable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import edu.cs371m.reddit.R
import edu.cs371m.reddit.api.RedditPost
import edu.cs371m.reddit.glide.Glide
import edu.cs371m.reddit.ui.MainViewModel
import edu.cs371m.reddit.ui.PostRowAdapter
import kotlinx.android.synthetic.main.action_bar.*

// NB: Could probably unify with PostRowAdapter if we had two
// different VH and override getItemViewType
// https://medium.com/@droidbyme/android-recyclerview-with-multiple-view-type-multiple-view-holder-af798458763b
class SubredditListAdapter(private val viewModel: MainViewModel)
    : RecyclerView.Adapter<SubredditListAdapter.VH>() {

    var subredditsList = listOf<RedditPost>()
    // ViewHolder pattern minimizes calls to findViewById
   inner class VH(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        // XXX Write me.
        private var subRowPic = itemView.findViewById<ImageView>(R.id.subRowPic)
        private var subRowHeading = itemView.findViewById<TextView>(R.id.subRowHeading)
        private var subRowDetails = itemView.findViewById<TextView>(R.id.subRowDetails)
        // NB: This one-liner will exit the current fragment
        // (itemView.context as FragmentActivity).supportFragmentManager.popBackStack()
        init {
            subRowHeading.setOnClickListener {
                viewModel.setTitle(subRowHeading.text.toString())
                (itemView.context as FragmentActivity).apply {
                    actionFavorite.isClickable = true
                    supportFragmentManager.popBackStack()
                }
            }
        }
       fun bind(item: RedditPost){
           val url = item.iconURL
           if(url != null){
           viewModel.netFetchImage(url, url, subRowPic)
           }
           subRowHeading.text = item.displayName
           subRowDetails.text = item.publicDescription
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_subreddit, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(subredditsList[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        if (subredditsList != null) {
            return subredditsList.size
        }
        return 0
    }

    fun addAll(items: List<RedditPost>) {
        subredditsList = items
    }
}