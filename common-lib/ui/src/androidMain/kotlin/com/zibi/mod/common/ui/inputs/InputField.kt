package com.zibi.mod.common.ui.inputs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.zibi.mod.common.ui.R
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.inputs.visualtransformation.MaskType
import com.zibi.mod.common.ui.inputs.visualtransformation.MaskVisualTransformation
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.ValidationState

private const val DEFAULT_MULTILINE_LINES_COUNT = 100

sealed class InputType {
  object Default : InputType()

  object Removable : InputType()

  data class Password(
    val keyboardType: KeyboardType = KeyboardType.Password
  ) : InputType()

  data class Tooltip(
    var onClickInfoButtonResponse: (coordinates: IntOffset) -> Unit
  ) : InputType()

  data class Number(
    val keyboardType: KeyboardType = KeyboardType.Number
  ) : InputType()

  data class Masked(
    val maskType: MaskType,
    val isRemovable: Boolean = false,
  ) : InputType() {
    val keyboardType: KeyboardType = maskType.keyboardType
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InputField(
  type: InputType = InputType.Default,
  label: String,
  hint: String,
  enabled: Boolean = true,
  content: String = "",
  optionalHint: String? = null,
  optionalText: String? = null,
  linkText: String? = null,
  multiLine: Boolean = false,
  validationState: ValidationState = ValidationState.Default,
  imeAction: ImeAction = ImeAction.Done,
  onValueChanged: (String) -> Unit,
  /* Do NOT use this value as "content"!!! */
  onMaskedValueChanged: (String) -> Unit = {},
  onFocusChanged: (Boolean) -> Unit = {},
  onLinkClick: () -> Unit = {},
) {

  var isFocused by remember { mutableStateOf(false) }
  var inputFieldCoordinates by remember { mutableStateOf(IntOffset(0, 0)) }
  var isPasswordVisible by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()
  val bringIntoViewRequester = remember { BringIntoViewRequester() }
  var firstFocus by remember { mutableStateOf(false) }
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .bringIntoViewRequester(bringIntoViewRequester)
      .onGloballyPositioned { coordinates ->
        inputFieldCoordinates = IntOffset(
          x = coordinates.positionInWindow().x.toInt(),
          y = coordinates.positionInWindow().y.toInt()
        )
        if (firstFocus) {
          coroutineScope.launch {
            bringIntoViewRequester.bringIntoView()
            delay(200)
            firstFocus = false
          }
        }
      }
  ) {
    Row {
      Text(
        modifier = Modifier.weight(1f),
        text = label,
        style = AppTheme.typography.labelRegular,
        color = AppTheme.colors.inputFieldLabel,
      )
    }

    Spacer(modifier = Modifier.height(AppTheme.dimensions.smallPadding))
    Card(
      border = BorderStroke(
        width = AppTheme.dimensions.xxSmallPadding,
        color = when {
          validationState is ValidationState.Invalid -> {
            LaunchedEffect(Unit) {
              bringIntoViewRequester.bringIntoView()
            }
            AppTheme.colors.red900
          }
          isFocused -> AppTheme.colors.primary
          else -> AppTheme.colors.inputFieldBorder
        }
      ),
      backgroundColor = Color.White,
      elevation = AppTheme.dimensions.zero,
    ) {
      BasicTextField(
        keyboardOptions = KeyboardOptions(
          keyboardType = when (type) {
            is InputType.Number -> type.keyboardType
            is InputType.Password -> type.keyboardType
            is InputType.Masked -> type.keyboardType
            else -> KeyboardType.Text
          },
          imeAction = imeAction
        ),
        enabled = enabled,
        maxLines = if (multiLine) DEFAULT_MULTILINE_LINES_COUNT else 1,
        singleLine = !multiLine,
        cursorBrush = if (validationState is ValidationState.Invalid)
          SolidColor(AppTheme.colors.red900)
        else
          SolidColor(
            value = if (isFocused)
              AppTheme.colors.primary
            else
              AppTheme.colors.inputFieldText
          ),
        modifier = Modifier
          .onFocusChanged { focusState ->
            firstFocus = focusState.isFocused
            isFocused = focusState.isFocused
            onFocusChanged(isFocused)
          }
          .height(if (multiLine)
              AppTheme.dimensions.multilineInputFieldHeight
            else
              AppTheme.dimensions.inputFieldHeight

          ),
        value = content,
        onValueChange = {
          if (type is InputType.Masked)
            type.maskType.run {
              onMaskedValueChanged(getTextValue(it))
              if (filter(it)) onValueChanged(it)
            }
          else
            onValueChanged(it)

        },
        visualTransformation = when (type) {
          is InputType.Password -> if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
          is InputType.Masked -> MaskVisualTransformation(type.maskType)
          InputType.Removable,
          is InputType.Tooltip,
          InputType.Default,
          is InputType.Number -> VisualTransformation.None
        },
        textStyle = AppTheme.typography.body1Regular,
        decorationBox = { innerTextField ->
          Row(
            Modifier
              .background(Color.White)
              .padding(AppTheme.dimensions.regularPadding)
              .fillMaxWidth(),
            verticalAlignment = if (multiLine) Alignment.Top else Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier.weight(
                if (validationState is ValidationState.Invalid || type !is InputType.Default) 0.9f else 1f
              )
            ) {
              when {
                content.isEmpty() -> {
                  val hintMessage = if (optionalHint != null) {
                    StringBuilder().append(hint).append(
                      stringResource(id = R.string.common_ui_input_field_text_parenthesis_open)
                    ).append(optionalHint).append(
                      stringResource(id = R.string.common_ui_input_field_text_parenthesis_close)
                    )
                  } else {
                    hint
                  }.toString()
                  Text(
                    text = hintMessage,
                    overflow = TextOverflow.Ellipsis,
                    style = AppTheme.typography.body2Regular,
                    color = AppTheme.colors.inputFieldText
                  )
                }
              }
              innerTextField()
            }
            Column(
              modifier = Modifier.weight(
                if (validationState is ValidationState.Invalid || type !is InputType.Default) 0.1f else 0.01f
              )
            ) {
              when (type) {

                is InputType.Password -> {
                  IconButton(
                    enabled = enabled,
                    onClick = { isPasswordVisible = !isPasswordVisible },
                  ) {
                    CustomIcon(
                      iconResId = if (isPasswordVisible) R.drawable.common_ui_ic_hide_password
                      else R.drawable.common_ui_ic_show_password,
                      iconSize = IconSize.Small,
                      contentDescription = stringResource(
                        id = if (isPasswordVisible) R.string
                          .common_ui_input_field_text_password_icon_visible_content_description
                        else R.string.common_ui_input_field_text_password_icon_invisible_content_description
                      ),
                    )
                  }
                }

                is InputType.Removable -> {
                  if (content.isNotEmpty()) {
                    ActionIcon(
                      iconResId = R.drawable.common_ui_ic_fail,
                      iconSize = IconSize.Small,
                      contentDescription = "remove_text_button",
                      enabled = enabled,
                      onClick = { onValueChanged("") },
                    )
                  }
                }

                is InputType.Tooltip -> {
                  ActionIcon(
                    iconResId = R.drawable.common_ui_ic_tooltip,
                    iconSize = IconSize.Medium,
                    contentDescription = "open_tooltip_button",
                    enabled = enabled,
                    onClick = { type.onClickInfoButtonResponse(inputFieldCoordinates) },
                  )
                }

                is InputType.Default, is InputType.Number -> {
                  if (validationState is ValidationState.Invalid) {
                    CustomIcon(
                      modifier = Modifier
                        .padding(
                          start = AppTheme.dimensions.mediumPadding,
                        )
                        .absoluteOffset(
                          x = -AppTheme.dimensions.smallPadding,
                          y = AppTheme.dimensions.zero
                        ),
                      iconResId = R.drawable.common_ui_ic_erorr_mark,
                      iconSize = IconSize.Medium,
                      contentDescription = stringResource(
                        id = R.string.common_ui_input_field_icon_error_content_description
                      ),
                      iconColor = AppTheme.colors.red900
                    )
                  }
                }
                is InputType.Masked -> {
                  if (content.isNotEmpty() && type.isRemovable) {
                    ActionIcon(
                      iconResId = R.drawable.common_ui_ic_fail,
                      iconSize = IconSize.Small,
                      contentDescription = "remove_text_button",
                      enabled = enabled,
                      onClick = { onValueChanged("") },
                    )
                  }
                }
              }
            }
          }
        }
      )
    }

    when {
      validationState is ValidationState.Invalid -> {
        Text(
          text = validationState.message,
          style = AppTheme.typography.labelRegularLight,
          color = AppTheme.colors.red900
        )
      }
      optionalText != null -> {
        Text(
          text = optionalText,
          style = AppTheme.typography.labelRegularLight,
          color = AppTheme.colors.inputFieldLabel
        )
      }
    }

    linkText?.let { text ->
      ClickableText(
        modifier = Modifier.align(Alignment.End),
        text = AnnotatedString(text),
        style = AppTheme.typography.label2Regular.copy(
          color = AppTheme.colors.statusBlue1
        ),
      ) {
        onLinkClick()
      }
    }

  }
}

@Composable
fun ActionIcon(
  iconResId: Int,
  iconSize: IconSize,
  contentDescription: String,
  enabled: Boolean,
  onClick: () -> Unit,
) {
  IconButton(
    enabled = enabled,
    onClick = onClick,
  ) {
    CustomIcon(
      iconResId = iconResId,
      iconSize = iconSize,
      contentDescription = contentDescription,
    )
  }
}