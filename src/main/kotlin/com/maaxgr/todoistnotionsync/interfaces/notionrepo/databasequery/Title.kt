package com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery

data class Title(
    val annotations: Annotations,
    val href: Any,
    val plain_text: String,
    val text: Text,
    val type: String
)
