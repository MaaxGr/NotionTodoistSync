package com.maaxgr.todoistnotionsync

import com.maaxgr.todoistnotionsync.interfaces.config.ConfigLoader
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigYaml
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepoImpl
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.TodoistRepo
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.TodoistRepoImpl
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun main() {

    startKoin {
        val module = module {
            single { ConfigLoader().loadConfig() }
            single { get<ConfigYaml>().notion }
            single { get<ConfigYaml>().todoist }
            single<NotionRepo> { NotionRepoImpl() }
            single<TodoistRepo> { TodoistRepoImpl() }
        }
        modules(module)
    }

    SyncApplication()

}
