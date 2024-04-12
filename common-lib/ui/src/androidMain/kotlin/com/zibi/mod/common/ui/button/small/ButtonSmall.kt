package com.zibi.mod.common.ui.button.small

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.zibi.mod.common.ui.theme.AppTheme.colors
import com.zibi.mod.common.ui.theme.AppTheme.dimensions
import com.zibi.mod.common.ui.theme.AppTheme.typography
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource

@Composable
fun ButtonSmallWithText(
    text: String,
    textColor: Color = colors.primary,
    backgroundColor: Color = colors.secondary,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    Button(
        modifier = Modifier.height(dimensions.smallButtonSize),
        shape = RoundedCornerShape(dimensions.xLargePadding),
        interactionSource = NoRippleInteractionSource(),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        enabled = enabled,
        onClick = {
            focusManager.clearFocus(force = true)
            onClick()
        },
        contentPadding = PaddingValues(
            vertical = dimensions.xSmallPadding,
            horizontal = dimensions.regularPadding
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = dimensions.zero,
            pressedElevation = dimensions.zero,
            disabledElevation = dimensions.zero
        ),
    ) {
        Text(
            textAlign = TextAlign.Center,
            color = textColor,
            text = text,
            style = typography.body2Regular,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ButtonSmallWithIcon(
    iconColor: Color = colors.primary,
    backgroundColor: Color = colors.secondary,
    iconResId: Int,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.size(dimensions.smallButtonSize),
        shape = RoundedCornerShape(dimensions.xLargePadding),
        interactionSource = NoRippleInteractionSource(),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        onClick = onClick,
        contentPadding = PaddingValues(dimensions.xSmallPadding),
        elevation = ButtonDefaults.elevation(
            defaultElevation = dimensions.zero,
            pressedElevation = dimensions.zero,
            disabledElevation = dimensions.zero
        ),
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            tint = iconColor,
            modifier = Modifier.size(dimensions.smallButtonIconSize),
            contentDescription = null
        )
    }
}

@Composable
fun ButtonSmallWithIconAndText(
    iconResId: Int,
    iconColor: Color = colors.primary,
    backgroundColor: Color = colors.secondary,
    text: String,
    textColor: Color = colors.primary,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ButtonSmallWithIcon(
            iconResId = iconResId,
            iconColor = iconColor,
            backgroundColor = backgroundColor,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(dimensions.mediumPadding))
        Text(
            textAlign = TextAlign.Center,
            color = textColor,
            text = text,
            style = typography.body2Regular,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
        )
    }
}

