package com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.sync

data class Item(
    val added_by_uid: Int,
    val assigned_by_uid: Any,
    val checked: Int,
    val child_order: Int,
    val collapsed: Int,
    val content: String,
    val date_added: String,
    val date_completed: Any,
    val day_order: Int,
    val description: String,
    val due: Any,
    val id: Long,
    val in_history: Int,
    val is_deleted: Int,
    val labels: List<Any>,
    val parent_id: Any,
    val priority: Int,
    val project_id: Long,
    val responsible_uid: Any,
    val section_id: Any,
    val sync_id: Any,
    val user_id: Int
)