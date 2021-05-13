package com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery

data class Name(
    val id: String,
    val title: List<Title>,
    val type: String
)
