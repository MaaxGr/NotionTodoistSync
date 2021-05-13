package com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery

data class Annotations(
    val bold: Boolean,
    val code: Boolean,
    val color: String,
    val italic: Boolean,
    val strikethrough: Boolean,
    val underline: Boolean
)
