package com.maaxgr.todoistnotionsync.interfaces.sync.integrator

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.Result
import com.maaxgr.todoistnotionsync.interfaces.sync.integrator.helper.TodoistToNotionUpdateHelper
import com.maaxgr.todoistnotionsync.interfaces.synctable.SyncTable
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.getacitivty.Event
import com.maaxgr.todoistnotionsync.utils.DATE_FORMAT_ISO8601_WITHMS_AND_WITHTS
import com.maaxgr.todoistnotionsync.utils.DATE_FORMAT_ISO8601_WITHOUTMS_AND_WITHTS
import com.maaxgr.todoistnotionsync.utils.asDate
import org.jraf.klibnotion.model.page.Page
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class UpdatedTodoistItemIntegrator(
    private val update: Event,
    private val notionEntries: List<NotionRepo.Entry>
) : Integrator, KoinComponent {

    private val notionRepo: NotionRepo by inject()
    private val syncTableService: SyncTable by inject()

    private val helper = TodoistToNotionUpdateHelper(
        notionEntries = notionEntries,
        todoistEvent = update,
        log = ::log,
        notionUpdate = { notionRepo.update(it.pageId, it.name) }
    )

    override val integratorName: String
        get() = "UPDATE_TODOIST_INTEGRATOR"

    override suspend fun integrate() {
        helper.check()
    }

}