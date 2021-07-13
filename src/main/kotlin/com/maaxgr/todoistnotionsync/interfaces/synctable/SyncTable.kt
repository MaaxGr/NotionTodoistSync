package com.maaxgr.todoistnotionsync.interfaces.synctable

import java.util.*


interface SyncTable {


    data class SyncTableEntry(
        val pageId: String,
        var notionId: String,
        var todoistId: String,
        var todoistLastUpdate: Date?
    )

    data class AddSyncTableEntry(
        val notionId: String,
        val todoistId: String,
        var todoistLastUpdate: Date
    )

    suspend fun getSyncTable(): List<SyncTableEntry>
    suspend fun reloadSyncTable()
    suspend fun addSyncEntry(values: AddSyncTableEntry)
    suspend fun deleteSyncEntry(pageId: String)
    suspend fun updateSyncEntry(values: SyncTableEntry)
}