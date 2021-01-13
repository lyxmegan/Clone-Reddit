package edu.cs371m.reddit.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.cs371m.reddit.MainActivity
import edu.cs371m.reddit.R
import edu.cs371m.reddit.ui.subreddits.SubredditListAdapter
import edu.cs371m.reddit.ui.subreddits.Subreddits
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.fragment_rv.*
import edu.cs371m.reddit.ui.Favorites



// XXX Write most of this file
class HomeFragment: Fragment() {
    private lateinit var swipe: SwipeRefreshLayout
    private val favoritesFragTag = "favoritesFragTag"
    private val subredditsFragTag = "subredditsFragTag"
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter : PostRowAdapter
    private lateinit var subreddits: Subreddits
    private lateinit var favorites: Favorites
    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private fun initSearch() {
        activity
            ?.findViewById<EditText>(R.id.actionSearch)
            ?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if(s.isEmpty()) (activity as MainActivity).hideKeyboard()
                    viewModel.setSearchTerm(s.toString())
                }
            })
    }

    // Set up the adapter
    private fun initAdapter(root: View) {
    }
    private fun initSwipeLayout(root: View) {
    }

    private fun initRecyclerView(){
        val rv = view?.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = PostRowAdapter(viewModel)
        rv?.adapter = adapter
        rv?.layoutManager = LinearLayoutManager(context)
        val itemDecor = DividerItemDecoration(rv!!.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_rv, container, false)
        (activity as AppCompatActivity).actionTitle.text = "r/aww"
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initSearch()
        initRecyclerView()
        viewModel.observeLivingPosts().observe(viewLifecycleOwner, Observer {
            requireActivity().swipeRefreshLayout.apply {
                isRefreshing = false
                setOnRefreshListener {
                    viewModel.netPosts()
                }
            }
            adapter.submitList(it)
            adapter.notifyDataSetChanged()

        })
        viewModel.observeTitle().observe(viewLifecycleOwner, {
            Log.d("title ", "$it")
            viewModel.setNewSubredit(it)
            viewModel.netPosts()
            requireActivity().actionTitle.text = "r/$it"
        })

        parentFragmentManager.addOnBackStackChangedListener {
            if(parentFragmentManager.backStackEntryCount == 0){
                Log.d("backStackchangedcalled", "called")
                adapter.notifyDataSetChanged()
            }
        }

        requireActivity().actionTitle.setOnClickListener {
            val subFrag = parentFragmentManager.findFragmentByTag(subredditsFragTag)
            val favFrag = parentFragmentManager.findFragmentByTag(favoritesFragTag)
            Log.d("subfrag", "$subFrag")
            Log.d("fav frag", "$favFrag")

            if (subFrag != null) {
                Log.d("....sub frag", "$subFrag")
            }
            if (subFrag == null) {
                Log.d("do", "process this")
                val favTitle = activity?.findViewById<ImageView>(R.id.actionFavorite)
                favTitle?.isClickable = false
                val title = activity?.findViewById<TextView>(R.id.actionTitle)
                title?.text = "Pick"
                parentFragmentManager.beginTransaction()
                    .add(R.id.main_frame, Subreddits.newInstance(), subredditsFragTag)
                    .addToBackStack("sub")
                    .commit()
            }

        }

        requireActivity().actionFavorite.setOnClickListener {
            val subFrag = parentFragmentManager.findFragmentByTag(subredditsFragTag)
            val favFrag = parentFragmentManager.findFragmentByTag(favoritesFragTag)
            //val favFrag = parentFragmentManager.findFragmentByTag(favoritesFragTag)
            Log.d("fav frag", "$favFrag")
            Log.d("subGraf", "$subFrag")
            if (favFrag != null) {
                Log.d("....fav frag", "$favFrag")
            }
            if (favFrag == null) {
                Log.d("do", "process this")
                val title = activity?.findViewById<TextView>(R.id.actionTitle)
                title?.isClickable = false
                title?.text = "Favorites"
                parentFragmentManager.beginTransaction()
                    .add(R.id.main_frame, Favorites.newInstance(), favoritesFragTag)
                    .addToBackStack("fav")
                    .commit()
            }
        }

        viewModel.observeTitle().observe(viewLifecycleOwner, {
            Log.d("title ", "$it")
            viewModel.setNewSubredit(it)
            viewModel.netPosts()
            requireActivity().actionTitle.text = "r/$it"
        })

    }

    override fun onResume() {
        super.onResume()
        Log.d("resume is called", "called")
    }
}