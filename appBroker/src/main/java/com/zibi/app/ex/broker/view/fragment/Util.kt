package com.zibi.app.ex.broker.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BlurMaskFilter
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.text.Spanned
import android.util.Base64
import android.util.DisplayMetrics
import android.view.View
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebViewDatabase
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.zibi.app.ex.broker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.String.join
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.text.Format
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.nCopies
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.round
import kotlin.system.exitProcess

object Util {

  private var dateFormat: Format? = null
  private var dateTimeFormat: Format? = null
  private var dateTimeFormatWithoutSeconds: Format? = null

  init {
    dateFormat = SimpleDateFormat(com.zibi.app.ex.broker.view.Constants.DATE_FORMAT)
    dateTimeFormat = SimpleDateFormat(com.zibi.app.ex.broker.view.Constants.DATE_TIME_FORMAT)
    dateTimeFormatWithoutSeconds = SimpleDateFormat(com.zibi.app.ex.broker.view.Constants.DATE_TIME_FORMAT_WITHOUT_SECONDS)
  }

  @JvmStatic
  fun fromHtml(html: String): Spanned {
    return HtmlCompat.fromHtml(
      html,
      HtmlCompat.FROM_HTML_MODE_LEGACY
    )
  }

  private const val HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>"
  private val pattern = Pattern.compile(HTML_PATTERN)

  @JvmStatic
  fun hasHTMLTags(text: String): Boolean {
    val matcher: Matcher = pattern.matcher(text)
    return matcher.find()
  }

  @JvmStatic
  fun base64Decode(data: String?): ByteArray {
    return Base64.decode(
      data,
      Base64.NO_WRAP
    )
  }

  @JvmStatic
  fun base64Encode(data: ByteArray): String {
    return Base64.encodeToString(
      data,
      Base64.NO_WRAP
    )
  }

  @JvmStatic
  fun bytesToString(data: ByteArray): String {
    return String(
      data,
      StandardCharsets.UTF_8
    )
  }

  @JvmStatic
  fun bytesToChars(data: ByteArray): CharArray {
    val result = CharArray(data.size)
    for (i in data.indices) {
      result[i] = data[i].toInt().toChar()
    }
    return result
  }

  @JvmStatic
  fun formatDateTime(obj: Any): String {
    return dateTimeFormat!!.format(obj)
  }

  @JvmStatic
  fun formatDateTimeWithoutSeconds(obj: Any): String {
    return dateTimeFormatWithoutSeconds!!.format(obj)
  }

  @JvmStatic
  fun formatDate(obj: Any): String {
    return dateFormat!!.format(obj)
  }

  @JvmStatic
  fun formatDate(
    date: Any?,
    targetPattern: String
  ): String? {
    return if (date is Date || date is Long) {
      val dateFormat = SimpleDateFormat(
        targetPattern,
        Locale.forLanguageTag("pl")
      )
      dateFormat.format(date)
    } else {
      "-"
    }
  }

  @JvmStatic
  fun parseDate(
    date: String?,
    pattern: String
  ): Date? {
    if (date == null) return null
    return try {
      SimpleDateFormat(pattern, Locale.forLanguageTag("pl")).parse(date)
    } catch (e: ParseException) {
      e.printStackTrace()
      null
    }
  }

  @JvmStatic
  fun getColor(
    activity: Activity,
    id: Int
  ): Int {
    return ResourcesCompat.getColor(
      activity.resources,
      id,
      null
    )
  }

  @JvmStatic
  @Throws(Settings.SettingNotFoundException::class)
  fun isAirplaneMode(activity: Activity): Boolean {
    return Settings.Global.getInt(
      activity.contentResolver,
      Settings.Global.AIRPLANE_MODE_ON
    ) != 0
  }

  @JvmStatic
  fun clearCookies(callback: ValueCallback<Boolean>?) {
    val cookieManager = CookieManager.getInstance()
    cookieManager.removeAllCookies(callback)
  }

