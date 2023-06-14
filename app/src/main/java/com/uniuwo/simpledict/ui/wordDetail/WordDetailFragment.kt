package com.uniuwo.simpledict.ui.wordDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.DialogFragment
import com.uniuwo.simpledcit.core.models.WordHolder
import com.uniuwo.simpledcit.core.models.WordListViewModel
import com.uniuwo.simpledict.R
import com.uniuwo.simpledict.databinding.FragmentWordDetailBinding


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

        val simples = WordListViewModel.findByWord(item.word)
        val details = WordListViewModel.findDetailByWord(item.word)

        if (simples.isEmpty() && details.isEmpty()) {
            val page = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body>" +
                    "<h1>${item.word}</h1><hr/>" +
                    "<em>未找到定义</em>" +
                    "</body></html>"
            webView.loadData(page, "text/html", "UTF-8")
        } else {
            val contentSimple =
                simples.map {
                    "<article class=\"dict-entry\">${it.entry.content}" + "</article>"
                }.joinToString("")
            val contentDetail =
                details.map {
                    "<article class=\"dict-entry\">${it.entry.content}" + "</article>"
                }.joinToString("")

            val page = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body>" +
                    "<h1>${item.word}</h1><hr/>" +
                    contentSimple +
                    "<hr/>" +
                    contentDetail +
                    "</body></html>"
            webView.loadData(page, "text/html", "UTF-8")
        }
    }
}