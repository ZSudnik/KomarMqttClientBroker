package com.zibi.common.device.lightbulb

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.zibi.common.device.Json

data class LightBulbShortModel(
     @Expose var POWER: String? = null,
     @Expose var Dimmer: Int? = null,
     @Expose var HSBColor: String? = null,
     @Expose var White: Int? = null,
     @Expose var CT: Int? = null,
) {
//     private lateinit var copyLB: LightBulbData
//     constructor(topic: String, dataStr: String) : this(dataStr) {
//          this.topic = topic
//     }

     constructor(dataStr: String) : this() {
         overwrite( dataStr)
     }

     fun overwrite(msg: String){
          try {
               val parsed = Json.parse(msg)
               parsed.getString("POWER")?.let { this.POWER = it }
               parsed.getInt("Dimmer")?.let { this.Dimmer = it }
               parsed.getString("HSBColor")?.let { this.HSBColor = it }
               parsed.getInt("White")?.let { this.White = it }
               parsed.getInt("CT")?.let { this.CT = it }
          } catch (e: Exception) {
               e.printStackTrace()
          }
     }

     fun toJsonString(): String {
          return Gson().toJson( this )
     }

     private data class LightBulbDataOut(
          @Expose var POWER: String = "ON", //"ON", "OFF"
          @Expose var Dimmer: Int = 50, //0-100 (0-255)
          @Expose var HSBColor: String = "300,100,100",
          @Expose var White: Int = 50, //0-100
          @Expose var CT: Int = 493, //153..500 = set color temperature from 153 (cold) to 500 (warm) for CT lights
     )
     private data class PowerX( @Expose var POWER: String )
     private data class DimmerX( @Expose var Dimmer: Int )
     private data class HsbColorX( @Expose var HSBColor: String )
     private data class WhiteX( @Expose var White: Int )
     private data class CTX( @Expose var CT: Int )

     private fun copy(): LightBulbShortModel = LightBulbShortModel(
//          topic = this.topic,
          POWER = this.POWER,
          Dimmer = this.Dimmer,
          HSBColor = this.HSBColor,
          White = this.White,
          CT = this.CT
     )
}
