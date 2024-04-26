package com.zibi.mod.data_store.data


object Topic{
//    private val stringResolver: StringResolver by inject()
//    private val clientSetting: ClientSetting by inject()

    object LivingRoom{
        val light1 = "t_light1"
        val light2 = "t_light2"
        object desc{
            val light1 = "Light 1"//stringResolver.getString(R.string.light_1)
            val light2 = "Light 2"//stringResolver.getString(R.string.light_2)
            val light1and2 = "Light 1 and 2"//stringResolver.getString(R.string.light_1_2)
        }
    }
    val list = mutableListOf(LivingRoom.light1, LivingRoom.light2)
}