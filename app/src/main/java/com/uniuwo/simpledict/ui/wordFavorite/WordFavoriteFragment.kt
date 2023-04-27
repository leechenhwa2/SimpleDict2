package com.uniuwo.simpledict.ui.wordFavorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniuwo.simpledict.R
import com.uniuwo.simpledict.models.WordFavoriteViewModel
import com.uniuwo.simpledict.models.WordListViewModel
import com.uniuwo.simpledict.ui.wordDetail.WordDetailFragment

/**
 * A fragment representing a list of Items.
 */
class WordFavoriteFragment : Fragment() {

    private lateinit var mAdapter: FavoriteItemRecyclerViewAdapter
    var wordDetailFragment: WordDetailFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_word_favorite_list, container, false)

        // Set the adapter
        mAdapter = FavoriteItemRecyclerViewAdapter(
            WordFavoriteViewModel.getFavorites(),
            onItemClickListener = { showDetailView() })

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
            }
        }
        return view
    }

    private fun showDetailView() {
        if(WordListViewModel.currentItem == null) return
        Toast.makeText(requireContext(), WordListViewModel.currentItem!!.entry.word, Toast.LENGTH_LONG).show()
        if(wordDetailFragment == null){
            wordDetailFragment =  WordDetailFragment()
        }
        wordDetailFragment?.show(childFragmentManager, "detail")
    }

}