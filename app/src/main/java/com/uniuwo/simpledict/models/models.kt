package com.uniuwo.simpledict.models

import com.uniuwo.simpledict.databus.DictEntry
import com.uniuwo.simpledict.databus.SimpleDictDatabase
import java.io.File

class WordEntry {

    lateinit var word: String
    lateinit var db: SimpleDictDatabase
    lateinit var entry: DictEntry

    constructor(word: String, db: SimpleDictDatabase, entry: DictEntry) {
        this.word = word
        this.db = db
        this.entry = entry
    }

    constructor()
    constructor(db: SimpleDictDatabase, entry: DictEntry) {
        this.db = db
        this.entry = entry

        this.word = entry.word
    }

    constructor(word: String) {
        this.word = word
    }


    override fun toString(): String {
        return word
    }
}

data class WordList(val path: File, val items: MutableList<String>, val title: String) {
    override fun toString(): String {
        return title
    }
}

class WordHolder (var word: String, var count: Int = 0, var tag: Any? = null)
