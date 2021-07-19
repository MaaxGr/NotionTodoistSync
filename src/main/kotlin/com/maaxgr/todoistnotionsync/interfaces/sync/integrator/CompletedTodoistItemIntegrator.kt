package com.maaxgr.todoistnotionsync.interfaces.sync.integrator

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.Result
import com.maaxgr.todoistnotionsync.interfaces.sync.integrator.helper.TodoistToNotionUpdateHelper
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.getacitivty.Event
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CompletedTodoistItemIntegrator(
    private val update: Event,
    private val notionEntries: List<NotionRepo.Entry>
) : Integrator, KoinComponent {

    private val notionRepo: NotionRepo by inject()

    override val integratorName: String
        get() = "COMPLETED_TODOIST_INTEGRATOR"

    private val helper = TodoistToNotionUpdateHelper(
        notionEntries = notionEntries,
        todoistEvent = update,
        log = ::log,
        notionUpdate = { notionRepo.check(it.pageId) }
    )

    override suspend fun integrate() {
        helper.check()
    }

}