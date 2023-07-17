package com.uniuwo.simpledcit.core.models

import com.uniuwo.simpledcit.core.databus.FavoriteEntry
import com.uniuwo.simpledcit.core.databus.SimpleDataBus

object WordListViewModel {
    val items: MutableList<WordHolder> = ArrayList()
    /** Wrapper for word. eg. currentItem = WordHolder(word) */
    var currentItem: WordHolder? = null

    var wordlist: WordList? = null
    var currentWord: String? = null

    val searchWords: MutableList<String> = ArrayList()

    fun pushSearchWord(word: String) {
        searchWords.remove(word)
        searchWords.add(0, word)
    }

    fun searchWord(word: String, pushSearchWord :Boolean = false): List<String> {
        val result = SimpleDataBus.searchSimpleByWord(word)

        if(pushSearchWord && result.contains(word)){
            pushSearchWord(word)
        }

        return result
    }

    fun findByWord(word: String): List<WordEntry> {
        return SimpleDataBus.findSimpleByWord(word)
    }

    fun findDetailByWord(word: String): List<WordEntry> {
        return SimpleDataBus.findDetailByWord(word)
    }

    fun makeDetailContent(word: String?): String {
        if (word == null) return ""

        val simples = findByWord(word)
        val details = findDetailByWord(word)

        if (simples.isEmpty() && details.isEmpty()) {
            val page = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body>" +
                    "<h1>${word}</h1><hr/>" +
                    "<em>未找到定义</em>" +
                    "</body></html>"
            return page
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
                    "<h1>${word}</h1><hr/>" +
                    contentSimple +
                    "<hr/>" +
                    contentDetail +
                    "</body></html>"
            return page
        }
    }


}

object WordFavoriteViewModel {

    fun getFavorites(): List<FavoriteEntry> {
        val favorites = SimpleDataBus.getAllFavorite()
        return favorites ?: emptyList()
    }
}

object WordDetailViewModel {

    var words: MutableList<String> = mutableListOf()
    var currentWord: String? = null

    fun prevWord(word: String?): String {
        if (word == null) return ""

        try {
            val index = words.indexOf(word)
            if (index > 0 && index < words.size) {
                return words[index - 1]
            }
        } catch (_: Exception) {

        }

        return word
    }

    fun nextWord(word: String?): String {
        if (word == null) return ""

        try {
            val index = words.indexOf(word)
            if (index >= 0 && index < (words.size - 1)) {
                return words[index + 1]
            }
        } catch (_: Exception) {

        }

        return word
    }
}

object WordSearchViewModel {

    var words: MutableList<String> = mutableListOf()
    var searchWord: String? = null

    var suggests: MutableList<String> = mutableListOf()
}
