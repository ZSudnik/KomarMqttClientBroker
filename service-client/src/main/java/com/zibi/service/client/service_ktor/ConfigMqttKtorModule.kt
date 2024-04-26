package com.zibi.service.client.service_ktor

import com.zibi.mod.data_store.data.Topic
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.zibi.komar.mclient.ktor.Mqtt
import io.zibi.komar.mclient.ktor.topic


fun Application.configureMqtt() {
    // Installs the plugin to the server so that you can use it, won't work otherwise
    install(Mqtt) {
        initSubscriptions(topics = Topic.list)
    }

    // Allows to map function to different topics
    routing {
        topic(Topic.LivingRoom.light1) {
            println(it)
        }
        topic(Topic.LivingRoom.light2) {
            println(it)
        }
    }
}