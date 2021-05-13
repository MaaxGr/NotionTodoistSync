package com.maaxgr.todoistnotionsync.interfaces.config

import kotlinx.serialization.Serializable as Serializable

@Serializable
data class ConfigYaml(
    val notion: ConfigNotion,
)

@Serializable
data class ConfigNotion(
    val token: String
)

