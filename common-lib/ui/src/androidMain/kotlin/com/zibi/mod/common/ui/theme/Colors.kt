package com.zibi.mod.common.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val lightPrimary = Color(0xFF014A93)
private val lightSecondary = Color(0xFFDBEAF9)
private val lightBackground = Color(0xFFF4F6FB)
private val lightBlack = Color(0xFF1D1D1D)
private val lightBlackWithOpacity = Color(0x881D1D1D)
private val lightGrey = Color(0xFF4A4A4A)
private val lightGrey2 = Color(0x29000000)
private val lightWhite = Color(0xFFFFFFFF)
private val lightValidStatus1 = Color(0xFF214B2B)
private val lightValidStatus2 = Color(0xFFCFEEDC)
private val lightWarningStatus1 = Color(0xFF504924)
private val lightWarningStatus2 = Color(0xFFFFF9D8)
private val lightErrorStatus1 = Color(0xFF900E1D)
private val lightErrorStatus2 = Color(0xFFFFDAD6)
private val lightGreenText = Color(0xFF2d4314)
private val lightGreen900 = Color(0xFF598527)
private val lightGreen = Color(0xFF26FF00)
private val lightYellow900 = Color(0xFFEBA828)
private val lightBlue900 = Color(0xFF007EF2)
private val lightErrorStatusWeb = Color(0xFFd5233f)

// TODO: brak kolorów
private val lightRedText = Color(0xFFbb0d29)
private val lightRed900 = Color(0xFFd5233f)
private val lightInputFieldBorder = Color(0xFFdddddd)
private val lightInputFieldText = Color(0xFF929292)
private val lightInputFieldLabel = Color(0xFF4a4a4a)
private val lightProfileThick = Color(0xFF016cd7)
private val lightControllerSwitchBackground = Color(0xFFe9ecf0)
private val lightProgressBackground = Color(0xFFEAEDF1)
private val lightProgressIndicator = Color(0xFF0052a5)
private val lightTopMenuTitlePrefix = Color(0xFFE54560)

private val lightValidStatusBackground = Color(0xFFdee5dd)
private val lightValidStatusBorder = lightGreen900
private val lightWarningStatusBackground = Color(0xFFfae9c9)
private val lightWarningStatusBorder = lightYellow900
private val lightErrorStatusBackground = Color(0xFFfbe9ec)
private val lightErrorStatusBorder = lightRed900
private val lightInfoStatusBackground = Color(0xFFbcebff)
private val lightInfoStatusBorder = lightBlue900
private val lightInfoStatusWeb = Color(0xFF007ef2)

private val lightDocumentCardZus = Color(0xFF009a3c)
private val lightDocumentCardDeputy = Color(0xFF4e3328)
private val lightDocumentCardSolicitor = Color(0xFF124734)
private val lightDocumentCardNurse = Color(0xFFd43e47)
private val lightDocumentCardDeputySecondary = Color(0xFFF0F0F1)
private val lightDocumentCardSolicitorSecondary = Color(0xFFE6EBEA)
private val lightDocumentCardNurseSecondary = Color(0xFFE4EFFE)
private val lightDocumentCardId = Color(0xFFee7a7c)
private val lightDocumentCardDrivingLicence = Color(0xFF7689be)
private val lightDocumentCardDrivingLicenceBlue = Color(0xFF007AFF)
private val lightDocumentCardCovid = Color(0xFF589481)
private val lightDocumentCardVehicle = Color(0xFF1b2f4d)
private val lightDocumentCardFamily = Color(0xFFdb97e6)
private val lightDocumentCardCityKrakow = Color(0xFFf18a31)
private val lightDocumentCardRefugee = Color(0xFF005bbb)
private val lightDocumentCardSchool = Color(0xFFe85266)
private val lightDocumentCardStudent = Color(0xFF44d1e2)
private val lightDocumentCardPkp = Color(0xFF012152)
private val lightModalBar = Color(0x332F353F)
private val lightSwitchComponentUnchecked = Color(0x52787880)
private val lightOnboardingVersion = Color(0xFF92a5c7)

