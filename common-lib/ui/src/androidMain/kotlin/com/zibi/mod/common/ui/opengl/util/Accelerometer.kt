package com.zibi.mod.common.ui.opengl.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.zibi.mod.common.ui.opengl.util.Vector.Companion.ZERO_VECTOR
import java.util.*

class Accelerometer(val context: Context) : SensorEventListener {

  private var sensorManager: SensorManager? = null
  private var accelerometer: Sensor? = null
  private val vectorHistory = Array(SMOOTHNESS) { ZERO_VECTOR }
  private var index = 0
  private var vector: Vector? = null
  private var simulator: Simulator? = null


  override fun onSensorChanged(event: SensorEvent) {
    index %= SMOOTHNESS
    vectorHistory[index++] = Vector(
      event.values[0],
      event.values[1],
      event.values[2]
    )
    vector = Vector.sum(*vectorHistory)
  }

  override fun onAccuracyChanged(
    sensor: Sensor,
    accuracy: Int
  ) {
  }

  fun getVector(): Vector {
    simulator?.let {
      return it.getVector()
    }
    return if (vector == null) ZERO_VECTOR else vector!!
  }

  fun registerListener() {
    sensorManager?.registerListener(
      this,
      accelerometer,
      SensorManager.SENSOR_DELAY_GAME
    )
  }

  fun unregisterListener() {
    sensorManager?.unregisterListener(this)
  }

  companion object {
    private const val SMOOTHNESS = 20
  }

  fun init(isPreview: Boolean) {
    if (isPreview) {
      simulator = Simulator()
    } else {
      sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
      accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
      Arrays.fill(
        vectorHistory,
        ZERO_VECTOR
      )
    }
  }

