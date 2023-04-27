package com.uniuwo.simpledict.models

import com.uniuwo.simpledict.databus.FavoriteEntry
import com.uniuwo.simpledict.databus.SimpleDataBus

object WordListViewModel {
    val items: MutableList<WordEntry> = ArrayList()

    var currentItem: WordEntry? = null

    val searchWords: MutableList<String> = ArrayList()

    fun searchWord(word: String): List<String> {
        if(searchWords.contains(word)){
            searchWords.remove(word)
        }
        searchWords.add(0, word)

        return SimpleDataBus.searchSimpleByWord(word)
    }

    fun findByWord(word: String): List<WordEntry> {
        val result = SimpleDataBus.findSimpleByWord(word)
        return result
    }
}

object WordFavoriteViewModel {
    val items: MutableList<WordEntry> = ArrayList()

    fun getFavorites(): List<FavoriteEntry> {
        val favorites = SimpleDataBus.getAllFavorite()
        return favorites ?: emptyList()
    }
}