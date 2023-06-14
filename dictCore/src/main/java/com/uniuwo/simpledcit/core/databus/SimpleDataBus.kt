package com.uniuwo.simpledcit.core.databus

import android.content.Context
import com.uniuwo.simpledcit.core.models.WordEntry
import com.uniuwo.simpledcit.core.models.WordHolder
import com.uniuwo.simpledcit.core.models.WordList
import java.io.File
import kotlin.random.Random
import kotlin.random.nextLong

object SimpleDataBus {

    private var externalDatabases: List<SimpleDictDatabase> = mutableListOf()

    val simpleDir = "simple"
    val detailDir = "detail"
    val wordListDir = "wordlist"

    var simpleDatabases: MutableList<SimpleDictDatabase> = mutableListOf()
    var detailDatabases: MutableList<SimpleDictDatabase> = mutableListOf()

    var wordListRepo: MutableList<WordList> = mutableListOf()

    var favoriteDatabase: FavoriteDatabase? = null

    var mainDirPath: File? = null

    fun initDatabases(appContext: Context) {
        initExternalDatabases(appContext)
        initFavoriteDatabase(appContext)
    }

    fun checkFolders(appContext: Context) {
        //scan external dir of app
        // eg. /storage/emulated/0/Android/data/com.uniuwo.simpledict/files
        val dirs = appContext.getExternalFilesDirs(null)
        if (dirs.isNotEmpty()) {
            val mainDir = dirs[0]
            mainDirPath = mainDir

            val readmeFile = File(mainDir, "readme.txt")
            checkReadmeFile(readmeFile)

            val simpleFile = File(mainDir, simpleDir)
            checkDir(simpleFile)

            val detailFile = File(mainDir, detailDir)
            checkDir(detailFile)

            val wordListFile = File(mainDir, wordListDir)
            checkDir(wordListFile)
        }
    }

    private fun initExternalDatabases(appContext: Context) {
        if (mainDirPath != null) {
            val simpleFile = File(mainDirPath, simpleDir)
            checkDir(simpleFile)

            val detailFile = File(mainDirPath, detailDir)
            checkDir(detailFile)

            simpleDatabases.addAll(scanDbs(appContext, simpleFile))
            detailDatabases.addAll(scanDbs(appContext, detailFile))
        }
    }

    private fun initFavoriteDatabase(appContext: Context) {
        val dir = appContext.filesDir
        val favoriteFile = File(dir, "favorite.db")
        favoriteDatabase = FavoriteDatabase(appContext, favoriteFile.absolutePath)
    }

    private fun checkReadmeFile(readmeFile: File) {
        if (readmeFile.exists()) return

        try {
            val state = readmeFile.createNewFile()
            if (!state) return

            readmeFile.outputStream().use {
                it.bufferedWriter().use {
                    it.write("支持SQLite格式数据库文件，扩展名用.db")
                }
            }
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }

    private fun scanDbs(appContext: Context, dir: File): MutableList<SimpleDictDatabase> {
        if (!dir.exists() || !dir.isDirectory) return mutableListOf()

        val files = dir.listFiles { _, name -> name.lowercase().endsWith(".db") }
        return files.map { file ->
            SimpleDictDatabase(appContext, file.absolutePath)
                .apply {
                    title = file.name
                }
        }.toMutableList()
    }

    private fun checkDir(dir: File) {
        if (!dir.exists()) {
            dir.mkdir()
        } else if (!dir.isDirectory) {
            dir.delete()
            dir.mkdir()
        }
    }


    fun findSimpleByWord(word: String): List<WordEntry> {
        return simpleDatabases.flatMap { db ->
            db.dictEntryDao.findByWord(word).map { dictEntry -> WordEntry(db, dictEntry) }
        }
    }

    fun findDetailByWord(word: String): List<WordEntry> {
        return detailDatabases.flatMap { db ->
            db.dictEntryDao.findByWord(word).map { dictEntry -> WordEntry(db, dictEntry) }
        }
    }

    //demo
    fun randomSimpleWords(count: Int): List<WordHolder> {
        val result = mutableListOf<WordHolder>()

        for (db in simpleDatabases) {
            val size = db.dictEntryDao.count()

            if (count > size) {
                val list = db.dictEntryDao.getAll()
                    .map { dictEntry -> WordHolder(dictEntry.word) }
                result.addAll(list)
                continue
            }

            val lastWord = db.dictEntryDao.lastWord()
            if (lastWord == null) continue

            val ids: List<Long> = (1..count).toList()
                .map { Random.nextLong(LongRange(100, lastWord.id)) }
                .sorted()

            val list = db.dictEntryDao.findAllByIds(ids.toLongArray())
                .map { dictEntry -> WordHolder(dictEntry.word) }
            result.addAll(list)
        }

        return if (result.size <= count) result else result.subList(0, count)
    }

    fun searchSimpleByWord(word: String): List<String> {
        return simpleDatabases.flatMap { db ->
            db.dictEntryDao.searchTop5ByWord(word)
        }
    }

    fun isFavorite(word: String): Boolean {
        val c = favoriteDatabase?.favoriteEntryDao?.countWord(word)
        return c != null && c > 0
    }

    fun saveFavorite(word: String, favorite: Boolean): Boolean {
        if (favorite) {
            val rowId = favoriteDatabase?.favoriteEntryDao?.save(word)
            return rowId != null && rowId > 0
        } else {
            val c = favoriteDatabase?.favoriteEntryDao?.remove(word)
            return c != null && c > 0
        }
    }

    fun getAllFavorite(): List<FavoriteEntry>? {
        return favoriteDatabase?.favoriteEntryDao?.getAll()
    }

    fun initWordList(appContext: Context) {
        if(mainDirPath == null || !mainDirPath!!.exists()) return

        val dir = File(mainDirPath, wordListDir)
        if(!dir.exists() || !dir.isDirectory) return

        val files = dir.listFiles { _, name -> name.lowercase().endsWith(".txt") }
        wordListRepo = files.map { file ->
            readWordListFile(file)
        }.toMutableList()
    }

    private fun readWordListFile(file: File): WordList {
        var lines = file.readLines().toMutableList()
        var title: String = file.nameWithoutExtension

        if (lines.size > 1) {
            val titleLine = lines.first().trim()
            if (titleLine.startsWith("#")) {
                title = titleLine.substring(1).trim()
                lines.removeFirst()
            }

            for (i in 0 until lines.size) {
                lines[i] = lines[i].trim()
            }
        }

        return WordList(file, lines, title)
    }
}
