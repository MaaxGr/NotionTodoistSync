package com.maaxgr.todoistnotionsync.interfaces.sync.integrator

interface Integrator {

    val integratorName: String

    suspend fun integrate()

    fun log(message: String) {
        println("[$integratorName] $message")
    }

}