package com.maaxgr.todoistnotionsync

import com.maaxgr.todoistnotionsync.interfaces.config.ConfigLoader
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigNotion
import com.maaxgr.todoistnotionsync.interfaces.config.ConfigYaml
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepo
import com.maaxgr.todoistnotionsync.interfaces.notionrepo.NotionRepoImpl
import com.maaxgr.todoistnotionsync.interfaces.synctable.SyncTable
import com.maaxgr.todoistnotionsync.interfaces.synctable.SyncTableImpl
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.TodoistRepo
import com.maaxgr.todoistnotionsync.interfaces.todoistrepo.TodoistRepoImpl
import kotlinx.coroutines.runBlocking
import org.jraf.klibnotion.client.Authentication
import org.jraf.klibnotion.client.ClientConfiguration
import org.jraf.klibnotion.client.NotionClient
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.system.exitProcess

fun main() {

    startKoin {
        val module = module {
            single { ConfigLoader().loadConfig() }
            single { get<ConfigYaml>().notion }
            single { get<ConfigYaml>().todoist }
            single<NotionRepo> { NotionRepoImpl() }
            single<TodoistRepo> { TodoistRepoImpl() }
            single { NotionClient.newInstance(ClientConfiguration(Authentication(get<ConfigNotion>().token))) }
            single<SyncTable> { SyncTableImpl() }
        }
        modules(module)
    }

    runBlocking {
        SyncApplication().init()
        exitProcess(0)
    }
}
