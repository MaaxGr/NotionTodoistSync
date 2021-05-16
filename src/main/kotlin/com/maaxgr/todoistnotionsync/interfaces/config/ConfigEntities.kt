package com.maaxgr.todoistnotionsync.interfaces.config

import kotlinx.serialization.Serializable as Serializable

@Serializable
data class ConfigYaml(
    val notion: ConfigNotion,
    val todoist: ConfigTodoist
)

@Serializable
data class ConfigNotion(
    val token: String
)

@Serializable
data class ConfigTodoist(
    val token: String
)

