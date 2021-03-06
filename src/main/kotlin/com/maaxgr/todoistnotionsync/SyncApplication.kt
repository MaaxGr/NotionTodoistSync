package com.maaxgr.todoistnotionsync

import com.maaxgr.todoistnotionsync.interfaces.notionconfigdatabase.NotionConfigDatabase
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.Result
import com.maaxgr.todoistnotionsync.interfaces.sync.integrator.AddedTodoistItemIntegrator
import com.maaxgr.todoistnotionsync.interfaces.sync.integrator.CompletedTodoistItemIntegrator
import com.maaxgr.todoistnotionsync.interfaces.sync.integrator.UncompletedTodoistItemIntegrator
import com.maaxgr.todoistnotionsync.interfaces.sync.integrator.UpdatedTodoistItemIntegrator
import com.maaxgr.todoistnotionsync.interfaces.synctable.SyncTable
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.TodoistRepo
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.getacitivty.Event
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.sync.Item
import com.maaxgr.todoistnotionsync.todoistmanager.TodoistManager
import org.jraf.klibnotion.model.page.Page
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*

class SyncApplication : KoinComponent {

    private val notionRepo: NotionRepo by inject()
    private val todoistRepo: TodoistRepo by inject()

    private val syncTable: SyncTable by inject()

    private val notionConfigDatabase: NotionConfigDatabase by inject()

    private val todoistManager = TodoistManager()

    private var notionEntries = listOf<NotionRepo.Entry>()

    suspend fun init() {
        syncTable.reloadSyncTable()

        //val syncTable = notionRepo.getSyncTable()
        notionEntries = notionRepo.getDatabaseEntries("")
//        val todoistEntries = todoistRepo.getTodoistEntries()

        integrateTodoistUpdates()

        //syncEntries(notionEntries, todoistEntries)

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

    private suspend fun integrateTodoistUpdates() {
        val updatesToIntegrate = getTodoistUpdatesToIntegrate()
        println("Updates to integrate: ${updatesToIntegrate.size}")

        for (update in updatesToIntegrate) {
            integrateTodoistUpdate(update)
        }

        if (updatesToIntegrate.isNotEmpty()) {
            notionConfigDatabase.setValue("todoist_last_activity_id", updatesToIntegrate.maxOf { it.id }.toString())
        }
    }

    private suspend fun integrateTodoistUpdate(update: Event) {
        if (update.object_type != "item") {
            println("Ignore update ${update.id} because eventtype ${update.object_type}")
            return
        }

        when (update.event_type) {
            "updated" -> UpdatedTodoistItemIntegrator(update, notionEntries).integrate()
            "completed" -> CompletedTodoistItemIntegrator(update, notionEntries).integrate()
            "uncompleted" -> UncompletedTodoistItemIntegrator(update, notionEntries).integrate()
            "added" -> AddedTodoistItemIntegrator(update, notionEntries).integrate()
            else -> println("No integration available for event type '${update.event_type}'")
        }
    }

    private suspend fun getTodoistUpdatesToIntegrate(): List<Event> {
        val todoistLastActivityId = notionConfigDatabase.getValue("todoist_last_activity_id")?.toLongOrNull() ?: 0L
        return todoistManager.getUpdatesToProcess(todoistLastActivityId)
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
