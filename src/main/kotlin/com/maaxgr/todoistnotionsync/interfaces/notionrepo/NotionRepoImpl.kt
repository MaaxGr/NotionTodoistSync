package com.maaxgr.todoistnotionsync.interfaces.notionrepo

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.result.Result
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigNotion
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.DatabaseQuery
import org.jraf.klibnotion.client.Authentication
import org.jraf.klibnotion.client.ClientConfiguration
import org.jraf.klibnotion.client.NotionClient
import org.jraf.klibnotion.model.base.UuidString
import org.jraf.klibnotion.model.base.reference.DatabaseReference
import org.jraf.klibnotion.model.property.value.PropertyValueList
import org.jraf.klibnotion.model.property.value.RichTextPropertyValue
import org.jraf.klibnotion.model.property.value.TitlePropertyValue
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotionRepoImpl : NotionRepo, KoinComponent {

    private val notionConfig: ConfigNotion by inject()

    private val client = NotionClient.newInstance(ClientConfiguration(Authentication(notionConfig.token)))

    private val syncDatabaseId: UuidString = "2c3f4342-89be-4e88-a6d0-a76efe31782a"

    override suspend fun getSyncTable(): List<NotionRepo.SyncTableValues> {
        val result = client.databases.queryDatabase(syncDatabaseId)

        return result.results.map { page ->
            val notionIdProperty = page.propertyValues.first { it.name == "notion_id" } as TitlePropertyValue
            val todoistProperty = page.propertyValues.first { it.name == "todoist_id" } as RichTextPropertyValue

            NotionRepo.SyncTableValues(
                pageId = page.id,
                notionId = notionIdProperty.value.plainText ?: "",
                todoistId = todoistProperty.value.plainText ?: ""
            )
        }
    }

    suspend fun addToSyncTable(values: NotionRepo.AddSyncTableValues) {
        client.pages.createPage(
            parentDatabase = DatabaseReference(syncDatabaseId),
            properties = PropertyValueList()
                .title("notion_id", values.notionId)
                .text("todoist_id", values.todoistId)
        )
    }

    suspend fun updateTodoistId(values: NotionRepo.SyncTableValues) {
        client.pages.updatePage(
            id = values.pageId, PropertyValueList()
                .title("notion_id", values.notionId)
                .text("todoist_id", values.todoistId)
        )
    }

    suspend fun deleteSyncEntry(pageId: String) {
        client.pages.archivePage(pageId)
    }

    override fun getDatabaseEntries(databaseId: String, queryBody: String): DatabaseQuery {


        val (request, response, result) = Fuel.post("https://api.notion.com/v1/databases/$databaseId/query")
            .header("Notion-Version", "2021-05-13")
            .header("Authorization", "Bearer ${notionConfig.token}")
            .jsonBody(queryBody)
            .responseObject<DatabaseQuery>()

        when (result) {
            is Result.Success -> {
                return result.get()
            }
            is Result.Failure -> {
                throw result.error
            }
        }
    }

}
