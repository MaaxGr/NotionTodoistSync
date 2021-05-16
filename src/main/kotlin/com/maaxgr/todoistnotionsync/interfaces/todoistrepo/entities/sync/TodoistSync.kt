package com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.sync

data class TodoistSync(
    val full_sync: Boolean,
    val items: List<Item>,
    val sync_token: String,
    val temp_id_mapping: TempIdMapping
)