package com.zibi.mod.common.ui.steps

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.R

data class Step(
    val title: String,
    val supportText: String? = null,
    val icon: Int = R.drawable.common_ui_ic_steps_default,
    val iconDescription: String = "Icon on onboarding steps",
)

@Composable
fun Steps(
  steps: List<Step>,
) {
  Column(modifier = Modifier.fillMaxSize()) {
    steps.forEach { step ->
      StepRow(
        step = step,
      )
    }
  }
}

@Composable
fun StepRow(step: Step) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
  ) {
    Box(modifier = Modifier.size(AppTheme.dimensions.stepsIconSize)) {
      Icon(
        painter = painterResource(id = step.icon),
        contentDescription = step.iconDescription,
      )
    }

    Spacer(modifier = Modifier.width(AppTheme.dimensions.regularPadding))

    Column(modifier = Modifier.fillMaxWidth()) {
      Text(
        text = step.title,
        style = AppTheme.typography.bodyMedium,
        color = AppTheme.colors.black,
      )
      if (step.supportText != null) {
        Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
        Text(
          text = step.supportText,
          style = AppTheme.typography.body1Regular,
          color = AppTheme.colors.grey,
        )
      }
      Spacer(modifier = Modifier.height(AppTheme.dimensions.xLargePadding))
    }
  }
}