private val darkPrimary = Color(0xFF014A93)
private val darkSecondary = Color(0xFFDBEAF9)
private val darkBackground = Color(0xFFF4F6FB)
private val darkBlack = Color(0xFF1D1D1D)
private val darkBlackWithOpacity = Color(0x881D1D1D)
private val darkGrey = Color(0xFF4A4A4A)
private val darkGrey2 = Color(0x29000000)
private val darkWhite = Color(0xFFFFFFFF)
private val darkValidStatus1 = Color(0xFF214B2B)
private val darkValidStatus2 = Color(0xFFCFEEDC)
private val darkWarningStatus1 = Color(0xFF504924)
private val darkWarningStatus2 = Color(0xFFFFF9D8)
private val darkErrorStatus1 = Color(0xFF900E1D)
private val darkErrorStatus2 = Color(0xFFFFDAD6)
private val darkGreenText = Color(0xFF2d4314)
private val darkGreen900 = Color(0xFF598527)
private val darkYellow900 = Color(0xFFEBA828)
private val darkBlue900 = Color(0xFF007EF2)
private val darkStatusErrorWeb = Color(0xFFd5233f)

// TODO: brak kolorów
private val darkRedText = Color(0xFFbb0d29)
private val darkRed900 = Color(0xFFd5233f)
private val darkInputFieldBorder = Color(0xFFdddddd)
private val darkInputFieldText = Color(0xFF929292)
private val darkInputFieldLabel = Color(0xFF4a4a4a)
private val darkProfileThick = Color(0xFF016cd7)
private val darkControllerSwitchBackground = Color(0xFFe9ecf0)
private val darkProgressBackground = Color(0xFFEAEDF1)
private val darkProgressIndicator = Color(0xFF0052a5)
private val darkTopMenuTitlePrefix = Color(0xFFE54560)


private val darkValidStatusBackground = Color(0xFFdee5dd)
private val darkValidStatusBorder = darkGreen900
private val darkWarningStatusBackground = Color(0xFFfae9c9)
private val darkWarningStatusBorder = darkYellow900
private val darkErrorStatusBackground = Color(0xFFfbe9ec)
private val darkErrorStatusBorder = darkRed900
private val darkInfoStatusBackground = Color(0xFFbcebff)
private val darkInfoStatusBorder = darkBlue900
private val darkInfoStatusWeb = Color(0xFF007ef2)
private val darkestInfoStatus = Color(0xFF01498b)

private val borderShadowColor = Color(0x0052a50d)

private val darkDocumentCardZus = Color(0xFF009a3c)
private val darkDocumentCardDeputy = Color(0xFF797677)
private val darkDocumentCardSolicitor = Color(0xFF124734)
private val darkDocumentCardNurse = Color(0xFFd43e47)
private val darkDocumentCardDeputySecondary = Color(0xFFF0F0F1)
private val darkDocumentCardSolicitorSecondary = Color(0xFFE6EBEA)
private val darkDocumentCardNurseSecondary = Color(0xFFE4EFFE)
private val darkDocumentCardId = Color(0xFFee7a7c)
private val darkDocumentCardDrivingLicence = Color(0xFF7689be)
private val darkDocumentCardDrivingLicenceBlue = Color(0xFF007AFF)
private val darkDocumentCardCovid = Color(0xFF589481)
private val darkDocumentCardVehicle = Color(0xFF1b2f4d)
private val darkDocumentCardFamily = Color(0xFFdb97e6)
private val darkDocumentCardCityKrakow = Color(0xFFf18a31)
private val darkDocumentCardRefugee = Color(0xFF005bbb)
private val darkDocumentCardSchool = Color(0xFFe85266)
private val darkDocumentCardStudent = Color(0xFF44d1e2)
private val darkDocumentCardPkp = Color(0xFF012152)
private val darkModalBar = Color(0x332F353F)
private val darkSwitchComponentUnchecked = Color(0x52787880)
private val darkOnboardingVersion = Color(0xFF92a5c7)

