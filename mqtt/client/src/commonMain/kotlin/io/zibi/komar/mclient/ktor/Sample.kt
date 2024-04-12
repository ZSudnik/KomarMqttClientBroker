package io.zibi.komar.mclient.ktor

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

class Sample {
}
@OptIn(InternalCoroutinesApi::class)
fun Application.main() {


    fun sendMsg(topic: String, msg:String): Flow<Pair<String, String>> = flow { // flow builder
        if(topic.isNotEmpty())
          emit(Pair(topic,msg)) // emit next value
    }

    var cc: Flow<Pair<String,String>>? = null


//    val mapEmmit: Map< String, (String,String)-> Flow<String>> = mapOf(
//        "topic1" to ::simple
//    )
//    val mapCollect: Map< String, (Flow<String>)?> = mapOf(
//        pair = "topic1" to cc
//    )

    routing {
        route("topic"){
            var pair: Pair<String,String> = Pair("","")
            var msg: String = ""
            var top: String = ""
            runBlocking {
                cc?.let { flowPair ->
                    flowPair
                        .filter { top == it.first }
//                        .collect {
//                            pair = it
//                        }
                }
        }

        topic("topic1"){
            cc = sendMsg(top,"{POWER=ON}")
//            cc = sendMsg(this.topic.value, it.payload.toString())
//            this.sendMessage( this.topic.value, "")
//                this.sendMessage( this.topic.value, it.toString())
            }

        }
    }
}