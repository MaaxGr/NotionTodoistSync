package com.maaxgr.todoistnotionsync.interfaces.notionrepo

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.DatabaseQuery

interface NotionRepo {
    fun getDatabaseEntries(databaseId: String, queryBody: String): DatabaseQuery
    suspend fun getSyncTable(): List<SyncTableValues>

    data class SyncTableValues(
        val pageId: String,
        val notionId: String,
        val todoistId: String
    )

    data class AddSyncTableValues(
        val notionId: String,
        val todoistId: String
    )

}
