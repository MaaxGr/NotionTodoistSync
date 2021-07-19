package com.maaxgr.todoistnotionsync.utils

import java.text.SimpleDateFormat
import java.util.*

val DATE_FORMAT_ISO8601_WITHOUTMS_AND_WITHTS = "yyyy-MM-dd'T'HH:mm:ssX"
val DATE_FORMAT_ISO8601_WITHMS_AND_WITHTS = "yyyy-MM-dd'T'HH:mm:ss.SSSX"

fun String.asDate(format: String, forceGMT: Boolean = false): Date {
    return SimpleDateFormat(format)
        .apply {
            if (forceGMT) {
                timeZone = TimeZone.getTimeZone("GMT")
            }
        }
        .parse(this)
}