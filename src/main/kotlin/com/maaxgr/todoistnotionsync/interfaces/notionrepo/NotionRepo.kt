package com.maaxgr.todoistnotionsync.interfaces.notionrepo

import org.jraf.klibnotion.model.base.UuidString
import org.jraf.klibnotion.model.date.Date
import org.jraf.klibnotion.model.date.Timestamp
import org.jraf.klibnotion.model.page.Page

interface NotionRepo {
    data class Entry(
        val pageId: UuidString,
        val lastEditedTime: Timestamp
    )

    suspend fun getDatabaseEntries(queryBody: String): List<Entry>

    suspend fun update(pageId: UuidString, title: String): Timestamp

    suspend fun check(pageId: UuidString): Timestamp

    suspend fun uncheck(pageId: UuidString): Timestamp
    suspend fun add(name: String): Page
}
