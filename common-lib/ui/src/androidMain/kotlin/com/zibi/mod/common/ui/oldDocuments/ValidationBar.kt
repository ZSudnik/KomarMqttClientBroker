package com.zibi.mod.common.ui.oldDocuments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//Testowo dla sprawdzenia widoku
data class StateValidationComponent(
  val isDocumentWithExpiresDate: Boolean,
  val validationText: String,
  val validationTextColor: Int,
  val validationIconId: Int,
  val expireDateText: String,
  val lastUpdateData: String
) {
//    companion object {
//        fun initial() = StateValidationComponent(
//            isDocumentWithExpiresDate = true,
//            validationText = "Dokument ważny",
//            validationTextColor = R.color.mPrawoJazdy_blue_light,
//            validationIconId = R.drawable.valid,
//            expireDateText = "bezterminowo",
//            lastUpdateData = "24.11.2022"
//        )
//    }

}


@Composable
fun DocumentValidationBarComponent(state: State<StateValidationComponent>) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .background(
        color = Color(0x8FFFFFFF)
      )
      .padding(
        start = 16.dp,
        top = 14.dp,
        end = 15.dp,
        bottom = 12.dp
      )
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Image(
          painter = painterResource(id = state.value.validationIconId),
          contentDescription = "Validation icon",
          modifier = Modifier.size(31.dp),
        )

        Spacer(modifier = Modifier.width(17.dp))

        Column(
          modifier = Modifier
        ) {
          Text(
            text = state.value.validationText,
            fontSize = 12.sp,
            color = Color(state.value.validationTextColor),
            fontFamily = com.zibi.mod.common.ui.theme.Typography.body1Regular.fontFamily,
            fontWeight = FontWeight.Medium
          )

          if (state.value.isDocumentWithExpiresDate) {
            Text(
              text = state.value.expireDateText,
              fontSize = 11.sp,
              color = Color(0xFF767C85),
              fontFamily = com.zibi.mod.common.ui.theme.Typography.body1Regular.fontFamily,
              fontWeight = FontWeight.Normal
            )
          }
        }
      }

      Button(modifier = Modifier.height(32.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF007AFF)),
        onClick = {}) {
        Text(
          text = "Aktualizuj",
          fontSize = 12.sp,
          color = Color.White,
          fontFamily = com.zibi.mod.common.ui.theme.Typography.body1Regular.fontFamily,
          fontWeight = FontWeight.Medium
        )
      }
    }

    Spacer(modifier = Modifier.height(15.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      Text(
        text = "Ostatnia aktualizacja: ${state.value.lastUpdateData}",
        fontSize = 11.sp,
        color = Color(0xFF767C85),
        fontFamily = com.zibi.mod.common.ui.theme.Typography.body1Regular.fontFamily,
        fontWeight = FontWeight.Medium
      )
    }

  }
}