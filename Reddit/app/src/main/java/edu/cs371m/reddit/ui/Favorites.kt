package edu.cs371m.reddit.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.cs371m.reddit.R
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.coroutines.flow.callbackFlow
import androidx.lifecycle.Observer

class Favorites: Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: PostRowAdapter

    companion object {
        fun newInstance(): Favorites {
            return Favorites()
        }
    }

    private fun initRecyclerView(root: View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = PostRowAdapter(viewModel)
        rv?.adapter = adapter
        rv?.layoutManager = LinearLayoutManager(context)
        val itemDecor = DividerItemDecoration(rv?.context, LinearLayoutManager.VERTICAL)
        rv?.addItemDecoration(itemDecor)
        val swipe = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipe?.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            parentFragmentManager.popBackStack()
            val title = activity?.findViewById<TextView>(R.id.actionTitle)
            title?.isClickable = true
            title?.text = "r/" + viewModel.subreddit.value
            Log.d("going back", "back")
            Log.d("title clickable", "${title?.isClickable}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rv, container, false)
        initRecyclerView(root)
        viewModel.observeLivingFav().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        return root
    }
}

