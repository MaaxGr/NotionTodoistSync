package com.maaxgr.todoistnotionsync

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.TodoistRepo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncApplication: KoinComponent {

    private val notionRepo: NotionRepo by inject()
    private val todoistRepo: TodoistRepo by inject()

    init {
        //TODO: cursor
        val queryBody = """
            {
                "filter": {
                    "property": "Sync",
                    "checkbox": {
                        "equals": true
                    }
                }
            }
        """.trimIndent()

        val notionEntries = notionRepo.getDatabaseEntries("9123f835-37e4-4c5e-afeb-3c8fb3655f44", queryBody).results
        val todoistEntries = todoistRepo.getTodoistUpdates().items

        val notionIdMapping = notionEntries.associateBy { it.id }
        val todoistMappingByNotionId = todoistEntries.associateBy { it.description }

        notionEntries.forEach { notionEntry ->
            val notionId = notionEntry.id
            val notionTaskName = notionEntry.properties.Name.title.first().plain_text

            val todoistEntry = todoistMappingByNotionId[notionId]


            if (todoistEntry == null) {
                println("Create task in todoist: $notionTaskName")
                todoistRepo.createEntry(notionTaskName, notionId)
            } else {
                if (notionTaskName != todoistEntry.content) {
                    println("Update task in todoist: $notionTaskName")
                    todoistRepo.updateContent(todoistEntry.id, notionTaskName)
                }

            }

        }

    }

}
