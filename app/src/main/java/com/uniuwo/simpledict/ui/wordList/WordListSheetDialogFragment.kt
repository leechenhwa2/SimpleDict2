package com.uniuwo.simpledict.ui.wordList

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uniuwo.simpledict.R
import com.uniuwo.simpledict.models.WordList

class WordListSheetDialogFragment(
    val adapter: ArrayAdapter<WordList>,
    val onItemClickListener: AdapterView.OnItemClickListener
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_word_list_sheet_dialog, container, false)

        val listView: ListView = view.findViewById(R.id.listView)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            onItemClickListener.onItemClick(parent, view, position, id)
            dismiss()
        }

        return view
    }

    companion object {
        val TAG = WordListSheetDialogFragment.javaClass.simpleName
    }
}

