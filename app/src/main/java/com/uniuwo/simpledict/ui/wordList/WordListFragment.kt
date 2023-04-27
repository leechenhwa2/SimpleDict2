package com.uniuwo.simpledict.ui.wordList

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniuwo.simpledict.R
import com.uniuwo.simpledict.databus.SimpleDataBus
import com.uniuwo.simpledict.models.WordEntry
import com.uniuwo.simpledict.models.WordListViewModel
import com.uniuwo.simpledict.ui.wordDetail.WordDetailFragment
import com.uniuwo.simpledict.utils.Toastx

/**
 * A fragment representing a list of Items.
 */
class WordListFragment : Fragment() {

    private lateinit var mAdapter: WordItemRecyclerViewAdapter

    var wordDetailFragment: WordDetailFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //for search view menu action
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_word_list, container, false)

        // Set the adapter
        mAdapter = WordItemRecyclerViewAdapter(
            WordListViewModel.items,
            onItemClickListener = { showDetailView() })

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null) {
            loadRandomItems()
        }
    }

    private fun loadRandomItems() {
        Handler(Looper.myLooper()!!).post {
            Thread {
                if (WordListViewModel.items.isEmpty()) {
                    WordListViewModel.items.addAll(SimpleDataBus.randomSimpleWords(10))
                    requireActivity().runOnUiThread {
                        mAdapter.notifyItemRangeChanged(0, WordListViewModel.items.size)
                    }
                }
            }.start()
        }
    }

    private fun showDetailView() {
        if(WordListViewModel.currentItem == null) return
        Toast.makeText(requireContext(), WordListViewModel.currentItem!!.entry.word, Toast.LENGTH_LONG).show()
        if(wordDetailFragment == null){
            wordDetailFragment =  WordDetailFragment()
        }
        wordDetailFragment?.show(childFragmentManager, "detail")
    }

    //search view
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        initSearchAction(menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initSearchAction(menu: Menu) {
        // Get the SearchView and set the searchable configuration
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
//            isIconifiedByDefault = true //default

            val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
            val to = intArrayOf(R.id.item_label)
            val cursorAdapter = SimpleCursorAdapter(context, R.layout.search_item, null,
                from, to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
            suggestionsAdapter = cursorAdapter


            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Toastx.short(requireContext(), "Query: $query")
                    if (query != null) {
                        val result = WordListViewModel.findByWord(query)
                        updateItems(result)
                    }

                    //collapse to icon again
                    //Note: call twice: first clear query text ,second iconify
                    setQuery("", false) // or isIconified = true
                    isIconified = true

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
//                    Toastx.short(requireContext(), "newText: $newText")
                    if (newText != null) {
                        val result = WordListViewModel.searchWord(newText)
                        val cursor = MatrixCursor(
                            arrayOf(
                                BaseColumns._ID,
                                SearchManager.SUGGEST_COLUMN_TEXT_1
                            )
                        )
                        result.forEachIndexed { index, s ->
                            cursor.addRow(arrayOf(index, s))
                        }
                        cursorAdapter.changeCursor(cursor)

                    }
                    return false
                }

            })

            setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {
                    return false
                }

                @SuppressLint("Range")
                override fun onSuggestionClick(position: Int): Boolean {
                    val cursor = suggestionsAdapter.getItem(position) as Cursor
                    val selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                    setQuery(selection, false)

                    return true
                }

            })
        }
    }

    private fun updateItems(entries: List<WordEntry>) {
        val count1 = WordListViewModel.items.size
        WordListViewModel.items.clear()
        requireActivity().runOnUiThread {
            mAdapter.notifyItemRangeRemoved(0, count1)
        }

        WordListViewModel.items.addAll(0, entries)
        requireActivity().runOnUiThread {
            mAdapter.notifyItemRangeChanged(0, WordListViewModel.items.size)
        }
    }

    fun hideKeyBoard() {
        val inputMethodManager = requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}