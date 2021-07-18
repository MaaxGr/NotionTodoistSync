package com.maaxgr.todoistnotionsync.interfaces.todoistrepo.entities.getacitivty

data class Activity(
    val count: Int,
    val events: List<Event>
)