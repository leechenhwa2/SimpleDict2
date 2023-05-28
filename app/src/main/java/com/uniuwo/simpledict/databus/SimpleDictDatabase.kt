package com.uniuwo.simpledict.databus

import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import androidx.core.database.getStringOrNull


data class DictEntry(
    val id: Long,

    val word: String,
    val content: String,

    val word1: String,
    val word2: String,
)

data class InfoEntry(
    val id: Long,

    val word: String,
    val content: String,

    val word1: String,
)


// Table contents are grouped together in an anonymous object.
object DictColumns : BaseColumns {
    const val TABLE_NAME = "dict"

    const val COLUMN_NAME_ID = "id"
    const val COLUMN_NAME_WORD = "word"
    const val COLUMN_NAME_CONTENT = "content"

    const val COLUMN_NAME_WORD1 = "word1"
    const val COLUMN_NAME_WORD2 = "word2"
}

object InfoColumns : BaseColumns {
    const val TABLE_NAME = "info"

    const val COLUMN_NAME_ID = "id"
    const val COLUMN_NAME_WORD = "word"
    const val COLUMN_NAME_CONTENT = "content"

    const val COLUMN_NAME_WORD1 = "word1"
}


class DictEntryDao(val dbHelper: SimpleDictDatabase) {
    val db: SQLiteDatabase = dbHelper.readableDatabase

    //    @Query("SELECT id,word,content,word1,word2 FROM dict")
    fun getAll(): List<DictEntry> {
        val sql = "SELECT id,word,content FROM dict"
        val cursor = db.rawQuery(sql, null)

        val items = mutableListOf<DictEntry>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_ID))
                val word = getString(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_WORD))
                val content =
                    getStringOrNull(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_CONTENT)) ?: ""
                items.add(DictEntry(itemId, word, content, "", ""))
            }
        }
        cursor.close()

        return items
    }

    //    @Query("SELECT id,word,content,word1,word2 FROM dict WHERE id IN (:ids)")
    fun findAllByIds(ids: LongArray): List<DictEntry> {
        val sql = "SELECT id,word,content FROM dict WHERE id IN (${ids.joinToString(",")})"

        val cursor = db.rawQuery(sql, null)

        val items = mutableListOf<DictEntry>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_ID))
                val word = getString(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_WORD))
                val content =
                    getStringOrNull(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_CONTENT)) ?: ""
                items.add(DictEntry(itemId, word, content, "", ""))
            }
        }
        cursor.close()

        return items
    }

    //    @Query("SELECT id,word,content,word1,word2 FROM dict WHERE word LIKE :word" + " ORDER BY id,word ASC LIMIT 10")
    fun findByWord(word: String): List<DictEntry> {
        val sql =
            "SELECT id,word,content FROM dict WHERE word LIKE ? " + " ORDER BY id,word ASC LIMIT 10"

        val cursor = db.rawQuery(sql, arrayOf(word))

        val items = mutableListOf<DictEntry>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_ID))
                val word = getString(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_WORD))
                val content =
                    getStringOrNull(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_CONTENT)) ?: ""
                items.add(DictEntry(itemId, word, content, "", ""))
            }
        }
        cursor.close()

        return items
    }

    fun searchTop5ByWord(word: String): List<String> {
        val sql =
            "SELECT word FROM dict WHERE word LIKE ? " + " ORDER BY id,word ASC LIMIT 5"

        val cursor = db.rawQuery(sql, arrayOf("$word%"))

        val items = mutableListOf<String>()
        with(cursor) {
            while (moveToNext()) {
                val result = getString(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_WORD))
                items.add(result)
            }
        }
        cursor.close()

        return items
    }


    //    @Query("SELECT id,word,content,word1,word2 FROM dict " + " ORDER BY id DESC LIMIT 1")
    fun lastWord(): DictEntry? {
        val sql = "SELECT id,word,content FROM dict " + " ORDER BY id DESC LIMIT 1"
        val cursor = db.rawQuery(sql, null)

        var item: DictEntry? = null
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_ID))
                val word = getString(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_WORD))
                val content =
                    getStringOrNull(getColumnIndexOrThrow(DictColumns.COLUMN_NAME_CONTENT)) ?: ""
                item = DictEntry(itemId, word, content, "", "")
                break
            }
        }
        cursor.close()

        return item
    }

    //    @Query("SELECT count(0) FROM dict")
    fun count(): Long {
        val sql = "SELECT count(0) FROM dict"
        val item = DatabaseUtils.longForQuery(db, sql, null)
        return item
    }

}


class InfoEntryDao(val dbHelper: SimpleDictDatabase) {
    val db: SQLiteDatabase = dbHelper.readableDatabase

    //    @Query("SELECT id,word,content,word1 FROM info")
    fun getAll(): List<InfoEntry> {
        val sql = "SELECT id,word,content FROM info"
        val cursor = db.rawQuery(sql, null)

        val items = mutableListOf<InfoEntry>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(InfoColumns.COLUMN_NAME_ID))
                val word = getString(getColumnIndexOrThrow(InfoColumns.COLUMN_NAME_WORD))
                val content =
                    getStringOrNull(getColumnIndexOrThrow(InfoColumns.COLUMN_NAME_CONTENT)) ?: ""
                items.add(InfoEntry(itemId, word, content, ""))
            }
        }
        cursor.close()

        return items
    }

    //    @Query("SELECT id,word,content,word1 FROM info WHERE id IN (:ids)")
    fun findAllByIds(ids: LongArray): List<InfoEntry> {
        val sql = "SELECT id,word,content FROM info WHERE id IN (${ids.joinToString(",")})"

        val cursor = db.rawQuery(sql, null)

        val items = mutableListOf<InfoEntry>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(InfoColumns.COLUMN_NAME_ID))
                val word = getString(getColumnIndexOrThrow(InfoColumns.COLUMN_NAME_WORD))
                val content =
                    getStringOrNull(getColumnIndexOrThrow(InfoColumns.COLUMN_NAME_CONTENT)) ?: ""
                items.add(InfoEntry(itemId, word, content, ""))
            }
        }
        cursor.close()

        return items
    }

    //    @Query("SELECT id,word,content,word1 FROM info WHERE word LIKE :word" + " ORDER BY id,word ASC LIMIT 10")
    fun findByWord(word: String): List<InfoEntry> {
        val sql =
            "SELECT id,word,content FROM info WHERE word LIKE ? " + " ORDER BY id,word ASC LIMIT 10"

        val cursor = db.rawQuery(sql, arrayOf(word))

        val items = mutableListOf<InfoEntry>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(InfoColumns.COLUMN_NAME_ID))
                val word = getString(getColumnIndexOrThrow(InfoColumns.COLUMN_NAME_WORD))
                val content =
                    getStringOrNull(getColumnIndexOrThrow(InfoColumns.COLUMN_NAME_CONTENT)) ?: ""
                items.add(InfoEntry(itemId, word, content, ""))
            }
        }
        cursor.close()

        return items
    }

}


class SimpleDictDatabase(
    val appContext: Context,
    var dbPath: String,
    version: Int = 1
) : SQLiteOpenHelper(appContext, dbPath, null, version) {

    var dictEntryDao: DictEntryDao = DictEntryDao(this)
    var infoEntryDao: InfoEntryDao = InfoEntryDao(this)

    var title: String = "SimpleDictDatabase"

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    override fun toString(): String {
        return if (dbPath.isEmpty()) super.toString() else dbPath
    }
}
