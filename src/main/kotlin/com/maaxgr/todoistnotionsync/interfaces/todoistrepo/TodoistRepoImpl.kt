package com.maaxgr.todoistnotionsync.interfaces.todoistrepo

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.result.Result
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigTodoist
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.addtask.AddTaskResponse
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.sync.Item
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.sync.TodoistSync
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat

class TodoistRepoImpl : TodoistRepo, KoinComponent {

    private val todoistConfig: ConfigTodoist by inject()

    override fun getTodoistEntries(): List<Item> {
        val (request, response, result) = Fuel.post("https://api.todoist.com/rest/v1/tasks")
            .header("Authorization", "Bearer ${todoistConfig.token}")
            .responseObject<List<Item>>()

        when(result) {
            is Result.Success -> {
                return result.value
            }
            is Result.Failure -> {
                throw result.error
            }
        }
    }

    fun getTodoistEntry(id: Long): Item {
        val (request, response, result) = Fuel.post("https://api.todoist.com/rest/v1/tasks/$id")
            .header("Authorization", "Bearer ${todoistConfig.token}")
            .responseObject<Item>()

        when(result) {
            is Result.Success -> {
                return result.value
            }
            is Result.Failure -> {
                throw result.error
            }
        }
    }


    override fun updateContent(todoistId: Long, content: String): Item {
        val body = mapOf("content" to content)

        val (request, response, result) = Fuel.post("https://api.todoist.com/rest/v1/tasks/$todoistId")
            .header("Authorization", "Bearer ${todoistConfig.token}")
            .jsonBody(body)
            .response()

        if (response.statusCode != 204) {
            throw Exception("Invalid Status Code ${response.statusCode}")
        }

        return getTodoistEntry(todoistId)
    }

    override fun setNotionEntryIdInTodoistEntry(todoistId: Long, notionId: String): Boolean {
        //TODO: Don't override complete description!
        val body = mapOf("description" to notionId)

        val (request, response, result) = Fuel.post("https://api.todoist.com/rest/v1/tasks/$todoistId")
            .header("Authorization", "Bearer ${todoistConfig.token}")
            .jsonBody(body)
            .response()

        return response.statusCode == 204
    }

    override fun createEntry(content: String): AddTaskResponse {
        //TODO: Prefix NotionId
        val body = mapOf(
            "content" to content,
            "description" to "",
            "project_id" to 2265619327
        )

        val (request, response, result) = Fuel.post("https://api.todoist.com/rest/v1/tasks")
            .header("Authorization", "Bearer ${todoistConfig.token}")
            .jsonBody(body)
            .responseObject<AddTaskResponse>()

        when(result) {
            is Result.Success -> {
                return result.value
            }
            is Result.Failure -> {
                throw result.error
            }
        }
    }

}
