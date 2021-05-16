package com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.addtask

data class AddTaskResponse(
    val assigner: Int,
    val comment_count: Int,
    val completed: Boolean,
    val content: String,
    val created: String,
    val creator: Int,
    val description: String,
    val id: Long,
    val label_ids: List<Any>,
    val order: Int,
    val priority: Int,
    val project_id: Long,
    val section_id: Int,
    val url: String
)