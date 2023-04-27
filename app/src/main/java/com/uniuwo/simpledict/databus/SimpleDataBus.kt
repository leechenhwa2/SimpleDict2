package com.uniuwo.simpledict.databus

import android.content.Context
import com.uniuwo.simpledict.models.WordEntry
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

    var favoriteDatabase: FavoriteDatabase? = null

    var mainDirPath: File? = null

    fun initDatabases(appContext: Context) {
        initExternalDatabases(appContext)
        initFavoriteDatabase(appContext)
    }

    private fun initExternalDatabases(appContext: Context) {
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
        return simpleDatabases.flatMap { db ->
            db.dictEntryDao.findByWord(word).map { dictEntry -> WordEntry(db, dictEntry) }
        }
    }

    fun randomSimpleWords(count: Int): List<WordEntry> {
        val result = mutableListOf<WordEntry>()

        for (db in simpleDatabases) {
            val size = db.dictEntryDao.count()

            if (count > size) {
                val list = db.dictEntryDao.getAll()
                    .map { dictEntry -> WordEntry(db, dictEntry) }
                result.addAll(list)
                continue
            }

            val lastWord = db.dictEntryDao.lastWord()
            if (lastWord == null) continue

            val ids: List<Long> = (1..count).toList()
                .map { Random.nextLong(LongRange(100, lastWord.id)) }
                .sorted()

            val list = db.dictEntryDao.findAllByIds(ids.toLongArray())
                .map { dictEntry -> WordEntry(db, dictEntry) }
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
}
