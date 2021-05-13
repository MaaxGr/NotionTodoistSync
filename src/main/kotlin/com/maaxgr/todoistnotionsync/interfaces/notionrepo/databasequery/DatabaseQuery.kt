package com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery

data class DatabaseQuery(
    val has_more: Boolean,
    val next_cursor: Any,
    val `object`: String,
    val results: List<Result>
)
