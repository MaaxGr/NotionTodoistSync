package com.maaxgr.todoistnotionsync.interfaces.notionrepo

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.result.Result
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigNotion
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.DatabaseQuery
import org.jraf.klibnotion.client.NotionClient
import org.jraf.klibnotion.model.base.UuidString
import org.jraf.klibnotion.model.base.reference.DatabaseReference
import org.jraf.klibnotion.model.date.Date
import org.jraf.klibnotion.model.date.Timestamp
import org.jraf.klibnotion.model.page.Page
import org.jraf.klibnotion.model.pagination.ResultPage
import org.jraf.klibnotion.model.property.value.PropertyValueList
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotionRepoImpl : NotionRepo, KoinComponent {

    private val notionConfig: ConfigNotion by inject()
    private val notionClient: NotionClient by inject()

    private val tableToSync: UuidString = "9123f835-37e4-4c5e-afeb-3c8fb3655f44"


    fun load() {

    }


    override suspend fun getDatabaseEntries(queryBody: String): List<NotionRepo.Entry> {
        return notionClient.databases.queryDatabase(tableToSync).results.map {
            NotionRepo.Entry(
                pageId = it.id,
                lastEditedTime = it.lastEdited
            )
        }
    }

    override suspend fun update(pageId: UuidString, title: String): Timestamp {
        val response = notionClient.pages.updatePage(pageId, PropertyValueList()
            .title("Name", title))

        return response.lastEdited
    }

    override suspend fun check(pageId: UuidString): Timestamp {
        val response = notionClient.pages.updatePage(pageId, PropertyValueList()
            .checkbox("Done", true))

        return response.lastEdited
    }

    override suspend fun uncheck(pageId: UuidString): Timestamp {
        val response = notionClient.pages.updatePage(pageId, PropertyValueList()
            .checkbox("Done", false))

        return response.lastEdited
    }

    override suspend fun add(name: String): Page {
        return notionClient.pages.createPage(
            DatabaseReference(tableToSync), PropertyValueList()
            .title("Name", name))
    }


}
