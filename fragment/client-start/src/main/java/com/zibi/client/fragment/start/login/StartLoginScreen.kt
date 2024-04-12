package com.zibi.client.fragment.start.login

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zibi.mod.common.ui.button.large.ButtonLarge
import com.zibi.mod.common.ui.button.large.ButtonLargeStyle
import com.zibi.mod.common.ui.dialog.small.DecisionDialog
import com.zibi.mod.common.ui.dialog.small.SmallDialogType
import com.zibi.mod.common.ui.inputs.InputField
import com.zibi.mod.common.ui.inputs.InputType
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.topMenu.TopMenu
import com.zibi.mod.common.ui.topMenu.model.TopMenuTitleSpanData
import com.zibi.client.fragment.start.login.model.StartLoginData
import com.zibi.client.fragment.start.utils.providePreviewStartLoginData

@Composable
fun StartLoginScreen(viewModel: StartLoginViewModel) {
    when (val state = viewModel.rememberState().value) {
        is StartLoginState.LoginScreen,
        is StartLoginState.DialogBadLogin ->
            StartMainLogin(viewModel.uiData(), state is StartLoginState.DialogBadLogin)
        else -> {}
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StartMainLogin(data: StartLoginData.Screen, showDialog: Boolean) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopMenu(
                title = data.topMenuTitle,
                titleSpanData = TopMenuTitleSpanData(
                    color = AppTheme.colors.topMenuTitlePrefix,
                    range = 0..0,
                ),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(AppTheme.colors.background)
                .padding(horizontal = AppTheme.dimensions.xmLargePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var userName by remember { mutableStateOf("") }
            var passwordField by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = AppTheme.dimensions.regularPadding),
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
                InputField(
                    label = data.labelUser,
                    hint = data.hintUser,
                    content = userName,
                    onValueChanged = { value ->
                        userName = value
                    }
                )
                Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
                InputField(
                    label = data.labelPassword,
                    hint = data.hintPassword,
                    content = passwordField,
                    type = InputType.Password(),
                    onValueChanged = { value ->
                        passwordField = value
                    }
                )
            }
            ButtonLarge(
                text = data.bottomButtonText,
                style = ButtonLargeStyle.PRIMARY,
                onClick = { data.onBottomNavigationButton(userName,passwordField) },
                modifier = Modifier.padding(
                    vertical = AppTheme.dimensions.buttonNextVerticalPadding
                )
            )
        }
    }
    if (showDialog) {
        DecisionDialog(
            dialog = SmallDialogType.Default(
                title = data.dialogTitle,
            content = data.dialogContent,
            positiveButtonText = data.dialogButtonLabel,
            onPositiveButtonClick = { data.onDialogButtonClicked() }
            ),
            onDismissRequest = {},
        )
    }

}

@Preview
@Composable
fun StartLoginFragmentPreview() {
    StartMainLogin(data = providePreviewStartLoginData(), showDialog = false)
}