  inner class Simulator() {

    var ind = 0

    fun getVector(): Vector {
      ind++
      if (ind >= sample.size) ind = 0
      return sample[ind]
    }

    private val sample: Array<Vector> = arrayOf(
      Vector(
        0.7567f,
        -1.2790f,
        58.0146f
      ),
      Vector(
        0.8632f,
        -1.5416f,
        67.8455f
      ),
      Vector(
        0.8632f,
        -1.5416f,
        67.8455f
      ),
      Vector(
        0.9488f,
        -1.7599f,
        77.5526f
      ),
      Vector(
        1.0385f,
        -1.9783f,
        87.1275f
      ),
      Vector(
        1.1192f,
        -2.2265f,
        96.8106f
      ),
      Vector(
        1.1127f,
        -2.4425f,
        106.5973f
      ),
      Vector(
        1.1402f,
        -2.6387f,
        116.4545f
      ),
      Vector(
        1.3460f,
        -2.8959f,
        126.3979f
      ),
      Vector(
        1.6349f,
        -3.1507f,
        136.4154f
      ),
      Vector(
        1.8323f,
        -3.3900f,
        146.1021f
      ),
      Vector(
        1.8323f,
        -3.3900f,
        146.1021f
      ),
      Vector(
        2.0267f,
        -3.6987f,
        155.8678f
      ),
      Vector(
        2.1613f,
        -3.9571f,
        165.7436f
      ),
      Vector(
        2.1769f,
        -4.0253f,
        175.4585f
      ),
      Vector(
        2.5639f,
        -4.1743f,
        185.5376f
      ),
      Vector(
        3.1202f,
        -4.5679f,
        195.6509f
      ),
      Vector(
        3.4325f,
        -4.7671f,
        195.7736f
      ),
      Vector(
        3.4325f,
        -4.7671f,
        195.7736f
      ),
      Vector(
        3.7208f,
        -4.9220f,
        196.2043f
      ),
      Vector(
        3.9990f,
        -4.8969f,
        196.5117f
      ),
      Vector(
        4.3609f,
        -5.0315f,
        196.5363f
      ),
      Vector(
        4.7438f,
        -5.0560f,
        196.1899f
      ),
      Vector(
        5.5609f,
        -5.1906f,
        195.8770f
      ),
      Vector(
        6.4774f,
        -5.2845f,
        195.7981f
      ),
      Vector(
        6.4774f,
        -5.2845f,
        195.7981f
      ),
      Vector(
        7.2873f,
        -5.3336f,
        195.8256f
      ),
      Vector(
        8.3330f,
        -5.4736f,
        196.0451f
      ),
      Vector(
        9.2836f,
        -5.5783f,
        195.9297f
      ),
      Vector(
        10.3328f,
        -5.6782f,
        195.6450f
      ),
      Vector(
        11.4987f,
        -6.0287f,
        195.5887f
      ),
      Vector(
        12.6060f,
        -6.9284f,
        195.9979f
      ),
      Vector(
        13.8730f,
        -7.2072f,
        195.4978f
      ),
      Vector(
        15.1322f,
        -8.0453f,
        196.1056f
      ),
      Vector(
        16.1611f,
        -8.1876f,
        196.2474f
      ),
      Vector(
        17.7458f,
        -8.7452f,
        195.9650f
      ),
      Vector(
        19.6588f,
        -9.3769f,
        195.5959f
      ),
      Vector(
        21.5049f,
        -10.0074f,
        195.0958f
      ),
      Vector(
        23.3282f,
        -10.4752f,
        194.6143f
      ),
      Vector(
        25.4243f,
        -10.9304f,
        195.1245f
      ),
      Vector(
        27.5528f,
        -11.0979f,
        195.2514f
      ),
      Vector(
        29.1751f,
        -11.2786f,
        194.2894f
      ),
      Vector(
        29.1751f,
        -11.2786f,
        194.2894f
      ),
      Vector(
        31.4309f,
        -11.3749f,
        193.3221f
      ),
      Vector(
        34.4770f,
        -11.6698f,
        193.0464f
      ),
      Vector(
        34.4770f,
        -11.6698f,
        193.0464f
      ),
      Vector(
        37.9328f,
        -12.1932f,
        193.2928f
      ),
      Vector(
        41.2259f,
        -12.6425f,
        193.5997f
      ),
      Vector(
        43.9238f,
        -12.8632f,
        192.9315f
      ),
      Vector(
        46.5344f,
        -12.9864f,
        191.2177f
      ),
      Vector(
        49.5081f,
        -12.8991f,
        189.8902f
      ),
      Vector(
        49.5081f,
        -12.8991f,
        189.8902f
      ),
      Vector(
        53.0973f,
        -12.9637f,
        188.2224f
      ),
      Vector(
        57.5934f,
        -13.0331f,
        186.5313f
      ),
      Vector(
        57.5934f,
        -13.0331f,
        186.5313f
      ),
      Vector(
        62.0848f,
        -12.5689f,
        184.4376f
      ),
      Vector(
        66.3649f,
        -12.5557f,
        183.4715f
      ),
      Vector(
        70.3574f,
        -11.8923f,
        180.9494f
      ),
      Vector(
        73.8969f,
        -11.5041f,
        178.6003f
      ),
      Vector(
        77.2289f,
        -10.9125f,
        176.3600f
      ),
      Vector(
        81.0048f,
        -10.5147f,
        174.6300f
      ),
      Vector(
        84.9637f,
        -10.1186f,
        172.9933f
      ),
      Vector(
        88.7618f,
        -9.6072f,
        171.1383f
      ),
      Vector(
        92.1123f,
        -9.0251f,
        168.7167f
      ),
      Vector(
        92.1123f,
        -9.0251f,
        168.7167f
      ),
      Vector(
        94.9346f,
        -8.7434f,
        165.7706f
      ),
      Vector(
        99.1400f,
        -8.7852f,
        163.3909f
      ),
      Vector(
        103.0152f,
        -8.6393f,
        162.3680f
      ),
      Vector(
        105.8017f,
        -8.3575f,
        161.0005f
      ),
      Vector(
        107.3337f,
        -7.8742f,
        158.9139f
      ),
      Vector(
        108.4673f,
        -7.2526f,
        156.1909f
      ),
      Vector(
        110.4563f,
        -7.0684f,
        154.6786f
      ),
      Vector(
        112.5452f,
        -6.9266f,
        155.0986f
      ),
      Vector(
        114.1359f,
        -6.7358f,
        155.6214f
      ),
      Vector(
        114.1359f,
        -6.7358f,
        155.6214f
      ),
      Vector(
        113.6441f,
        -5.9498f,
        156.7861f
      ),
      Vector(
        113.6441f,
        -5.9498f,
        156.7861f
      ),
      Vector(
        112.4346f,
        -5.1817f,
        156.7993f
      ),
      Vector(
        111.1448f,
        -4.5936f,
        156.2848f
      ),
      Vector(
        110.5993f,
        -4.2281f,
        157.1558f
      ),
      Vector(
        110.2834f,
        -4.0905f,
        158.2876f
      ),
      Vector(
        109.4890f,
        -3.8022f,
        159.9877f
      ),
      Vector(
        109.4890f,
        -3.8022f,
        159.9877f
      ),
      Vector(
        107.9785f,
        -3.3194f,
        161.5490f
      ),
      Vector(
        105.3183f,
        -2.7996f,
        162.4296f
      ),
      Vector(
        102.3727f,
        -2.2427f,
        163.1492f
      ),
      Vector(
        100.1994f,
        -1.8473f,
        163.8665f
      ),
      Vector(
        98.1039f,
        -1.4824f,
        166.2282f
      ),
      Vector(
        94.8377f,
        -0.9200f,
        169.0978f
      ),
      Vector(
        90.5582f,
        -0.3517f,
        170.4013f
      ),
      Vector(
        86.2768f,
        0.3093f,
        171.8202f
      ),
      Vector(
        82.3634f,
        1.0271f,
        173.4916f
      ),
      Vector(
        78.5899f,
        1.4991f,
        175.5476f
      ),
      Vector(
        74.5855f,
        2.1673f,
        177.7718f
      ),
      Vector(
        70.0583f,
        2.9815f,
        179.1363f
      ),
      Vector(
        65.4868f,
        3.5121f,
        180.3548f
      ),
      Vector(
        61.1474f,
        4.1067f,
        181.6499f
      ),
      Vector(
        56.7362f,
        4.7527f,
        183.0013f
      ),
      Vector(
        52.2180f,
        5.2552f,
        184.7971f
      ),
      Vector(
        52.2180f,
        5.2552f,
        184.7971f
      ),
      Vector(
        47.6076f,
        5.5280f,
        186.4721f
      ),
      Vector(
        42.4888f,
        5.8971f,
        187.4292f
      ),
      Vector(
        37.8850f,
        6.0239f,
        188.6824f
      ),
      Vector(
        32.8140f,
        6.2889f,
        189.4715f
      ),
      Vector(
        27.6766f,
        6.3033f,
        189.9447f
      ),
      Vector(
        23.2271f,
        6.5186f,
        190.6458f
      ),
      Vector(
        19.0899f,
        6.6275f,
        191.8194f
      ),
      Vector(
        19.0899f,
        6.6275f,
        191.8194f
      ),
      Vector(
        14.2905f,
        6.7777f,
        192.9913f
      ),
      Vector(
        9.4684f,
        7.0170f,
        193.3317f
      ),
      Vector(
        4.8048f,
        7.3017f,
        193.4477f
      ),
      Vector(
        0.8824f,
        7.3023f,
        193.9329f
      ),
      Vector(
        -3.2596f,
        7.4040f,
        194.2966f
      ),
      Vector(
        -7.5290f,
        7.3286f,
        194.6519f
      ),
      Vector(
        -12.1442f,
        7.1486f,
        194.7291f
      ),
      Vector(
        -16.9711f,
        7.0546f,
        194.2069f
      ),
      Vector(
        -21.8883f,
        7.1252f,
        193.3909f
      ),
      Vector(
        -26.6237f,
        7.3932f,
        192.5355f
      ),
      Vector(
        -31.1474f,
        7.5027f,
        192.0139f
      ),
      Vector(
        -35.6112f,
        7.4315f,
        191.0173f
      ),
      Vector(
        -40.3107f,
        7.3316f,
        189.6641f
      ),
      Vector(
        -45.1029f,
        7.4100f,
        188.4557f
      ),
      Vector(
        -49.7743f,
        7.5368f,
        187.5608f
      ),
      Vector(
        -54.4810f,
        8.0279f,
        186.2818f
      ),
      Vector(
        -58.5249f,
        8.3504f,
        185.1016f
      ),
      Vector(
        -62.2720f,
        8.6399f,
        183.9255f
      ),
      Vector(
        -66.0401f,
        8.5220f,
        182.9887f
      ),
      Vector(
        -70.1324f,
        8.2445f,
        182.0023f
      ),
      Vector(
        -74.6106f,
        8.0381f,
        180.7921f
      ),
      Vector(
        -78.8782f,
        8.1452f,
        178.8007f
      ),
      Vector(
        -82.1612f,
        8.2564f,
        177.3279f
      ),
      Vector(
        -84.6120f,
        7.9095f,
        177.2268f
      ),
      Vector(
        -86.6405f,
        7.2329f,
        177.0527f
      ),
      Vector(
        -87.8411f,
        6.4588f,
        176.9606f
      ),
      Vector(
        -88.5751f,
        6.0497f,
        177.1766f
      ),
      Vector(
        -88.9693f,
        5.2397f,
        176.9708f
      ),
      Vector(
        -88.9693f,
        5.2397f,
        176.9708f
      ),
      Vector(
        -88.8264f,
        3.8327f,
        176.7255f
      ),
      Vector(
        -87.8094f,
        1.2455f,
        176.3606f
      ),
      Vector(
        -87.8094f,
        1.2455f,
        176.3606f
      ),
      Vector(
        -86.5974f,
        -0.2076f,
        176.9522f
      ),
      Vector(
        -84.8004f,
        -1.9298f,
        177.8938f
      ),
      Vector(
        -82.5925f,
        -3.7035f,
        179.0029f
      ),
      Vector(
        -80.1141f,
        -5.6357f,
        179.6723f
      ),
      Vector(
        -77.5610f,
        -7.8987f,
        179.8493f
      ),
      Vector(
        -74.7901f,
        -10.1204f,
        179.9570f
      ),
      Vector(
        -72.0192f,
        -12.3841f,
        180.8771f
      ),
      Vector(
        -68.9109f,
        -14.5226f,
        182.1363f
      ),
      Vector(
        -65.1781f,
        -16.9771f,
        183.4625f
      ),
      Vector(
        -61.0888f,
        -19.6726f,
        184.8701f
      ),
      Vector(
        -56.6842f,
        -22.5320f,
        186.4739f
      ),
      Vector(
        -52.8264f,
        -25.6170f,
        187.3891f
      ),
      Vector(
        -49.6971f,
        -28.3173f,
        186.8382f
      ),
      Vector(
        -49.6971f,
        -28.3173f,
        186.8382f
      ),
      Vector(
        -46.3346f,
        -30.8273f,
        186.2041f
      ),
      Vector(
        -42.6108f,
        -33.4403f,
        185.5006f
      ),
      Vector(
        -38.4688f,
        -36.5761f,
        185.0065f
      ),
      Vector(
        -34.6056f,
        -39.5001f,
        185.3403f
      ),
      Vector(
        -30.8907f,
        -42.3775f,
        185.8828f
      ),
      Vector(
        -27.3081f,
        -45.9595f,
        186.4852f
      ),
      Vector(
        -23.6231f,
        -49.4872f,
        186.0342f
      ),
      Vector(
        -23.6231f,
        -49.4872f,
        186.0342f
      ),
      Vector(
        -19.8879f,
        -53.0094f,
        185.5161f
      ),
      Vector(
        -15.9817f,
        -56.2834f,
        184.6224f
      ),
      Vector(
        -12.3553f,
        -59.5837f,
        183.6779f
      ),
      Vector(
        -8.7697f,
        -62.7404f,
        182.8278f
      ),
      Vector(
        -5.3737f,
        -65.8696f,
        182.4593f
      ),
      Vector(
        -2.0883f,
        -69.0688f,
        182.2242f
      ),
      Vector(
        1.1438f,
        -72.2088f,
        181.5740f
      ),
      Vector(
        4.4489f,
        -75.3817f,
        180.3560f
      ),
      Vector(
        7.9825f,
        -77.7655f,
        178.8916f
      ),
      Vector(
        7.9825f,
        -77.7655f,
        178.8916f
      ),
      Vector(
        11.8373f,
        -79.8569f,
        177.0683f
      ),
      Vector(
        15.2554f,
        -81.9093f,
        175.5728f
      ),
      Vector(
        18.2237f,
        -83.6256f,
        174.1138f
      ),
      Vector(
        21.4696f,
        -85.7402f,
        173.4886f
      ),
      Vector(
        25.4267f,
        -88.2024f,
        173.6926f
      ),
      Vector(
        28.8024f,
        -89.9300f,
        173.7028f
      ),
      Vector(
        31.2712f,
        -90.8770f,
        172.2934f
      ),
      Vector(
        31.2712f,
        -90.8770f,
        172.2934f
      ),
      Vector(
        33.6251f,
        -91.5177f,
        170.3989f
      ),
      Vector(
        36.5157f,
        -91.7354f,
        168.1281f
      ),
      Vector(
        39.8937f,
        -91.7839f,
        166.6631f
      ),
      Vector(
        43.7743f,
        -91.7432f,
        165.8412f
      ),
      Vector(
        47.3085f,
        -91.3155f,
        165.0288f
      ),
      Vector(
        50.0507f,
        -90.6084f,
        164.7064f
      ),
      Vector(
        50.0507f,
        -90.6084f,
        164.7064f
      ),
      Vector(
        52.1970f,
        -89.4311f,
        164.7387f
      ),
      Vector(
        54.2668f,
        -88.0295f,
        164.1470f
      ),
      Vector(
        57.6778f,
        -86.3713f,
        163.7863f
      ),
      Vector(
        60.9542f,
        -84.4942f,
        163.7803f
      ),
      Vector(
        63.8094f,
        -82.4489f,
        163.8168f
      ),
      Vector(
        65.2971f,
        -80.2595f,
        163.4872f
      ),
      Vector(
        66.9488f,
        -77.6896f,
        163.0864f
      ),
      Vector(
        68.6399f,
        -75.1173f,
        163.1905f
      ),
      Vector(
        70.5942f,
        -72.4170f,
        163.4477f
      ),
      Vector(
        70.5942f,
        -72.4170f,
        163.4477f
      ),
      Vector(
        72.6461f,
        -69.7328f,
        164.5574f
      ),
      Vector(
        74.3821f,
        -66.4289f,
        165.1742f
      ),
      Vector(
        75.0868f,
        -62.3887f,
        164.7943f
      ),
      Vector(
        75.3500f,
        -58.7689f,
        164.3588f
      ),
      Vector(
        76.7294f,
        -55.3185f,
        164.6352f
      ),
      Vector(
        77.7757f,
        -52.0523f,
        165.6629f
      ),
      Vector(
        78.0814f,
        -48.7364f,
        167.1614f
      ),
      Vector(
        78.0814f,
        -48.7364f,
        167.1614f
      ),
      Vector(
        77.6979f,
        -45.5605f,
        168.3799f
      ),
      Vector(
        76.1306f,
        -42.1573f,
        169.5387f
      ),
      Vector(
        73.8042f,
        -38.8888f,
        170.7094f
      ),
      Vector(
        71.7542f,
        -35.4227f,
        171.1795f
      ),
      Vector(
        70.2234f,
        -32.0889f,
        172.0487f
      ),
      Vector(
        68.6136f,
        -29.0991f,
        173.5622f
      ),
      Vector(
        68.6136f,
        -29.0991f,
        173.5622f
      ),
      Vector(
        65.0285f,
        -26.0919f,
        174.9542f
      ),
      Vector(
        61.3454f,
        -23.2313f,
        176.1704f
      ),
      Vector(
        58.3071f,
        -21.0574f,
        178.6290f
      ),
      Vector(
        55.2018f,
        -18.8118f,
        181.1498f
      ),
      Vector(
        50.7889f,
        -16.7545f,
        182.1094f
      ),
      Vector(
        45.8052f,
        -14.9157f,
        183.4625f
      ),
      Vector(
        45.8052f,
        -14.9157f,
        183.4625f
      ),
      Vector(
        40.6050f,
        -13.3238f,
        185.3289f
      ),
      Vector(
        34.9920f,
        -11.6818f,
        186.6509f
      ),
      Vector(
        29.1709f,
        -10.0762f,
        187.0081f
      ),
      Vector(
        23.6967f,
        -9.1675f,
        188.6358f
      ),
      Vector(
        17.7075f,
        -8.1936f,
        190.7433f
      ),
      Vector(
        10.7444f,
        -7.1492f,
        192.3704f
      ),
      Vector(
        4.5200f,
        -6.3296f,
        192.9500f
      ),
      Vector(
        4.5200f,
        -6.3296f,
        192.9500f
      ),
      Vector(
        -7.3693f,
        -4.3813f,
        193.6739f
      ),
      Vector(
        -12.3559f,
        -3.3709f,
        193.2384f
      ),
      Vector(
        -12.3559f,
        -3.3709f,
        193.2384f
      ),
      Vector(
        -16.6875f,
        -2.5226f,
        193.2575f
      ),
      Vector(
        -20.7751f,
        -1.8108f,
        194.0035f
      ),
      Vector(
        -24.7203f,
        -1.2239f,
        193.8851f
      ),
      Vector(
        -28.5189f,
        -0.3135f,
        193.7457f
      ),
      Vector(
        -31.5057f,
        0.2895f,
        194.0047f
      ),
      Vector(
        -33.8004f,
        0.9170f,
        193.9437f
      ),
      Vector(
        -33.8004f,
        0.9170f,
        193.9437f
      ),
      Vector(
        -36.1179f,
        2.3324f,
        191.9373f
      ),
      Vector(
        -37.7270f,
        3.7502f,
        190.4597f
      ),
      Vector(
        -38.7464f,
        4.4794f,
        191.0382f
      ),
      Vector(
        -39.6216f,
        5.4628f,
        191.1076f
      ),
      Vector(
        -40.2850f,
        6.8166f,
        191.4192f
      ),
      Vector(
        -39.8614f,
        7.4112f,
        192.0641f
      ),
      Vector(
        -39.2453f,
        8.4144f,
        192.5427f
      ),
      Vector(
        -39.2453f,
        8.4144f,
        192.5427f
      ),
      Vector(
        -38.5735f,
        9.2776f,
        191.5263f
      ),
      Vector(
        -36.6808f,
        10.0546f,
        190.7588f
      ),
      Vector(
        -33.9745f,
        10.7988f,
        190.2091f
      ),
      Vector(
        -30.4092f,
        11.5047f,
        189.8113f
      ),
      Vector(
        -27.8100f,
        11.6160f,
        190.0787f
      ),
      Vector(
        -24.6287f,
        12.2931f,
        191.8553f
      ),
      Vector(
        -22.8102f,
        13.1689f,
        192.5720f
      ),
      Vector(
        -21.2381f,
        13.9310f,
        193.1397f
      ),
      Vector(
        -19.3717f,
        14.1380f,
        194.1608f
      ),
      Vector(
        -19.3717f,
        14.1380f,
        194.1608f
      ),
      Vector(
        -17.0578f,
        13.9789f,
        194.4276f
      ),
      Vector(
        -15.0580f,
        13.7569f,
        194.9104f
      ),
      Vector(
        -12.9512f,
        13.7970f,
        194.9863f
      ),
      Vector(
        -11.8044f,
        13.9944f,
        195.0366f
      ),
      Vector(
        -10.7480f,
        13.9059f,
        195.2717f
      ),
      Vector(
        -8.8277f,
        13.9334f,
        196.2390f
      ),
      Vector(
        -7.2963f,
        14.1500f,
        196.2330f
      ),
      Vector(
        -7.2963f,
        14.1500f,
        196.2330f
      ),
      Vector(
        -5.3791f,
        13.9089f,
        196.6278f
      ),
      Vector(
        -3.5073f,
        13.2568f,
        196.1403f
      ),
      Vector(
        -2.2008f,
        13.0983f,
        195.2795f
      ),
      Vector(
        -0.9918f,
        12.4517f,
        195.3064f
      ),
      Vector(
        0.0933f,
        12.1454f,
        195.6019f
      ),
      Vector(
        0.6179f,
        11.8971f,
        195.1903f
      ),
      Vector(
        0.6179f,
        11.8971f,
        195.1903f
      ),
    )
  }
}