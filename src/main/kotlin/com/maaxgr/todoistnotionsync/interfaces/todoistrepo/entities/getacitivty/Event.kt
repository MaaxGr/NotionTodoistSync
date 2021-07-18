package com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.getacitivty

data class Event(
    val event_date: String,
    val event_type: String,
    val extra_data: ExtraData,
    val id: Long,
    val initiator_id: Any,
    val object_id: Long,
    val object_type: String,
    val parent_item_id: Any,
    val parent_project_id: Long
)