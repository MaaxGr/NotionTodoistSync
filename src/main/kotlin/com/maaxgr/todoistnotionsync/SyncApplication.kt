package com.maaxgr.todoistnotionsync

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.Result
import com.maaxgr.todoistnotionsync.interfaces.synctable.SyncTable
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.TodoistRepo
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.sync.Item
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat

class SyncApplication : KoinComponent {

    private val notionRepo: NotionRepo by inject()
    private val todoistRepo: TodoistRepo by inject()

    private val syncTable: SyncTable by inject()

    private val tableToSync = "9123f835-37e4-4c5e-afeb-3c8fb3655f44"

    suspend fun init() {
        syncTable.reloadSyncTable()

        //val syncTable = notionRepo.getSyncTable()
        val notionEntries = notionRepo.getDatabaseEntries(tableToSync, "").results
        val todoistEntries = todoistRepo.getTodoistUpdates().items

        syncEntries(notionEntries, todoistEntries)

//        val notionIdMapping = notionEntries.associateBy { it.id }
//        val todoistMappingByNotionId = todoistEntries.associateBy { it.description }
//
//        notionEntries.forEach { notionEntry ->
//            val notionId = notionEntry.id
//            val notionTaskName = notionEntry.properties.Name.title.first().plain_text
//
//            val todoistEntry = todoistMappingByNotionId[notionId]
//
//
//            if (todoistEntry == null) {
//                println("Create task in todoist: $notionTaskName")
//                todoistRepo.createEntry(notionTaskName)
//            } else {
//                if (notionTaskName != todoistEntry.content) {
//                    println("Update task in todoist: $notionTaskName")
//                    todoistRepo.updateContent(todoistEntry.id, notionTaskName)
//                }
//
//            }
//
//        }
    }

    private suspend fun syncEntries(notionEntries: List<Result>, todoistEntries: List<Item>) {

        val notionIdMapping = notionEntries.associateBy { it.id }
        val todoistIdMapping = todoistEntries.associateBy { it.id }

        val syncTableEntry = syncTable.getSyncTable()

        // iterate notion entries
        notionEntries.forEach { notionEntry ->

            val syncEntry = syncTableEntry.firstOrNull { it.notionId == notionEntry.id }

            if (syncEntry == null) {
                val todoistAddResponse = todoistRepo.createEntry(notionEntry.properties.Name.title.firstOrNull()?.plain_text ?: "null")

                val created = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(todoistAddResponse.created)

                syncTable.addSyncEntry(
                    SyncTable.AddSyncTableEntry(
                        notionId = notionEntry.id,
                        todoistId = todoistAddResponse.id.toString(),
                        todoistLastUpdate = created
                    )
                )

            }
        }


    }

}
