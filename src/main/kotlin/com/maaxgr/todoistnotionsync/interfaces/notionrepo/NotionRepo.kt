package com.maaxgr.todoistnotionsync.interfaces.notionrepo

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.DatabaseQuery

interface NotionRepo {
    fun getDatabaseEntries(): DatabaseQuery
}
