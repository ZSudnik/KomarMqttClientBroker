package com.zibi.common.device.lightbulb

//import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.zibi.common.device.Json
import com.zibi.mod.common.ui.colorpicker.HsvColor


class LightBulbData(
     var topic: String = "",
     var POWER: Power = Power.ON,
     var Dimmer: Int = 50, //0-100
     var HSBColor: HsvColor = HsvColor.DEFAULT,
     var White: Int = 50,
     var CT: Int = 493, //no used now
     var isColorOrWhite: Boolean = true,
) {
     private lateinit var copyLB: LightBulbData
     constructor(topic: String, dataStr: String) : this(dataStr) {
          this.topic = topic
     }

     constructor(dataStr: String) : this() {
          try {
               val parsed = Json.parse(dataStr)
               parsed.getString("POWER")?.let { this.POWER = Power.getState(it) }
               parsed.getString("HSBColor")?.let { this.HSBColor = HsvColor.fromStrJson(it) }
               parsed.getInt("Dimmer")?.let {
                    this.Dimmer = it
                    this.HSBColor.value = it/100f
               }
               parsed.getInt("White")?.let { this.White = it }
               parsed.getInt("CT")?.let { this.CT = it }
          } catch (e: Exception) {
               e.printStackTrace()
          }
          copyLB = this.copy()
     }


     fun toJsonString(): String {
         return when{
               copyLB.POWER != this.POWER ->{
                    copyLB.POWER = this.POWER
                    Gson().toJson( LBPower( this.POWER.key))
               }
               copyLB.Dimmer != this.Dimmer ->{
                    copyLB.Dimmer = this.Dimmer
                    Gson().toJson( LBDimmer( this.Dimmer))
               }
              copyLB.HSBColor != this.HSBColor ->{
                   copyLB.HSBColor = this.HSBColor
                   Gson().toJson( LBHsbColor( this.HSBColor.toStrJson()))
              }
//              copyLB.Color != this.Color ->{
//                    copyLB.Color = this.Color
//                    Gson().toJson( LBColor( this.Color.toString()))
//               }
               copyLB.White != this.White ->{
                    copyLB.White = this.White
                    Gson().toJson( LBWhite( this.White))
               }
               copyLB.CT != this.CT ->{
                    copyLB.CT = this.CT
                    Gson().toJson( LBCT( this.CT))
               }
              else -> "{}"
         }
       }

     private data class LightBulbDataOut(
          @Expose var POWER: String = "ON", //"ON", "OFF"
          @Expose var Dimmer: Int = 50, //0-100 (0-255)
          @Expose var HSBColor: String = "300,100,100",
//          @Expose var Color: String = "0000000000", //"00FFFFFFFF"
          @Expose var White: Int = 50, //0-100
          @Expose var CT: Int = 493, //153..500 = set color temperature from 153 (cold) to 500 (warm) for CT lights
     )
     private data class LBPower( @Expose var POWER: String )
     private data class LBDimmer( @Expose var Dimmer: Int )
     private data class LBHsbColor( @Expose var HSBColor: String )
//     private data class LBColor( @Expose var Color: String )
     private data class LBWhite( @Expose var White: Int )
     private data class LBCT( @Expose var CT: Int )

     private fun copy(): LightBulbData = LightBulbData(
          topic = this.topic,
          POWER = this.POWER,
          Dimmer = this.Dimmer,
          HSBColor = this.HSBColor,
//          Color = this.Color,
          White = this.White,
          CT = this.CT
     )
}

enum class Power(val key: String) {
     ON("ON"), OFF("OFF");
     fun isON() = this == ON
     companion object {
          fun getState(key: String): Power = entries.find { it.key == key } ?: ON
     }
}

data class ColorRGBWY(
     var isColorOrWhite: Boolean = true,
     var rr: Int = 0, // 0xFF,
     var gg: Int = 0, // 0xFF,
     var bb: Int = 0, // 0xFF,
     var ww: Int = 0, // 0xFF,
     var yy: Int = 0, // 0xFF,
){
     constructor( value: String): this(){
          this.from(value)
     }

     override fun toString(): String =
           "${rr.toString(16)}," +
                  "${gg.toString(16)}," +
                  "${bb.toString(16)}," +
                  "${ww.toString(16)}," +
                  "${yy.toString(16)} "

//     fun toHsvString(): String {
//          TODO() //``conver WW YY to rgb
//          val hsv =  RGB(
//               r = rr,
//               g = gg,
//               b = bb,
//               255
//          ).toHSV()
//          return "${hsv.h}, ${hsv.s * 100}, ${hsv.v * 100}"
//     }

     private fun from( value: String){
          val index = value.length
          this.rr = value.subSequence( index-10, index-8).toString().toInt(16)
          this.gg = value.subSequence( index-8, index-6).toString().toInt(16)
          this.bb = value.subSequence( index-6, index-4).toString().toInt(16)
          this.ww = value.subSequence( index-4, index-2).toString().toInt(16)
          this.yy = value.subSequence( index-2, index).toString().toInt(16)
          isColorOrWhite = ( rr > 0 || gg > 0 || bb > 0)
     }

//     fun getColor( limitYellow: Color): Color {
//          return if( isColorOrWhite)
//               Color( red= rr, green = gg, blue = bb, alpha = 255)
//          else {
//               val g = (limitYellow.green*255).toInt()
//               val b = (limitYellow.blue*255).toInt()
//               Color(
//                    red = (limitYellow.red*255).toInt(),
//                    green = if(g<gg) gg else g,
//                    blue = if(b<bb) bb else b,
//                    alpha = 255
//               )
//          }
//     }
}
