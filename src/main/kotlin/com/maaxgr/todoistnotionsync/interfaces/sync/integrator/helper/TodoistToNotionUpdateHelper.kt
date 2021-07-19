package com.maaxgr.todoistnotionsync.interfaces.sync.integrator.helper

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.Result
import com.maaxgr.todoistnotionsync.interfaces.synctable.SyncTable
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.getacitivty.Event
import com.maaxgr.todoistnotionsync.utils.DATE_FORMAT_ISO8601_WITHMS_AND_WITHTS
import com.maaxgr.todoistnotionsync.utils.DATE_FORMAT_ISO8601_WITHOUTMS_AND_WITHTS
import com.maaxgr.todoistnotionsync.utils.asDate
import org.jraf.klibnotion.model.base.UuidString
import org.jraf.klibnotion.model.page.Page
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.sql.Timestamp
import java.util.*

class TodoistToNotionUpdateHelper(
    private val notionEntries: List<NotionRepo.Entry>,
    private val todoistEvent: Event,
    private val log: (String) -> Unit,
    private val notionUpdate: suspend (payload: UpdatePayload) -> Date
): KoinComponent {

    private val syncTableService: SyncTable by inject()
    private val notionRepo: NotionRepo by inject()

    suspend fun check() {
        log("Processing update event ${todoistEvent.id}")

        val todoistLastUpdate = todoistEvent.event_date.asDate(DATE_FORMAT_ISO8601_WITHOUTMS_AND_WITHTS)
        val notionSyncEntry = syncTableService.getSyncTable().firstOrNull { it.todoistId == todoistEvent.object_id }
        if (notionSyncEntry == null) {
            log("Can't update notion for ${todoistEvent.id}. No id in sync table")
            return
        }

        val entry = notionEntries.firstOrNull { it.pageId == notionSyncEntry.notionId }
        if (entry == null) {
            log("Can't update notion for ${todoistEvent.id}. No entry in data table")
            return
        }

        val notionLastUpdate = Date(entry.lastEditedTime.time)

        if (todoistLastUpdate.time > notionLastUpdate.time) {
            val updatedTimestamp = notionUpdate(UpdatePayload(entry.pageId, todoistEvent.extra_data.content))
            log("Applied update ${todoistEvent.id}")

            syncTableService.updateSyncEntry(notionSyncEntry.copy(
                todoistLastUpdate = todoistLastUpdate,
                notionLastUpdate = Date(updatedTimestamp.time)
            ))
            log("Updated sync table for update ${todoistEvent.id}")

        } else {
            log("Don't update notion for ${todoistEvent.id}. Notion update date is newer")
        }
    }

    data class UpdatePayload(
        val pageId: UuidString,
        val name: String
    )

}