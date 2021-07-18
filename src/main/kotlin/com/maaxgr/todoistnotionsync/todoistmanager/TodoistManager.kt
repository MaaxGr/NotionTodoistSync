package com.maaxgr.todoistnotionsync.todoistmanager

import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.TodoistRepo
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.getacitivty.Event
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodoistManager: KoinComponent {

    private val todoistRepo: TodoistRepo by inject()

    fun getUpdatesToProcess(lastId: Long): List<Event> {
        val updates: MutableList<Event> = mutableListOf()
        var pageId = 1
        while (true) {
            val moreUpdates = todoistRepo.readActivity(10, (pageId - 1) * 10)

            if (moreUpdates.isEmpty()) {
                return updates
            }

            val moreMinID = moreUpdates.minOf { it.id }

            updates.addAll(moreUpdates)

            if (moreMinID <= lastId) {
                return updates.filter { it.id > lastId }.sortedBy { it.id }
            }

            pageId++
        }
    }



}