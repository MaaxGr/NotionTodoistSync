package com.maaxgr.todoistnotionsync.interfaces.synctable

import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import org.jraf.klibnotion.client.NotionClient
import org.jraf.klibnotion.model.base.UuidString
import org.jraf.klibnotion.model.base.reference.DatabaseReference
import org.jraf.klibnotion.model.date.Date
import org.jraf.klibnotion.model.date.DateOrDateRange
import org.jraf.klibnotion.model.date.DateTime
import org.jraf.klibnotion.model.date.Timestamp
import org.jraf.klibnotion.model.property.value.DatePropertyValue
import org.jraf.klibnotion.model.property.value.PropertyValueList
import org.jraf.klibnotion.model.property.value.RichTextPropertyValue
import org.jraf.klibnotion.model.property.value.TitlePropertyValue
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*

class SyncTableImpl : SyncTable, KoinComponent {

    private val notionRepo: NotionRepo by inject()
    private val syncDatabaseId: UuidString = "2c3f4342-89be-4e88-a6d0-a76efe31782a"
    private val notionClient: NotionClient by inject()

    private val syncTable: MutableList<SyncTable.SyncTableEntry> = mutableListOf()

    override suspend fun reloadSyncTable() {
        syncTable.clear()

        val result = notionClient.databases.queryDatabase(syncDatabaseId)

        val list = result.results.map { page ->
            val props = page.propertyValues

            val notionIdProperty = props.first { it.name == "notion_id" } as TitlePropertyValue
            val todoistProperty = props.first { it.name == "todoist_id" } as RichTextPropertyValue
            val todoistUpdateTimeProperty = props.firstOrNull { it.name == "todoist_update_time" } as DatePropertyValue?
            val lastUpdateDate = todoistUpdateTimeProperty?.value?.start?.timestamp

            SyncTable.SyncTableEntry(
                pageId = page.id,
                notionId = notionIdProperty.value.plainText ?: "",
                todoistId = todoistProperty.value.plainText ?: "",
                todoistLastUpdate = lastUpdateDate
            )
        }

        syncTable.addAll(list)
    }

    override suspend fun getSyncTable(): List<SyncTable.SyncTableEntry> {
        return syncTable
    }

    override suspend fun addSyncEntry(values: SyncTable.AddSyncTableEntry) {
        val createdPage = notionClient.pages.createPage(
            parentDatabase = DatabaseReference(syncDatabaseId),
            properties = PropertyValueList()
                .title("notion_id", values.notionId)
                .text("todoist_id", values.todoistId)
                .date("todoist_update_time", DateOrDateRange(DateTime(Timestamp(values.todoistLastUpdate!!.time)), null))
        )

        syncTable.add(
            SyncTable.SyncTableEntry(
                pageId = createdPage.id,
                notionId = values.notionId,
                todoistId = values.todoistId,
                todoistLastUpdate = null
            )
        )
    }

    override suspend fun deleteSyncEntry(pageId: String) {
        notionClient.pages.archivePage(pageId)
        syncTable.removeIf { it.pageId == pageId }
    }

    override suspend fun updateSyncEntry(values: SyncTable.SyncTableEntry) {
        val propertyList = PropertyValueList()
            .title("notion_id", values.notionId)
            .text("todoist_id", values.todoistId)

        println(TimeZone.getDefault())

        println(SimpleDateFormat().format(Date(System.currentTimeMillis())))


        values.todoistLastUpdate?.let {
            propertyList.date("todoist_update_time", DateOrDateRange(DateTime(Timestamp(System.currentTimeMillis())), null))
        }

        notionClient.pages.updatePage(
            id = values.pageId,
            properties = propertyList
        )

        syncTable.first { it.pageId == values.pageId }.apply {
            notionId = values.notionId
            todoistId = values.todoistId
        }
    }

}