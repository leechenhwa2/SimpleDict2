package com.uniuwo.simpledict.ui.wordDetail

import android.content.ContentResolver.MimeTypeInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.DialogFragment
import com.uniuwo.simpledict.R
import com.uniuwo.simpledict.databinding.FragmentWordDetailBinding
import com.uniuwo.simpledict.models.WordEntry
import com.uniuwo.simpledict.models.WordHolder
import com.uniuwo.simpledict.models.WordListViewModel


class WordDetailFragment : DialogFragment() {

    private lateinit var binding: FragmentWordDetailBinding
    lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//      return  inflater.inflate(R.layout.fragment_word_detail, container, false)

        binding = FragmentWordDetailBinding.inflate(inflater)
        binding.buttonPrev.setOnClickListener { tryGoPrev() }
        binding.buttonNext.setOnClickListener { tryGoNext() }
        webView = binding.contentView
        return binding.root
    }

    private fun tryGoPrev() {
        val index = WordListViewModel.items.indexOf(WordListViewModel.currentItem)
        if (index > 0) {
            WordListViewModel.currentItem = WordListViewModel.items[index - 1]
            loadDetailPage()
        }
    }

    private fun tryGoNext() {
        val index = WordListViewModel.items.indexOf(WordListViewModel.currentItem)
        if (index < WordListViewModel.items.size - 1) {
            WordListViewModel.currentItem = WordListViewModel.items[index + 1]
            loadDetailPage()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadDetailPage()
    }

    //note: larger UI
    override fun getTheme(): Int {
        return R.style.Theme_FullScreenDialog
    }

    private fun loadDetailPage() {
        loadItemDetail(WordListViewModel.currentItem)

        updateButtonStates()
    }

    private fun updateButtonStates() {
        val index = WordListViewModel.items.indexOf(WordListViewModel.currentItem)

        if(index > 0){
            binding.buttonPrev.isEnabled = true
            val prev = WordListViewModel.items[index - 1]
            binding.buttonPrev.text = "< ${prev.word}"
        } else {
            binding.buttonPrev.isEnabled = false
        }

        if(index < (WordListViewModel.items.size - 1)) {
            binding.buttonNext.isEnabled = true
            val next = WordListViewModel.items[index + 1]
            binding.buttonNext.text = "${next.word} >"
        } else {
            binding.buttonNext.isEnabled = false
        }

    }

    private fun loadItemDetail(item: WordHolder?) {
        if (item == null) return

        val entries : MutableList<WordEntry> = mutableListOf()

        val simples = WordListViewModel.findByWord(item.word)
        val details = WordListViewModel.findDetailByWord(item.word)
        entries.addAll(simples)
        entries.addAll(details)

        if (entries.isEmpty()) {
            val page = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body>" +
                    "<em>未找到定义</em>" +
                    "</body></html>"
            webView.loadData(page, "text/html", "UTF-8")
        } else {
            val content =
                entries.map {
                    "<article class=\"dict-entry\"><h1>${it.entry.word}</h1> ${it.entry.content}" + "</article>"
                }.joinToString("")

            val page = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body>" +
                    content +
                    "</body></html>"
            webView.loadData(page, "text/html", "UTF-8")
        }
    }
}