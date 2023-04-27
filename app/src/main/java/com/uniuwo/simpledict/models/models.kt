package com.uniuwo.simpledict.models

import com.uniuwo.simpledict.databus.DictEntry
import com.uniuwo.simpledict.databus.SimpleDictDatabase

data class WordEntry(val db: SimpleDictDatabase, val entry: DictEntry)