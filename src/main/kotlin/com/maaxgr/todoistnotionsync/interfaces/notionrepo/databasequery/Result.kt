package com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery

data class Result(
    val archived: Boolean,
    val created_time: String,
    val id: String,
    val last_edited_time: String,
    val `object`: String,
    val parent: Parent,
    val properties: Properties
)
