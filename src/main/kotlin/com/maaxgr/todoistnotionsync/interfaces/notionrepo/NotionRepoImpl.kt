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
