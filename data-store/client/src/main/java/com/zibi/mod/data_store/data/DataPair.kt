package com.zibi.mod.data_store.data

data class DataPair(
    val topic: String,
    val msg: String,
){
    override fun toString(): String {
        return "DataPublic : topic=$topic, msg=$msg "
    }
}
