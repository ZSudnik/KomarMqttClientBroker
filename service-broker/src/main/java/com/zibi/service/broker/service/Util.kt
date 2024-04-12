package com.zibi.service.broker.service

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun timeStr(): String = DateTimeFormatter
    .ofPattern("HH:mm:ss.SSS")
    .withZone(ZoneId.systemDefault())
    .format(Instant.now())
