package edu.cs371m.reddit.ui.subreddits

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.cs371m.reddit.MainActivity
import edu.cs371m.reddit.R
import edu.cs371m.reddit.ui.MainViewModel
import edu.cs371m.reddit.ui.PostRowAdapter
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.fragment_rv.*


class Subreddits : Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: SubredditListAdapter

    companion object {
        fun newInstance(): Subreddits {
            return Subreddits()
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this){
            parentFragmentManager.popBackStack()
            Log.d("going back from sub", "back")
            val fav = activity?.findViewById<ImageView>(R.id.actionFavorite)
            fav?.isClickable = true
            val title = activity?.findViewById<TextView>(R.id.actionTitle)
            title?.text = "r/" + viewModel.subreddit.value
        }
    }
    private fun initRecyclerView(){
        val rv = view?.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = SubredditListAdapter(viewModel)
        rv?.adapter = adapter
        rv?.layoutManager = LinearLayoutManager(context)
        val itemDecor = DividerItemDecoration(rv!!.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_rv, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()
        viewModel.observeLivingSub().observe(viewLifecycleOwner, {
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
        })

        val swipe = view?.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipe?.isEnabled = false

    }
}
