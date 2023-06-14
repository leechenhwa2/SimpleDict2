package com.uniuwo.simpledcit.core.models

import com.uniuwo.simpledcit.core.databus.FavoriteEntry
import com.uniuwo.simpledcit.core.databus.SimpleDataBus

object WordListViewModel {
    val items: MutableList<WordHolder> = ArrayList()

    var currentItem: WordHolder? = null

    val searchWords: MutableList<String> = ArrayList()

    fun searchWord(word: String): List<String> {
        if(searchWords.contains(word)){
            searchWords.remove(word)
        }
        searchWords.add(0, word)

        return SimpleDataBus.searchSimpleByWord(word)
    }

    fun findByWord(word: String): List<WordEntry> {
        return SimpleDataBus.findSimpleByWord(word)
    }

    fun findDetailByWord(word: String): List<WordEntry> {
        return SimpleDataBus.findDetailByWord(word)
    }
}

object WordFavoriteViewModel {

    fun getFavorites(): List<FavoriteEntry> {
        val favorites = SimpleDataBus.getAllFavorite()
        return favorites ?: emptyList()
    }
}