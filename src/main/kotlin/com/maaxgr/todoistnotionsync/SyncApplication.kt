package com.maaxgr.todoistnotionsync

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.Result
import com.maaxgr.todoistnotionsync.interfaces.synctable.SyncTable
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.TodoistRepo
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.sync.Item
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*

class SyncApplication : KoinComponent {

    private val notionRepo: NotionRepo by inject()
    private val todoistRepo: TodoistRepo by inject()

    private val syncTable: SyncTable by inject()

    private val tableToSync = "9123f835-37e4-4c5e-afeb-3c8fb3655f44"

    suspend fun init() {
        syncTable.reloadSyncTable()

        //val syncTable = notionRepo.getSyncTable()
        val notionEntries = notionRepo.getDatabaseEntries(tableToSync, "").results
        val todoistEntries = todoistRepo.getTodoistEntries()

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

            val text = notionEntry.properties.Name.title.firstOrNull()?.plain_text ?: "null"

            val todoistAddResponse = todoistRepo.createEntry(text)

            val todoistLastUpdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").apply { timeZone = TimeZone.getTimeZone("GMT") }.parse(todoistAddResponse.created)
            val notionLastUpdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").apply { timeZone = TimeZone.getTimeZone("GMT") }.parse(notionEntry.last_edited_time)

            if (syncEntry == null) {
                syncTable.addSyncEntry(
                    SyncTable.AddSyncTableEntry(
                        notionId = notionEntry.id,
                        todoistId = todoistAddResponse.id,
                        todoistLastUpdate = todoistLastUpdate,
                        notionLastUpdate = notionLastUpdate
                    )
                )
            } else {

                val todoistEntry = todoistIdMapping[syncEntry.todoistId]

                if (todoistEntry == null) {
                    syncTable.addSyncEntry(
                        SyncTable.AddSyncTableEntry(
                            notionId = notionEntry.id,
                            todoistId = todoistAddResponse.id,
                            todoistLastUpdate = todoistLastUpdate,
                            notionLastUpdate = notionLastUpdate
                        )
                    )
                } else {

                    if (syncEntry.todoistLastUpdate == null || notionLastUpdate.time > syncEntry.todoistLastUpdate!!.time) {
                        val updatedEntry = todoistRepo.updateContent(syncEntry.todoistId, text)
                        //val updatedTimestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").apply { timeZone = TimeZone.getTimeZone("GMT") }.parse(updatedEntry)

                        syncTable.updateSyncEntry(
                            syncEntry.copy()
                        )
                    }

                }



            }
        }


    }

}
