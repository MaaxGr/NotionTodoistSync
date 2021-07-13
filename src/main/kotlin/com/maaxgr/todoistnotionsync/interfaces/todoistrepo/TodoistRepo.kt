package com.maaxgr.todoistnotionsync.interfaces.todoistrepo

import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.addtask.AddTaskResponse
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.sync.TodoistSync

interface TodoistRepo {
    fun getTodoistUpdates(): TodoistSync
    fun setNotionEntryIdInTodoistEntry(todoistId: Long, notionId: String): Boolean
    fun createEntry(content: String): AddTaskResponse
    fun updateContent(todoistId: Long, content: String): Boolean
}
