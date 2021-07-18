package com.maaxgr.todoistnotionsync.interfaces.notionconfigdatabase

import com.maaxgr.todoistnotionsync.interfaces.config.ConfigNotion
import org.jraf.klibnotion.client.NotionClient
import org.jraf.klibnotion.model.base.UuidString
import org.jraf.klibnotion.model.base.reference.DatabaseReference
import org.jraf.klibnotion.model.database.query.DatabaseQuery
import org.jraf.klibnotion.model.database.query.filter.DatabaseQueryPredicate
import org.jraf.klibnotion.model.database.query.filter.DatabaseQueryPropertyFilter
import org.jraf.klibnotion.model.property.value.PropertyValueList
import org.jraf.klibnotion.model.property.value.RichTextPropertyValue
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import javax.xml.crypto.Data

class NotionConfigDatabaseImpl : NotionConfigDatabase, KoinComponent {

    private val notionConfig: ConfigNotion by inject()
    private val databaseId: UuidString = "046d1c48-0347-41b8-a503-ad2d2fb4c976"
    private val notionClient: NotionClient by inject()

    override suspend fun getValue(key: String): String? {

        val filter = DatabaseQueryPropertyFilter.Title(
            propertyIdOrName = "config_key",
            predicate = DatabaseQueryPredicate.Text.Equals(key)
        )

        val results = notionClient.databases.queryDatabase(databaseId, DatabaseQuery().any(filter)).results
        if (results.isEmpty()) {
            return null
        }

        val valueColumn = results.first().propertyValues.first { it.name == "config_value" } as RichTextPropertyValue
        return valueColumn.value.plainText
    }

    private suspend fun getPageId(key: String): UuidString? {
        val filter = DatabaseQueryPropertyFilter.Title(
            propertyIdOrName = "config_key",
            predicate = DatabaseQueryPredicate.Text.Equals(key)
        )

        val results = notionClient.databases.queryDatabase(databaseId, DatabaseQuery().any(filter)).results
        if (results.isEmpty()) {
            return null
        }

        return results.first().id
    }

    override suspend fun setValue(key: String, value: String) {
        val pageId = getPageId(key)

        if (pageId == null) {
            notionClient.pages.createPage(
                parentDatabase = DatabaseReference(databaseId),
                properties = PropertyValueList()
                    .title("config_key", key)
                    .text("config_value", value)
            )
        } else {
            notionClient.pages.updatePage(pageId, PropertyValueList().text("config_value", value))
        }
    }

}