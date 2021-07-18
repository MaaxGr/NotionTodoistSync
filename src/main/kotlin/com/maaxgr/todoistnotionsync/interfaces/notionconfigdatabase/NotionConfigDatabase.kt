package com.maaxgr.todoistnotionsync.interfaces.notionconfigdatabase

interface NotionConfigDatabase {
    suspend fun getValue(key: String): String?
    suspend fun setValue(key: String, value: String)
}