  @JvmStatic
  fun clearWebViewData(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
      val webViewDatabase = WebViewDatabase.getInstance(context)
      webViewDatabase.clearHttpAuthUsernamePassword()
//      webViewDatabase.clearFormData()
//      webViewDatabase.clearUsernamePassword()
    }
  }

  @JvmStatic
  fun killApp() {
    android.os.Process.killProcess(android.os.Process.myPid())
    exitProcess(1)
  }

  @JvmStatic
  fun killApp(activity: Activity) {
    activity.finishAndRemoveTask()
  }

  @JvmStatic
  fun generateSalt(): ByteArray {
    val saltRND = ByteArray(8)
    val sr = SecureRandom()
    sr.nextBytes(saltRND)
    return saltRND
  }

  @JvmStatic
  fun clearSharedPreferences(context: Context) {
    val settings = context.getSharedPreferences(
      com.zibi.app.ex.broker.view.Constants.SHARED_PREFERENCES_NAME,
      0
    )
    val editor = settings.edit()
    editor.clear()
    editor.apply()

    val settings2 = context.getSharedPreferences(
      com.zibi.app.ex.broker.view.Constants.SHARED_PREF_BLUE_BORN_WARN,
      0
    )
    val editor2 = settings2.edit()
    editor2.clear()
    editor2.apply()
  }

  @JvmStatic
  @Throws(NoSuchAlgorithmException::class)
  fun getHash(data: ByteArray): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(data)
    return md.digest()
  }

  @JvmStatic
  fun byteToHex(byteArray: ByteArray): String {
    val stringBuilder = StringBuilder(byteArray.size * 2)
    for (i in byteArray.indices) {
      var byteAsString = Integer.toHexString(byteArray[i].toInt())
      val length = byteAsString.length
      if (length == 1) byteAsString = "0$byteAsString"
      if (length > 2) byteAsString = byteAsString.substring(
        length - 2,
        length
      )
      stringBuilder.append(byteAsString.uppercase())
    }
    return stringBuilder.toString()
  }

  @JvmStatic
  fun saveStringInSharedPreferences(
    activity: Context,
    key: String,
    value: String
  ) {
    val settings = activity.getSharedPreferences(
      com.zibi.app.ex.broker.view.Constants.SHARED_PREFERENCES_NAME,
      Context.MODE_PRIVATE
    )
    val editor = settings.edit()
    editor.putString(
      key,
      value
    )
    editor.apply()
  }

  @JvmStatic
  fun getStringFromSharedPreferences(
    activity: Context,
    key: String
  ): String? {
    val sharedPreferences = activity.getSharedPreferences(
      com.zibi.app.ex.broker.view.Constants.SHARED_PREFERENCES_NAME,
      Context.MODE_PRIVATE
    )
    return sharedPreferences.getString(
      key,
      com.zibi.app.ex.broker.view.Constants.SHARED_PREFERENCES_STRING_DEFAULT_VALUE
    )
  }

  @JvmStatic
  fun hexStringToByteArray(string: String): ByteArray {
    val byteArray = ByteArray(string.length / 2)
    for (i in byteArray.indices) {
      val index = i * 2
      val decimalNumber = Integer.parseInt(
        string.substring(
          index,
          index + 2
        ),
        16
      )
      byteArray[i] = decimalNumber.toByte()
    }
    return byteArray
  }

  @JvmStatic
  fun getBooleanState(
    context: Context,
    variable: String,
    defValue: Boolean?
  ): Boolean {
    val prefs = context.getSharedPreferences(
      "MyPrefsFile",
      Context.MODE_PRIVATE
    )
    return prefs.getBoolean(
      variable,
      defValue!!
    )
  }

  @JvmStatic
  fun setBooleanState(
    context: Context,
    variable: String,
    value: Boolean
  ) {
    val editor = context.getSharedPreferences(
      "MyPrefsFile",
      Context.MODE_PRIVATE
    ).edit()
    editor.putBoolean(
      variable,
      value
    )
    editor.apply()
  }

  @JvmStatic
  fun getCurrentFragmentTag(activity: Activity): String {
    val currentFragment = getCurrentFragment(activity)
    return when (val currentFragmentTag = currentFragment?.tag) {
      null -> ""
      else -> currentFragmentTag
    }
  }

  @JvmStatic
  fun getCurrentFragment(activity: Activity): Fragment? {
    return (activity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.AppContent)
  }

  @Suppress("DEPRECATION")
  @JvmStatic
  fun getDisplayHeightPixels(activity: Activity): Int {
    val displayMetrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val display = activity.display
      display?.getRealMetrics(displayMetrics)
    } else {
      @Suppress("DEPRECATION")
      val display = activity.windowManager.defaultDisplay
      @Suppress("DEPRECATION")
      display.getMetrics(displayMetrics)
    }
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
  }

  @JvmStatic
  fun isInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
      networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
      networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
      networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
      networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
      networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) -> true
      else -> false
    }
  }

  @JvmStatic
  fun dpToPx(dp: Int): Int {
    return round(dp * Resources.getSystem().displayMetrics.density).toInt()
  }

  @JvmStatic
  fun dpToPx(dp: Float): Float {
    return round(dp * Resources.getSystem().displayMetrics.density)
  }

  @JvmStatic
  fun containsForbiddenCharacters(
    regex: String,
    text: String
  ): Boolean {
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(text)
    return !matcher.matches()
  }

  @JvmStatic
  fun TextView?.blur(on: Boolean) {
    this?.setLayerType(
      View.LAYER_TYPE_SOFTWARE,
      null
    )
    val filter = BlurMaskFilter(
      15.toFloat(),
      BlurMaskFilter.Blur.NORMAL
    )
    if (on) {
      this?.paint?.maskFilter = filter
    } else {
      this?.paint?.maskFilter = null
    }
  }

  @JvmStatic
  fun shortenError(error: String?): String {
    return when {
      error == null -> "error is empty"
      error.length > 200 -> error.substring(
        0,
        199
      )
      else -> error
    }
  }

  @Suppress("DEPRECATION")
  @JvmStatic
  fun getWindowHeightWithoutStatusBar(activity: Activity): Int {
    val size = Point()
    activity.windowManager.defaultDisplay.getRealSize(size)
    var statusBarHeight = 0
    val resourceId = activity.resources.getIdentifier(
      "status_bar_height",
      "dimen",
      "android"
    )
    if (resourceId > 0) {
      statusBarHeight = activity.resources.getDimensionPixelSize(resourceId)
    }
    return size.y - statusBarHeight
  }

  fun ScrollView.scrollToBottom() {
    val lastChild = getChildAt(childCount - 1)
    val bottom = lastChild.bottom + paddingBottom
    val delta = bottom - (scrollY + height)
    smoothScrollBy(
      0,
      delta
    )
  }

  fun String?.getReadableValue(): String {
    return this.getReadableValue(false)
  }

  fun String?.getReadableValue(processEmptyString: Boolean): String {
    return when {
      this == null -> "-"
      this.lowercase(Locale.getDefault()) == "null" -> "-"
      this.isEmpty() && processEmptyString -> "-"
      else -> this
    }
  }

  fun retrieveBirthYearFromPesel(pesel: String?): Short {
    var year: Short

    year = (10 * Character.getNumericValue(pesel!![0])).toShort()
    year = (year + Character.getNumericValue(pesel[1])).toShort()

    var month: Byte = (10 * Character.getNumericValue(pesel[2])).toByte()
    month = (month + Character.getNumericValue(pesel[3])).toByte()

    when (month) {
      in 1..12 -> {
        year = (1900 + year).toShort()
      }
      in 21..32 -> {
        year = (2000 + year).toShort()
      }
      in 41..52 -> {
        year = (2100 + year).toShort()
      }
      in 61..72 -> {
        year = (2200 + year).toShort()
      }
    }

    return year
  }

  fun retrieveBirthMonthFromPesel(pesel: String?): Byte {
    var month: Byte

    month = (10 * Character.getNumericValue(pesel!![2])).toByte()
    month = (month + Character.getNumericValue(pesel[3])).toByte()
    when (month) {
      in 1..12 -> return month
      in 21..32 -> month = (month - 20).toByte()
      in 41..52 -> month = (month - 40).toByte()
      in 61..72 -> month = (month - 60).toByte()
    }
    return month
  }

  fun retrieveBirthDayFromPesel(pesel: String?): Byte {

    var day: Byte = (10 * Character.getNumericValue(pesel!![4])).toByte()
    day = (day + Character.getNumericValue(pesel[5])).toByte()

    return day
  }

  fun getRomanNumber(number: Int): String {
    return join(
      "",
      nCopies(
        number,
        "I"
      )
    ).replace(
      "IIIII",
      "V"
    ).replace(
      "IIII",
      "IV"
    ).replace(
      "VV",
      "X"
    ).replace(
      "VIV",
      "IX"
    ).replace(
      "XXXXX",
      "L"
    ).replace(
      "XXXX",
      "XL"
    ).replace(
      "LL",
      "C"
    ).replace(
      "LXL",
      "XC"
    ).replace(
      "CCCCC",
      "D"
    ).replace(
      "CCCC",
      "CD"
    ).replace(
      "DD",
      "M"
    ).replace(
      "DCD",
      "CM"
    )
  }

  @SuppressLint("QueryPermissionsNeeded")
  fun isApplicationInstalledOnDevice(
    context: Context,
    packageName: String
  ): Boolean {
    val packageManager = context.packageManager
    val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0L))
    } else {
      packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }
    for (packageInfo in packages) {
      if (packageInfo.packageName.equals(packageName)) {
        return true
      }
    }
    return false
  }

}