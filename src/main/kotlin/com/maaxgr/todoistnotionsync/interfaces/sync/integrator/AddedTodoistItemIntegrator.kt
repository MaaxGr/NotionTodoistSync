package com.maaxgr.todoistnotionsync.interfaces.sync.integrator

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.Result
import com.maaxgr.todoistnotionsync.interfaces.sync.integrator.helper.TodoistToNotionUpdateHelper
import com.maaxgr.todoistnotionsync.interfaces.synctable.SyncTable
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.getacitivty.Event
import com.maaxgr.todoistnotionsync.utils.DATE_FORMAT_ISO8601_WITHMS_AND_WITHTS
import com.maaxgr.todoistnotionsync.utils.DATE_FORMAT_ISO8601_WITHOUTMS_AND_WITHTS
import com.maaxgr.todoistnotionsync.utils.asDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class AddedTodoistItemIntegrator(
    private val event: Event,
    private val notionEntries: List<NotionRepo.Entry>
) : Integrator, KoinComponent {

    private val notionRepo: NotionRepo by inject()
    private val syncTableService: SyncTable by inject()

    override val integratorName: String
        get() = "UPDATE_TODOIST_INTEGRATOR"

    override suspend fun integrate() {
        val eventDate = event.event_date.asDate(DATE_FORMAT_ISO8601_WITHOUTMS_AND_WITHTS)
        val newPage = notionRepo.add(event.extra_data.content)

        syncTableService.addSyncEntry(
            SyncTable.AddSyncTableEntry(
            notionId = newPage.id,
                todoistId = event.object_id,
                todoistLastUpdate = eventDate,
                notionLastUpdate = eventDate,
        ))

    }

}