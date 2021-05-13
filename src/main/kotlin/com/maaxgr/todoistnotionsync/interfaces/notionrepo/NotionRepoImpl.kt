package com.maaxgr.todoistnotionsync.interfaces.notionrepo

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.result.Result
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigNotion
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.databasequery.DatabaseQuery
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotionRepoImpl : NotionRepo, KoinComponent {

    private val notionConfig: ConfigNotion by inject()

    override fun getDatabaseEntries(): DatabaseQuery {
        val queryBody = """
            {
                "filter": {
                    "property": "Sync",
                    "checkbox": {
                        "equals": true
                    }
                }
            }
        """.trimIndent()

        val (request, response, result) = Fuel.post("https://api.notion.com/v1/databases/9123f835-37e4-4c5e-afeb-3c8fb3655f44/query")
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
