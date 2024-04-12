package com.zibi.client.fragment.start.data

import com.zibi.mod.data_store.data.Topic
import kotlin.reflect.full.isSubclassOf

sealed class LightPoint(val topics: List<String>, var desc : String = "") {

    data object LightOne :
        LightPoint(
            topics = listOf(Topic.LivingRoom.light1),
            desc = Topic.LivingRoom.desc.light1
        )
    data object LightTwo :
        LightPoint(
            topics = listOf(Topic.LivingRoom.light2),
            desc = Topic.LivingRoom.desc.light2
        )
    data object LightAll :
        LightPoint(
            topics = listOf(Topic.LivingRoom.light1, Topic.LivingRoom.light2),
            desc =  Topic.LivingRoom.desc.light1and2
        )
}

class AllLight {

    val entries = LightPoint::class.nestedClasses
        .asSequence()
        .filter { klass -> klass.isSubclassOf(LightPoint::class) }
        .filter { klass -> klass.isFinal }
        .map { klass -> klass.objectInstance }
        .filterNotNull()
        .filterIsInstance<LightPoint>()
        .sortedByDescending { lightPoint -> lightPoint.desc }
        .toList()
}