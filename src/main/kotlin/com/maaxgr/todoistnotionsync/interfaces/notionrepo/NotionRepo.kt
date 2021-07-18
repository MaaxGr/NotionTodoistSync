package com.maaxgr.todoistnotionsync.interfaces.notionrepo

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.DatabaseQuery
import org.jraf.klibnotion.model.base.UuidString
import org.jraf.klibnotion.model.date.Timestamp

interface NotionRepo {
    fun getDatabaseEntries(queryBody: String): DatabaseQuery


    suspend fun update(pageId: UuidString, title: String): Timestamp
}
