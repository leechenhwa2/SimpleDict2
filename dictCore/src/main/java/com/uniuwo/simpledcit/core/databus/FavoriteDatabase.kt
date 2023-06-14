package com.uniuwo.simpledcit.core.databus

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

data class FavoriteEntry(
    val id: Long,
    val word: String,
)

object FavoriteColumns : BaseColumns {
    const val TABLE_NAME = "favorite"

    const val COLUMN_NAME_ID = "id"
    const val COLUMN_NAME_WORD = "word"
}

class FavoriteEntryDao(val dbHelper: FavoriteDatabase) {
    val db = dbHelper.writableDatabase

    //    @Query("SELECT id,word FROM favorite")
    fun getAll(): List<FavoriteEntry> {
        val sql = "SELECT id,word FROM favorite ORDER BY id DESC"

        val cursor = db.rawQuery(sql, null)

        val items = mutableListOf<FavoriteEntry>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(FavoriteColumns.COLUMN_NAME_ID))
                val word = getString(getColumnIndexOrThrow(FavoriteColumns.COLUMN_NAME_WORD))
                items.add(FavoriteEntry(itemId, word))
            }
        }
        cursor.close()

        return items
    }

    //    @Query("SELECT id,word FROM favorite WHERE word LIKE :word" + " LIMIT 10")
    fun findByWord(word: String): List<FavoriteEntry> {
        val sql =
            "SELECT id,word FROM favorite WHERE word LIKE ? " + " ORDER BY id,word ASC LIMIT 10"

        val cursor = db.rawQuery(sql, arrayOf(word))

        val items = mutableListOf<FavoriteEntry>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(FavoriteColumns.COLUMN_NAME_ID))
                val word = getString(getColumnIndexOrThrow(FavoriteColumns.COLUMN_NAME_WORD))
                items.add(FavoriteEntry(itemId, word))
            }
        }
        cursor.close()

        return items
    }

    fun countWord(word: String): Long {
        val sql = "SELECT count(0) FROM favorite WHERE word LIKE ? "
        val item = DatabaseUtils.longForQuery(db, sql, arrayOf(word))
        return item
    }

    //    @Query("SELECT count(0) FROM favorite")
    fun count(): Long {
        val sql = "SELECT count(0) FROM favorite"
        val item = DatabaseUtils.longForQuery(db, sql, null)
        return item
    }

    fun save(word: String): Long {
        val values = ContentValues().apply {
            put("word", word)
        }

        // Insert the new row, returning the primary key value of the new row
        val rowId = db.insert(FavoriteColumns.TABLE_NAME, null, values)
        return rowId
    }

    fun remove(word: String): Int {
        val selection = "word LIKE ?"
        val selectionArgs = arrayOf(word)

        val deletedRows = db.delete(FavoriteColumns.TABLE_NAME, selection, selectionArgs)
        return deletedRows
    }

}

class FavoriteDatabase(
    val appContext: Context,
    var dbPath: String,
    version: Int = 1
) : SQLiteOpenHelper(appContext, dbPath, null, version) {

    val favoriteEntryDao: FavoriteEntryDao = FavoriteEntryDao(this)

    var title: String = "FavoriteDatabase"

    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE favorite (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT)"
        db?.execSQL(sql)
        val idxSql = "CREATE INDEX IDX_favorite_word ON favorite (word)"
        db?.execSQL(idxSql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //clean
        val idxSql = "DROP INDEX IF EXISTS IDX_favorite_word"
        db?.execSQL(idxSql)
        val sql = "DROP TABLE IF EXISTS favorite"
        db?.execSQL(sql)

        //recreate
        onCreate(db)
    }
}
