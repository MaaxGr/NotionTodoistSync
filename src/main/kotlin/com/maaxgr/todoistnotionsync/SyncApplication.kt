package com.maaxgr.todoistnotionsync

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncApplication: KoinComponent {

    private val notionRepo: NotionRepo by inject()

    init {

        val query = notionRepo.getDatabaseEntries()

        query.results.forEach {
            println("${it.properties.Name.title.first().plain_text} => Done? ${it.properties.Done.checkbox}")
        }
    }

}