enum class ThemeColors(
  val primary: Color,
  val secondary: Color,
  val background: Color,
  val semiTransparentBackground: Color,
  val black: Color,
  val grey: Color,
  val white: Color,
  val statusGreen1: Color,
  val statusGreen2: Color,
  val statusYellow1: Color,
  val statusYellow2: Color,
  val statusRed1: Color,
  val statusRed2: Color,
  val statusBlue1: Color,
  val greenText: Color,
  val green900: Color,
  val lightGreen: Color,
  val yellow900: Color,
  val blue900: Color,
  val statusRedWeb: Color,
  val redText: Color,
  val red900: Color,
  val inputFieldBorder: Color,
  val inputFieldText: Color,
  val inputFieldLabel: Color,
  val profileThick: Color,
  val controllerSwitchBackground: Color,
  val progressBackground: Color,
  val progressIndicator: Color,
  val topMenuTitlePrefix: Color,
  val validStatusBackground: Color,
  val validStatusBorder: Color,
  val warningStatusBackground: Color,
  val warningStatusYellowBorder: Color,
  val errorStatusBackground: Color,
  val errorStatusBorder: Color,
  val infoStatusBackground: Color,
  val infoStatusBorder: Color,
  val infoStatusWeb: Color,
  val borderShadow: Color,
  val documentCardZus: Color,
  val documentCardDeputy: Color,
  val documentCardSolicitor: Color,
  val documentCardNurse: Color,
  val documentCardDeputySecondary: Color,
  val documentCardSolicitorSecondary: Color,
  val documentCardNurseSecondary: Color,
  val documentCardId: Color,
  val documentCardDrivingLicence: Color,
  val documentCardDrivingLicenceBlue: Color,
  val documentCardCovid: Color,
  val documentCardVehicle: Color,
  val documentCardFamily: Color,
  val documentCardCityKrakow: Color,
  val documentCardRefugee: Color,
  val documentCardSchool: Color,
  val documentCardStudent: Color,
  val documentCardPkp: Color,
  val modalBar: Color,
  val switchComponentUnchecked: Color,
  val tabBarBackground: Color,
  val onboardingVersion: Color,
  val isLight: Boolean,
) {
  LIGHT(
    primary = lightPrimary,
    secondary = lightSecondary,
    background = lightBackground,
    semiTransparentBackground = lightBlackWithOpacity,
    black = lightBlack,
    grey = lightGrey,
    white = lightWhite,
    statusGreen1 = lightValidStatus1,
    statusGreen2 = lightValidStatus2,
    statusYellow1 = lightWarningStatus1,
    statusYellow2 = lightWarningStatus2,
    statusRed1 = lightErrorStatus1,
    statusRed2 = lightErrorStatus2,
    statusBlue1 = darkestInfoStatus,
    greenText = lightGreenText,
    green900 = lightGreen900,
    lightGreen= lightGreen,
    blue900 = lightBlue900,
    yellow900 = lightYellow900,
    statusRedWeb = lightErrorStatusWeb,
    redText = lightRedText,
    red900 = lightRed900,
    inputFieldBorder = lightInputFieldBorder,
    inputFieldText = lightInputFieldText,
    inputFieldLabel = lightInputFieldLabel,
    profileThick = lightProfileThick,
    controllerSwitchBackground = lightControllerSwitchBackground,
    progressBackground = lightProgressBackground,
    progressIndicator = lightProgressIndicator,
    topMenuTitlePrefix = lightTopMenuTitlePrefix,
    validStatusBackground = lightValidStatusBackground,
    validStatusBorder = lightValidStatusBorder,
    warningStatusBackground = lightWarningStatusBackground,
    warningStatusYellowBorder = lightWarningStatusBorder,
    errorStatusBackground = lightErrorStatusBackground,
    errorStatusBorder = lightErrorStatusBorder,
    infoStatusBackground = lightInfoStatusBackground,
    infoStatusBorder = lightInfoStatusBorder,
    infoStatusWeb = lightInfoStatusWeb,
    borderShadow = borderShadowColor,
    documentCardZus = lightDocumentCardZus,
    documentCardDeputy = lightDocumentCardDeputy,
    documentCardSolicitor = lightDocumentCardSolicitor,
    documentCardNurse = lightDocumentCardNurse,
    documentCardDeputySecondary = lightDocumentCardDeputySecondary,
    documentCardSolicitorSecondary = lightDocumentCardSolicitorSecondary,
    documentCardNurseSecondary = lightDocumentCardNurseSecondary,
    documentCardId = lightDocumentCardId,
    documentCardDrivingLicence = lightDocumentCardDrivingLicence,
    documentCardDrivingLicenceBlue = lightDocumentCardDrivingLicenceBlue,
    documentCardCovid = lightDocumentCardCovid,
    documentCardVehicle = lightDocumentCardVehicle,
    documentCardFamily = lightDocumentCardFamily,
    documentCardCityKrakow = lightDocumentCardCityKrakow,
    documentCardRefugee = lightDocumentCardRefugee,
    documentCardSchool = lightDocumentCardSchool,
    documentCardStudent = lightDocumentCardStudent,
    documentCardPkp = lightDocumentCardPkp,
    tabBarBackground = lightGrey2,
    modalBar = lightModalBar,
    switchComponentUnchecked = lightSwitchComponentUnchecked,
    onboardingVersion = lightOnboardingVersion,
    isLight = true,
  ),
  DARK(
    primary = darkPrimary,
    secondary = darkSecondary,
    background = darkBackground,
    semiTransparentBackground = darkBlackWithOpacity,
    black = darkBlack,
    grey = darkGrey,
    white = darkWhite,
    statusGreen1 = darkValidStatus1,
    statusGreen2 = darkValidStatus2,
    statusYellow1 = darkWarningStatus1,
    statusYellow2 = darkWarningStatus2,
    statusRed1 = darkErrorStatus1,
    statusRed2 = darkErrorStatus2,
    statusBlue1 = darkestInfoStatus,
    greenText = darkGreenText,
    green900 = darkGreen900,
    lightGreen= lightGreen,
    yellow900 = darkYellow900,
    blue900 = darkBlue900,
    statusRedWeb = darkStatusErrorWeb,
    redText = darkRedText,
    red900 = darkRed900,
    inputFieldBorder = darkInputFieldBorder,
    inputFieldText = darkInputFieldText,
    inputFieldLabel = darkInputFieldLabel,
    profileThick = darkProfileThick,
    controllerSwitchBackground = darkControllerSwitchBackground,
    progressBackground = darkProgressBackground,
    progressIndicator = darkProgressIndicator,
    topMenuTitlePrefix = darkTopMenuTitlePrefix,
    validStatusBackground = darkValidStatusBackground,
    validStatusBorder = darkValidStatusBorder,
    warningStatusBackground = darkWarningStatusBackground,
    warningStatusYellowBorder = darkWarningStatusBorder,
    errorStatusBackground = darkErrorStatusBackground,
    errorStatusBorder = darkErrorStatusBorder,
    infoStatusBackground = darkInfoStatusBackground,
    infoStatusBorder = darkInfoStatusBorder,
    infoStatusWeb = darkInfoStatusWeb,
    borderShadow = borderShadowColor,
    documentCardZus = darkDocumentCardZus,
    documentCardDeputy = darkDocumentCardDeputy,
    documentCardSolicitor = darkDocumentCardSolicitor,
    documentCardNurse = darkDocumentCardNurse,
    documentCardDeputySecondary = darkDocumentCardDeputySecondary,
    documentCardSolicitorSecondary = darkDocumentCardSolicitorSecondary,
    documentCardNurseSecondary = darkDocumentCardNurseSecondary,
    documentCardId = darkDocumentCardId,
    documentCardDrivingLicence = darkDocumentCardDrivingLicence,
    documentCardDrivingLicenceBlue = darkDocumentCardDrivingLicenceBlue,
    documentCardCovid = darkDocumentCardCovid,
    documentCardVehicle = darkDocumentCardVehicle,
    documentCardFamily = darkDocumentCardFamily,
    documentCardCityKrakow = darkDocumentCardCityKrakow,
    documentCardRefugee = darkDocumentCardRefugee,
    documentCardSchool = darkDocumentCardSchool,
    documentCardStudent = darkDocumentCardStudent,
    documentCardPkp = darkDocumentCardPkp,
    tabBarBackground = darkGrey2,
    modalBar = darkModalBar,
    switchComponentUnchecked = darkSwitchComponentUnchecked,
    onboardingVersion = darkOnboardingVersion,
    isLight = false,
  );

  fun getColors() = listOf(
    primary to "primary",
    secondary to "secondary",
    background to "background",
    black to "black",
    grey to "grey",
    white to "white",
    statusGreen1 to "statusGreen1",
    statusGreen2 to "statusGreen2",
    statusYellow1 to "statusYellow1",
    statusYellow2 to "statusYellow2",
    statusRed1 to "statusRed1",
    statusRed2 to "statusRed2",
    statusBlue1 to "statusBlue1",
    greenText to "greenText",
    green900 to "green900",
    yellow900 to "yellow900",
    blue900 to "blue900",
    statusRedWeb to "statusRedWeb",
    redText to "redText",
    red900 to "red900",
    inputFieldBorder to "inputFieldBorder",
    inputFieldText to "inputFieldText",
    inputFieldLabel to "inputFieldLabel",
    profileThick to "profileThick",
    controllerSwitchBackground to "controllerSwitchBackground",
    progressBackground to "progressBackground",
    progressIndicator to "progressIndicator",
    topMenuTitlePrefix to "topMenuTitlePrefix",
    validStatusBackground to "validStatusBackground",
    validStatusBorder to "validStatusBorder",
    warningStatusBackground to "warningStatusBackground",
    warningStatusYellowBorder to "warningStatusYellowBorder",
    errorStatusBackground to "errorStatusBackground",
    errorStatusBorder to "errorStatusBorder",
    infoStatusBackground to "infoStatusBackground",
    infoStatusBorder to "infoStatusBorder",
    infoStatusWeb to "infoStatusWeb",
    borderShadow to "borderShadow",
    documentCardZus to "documentCardZus",
    documentCardDeputy to "documentCardDeputy",
    documentCardSolicitor to "documentCardSolicitor",
    documentCardNurse to "documentCardNurse",
    documentCardDeputySecondary to "documentCardDeputySecondary",
    documentCardSolicitorSecondary to "documentCardSolicitorSecondary",
    documentCardNurseSecondary to "documentCardNurseSecondary",
    documentCardId to "documentCardId",
    documentCardDrivingLicence to "documentCardDrivingLicence",
    documentCardDrivingLicenceBlue to "documentCardDrivingLicenceBlue",
    documentCardCovid to "documentCardCovid",
    documentCardVehicle to "documentCardVehicle",
    documentCardFamily to "documentCardFamily",
    documentCardCityKrakow to "documentCardCityKrakow",
    documentCardRefugee to "documentCardRefugee",
    documentCardSchool to "documentCardSchool",
    documentCardStudent to "documentCardStudent",
    documentCardPkp to "documentCardPkp",
    modalBar to "modalBar",
    switchComponentUnchecked to "switchComponentUnchecked",
    tabBarBackground to "tabBarBackground",
    onboardingVersion to "onboardingVersion",
  ).map {
    Color(
      name = it.second,
      color = it.first,
    )
  }
}

data class Color(
  val name: String,
  val color: Color
)

internal val LocalColors = staticCompositionLocalOf { ThemeColors.LIGHT }

